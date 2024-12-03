/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.startup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.startup.UserConfig;
import org.apache.catalina.startup.UserDatabase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.StringManager;

public final class PasswdUserDatabase
implements UserDatabase {
    private static final Log log = LogFactory.getLog(PasswdUserDatabase.class);
    private static final StringManager sm = StringManager.getManager(PasswdUserDatabase.class);
    private static final String PASSWORD_FILE = "/etc/passwd";
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
        try (BufferedReader reader = new BufferedReader(new FileReader(PASSWORD_FILE));){
            String line = reader.readLine();
            while (line != null) {
                String[] tokens = line.split(":");
                if (tokens.length > 5 && tokens[0].length() > 0 && tokens[5].length() > 0) {
                    this.homes.put(tokens[0], tokens[5]);
                }
                line = reader.readLine();
            }
        }
        catch (Exception e) {
            log.warn((Object)sm.getString("passwdUserDatabase.readFail"), (Throwable)e);
        }
    }
}

