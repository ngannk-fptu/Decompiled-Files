/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ctc.wstx.evt.SimpleStartElement
 *  com.ctc.wstx.io.WstxInputLocation
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.collections.IteratorUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.stax2.ri.evt.AttributeEventImpl
 */
package com.atlassian.confluence.content.render.xhtml.view;

import com.atlassian.confluence.content.render.xhtml.ElementTransformer;
import com.ctc.wstx.evt.SimpleStartElement;
import com.ctc.wstx.io.WstxInputLocation;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.stax2.ri.evt.AttributeEventImpl;

public class TableStylingElementTransformer
implements ElementTransformer {
    public static final Map<String, String> ELEMENT_TO_CLASS = ImmutableMap.of((Object)"table", (Object)"confluenceTable", (Object)"th", (Object)"confluenceTh", (Object)"td", (Object)"confluenceTd");
    public static final Set<QName> HANDLED_EVENTS = ImmutableSet.of((Object)new QName("http://www.w3.org/1999/xhtml", "table"), (Object)new QName("http://www.w3.org/1999/xhtml", "th"), (Object)new QName("http://www.w3.org/1999/xhtml", "td"));

    @Override
    public Set<QName> getHandledElementNames() {
        return HANDLED_EVENTS;
    }

    @Override
    public StartElement transform(StartElement event) {
        List<Attribute> attrs;
        String localName = event.getName().getLocalPart().toLowerCase();
        String classValue = ELEMENT_TO_CLASS.get(localName);
        if (StringUtils.isBlank((CharSequence)classValue)) {
            return event;
        }
        Attribute classAttr = event.getAttributeByName(new QName("class"));
        if (classAttr == null) {
            Location l = event.getLocation();
            WstxInputLocation loc = new WstxInputLocation(null, l.getPublicId(), l.getSystemId(), -1L, l.getLineNumber(), -1);
            classAttr = new AttributeEventImpl((Location)loc, new QName("class"), classValue, true);
            attrs = IteratorUtils.toList(event.getAttributes(), (int)1);
            attrs.add(classAttr);
        } else {
            if (StringUtils.isNotBlank((CharSequence)classAttr.getValue())) {
                Set<String> cssClasses = TableStylingElementTransformer.getCssClasses(classAttr.getValue());
                if (cssClasses.contains(classValue)) {
                    return event;
                }
                cssClasses.add(classValue);
                classValue = StringUtils.join(cssClasses, (char)' ');
            }
            classAttr = new AttributeEventImpl(classAttr.getLocation(), new QName("class"), classValue, true);
            attrs = new ArrayList(1);
            Iterator<Attribute> attrItr = event.getAttributes();
            while (attrItr.hasNext()) {
                Attribute attr = attrItr.next();
                if ("class".equals(attr.getName().getLocalPart())) {
                    attrs.add(classAttr);
                    continue;
                }
                attrs.add(attr);
            }
        }
        SimpleStartElement transformed = SimpleStartElement.construct((Location)event.getLocation(), (QName)event.getName(), attrs.iterator(), event.getNamespaces(), (NamespaceContext)event.getNamespaceContext());
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

