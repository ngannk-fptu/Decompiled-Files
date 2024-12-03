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
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SupportedMethodSetProperty
extends AbstractDavProperty<String[]>
implements DeltaVConstants {
    private static Logger log = LoggerFactory.getLogger(SupportedMethodSetProperty.class);
    private final String[] methods;

    public SupportedMethodSetProperty(String[] methods) {
        super(DeltaVConstants.SUPPORTED_METHOD_SET, true);
        this.methods = methods;
    }

    @Override
    public String[] getValue() {
        return this.methods;
    }

    @Override
    public Element toXml(Document document) {
        Element elem = this.getName().toXml(document);
        for (String method : this.methods) {
            Element methodElem = DomUtil.addChildElement(elem, "supported-method", DeltaVConstants.NAMESPACE);
            DomUtil.setAttribute(methodElem, "name", DeltaVConstants.NAMESPACE, method);
        }
        return elem;
    }
}

