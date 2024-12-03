/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

public interface TextSpan {
    public long getStartLine();

    public long getStartColumn();

    public long getFinishLine();

    public long getFinishColumn();
}

