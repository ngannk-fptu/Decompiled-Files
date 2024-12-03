/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.startup;

import java.util.Enumeration;
import org.apache.catalina.startup.UserConfig;

public interface UserDatabase {
    public UserConfig getUserConfig();

    public void setUserConfig(UserConfig var1);

    public String getHome(String var1);

    public Enumeration<String> getUsers();
}

