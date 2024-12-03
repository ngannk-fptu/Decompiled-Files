/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.surround.parser;

import java.io.IOException;

public interface CharStream {
    public char readChar() throws IOException;

    @Deprecated
    public int getColumn();

    @Deprecated
    public int getLine();

    public int getEndColumn();

    public int getEndLine();

    public int getBeginColumn();

    public int getBeginLine();

    public void backup(int var1);

    public char BeginToken() throws IOException;

    public String GetImage();

    public char[] GetSuffix(int var1);

    public void Done();
}

