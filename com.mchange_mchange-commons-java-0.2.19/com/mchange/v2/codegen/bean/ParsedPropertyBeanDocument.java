/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v1.xml.DomParseUtils;
import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.Property;
import com.mchange.v2.codegen.bean.SimpleProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ParsedPropertyBeanDocument {
    static final String[] EMPTY_SA = new String[0];
    String packageName;
    int class_modifiers;
    String className;
    String superclassName;
    String[] interfaceNames = EMPTY_SA;
    String[] generalImports = EMPTY_SA;
    String[] specificImports = EMPTY_SA;
    Property[] properties;

    public ParsedPropertyBeanDocument(Document document) {
        Element element = document.getDocumentElement();
        this.packageName = DomParseUtils.allTextFromUniqueChild(element, "package");
        Element element2 = DomParseUtils.uniqueImmediateChild(element, "modifiers");
        this.class_modifiers = element2 != null ? ParsedPropertyBeanDocument.parseModifiers(element2) : 1;
        Element element3 = DomParseUtils.uniqueChild(element, "imports");
        if (element3 != null) {
            this.generalImports = DomParseUtils.allTextFromImmediateChildElements(element3, "general");
            this.specificImports = DomParseUtils.allTextFromImmediateChildElements(element3, "specific");
        }
        this.className = DomParseUtils.allTextFromUniqueChild(element, "output-class");
        this.superclassName = DomParseUtils.allTextFromUniqueChild(element, "extends");
        Element element4 = DomParseUtils.uniqueChild(element, "implements");
        if (element4 != null) {
            this.interfaceNames = DomParseUtils.allTextFromImmediateChildElements(element4, "interface");
        }
        Element element5 = DomParseUtils.uniqueChild(element, "properties");
        this.properties = this.findProperties(element5);
    }

    public ClassInfo getClassInfo() {
        return new ClassInfo(){

            @Override
            public String getPackageName() {
                return ParsedPropertyBeanDocument.this.packageName;
            }

            @Override
            public int getModifiers() {
                return ParsedPropertyBeanDocument.this.class_modifiers;
            }

            @Override
            public String getClassName() {
                return ParsedPropertyBeanDocument.this.className;
            }

            @Override
            public String getSuperclassName() {
                return ParsedPropertyBeanDocument.this.superclassName;
            }

            @Override
            public String[] getInterfaceNames() {
                return ParsedPropertyBeanDocument.this.interfaceNames;
            }

            @Override
            public String[] getGeneralImports() {
                return ParsedPropertyBeanDocument.this.generalImports;
            }

            @Override
            public String[] getSpecificImports() {
                return ParsedPropertyBeanDocument.this.specificImports;
            }
        };
    }

    public Property[] getProperties() {
        return (Property[])this.properties.clone();
    }

    private Property[] findProperties(Element element) {
        NodeList nodeList = DomParseUtils.immediateChildElementsByTagName(element, "property");
        int n = nodeList.getLength();
        Property[] propertyArray = new Property[n];
        for (int i = 0; i < n; ++i) {
            Element element2 = (Element)nodeList.item(i);
            int n2 = ParsedPropertyBeanDocument.modifiersThroughParentElem(element2, "variable", 2);
            String string = DomParseUtils.allTextFromUniqueChild(element2, "name", true);
            String string2 = DomParseUtils.allTextFromUniqueChild(element2, "type", true);
            String string3 = DomParseUtils.allTextFromUniqueChild(element2, "defensive-copy", true);
            String string4 = DomParseUtils.allTextFromUniqueChild(element2, "default-value", true);
            int n3 = ParsedPropertyBeanDocument.modifiersThroughParentElem(element2, "getter", 1);
            int n4 = ParsedPropertyBeanDocument.modifiersThroughParentElem(element2, "setter", 1);
            Element element3 = DomParseUtils.uniqueChild(element2, "read-only");
            boolean bl = element3 != null;
            Element element4 = DomParseUtils.uniqueChild(element2, "bound");
            boolean bl2 = element4 != null;
            Element element5 = DomParseUtils.uniqueChild(element2, "constrained");
            boolean bl3 = element5 != null;
            propertyArray[i] = new SimpleProperty(n2, string, string2, string3, string4, n3, n4, bl, bl2, bl3);
        }
        return propertyArray;
    }

    private static int modifiersThroughParentElem(Element element, String string, int n) {
        Element element2 = DomParseUtils.uniqueChild(element, string);
        if (element2 != null) {
            Element element3 = DomParseUtils.uniqueChild(element2, "modifiers");
            if (element3 != null) {
                return ParsedPropertyBeanDocument.parseModifiers(element3);
            }
            return n;
        }
        return n;
    }

    private static int parseModifiers(Element element) {
        int n = 0;
        for (String string : DomParseUtils.allTextFromImmediateChildElements(element, "modifier", true)) {
            if ("public".equals(string)) {
                n |= 1;
                continue;
            }
            if ("protected".equals(string)) {
                n |= 4;
                continue;
            }
            if ("private".equals(string)) {
                n |= 2;
                continue;
            }
            if ("final".equals(string)) {
                n |= 0x10;
                continue;
            }
            if ("abstract".equals(string)) {
                n |= 0x400;
                continue;
            }
            if ("static".equals(string)) {
                n |= 8;
                continue;
            }
            if ("synchronized".equals(string)) {
                n |= 0x20;
                continue;
            }
            if ("volatile".equals(string)) {
                n |= 0x40;
                continue;
            }
            if ("transient".equals(string)) {
                n |= 0x80;
                continue;
            }
            if ("strictfp".equals(string)) {
                n |= 0x800;
                continue;
            }
            if ("native".equals(string)) {
                n |= 0x100;
                continue;
            }
            if ("interface".equals(string)) {
                n |= 0x200;
                continue;
            }
            throw new IllegalArgumentException("Bad modifier: " + string);
        }
        return n;
    }
}

