/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.jcr.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.security.SupportedPrivilege;
import org.apache.jackrabbit.webdav.security.SupportedPrivilegeSetProperty;
import org.apache.jackrabbit.webdav.xml.Namespace;

public class JcrSupportedPrivilegesProperty {
    private final Session session;
    private final String absPath;
    private final Set<Privilege> privileges = new HashSet<Privilege>();
    private final Map<String, SupportedPrivilege> supportedPrivileges = new HashMap<String, SupportedPrivilege>();
    private final HashSet<String> aggregated = new HashSet();

    public JcrSupportedPrivilegesProperty(Session session) throws RepositoryException {
        this.session = session;
        this.absPath = null;
        AccessControlManager acMgr = session.getAccessControlManager();
        Privilege jcrAll = acMgr.privilegeFromName("{http://www.jcp.org/jcr/1.0}all");
        this.privileges.add(jcrAll);
    }

    public JcrSupportedPrivilegesProperty(Session session, String absPath) {
        this.session = session;
        this.absPath = absPath;
    }

    public SupportedPrivilegeSetProperty asDavProperty() throws RepositoryException {
        if (this.privileges.isEmpty()) {
            AccessControlManager acMgr = this.session.getAccessControlManager();
            this.privileges.addAll(Arrays.asList(acMgr.getSupportedPrivileges(this.absPath)));
        }
        for (Privilege p : this.privileges) {
            if (this.aggregated.contains(p.getName())) continue;
            this.createSupportedPrivilege(p);
        }
        return new SupportedPrivilegeSetProperty(this.supportedPrivileges.values().toArray(new SupportedPrivilege[this.supportedPrivileges.size()]));
    }

    private SupportedPrivilege createSupportedPrivilege(Privilege privilege) throws RepositoryException {
        String privilegeName = privilege.getName();
        String localName = Text.getLocalName(privilegeName);
        String prefix = Text.getNamespacePrefix(privilegeName);
        Namespace ns = prefix.isEmpty() ? Namespace.EMPTY_NAMESPACE : Namespace.getNamespace(prefix, this.session.getNamespaceURI(prefix));
        org.apache.jackrabbit.webdav.security.Privilege davPrivilege = org.apache.jackrabbit.webdav.security.Privilege.getPrivilege(localName, ns);
        SupportedPrivilege[] aggregates = privilege.isAggregate() ? this.getDeclaredAggregates(privilege) : null;
        SupportedPrivilege sp = new SupportedPrivilege(davPrivilege, null, null, privilege.isAbstract(), aggregates);
        if (!this.aggregated.contains(privilegeName)) {
            this.supportedPrivileges.put(privilegeName, sp);
        }
        return sp;
    }

    private SupportedPrivilege[] getDeclaredAggregates(Privilege privilege) throws RepositoryException {
        ArrayList<SupportedPrivilege> declAggr = new ArrayList<SupportedPrivilege>();
        for (Privilege decl : privilege.getDeclaredAggregatePrivileges()) {
            String name = decl.getName();
            if (!this.aggregated.add(name)) continue;
            if (this.supportedPrivileges.containsKey(name)) {
                declAggr.add(this.supportedPrivileges.remove(name));
                continue;
            }
            declAggr.add(this.createSupportedPrivilege(decl));
        }
        return declAggr.toArray(new SupportedPrivilege[declAggr.size()]);
    }
}

