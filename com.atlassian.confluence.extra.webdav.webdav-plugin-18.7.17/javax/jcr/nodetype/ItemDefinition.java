/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.nodetype;

import javax.jcr.nodetype.NodeType;

public interface ItemDefinition {
    public NodeType getDeclaringNodeType();

    public String getName();

    public boolean isAutoCreated();

    public boolean isMandatory();

    public int getOnParentVersion();

    public boolean isProtected();
}

