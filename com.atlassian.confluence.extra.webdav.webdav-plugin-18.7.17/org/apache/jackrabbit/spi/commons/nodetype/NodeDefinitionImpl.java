/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import javax.jcr.NamespaceException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QItemDefinition;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.nodetype.AbstractNodeTypeManager;
import org.apache.jackrabbit.spi.commons.nodetype.ItemDefinitionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeDefinitionImpl
extends ItemDefinitionImpl
implements NodeDefinition {
    private static final Logger log = LoggerFactory.getLogger(NodeDefinitionImpl.class);

    public NodeDefinitionImpl(QItemDefinition itemDef, NamePathResolver resolver) {
        super(itemDef, resolver);
    }

    public NodeDefinitionImpl(QItemDefinition itemDef, AbstractNodeTypeManager ntMgr, NamePathResolver resolver) {
        super(itemDef, ntMgr, resolver);
    }

    public QNodeDefinition unwrap() {
        return (QNodeDefinition)this.itemDef;
    }

    @Override
    public boolean allowsSameNameSiblings() {
        return ((QNodeDefinition)this.itemDef).allowsSameNameSiblings();
    }

    @Override
    public String getDefaultPrimaryTypeName() {
        Name ntName = ((QNodeDefinition)this.itemDef).getDefaultPrimaryType();
        if (ntName == null) {
            return null;
        }
        try {
            return this.resolver.getJCRName(ntName);
        }
        catch (NamespaceException e) {
            log.error("invalid default node type " + ntName, (Throwable)e);
            return null;
        }
    }

    @Override
    public NodeType getDefaultPrimaryType() {
        if (this.ntMgr == null) {
            return null;
        }
        Name ntName = ((QNodeDefinition)this.itemDef).getDefaultPrimaryType();
        if (ntName == null) {
            return null;
        }
        try {
            return this.ntMgr.getNodeType(ntName);
        }
        catch (NoSuchNodeTypeException e) {
            log.error("invalid default node type " + ntName, (Throwable)e);
            return null;
        }
    }

    @Override
    public NodeType[] getRequiredPrimaryTypes() {
        if (this.ntMgr == null) {
            return null;
        }
        Name[] ntNames = ((QNodeDefinition)this.itemDef).getRequiredPrimaryTypes();
        try {
            if (ntNames == null || ntNames.length == 0) {
                return new NodeType[]{this.ntMgr.getNodeType(NameConstants.NT_BASE)};
            }
            NodeType[] nodeTypes = new NodeType[ntNames.length];
            for (int i = 0; i < ntNames.length; ++i) {
                nodeTypes[i] = this.ntMgr.getNodeType(ntNames[i]);
            }
            return nodeTypes;
        }
        catch (NoSuchNodeTypeException e) {
            log.error("required node type does not exist", (Throwable)e);
            return new NodeType[0];
        }
    }

    @Override
    public String[] getRequiredPrimaryTypeNames() {
        Name[] ntNames = ((QNodeDefinition)this.itemDef).getRequiredPrimaryTypes();
        try {
            if (ntNames == null || ntNames.length == 0) {
                return new String[]{this.resolver.getJCRName(NameConstants.NT_BASE)};
            }
            String[] jcrNames = new String[ntNames.length];
            for (int i = 0; i < ntNames.length; ++i) {
                jcrNames[i] = this.resolver.getJCRName(ntNames[i]);
            }
            return jcrNames;
        }
        catch (NamespaceException e) {
            log.error("required node type does not exist", (Throwable)e);
            return new String[0];
        }
    }
}

