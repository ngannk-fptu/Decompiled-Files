/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

class DbAuth {
    String username;
    String password;

    public DbAuth(String string, String string2) {
        this.username = string;
        this.password = string2;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean equals(Object object) {
        if (object != null && this.getClass() == object.getClass()) {
            DbAuth dbAuth = (DbAuth)object;
            return this.username.equals(dbAuth.username) && this.password.equals(dbAuth.password);
        }
        return false;
    }

    public int hashCode() {
        return this.username.hashCode() ^ this.password.hashCode();
    }
}

