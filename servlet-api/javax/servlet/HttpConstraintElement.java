/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet;

import java.util.ResourceBundle;
import javax.servlet.annotation.ServletSecurity;

public class HttpConstraintElement {
    private static final String LSTRING_FILE = "javax.servlet.LocalStrings";
    private static final ResourceBundle lStrings = ResourceBundle.getBundle("javax.servlet.LocalStrings");
    private final ServletSecurity.EmptyRoleSemantic emptyRoleSemantic;
    private final ServletSecurity.TransportGuarantee transportGuarantee;
    private final String[] rolesAllowed;

    public HttpConstraintElement() {
        this.emptyRoleSemantic = ServletSecurity.EmptyRoleSemantic.PERMIT;
        this.transportGuarantee = ServletSecurity.TransportGuarantee.NONE;
        this.rolesAllowed = new String[0];
    }

    public HttpConstraintElement(ServletSecurity.EmptyRoleSemantic emptyRoleSemantic) {
        this.emptyRoleSemantic = emptyRoleSemantic;
        this.transportGuarantee = ServletSecurity.TransportGuarantee.NONE;
        this.rolesAllowed = new String[0];
    }

    public HttpConstraintElement(ServletSecurity.TransportGuarantee transportGuarantee, String ... rolesAllowed) {
        this.emptyRoleSemantic = ServletSecurity.EmptyRoleSemantic.PERMIT;
        this.transportGuarantee = transportGuarantee;
        this.rolesAllowed = rolesAllowed;
    }

    public HttpConstraintElement(ServletSecurity.EmptyRoleSemantic emptyRoleSemantic, ServletSecurity.TransportGuarantee transportGuarantee, String ... rolesAllowed) {
        if (rolesAllowed != null && rolesAllowed.length > 0 && ServletSecurity.EmptyRoleSemantic.DENY.equals((Object)emptyRoleSemantic)) {
            throw new IllegalArgumentException(lStrings.getString("httpConstraintElement.invalidRolesDeny"));
        }
        this.emptyRoleSemantic = emptyRoleSemantic;
        this.transportGuarantee = transportGuarantee;
        this.rolesAllowed = rolesAllowed;
    }

    public ServletSecurity.EmptyRoleSemantic getEmptyRoleSemantic() {
        return this.emptyRoleSemantic;
    }

    public ServletSecurity.TransportGuarantee getTransportGuarantee() {
        return this.transportGuarantee;
    }

    public String[] getRolesAllowed() {
        return this.rolesAllowed;
    }
}

