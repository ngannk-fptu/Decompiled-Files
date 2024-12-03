/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.adapters;

import org.jdom2.DocType;
import org.jdom2.JDOMException;
import org.w3c.dom.Document;

public interface DOMAdapter {
    public Document createDocument() throws JDOMException;

    public Document createDocument(DocType var1) throws JDOMException;
}

