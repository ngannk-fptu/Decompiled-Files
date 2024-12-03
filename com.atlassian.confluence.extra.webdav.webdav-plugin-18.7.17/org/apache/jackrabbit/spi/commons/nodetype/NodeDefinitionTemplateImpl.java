/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeDefinitionTemplate;
import javax.jcr.nodetype.NodeType;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.nodetype.AbstractItemDefinitionTemplate;
import org.apache.jackrabbit.spi.commons.nodetype.NodeDefinitionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NodeDefinitionTemplateImpl
extends AbstractItemDefinitionTemplate
implements NodeDefinitionTemplate {
    private static final Logger log = LoggerFactory.getLogger(NodeDefinitionTemplateImpl.class);
    private NodeType[] requiredPrimaryTypes;
    private Name[] requiredPrimaryTypeNames;
    private Name defaultPrimaryTypeName;
    private boolean allowSameNameSiblings;

    NodeDefinitionTemplateImpl(NamePathResolver resolver) throws RepositoryException {
        super(resolver);
        this.requiredPrimaryTypes = null;
        this.requiredPrimaryTypeNames = null;
    }

    NodeDefinitionTemplateImpl(NodeDefinition def, NamePathResolver resolver) throws ConstraintViolationException {
        super(def, resolver);
        this.requiredPrimaryTypes = def.getRequiredPrimaryTypes();
        this.allowSameNameSiblings = def.allowsSameNameSiblings();
        if (def instanceof NodeDefinitionImpl) {
            QNodeDefinition qDef = (QNodeDefinition)((NodeDefinitionImpl)def).itemDef;
            this.requiredPrimaryTypeNames = qDef.getRequiredPrimaryTypes();
            this.defaultPrimaryTypeName = qDef.getDefaultPrimaryType();
        } else {
            this.setRequiredPrimaryTypeNames(def.getRequiredPrimaryTypeNames());
            this.setDefaultPrimaryTypeName(def.getDefaultPrimaryTypeName());
        }
    }

    @Override
    public void setRequiredPrimaryTypeNames(String[] requiredPrimaryTypeNames) throws ConstraintViolationException {
        if (requiredPrimaryTypeNames == null) {
            throw new ConstraintViolationException("null isn't a valid array of JCR names.");
        }
        this.requiredPrimaryTypeNames = new Name[requiredPrimaryTypeNames.length];
        for (int i = 0; i < requiredPrimaryTypeNames.length; ++i) {
            try {
                this.requiredPrimaryTypeNames[i] = this.resolver.getQName(requiredPrimaryTypeNames[i]);
                continue;
            }
            catch (RepositoryException e) {
                throw new ConstraintViolationException(e);
            }
        }
    }

    @Override
    public void setDefaultPrimaryTypeName(String defaultPrimaryType) throws ConstraintViolationException {
        try {
            this.defaultPrimaryTypeName = defaultPrimaryType == null ? null : this.resolver.getQName(defaultPrimaryType);
        }
        catch (RepositoryException e) {
            throw new ConstraintViolationException(e);
        }
    }

    @Override
    public void setSameNameSiblings(boolean allowSameNameSiblings) {
        this.allowSameNameSiblings = allowSameNameSiblings;
    }

    @Override
    public NodeType[] getRequiredPrimaryTypes() {
        return this.requiredPrimaryTypes;
    }

    @Override
    public String[] getRequiredPrimaryTypeNames() {
        if (this.requiredPrimaryTypeNames == null) {
            return null;
        }
        String[] rptNames = new String[this.requiredPrimaryTypeNames.length];
        for (int i = 0; i < this.requiredPrimaryTypeNames.length; ++i) {
            try {
                rptNames[i] = this.resolver.getJCRName(this.requiredPrimaryTypeNames[i]);
                continue;
            }
            catch (NamespaceException e) {
                log.error("invalid node type name: " + this.requiredPrimaryTypeNames[i], (Throwable)e);
                rptNames[i] = this.requiredPrimaryTypeNames[i].toString();
            }
        }
        return rptNames;
    }

    @Override
    public NodeType getDefaultPrimaryType() {
        return null;
    }

    @Override
    public String getDefaultPrimaryTypeName() {
        if (this.defaultPrimaryTypeName == null) {
            return null;
        }
        try {
            return this.resolver.getJCRName(this.defaultPrimaryTypeName);
        }
        catch (NamespaceException e) {
            log.error("encountered unregistered namespace in default primary type name", (Throwable)e);
            return this.defaultPrimaryTypeName.toString();
        }
    }

    @Override
    public boolean allowsSameNameSiblings() {
        return this.allowSameNameSiblings;
    }
}

