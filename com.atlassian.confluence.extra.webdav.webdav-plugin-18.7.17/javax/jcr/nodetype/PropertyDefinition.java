/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.nodetype;

import javax.jcr.Value;
import javax.jcr.nodetype.ItemDefinition;

public interface PropertyDefinition
extends ItemDefinition {
    public int getRequiredType();

    public String[] getValueConstraints();

    public Value[] getDefaultValues();

    public boolean isMultiple();

    public String[] getAvailableQueryOperators();

    public boolean isFullTextSearchable();

    public boolean isQueryOrderable();
}

