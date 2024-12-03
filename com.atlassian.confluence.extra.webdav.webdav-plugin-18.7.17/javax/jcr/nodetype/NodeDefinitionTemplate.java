/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.nodetype;

import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeDefinition;

public interface NodeDefinitionTemplate
extends NodeDefinition {
    public void setName(String var1) throws ConstraintViolationException;

    public void setAutoCreated(boolean var1);

    public void setMandatory(boolean var1);

    public void setOnParentVersion(int var1);

    public void setProtected(boolean var1);

    public void setRequiredPrimaryTypeNames(String[] var1) throws ConstraintViolationException;

    public void setDefaultPrimaryTypeName(String var1) throws ConstraintViolationException;

    public void setSameNameSiblings(boolean var1);
}

