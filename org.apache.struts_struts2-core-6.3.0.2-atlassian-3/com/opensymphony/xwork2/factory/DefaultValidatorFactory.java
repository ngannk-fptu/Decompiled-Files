/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.factory.ValidatorFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.validator.Validator;
import java.util.Map;

public class DefaultValidatorFactory
implements ValidatorFactory {
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
    public Validator buildValidator(String className, Map<String, Object> params, Map<String, Object> extraContext) throws Exception {
        Validator validator = (Validator)this.objectFactory.buildBean(className, extraContext);
        this.reflectionProvider.setProperties(params, validator, extraContext);
        return validator;
    }
}

