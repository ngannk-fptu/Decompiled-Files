/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.nodetype;

import javax.jcr.Value;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.PropertyDefinition;

public interface PropertyDefinitionTemplate
extends PropertyDefinition {
    public void setName(String var1) throws ConstraintViolationException;

    public void setAutoCreated(boolean var1);

    public void setMandatory(boolean var1);

    public void setOnParentVersion(int var1);

    public void setProtected(boolean var1);

    public void setRequiredType(int var1);

    public void setValueConstraints(String[] var1);

    public void setDefaultValues(Value[] var1);

    public void setMultiple(boolean var1);

    public void setAvailableQueryOperators(String[] var1);

    public void setFullTextSearchable(boolean var1);

    public void setQueryOrderable(boolean var1);
}

