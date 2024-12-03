/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.factory.InterceptorFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.WithLazyParams;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultInterceptorFactory
implements InterceptorFactory {
    private static final Logger LOG = LogManager.getLogger(DefaultInterceptorFactory.class);
    private ObjectFactory objectFactory;
    private ReflectionProvider reflectionProvider;

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    @Override
    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map<String, String> interceptorRefParams) throws ConfigurationException {
        String message;
        Throwable cause;
        String interceptorClassName = interceptorConfig.getClassName();
        Map<String, String> thisInterceptorClassParams = interceptorConfig.getParams();
        HashMap<String, String> params = thisInterceptorClassParams == null ? new HashMap<String, String>() : new HashMap<String, String>(thisInterceptorClassParams);
        params.putAll(interceptorRefParams);
        try {
            Object o = this.objectFactory.buildBean(interceptorClassName, null);
            if (o instanceof WithLazyParams) {
                LOG.debug("Interceptor {} is marked with interface {} and params will be set during action invocation", (Object)interceptorClassName, (Object)WithLazyParams.class.getName());
            } else {
                this.reflectionProvider.setProperties(params, o);
            }
            if (o instanceof Interceptor) {
                Interceptor interceptor = (Interceptor)o;
                interceptor.init();
                return interceptor;
            }
            throw new ConfigurationException("Class [" + interceptorClassName + "] does not implement Interceptor", (Object)interceptorConfig);
        }
        catch (InstantiationException e) {
            cause = e;
            message = "Unable to instantiate an instance of Interceptor class [" + interceptorClassName + "].";
        }
        catch (IllegalAccessException e) {
            cause = e;
            message = "IllegalAccessException while attempting to instantiate an instance of Interceptor class [" + interceptorClassName + "].";
        }
        catch (ClassCastException e) {
            cause = e;
            message = "Class [" + interceptorClassName + "] does not implement com.opensymphony.xwork2.interceptor.Interceptor";
        }
        catch (Exception e) {
            cause = e;
            message = "Caught Exception while registering Interceptor class " + interceptorClassName;
        }
        catch (NoClassDefFoundError e) {
            cause = e;
            message = "Could not load class " + interceptorClassName + ". Perhaps it exists but certain dependencies are not available?";
        }
        throw new ConfigurationException(message, cause, interceptorConfig);
    }
}

