/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.core.compiler.CharOperation;

public class AccessRule {
    public static final int IgnoreIfBetter = 0x2000000;
    public final char[] pattern;
    public final int problemId;

    public AccessRule(char[] pattern, int problemId) {
        this(pattern, problemId, false);
    }

    public AccessRule(char[] pattern, int problemId, boolean keepLooking) {
        this.pattern = pattern;
        this.problemId = keepLooking ? problemId | 0x2000000 : problemId;
    }

    public int hashCode() {
        return this.problemId * 17 + CharOperation.hashCode(this.pattern);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AccessRule)) {
            return false;
        }
        AccessRule other = (AccessRule)obj;
        if (this.problemId != other.problemId) {
            return false;
        }
        return CharOperation.equals(this.pattern, other.pattern);
    }

    public int getProblemId() {
        return this.problemId & 0xFDFFFFFF;
    }

    public boolean ignoreIfBetter() {
        return (this.problemId & 0x2000000) != 0;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("pattern=");
        buffer.append(this.pattern);
        switch (this.getProblemId()) {
            case 0x1000133: {
                buffer.append(" (NON ACCESSIBLE");
                break;
            }
            case 0x1000118: {
                buffer.append(" (DISCOURAGED");
                break;
            }
            default: {
                buffer.append(" (ACCESSIBLE");
            }
        }
        if (this.ignoreIfBetter()) {
            buffer.append(" | IGNORE IF BETTER");
        }
        buffer.append(')');
        return buffer.toString();
    }
}

