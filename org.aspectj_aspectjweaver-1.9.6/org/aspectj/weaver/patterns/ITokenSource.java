/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.patterns.IToken;

public interface ITokenSource {
    public IToken next();

    public IToken peek();

    public IToken peek(int var1);

    public int getIndex();

    public void setIndex(int var1);

    public ISourceContext getSourceContext();

    public boolean hasMoreTokens();
}

