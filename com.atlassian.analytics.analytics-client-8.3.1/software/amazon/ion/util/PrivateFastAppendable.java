/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.util;

import java.io.IOException;

@Deprecated
public interface PrivateFastAppendable
extends Appendable {
    public void appendAscii(char var1) throws IOException;

    public void appendAscii(CharSequence var1) throws IOException;

    public void appendAscii(CharSequence var1, int var2, int var3) throws IOException;

    public void appendUtf16(char var1) throws IOException;

    public void appendUtf16Surrogate(char var1, char var2) throws IOException;
}

