/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.jcr;

import org.apache.jackrabbit.commons.webdav.JcrRemotingConstants;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.security.Privilege;
import org.apache.jackrabbit.webdav.xml.Namespace;

public interface ItemResourceConstants
extends JcrRemotingConstants {
    public static final String METHODS = "OPTIONS, GET, HEAD, TRACE, PROPFIND, PROPPATCH, MKCOL, COPY, PUT, DELETE, MOVE, LOCK, UNLOCK, SUBSCRIBE, UNSUBSCRIBE, POLL, SEARCH, REPORT";
    public static final Namespace NAMESPACE = Namespace.getNamespace("dcr", "http://www.day.com/jcr/webdav/1.0");
    public static final Scope EXCLUSIVE_SESSION = Scope.create("exclusive-session-scoped", NAMESPACE);
    public static final DavPropertyName JCR_WORKSPACE_NAME = DavPropertyName.create("workspaceName", NAMESPACE);
    public static final DavPropertyName JCR_NAME = DavPropertyName.create("name", NAMESPACE);
    public static final DavPropertyName JCR_PATH = DavPropertyName.create("path", NAMESPACE);
    public static final DavPropertyName JCR_DEPTH = DavPropertyName.create("depth", NAMESPACE);
    public static final DavPropertyName JCR_PARENT = DavPropertyName.create("parent", NAMESPACE);
    public static final DavPropertyName JCR_ISNEW = DavPropertyName.create("isnew", NAMESPACE);
    public static final DavPropertyName JCR_ISMODIFIED = DavPropertyName.create("ismodified", NAMESPACE);
    public static final DavPropertyName JCR_DEFINITION = DavPropertyName.create("definition", NAMESPACE);
    public static final DavPropertyName JCR_SELECTOR_NAME = DavPropertyName.create("selectorName", NAMESPACE);
    public static final DavPropertyName JCR_PRIMARYNODETYPE = DavPropertyName.create("primarynodetype", NAMESPACE);
    public static final DavPropertyName JCR_MIXINNODETYPES = DavPropertyName.create("mixinnodetypes", NAMESPACE);
    public static final DavPropertyName JCR_INDEX = DavPropertyName.create("index", NAMESPACE);
    public static final DavPropertyName JCR_REFERENCES = DavPropertyName.create("references", NAMESPACE);
    public static final DavPropertyName JCR_WEAK_REFERENCES = DavPropertyName.create("weakreferences", NAMESPACE);
    public static final DavPropertyName JCR_UUID = DavPropertyName.create("uuid", NAMESPACE);
    public static final DavPropertyName JCR_PRIMARYITEM = DavPropertyName.create("primaryitem", NAMESPACE);
    public static final DavPropertyName JCR_TYPE = DavPropertyName.create("type", NAMESPACE);
    public static final DavPropertyName JCR_VALUE = DavPropertyName.create("value", NAMESPACE);
    public static final DavPropertyName JCR_VALUES = DavPropertyName.create("values", NAMESPACE);
    public static final DavPropertyName JCR_LENGTH = DavPropertyName.create("length", NAMESPACE);
    public static final DavPropertyName JCR_LENGTHS = DavPropertyName.create("lengths", NAMESPACE);
    public static final DavPropertyName JCR_GET_STRING = DavPropertyName.create("getstring", NAMESPACE);
    public static final DavPropertyName JCR_NAMESPACES = DavPropertyName.create("namespaces", NAMESPACE);
    public static final DavPropertyName JCR_NODETYPES_CND = DavPropertyName.create("nodetypes-cnd", NAMESPACE);
    public static final DavPropertyName JCR_VERSIONABLEUUID = DavPropertyName.create("versionableuuid", NAMESPACE);
    public static final Privilege PRIVILEGE_JCR_READ = Privilege.getPrivilege("read", NAMESPACE);
    public static final Privilege PRIVILEGE_JCR_ADD_NODE = Privilege.getPrivilege("add_node", NAMESPACE);
    public static final Privilege PRIVILEGE_JCR_SET_PROPERTY = Privilege.getPrivilege("set_property", NAMESPACE);
    public static final Privilege PRIVILEGE_JCR_REMOVE = Privilege.getPrivilege("remove", NAMESPACE);
}

