/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 *  javax.resource.spi.UnavailableException
 *  javax.resource.spi.endpoint.MessageEndpoint
 *  org.aopalliance.aop.Advice
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.aop.support.DelegatingIntroductionInterceptor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.jca.endpoint;

import java.lang.reflect.Method;
import javax.resource.ResourceException;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.transaction.xa.XAResource;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.jca.endpoint.AbstractMessageEndpointFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class GenericMessageEndpointFactory
extends AbstractMessageEndpointFactory {
    @Nullable
    private Object messageListener;

    public void setMessageListener(Object messageListener) {
        this.messageListener = messageListener;
    }

    protected Object getMessageListener() {
        Assert.state((this.messageListener != null ? 1 : 0) != 0, (String)"No message listener set");
        return this.messageListener;
    }

    @Override
    public MessageEndpoint createEndpoint(XAResource xaResource) throws UnavailableException {
        GenericMessageEndpoint endpoint = (GenericMessageEndpoint)super.createEndpoint(xaResource);
        Object target = this.getMessageListener();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        DelegatingIntroductionInterceptor introduction = new DelegatingIntroductionInterceptor((Object)endpoint);
        introduction.suppressInterface(MethodInterceptor.class);
        proxyFactory.addAdvice((Advice)introduction);
        return (MessageEndpoint)proxyFactory.getProxy(target.getClass().getClassLoader());
    }

    @Override
    protected AbstractMessageEndpointFactory.AbstractMessageEndpoint createEndpointInternal() throws UnavailableException {
        return new GenericMessageEndpoint();
    }

    public static class InternalResourceException
    extends RuntimeException {
        public InternalResourceException(ResourceException cause) {
            super(cause);
        }
    }

    private class GenericMessageEndpoint
    extends AbstractMessageEndpointFactory.AbstractMessageEndpoint
    implements MethodInterceptor {
        private GenericMessageEndpoint() {
        }

        @Nullable
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            boolean applyDeliveryCalls;
            Throwable endpointEx = null;
            boolean bl = applyDeliveryCalls = !this.hasBeforeDeliveryBeenCalled();
            if (applyDeliveryCalls) {
                try {
                    this.beforeDelivery(null);
                }
                catch (ResourceException ex) {
                    throw this.adaptExceptionIfNecessary(methodInvocation, ex);
                }
            }
            try {
                Object ex = methodInvocation.proceed();
                return ex;
            }
            catch (Throwable ex) {
                endpointEx = ex;
                this.onEndpointException(ex);
                throw ex;
            }
            finally {
                block13: {
                    if (applyDeliveryCalls) {
                        try {
                            this.afterDelivery();
                        }
                        catch (ResourceException ex) {
                            if (endpointEx != null) break block13;
                            throw this.adaptExceptionIfNecessary(methodInvocation, ex);
                        }
                    }
                }
            }
        }

        private Exception adaptExceptionIfNecessary(MethodInvocation methodInvocation, ResourceException ex) {
            if (ReflectionUtils.declaresException((Method)methodInvocation.getMethod(), ((Object)((Object)ex)).getClass())) {
                return ex;
            }
            return new InternalResourceException(ex);
        }

        @Override
        protected ClassLoader getEndpointClassLoader() {
            return GenericMessageEndpointFactory.this.getMessageListener().getClass().getClassLoader();
        }
    }
}

