/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.adapters;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jdom2.JDOMException;
import org.jdom2.adapters.AbstractDOMAdapter;
import org.w3c.dom.Document;

public class JAXPDOMAdapter
extends AbstractDOMAdapter {
    private static final ThreadLocal<DocumentBuilder> localbuilder = new ThreadLocal();

    public Document createDocument() throws JDOMException {
        DocumentBuilder db = localbuilder.get();
        if (db == null) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                db = dbf.newDocumentBuilder();
                localbuilder.set(db);
            }
            catch (ParserConfigurationException e) {
                throw new JDOMException("Unable to obtain a DOM parser. See cause:", e);
            }
        }
        return db.newDocument();
    }
}

