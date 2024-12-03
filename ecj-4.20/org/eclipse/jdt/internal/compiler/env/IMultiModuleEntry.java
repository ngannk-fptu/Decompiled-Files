/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import java.util.Collection;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.IModulePathEntry;

public interface IMultiModuleEntry
extends IModulePathEntry {
    @Override
    public IModule getModule(char[] var1);

    public Collection<String> getModuleNames(Collection<String> var1);
}

