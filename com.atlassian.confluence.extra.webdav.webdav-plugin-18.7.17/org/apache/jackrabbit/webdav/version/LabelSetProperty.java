/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LabelSetProperty
extends AbstractDavProperty<String[]> {
    private static Logger log = LoggerFactory.getLogger(LabelSetProperty.class);
    private final String[] value;

    public LabelSetProperty(String[] labels) {
        super(VersionResource.LABEL_NAME_SET, true);
        this.value = labels;
    }

    @Override
    public String[] getValue() {
        return this.value;
    }

    @Override
    public Element toXml(Document document) {
        Element elem = this.getName().toXml(document);
        for (String str : this.value) {
            DomUtil.addChildElement(elem, "label-name", DeltaVConstants.NAMESPACE, str);
        }
        return elem;
    }
}

