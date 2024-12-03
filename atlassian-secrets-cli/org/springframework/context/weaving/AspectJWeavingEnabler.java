/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter
 */
package org.springframework.context.weaving;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.Ordered;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.lang.Nullable;

public class AspectJWeavingEnabler
implements BeanFactoryPostProcessor,
BeanClassLoaderAware,
LoadTimeWeaverAware,
Ordered {
    public static final String ASPECTJ_AOP_XML_RESOURCE = "META-INF/aop.xml";
    @Nullable
    private ClassLoader beanClassLoader;
    @Nullable
    private LoadTimeWeaver loadTimeWeaver;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
        this.loadTimeWeaver = loadTimeWeaver;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        AspectJWeavingEnabler.enableAspectJWeaving(this.loadTimeWeaver, this.beanClassLoader);
    }

    public static void enableAspectJWeaving(@Nullable LoadTimeWeaver weaverToUse, @Nullable ClassLoader beanClassLoader) {
        if (weaverToUse == null) {
            if (InstrumentationLoadTimeWeaver.isInstrumentationAvailable()) {
                weaverToUse = new InstrumentationLoadTimeWeaver(beanClassLoader);
            } else {
                throw new IllegalStateException("No LoadTimeWeaver available");
            }
        }
        weaverToUse.addTransformer(new AspectJClassBypassingClassFileTransformer((ClassFileTransformer)new ClassPreProcessorAgentAdapter()));
    }

    private static class AspectJClassBypassingClassFileTransformer
    implements ClassFileTransformer {
        private final ClassFileTransformer delegate;

        public AspectJClassBypassingClassFileTransformer(ClassFileTransformer delegate) {
            this.delegate = delegate;
        }

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            if (className.startsWith("org.aspectj") || className.startsWith("org/aspectj")) {
                return classfileBuffer;
            }
            return this.delegate.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        }
    }
}

