/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.apache.batik.dom.util.DocumentDescriptor;
import org.w3c.dom.Document;
import org.xml.sax.XMLReader;

public interface DocumentFactory {
    public void setValidating(boolean var1);

    public boolean isValidating();

    public Document createDocument(String var1, String var2, String var3) throws IOException;

    public Document createDocument(String var1, String var2, String var3, InputStream var4) throws IOException;

    public Document createDocument(String var1, String var2, String var3, XMLReader var4) throws IOException;

    public Document createDocument(String var1, String var2, String var3, Reader var4) throws IOException;

    public DocumentDescriptor getDocumentDescriptor();
}

