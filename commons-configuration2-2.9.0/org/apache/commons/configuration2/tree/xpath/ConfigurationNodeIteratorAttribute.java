/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.jxpath.ri.QName
 *  org.apache.commons.jxpath.ri.model.NodePointer
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.tree.xpath;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.apache.commons.configuration2.tree.xpath.ConfigurationAttributePointer;
import org.apache.commons.configuration2.tree.xpath.ConfigurationNodeIteratorBase;
import org.apache.commons.configuration2.tree.xpath.ConfigurationNodePointer;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.lang3.StringUtils;

class ConfigurationNodeIteratorAttribute<T>
extends ConfigurationNodeIteratorBase<T> {
    private static final String WILDCARD = "*";
    private final ConfigurationNodePointer<T> parentPointer;
    private final List<String> attributeNames;

    public ConfigurationNodeIteratorAttribute(ConfigurationNodePointer<T> parent, QName name) {
        super(parent, false);
        this.parentPointer = parent;
        this.attributeNames = this.createAttributeDataList(parent, name);
    }

    @Override
    protected NodePointer createNodePointer(int position) {
        return new ConfigurationAttributePointer<T>(this.parentPointer, this.attributeNames.get(position));
    }

    @Override
    protected int size() {
        return this.attributeNames.size();
    }

    private List<String> createAttributeDataList(ConfigurationNodePointer<T> parent, QName name) {
        ArrayList<String> result = new ArrayList<String>();
        if (!WILDCARD.equals(name.getName())) {
            this.addAttributeData(parent, result, ConfigurationNodeIteratorAttribute.qualifiedName(name));
        } else {
            LinkedHashSet<String> names = new LinkedHashSet<String>(parent.getNodeHandler().getAttributes(parent.getConfigurationNode()));
            String prefix = name.getPrefix() != null ? ConfigurationNodeIteratorAttribute.prefixName(name.getPrefix(), null) : null;
            names.forEach(n -> {
                if (prefix == null || StringUtils.startsWith((CharSequence)n, (CharSequence)prefix)) {
                    this.addAttributeData(parent, (List<String>)result, (String)n);
                }
            });
        }
        return result;
    }

    private void addAttributeData(ConfigurationNodePointer<T> parent, List<String> result, String name) {
        if (parent.getNodeHandler().getAttributeValue(parent.getConfigurationNode(), name) != null) {
            result.add(name);
        }
    }
}

