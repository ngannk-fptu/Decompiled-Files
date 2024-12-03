/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.debug;

public interface DebuggableScript {
    public boolean isTopLevel();

    public boolean isFunction();

    public String getFunctionName();

    public int getParamCount();

    public int getParamAndVarCount();

    public String getParamOrVarName(int var1);

    public String getSourceName();

    public boolean isGeneratedScript();

    public int[] getLineNumbers();

    public int getFunctionCount();

    public DebuggableScript getFunction(int var1);

    public DebuggableScript getParent();
}

