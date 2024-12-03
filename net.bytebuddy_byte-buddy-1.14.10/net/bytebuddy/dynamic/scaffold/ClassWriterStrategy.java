/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.scaffold;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.pool.TypePool;

public interface ClassWriterStrategy {
    public ClassWriter resolve(int var1, TypePool var2);

    public ClassWriter resolve(int var1, TypePool var2, ClassReader var3);

    public static class FrameComputingClassWriter
    extends ClassWriter {
        private final TypePool typePool;

        public FrameComputingClassWriter(int flags, TypePool typePool) {
            super(flags);
            this.typePool = typePool;
        }

        public FrameComputingClassWriter(ClassReader classReader, int flags, TypePool typePool) {
            super(classReader, flags);
            this.typePool = typePool;
        }

        protected String getCommonSuperClass(String leftTypeName, String rightTypeName) {
            TypeDescription.Generic superClass;
            TypeDescription rightType;
            TypeDescription leftType = this.typePool.describe(leftTypeName.replace('/', '.')).resolve();
            if (leftType.isAssignableFrom(rightType = this.typePool.describe(rightTypeName.replace('/', '.')).resolve())) {
                return leftType.getInternalName();
            }
            if (leftType.isAssignableTo(rightType)) {
                return rightType.getInternalName();
            }
            if (leftType.isInterface() || rightType.isInterface()) {
                return TypeDescription.ForLoadedType.of(Object.class).getInternalName();
            }
            do {
                if ((superClass = leftType.getSuperClass()) != null) continue;
                return TypeDescription.ForLoadedType.of(Object.class).getInternalName();
            } while (!(leftType = superClass.asErasure()).isAssignableFrom(rightType));
            return leftType.getInternalName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Default implements ClassWriterStrategy
    {
        CONSTANT_POOL_RETAINING{

            public ClassWriter resolve(int flags, TypePool typePool, ClassReader classReader) {
                return new FrameComputingClassWriter(classReader, flags, typePool);
            }
        }
        ,
        CONSTANT_POOL_DISCARDING{

            public ClassWriter resolve(int flags, TypePool typePool, ClassReader classReader) {
                return this.resolve(flags, typePool);
            }
        };


        @Override
        public ClassWriter resolve(int flags, TypePool typePool) {
            return new FrameComputingClassWriter(flags, typePool);
        }
    }
}

