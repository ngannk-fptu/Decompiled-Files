/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.parser;

import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveId;
import com.atlassian.confluence.importexport.xmlimport.model.ReferenceProperty;
import com.atlassian.confluence.importexport.xmlimport.parser.DefaultFragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.FragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.PrimitiveIdParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@Deprecated
public class ReferencePropertyParser
extends DefaultFragmentParser<ReferenceProperty> {
    private String name;
    private String className;
    private PrimitiveId id;
    private String packageName;

    public ReferencePropertyParser(String elementName) {
        super(elementName);
    }

    @Override
    protected void initialise(Attributes attributes) {
        this.name = attributes.getValue("name");
        this.packageName = attributes.getValue("package");
        this.className = attributes.getValue("class");
    }

    @Override
    protected FragmentParser<?> newDelegate(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("id".equals(qName)) {
            return new PrimitiveIdParser();
        }
        throw new SAXException("Element " + qName + " found in <property> where <id> expected");
    }

    @Override
    protected void delegateDone() {
        this.id = (PrimitiveId)this.getDelegate().build();
        this.setDelegate(null);
    }

    @Override
    public ReferenceProperty build() {
        return new ReferenceProperty(this.name, this.packageName, this.className, this.id);
    }
}

