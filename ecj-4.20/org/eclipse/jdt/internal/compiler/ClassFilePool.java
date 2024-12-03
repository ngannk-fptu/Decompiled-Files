/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler;

import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;

public class ClassFilePool {
    public static final int POOL_SIZE = 25;
    ClassFile[] classFiles = new ClassFile[25];

    private ClassFilePool() {
    }

    public static ClassFilePool newInstance() {
        return new ClassFilePool();
    }

    public synchronized ClassFile acquire(SourceTypeBinding typeBinding) {
        int i = 0;
        while (i < 25) {
            ClassFile classFile = this.classFiles[i];
            if (classFile == null) {
                ClassFile newClassFile;
                this.classFiles[i] = newClassFile = new ClassFile(typeBinding);
                newClassFile.isShared = true;
                return newClassFile;
            }
            if (!classFile.isShared) {
                classFile.reset(typeBinding, typeBinding.scope.compilerOptions());
                classFile.isShared = true;
                return classFile;
            }
            ++i;
        }
        return new ClassFile(typeBinding);
    }

    public synchronized ClassFile acquireForModule(ModuleBinding moduleBinding, CompilerOptions options) {
        int i = 0;
        while (i < 25) {
            ClassFile classFile = this.classFiles[i];
            if (classFile == null) {
                ClassFile newClassFile;
                this.classFiles[i] = newClassFile = new ClassFile(moduleBinding, options);
                newClassFile.isShared = true;
                return newClassFile;
            }
            if (!classFile.isShared) {
                classFile.reset(null, options);
                classFile.isShared = true;
                return classFile;
            }
            ++i;
        }
        return new ClassFile(moduleBinding, options);
    }

    public synchronized void release(ClassFile classFile) {
        classFile.isShared = false;
    }

    public void reset() {
        Arrays.fill(this.classFiles, null);
    }
}

