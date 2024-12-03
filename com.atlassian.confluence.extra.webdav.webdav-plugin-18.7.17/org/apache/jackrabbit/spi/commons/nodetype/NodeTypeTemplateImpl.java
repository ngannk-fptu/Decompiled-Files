/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import java.util.LinkedList;
import java.util.List;
import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeDefinitionTemplate;
import javax.jcr.nodetype.NodeTypeDefinition;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.nodetype.PropertyDefinitionTemplate;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.nodetype.NodeDefinitionTemplateImpl;
import org.apache.jackrabbit.spi.commons.nodetype.NodeTypeDefinitionImpl;
import org.apache.jackrabbit.spi.commons.nodetype.PropertyDefinitionTemplateImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeTypeTemplateImpl
implements NodeTypeTemplate {
    private static final Logger log = LoggerFactory.getLogger(NodeTypeTemplateImpl.class);
    private Name name;
    private Name[] superTypeNames;
    private Name primaryItemName;
    private boolean abstractStatus;
    private boolean queryable;
    private boolean mixin;
    private boolean orderableChildNodes;
    private List<NodeDefinitionTemplate> nodeDefinitionTemplates;
    private List<PropertyDefinitionTemplate> propertyDefinitionTemplates;
    private final NamePathResolver resolver;

    NodeTypeTemplateImpl(NamePathResolver resolver) {
        this.queryable = true;
        this.superTypeNames = Name.EMPTY_ARRAY;
        this.resolver = resolver;
    }

    NodeTypeTemplateImpl(NodeTypeDefinition def, NamePathResolver resolver) throws RepositoryException {
        PropertyDefinition[] propDefs;
        this.resolver = resolver;
        if (def instanceof NodeTypeDefinitionImpl) {
            QNodeTypeDefinition qDef = ((NodeTypeDefinitionImpl)def).ntd;
            this.name = qDef.getName();
            this.superTypeNames = qDef.getSupertypes();
            this.primaryItemName = qDef.getPrimaryItemName();
        } else {
            this.setName(def.getName());
            this.setDeclaredSuperTypeNames(def.getDeclaredSupertypeNames());
            this.setPrimaryItemName(def.getPrimaryItemName());
        }
        this.abstractStatus = def.isAbstract();
        this.mixin = def.isMixin();
        this.queryable = def.isQueryable();
        this.orderableChildNodes = def.hasOrderableChildNodes();
        NodeDefinition[] nodeDefs = def.getDeclaredChildNodeDefinitions();
        if (nodeDefs != null) {
            List list = this.getNodeDefinitionTemplates();
            for (NodeDefinition nodeDef : nodeDefs) {
                list.add(new NodeDefinitionTemplateImpl(nodeDef, resolver));
            }
        }
        if ((propDefs = def.getDeclaredPropertyDefinitions()) != null) {
            List list = this.getPropertyDefinitionTemplates();
            for (PropertyDefinition propDef : propDefs) {
                list.add(new PropertyDefinitionTemplateImpl(propDef, resolver));
            }
        }
    }

    @Override
    public void setName(String name) throws ConstraintViolationException {
        try {
            this.name = this.resolver.getQName(name);
        }
        catch (RepositoryException e) {
            throw new ConstraintViolationException(e);
        }
    }

    @Override
    public void setDeclaredSuperTypeNames(String[] names) throws ConstraintViolationException {
        if (names == null) {
            throw new ConstraintViolationException("null isn't a valid array of JCR names.");
        }
        this.superTypeNames = new Name[names.length];
        for (int i = 0; i < names.length; ++i) {
            try {
                this.superTypeNames[i] = this.resolver.getQName(names[i]);
                continue;
            }
            catch (RepositoryException e) {
                throw new ConstraintViolationException(e);
            }
        }
    }

    @Override
    public void setAbstract(boolean abstractStatus) {
        this.abstractStatus = abstractStatus;
    }

    @Override
    public void setMixin(boolean mixin) {
        this.mixin = mixin;
    }

    @Override
    public void setOrderableChildNodes(boolean orderable) {
        this.orderableChildNodes = orderable;
    }

    @Override
    public void setPrimaryItemName(String name) throws ConstraintViolationException {
        if (name == null) {
            this.primaryItemName = null;
        } else {
            try {
                this.primaryItemName = this.resolver.getQName(name);
            }
            catch (RepositoryException e) {
                throw new ConstraintViolationException(e);
            }
        }
    }

    @Override
    public List getPropertyDefinitionTemplates() {
        if (this.propertyDefinitionTemplates == null) {
            this.propertyDefinitionTemplates = new LinkedList<PropertyDefinitionTemplate>();
        }
        return this.propertyDefinitionTemplates;
    }

    @Override
    public List getNodeDefinitionTemplates() {
        if (this.nodeDefinitionTemplates == null) {
            this.nodeDefinitionTemplates = new LinkedList<NodeDefinitionTemplate>();
        }
        return this.nodeDefinitionTemplates;
    }

    @Override
    public void setQueryable(boolean queryable) {
        this.queryable = queryable;
    }

    @Override
    public String getName() {
        if (this.name == null) {
            return null;
        }
        try {
            return this.resolver.getJCRName(this.name);
        }
        catch (NamespaceException e) {
            log.error("encountered unregistered namespace in node type name", (Throwable)e);
            return this.name.toString();
        }
    }

    @Override
    public String[] getDeclaredSupertypeNames() {
        String[] names = new String[this.superTypeNames.length];
        for (int i = 0; i < this.superTypeNames.length; ++i) {
            try {
                names[i] = this.resolver.getJCRName(this.superTypeNames[i]);
                continue;
            }
            catch (NamespaceException e) {
                log.error("encountered unregistered namespace in super type name", (Throwable)e);
                names[i] = this.superTypeNames[i].toString();
            }
        }
        return names;
    }

    @Override
    public boolean isAbstract() {
        return this.abstractStatus;
    }

    @Override
    public boolean isMixin() {
        return this.mixin;
    }

    @Override
    public boolean isQueryable() {
        return this.queryable;
    }

    @Override
    public boolean hasOrderableChildNodes() {
        return this.orderableChildNodes;
    }

    @Override
    public String getPrimaryItemName() {
        if (this.primaryItemName == null) {
            return null;
        }
        try {
            return this.resolver.getJCRName(this.primaryItemName);
        }
        catch (NamespaceException e) {
            log.error("encountered unregistered namespace in primary type name", (Throwable)e);
            return this.primaryItemName.toString();
        }
    }

    @Override
    public PropertyDefinition[] getDeclaredPropertyDefinitions() {
        if (this.propertyDefinitionTemplates == null) {
            return null;
        }
        return this.propertyDefinitionTemplates.toArray(new PropertyDefinition[this.propertyDefinitionTemplates.size()]);
    }

    @Override
    public NodeDefinition[] getDeclaredChildNodeDefinitions() {
        if (this.nodeDefinitionTemplates == null) {
            return null;
        }
        return this.nodeDefinitionTemplates.toArray(new NodeDefinition[this.nodeDefinitionTemplates.size()]);
    }
}

