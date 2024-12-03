/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.importexport.xmlimport.parser;

import com.atlassian.confluence.importexport.xmlimport.ImportProcessor;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.parser.FragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.ImportedObjectParser;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@Deprecated
public class BackupParser
extends DefaultHandler {
    private final ImportProcessor callback;
    private FragmentParser<ImportedObject> delegate;

    public BackupParser(ImportProcessor callback) {
        this.callback = callback;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (this.delegate == null) {
            if ("object".equals(qName)) {
                this.delegate = new ImportedObjectParser();
            } else if (!"hibernate-generic".equals(qName)) {
                throw new SAXException("Unexpected element: " + localName + " when looking for <object>");
            }
        }
        if (this.delegate != null) {
            this.delegate.startElement(uri, localName, qName, attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (this.delegate != null) {
            this.delegate.endElement(uri, localName, qName);
            if (this.delegate.isDone()) {
                try {
                    this.callback.processObject(this.delegate.build());
                }
                catch (Exception e) {
                    throw new SAXException("Error while importing backup: " + e.getMessage(), e);
                }
                this.delegate = null;
            }
        } else if (!"hibernate-generic".equals(qName)) {
            throw new SAXException("Unexpected end of element " + qName + " when looking for <object>");
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.delegate != null) {
            this.delegate.characters(ch, start, length);
        } else {
            String chars = new String(ch, start, length);
            if (!StringUtils.isBlank((CharSequence)chars)) {
                throw new SAXException("Non-whitespace found when looking for <object>: " + chars);
            }
        }
    }
}

