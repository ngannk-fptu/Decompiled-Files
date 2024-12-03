/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.startup;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.startup.UserDatabase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public final class UserConfig
implements LifecycleListener {
    private static final Log log = LogFactory.getLog(UserConfig.class);
    private String configClass = "org.apache.catalina.startup.ContextConfig";
    private String contextClass = "org.apache.catalina.core.StandardContext";
    private String directoryName = "public_html";
    private String homeBase = null;
    private Host host = null;
    private static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.startup");
    private String userClass = "org.apache.catalina.startup.PasswdUserDatabase";
    Pattern allow = null;
    Pattern deny = null;

    public String getConfigClass() {
        return this.configClass;
    }

    public void setConfigClass(String configClass) {
        this.configClass = configClass;
    }

    public String getContextClass() {
        return this.contextClass;
    }

    public void setContextClass(String contextClass) {
        this.contextClass = contextClass;
    }

    public String getDirectoryName() {
        return this.directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getHomeBase() {
        return this.homeBase;
    }

    public void setHomeBase(String homeBase) {
        this.homeBase = homeBase;
    }

    public String getUserClass() {
        return this.userClass;
    }

    public void setUserClass(String userClass) {
        this.userClass = userClass;
    }

    public String getAllow() {
        if (this.allow == null) {
            return null;
        }
        return this.allow.toString();
    }

    public void setAllow(String allow) {
        this.allow = allow == null || allow.length() == 0 ? null : Pattern.compile(allow);
    }

    public String getDeny() {
        if (this.deny == null) {
            return null;
        }
        return this.deny.toString();
    }

    public void setDeny(String deny) {
        this.deny = deny == null || deny.length() == 0 ? null : Pattern.compile(deny);
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        try {
            this.host = (Host)event.getLifecycle();
        }
        catch (ClassCastException e) {
            log.error((Object)sm.getString("hostConfig.cce", new Object[]{event.getLifecycle()}), (Throwable)e);
            return;
        }
        if (event.getType().equals("start")) {
            this.start();
        } else if (event.getType().equals("stop")) {
            this.stop();
        }
    }

    private void deploy() {
        if (this.host.getLogger().isDebugEnabled()) {
            this.host.getLogger().debug((Object)sm.getString("userConfig.deploying"));
        }
        UserDatabase database = null;
        try {
            Class<?> clazz = Class.forName(this.userClass);
            database = (UserDatabase)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            database.setUserConfig(this);
        }
        catch (Exception e) {
            this.host.getLogger().error((Object)sm.getString("userConfig.database"), (Throwable)e);
            return;
        }
        ExecutorService executor = this.host.getStartStopExecutor();
        ArrayList results = new ArrayList();
        Enumeration<String> users = database.getUsers();
        while (users.hasMoreElements()) {
            String user = users.nextElement();
            if (!this.isDeployAllowed(user)) continue;
            String string = database.getHome(user);
            results.add(executor.submit(new DeployUserDirectory(this, user, string)));
        }
        for (Future future : results) {
            try {
                future.get();
            }
            catch (Exception e) {
                this.host.getLogger().error((Object)sm.getString("userConfig.deploy.threaded.error"), (Throwable)e);
            }
        }
    }

    private void deploy(String user, String home) {
        String contextPath = "/~" + user;
        if (this.host.findChild(contextPath) != null) {
            return;
        }
        File app = new File(home, this.directoryName);
        if (!app.exists() || !app.isDirectory()) {
            return;
        }
        this.host.getLogger().info((Object)sm.getString("userConfig.deploy", new Object[]{user}));
        try {
            Class<?> clazz = Class.forName(this.contextClass);
            Context context = (Context)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            context.setPath(contextPath);
            context.setDocBase(app.toString());
            clazz = Class.forName(this.configClass);
            LifecycleListener listener = (LifecycleListener)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            context.addLifecycleListener(listener);
            this.host.addChild(context);
        }
        catch (Exception e) {
            this.host.getLogger().error((Object)sm.getString("userConfig.error", new Object[]{user}), (Throwable)e);
        }
    }

    private void start() {
        if (this.host.getLogger().isDebugEnabled()) {
            this.host.getLogger().debug((Object)sm.getString("userConfig.start"));
        }
        this.deploy();
    }

    private void stop() {
        if (this.host.getLogger().isDebugEnabled()) {
            this.host.getLogger().debug((Object)sm.getString("userConfig.stop"));
        }
    }

    private boolean isDeployAllowed(String user) {
        if (this.deny != null && this.deny.matcher(user).matches()) {
            return false;
        }
        if (this.allow != null) {
            return this.allow.matcher(user).matches();
        }
        return true;
    }

    private static class DeployUserDirectory
    implements Runnable {
        private UserConfig config;
        private String user;
        private String home;

        DeployUserDirectory(UserConfig config, String user, String home) {
            this.config = config;
            this.user = user;
            this.home = home;
        }

        @Override
        public void run() {
            this.config.deploy(this.user, this.home);
        }
    }
}

