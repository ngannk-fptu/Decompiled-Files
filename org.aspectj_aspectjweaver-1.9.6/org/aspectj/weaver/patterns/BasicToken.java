/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import org.aspectj.weaver.patterns.IToken;
import org.aspectj.weaver.patterns.Pointcut;

public final class BasicToken
implements IToken {
    private String value;
    private boolean isIdentifier;
    private String literalKind;
    private int start;
    private int end;

    public static BasicToken makeOperator(String value, int start, int end) {
        return new BasicToken(value.intern(), false, null, start, end);
    }

    public static BasicToken makeIdentifier(String value, int start, int end) {
        return new BasicToken(value, true, null, start, end);
    }

    public static BasicToken makeLiteral(String value, String kind, int start, int end) {
        return new BasicToken(value, false, kind.intern(), start, end);
    }

    private BasicToken(String value, boolean isIdentifier, String literalKind, int start, int end) {
        this.value = value;
        this.isIdentifier = isIdentifier;
        this.literalKind = literalKind;
        this.start = start;
        this.end = end;
    }

    @Override
    public int getStart() {
        return this.start;
    }

    @Override
    public int getEnd() {
        return this.end;
    }

    public String getFileName() {
        return "unknown";
    }

    @Override
    public String getString() {
        return this.value;
    }

    @Override
    public boolean isIdentifier() {
        return this.isIdentifier;
    }

    @Override
    public Pointcut maybeGetParsedPointcut() {
        return null;
    }

    public String toString() {
        String s = this.isIdentifier ? this.value : "'" + this.value + "'";
        return s + "@" + this.start + ":" + this.end;
    }

    @Override
    public String getLiteralKind() {
        return this.literalKind;
    }
}

