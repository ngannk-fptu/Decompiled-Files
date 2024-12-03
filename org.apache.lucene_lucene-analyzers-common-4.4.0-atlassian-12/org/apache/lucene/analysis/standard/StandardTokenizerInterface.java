/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.standard;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public interface StandardTokenizerInterface {
    public static final int YYEOF = -1;

    public void getText(CharTermAttribute var1);

    public int yychar();

    public void yyreset(Reader var1);

    public int yylength();

    public int getNextToken() throws IOException;
}

