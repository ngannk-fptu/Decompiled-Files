/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.parser;

import com.atlassian.confluence.importexport.xmlimport.model.CompositeId;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.parser.CollectionPropertyParser;
import com.atlassian.confluence.importexport.xmlimport.parser.ComponentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.CompositeIdParser;
import com.atlassian.confluence.importexport.xmlimport.parser.DefaultFragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.FragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.ImportedPropertyParserFactory;
import com.atlassian.confluence.importexport.xmlimport.parser.PrimitiveIdParser;
import java.util.ArrayList;
import java.util.Collection;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@Deprecated
public class ImportedObjectParser
extends DefaultFragmentParser<ImportedObject> {
    private String className;
    private String packageName;
    private Collection<ImportedProperty> properties = new ArrayList<ImportedProperty>();
    private CompositeId compositeId;

    public ImportedObjectParser() {
        super("object");
    }

    @Override
    protected FragmentParser<?> newDelegate(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("collection".equals(qName)) {
            return new CollectionPropertyParser();
        }
        if ("composite-id".equals(qName)) {
            return new CompositeIdParser();
        }
        if ("id".equals(qName)) {
            return new PrimitiveIdParser();
        }
        if ("property".equals(qName)) {
            return ImportedPropertyParserFactory.createParser("property", attributes);
        }
        if ("component".equals(qName)) {
            return new ComponentParser();
        }
        throw new SAXException("Unknown element " + qName + " while looking for <id> <collection> or <property>");
    }

    @Override
    protected void delegateDone() {
        if (this.getDelegate() instanceof CompositeIdParser) {
            this.compositeId = (CompositeId)this.getDelegate().build();
        } else {
            this.properties.add((ImportedProperty)this.getDelegate().build());
        }
        this.setDelegate(null);
    }

    @Override
    protected void initialise(Attributes attributes) {
        this.packageName = attributes.getValue("package");
        this.className = attributes.getValue("class");
    }

    @Override
    public ImportedObject build() {
        return new ImportedObject(this.className, this.packageName, this.properties, this.compositeId);
    }
}

