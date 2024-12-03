/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import javax.jcr.NamespaceException;
import javax.jcr.nodetype.ItemDefinition;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import org.apache.jackrabbit.spi.QItemDefinition;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.nodetype.AbstractNodeTypeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class ItemDefinitionImpl
implements ItemDefinition {
    private static Logger log = LoggerFactory.getLogger(ItemDefinitionImpl.class);
    protected static final String ANY_NAME = "*";
    protected final NamePathResolver resolver;
    protected final AbstractNodeTypeManager ntMgr;
    protected final QItemDefinition itemDef;

    ItemDefinitionImpl(QItemDefinition itemDef, NamePathResolver resolver) {
        this(itemDef, null, resolver);
    }

    ItemDefinitionImpl(QItemDefinition itemDef, AbstractNodeTypeManager ntMgr, NamePathResolver resolver) {
        this.itemDef = itemDef;
        this.resolver = resolver;
        this.ntMgr = ntMgr;
    }

    @Override
    public NodeType getDeclaringNodeType() {
        if (this.ntMgr == null) {
            return null;
        }
        try {
            return this.ntMgr.getNodeType(this.itemDef.getDeclaringNodeType());
        }
        catch (NoSuchNodeTypeException e) {
            log.error("declaring node type does not exist", (Throwable)e);
            return null;
        }
    }

    @Override
    public String getName() {
        if (this.itemDef.definesResidual()) {
            return ANY_NAME;
        }
        try {
            return this.resolver.getJCRName(this.itemDef.getName());
        }
        catch (NamespaceException e) {
            log.error("encountered unregistered namespace in property name", (Throwable)e);
            return this.itemDef.getName().toString();
        }
    }

    @Override
    public int getOnParentVersion() {
        return this.itemDef.getOnParentVersion();
    }

    @Override
    public boolean isAutoCreated() {
        return this.itemDef.isAutoCreated();
    }

    @Override
    public boolean isMandatory() {
        return this.itemDef.isMandatory();
    }

    @Override
    public boolean isProtected() {
        return this.itemDef.isProtected();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemDefinitionImpl)) {
            return false;
        }
        return this.itemDef.equals(((ItemDefinitionImpl)o).itemDef);
    }

    public int hashCode() {
        return this.itemDef.hashCode();
    }
}

