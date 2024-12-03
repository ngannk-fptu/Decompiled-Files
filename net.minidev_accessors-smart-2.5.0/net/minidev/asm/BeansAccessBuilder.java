/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.ClassWriter
 *  org.objectweb.asm.Label
 *  org.objectweb.asm.MethodVisitor
 *  org.objectweb.asm.Type
 */
package net.minidev.asm;

import java.lang.reflect.Method;
import java.util.HashMap;
import net.minidev.asm.ASMUtil;
import net.minidev.asm.Accessor;
import net.minidev.asm.BeansAccess;
import net.minidev.asm.DynamicClassLoader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class BeansAccessBuilder {
    private static String METHOD_ACCESS_NAME = Type.getInternalName(BeansAccess.class);
    final Class<?> type;
    final Accessor[] accs;
    final DynamicClassLoader loader;
    final String className;
    final String accessClassName;
    final String accessClassNameInternal;
    final String classNameInternal;
    final HashMap<Class<?>, Method> convMtds = new HashMap();
    Class<? extends Exception> exceptionClass = NoSuchFieldException.class;

    public BeansAccessBuilder(Class<?> type, Accessor[] accs, DynamicClassLoader loader) {
        this.type = type;
        this.accs = accs;
        this.loader = loader;
        this.className = type.getName();
        this.accessClassName = this.className.startsWith("java.") ? "net.minidev.asm." + this.className + "AccAccess" : this.className.concat("AccAccess");
        this.accessClassNameInternal = this.accessClassName.replace('.', '/');
        this.classNameInternal = this.className.replace('.', '/');
    }

    public void addConversion(Iterable<Class<?>> conv) {
        if (conv == null) {
            return;
        }
        for (Class<?> c : conv) {
            this.addConversion(c);
        }
    }

    public void addConversion(Class<?> conv) {
        if (conv == null) {
            return;
        }
        for (Method mtd : conv.getMethods()) {
            Class<?> rType;
            Class<?>[] param;
            if ((mtd.getModifiers() & 8) == 0 || (param = mtd.getParameterTypes()).length != 1 || !param[0].equals(Object.class) || (rType = mtd.getReturnType()).equals(Void.TYPE)) continue;
            this.convMtds.put(rType, mtd);
        }
    }

    public Class<?> bulid() {
        String sig;
        Type fieldType;
        Accessor acc;
        int n;
        Accessor[] accessorArray;
        int i3;
        Label defaultLabel;
        Label[] labels;
        ClassWriter cw = new ClassWriter(1);
        boolean USE_HASH = this.accs.length > 10;
        int HASH_LIMIT = 14;
        String signature = "Lnet/minidev/asm/BeansAccess<L" + this.classNameInternal + ";>;";
        cw.visit(50, 33, this.accessClassNameInternal, signature, METHOD_ACCESS_NAME, null);
        MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, METHOD_ACCESS_NAME, "<init>", "()V", false);
        mv.visitInsn(177);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        mv = cw.visitMethod(1, "set", "(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
        mv.visitCode();
        if (this.accs.length != 0) {
            if (this.accs.length > HASH_LIMIT) {
                mv.visitVarInsn(21, 2);
                labels = ASMUtil.newLabels(this.accs.length);
                defaultLabel = new Label();
                mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);
                i3 = 0;
                accessorArray = this.accs;
                n = accessorArray.length;
                for (int j = 0; j < n; ++j) {
                    acc = accessorArray[j];
                    mv.visitLabel(labels[i3++]);
                    if (!acc.isWritable()) {
                        mv.visitInsn(177);
                        continue;
                    }
                    this.internalSetFiled(mv, acc);
                }
                mv.visitLabel(defaultLabel);
            } else {
                labels = ASMUtil.newLabels(this.accs.length);
                int i2 = 0;
                Accessor[] i3 = this.accs;
                int n2 = i3.length;
                for (n = 0; n < n2; ++n) {
                    Accessor acc2 = i3[n];
                    this.ifNotEqJmp(mv, 2, i2, labels[i2]);
                    this.internalSetFiled(mv, acc2);
                    mv.visitLabel(labels[i2]);
                    mv.visitFrame(3, 0, null, 0, null);
                    ++i2;
                }
            }
        }
        if (this.exceptionClass != null) {
            this.throwExIntParam(mv, this.exceptionClass);
        } else {
            mv.visitInsn(177);
        }
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        mv = cw.visitMethod(1, "get", "(Ljava/lang/Object;I)Ljava/lang/Object;", null, null);
        mv.visitCode();
        if (this.accs.length == 0) {
            mv.visitFrame(3, 0, null, 0, null);
        } else if (this.accs.length > HASH_LIMIT) {
            mv.visitVarInsn(21, 2);
            labels = ASMUtil.newLabels(this.accs.length);
            defaultLabel = new Label();
            mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);
            i3 = 0;
            accessorArray = this.accs;
            n = accessorArray.length;
            for (int acc2 = 0; acc2 < n; ++acc2) {
                acc = accessorArray[acc2];
                mv.visitLabel(labels[i3++]);
                mv.visitFrame(3, 0, null, 0, null);
                if (!acc.isReadable()) {
                    mv.visitInsn(1);
                    mv.visitInsn(176);
                    continue;
                }
                mv.visitVarInsn(25, 1);
                mv.visitTypeInsn(192, this.classNameInternal);
                Type fieldType2 = Type.getType(acc.getType());
                if (acc.isPublic() || acc.getter == null) {
                    mv.visitFieldInsn(180, this.classNameInternal, acc.getName(), fieldType2.getDescriptor());
                } else {
                    String sig2 = Type.getMethodDescriptor((Method)acc.getter);
                    mv.visitMethodInsn(182, this.classNameInternal, acc.getter.getName(), sig2, false);
                }
                ASMUtil.autoBoxing(mv, fieldType2);
                mv.visitInsn(176);
            }
            mv.visitLabel(defaultLabel);
            mv.visitFrame(3, 0, null, 0, null);
        } else {
            labels = ASMUtil.newLabels(this.accs.length);
            int i4 = 0;
            Accessor[] accessorArray2 = this.accs;
            int n3 = accessorArray2.length;
            for (n = 0; n < n3; ++n) {
                Accessor acc3 = accessorArray2[n];
                this.ifNotEqJmp(mv, 2, i4, labels[i4]);
                mv.visitVarInsn(25, 1);
                mv.visitTypeInsn(192, this.classNameInternal);
                fieldType = Type.getType(acc3.getType());
                if (acc3.isPublic() || acc3.getter == null) {
                    mv.visitFieldInsn(180, this.classNameInternal, acc3.getName(), fieldType.getDescriptor());
                } else {
                    if (acc3.getter == null) {
                        throw new RuntimeException("no Getter for field " + acc3.getName() + " in class " + this.className);
                    }
                    sig = Type.getMethodDescriptor((Method)acc3.getter);
                    mv.visitMethodInsn(182, this.classNameInternal, acc3.getter.getName(), sig, false);
                }
                ASMUtil.autoBoxing(mv, fieldType);
                mv.visitInsn(176);
                mv.visitLabel(labels[i4]);
                mv.visitFrame(3, 0, null, 0, null);
                ++i4;
            }
        }
        if (this.exceptionClass != null) {
            this.throwExIntParam(mv, this.exceptionClass);
        } else {
            mv.visitInsn(1);
            mv.visitInsn(176);
        }
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        if (!USE_HASH) {
            mv = cw.visitMethod(1, "set", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            labels = ASMUtil.newLabels(this.accs.length);
            int i5 = 0;
            Accessor[] accessorArray3 = this.accs;
            int n4 = accessorArray3.length;
            for (n = 0; n < n4; ++n) {
                Accessor acc4 = accessorArray3[n];
                mv.visitVarInsn(25, 2);
                mv.visitLdcInsn((Object)acc4.fieldName);
                mv.visitMethodInsn(182, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
                mv.visitJumpInsn(153, labels[i5]);
                this.internalSetFiled(mv, acc4);
                mv.visitLabel(labels[i5]);
                mv.visitFrame(3, 0, null, 0, null);
                ++i5;
            }
            if (this.exceptionClass != null) {
                this.throwExStrParam(mv, this.exceptionClass);
            } else {
                mv.visitInsn(177);
            }
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        if (!USE_HASH) {
            mv = cw.visitMethod(1, "get", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            labels = ASMUtil.newLabels(this.accs.length);
            int i6 = 0;
            for (Accessor acc5 : this.accs) {
                mv.visitVarInsn(25, 2);
                mv.visitLdcInsn((Object)acc5.fieldName);
                mv.visitMethodInsn(182, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
                mv.visitJumpInsn(153, labels[i6]);
                mv.visitVarInsn(25, 1);
                mv.visitTypeInsn(192, this.classNameInternal);
                fieldType = Type.getType(acc5.getType());
                if (acc5.isPublic() || acc5.getter == null) {
                    mv.visitFieldInsn(180, this.classNameInternal, acc5.getName(), fieldType.getDescriptor());
                } else {
                    sig = Type.getMethodDescriptor((Method)acc5.getter);
                    mv.visitMethodInsn(182, this.classNameInternal, acc5.getter.getName(), sig, false);
                }
                ASMUtil.autoBoxing(mv, fieldType);
                mv.visitInsn(176);
                mv.visitLabel(labels[i6]);
                mv.visitFrame(3, 0, null, 0, null);
                ++i6;
            }
            if (this.exceptionClass != null) {
                this.throwExStrParam(mv, this.exceptionClass);
            } else {
                mv.visitInsn(1);
                mv.visitInsn(176);
            }
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        mv = cw.visitMethod(1, "newInstance", "()Ljava/lang/Object;", null, null);
        mv.visitCode();
        mv.visitTypeInsn(187, this.classNameInternal);
        mv.visitInsn(89);
        mv.visitMethodInsn(183, this.classNameInternal, "<init>", "()V", false);
        mv.visitInsn(176);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
        cw.visitEnd();
        byte[] data = cw.toByteArray();
        return this.loader.defineClass(this.accessClassName, data);
    }

    private void dumpDebug(byte[] data, String destFile) {
    }

    private void internalSetFiled(MethodVisitor mv, Accessor acc) {
        Label isNull;
        mv.visitVarInsn(25, 1);
        mv.visitTypeInsn(192, this.classNameInternal);
        mv.visitVarInsn(25, 3);
        Type fieldType = Type.getType(acc.getType());
        Class<?> type = acc.getType();
        String destClsName = Type.getInternalName(type);
        Method conMtd = this.convMtds.get(type);
        if (conMtd != null) {
            String clsSig = Type.getInternalName(conMtd.getDeclaringClass());
            String mtdName = conMtd.getName();
            String mtdSig = Type.getMethodDescriptor((Method)conMtd);
            mv.visitMethodInsn(184, clsSig, mtdName, mtdSig, false);
        } else if (acc.isEnum()) {
            isNull = new Label();
            mv.visitJumpInsn(198, isNull);
            mv.visitVarInsn(25, 3);
            mv.visitMethodInsn(182, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(184, destClsName, "valueOf", "(Ljava/lang/String;)L" + destClsName + ";", false);
            mv.visitVarInsn(58, 3);
            mv.visitLabel(isNull);
            mv.visitFrame(3, 0, null, 0, null);
            mv.visitVarInsn(25, 1);
            mv.visitTypeInsn(192, this.classNameInternal);
            mv.visitVarInsn(25, 3);
            mv.visitTypeInsn(192, destClsName);
        } else if (type.equals(String.class)) {
            isNull = new Label();
            mv.visitJumpInsn(198, isNull);
            mv.visitVarInsn(25, 3);
            mv.visitMethodInsn(182, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
            mv.visitVarInsn(58, 3);
            mv.visitLabel(isNull);
            mv.visitFrame(3, 0, null, 0, null);
            mv.visitVarInsn(25, 1);
            mv.visitTypeInsn(192, this.classNameInternal);
            mv.visitVarInsn(25, 3);
            mv.visitTypeInsn(192, destClsName);
        } else {
            mv.visitTypeInsn(192, destClsName);
        }
        if (acc.isPublic() || acc.setter == null) {
            mv.visitFieldInsn(181, this.classNameInternal, acc.getName(), fieldType.getDescriptor());
        } else {
            String sig = Type.getMethodDescriptor((Method)acc.setter);
            mv.visitMethodInsn(182, this.classNameInternal, acc.setter.getName(), sig, false);
        }
        mv.visitInsn(177);
    }

    private void throwExIntParam(MethodVisitor mv, Class<?> exCls) {
        String exSig = Type.getInternalName(exCls);
        mv.visitTypeInsn(187, exSig);
        mv.visitInsn(89);
        mv.visitLdcInsn((Object)("mapping " + this.className + " failed to map field:"));
        mv.visitVarInsn(21, 2);
        mv.visitMethodInsn(184, "java/lang/Integer", "toString", "(I)Ljava/lang/String;", false);
        mv.visitMethodInsn(182, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;", false);
        mv.visitMethodInsn(183, exSig, "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(191);
    }

    private void throwExStrParam(MethodVisitor mv, Class<?> exCls) {
        String exSig = Type.getInternalName(exCls);
        mv.visitTypeInsn(187, exSig);
        mv.visitInsn(89);
        mv.visitLdcInsn((Object)("mapping " + this.className + " failed to map field:"));
        mv.visitVarInsn(25, 2);
        mv.visitMethodInsn(182, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;", false);
        mv.visitMethodInsn(183, exSig, "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(191);
    }

    private void ifNotEqJmp(MethodVisitor mv, int param, int value, Label label) {
        mv.visitVarInsn(21, param);
        if (value == 0) {
            mv.visitJumpInsn(154, label);
        } else if (value == 1) {
            mv.visitInsn(4);
            mv.visitJumpInsn(160, label);
        } else if (value == 2) {
            mv.visitInsn(5);
            mv.visitJumpInsn(160, label);
        } else if (value == 3) {
            mv.visitInsn(6);
            mv.visitJumpInsn(160, label);
        } else if (value == 4) {
            mv.visitInsn(7);
            mv.visitJumpInsn(160, label);
        } else if (value == 5) {
            mv.visitInsn(8);
            mv.visitJumpInsn(160, label);
        } else if (value >= 6) {
            mv.visitIntInsn(16, value);
            mv.visitJumpInsn(160, label);
        } else {
            throw new RuntimeException("non supported negative values");
        }
    }
}

