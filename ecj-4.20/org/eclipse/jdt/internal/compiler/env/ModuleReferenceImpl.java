/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.IModule;

public class ModuleReferenceImpl
implements IModule.IModuleReference {
    public char[] name;
    public int modifiers;

    @Override
    public char[] name() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IModule.IModuleReference)) {
            return false;
        }
        IModule.IModuleReference mod = (IModule.IModuleReference)o;
        if (this.modifiers != mod.getModifiers()) {
            return false;
        }
        return CharOperation.equals(this.name, mod.name());
    }

    public int hashCode() {
        return CharOperation.hashCode(this.name);
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }
}

