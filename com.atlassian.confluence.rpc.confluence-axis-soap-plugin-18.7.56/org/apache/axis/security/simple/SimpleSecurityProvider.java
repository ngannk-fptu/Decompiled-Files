/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.security.simple;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.security.simple.SimpleAuthenticatedUser;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class SimpleSecurityProvider
implements SecurityProvider {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$security$simple$SimpleSecurityProvider == null ? (class$org$apache$axis$security$simple$SimpleSecurityProvider = SimpleSecurityProvider.class$("org.apache.axis.security.simple.SimpleSecurityProvider")) : class$org$apache$axis$security$simple$SimpleSecurityProvider).getName());
    HashMap users = null;
    HashMap perms = null;
    boolean initialized = false;
    static /* synthetic */ Class class$org$apache$axis$security$simple$SimpleSecurityProvider;

    private synchronized void initialize(MessageContext msgContext) {
        if (this.initialized) {
            return;
        }
        String configPath = msgContext.getStrProp("configPath");
        configPath = configPath == null ? "" : configPath + File.separator;
        File userFile = new File(configPath + "users.lst");
        if (userFile.exists()) {
            this.users = new HashMap();
            try {
                FileReader fr = new FileReader(userFile);
                LineNumberReader lnr = new LineNumberReader(fr);
                String line = null;
                while ((line = lnr.readLine()) != null) {
                    String passwd;
                    StringTokenizer st = new StringTokenizer(line);
                    if (!st.hasMoreTokens()) continue;
                    String userID = st.nextToken();
                    String string = passwd = st.hasMoreTokens() ? st.nextToken() : "";
                    if (log.isDebugEnabled()) {
                        log.debug((Object)Messages.getMessage("fromFile00", userID, passwd));
                    }
                    this.users.put(userID, passwd);
                }
                lnr.close();
            }
            catch (Exception e) {
                log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
                return;
            }
        }
        this.initialized = true;
    }

    public AuthenticatedUser authenticate(MessageContext msgContext) {
        if (!this.initialized) {
            this.initialize(msgContext);
        }
        String username = msgContext.getUsername();
        String password = msgContext.getPassword();
        if (this.users != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("user00", username));
            }
            if (username == null || username.equals("") || !this.users.containsKey(username)) {
                return null;
            }
            String valid = (String)this.users.get(username);
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("password00", password));
            }
            if (valid.length() > 0 && !valid.equals(password)) {
                return null;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("auth00", username));
            }
            return new SimpleAuthenticatedUser(username);
        }
        return null;
    }

    public boolean userMatches(AuthenticatedUser user, String principal) {
        if (user == null) {
            return principal == null;
        }
        return user.getName().compareToIgnoreCase(principal) == 0;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

