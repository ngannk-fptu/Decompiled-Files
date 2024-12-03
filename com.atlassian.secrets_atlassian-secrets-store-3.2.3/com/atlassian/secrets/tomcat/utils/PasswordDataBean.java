/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.tomcat.utils;

import java.util.function.Consumer;

public class PasswordDataBean {
    public final String encryptedPasswordFile;
    public final Consumer<String> superSetter;
    public final String passwordName;

    public PasswordDataBean(String encryptedPasswordFile, Consumer<String> superSetter, String passwordName) {
        this.encryptedPasswordFile = encryptedPasswordFile;
        this.superSetter = superSetter;
        this.passwordName = passwordName;
    }
}

