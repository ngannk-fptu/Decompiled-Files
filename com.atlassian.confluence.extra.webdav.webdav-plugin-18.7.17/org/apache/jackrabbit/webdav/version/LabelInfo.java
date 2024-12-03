/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LabelInfo
implements DeltaVConstants,
XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(LabelInfo.class);
    public static final int TYPE_SET = 0;
    public static final int TYPE_REMOVE = 1;
    public static final int TYPE_ADD = 2;
    public static String[] typeNames = new String[]{"set", "remove", "add"};
    private final int depth;
    private final int type;
    private final String labelName;

    public LabelInfo(String labelName, String type) {
        int i;
        if (labelName == null) {
            throw new IllegalArgumentException("Label name must not be null.");
        }
        boolean validType = false;
        for (i = 0; i < typeNames.length; ++i) {
            if (!typeNames[i].equals(type)) continue;
            validType = true;
            break;
        }
        if (!validType) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        this.type = i;
        this.labelName = labelName;
        this.depth = 0;
    }

    public LabelInfo(String labelName, int type) {
        this(labelName, type, 0);
    }

    public LabelInfo(String labelName, int type, int depth) {
        if (labelName == null) {
            throw new IllegalArgumentException("Label name must not be null.");
        }
        if (type < 0 || type > 2) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        this.labelName = labelName;
        this.type = type;
        this.depth = depth;
    }

    public LabelInfo(Element labelElement, int depth) throws DavException {
        if (!DomUtil.matches(labelElement, "label", DeltaVConstants.NAMESPACE)) {
            log.warn("DAV:label element expected");
            throw new DavException(400);
        }
        String label = null;
        int type = -1;
        for (int i = 0; i < typeNames.length && type == -1; ++i) {
            if (!DomUtil.hasChildElement(labelElement, typeNames[i], NAMESPACE)) continue;
            type = i;
            Element el = DomUtil.getChildElement(labelElement, typeNames[i], NAMESPACE);
            label = DomUtil.getChildText(el, "label-name", NAMESPACE);
        }
        if (label == null) {
            log.warn("DAV:label element must contain at least one set, add or remove element defining a label-name.");
            throw new DavException(400);
        }
        this.labelName = label;
        this.type = type;
        this.depth = depth;
    }

    public LabelInfo(Element labelElement) throws DavException {
        this(labelElement, 0);
    }

    public String getLabelName() {
        return this.labelName;
    }

    public int getType() {
        return this.type;
    }

    public int getDepth() {
        return this.depth;
    }

    @Override
    public Element toXml(Document document) {
        Element label = DomUtil.createElement(document, "label", NAMESPACE);
        Element typeElem = DomUtil.addChildElement(label, typeNames[this.type], NAMESPACE);
        DomUtil.addChildElement(typeElem, "label-name", NAMESPACE, this.labelName);
        return label;
    }
}

