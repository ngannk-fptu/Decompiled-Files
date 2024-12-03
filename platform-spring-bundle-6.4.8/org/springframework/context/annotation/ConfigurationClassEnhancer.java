/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.context.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.scope.ScopedProxyFactoryBean;
import org.springframework.asm.Type;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.SimpleInstantiationStrategy;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.ClassLoaderAwareGeneratorStrategy;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.cglib.transform.ClassEmitterTransformer;
import org.springframework.cglib.transform.TransformingClassGenerator;
import org.springframework.context.annotation.BeanAnnotationHelper;
import org.springframework.context.annotation.ScopedProxyCreator;
import org.springframework.lang.Nullable;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.SpringObjenesis;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

class ConfigurationClassEnhancer {
    private static final Callback[] CALLBACKS = new Callback[]{new BeanMethodInterceptor(), new BeanFactoryAwareMethodInterceptor(), NoOp.INSTANCE};
    private static final ConditionalCallbackFilter CALLBACK_FILTER = new ConditionalCallbackFilter(CALLBACKS);
    private static final String BEAN_FACTORY_FIELD = "$$beanFactory";
    private static final Log logger = LogFactory.getLog(ConfigurationClassEnhancer.class);
    private static final SpringObjenesis objenesis = new SpringObjenesis();

    ConfigurationClassEnhancer() {
    }

