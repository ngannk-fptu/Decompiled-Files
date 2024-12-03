/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Address
 *  javax.mail.internet.AddressException
 *  javax.mail.internet.InternetAddress
 */
package com.atlassian.confluence.mail.address;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Deprecated
public class ConfluenceMailAddress {
    private final String fullAddressString;
    private final String personal;
    private final String address;

    public ConfluenceMailAddress(String fullAddressString) {
        FieldsHolder fields = this.computeFields(fullAddressString);
        this.fullAddressString = fields.fullAddressStringField;
        this.personal = fields.personalField;
        this.address = fields.addressField;
    }

    private FieldsHolder computeFields(String fullAddrString) {
        FieldsHolder ret = new FieldsHolder();
        try {
            InternetAddress addressObject = new InternetAddress(fullAddrString);
            ret.fullAddressStringField = addressObject.toUnicodeString();
            ret.personalField = addressObject.getPersonal();
            ret.addressField = addressObject.getAddress();
        }
        catch (AddressException e) {
            ret.fullAddressStringField = fullAddrString;
        }
        return ret;
    }

    public ConfluenceMailAddress(InternetAddress addressObject) {
        this.fullAddressString = addressObject.toUnicodeString();
        this.personal = addressObject.getPersonal();
        this.address = addressObject.getAddress();
    }

    public ConfluenceMailAddress(ConfluenceMailAddress addressObject) {
        this.fullAddressString = addressObject.toUnicodeString();
        this.personal = addressObject.getPersonal();
        this.address = addressObject.getAddress();
    }

    public ConfluenceMailAddress(Address addressObject) {
        this.fullAddressString = addressObject.toString();
        this.address = null;
        this.personal = null;
    }

    public String getSender() {
        if (this.getPersonal() != null) {
            return this.getPersonal();
        }
        return this.toUnicodeString();
    }

    public String toUnicodeString() {
        return this.fullAddressString;
    }

    public String getPersonal() {
        return this.personal;
    }

    public String getAddress() {
        return this.address;
    }

    public String toString() {
        return this.fullAddressString;
    }

    public boolean equals(Object o) {
        if (o instanceof ConfluenceMailAddress) {
            ConfluenceMailAddress address = (ConfluenceMailAddress)o;
            return this.fullAddressString.equals(address.toString());
        }
        return false;
    }

    public int hashCode() {
        return this.fullAddressString.hashCode();
    }

    private static final class FieldsHolder {
        String fullAddressStringField;
        String personalField;
        String addressField;

        private FieldsHolder() {
        }
    }
}

