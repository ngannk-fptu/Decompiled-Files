/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler;

import java.util.Locale;
import org.eclipse.jdt.core.compiler.CategorizedProblem;

public interface IProblemFactory {
    public CategorizedProblem createProblem(char[] var1, int var2, String[] var3, String[] var4, int var5, int var6, int var7, int var8, int var9);

    public CategorizedProblem createProblem(char[] var1, int var2, String[] var3, int var4, String[] var5, int var6, int var7, int var8, int var9, int var10);

    public Locale getLocale();

    public String getLocalizedMessage(int var1, String[] var2);

    public String getLocalizedMessage(int var1, int var2, String[] var3);
}

