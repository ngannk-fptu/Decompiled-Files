/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.stream.buffer.stax;

import com.sun.xml.stream.buffer.AbstractCreator;
import java.util.ArrayList;
import java.util.List;

abstract class StreamBufferCreator
extends AbstractCreator {
    private boolean checkAttributeValue = false;
    protected List<String> attributeValuePrefixes = new ArrayList<String>();

    StreamBufferCreator() {
    }

    protected void storeQualifiedName(int item, String prefix, String uri, String localName) {
        if (uri != null && uri.length() > 0) {
            if (prefix != null && prefix.length() > 0) {
                item |= 1;
                this.storeStructureString(prefix);
            }
            item |= 2;
            this.storeStructureString(uri);
        }
        this.storeStructureString(localName);
        this.storeStructure(item);
    }

    protected final void storeNamespaceAttribute(String prefix, String uri) {
        int item = 64;
        if (prefix != null && prefix.length() > 0) {
            item |= 1;
            this.storeStructureString(prefix);
        }
        if (uri != null && uri.length() > 0) {
            item |= 2;
            this.storeStructureString(uri);
        }
        this.storeStructure(item);
    }

    protected final void storeAttribute(String prefix, String uri, String localName, String type, String value) {
        this.storeQualifiedName(48, prefix, uri, localName);
        this.storeStructureString(type);
        this.storeContentString(value);
        if (this.checkAttributeValue && value.indexOf("://") == -1) {
            String valuePrefix;
            int firstIndex = value.indexOf(":");
            int lastIndex = value.lastIndexOf(":");
            if (firstIndex != -1 && lastIndex == firstIndex && !this.attributeValuePrefixes.contains(valuePrefix = value.substring(0, firstIndex))) {
                this.attributeValuePrefixes.add(valuePrefix);
            }
        }
    }

    public final List getAttributeValuePrefixes() {
        return this.attributeValuePrefixes;
    }

    protected final void storeProcessingInstruction(String target, String data) {
        this.storeStructure(112);
        this.storeStructureString(target);
        this.storeStructureString(data);
    }

    public final boolean isCheckAttributeValue() {
        return this.checkAttributeValue;
    }

    public final void setCheckAttributeValue(boolean value) {
        this.checkAttributeValue = value;
    }
}

