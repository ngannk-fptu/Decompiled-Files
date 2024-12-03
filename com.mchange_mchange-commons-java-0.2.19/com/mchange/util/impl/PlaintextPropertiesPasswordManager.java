/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.PasswordManager;
import com.mchange.util.impl.SyncedProperties;
import java.io.File;
import java.io.IOException;

public class PlaintextPropertiesPasswordManager
implements PasswordManager {
    private static final String PASSWORD_PROP_PFX = "password.";
    private static final String HEADER = "com.mchange.util.impl.PlaintextPropertiesPasswordManager data";
    SyncedProperties props;

    public PlaintextPropertiesPasswordManager(File file) throws IOException {
        this.props = new SyncedProperties(file, HEADER);
    }

    @Override
    public boolean validate(String string, String string2) throws IOException {
        return string2.equals(this.props.getProperty(PASSWORD_PROP_PFX + string));
    }

    @Override
    public boolean updatePassword(String string, String string2, String string3) throws IOException {
        if (!this.validate(string, string2)) {
            return false;
        }
        this.props.put(PASSWORD_PROP_PFX + string, string3);
        return true;
    }
}

