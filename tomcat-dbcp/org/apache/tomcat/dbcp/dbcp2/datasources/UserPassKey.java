/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.io.Serializable;
import java.util.Objects;
import org.apache.tomcat.dbcp.dbcp2.datasources.CharArray;

final class UserPassKey
implements Serializable {
    private static final long serialVersionUID = 5142970911626584817L;
    private final CharArray name;
    private final CharArray password;

    UserPassKey(char[] userName, char[] password) {
        this(new CharArray(userName), new CharArray(password));
    }

    UserPassKey(CharArray userName, CharArray userPassword) {
        this.name = userName;
        this.password = userPassword;
    }

    UserPassKey(String userName) {
        this(new CharArray(userName), CharArray.NULL);
    }

    UserPassKey(String userName, char[] password) {
        this(new CharArray(userName), new CharArray(password));
    }

    UserPassKey(String userName, String userPassword) {
        this(new CharArray(userName), new CharArray(userPassword));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        UserPassKey other = (UserPassKey)obj;
        return Objects.equals(this.name, other.name);
    }

    String getPassword() {
        return this.password.asString();
    }

    char[] getPasswordCharArray() {
        return this.password.get();
    }

    String getUserName() {
        return this.name.asString();
    }

    public int hashCode() {
        return Objects.hash(this.name);
    }
}

