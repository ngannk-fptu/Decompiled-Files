/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.impl.llom.RoleChecker;

public class MURoleChecker
extends RoleChecker {
    public MURoleChecker(String role) {
        super(role);
    }

    public boolean checkHeader(SOAPHeaderBlock header) {
        if (header.getMustUnderstand()) {
            return super.checkHeader(header);
        }
        return false;
    }
}

