/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.caucho.hessian.HessianException
 *  com.caucho.hessian.client.HessianConnectionException
 *  com.caucho.hessian.client.HessianConnectionFactory
 *  com.caucho.hessian.client.HessianProxyFactory
 *  com.caucho.hessian.client.HessianRuntimeException
 *  com.caucho.hessian.io.SerializerFactory
 */
package org.springframework.remoting.caucho;

import com.caucho.hessian.HessianException;
import com.caucho.hessian.client.HessianConnectionException;
import com.caucho.hessian.client.HessianConnectionFactory;
import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import com.caucho.hessian.io.SerializerFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;
import org.springframework.util.Assert;

@Deprecated
public class HessianClientInterceptor
extends UrlBasedRemoteAccessor
implements MethodInterceptor {
    private HessianProxyFactory proxyFactory = new HessianProxyFactory();
    @Nullable
    private Object hessianProxy;

    public void setProxyFactory(@Nullable HessianProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory != null ? proxyFactory : new HessianProxyFactory();
    }

    public void setSerializerFactory(SerializerFactory serializerFactory) {
        this.proxyFactory.setSerializerFactory(serializerFactory);
    }

    public void setSendCollectionType(boolean sendCollectionType) {
        this.proxyFactory.getSerializerFactory().setSendCollectionType(sendCollectionType);
    }

    public void setAllowNonSerializable(boolean allowNonSerializable) {
        this.proxyFactory.getSerializerFactory().setAllowNonSerializable(allowNonSerializable);
    }

    public void setOverloadEnabled(boolean overloadEnabled) {
        this.proxyFactory.setOverloadEnabled(overloadEnabled);
    }

    public void setUsername(String username) {
        this.proxyFactory.setUser(username);
    }

    public void setPassword(String password) {
        this.proxyFactory.setPassword(password);
    }

    public void setDebug(boolean debug) {
        this.proxyFactory.setDebug(debug);
    }

    public void setChunkedPost(boolean chunkedPost) {
        this.proxyFactory.setChunkedPost(chunkedPost);
    }

    public void setConnectionFactory(HessianConnectionFactory connectionFactory) {
        this.proxyFactory.setConnectionFactory(connectionFactory);
    }

    public void setConnectTimeout(long timeout) {
        this.proxyFactory.setConnectTimeout(timeout);
    }

    public void setReadTimeout(long timeout) {
        this.proxyFactory.setReadTimeout(timeout);
    }

    public void setHessian2(boolean hessian2) {
        this.proxyFactory.setHessian2Request(hessian2);
        this.proxyFactory.setHessian2Reply(hessian2);
    }

    public void setHessian2Request(boolean hessian2) {
        this.proxyFactory.setHessian2Request(hessian2);
    }

    public void setHessian2Reply(boolean hessian2) {
        this.proxyFactory.setHessian2Reply(hessian2);
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.prepare();
    }

    public void prepare() throws RemoteLookupFailureException {
        try {
            this.hessianProxy = this.createHessianProxy(this.proxyFactory);
        }
        catch (MalformedURLException ex) {
            throw new RemoteLookupFailureException("Service URL [" + this.getServiceUrl() + "] is invalid", ex);
        }
    }

    protected Object createHessianProxy(HessianProxyFactory proxyFactory) throws MalformedURLException {
        Assert.notNull(this.getServiceInterface(), "'serviceInterface' is required");
        return proxyFactory.create(this.getServiceInterface(), this.getServiceUrl(), this.getBeanClassLoader());
    }

    @Override
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (this.hessianProxy == null) {
            throw new IllegalStateException("HessianClientInterceptor is not properly initialized - invoke 'prepare' before attempting any operations");
        }
        ClassLoader originalClassLoader = this.overrideThreadContextClassLoader();
        try {
            Object object = invocation.getMethod().invoke(this.hessianProxy, invocation.getArguments());
            return object;
        }
        catch (InvocationTargetException ex) {
            Throwable targetEx = ex.getTargetException();
            if (targetEx instanceof InvocationTargetException) {
                targetEx = ((InvocationTargetException)targetEx).getTargetException();
            }
            if (targetEx instanceof HessianConnectionException) {
                throw this.convertHessianAccessException(targetEx);
            }
            if (targetEx instanceof HessianException || targetEx instanceof HessianRuntimeException) {
                Throwable cause = targetEx.getCause();
                throw this.convertHessianAccessException(cause != null ? cause : targetEx);
            }
            if (targetEx instanceof UndeclaredThrowableException) {
                UndeclaredThrowableException utex = (UndeclaredThrowableException)targetEx;
                throw this.convertHessianAccessException(utex.getUndeclaredThrowable());
            }
            throw targetEx;
        }
        catch (Throwable ex) {
            throw new RemoteProxyFailureException("Failed to invoke Hessian proxy for remote service [" + this.getServiceUrl() + "]", ex);
        }
        finally {
            this.resetThreadContextClassLoader(originalClassLoader);
        }
    }

    protected RemoteAccessException convertHessianAccessException(Throwable ex) {
        if (ex instanceof HessianConnectionException || ex instanceof ConnectException) {
            return new RemoteConnectFailureException("Cannot connect to Hessian remote service at [" + this.getServiceUrl() + "]", ex);
        }
        return new RemoteAccessException("Cannot access Hessian remote service at [" + this.getServiceUrl() + "]", ex);
    }
}

