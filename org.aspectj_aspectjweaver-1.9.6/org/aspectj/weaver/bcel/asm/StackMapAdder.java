/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel.asm;

import aj.org.objectweb.asm.ClassReader;
import aj.org.objectweb.asm.ClassVisitor;
import aj.org.objectweb.asm.ClassWriter;
import aj.org.objectweb.asm.MethodVisitor;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.asm.AsmDetector;

public class StackMapAdder {
    public static byte[] addStackMaps(World world, byte[] data) {
        try {
            ClassReader cr = new ClassReader(data);
            AspectJConnectClassWriter cw = new AspectJConnectClassWriter(cr, world);
            AspectJClassVisitor cv = new AspectJClassVisitor(cw);
            cr.accept(cv, 0);
            return cw.toByteArray();
        }
        catch (Throwable t) {
            System.err.println("AspectJ Internal Error: unable to add stackmap attributes. " + t.getMessage());
            t.printStackTrace();
            AsmDetector.isAsmAround = false;
            return data;
        }
    }

    private static class AspectJConnectClassWriter
    extends ClassWriter {
        private final World world;

        public AspectJConnectClassWriter(ClassReader cr, World w) {
            super(cr, 2);
            this.world = w;
        }

        @Override
        protected String getCommonSuperClass(String type1, String type2) {
            ResolvedType resolvedType2;
            ResolvedType resolvedType1 = this.world.resolve(UnresolvedType.forName(type1.replace('/', '.')));
            if (resolvedType1.isAssignableFrom(resolvedType2 = this.world.resolve(UnresolvedType.forName(type2.replace('/', '.'))))) {
                return type1;
            }
            if (resolvedType2.isAssignableFrom(resolvedType1)) {
                return type2;
            }
            if (resolvedType1.isInterface() || resolvedType2.isInterface()) {
                return "java/lang/Object";
            }
            do {
                if ((resolvedType1 = resolvedType1.getSuperclass()) == null) {
                    return "java/lang/Object";
                }
                if (!resolvedType1.isParameterizedOrGenericType()) continue;
                resolvedType1 = resolvedType1.getRawType();
            } while (!resolvedType1.isAssignableFrom(resolvedType2));
            return resolvedType1.getRawName().replace('.', '/');
        }
    }

    private static class AspectJClassVisitor
    extends ClassVisitor {
        public AspectJClassVisitor(ClassVisitor classwriter) {
            super(458752, classwriter);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return new AJMethodVisitor(mv);
        }

        static class AJMethodVisitor
        extends MethodVisitor {
            public AJMethodVisitor(MethodVisitor mv) {
                super(458752, mv);
            }
        }
    }
}

