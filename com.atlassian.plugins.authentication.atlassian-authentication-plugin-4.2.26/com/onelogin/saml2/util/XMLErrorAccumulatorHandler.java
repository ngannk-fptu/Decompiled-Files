/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.onelogin.saml2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLErrorAccumulatorHandler
extends DefaultHandler {
    private final Logger LOG = LoggerFactory.getLogger(XMLErrorAccumulatorHandler.class);
    private final List<Exception> errorXML = new ArrayList<Exception>();

    @Override
    public void error(SAXParseException e) throws SAXException {
        this.errorXML.add(e);
        this.LOG.debug("ERROR parsing xml: " + e.getMessage());
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        this.errorXML.add(e);
        this.LOG.debug("FATALERROR parsing xml: " + e.getMessage());
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        this.errorXML.add(e);
        this.LOG.debug("WARNING parsing xml: " + e.getMessage());
    }

    public List<Exception> getErrorXML() {
        return Collections.unmodifiableList(this.errorXML);
    }

    public boolean hasError() {
        return !this.errorXML.isEmpty();
    }
}

