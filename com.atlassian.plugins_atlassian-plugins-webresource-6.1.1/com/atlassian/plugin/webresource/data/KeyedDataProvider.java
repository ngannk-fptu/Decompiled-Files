/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.dom4j.Element
 */
package com.atlassian.plugin.webresource.data;

import com.google.common.base.Preconditions;
import org.dom4j.Element;

class KeyedDataProvider {
    private final String key;
    private final String className;

    KeyedDataProvider(Element e) {
        Preconditions.checkArgument((e.attribute("key") != null ? 1 : 0) != 0, (Object)"key");
        Preconditions.checkArgument((e.attribute("class") != null ? 1 : 0) != 0, (Object)"class");
        this.key = e.attributeValue("key");
        this.className = e.attributeValue("class");
    }

    String getKey() {
        return this.key;
    }

    String getClassName() {
        return this.className;
    }
}

