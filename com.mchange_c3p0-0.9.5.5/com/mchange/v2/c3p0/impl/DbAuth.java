/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.lang.ObjectUtils
 *  com.mchange.v2.ser.UnsupportedVersionException
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v2.lang.ObjectUtils;
import com.mchange.v2.ser.UnsupportedVersionException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class DbAuth
implements Serializable {
    transient String username;
    transient String password;
    static final long serialVersionUID = 1L;
    private static final short VERSION = 1;

    public DbAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUser() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getMaskedUserString() {
        return this.getMaskedUserString(2, 8);
    }

    private String getMaskedUserString(int chars_to_reveal, int total_chars) {
        if (this.username == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer(32);
        if (this.username.length() >= chars_to_reveal) {
            sb.append(this.username.substring(0, chars_to_reveal));
            int len = total_chars - chars_to_reveal;
            for (int i = 0; i < len; ++i) {
                sb.append('*');
            }
        } else {
            sb.append(this.username);
        }
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && this.getClass() == o.getClass()) {
            DbAuth other = (DbAuth)o;
            return ObjectUtils.eqOrBothNull((Object)this.username, (Object)other.username) && ObjectUtils.eqOrBothNull((Object)this.password, (Object)other.password);
        }
        return false;
    }

    public int hashCode() {
        return ObjectUtils.hashOrZero((Object)this.username) ^ ObjectUtils.hashOrZero((Object)this.password);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeShort(1);
        out.writeObject(this.username);
        out.writeObject(this.password);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        short version = in.readShort();
        switch (version) {
            case 1: {
                this.username = (String)in.readObject();
                this.password = (String)in.readObject();
                break;
            }
            default: {
                throw new UnsupportedVersionException((Object)this, (int)version);
            }
        }
    }
}

