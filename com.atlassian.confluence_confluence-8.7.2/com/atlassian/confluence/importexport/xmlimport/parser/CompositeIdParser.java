/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.parser;

import com.atlassian.confluence.importexport.xmlimport.model.CompositeId;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import com.atlassian.confluence.importexport.xmlimport.parser.DefaultFragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.FragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.PrimitivePropertyParser;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@Deprecated
public class CompositeIdParser
extends DefaultFragmentParser<CompositeId> {
    private List<PrimitiveProperty> properties = new ArrayList<PrimitiveProperty>();

    public CompositeIdParser() {
        super("composite-id");
    }

    @Override
    protected void delegateDone() {
        this.properties.add((PrimitiveProperty)this.getDelegate().build());
        this.setDelegate(null);
    }

    @Override
    protected FragmentParser<?> newDelegate(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("property".equals(qName)) {
            return new PrimitivePropertyParser("property");
        }
        throw new SAXException("Unexpected element " + qName + " found while looking for <property>");
    }

    @Override
    protected void initialise(Attributes attributes) {
    }

    @Override
    public CompositeId build() {
        return new CompositeId(this.properties);
    }
}

