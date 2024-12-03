/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;

public class PlainPackageBinding
extends PackageBinding {
    public PlainPackageBinding(char[] topLevelPackageName, LookupEnvironment environment, ModuleBinding enclosingModule) {
        this(new char[][]{topLevelPackageName}, null, environment, enclosingModule);
    }

    public PlainPackageBinding(LookupEnvironment environment) {
        this(CharOperation.NO_CHAR_CHAR, null, environment, environment.module);
    }

    public PlainPackageBinding(char[][] compoundName, PackageBinding parent, LookupEnvironment environment, ModuleBinding enclosingModule) {
        super(compoundName, parent, environment, enclosingModule);
    }

    protected PlainPackageBinding(char[][] compoundName, LookupEnvironment environment) {
        super(compoundName, environment);
    }

    @Override
    public PlainPackageBinding getIncarnation(ModuleBinding moduleBinding) {
        if (this.enclosingModule == moduleBinding) {
            return this;
        }
        return null;
    }
}

