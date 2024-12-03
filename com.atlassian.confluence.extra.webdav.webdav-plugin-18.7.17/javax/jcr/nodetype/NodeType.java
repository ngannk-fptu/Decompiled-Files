/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.nodetype;

import javax.jcr.Value;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeTypeDefinition;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.PropertyDefinition;

public interface NodeType
extends NodeTypeDefinition {
    public static final String NT_BASE = "{http://www.jcp.org/jcr/nt/1.0}base";
    public static final String NT_HIERARCHY_NODE = "{http://www.jcp.org/jcr/nt/1.0}hierarchyNode";
    public static final String NT_FOLDER = "{http://www.jcp.org/jcr/nt/1.0}folder";
    public static final String NT_FILE = "{http://www.jcp.org/jcr/nt/1.0}file";
    public static final String NT_LINKED_FILE = "{http://www.jcp.org/jcr/nt/1.0}linkedFile";
    public static final String NT_RESOURCE = "{http://www.jcp.org/jcr/nt/1.0}resource";
    public static final String NT_UNSTRUCTURED = "{http://www.jcp.org/jcr/nt/1.0}unstructured";
    public static final String NT_ADDRESS = "{http://www.jcp.org/jcr/nt/1.0}address";
    public static final String MIX_REFERENCEABLE = "{http://www.jcp.org/jcr/mix/1.0}referenceable";
    public static final String MIX_TITLE = "{http://www.jcp.org/jcr/mix/1.0}title";
    public static final String MIX_CREATED = "{http://www.jcp.org/jcr/mix/1.0}created";
    public static final String MIX_LAST_MODIFIED = "{http://www.jcp.org/jcr/mix/1.0}lastModified";
    public static final String MIX_LANGUAGE = "{http://www.jcp.org/jcr/mix/1.0}language";
    public static final String MIX_MIMETYPE = "{http://www.jcp.org/jcr/mix/1.0}mimeType";
    public static final String NT_NODE_TYPE = "{http://www.jcp.org/jcr/nt/1.0}nodeType";
    public static final String NT_PROPERTY_DEFINITION = "{http://www.jcp.org/jcr/nt/1.0}propertyDefinition";
    public static final String NT_CHILD_NODE_DEFINITION = "{http://www.jcp.org/jcr/nt/1.0}childNodeDefinition";
    public static final String MIX_SHAREABLE = "{http://www.jcp.org/jcr/mix/1.0}shareable";
    public static final String MIX_LOCKABLE = "{http://www.jcp.org/jcr/mix/1.0}lockable";
    public static final String MIX_LIFECYCLE = "{http://www.jcp.org/jcr/mix/1.0}lifecycle";
    public static final String MIX_SIMPLE_VERSIONABLE = "{http://www.jcp.org/jcr/mix/1.0}simpleVersionable";
    public static final String MIX_VERSIONABLE = "{http://www.jcp.org/jcr/mix/1.0}versionable";
    public static final String NT_VERSION_HISTORY = "{http://www.jcp.org/jcr/nt/1.0}versionHistory";
    public static final String NT_VERSION = "{http://www.jcp.org/jcr/nt/1.0}version";
    public static final String NT_FROZEN_NODE = "{http://www.jcp.org/jcr/nt/1.0}frozenNode";
    public static final String NT_VERSIONED_CHILD = "{http://www.jcp.org/jcr/nt/1.0}versionedChild";
    public static final String NT_ACTIVITY = "{http://www.jcp.org/jcr/nt/1.0}activity";
    public static final String NT_CONFIGURATION = "{http://www.jcp.org/jcr/nt/1.0}configuration";
    public static final String NT_QUERY = "{http://www.jcp.org/jcr/nt/1.0}query";

    public NodeType[] getSupertypes();

    public NodeType[] getDeclaredSupertypes();

    public NodeTypeIterator getSubtypes();

    public NodeTypeIterator getDeclaredSubtypes();

    public boolean isNodeType(String var1);

    public PropertyDefinition[] getPropertyDefinitions();

    public NodeDefinition[] getChildNodeDefinitions();

    public boolean canSetProperty(String var1, Value var2);

    public boolean canSetProperty(String var1, Value[] var2);

    public boolean canAddChildNode(String var1);

    public boolean canAddChildNode(String var1, String var2);

    public boolean canRemoveItem(String var1);

    public boolean canRemoveNode(String var1);

    public boolean canRemoveProperty(String var1);
}

