/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.nodetype;

import javax.jcr.nodetype.ItemDefinition;
import javax.jcr.nodetype.NodeType;

public interface NodeDefinition
extends ItemDefinition {
    public NodeType[] getRequiredPrimaryTypes();

    public String[] getRequiredPrimaryTypeNames();

    public NodeType getDefaultPrimaryType();

    public String getDefaultPrimaryTypeName();

    public boolean allowsSameNameSiblings();
}

