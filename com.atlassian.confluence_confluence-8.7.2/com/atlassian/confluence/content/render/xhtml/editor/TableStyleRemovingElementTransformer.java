/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor;

import com.atlassian.confluence.content.render.xhtml.ElementTransformer;
import com.atlassian.confluence.content.render.xhtml.XMLEventFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.view.TableStylingElementTransformer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.StringUtils;

public class TableStyleRemovingElementTransformer
implements ElementTransformer {
    private XMLEventFactory xmlEventFactory;

    public TableStyleRemovingElementTransformer(XMLEventFactoryProvider xmlEventFactoryProvider) {
        this.xmlEventFactory = xmlEventFactoryProvider.getXmlEventFactory();
    }

    @Override
    public Set<QName> getHandledElementNames() {
        return TableStylingElementTransformer.HANDLED_EVENTS;
    }

    @Override
    public StartElement transform(StartElement event) {
        String localName = event.getName().getLocalPart().toLowerCase();
        String classValue = TableStylingElementTransformer.ELEMENT_TO_CLASS.get(localName);
        if (StringUtils.isBlank((CharSequence)classValue)) {
            return event;
        }
        Attribute classAttr = event.getAttributeByName(new QName("class"));
        if (classAttr == null) {
            return event;
        }
        ArrayList<Attribute> attrs = null;
        if (StringUtils.isBlank((CharSequence)classAttr.getValue())) {
            return event;
        }
        Set<String> cssClasses = TableStyleRemovingElementTransformer.getCssClasses(classAttr.getValue());
        boolean remove = cssClasses.remove(classValue);
        if (!remove) {
            return event;
        }
        classValue = StringUtils.join(cssClasses, (char)' ');
        classAttr = this.xmlEventFactory.createAttribute("class", classValue);
        attrs = new ArrayList<Attribute>(1);
        Iterator<Attribute> attrItr = event.getAttributes();
        while (attrItr.hasNext()) {
            Attribute attr = attrItr.next();
            if ("class".equals(attr.getName().getLocalPart())) {
                if (!StringUtils.isNotBlank((CharSequence)classValue)) continue;
                attrs.add(classAttr);
                continue;
            }
            attrs.add(attr);
        }
        StartElement transformed = this.xmlEventFactory.createStartElement(event.getName(), attrs.iterator(), event.getNamespaces());
        return transformed;
    }

    @Override
    public EndElement transform(EndElement element) {
        return element;
    }

    private static Set<String> getCssClasses(String attrValue) {
        String[] cssClassArray = StringUtils.split((String)attrValue);
        if (cssClassArray == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<String>(Arrays.asList(cssClassArray));
    }
}

