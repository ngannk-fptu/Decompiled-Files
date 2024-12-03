/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.jcr.security;

import java.util.ArrayList;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.Privilege;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.security.CurrentUserPrivilegeSetProperty;
import org.apache.jackrabbit.webdav.xml.Namespace;

public class JcrUserPrivilegesProperty {
    private final Session session;
    private final String absPath;

    public JcrUserPrivilegesProperty(Session session, String absPath) throws RepositoryException {
        this.session = session;
        this.absPath = absPath;
    }

    public CurrentUserPrivilegeSetProperty asDavProperty() throws RepositoryException {
        ArrayList<org.apache.jackrabbit.webdav.security.Privilege> davPrivs = new ArrayList<org.apache.jackrabbit.webdav.security.Privilege>();
        for (Privilege privilege : this.session.getAccessControlManager().getPrivileges(this.absPath)) {
            String privilegeName = privilege.getName();
            String prefix = Text.getNamespacePrefix(privilegeName);
            Namespace ns = prefix.isEmpty() ? Namespace.EMPTY_NAMESPACE : Namespace.getNamespace(prefix, this.session.getNamespaceURI(prefix));
            davPrivs.add(org.apache.jackrabbit.webdav.security.Privilege.getPrivilege(Text.getLocalName(privilegeName), ns));
        }
        return new CurrentUserPrivilegeSetProperty(davPrivs.toArray(new org.apache.jackrabbit.webdav.security.Privilege[davPrivs.size()]));
    }
}

