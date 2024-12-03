/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.model.application;

import java.io.Serializable;
import org.apache.commons.lang3.Validate;

public class RemoteAddress
implements Serializable,
Comparable<RemoteAddress> {
    private String address;

    private RemoteAddress() {
    }

    public RemoteAddress(String address) {
        Validate.notEmpty((CharSequence)address, (String)"You cannot create a remote address with null address", (Object[])new Object[0]);
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RemoteAddress that = (RemoteAddress)o;
        return !(this.address != null ? !this.address.equals(that.address) : that.address != null);
    }

    public int hashCode() {
        return this.address != null ? this.address.hashCode() : 0;
    }

    @Override
    public int compareTo(RemoteAddress o) {
        return this.address.compareTo(o.getAddress());
    }

    public String toString() {
        return "RemoteAddress{address='" + this.address + '\'' + '}';
    }
}

