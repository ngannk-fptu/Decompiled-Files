/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.instrument.InstrumentationSavingAgent
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import org.springframework.instrument.InstrumentationSavingAgent;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.SimpleThrowawayClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class InstrumentationLoadTimeWeaver
implements LoadTimeWeaver {
    private static final boolean AGENT_CLASS_PRESENT = ClassUtils.isPresent((String)"org.springframework.instrument.InstrumentationSavingAgent", (ClassLoader)InstrumentationLoadTimeWeaver.class.getClassLoader());
    @Nullable
    private final ClassLoader classLoader;
    @Nullable
    private final Instrumentation instrumentation;
    private final List<ClassFileTransformer> transformers = new ArrayList<ClassFileTransformer>(4);

    public InstrumentationLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }

    public InstrumentationLoadTimeWeaver(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.instrumentation = InstrumentationLoadTimeWeaver.getInstrumentation();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addTransformer(ClassFileTransformer transformer) {
        Assert.notNull((Object)transformer, (String)"Transformer must not be null");
        FilteringClassFileTransformer actualTransformer = new FilteringClassFileTransformer(transformer, this.classLoader);
        List<ClassFileTransformer> list = this.transformers;
        synchronized (list) {
            Assert.state((this.instrumentation != null ? 1 : 0) != 0, (String)"Must start with Java agent to use InstrumentationLoadTimeWeaver. See Spring documentation.");
            this.instrumentation.addTransformer(actualTransformer);
            this.transformers.add(actualTransformer);
        }
    }

    @Override
    public ClassLoader getInstrumentableClassLoader() {
        Assert.state((this.classLoader != null ? 1 : 0) != 0, (String)"No ClassLoader available");
        return this.classLoader;
    }

    @Override
    public ClassLoader getThrowawayClassLoader() {
        return new SimpleThrowawayClassLoader(this.getInstrumentableClassLoader());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeTransformers() {
        List<ClassFileTransformer> list = this.transformers;
        synchronized (list) {
            if (this.instrumentation != null && !this.transformers.isEmpty()) {
                for (int i = this.transformers.size() - 1; i >= 0; --i) {
                    this.instrumentation.removeTransformer(this.transformers.get(i));
                }
                this.transformers.clear();
            }
        }
    }

    public static boolean isInstrumentationAvailable() {
        return InstrumentationLoadTimeWeaver.getInstrumentation() != null;
    }

    @Nullable
    private static Instrumentation getInstrumentation() {
        if (AGENT_CLASS_PRESENT) {
            return InstrumentationAccessor.getInstrumentation();
        }
        return null;
    }

    private static class FilteringClassFileTransformer
    implements ClassFileTransformer {
        private final ClassFileTransformer targetTransformer;
        @Nullable
        private final ClassLoader targetClassLoader;

        public FilteringClassFileTransformer(ClassFileTransformer targetTransformer, @Nullable ClassLoader targetClassLoader) {
            this.targetTransformer = targetTransformer;
            this.targetClassLoader = targetClassLoader;
        }

        @Override
        @Nullable
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            if (this.targetClassLoader != loader) {
                return null;
            }
            return this.targetTransformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        }

        public String toString() {
            return "FilteringClassFileTransformer for: " + this.targetTransformer.toString();
        }
    }

    private static class InstrumentationAccessor {
        private InstrumentationAccessor() {
        }

        public static Instrumentation getInstrumentation() {
            return InstrumentationSavingAgent.getInstrumentation();
        }
    }
}

