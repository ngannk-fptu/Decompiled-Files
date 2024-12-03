/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.jcr.nodetype;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.jcr.nodetype.NodeType;
import org.apache.jackrabbit.commons.webdav.NodeTypeConstants;
import org.apache.jackrabbit.commons.webdav.NodeTypeUtil;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NodeTypeProperty
extends AbstractDavProperty<Set<String>>
implements NodeTypeConstants {
    private final Set<String> nodetypeNames = new HashSet<String>();

    public NodeTypeProperty(DavPropertyName name, NodeType nodeType, boolean isProtected) {
        this(name, new NodeType[]{nodeType}, isProtected);
    }

    public NodeTypeProperty(DavPropertyName name, NodeType[] nodeTypes, boolean isProtected) {
        super(name, isProtected);
        for (NodeType nt : nodeTypes) {
            if (nt == null) continue;
            this.nodetypeNames.add(nt.getName());
        }
    }

    public NodeTypeProperty(DavPropertyName name, String[] nodeTypeNames, boolean isProtected) {
        super(name, isProtected);
        for (String nodeTypeName : nodeTypeNames) {
            if (nodeTypeName == null) continue;
            this.nodetypeNames.add(nodeTypeName);
        }
    }

    public NodeTypeProperty(DavProperty<?> property) {
        super(property.getName(), property.isInvisibleInAllprop());
        if (property instanceof NodeTypeProperty) {
            this.nodetypeNames.addAll(((NodeTypeProperty)property).nodetypeNames);
        } else {
            this.nodetypeNames.addAll(NodeTypeUtil.ntNamesFromXml(property.getValue()));
        }
    }

    public Set<String> getNodeTypeNames() {
        return Collections.unmodifiableSet(this.nodetypeNames);
    }

    @Override
    public Set<String> getValue() {
        return Collections.unmodifiableSet(this.nodetypeNames);
    }

    @Override
    public Element toXml(Document document) {
        Element elem = this.getName().toXml(document);
        for (String name : this.getNodeTypeNames()) {
            elem.appendChild(NodeTypeUtil.ntNameToXml(name, document));
        }
        return elem;
    }
}

