/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.patterns.BasicToken;
import org.aspectj.weaver.patterns.Pointcut;

public interface IToken
extends IHasPosition {
    public static final IToken EOF = BasicToken.makeOperator("<eof>", 0, 0);

    public String getString();

    public boolean isIdentifier();

    public String getLiteralKind();

    public Pointcut maybeGetParsedPointcut();
}

