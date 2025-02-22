/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.rmi.ConnectException;
import java.rmi.ConnectIOException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.StubNotFoundException;
import java.rmi.UnknownHostException;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.util.ReflectionUtils;

@Deprecated
public abstract class RmiClientInterceptorUtils {
    private static final Log logger = LogFactory.getLog(RmiClientInterceptorUtils.class);

    @Nullable
    public static Object invokeRemoteMethod(MethodInvocation invocation, Object stub) throws InvocationTargetException {
        Method method = invocation.getMethod();
        try {
            if (method.getDeclaringClass().isInstance(stub)) {
                return method.invoke(stub, invocation.getArguments());
            }
            Method stubMethod = stub.getClass().getMethod(method.getName(), method.getParameterTypes());
            return stubMethod.invoke(stub, invocation.getArguments());
        }
        catch (InvocationTargetException ex) {
            throw ex;
        }
        catch (NoSuchMethodException ex) {
            throw new RemoteProxyFailureException("No matching RMI stub method found for: " + method, ex);
        }
        catch (Throwable ex) {
            throw new RemoteProxyFailureException("Invocation of RMI stub method failed: " + method, ex);
        }
    }

    public static Exception convertRmiAccessException(Method method, Throwable ex, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)message, ex);
        }
        if (ReflectionUtils.declaresException(method, RemoteException.class)) {
            return new RemoteException(message, ex);
        }
        return new RemoteAccessException(message, ex);
    }

    public static Exception convertRmiAccessException(Method method, RemoteException ex, String serviceName) {
        return RmiClientInterceptorUtils.convertRmiAccessException(method, ex, RmiClientInterceptorUtils.isConnectFailure(ex), serviceName);
    }

    public static Exception convertRmiAccessException(Method method, RemoteException ex, boolean isConnectFailure, String serviceName) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Remote service [" + serviceName + "] threw exception"), (Throwable)ex);
        }
        if (ReflectionUtils.declaresException(method, ex.getClass())) {
            return ex;
        }
        if (isConnectFailure) {
            return new RemoteConnectFailureException("Could not connect to remote service [" + serviceName + "]", ex);
        }
        return new RemoteAccessException("Could not access remote service [" + serviceName + "]", ex);
    }

    public static boolean isConnectFailure(RemoteException ex) {
        return ex instanceof ConnectException || ex instanceof ConnectIOException || ex instanceof UnknownHostException || ex instanceof NoSuchObjectException || ex instanceof StubNotFoundException || ex.getCause() instanceof SocketException;
    }
}