    public Class<?> enhance(Class<?> configClass, @Nullable ClassLoader classLoader) {
        if (EnhancedConfiguration.class.isAssignableFrom(configClass)) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)String.format("Ignoring request to enhance %s as it has already been enhanced. This usually indicates that more than one ConfigurationClassPostProcessor has been registered (e.g. via <context:annotation-config>). This is harmless, but you may want check your configuration and remove one CCPP if possible", configClass.getName()));
            }
            return configClass;
        }
        Class<?> enhancedClass = this.createClass(this.newEnhancer(configClass, classLoader));
        if (logger.isTraceEnabled()) {
            logger.trace((Object)String.format("Successfully enhanced %s; enhanced class name is: %s", configClass.getName(), enhancedClass.getName()));
        }
        return enhancedClass;
    }

    private Enhancer newEnhancer(Class<?> configSuperClass, @Nullable ClassLoader classLoader) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(configSuperClass);
        enhancer.setInterfaces(new Class[]{EnhancedConfiguration.class});
        enhancer.setUseFactory(false);
        enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
        enhancer.setStrategy(new BeanFactoryAwareGeneratorStrategy(classLoader));
        enhancer.setCallbackFilter(CALLBACK_FILTER);
        enhancer.setCallbackTypes(CALLBACK_FILTER.getCallbackTypes());
        return enhancer;
    }

    private Class<?> createClass(Enhancer enhancer) {
        Class subclass = enhancer.createClass();
        Enhancer.registerStaticCallbacks(subclass, CALLBACKS);
        return subclass;
    }

    private static class BeanMethodInterceptor
    implements MethodInterceptor,
    ConditionalCallback {
        private BeanMethodInterceptor() {
        }

        @Override
        @Nullable
        public Object intercept(Object enhancedConfigInstance, Method beanMethod, Object[] beanMethodArgs, MethodProxy cglibMethodProxy) throws Throwable {
            Object factoryBean;
            String scopedBeanName;
            ConfigurableBeanFactory beanFactory = this.getBeanFactory(enhancedConfigInstance);
            String beanName = BeanAnnotationHelper.determineBeanNameFor(beanMethod);
            if (BeanAnnotationHelper.isScopedProxy(beanMethod) && beanFactory.isCurrentlyInCreation(scopedBeanName = ScopedProxyCreator.getTargetBeanName(beanName))) {
                beanName = scopedBeanName;
            }
            if (this.factoryContainsBean(beanFactory, "&" + beanName) && this.factoryContainsBean(beanFactory, beanName) && !((factoryBean = beanFactory.getBean("&" + beanName)) instanceof ScopedProxyFactoryBean)) {
                return this.enhanceFactoryBean(factoryBean, beanMethod.getReturnType(), beanFactory, beanName);
            }
            if (this.isCurrentlyInvokedFactoryMethod(beanMethod)) {
                if (logger.isInfoEnabled() && BeanFactoryPostProcessor.class.isAssignableFrom(beanMethod.getReturnType())) {
                    logger.info((Object)String.format("@Bean method %s.%s is non-static and returns an object assignable to Spring's BeanFactoryPostProcessor interface. This will result in a failure to process annotations such as @Autowired, @Resource and @PostConstruct within the method's declaring @Configuration class. Add the 'static' modifier to this method to avoid these container lifecycle issues; see @Bean javadoc for complete details.", beanMethod.getDeclaringClass().getSimpleName(), beanMethod.getName()));
                }
                return cglibMethodProxy.invokeSuper(enhancedConfigInstance, beanMethodArgs);
            }
            return this.resolveBeanReference(beanMethod, beanMethodArgs, beanFactory, beanName);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Object resolveBeanReference(Method beanMethod, Object[] beanMethodArgs, ConfigurableBeanFactory beanFactory, String beanName) {
            boolean alreadyInCreation = beanFactory.isCurrentlyInCreation(beanName);
            try {
                Method currentlyInvoked;
                Object beanInstance;
                boolean useArgs;
                if (alreadyInCreation) {
                    beanFactory.setCurrentlyInCreation(beanName, false);
                }
                boolean bl = useArgs = !ObjectUtils.isEmpty(beanMethodArgs);
                if (useArgs && beanFactory.isSingleton(beanName)) {
                    for (Object arg : beanMethodArgs) {
                        if (arg != null) continue;
                        useArgs = false;
                        break;
                    }
                }
                Object object = beanInstance = useArgs ? beanFactory.getBean(beanName, beanMethodArgs) : beanFactory.getBean(beanName);
                if (!ClassUtils.isAssignableValue(beanMethod.getReturnType(), beanInstance)) {
                    if (beanInstance.equals(null)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug((Object)String.format("@Bean method %s.%s called as bean reference for type [%s] returned null bean; resolving to null value.", beanMethod.getDeclaringClass().getSimpleName(), beanMethod.getName(), beanMethod.getReturnType().getName()));
                        }
                        beanInstance = null;
                    } else {
                        String msg = String.format("@Bean method %s.%s called as bean reference for type [%s] but overridden by non-compatible bean instance of type [%s].", beanMethod.getDeclaringClass().getSimpleName(), beanMethod.getName(), beanMethod.getReturnType().getName(), beanInstance.getClass().getName());
                        try {
                            BeanDefinition beanDefinition = beanFactory.getMergedBeanDefinition(beanName);
                            msg = msg + " Overriding bean of same name declared in: " + beanDefinition.getResourceDescription();
                        }
                        catch (NoSuchBeanDefinitionException beanDefinition) {
                            // empty catch block
                        }
                        throw new IllegalStateException(msg);
                    }
                }
                if ((currentlyInvoked = SimpleInstantiationStrategy.getCurrentlyInvokedFactoryMethod()) != null) {
                    String outerBeanName = BeanAnnotationHelper.determineBeanNameFor(currentlyInvoked);
                    beanFactory.registerDependentBean(beanName, outerBeanName);
                }
                Object object2 = beanInstance;
                return object2;
            }
            finally {
                if (alreadyInCreation) {
                    beanFactory.setCurrentlyInCreation(beanName, true);
                }
            }
        }

        @Override
        public boolean isMatch(Method candidateMethod) {
            return candidateMethod.getDeclaringClass() != Object.class && !BeanFactoryAwareMethodInterceptor.isSetBeanFactory(candidateMethod) && BeanAnnotationHelper.isBeanAnnotated(candidateMethod);
        }

        private ConfigurableBeanFactory getBeanFactory(Object enhancedConfigInstance) {
            Field field = ReflectionUtils.findField(enhancedConfigInstance.getClass(), ConfigurationClassEnhancer.BEAN_FACTORY_FIELD);
            Assert.state(field != null, "Unable to find generated bean factory field");
            Object beanFactory = ReflectionUtils.getField(field, enhancedConfigInstance);
            Assert.state(beanFactory != null, "BeanFactory has not been injected into @Configuration class");
            Assert.state(beanFactory instanceof ConfigurableBeanFactory, "Injected BeanFactory is not a ConfigurableBeanFactory");
            return (ConfigurableBeanFactory)beanFactory;
        }

        private boolean factoryContainsBean(ConfigurableBeanFactory beanFactory, String beanName) {
            return beanFactory.containsBean(beanName) && !beanFactory.isCurrentlyInCreation(beanName);
        }

        private boolean isCurrentlyInvokedFactoryMethod(Method method) {
            Method currentlyInvoked = SimpleInstantiationStrategy.getCurrentlyInvokedFactoryMethod();
            return currentlyInvoked != null && method.getName().equals(currentlyInvoked.getName()) && Arrays.equals(method.getParameterTypes(), currentlyInvoked.getParameterTypes());
        }

        private Object enhanceFactoryBean(Object factoryBean, Class<?> exposedType, ConfigurableBeanFactory beanFactory, String beanName) {
            try {
                Class<?> clazz = factoryBean.getClass();
                boolean finalClass = Modifier.isFinal(clazz.getModifiers());
                boolean finalMethod = Modifier.isFinal(clazz.getMethod("getObject", new Class[0]).getModifiers());
                if (finalClass || finalMethod) {
                    if (exposedType.isInterface()) {
                        if (logger.isTraceEnabled()) {
                            logger.trace((Object)("Creating interface proxy for FactoryBean '" + beanName + "' of type [" + clazz.getName() + "] for use within another @Bean method because its " + (finalClass ? "implementation class" : "getObject() method") + " is final: Otherwise a getObject() call would not be routed to the factory."));
                        }
                        return this.createInterfaceProxyForFactoryBean(factoryBean, exposedType, beanFactory, beanName);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug((Object)("Unable to proxy FactoryBean '" + beanName + "' of type [" + clazz.getName() + "] for use within another @Bean method because its " + (finalClass ? "implementation class" : "getObject() method") + " is final: A getObject() call will NOT be routed to the factory. Consider declaring the return type as a FactoryBean interface."));
                    }
                    return factoryBean;
                }
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
            return this.createCglibProxyForFactoryBean(factoryBean, beanFactory, beanName);
        }

        private Object createInterfaceProxyForFactoryBean(Object factoryBean, Class<?> interfaceType, ConfigurableBeanFactory beanFactory, String beanName) {
            return Proxy.newProxyInstance(factoryBean.getClass().getClassLoader(), new Class[]{interfaceType}, (proxy, method, args) -> {
                if (method.getName().equals("getObject") && args == null) {
                    return beanFactory.getBean(beanName);
                }
                return ReflectionUtils.invokeMethod(method, factoryBean, args);
            });
        }

        private Object createCglibProxyForFactoryBean(Object factoryBean, ConfigurableBeanFactory beanFactory, String beanName) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(factoryBean.getClass());
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            enhancer.setCallbackType(MethodInterceptor.class);
            Class fbClass = enhancer.createClass();
            Object fbProxy = null;
            if (objenesis.isWorthTrying()) {
                try {
                    fbProxy = objenesis.newInstance(fbClass, enhancer.getUseCache());
                }
                catch (ObjenesisException ex) {
                    logger.debug((Object)"Unable to instantiate enhanced FactoryBean using Objenesis, falling back to regular construction", (Throwable)ex);
                }
            }
            if (fbProxy == null) {
                try {
                    fbProxy = ReflectionUtils.accessibleConstructor(fbClass, new Class[0]).newInstance(new Object[0]);
                }
                catch (Throwable ex) {
                    throw new IllegalStateException("Unable to instantiate enhanced FactoryBean using Objenesis, and regular FactoryBean instantiation via default constructor fails as well", ex);
                }
            }
            ((Factory)fbProxy).setCallback(0, (obj, method, args, proxy) -> {
                if (method.getName().equals("getObject") && args.length == 0) {
                    return beanFactory.getBean(beanName);
                }
                return proxy.invoke(factoryBean, args);
            });
            return fbProxy;
        }
    }

    private static class BeanFactoryAwareMethodInterceptor
    implements MethodInterceptor,
    ConditionalCallback {
        private BeanFactoryAwareMethodInterceptor() {
        }

        @Override
        @Nullable
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            Field field = ReflectionUtils.findField(obj.getClass(), ConfigurationClassEnhancer.BEAN_FACTORY_FIELD);
            Assert.state(field != null, "Unable to find generated BeanFactory field");
            field.set(obj, args[0]);
            if (BeanFactoryAware.class.isAssignableFrom(ClassUtils.getUserClass(obj.getClass().getSuperclass()))) {
                return proxy.invokeSuper(obj, args);
            }
            return null;
        }

        @Override
        public boolean isMatch(Method candidateMethod) {
            return BeanFactoryAwareMethodInterceptor.isSetBeanFactory(candidateMethod);
        }

        public static boolean isSetBeanFactory(Method candidateMethod) {
            return candidateMethod.getName().equals("setBeanFactory") && candidateMethod.getParameterCount() == 1 && BeanFactory.class == candidateMethod.getParameterTypes()[0] && BeanFactoryAware.class.isAssignableFrom(candidateMethod.getDeclaringClass());
        }
    }

    private static class BeanFactoryAwareGeneratorStrategy
    extends ClassLoaderAwareGeneratorStrategy {
        public BeanFactoryAwareGeneratorStrategy(@Nullable ClassLoader classLoader) {
            super(classLoader);
        }

        @Override
        protected ClassGenerator transform(ClassGenerator cg) throws Exception {
            ClassEmitterTransformer transformer = new ClassEmitterTransformer(){

                @Override
                public void end_class() {
                    this.declare_field(1, ConfigurationClassEnhancer.BEAN_FACTORY_FIELD, Type.getType(BeanFactory.class), null);
                    super.end_class();
                }
            };
            return new TransformingClassGenerator(cg, transformer);
        }
    }

    private static class ConditionalCallbackFilter
    implements CallbackFilter {
        private final Callback[] callbacks;
        private final Class<?>[] callbackTypes;

        public ConditionalCallbackFilter(Callback[] callbacks) {
            this.callbacks = callbacks;
            this.callbackTypes = new Class[callbacks.length];
            for (int i2 = 0; i2 < callbacks.length; ++i2) {
                this.callbackTypes[i2] = callbacks[i2].getClass();
            }
        }

        @Override
        public int accept(Method method) {
            for (int i2 = 0; i2 < this.callbacks.length; ++i2) {
                Callback callback = this.callbacks[i2];
                if (callback instanceof ConditionalCallback && !((ConditionalCallback)callback).isMatch(method)) continue;
                return i2;
            }
            throw new IllegalStateException("No callback available for method " + method.getName());
        }

        public Class<?>[] getCallbackTypes() {
            return this.callbackTypes;
        }
    }

    private static interface ConditionalCallback
    extends Callback {
        public boolean isMatch(Method var1);
    }

    public static interface EnhancedConfiguration
    extends BeanFactoryAware {
    }
}

