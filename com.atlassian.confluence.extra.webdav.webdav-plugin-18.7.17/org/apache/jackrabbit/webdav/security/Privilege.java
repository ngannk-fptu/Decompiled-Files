/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.security;

import java.util.HashMap;
import java.util.Map;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Privilege
implements XmlSerializable {
    public static final String XML_PRIVILEGE = "privilege";
    private static final Map<String, Privilege> REGISTERED_PRIVILEGES = new HashMap<String, Privilege>();
    public static final Privilege PRIVILEGE_READ = Privilege.getPrivilege("read", SecurityConstants.NAMESPACE);
    public static final Privilege PRIVILEGE_WRITE = Privilege.getPrivilege("write", SecurityConstants.NAMESPACE);
    public static final Privilege PRIVILEGE_WRITE_PROPERTIES = Privilege.getPrivilege("write-properties", SecurityConstants.NAMESPACE);
    public static final Privilege PRIVILEGE_WRITE_CONTENT = Privilege.getPrivilege("write-content", SecurityConstants.NAMESPACE);
    public static final Privilege PRIVILEGE_UNLOCK = Privilege.getPrivilege("unlock", SecurityConstants.NAMESPACE);
    public static final Privilege PRIVILEGE_READ_ACL = Privilege.getPrivilege("read-acl", SecurityConstants.NAMESPACE);
    public static final Privilege PRIVILEGE_READ_CURRENT_USER_PRIVILEGE_SET = Privilege.getPrivilege("read-current-user-privilege-set", SecurityConstants.NAMESPACE);
    public static final Privilege PRIVILEGE_WRITE_ACL = Privilege.getPrivilege("write-acl", SecurityConstants.NAMESPACE);
    public static final Privilege PRIVILEGE_BIND = Privilege.getPrivilege("bind", SecurityConstants.NAMESPACE);
    public static final Privilege PRIVILEGE_UNBIND = Privilege.getPrivilege("unbind", SecurityConstants.NAMESPACE);
    public static final Privilege PRIVILEGE_ALL = Privilege.getPrivilege("all", SecurityConstants.NAMESPACE);
    private final String privilege;
    private final Namespace namespace;

    private Privilege(String privilege, Namespace namespace) {
        this.privilege = privilege;
        this.namespace = namespace;
    }

    public String getName() {
        return this.privilege;
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    @Override
    public Element toXml(Document document) {
        Element privEl = DomUtil.createElement(document, XML_PRIVILEGE, SecurityConstants.NAMESPACE);
        DomUtil.addChildElement(privEl, this.privilege, this.namespace);
        return privEl;
    }

    public static Privilege getPrivilege(String privilege, Namespace namespace) {
        String key;
        if (privilege == null) {
            throw new IllegalArgumentException("'null' is not a valid privilege.");
        }
        if (namespace == null) {
            namespace = Namespace.EMPTY_NAMESPACE;
        }
        if (REGISTERED_PRIVILEGES.containsKey(key = "{" + namespace.getURI() + "}" + privilege)) {
            return REGISTERED_PRIVILEGES.get(key);
        }
        Privilege p = new Privilege(privilege, namespace);
        REGISTERED_PRIVILEGES.put(key, p);
        return p;
    }

    public static Privilege getPrivilege(Element privilege) throws DavException {
        if (!DomUtil.matches(privilege, XML_PRIVILEGE, SecurityConstants.NAMESPACE)) {
            throw new DavException(400, "DAV:privilege element expected.");
        }
        Element el = DomUtil.getFirstChildElement(privilege);
        return Privilege.getPrivilege(el.getLocalName(), DomUtil.getNamespace(el));
    }
}

