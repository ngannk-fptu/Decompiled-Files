/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.spi.ClassTransformer
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.orm.jpa.persistenceunit;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import javax.persistence.spi.ClassTransformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

class ClassFileTransformerAdapter
implements ClassFileTransformer {
    private static final Log logger = LogFactory.getLog(ClassFileTransformerAdapter.class);
    private final ClassTransformer classTransformer;
    private boolean currentlyTransforming = false;

    public ClassFileTransformerAdapter(ClassTransformer classTransformer) {
        Assert.notNull((Object)classTransformer, (String)"ClassTransformer must not be null");
        this.classTransformer = classTransformer;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    @Nullable
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        ClassFileTransformerAdapter classFileTransformerAdapter = this;
        synchronized (classFileTransformerAdapter) {
            if (this.currentlyTransforming) {
                return null;
            }
            this.currentlyTransforming = true;
            try {
                byte[] transformed = this.classTransformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
                if (transformed != null && logger.isDebugEnabled()) {
                    logger.debug((Object)("Transformer of class [" + this.classTransformer.getClass().getName() + "] transformed class [" + className + "]; bytes in=" + classfileBuffer.length + "; bytes out=" + transformed.length));
                }
                byte[] byArray = transformed;
                return byArray;
            }
            catch (ClassCircularityError ex) {
                if (logger.isErrorEnabled()) {
                    logger.error((Object)("Circularity error while weaving class [" + className + "] with transformer of class [" + this.classTransformer.getClass().getName() + "]"), (Throwable)ex);
                }
                throw new IllegalStateException("Failed to weave class [" + className + "]", ex);
            }
            catch (Throwable ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn((Object)("Error weaving class [" + className + "] with transformer of class [" + this.classTransformer.getClass().getName() + "]"), ex);
                }
                throw new IllegalStateException("Could not weave class [" + className + "]", ex);
            }
            finally {
                this.currentlyTransforming = false;
            }
        }
    }

    public String toString() {
        return "Standard ClassFileTransformer wrapping JPA transformer: " + this.classTransformer;
    }
}

