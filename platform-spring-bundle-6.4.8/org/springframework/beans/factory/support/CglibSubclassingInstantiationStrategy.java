/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.LookupOverride;
import org.springframework.beans.factory.support.MethodOverride;
import org.springframework.beans.factory.support.MethodReplacer;
import org.springframework.beans.factory.support.ReplaceOverride;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.SimpleInstantiationStrategy;
import org.springframework.cglib.core.ClassLoaderAwareGeneratorStrategy;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class CglibSubclassingInstantiationStrategy
extends SimpleInstantiationStrategy {
    private static final int PASSTHROUGH = 0;
    private static final int LOOKUP_OVERRIDE = 1;
    private static final int METHOD_REPLACER = 2;

    @Override
    protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
        return this.instantiateWithMethodInjection(bd, beanName, owner, null, new Object[0]);
    }

    @Override
    protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner, @Nullable Constructor<?> ctor, Object ... args) {
        return new CglibSubclassCreator(bd, owner).instantiate(ctor, args);
    }

    private static class ReplaceOverrideMethodInterceptor
    extends CglibIdentitySupport
    implements MethodInterceptor {
        private final BeanFactory owner;

        public ReplaceOverrideMethodInterceptor(RootBeanDefinition beanDefinition, BeanFactory owner) {
            super(beanDefinition);
            this.owner = owner;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy mp) throws Throwable {
            ReplaceOverride ro = (ReplaceOverride)this.getBeanDefinition().getMethodOverrides().getOverride(method);
            Assert.state(ro != null, "ReplaceOverride not found");
            MethodReplacer mr = this.owner.getBean(ro.getMethodReplacerBeanName(), MethodReplacer.class);
            return mr.reimplement(obj, method, args);
        }
    }

    private static class LookupOverrideMethodInterceptor
    extends CglibIdentitySupport
    implements MethodInterceptor {
        private final BeanFactory owner;

        public LookupOverrideMethodInterceptor(RootBeanDefinition beanDefinition, BeanFactory owner) {
            super(beanDefinition);
            this.owner = owner;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy mp) throws Throwable {
            Object[] argsToUse;
            LookupOverride lo = (LookupOverride)this.getBeanDefinition().getMethodOverrides().getOverride(method);
            Assert.state(lo != null, "LookupOverride not found");
            Object object = argsToUse = args.length > 0 ? args : null;
            if (StringUtils.hasText(lo.getBeanName())) {
                Object bean2 = argsToUse != null ? this.owner.getBean(lo.getBeanName(), argsToUse) : this.owner.getBean(lo.getBeanName());
                return bean2.equals(null) ? null : bean2;
            }
            ResolvableType genericReturnType = ResolvableType.forMethodReturnType(method);
            return argsToUse != null ? this.owner.getBeanProvider(genericReturnType).getObject(argsToUse) : this.owner.getBeanProvider(genericReturnType).getObject();
        }
    }

    private static class MethodOverrideCallbackFilter
    extends CglibIdentitySupport
    implements CallbackFilter {
        private static final Log logger = LogFactory.getLog(MethodOverrideCallbackFilter.class);

        public MethodOverrideCallbackFilter(RootBeanDefinition beanDefinition) {
            super(beanDefinition);
        }

        @Override
        public int accept(Method method) {
            MethodOverride methodOverride = this.getBeanDefinition().getMethodOverrides().getOverride(method);
            if (logger.isTraceEnabled()) {
                logger.trace((Object)("MethodOverride for " + method + ": " + methodOverride));
            }
            if (methodOverride == null) {
                return 0;
            }
            if (methodOverride instanceof LookupOverride) {
                return 1;
            }
            if (methodOverride instanceof ReplaceOverride) {
                return 2;
            }
            throw new UnsupportedOperationException("Unexpected MethodOverride subclass: " + methodOverride.getClass().getName());
        }
    }

    private static class CglibIdentitySupport {
        private final RootBeanDefinition beanDefinition;

        public CglibIdentitySupport(RootBeanDefinition beanDefinition) {
            this.beanDefinition = beanDefinition;
        }

        public RootBeanDefinition getBeanDefinition() {
            return this.beanDefinition;
        }

        public boolean equals(@Nullable Object other) {
            return other != null && this.getClass() == other.getClass() && this.beanDefinition.equals(((CglibIdentitySupport)other).beanDefinition);
        }

        public int hashCode() {
            return this.beanDefinition.hashCode();
        }
    }

    private static class CglibSubclassCreator {
        private static final Class<?>[] CALLBACK_TYPES = new Class[]{NoOp.class, LookupOverrideMethodInterceptor.class, ReplaceOverrideMethodInterceptor.class};
        private final RootBeanDefinition beanDefinition;
        private final BeanFactory owner;

        CglibSubclassCreator(RootBeanDefinition beanDefinition, BeanFactory owner) {
            this.beanDefinition = beanDefinition;
            this.owner = owner;
        }

        public Object instantiate(@Nullable Constructor<?> ctor, Object ... args) {
            Object instance;
            Class<?> subclass = this.createEnhancedSubclass(this.beanDefinition);
            if (ctor == null) {
                instance = BeanUtils.instantiateClass(subclass);
            } else {
                try {
                    Constructor<?> enhancedSubclassConstructor = subclass.getConstructor(ctor.getParameterTypes());
                    instance = enhancedSubclassConstructor.newInstance(args);
                }
                catch (Exception ex) {
                    throw new BeanInstantiationException(this.beanDefinition.getBeanClass(), "Failed to invoke constructor for CGLIB enhanced subclass [" + subclass.getName() + "]", (Throwable)ex);
                }
            }
            Factory factory = (Factory)instance;
            factory.setCallbacks(new Callback[]{NoOp.INSTANCE, new LookupOverrideMethodInterceptor(this.beanDefinition, this.owner), new ReplaceOverrideMethodInterceptor(this.beanDefinition, this.owner)});
            return instance;
        }

        private Class<?> createEnhancedSubclass(RootBeanDefinition beanDefinition) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(beanDefinition.getBeanClass());
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            if (this.owner instanceof ConfigurableBeanFactory) {
                ClassLoader cl = ((ConfigurableBeanFactory)this.owner).getBeanClassLoader();
                enhancer.setStrategy(new ClassLoaderAwareGeneratorStrategy(cl));
            }
            enhancer.setCallbackFilter(new MethodOverrideCallbackFilter(beanDefinition));
            enhancer.setCallbackTypes(CALLBACK_TYPES);
            return enhancer.createClass();
        }
    }
}

