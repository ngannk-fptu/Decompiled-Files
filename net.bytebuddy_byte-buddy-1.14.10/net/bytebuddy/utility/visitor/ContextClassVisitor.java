/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility.visitor;

import java.util.List;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.utility.OpenedClassReader;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ContextClassVisitor
extends ClassVisitor {
    private boolean active;

    protected ContextClassVisitor(ClassVisitor classVisitor) {
        super(OpenedClassReader.ASM_API, classVisitor);
    }

    public ContextClassVisitor active() {
        this.active = true;
        return this;
    }

    public abstract List<DynamicType> getAuxiliaryTypes();

    public abstract LoadedTypeInitializer getLoadedTypeInitializer();

    @Override
    public void visitEnd() {
        super.visitEnd();
        if (!(this.active || this.getAuxiliaryTypes().isEmpty() && !this.getLoadedTypeInitializer().isAlive())) {
            throw new IllegalStateException(this + " is not defined 'active' but defines auxiliary types or an alive type initializer");
        }
    }
}

