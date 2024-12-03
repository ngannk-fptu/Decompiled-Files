/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  com.google.common.base.Preconditions
 *  org.dom4j.Element
 *  org.dom4j.Node
 */
package com.atlassian.plugin.osgi.factory.transform.model;

import com.atlassian.plugin.util.validation.ValidationPattern;
import com.google.common.base.Preconditions;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.dom4j.Element;
import org.dom4j.Node;

public class ComponentImport {
    private static final String INTERFACE = "interface";
    private final String key;
    private final Set<String> interfaces;
    private final String filter;
    private final Element source;

    public ComponentImport(Element element) {
        Preconditions.checkNotNull((Object)element);
        ValidationPattern.createPattern().rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@key").withError("The key is required"), ValidationPattern.test((String)"(@interface and string-length(@interface) > 0) or (interface and string-length(interface[1]) > 0)").withError("The interface must be specified either via the 'interface'attribute or child 'interface' elements")}).evaluate((Node)element);
        this.source = element;
        this.key = element.attributeValue("key").trim();
        String filter = element.attributeValue("filter");
        this.filter = filter != null ? filter.trim() : null;
        this.interfaces = new LinkedHashSet<String>();
        if (element.attribute(INTERFACE) != null) {
            this.interfaces.add(element.attributeValue(INTERFACE).trim());
        } else {
            List compInterfaces = element.elements(INTERFACE);
            for (Element inf : compInterfaces) {
                this.interfaces.add(inf.getTextTrim());
            }
        }
    }

    public String getKey() {
        return this.key;
    }

    public Set<String> getInterfaces() {
        return this.interfaces;
    }

    public Element getSource() {
        return this.source;
    }

    public String getFilter() {
        return this.filter;
    }
}

