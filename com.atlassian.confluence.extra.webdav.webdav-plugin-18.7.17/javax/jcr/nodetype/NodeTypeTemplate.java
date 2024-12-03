/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.nodetype;

import java.util.List;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeTypeDefinition;

public interface NodeTypeTemplate
extends NodeTypeDefinition {
    public void setName(String var1) throws ConstraintViolationException;

    public void setDeclaredSuperTypeNames(String[] var1) throws ConstraintViolationException;

    public void setAbstract(boolean var1);

    public void setMixin(boolean var1);

    public void setOrderableChildNodes(boolean var1);

    public void setPrimaryItemName(String var1) throws ConstraintViolationException;

    public void setQueryable(boolean var1);

    public List getPropertyDefinitionTemplates();

    public List getNodeDefinitionTemplates();
}

