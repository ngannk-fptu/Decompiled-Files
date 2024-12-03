/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.parser;

import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.parser.EnumPropertyParser;
import com.atlassian.confluence.importexport.xmlimport.parser.FragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.PrimitivePropertyParser;
import com.atlassian.confluence.importexport.xmlimport.parser.ReferencePropertyParser;
import org.xml.sax.Attributes;

@Deprecated
public class ImportedPropertyParserFactory {
    public static FragmentParser<? extends ImportedProperty> createParser(String elementName, Attributes attributes) {
        if (attributes.getValue("class") != null) {
            return new ReferencePropertyParser(elementName);
        }
        if (attributes.getValue("enum-class") != null) {
            return new EnumPropertyParser(elementName);
        }
        return new PrimitivePropertyParser(elementName);
    }
}

