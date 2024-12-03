/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.Member;

public interface IEclipseSourceContext
extends ISourceContext {
    public void removeUnnecessaryProblems(Member var1, int var2);
}

