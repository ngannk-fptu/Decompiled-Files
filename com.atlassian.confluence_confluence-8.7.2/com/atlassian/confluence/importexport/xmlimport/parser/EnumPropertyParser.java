/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.parser;

import com.atlassian.confluence.importexport.xmlimport.model.ContentTypeEnumProperty;
import com.atlassian.confluence.importexport.xmlimport.model.EnumProperty;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.parser.DefaultFragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.FragmentParser;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@Deprecated
public class EnumPropertyParser
extends DefaultFragmentParser<ImportedProperty> {
    private String name;
    private String className;
    private String packageName;
    private StringBuffer value = new StringBuffer();

    public EnumPropertyParser(String elementName) {
        super(elementName);
    }

    @Override
    protected void initialise(Attributes attributes) {
        this.name = attributes.getValue("name");
        this.packageName = attributes.getValue("package");
        this.className = attributes.getValue("enum-class");
    }

    @Override
    protected FragmentParser<?> newDelegate(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        throw new SAXException("Element " + qName + " found in <property> where none expected");
    }

    @Override
    protected void delegateDone() {
        throw new IllegalStateException("This method should never be called");
    }

    @Override
    public void handleCharacters(char[] ch, int start, int length) throws SAXException {
        this.value.append(ch, start, length);
    }

    @Override
    public ImportedProperty build() {
        if (this.isContentTypeEnum()) {
            return new ContentTypeEnumProperty(this.name, this.value.toString());
        }
        return new EnumProperty(this.name, this.packageName, this.className, this.value.toString());
    }

    private boolean isContentTypeEnum() {
        return this.packageName.equals(ContentTypeEnum.class.getPackage().getName()) && this.className.equals(ContentTypeEnum.class.getSimpleName());
    }
}

