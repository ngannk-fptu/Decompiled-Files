/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.sax;

import org.jdom2.JDOMException;
import org.xml.sax.XMLReader;

public interface XMLReaderJDOMFactory {
    public XMLReader createXMLReader() throws JDOMException;

    public boolean isValidating();
}

