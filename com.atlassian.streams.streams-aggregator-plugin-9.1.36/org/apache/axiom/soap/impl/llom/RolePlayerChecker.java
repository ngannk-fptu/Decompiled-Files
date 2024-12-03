/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import java.util.List;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.RolePlayer;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.llom.Checker;

public class RolePlayerChecker
implements Checker {
    RolePlayer rolePlayer;
    String namespace;

    public RolePlayerChecker(RolePlayer rolePlayer) {
        this.rolePlayer = rolePlayer;
    }

    public RolePlayerChecker(RolePlayer rolePlayer, String namespace) {
        this.rolePlayer = rolePlayer;
        this.namespace = namespace;
    }

    public boolean checkHeader(SOAPHeaderBlock header) {
        List roles;
        OMNamespace headerNamespace;
        if (!(this.namespace == null || (headerNamespace = header.getNamespace()) != null && this.namespace.equals(headerNamespace.getNamespaceURI()))) {
            return false;
        }
        String role = header.getRole();
        SOAPVersion version = header.getVersion();
        if (role == null || role.equals("") || version instanceof SOAP12Version && role.equals("http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver")) {
            return this.rolePlayer == null || this.rolePlayer.isUltimateDestination();
        }
        if (role.equals(version.getNextRoleURI())) {
            return true;
        }
        if (version instanceof SOAP12Version && role.equals("http://www.w3.org/2003/05/soap-envelope/role/none")) {
            return false;
        }
        List list = roles = this.rolePlayer == null ? null : this.rolePlayer.getRoles();
        if (roles != null) {
            for (String thisRole : roles) {
                if (!thisRole.equals(role)) continue;
                return true;
            }
        }
        return false;
    }
}

