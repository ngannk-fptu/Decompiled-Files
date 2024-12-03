/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.factory.ResultFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionExceptionHandler;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.util.Map;

public class DefaultResultFactory
implements ResultFactory {
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
    public Result buildResult(ResultConfig resultConfig, Map<String, Object> extraContext) throws Exception {
        String resultClassName = resultConfig.getClassName();
        Result result = null;
        if (resultClassName != null) {
            result = (Result)this.objectFactory.buildBean(resultClassName, extraContext);
            Map<String, String> params = resultConfig.getParams();
            if (params != null) {
                for (Map.Entry<String, String> paramEntry : params.entrySet()) {
                    try {
                        this.reflectionProvider.setProperty(paramEntry.getKey(), paramEntry.getValue(), result, extraContext, true);
                    }
                    catch (ReflectionException ex) {
                        if (!(result instanceof ReflectionExceptionHandler)) continue;
                        ((ReflectionExceptionHandler)((Object)result)).handle(ex);
                    }
                }
            }
        }
        return result;
    }
}

