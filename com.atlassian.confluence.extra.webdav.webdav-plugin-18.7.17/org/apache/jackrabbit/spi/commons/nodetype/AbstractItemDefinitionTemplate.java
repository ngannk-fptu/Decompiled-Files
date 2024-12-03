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
import javax.jcr.nodetype.ItemDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.OnParentVersionAction;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.nodetype.ItemDefinitionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractItemDefinitionTemplate
implements ItemDefinition {
    private static final Logger log = LoggerFactory.getLogger(AbstractItemDefinitionTemplate.class);
    private Name name;
    private boolean autoCreated;
    private boolean mandatory;
    private int opv = 1;
    private boolean protectedStatus;
    protected final NamePathResolver resolver;

    AbstractItemDefinitionTemplate(NamePathResolver resolver) {
        this.resolver = resolver;
    }

    AbstractItemDefinitionTemplate(ItemDefinition def, NamePathResolver resolver) throws ConstraintViolationException {
        this.resolver = resolver;
        if (def instanceof ItemDefinitionImpl) {
            this.name = ((ItemDefinitionImpl)def).itemDef.getName();
        } else {
            this.setName(def.getName());
        }
        this.autoCreated = def.isAutoCreated();
        this.mandatory = def.isMandatory();
        this.opv = def.getOnParentVersion();
        this.protectedStatus = def.isProtected();
    }

    public void setName(String name) throws ConstraintViolationException {
        if ("*".equals(name)) {
            this.name = NameConstants.ANY_NAME;
        } else {
            try {
                this.name = this.resolver.getQName(name);
            }
            catch (RepositoryException e) {
                throw new ConstraintViolationException(e);
            }
        }
    }

    public void setAutoCreated(boolean autoCreated) {
        this.autoCreated = autoCreated;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public void setOnParentVersion(int opv) {
        OnParentVersionAction.nameFromValue(opv);
        this.opv = opv;
    }

    public void setProtected(boolean protectedStatus) {
        this.protectedStatus = protectedStatus;
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
            log.error("encountered unregistered namespace in item definition name", (Throwable)e);
            return this.name.toString();
        }
    }

    @Override
    public NodeType getDeclaringNodeType() {
        return null;
    }

    @Override
    public boolean isAutoCreated() {
        return this.autoCreated;
    }

    @Override
    public boolean isMandatory() {
        return this.mandatory;
    }

    @Override
    public int getOnParentVersion() {
        return this.opv;
    }

    @Override
    public boolean isProtected() {
        return this.protectedStatus;
    }
}

