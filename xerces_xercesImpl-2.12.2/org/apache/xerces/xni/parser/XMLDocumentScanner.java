/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni.parser;

import java.io.IOException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLInputSource;

public interface XMLDocumentScanner
extends XMLDocumentSource {
    public void setInputSource(XMLInputSource var1) throws IOException;

    public boolean scanDocument(boolean var1) throws IOException, XNIException;
}

