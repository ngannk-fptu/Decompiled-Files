/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.parser;

import com.atlassian.confluence.importexport.xmlimport.model.ComponentProperty;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import com.atlassian.confluence.importexport.xmlimport.parser.DefaultFragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.FragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.PrimitivePropertyParser;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@Deprecated
public class ComponentParser
extends DefaultFragmentParser<ComponentProperty> {
    private String name;
    private List<PrimitiveProperty> properties = new ArrayList<PrimitiveProperty>();

    public ComponentParser() {
        super("component");
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
        this.name = attributes.getValue("name");
    }

    @Override
    public ComponentProperty build() {
        return new ComponentProperty(this.name, this.properties);
    }
}

