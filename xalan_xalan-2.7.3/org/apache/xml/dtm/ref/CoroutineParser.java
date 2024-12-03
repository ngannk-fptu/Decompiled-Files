/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.ref.CoroutineManager;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public interface CoroutineParser {
    public int getParserCoroutineID();

    public CoroutineManager getCoroutineManager();

    public void setContentHandler(ContentHandler var1);

    public void setLexHandler(LexicalHandler var1);

    public Object doParse(InputSource var1, int var2);

    public Object doMore(boolean var1, int var2);

    public void doTerminate(int var1);

    public void init(CoroutineManager var1, int var2, XMLReader var3);
}

