/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

public final class XmlEventUtils {
    private XmlEventUtils() {
    }

    public static List<Attribute> getExtendedAttributes(StartElement element, Attribute newAttribute) {
        ArrayList<Attribute> result = new ArrayList<Attribute>();
        result.add(newAttribute);
        Iterator<Attribute> attributesIterator = element.getAttributes();
        while (attributesIterator.hasNext()) {
            Attribute attribute = attributesIterator.next();
            if (newAttribute.getName().equals(attribute.getName())) continue;
            result.add(attribute);
        }
        return result;
    }

    public static List<Attribute> removeAttribute(StartElement element, QName attributeName) {
        ArrayList<Attribute> result = new ArrayList<Attribute>();
        Iterator<Attribute> attributesIterator = element.getAttributes();
        while (attributesIterator.hasNext()) {
            Attribute attribute = attributesIterator.next();
            if (attributeName.equals(attribute.getName())) continue;
            result.add(attribute);
        }
        return result;
    }
}

