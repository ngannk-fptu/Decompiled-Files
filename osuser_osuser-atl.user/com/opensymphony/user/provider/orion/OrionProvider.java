/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.evermind.security.RoleManager
 *  com.evermind.security.UserManager
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.orion;

import com.evermind.security.RoleManager;
import com.evermind.security.UserManager;
import com.opensymphony.user.Entity;
import com.opensymphony.user.provider.UserProvider;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Properties;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class OrionProvider
implements UserProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$orion$OrionProvider == null ? (class$com$opensymphony$user$provider$orion$OrionProvider = OrionProvider.class$("com.opensymphony.user.provider.orion.OrionProvider")) : class$com$opensymphony$user$provider$orion$OrionProvider));
    protected transient RoleManager roleManager;
    protected transient UserManager userManager;
    static /* synthetic */ Class class$com$opensymphony$user$provider$orion$OrionProvider;

    public void flushCaches() {
    }

    public boolean init(Properties properties) {
        try {
            InitialContext context = new InitialContext();
            this.roleManager = (RoleManager)context.lookup("java:comp/RoleManager");
            this.userManager = (UserManager)context.lookup("java:comp/UserManager");
            return true;
        }
        catch (Exception ex) {
            log.error((Object)("Error in init(" + properties + ")"), (Throwable)ex);
            return false;
        }
    }

    public boolean load(String name, Entity.Accessor accessor) {
        accessor.setMutable(true);
        return true;
    }

    public boolean store(String name, Entity.Accessor accessor) {
        return true;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.init(null);
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

