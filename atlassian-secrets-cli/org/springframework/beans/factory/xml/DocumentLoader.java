/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.xml;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

public interface DocumentLoader {
    public Document loadDocument(InputSource var1, EntityResolver var2, ErrorHandler var3, int var4, boolean var5) throws Exception;
}

