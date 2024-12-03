/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.nodetype;

import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.PropertyDefinition;

public interface NodeTypeDefinition {
    public String getName();

    public String[] getDeclaredSupertypeNames();

    public boolean isAbstract();

    public boolean isMixin();

    public boolean hasOrderableChildNodes();

    public boolean isQueryable();

    public String getPrimaryItemName();

    public PropertyDefinition[] getDeclaredPropertyDefinitions();

    public NodeDefinition[] getDeclaredChildNodeDefinitions();
}

