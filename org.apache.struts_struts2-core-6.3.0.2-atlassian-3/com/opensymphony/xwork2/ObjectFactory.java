/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.UnknownHandler;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.factory.ActionFactory;
import com.opensymphony.xwork2.factory.ConverterFactory;
import com.opensymphony.xwork2.factory.InterceptorFactory;
import com.opensymphony.xwork2.factory.ResultFactory;
import com.opensymphony.xwork2.factory.UnknownHandlerFactory;
import com.opensymphony.xwork2.factory.ValidatorFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.validator.Validator;
import java.io.Serializable;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ObjectFactory
implements Serializable {
    private static final Logger LOG = LogManager.getLogger(ObjectFactory.class);
    private transient ClassLoader ccl;
    private Container container;
    private ActionFactory actionFactory;
    private ResultFactory resultFactory;
    private InterceptorFactory interceptorFactory;
    private ValidatorFactory validatorFactory;
    private ConverterFactory converterFactory;
    private UnknownHandlerFactory unknownHandlerFactory;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(value="struts.objectFactory.classloader", required=false)
    public void setClassLoader(ClassLoader cl) {
        this.ccl = cl;
    }

    @Inject
    public void setActionFactory(ActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    @Inject
    public void setResultFactory(ResultFactory resultFactory) {
        this.resultFactory = resultFactory;
    }

    @Inject
    public void setInterceptorFactory(InterceptorFactory interceptorFactory) {
        this.interceptorFactory = interceptorFactory;
    }

    @Inject
    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    @Inject
    public void setConverterFactory(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    @Inject
    public void setUnknownHandlerFactory(UnknownHandlerFactory unknownHandlerFactory) {
        this.unknownHandlerFactory = unknownHandlerFactory;
    }

    public boolean isNoArgConstructorRequired() {
        return true;
    }

    public Class getClassInstance(String className) throws ClassNotFoundException {
        if (this.ccl != null) {
            return this.ccl.loadClass(className);
        }
        return ClassLoaderUtil.loadClass(className, this.getClass());
    }

    public Object buildAction(String actionName, String namespace, ActionConfig config, Map<String, Object> extraContext) throws Exception {
        return this.actionFactory.buildAction(actionName, namespace, config, extraContext);
    }

    public Object buildBean(Class clazz, Map<String, Object> extraContext) throws Exception {
        return this.container.inject(clazz);
    }

    protected Object injectInternalBeans(Object obj) {
        if (obj != null && this.container != null) {
            LOG.debug("Injecting internal beans into [{}]", (Object)obj.getClass().getSimpleName());
            this.container.inject(obj);
        }
        return obj;
    }

    public Object buildBean(String className, Map<String, Object> extraContext) throws Exception {
        return this.buildBean(className, extraContext, true);
    }

    public Object buildBean(String className, Map<String, Object> extraContext, boolean injectInternal) throws Exception {
        Class clazz = this.getClassInstance(className);
        return this.buildBean(clazz, extraContext);
    }

    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map<String, String> interceptorRefParams) throws ConfigurationException {
        return this.interceptorFactory.buildInterceptor(interceptorConfig, interceptorRefParams);
    }

    public Result buildResult(ResultConfig resultConfig, Map<String, Object> extraContext) throws Exception {
        return this.resultFactory.buildResult(resultConfig, extraContext);
    }

    public Validator buildValidator(String className, Map<String, Object> params, Map<String, Object> extraContext) throws Exception {
        return this.validatorFactory.buildValidator(className, params, extraContext);
    }

    public TypeConverter buildConverter(Class<? extends TypeConverter> converterClass, Map<String, Object> extraContext) throws Exception {
        return this.converterFactory.buildConverter(converterClass, extraContext);
    }

    public UnknownHandler buildUnknownHandler(String unknownHandlerName, Map<String, Object> extraContext) throws Exception {
        return this.unknownHandlerFactory.buildUnknownHandler(unknownHandlerName, extraContext);
    }
}

