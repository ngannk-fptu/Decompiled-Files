/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.MethodVisitor;
import org.codehaus.groovy.classgen.asm.MethodCaller;

public class MethodCallerMultiAdapter {
    private MethodCaller[] methods;
    boolean skipSpreadSafeAndSafe;
    public static final int MAX_ARGS = 0;

    public static MethodCallerMultiAdapter newStatic(Class theClass, String baseName, boolean createNArgs, boolean skipSpreadSafeAndSafe) {
        MethodCallerMultiAdapter mcma = new MethodCallerMultiAdapter();
        mcma.skipSpreadSafeAndSafe = skipSpreadSafeAndSafe;
        if (createNArgs) {
            int numberOfBaseMethods = mcma.numberOfBaseMethods();
            mcma.methods = new MethodCaller[2 * numberOfBaseMethods];
            for (int i = 0; i <= 0; ++i) {
                mcma.methods[i * numberOfBaseMethods] = MethodCaller.newStatic(theClass, baseName + i);
                if (skipSpreadSafeAndSafe) continue;
                mcma.methods[i * numberOfBaseMethods + 1] = MethodCaller.newStatic(theClass, baseName + i + "Safe");
                mcma.methods[i * numberOfBaseMethods + 2] = MethodCaller.newStatic(theClass, baseName + i + "SpreadSafe");
            }
            mcma.methods[1 * numberOfBaseMethods] = MethodCaller.newStatic(theClass, baseName + "N");
            if (!skipSpreadSafeAndSafe) {
                mcma.methods[1 * numberOfBaseMethods + 1] = MethodCaller.newStatic(theClass, baseName + "NSafe");
                mcma.methods[1 * numberOfBaseMethods + 2] = MethodCaller.newStatic(theClass, baseName + "NSpreadSafe");
            }
        } else {
            mcma.methods = !skipSpreadSafeAndSafe ? new MethodCaller[]{MethodCaller.newStatic(theClass, baseName), MethodCaller.newStatic(theClass, baseName + "Safe"), MethodCaller.newStatic(theClass, baseName + "SpreadSafe")} : new MethodCaller[]{MethodCaller.newStatic(theClass, baseName)};
        }
        return mcma;
    }

    public void call(MethodVisitor methodVisitor, int numberOfArguments, boolean safe, boolean spreadSafe) {
        int offset = 0;
        if (safe && !this.skipSpreadSafeAndSafe) {
            offset = 1;
        }
        if (spreadSafe && !this.skipSpreadSafeAndSafe) {
            offset = 2;
        }
        offset = numberOfArguments > 0 || numberOfArguments < 0 ? (offset += 1 * this.numberOfBaseMethods()) : (offset += numberOfArguments * this.numberOfBaseMethods());
        this.methods[offset].call(methodVisitor);
    }

    private int numberOfBaseMethods() {
        if (this.skipSpreadSafeAndSafe) {
            return 1;
        }
        return 3;
    }
}

