/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import java.io.IOException;

public interface PasswordManager {
    public boolean validate(String var1, String var2) throws IOException;

    public boolean updatePassword(String var1, String var2, String var3) throws IOException;
}

