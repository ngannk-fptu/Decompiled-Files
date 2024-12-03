/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Configuration
 *  javax.validation.ConstraintValidatorFactory
 *  javax.validation.MessageInterpolator
 *  javax.validation.ParameterNameProvider
 *  javax.validation.TraversableResolver
 *  javax.validation.Validation
 *  javax.validation.ValidationException
 *  javax.validation.ValidationProviderResolver
 *  javax.validation.Validator
 *  javax.validation.ValidatorContext
 *  javax.validation.ValidatorFactory
 *  javax.validation.bootstrap.GenericBootstrap
 *  org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator
 *  org.hibernate.validator.spi.resourceloading.ResourceBundleLocator
 */
package org.springframework.validation.beanvalidation;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.validation.Configuration;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidationProviderResolver;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;
import javax.validation.bootstrap.GenericBootstrap;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.beanvalidation.LocaleContextMessageInterpolator;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

public class LocalValidatorFactoryBean
extends SpringValidatorAdapter
implements ValidatorFactory,
ApplicationContextAware,
InitializingBean,
DisposableBean {
    @Nullable
    private Class providerClass;
    @Nullable
    private ValidationProviderResolver validationProviderResolver;
    @Nullable
    private MessageInterpolator messageInterpolator;
    @Nullable
    private TraversableResolver traversableResolver;
    @Nullable
    private ConstraintValidatorFactory constraintValidatorFactory;
    @Nullable
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    @Nullable
    private Resource[] mappingLocations;
    private final Map<String, String> validationPropertyMap = new HashMap<String, String>();
    @Nullable
    private ApplicationContext applicationContext;
    @Nullable
    private ValidatorFactory validatorFactory;

    public void setProviderClass(Class providerClass) {
        this.providerClass = providerClass;
    }

    public void setValidationProviderResolver(ValidationProviderResolver validationProviderResolver) {
        this.validationProviderResolver = validationProviderResolver;
    }

    public void setMessageInterpolator(MessageInterpolator messageInterpolator) {
        this.messageInterpolator = messageInterpolator;
    }

    public void setValidationMessageSource(MessageSource messageSource) {
        this.messageInterpolator = HibernateValidatorDelegate.buildMessageInterpolator(messageSource);
    }

    public void setTraversableResolver(TraversableResolver traversableResolver) {
        this.traversableResolver = traversableResolver;
    }

    public void setConstraintValidatorFactory(ConstraintValidatorFactory constraintValidatorFactory) {
        this.constraintValidatorFactory = constraintValidatorFactory;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    public void setMappingLocations(Resource ... mappingLocations) {
        this.mappingLocations = mappingLocations;
    }

    public void setValidationProperties(Properties jpaProperties) {
        CollectionUtils.mergePropertiesIntoMap(jpaProperties, this.validationPropertyMap);
    }

    public void setValidationPropertyMap(@Nullable Map<String, String> validationProperties) {
        if (validationProperties != null) {
            this.validationPropertyMap.putAll(validationProperties);
        }
    }

    public Map<String, String> getValidationPropertyMap() {
        return this.validationPropertyMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        ConstraintValidatorFactory targetConstraintValidatorFactory;
        MessageInterpolator targetInterpolator;
        Configuration configuration;
        GenericBootstrap bootstrap;
        if (this.providerClass != null) {
            bootstrap = Validation.byProvider((Class)this.providerClass);
            if (this.validationProviderResolver != null) {
                bootstrap = bootstrap.providerResolver(this.validationProviderResolver);
            }
            configuration = bootstrap.configure();
        } else {
            bootstrap = Validation.byDefaultProvider();
            if (this.validationProviderResolver != null) {
                bootstrap = bootstrap.providerResolver(this.validationProviderResolver);
            }
            configuration = bootstrap.configure();
        }
        if (this.applicationContext != null) {
            try {
                Method eclMethod = configuration.getClass().getMethod("externalClassLoader", ClassLoader.class);
                ReflectionUtils.invokeMethod(eclMethod, configuration, this.applicationContext.getClassLoader());
            }
            catch (NoSuchMethodException eclMethod) {
                // empty catch block
            }
        }
        if ((targetInterpolator = this.messageInterpolator) == null) {
            targetInterpolator = configuration.getDefaultMessageInterpolator();
        }
        configuration.messageInterpolator((MessageInterpolator)new LocaleContextMessageInterpolator(targetInterpolator));
        if (this.traversableResolver != null) {
            configuration.traversableResolver(this.traversableResolver);
        }
        if ((targetConstraintValidatorFactory = this.constraintValidatorFactory) == null && this.applicationContext != null) {
            targetConstraintValidatorFactory = new SpringConstraintValidatorFactory(this.applicationContext.getAutowireCapableBeanFactory());
        }
        if (targetConstraintValidatorFactory != null) {
            configuration.constraintValidatorFactory(targetConstraintValidatorFactory);
        }
        if (this.parameterNameDiscoverer != null) {
            this.configureParameterNameProvider(this.parameterNameDiscoverer, configuration);
        }
        if (this.mappingLocations != null) {
            for (Resource location : this.mappingLocations) {
                try {
                    configuration.addMapping(location.getInputStream());
                }
                catch (IOException ex) {
                    throw new IllegalStateException("Cannot read mapping resource: " + location);
                }
            }
        }
        this.validationPropertyMap.forEach((arg_0, arg_1) -> ((Configuration)configuration).addProperty(arg_0, arg_1));
        this.postProcessConfiguration(configuration);
        this.validatorFactory = configuration.buildValidatorFactory();
        this.setTargetValidator(this.validatorFactory.getValidator());
    }

    private void configureParameterNameProvider(final ParameterNameDiscoverer discoverer, Configuration<?> configuration) {
        final ParameterNameProvider defaultProvider = configuration.getDefaultParameterNameProvider();
        configuration.parameterNameProvider(new ParameterNameProvider(){

            public List<String> getParameterNames(Constructor<?> constructor) {
                String[] paramNames = discoverer.getParameterNames(constructor);
                return paramNames != null ? Arrays.asList(paramNames) : defaultProvider.getParameterNames(constructor);
            }

            public List<String> getParameterNames(Method method) {
                String[] paramNames = discoverer.getParameterNames(method);
                return paramNames != null ? Arrays.asList(paramNames) : defaultProvider.getParameterNames(method);
            }
        });
    }

    protected void postProcessConfiguration(Configuration<?> configuration) {
    }

    public Validator getValidator() {
        Assert.notNull((Object)this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.getValidator();
    }

    public ValidatorContext usingContext() {
        Assert.notNull((Object)this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.usingContext();
    }

    public MessageInterpolator getMessageInterpolator() {
        Assert.notNull((Object)this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.getMessageInterpolator();
    }

    public TraversableResolver getTraversableResolver() {
        Assert.notNull((Object)this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.getTraversableResolver();
    }

    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        Assert.notNull((Object)this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.getConstraintValidatorFactory();
    }

    public ParameterNameProvider getParameterNameProvider() {
        Assert.notNull((Object)this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.getParameterNameProvider();
    }

    @Override
    public <T> T unwrap(@Nullable Class<T> type) {
        if (type == null || !ValidatorFactory.class.isAssignableFrom(type)) {
            try {
                return super.unwrap(type);
            }
            catch (ValidationException validationException) {
                // empty catch block
            }
        }
        if (this.validatorFactory != null) {
            try {
                return (T)this.validatorFactory.unwrap(type);
            }
            catch (ValidationException ex) {
                if (ValidatorFactory.class == type) {
                    return (T)this.validatorFactory;
                }
                throw ex;
            }
        }
        throw new ValidationException("Cannot unwrap to " + type);
    }

    public void close() {
        if (this.validatorFactory != null) {
            this.validatorFactory.close();
        }
    }

    @Override
    public void destroy() {
        this.close();
    }

    private static class HibernateValidatorDelegate {
        private HibernateValidatorDelegate() {
        }

        public static MessageInterpolator buildMessageInterpolator(MessageSource messageSource) {
            return new ResourceBundleMessageInterpolator((ResourceBundleLocator)new MessageSourceResourceBundleLocator(messageSource));
        }
    }
}

