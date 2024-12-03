/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.nodetype;

import javax.jcr.nodetype.ItemDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.OnParentVersionAction;
import org.apache.jackrabbit.commons.webdav.NodeTypeConstants;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class ItemDefinitionImpl
implements ItemDefinition,
NodeTypeConstants,
XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(ItemDefinitionImpl.class);
    private final String name;
    private NodeType declaringNodeType;
    private final boolean isAutoCreated;
    private final boolean isMandatory;
    private final boolean isProtected;
    private final int onParentVersion;

    ItemDefinitionImpl(ItemDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("PropDef argument can not be null");
        }
        this.name = definition.getName();
        this.declaringNodeType = definition.getDeclaringNodeType();
        this.isAutoCreated = definition.isAutoCreated();
        this.isMandatory = definition.isMandatory();
        this.isProtected = definition.isProtected();
        this.onParentVersion = definition.getOnParentVersion();
    }

    @Override
    public NodeType getDeclaringNodeType() {
        return this.declaringNodeType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isAutoCreated() {
        return this.isAutoCreated;
    }

    @Override
    public boolean isMandatory() {
        return this.isMandatory;
    }

    @Override
    public int getOnParentVersion() {
        return this.onParentVersion;
    }

    @Override
    public boolean isProtected() {
        return this.isProtected;
    }

    @Override
    public Element toXml(Document document) {
        Element elem = document.createElement(this.getElementName());
        NodeType dnt = this.getDeclaringNodeType();
        if (dnt != null) {
            elem.setAttribute("declaringNodeType", dnt.getName());
        }
        elem.setAttribute("name", this.getName());
        elem.setAttribute("autoCreated", Boolean.toString(this.isAutoCreated()));
        elem.setAttribute("mandatory", Boolean.toString(this.isMandatory()));
        elem.setAttribute("onParentVersion", OnParentVersionAction.nameFromValue(this.getOnParentVersion()));
        elem.setAttribute("protected", Boolean.toString(this.isProtected()));
        return elem;
    }

    abstract String getElementName();
}

