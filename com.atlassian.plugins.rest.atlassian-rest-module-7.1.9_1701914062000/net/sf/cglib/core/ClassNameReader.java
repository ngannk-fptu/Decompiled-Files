/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.core;

import java.util.ArrayList;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

public class ClassNameReader {
    private static final EarlyExitException EARLY_EXIT = new EarlyExitException();

    private ClassNameReader() {
    }

    public static String getClassName(ClassReader r) {
        return ClassNameReader.getClassInfo(r)[0];
    }

    public static String[] getClassInfo(ClassReader r) {
        final ArrayList array = new ArrayList();
        try {
            r.accept(new ClassVisitor(262144, null){

                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    array.add(name.replace('/', '.'));
                    if (superName != null) {
                        array.add(superName.replace('/', '.'));
                    }
                    for (int i = 0; i < interfaces.length; ++i) {
                        array.add(interfaces[i].replace('/', '.'));
                    }
                    throw EARLY_EXIT;
                }
            }, 6);
        }
        catch (EarlyExitException earlyExitException) {
            // empty catch block
        }
        return array.toArray(new String[0]);
    }

    private static class EarlyExitException
    extends RuntimeException {
        private EarlyExitException() {
        }
    }
}

