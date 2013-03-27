/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.runtime.jmx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.management.Attribute;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import com.sysdeo.eclipse.tomcat.StringUtil;

/**
 * 
 * @author jfernandes
 */
public class UqbarMBeanServerInvocationHandler implements InvocationHandler {
	private final MBeanServerConnection connection;
	private final ObjectName objectName;

	public UqbarMBeanServerInvocationHandler(MBeanServerConnection connection, ObjectName objectName) {
		this.connection = connection;
		this.objectName = objectName;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final Class methodClass = method.getDeclaringClass();

		if (methodClass.equals(NotificationBroadcaster.class) || methodClass.equals(NotificationEmitter.class))
			return invokeBroadcasterMethod(proxy, method, args);

		final String methodName = method.getName();
		final Class[] paramTypes = method.getParameterTypes();
		final Class returnType = method.getReturnType();

		/* Inexplicably, InvocationHandler specifies that args is null
		   when the method takes no arguments rather than a
		   zero-length array.  */
		final int nargs = (args == null) ? 0 : args.length;

		if (methodName.startsWith("get") && methodName.length() > 3 && nargs == 0 && !returnType.equals(Void.TYPE)) {
			return connection.getAttribute(objectName, attributeFromMethodName(methodName));
		}

		if (methodName.startsWith("is") && methodName.length() > 2 && nargs == 0
				&& (returnType.equals(Boolean.TYPE) || returnType.equals(Boolean.class))) {
			return connection.getAttribute(objectName, methodName.substring(2));
		}

		if (methodName.startsWith("set") && methodName.length() > 3 && nargs == 1 && returnType.equals(Void.TYPE)) {
			Attribute attr = new Attribute(this.attributeFromMethodName(methodName), args[0]);
			connection.setAttribute(objectName, attr);
			return null;
		}

		final String[] signature = new String[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++)
			signature[i] = paramTypes[i].getName();
		try {
			return connection.invoke(objectName, methodName, args, signature);
		}
		catch (MBeanException e) {
			throw e.getTargetException();
		}
		/* The invoke may fail because it can't get to the MBean, with
		   one of the these exceptions declared by
		   MBeanServerConnection.invoke:
		   - RemoteException: can't talk to MBeanServer;
		   - InstanceNotFoundException: objectName is not registered;
		   - ReflectionException: objectName is registered but does not
		     have the method being invoked.
		   In all of these cases, the exception will be wrapped by the
		   proxy mechanism in an UndeclaredThrowableException unless
		   it happens to be declared in the "throws" clause of the
		   method being invoked on the proxy.
		*/
	}

	protected String attributeFromMethodName(String methodName) {
		String propertyName = methodName.substring(3);
		return Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
	}

	private Object invokeBroadcasterMethod(Object proxy, Method method, Object[] args) throws Exception {
		final String methodName = method.getName();
		final int nargs = (args == null) ? 0 : args.length;

		if (methodName.equals("addNotificationListener")) {
			/* The various throws of IllegalArgumentException here
			should not happen, since we know what the methods in
			NotificationBroadcaster and NotificationEmitter
			are.  */
			if (nargs != 3) {
				final String msg = "Bad arg count to addNotificationListener: " + nargs;
				throw new IllegalArgumentException(msg);
			}
			/* Other inconsistencies will produce ClassCastException
			below.  */

			NotificationListener listener = (NotificationListener) args[0];
			NotificationFilter filter = (NotificationFilter) args[1];
			Object handback = args[2];
			connection.addNotificationListener(objectName, listener, filter, handback);
			return null;

		}
		else if (methodName.equals("removeNotificationListener")) {

			/* NullPointerException if method with no args, but that
			shouldn't happen because removeNL does have args.  */
			NotificationListener listener = (NotificationListener) args[0];

			switch (nargs) {
			case 1:
				connection.removeNotificationListener(objectName, listener);
				return null;

			case 3:
				NotificationFilter filter = (NotificationFilter) args[1];
				Object handback = args[2];
				connection.removeNotificationListener(objectName, listener, filter, handback);
				return null;

			default:
				final String msg = "Bad arg count to removeNotificationListener: " + nargs;
				throw new IllegalArgumentException(msg);
			}

		}
		else if (methodName.equals("getNotificationInfo")) {

			if (args != null) {
				throw new IllegalArgumentException("getNotificationInfo has " + "args");
			}

			MBeanInfo info = connection.getMBeanInfo(objectName);
			return info.getNotifications();

		}
		else {
			throw new IllegalArgumentException("Bad method name: " + methodName);
		}
	}

	public static Object newProxyInstance(MBeanServerConnection connection, ObjectName objectName, Class interfaceClass, boolean notificationBroadcaster) {
		final InvocationHandler handler = new UqbarMBeanServerInvocationHandler(connection, objectName);
		final Class[] interfaces;
		if (notificationBroadcaster) {
			interfaces = new Class[] { interfaceClass, NotificationEmitter.class };
		}
		else
			interfaces = new Class[] { interfaceClass };
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(), interfaces, handler);
	}

}
