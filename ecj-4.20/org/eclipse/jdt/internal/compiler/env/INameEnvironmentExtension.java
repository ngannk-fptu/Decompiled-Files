/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;

public interface INameEnvironmentExtension
extends INameEnvironment {
    public NameEnvironmentAnswer findType(char[] var1, char[][] var2, boolean var3, char[] var4);

    default public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName, boolean searchWithSecondaryTypes) {
        return this.findType(typeName, packageName, searchWithSecondaryTypes, null);
    }
}

