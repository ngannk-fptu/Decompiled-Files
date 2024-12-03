/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class KerbCallback
implements CallbackHandler {
    private final SQLServerConnection con;
    private String usernameRequested = null;

    KerbCallback(SQLServerConnection con) {
        this.con = con;
    }

    private static String getAnyOf(Callback callback, Properties properties, String ... names) throws UnsupportedCallbackException {
        for (String name : names) {
            String val = properties.getProperty(name);
            if (val == null || val.trim().isEmpty()) continue;
            return val;
        }
        throw new UnsupportedCallbackException(callback, "Cannot get any of properties: " + Arrays.toString(names) + " from con properties");
    }

    public String getUsernameRequested() {
        return this.usernameRequested;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                this.usernameRequested = KerbCallback.getAnyOf(callback, this.con.activeConnectionProperties, "user", SQLServerDriverStringProperty.USER.name());
                ((NameCallback)callback).setName(this.usernameRequested);
                continue;
            }
            if (callback instanceof PasswordCallback) {
                String password = KerbCallback.getAnyOf(callback, this.con.activeConnectionProperties, "password", SQLServerDriverStringProperty.PASSWORD.name());
                ((PasswordCallback)callback).setPassword(password.toCharArray());
                continue;
            }
            throw new UnsupportedCallbackException(callback, "Unrecognized Callback type: " + callback.getClass());
        }
    }
}

