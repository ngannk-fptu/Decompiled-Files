/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.impl.llom.Checker;

public class RoleChecker
implements Checker {
    String role;

    public RoleChecker(String role) {
        this.role = role;
    }

    public boolean checkHeader(SOAPHeaderBlock header) {
        if (this.role == null) {
            return true;
        }
        String thisRole = header.getRole();
        return this.role.equals(thisRole);
    }
}

