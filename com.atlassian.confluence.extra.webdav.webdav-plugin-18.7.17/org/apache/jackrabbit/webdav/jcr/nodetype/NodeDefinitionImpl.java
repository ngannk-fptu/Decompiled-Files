/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.nodetype;

import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import org.apache.jackrabbit.webdav.jcr.nodetype.ItemDefinitionImpl;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class NodeDefinitionImpl
extends ItemDefinitionImpl
implements NodeDefinition {
    private static Logger log = LoggerFactory.getLogger(NodeDefinitionImpl.class);
    private final NodeType[] requiredPrimaryTypes;
    private final NodeType defaultPrimaryType;
    private final boolean allowsSameNameSiblings;

    private NodeDefinitionImpl(NodeDefinition definition) {
        super(definition);
        this.requiredPrimaryTypes = definition.getRequiredPrimaryTypes();
        this.defaultPrimaryType = definition.getDefaultPrimaryType();
        this.allowsSameNameSiblings = definition.allowsSameNameSiblings();
    }

    public static NodeDefinitionImpl create(NodeDefinition definition) {
        if (definition instanceof NodeDefinitionImpl) {
            return (NodeDefinitionImpl)definition;
        }
        return new NodeDefinitionImpl(definition);
    }

    @Override
    public NodeType[] getRequiredPrimaryTypes() {
        return this.requiredPrimaryTypes;
    }

    @Override
    public NodeType getDefaultPrimaryType() {
        return this.defaultPrimaryType;
    }

    @Override
    public boolean allowsSameNameSiblings() {
        return this.allowsSameNameSiblings;
    }

    @Override
    public String getDefaultPrimaryTypeName() {
        return this.defaultPrimaryType.getName();
    }

    @Override
    public String[] getRequiredPrimaryTypeNames() {
        String[] names = new String[this.requiredPrimaryTypes.length];
        for (int i = 0; i < this.requiredPrimaryTypes.length; ++i) {
            names[i] = this.requiredPrimaryTypes[i].getName();
        }
        return names;
    }

    @Override
    public Element toXml(Document document) {
        Element elem = super.toXml(document);
        elem.setAttribute("sameNameSiblings", Boolean.toString(this.allowsSameNameSiblings()));
        NodeType defaultPrimaryType = this.getDefaultPrimaryType();
        if (defaultPrimaryType != null) {
            elem.setAttribute("defaultPrimaryType", defaultPrimaryType.getName());
        }
        Element reqPrimaryTypes = document.createElement("requiredPrimaryTypes");
        for (NodeType nt : this.getRequiredPrimaryTypes()) {
            Element rptElem = document.createElement("requiredPrimaryType");
            DomUtil.setText(rptElem, nt.getName());
            reqPrimaryTypes.appendChild(rptElem);
        }
        elem.appendChild(reqPrimaryTypes);
        return elem;
    }

    @Override
    String getElementName() {
        return "childNodeDefinition";
    }
}

