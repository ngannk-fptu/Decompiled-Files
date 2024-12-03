/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.JDOMException
 *  org.jdom2.input.SAXBuilder
 *  org.jdom2.input.sax.XMLReaderJDOMFactory
 *  org.jdom2.input.sax.XMLReaders
 */
package com.rometools.rome.io;

import org.jdom2.JDOMException;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaders;
import org.xml.sax.XMLReader;

public class SAXBuilder
extends org.jdom2.input.SAXBuilder {
    public SAXBuilder(XMLReaderJDOMFactory factory) {
        super(factory);
    }

    @Deprecated
    public SAXBuilder(boolean validate) {
        super((XMLReaderJDOMFactory)(validate ? XMLReaders.DTDVALIDATING : XMLReaders.NONVALIDATING));
    }

    public XMLReader createParser() throws JDOMException {
        return super.createParser();
    }
}

