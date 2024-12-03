/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.ISourceModule;
import org.eclipse.jdt.internal.compiler.env.ISourceType;
import org.eclipse.jdt.internal.compiler.lookup.BinaryModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;

public interface ITypeRequestor {
    public void accept(IBinaryType var1, PackageBinding var2, AccessRestriction var3);

    public void accept(ICompilationUnit var1, AccessRestriction var2);

    public void accept(ISourceType[] var1, PackageBinding var2, AccessRestriction var3);

    default public void accept(IModule module, LookupEnvironment environment) {
        if (module instanceof ISourceModule) {
            ICompilationUnit compilationUnit = ((ISourceModule)module).getCompilationUnit();
            if (compilationUnit != null) {
                this.accept(compilationUnit, null);
            }
        } else {
            BinaryModuleBinding.create(module, environment);
        }
    }
}

