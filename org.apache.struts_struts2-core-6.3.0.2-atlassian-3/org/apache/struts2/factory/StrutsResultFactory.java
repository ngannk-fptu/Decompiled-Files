/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.factory;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.factory.ResultFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.result.ParamNameAwareResult;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionExceptionHandler;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.util.Map;

public class StrutsResultFactory
implements ResultFactory {
    protected ObjectFactory objectFactory;
    protected ReflectionProvider reflectionProvider;

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider provider) {
        this.reflectionProvider = provider;
    }

    @Override
    public Result buildResult(ResultConfig resultConfig, Map<String, Object> extraContext) throws Exception {
        String resultClassName = resultConfig.getClassName();
        Result result = null;
        if (resultClassName != null) {
            result = (Result)this.objectFactory.buildBean(resultClassName, extraContext);
            Map<String, String> params = resultConfig.getParams();
            if (params != null) {
                this.setParameters(extraContext, result, params);
            }
        }
        return result;
    }

    protected void setParameters(Map<String, Object> extraContext, Result result, Map<String, String> params) {
        for (Map.Entry<String, String> paramEntry : params.entrySet()) {
            try {
                String name = paramEntry.getKey();
                String value = paramEntry.getValue();
                this.setParameter(result, name, value, extraContext);
            }
            catch (ReflectionException ex) {
                if (!(result instanceof ReflectionExceptionHandler)) continue;
                ((ReflectionExceptionHandler)((Object)result)).handle(ex);
            }
        }
    }

    protected void setParameter(Result result, String name, String value, Map<String, Object> extraContext) {
        if (result instanceof ParamNameAwareResult) {
            if (((ParamNameAwareResult)((Object)result)).acceptableParameterName(name, value)) {
                this.reflectionProvider.setProperty(name, value, result, extraContext, true);
            }
        } else {
            this.reflectionProvider.setProperty(name, value, result, extraContext, true);
        }
    }
}

