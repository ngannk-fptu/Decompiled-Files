/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.JDOMException
 *  org.jdom.input.SAXBuilder
 */
package com.sun.syndication.io;

import org.jdom.JDOMException;
import org.xml.sax.XMLReader;

public class SAXBuilder
extends org.jdom.input.SAXBuilder {
    public SAXBuilder(boolean _validate) {
        super(_validate);
    }

    public XMLReader createParser() throws JDOMException {
        return super.createParser();
    }
}

