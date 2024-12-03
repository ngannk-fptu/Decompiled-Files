/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.xni.parser;

import java.io.IOException;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLDocumentSource;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLInputSource;

public interface XMLDocumentScanner
extends XMLDocumentSource {
    public void setInputSource(XMLInputSource var1) throws IOException;

    public boolean scanDocument(boolean var1) throws IOException, XNIException;
}

