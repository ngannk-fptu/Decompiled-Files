/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.internal.enhance;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import org.hibernate.bytecode.enhance.spi.EnhancementContext;
import org.hibernate.bytecode.enhance.spi.EnhancementContextWrapper;
import org.hibernate.bytecode.enhance.spi.Enhancer;
import org.hibernate.bytecode.spi.ClassTransformer;
import org.hibernate.cfg.Environment;

public class EnhancingClassTransformerImpl
implements ClassTransformer {
    private final EnhancementContext enhancementContext;

    public EnhancingClassTransformerImpl(EnhancementContext enhancementContext) {
        this.enhancementContext = enhancementContext;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            Enhancer enhancer = Environment.getBytecodeProvider().getEnhancer(new EnhancementContextWrapper(this.enhancementContext, loader));
            return enhancer.enhance(className, classfileBuffer);
        }
        catch (Exception e) {
            throw new IllegalClassFormatException("Error performing enhancement of " + className){

                @Override
                public synchronized Throwable getCause() {
                    return e;
                }
            };
        }
    }
}

