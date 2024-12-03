/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.parser;

import com.atlassian.confluence.importexport.xmlimport.model.CollectionProperty;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.parser.DefaultFragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.FragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.ImportedPropertyParserFactory;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@Deprecated
public class CollectionPropertyParser
extends DefaultFragmentParser<CollectionProperty> {
    private String name;
    private List<ImportedProperty> values = new ArrayList<ImportedProperty>();

    public CollectionPropertyParser() {
        super("collection");
    }

    @Override
    protected void initialise(Attributes attributes) {
        this.name = attributes.getValue("name");
    }

    @Override
    protected FragmentParser<?> newDelegate(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("element".equals(qName)) {
            return ImportedPropertyParserFactory.createParser("element", attributes);
        }
        throw new SAXException("Unexpected element " + qName + " in <collection> where <element> expected");
    }

    @Override
    protected void delegateDone() {
        this.values.add((ImportedProperty)this.getDelegate().build());
        this.setDelegate(null);
    }

    @Override
    public CollectionProperty build() {
        return new CollectionProperty(this.name, this.values);
    }
}

