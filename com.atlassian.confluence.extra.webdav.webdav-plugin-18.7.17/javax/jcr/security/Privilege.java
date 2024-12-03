/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.security;

public interface Privilege {
    public static final String JCR_READ = "{http://www.jcp.org/jcr/1.0}read";
    public static final String JCR_MODIFY_PROPERTIES = "{http://www.jcp.org/jcr/1.0}modifyProperties";
    public static final String JCR_ADD_CHILD_NODES = "{http://www.jcp.org/jcr/1.0}addChildNodes";
    public static final String JCR_REMOVE_NODE = "{http://www.jcp.org/jcr/1.0}removeNode";
    public static final String JCR_REMOVE_CHILD_NODES = "{http://www.jcp.org/jcr/1.0}removeChildNodes";
    public static final String JCR_WRITE = "{http://www.jcp.org/jcr/1.0}write";
    public static final String JCR_READ_ACCESS_CONTROL = "{http://www.jcp.org/jcr/1.0}readAccessControl";
    public static final String JCR_MODIFY_ACCESS_CONTROL = "{http://www.jcp.org/jcr/1.0}modifyAccessControl";
    public static final String JCR_LOCK_MANAGEMENT = "{http://www.jcp.org/jcr/1.0}lockManagement";
    public static final String JCR_VERSION_MANAGEMENT = "{http://www.jcp.org/jcr/1.0}versionManagement";
    public static final String JCR_NODE_TYPE_MANAGEMENT = "{http://www.jcp.org/jcr/1.0}nodeTypeManagement";
    public static final String JCR_RETENTION_MANAGEMENT = "{http://www.jcp.org/jcr/1.0}retentionManagement";
    public static final String JCR_LIFECYCLE_MANAGEMENT = "{http://www.jcp.org/jcr/1.0}lifecycleManagement";
    public static final String JCR_ALL = "{http://www.jcp.org/jcr/1.0}all";

    public String getName();

    public boolean isAbstract();

    public boolean isAggregate();

    public Privilege[] getDeclaredAggregatePrivileges();

    public Privilege[] getAggregatePrivileges();
}

