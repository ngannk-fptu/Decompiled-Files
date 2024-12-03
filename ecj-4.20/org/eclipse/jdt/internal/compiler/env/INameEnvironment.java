/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;

public interface INameEnvironment {
    public NameEnvironmentAnswer findType(char[][] var1);

    public NameEnvironmentAnswer findType(char[] var1, char[][] var2);

    public boolean isPackage(char[][] var1, char[] var2);

    public void cleanup();
}

