/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class WeavingTransformer {
    @Nullable
    private final ClassLoader classLoader;
    private final List<ClassFileTransformer> transformers = new ArrayList<ClassFileTransformer>();

    public WeavingTransformer(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void addTransformer(ClassFileTransformer transformer) {
        Assert.notNull((Object)transformer, "Transformer must not be null");
        this.transformers.add(transformer);
    }

    public byte[] transformIfNecessary(String className, byte[] bytes) {
        String internalName = className.replace(".", "/");
        return this.transformIfNecessary(className, internalName, bytes, null);
    }

    public byte[] transformIfNecessary(String className, String internalName, byte[] bytes, @Nullable ProtectionDomain pd) {
        byte[] result = bytes;
        for (ClassFileTransformer cft : this.transformers) {
            try {
                byte[] transformed = cft.transform(this.classLoader, internalName, null, pd, result);
                if (transformed == null) continue;
                result = transformed;
            }
            catch (IllegalClassFormatException ex) {
                throw new IllegalStateException("Class file transformation failed", ex);
            }
        }
        return result;
    }
}

