/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.startup;

import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.startup.UserConfig;
import org.apache.catalina.startup.UserDatabase;

public final class HomesUserDatabase
implements UserDatabase {
    private final Map<String, String> homes = new HashMap<String, String>();
    private UserConfig userConfig = null;

    @Override
    public UserConfig getUserConfig() {
        return this.userConfig;
    }

    @Override
    public void setUserConfig(UserConfig userConfig) {
        this.userConfig = userConfig;
        this.init();
    }

    @Override
    public String getHome(String user) {
        return this.homes.get(user);
    }

    @Override
    public Enumeration<String> getUsers() {
        return Collections.enumeration(this.homes.keySet());
    }

    private void init() {
        String homeBase = this.userConfig.getHomeBase();
        File homeBaseDir = new File(homeBase);
        if (!homeBaseDir.exists() || !homeBaseDir.isDirectory()) {
            return;
        }
        String[] homeBaseFiles = homeBaseDir.list();
        if (homeBaseFiles == null) {
            return;
        }
        for (String homeBaseFile : homeBaseFiles) {
            File homeDir = new File(homeBaseDir, homeBaseFile);
            if (!homeDir.isDirectory() || !homeDir.canRead()) continue;
            this.homes.put(homeBaseFile, homeDir.toString());
        }
    }
}

