/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.ldap.control.ldap;

import javax.naming.ldap.Control;

public class DeletedControl
implements Control {
    @Override
    public byte[] getEncodedValue() {
        return new byte[0];
    }

    @Override
    public String getID() {
        return "1.2.840.113556.1.4.417";
    }

    @Override
    public boolean isCritical() {
        return true;
    }
}

