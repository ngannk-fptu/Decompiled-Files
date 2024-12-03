/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.standard;

import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import java.io.IOException;
import java.io.Reader;

public interface StandardTokenizerInterface {
    public static final int YYEOF = -1;

    public void getText(CharTermAttribute var1);

    public int yychar();

    public void yyreset(Reader var1);

    public int yylength();

    public int getNextToken() throws IOException;
}

