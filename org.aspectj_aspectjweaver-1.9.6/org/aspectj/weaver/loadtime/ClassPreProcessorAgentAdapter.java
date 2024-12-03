/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.loadtime;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import org.aspectj.weaver.loadtime.Aj;
import org.aspectj.weaver.loadtime.ClassPreProcessor;

public class ClassPreProcessorAgentAdapter
implements ClassFileTransformer {
    private static ClassPreProcessor classPreProcessor;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
        if (classBeingRedefined != null) {
            System.err.println("INFO: (Enh120375):  AspectJ attempting reweave of '" + className + "'");
            classPreProcessor.prepareForRedefinition(loader, className);
        }
        return classPreProcessor.preProcess(className, bytes, loader, protectionDomain);
    }

    static {
        try {
            classPreProcessor = new Aj();
            classPreProcessor.initialize();
        }
        catch (Exception e) {
            throw new ExceptionInInitializerError("could not initialize JSR163 preprocessor due to: " + e.toString());
        }
    }
}

