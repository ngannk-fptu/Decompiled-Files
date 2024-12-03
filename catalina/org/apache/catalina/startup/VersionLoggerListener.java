/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.startup;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.util.ServerInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class VersionLoggerListener
implements LifecycleListener {
    private static final Log log = LogFactory.getLog(VersionLoggerListener.class);
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.startup");
    private boolean logArgs = true;
    private boolean logEnv = false;
    private boolean logProps = false;

    public boolean getLogArgs() {
        return this.logArgs;
    }

    public void setLogArgs(boolean logArgs) {
        this.logArgs = logArgs;
    }

    public boolean getLogEnv() {
        return this.logEnv;
    }

    public void setLogEnv(boolean logEnv) {
        this.logEnv = logEnv;
    }

    public boolean getLogProps() {
        return this.logProps;
    }

    public void setLogProps(boolean logProps) {
        this.logProps = logProps;
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        if ("before_init".equals(event.getType())) {
            if (!(event.getLifecycle() instanceof Server)) {
                log.warn((Object)sm.getString("listener.notServer", new Object[]{event.getLifecycle().getClass().getSimpleName()}));
            }
            this.log();
        }
    }

    private void log() {
        TreeMap<String, String> sortedMap;
        log.info((Object)sm.getString("versionLoggerListener.serverInfo.server.version", new Object[]{ServerInfo.getServerInfo()}));
        log.info((Object)sm.getString("versionLoggerListener.serverInfo.server.built", new Object[]{ServerInfo.getServerBuilt()}));
        log.info((Object)sm.getString("versionLoggerListener.serverInfo.server.number", new Object[]{ServerInfo.getServerNumber()}));
        log.info((Object)sm.getString("versionLoggerListener.os.name", new Object[]{System.getProperty("os.name")}));
        log.info((Object)sm.getString("versionLoggerListener.os.version", new Object[]{System.getProperty("os.version")}));
        log.info((Object)sm.getString("versionLoggerListener.os.arch", new Object[]{System.getProperty("os.arch")}));
        log.info((Object)sm.getString("versionLoggerListener.java.home", new Object[]{System.getProperty("java.home")}));
        log.info((Object)sm.getString("versionLoggerListener.vm.version", new Object[]{System.getProperty("java.runtime.version")}));
        log.info((Object)sm.getString("versionLoggerListener.vm.vendor", new Object[]{System.getProperty("java.vm.vendor")}));
        log.info((Object)sm.getString("versionLoggerListener.catalina.base", new Object[]{System.getProperty("catalina.base")}));
        log.info((Object)sm.getString("versionLoggerListener.catalina.home", new Object[]{System.getProperty("catalina.home")}));
        if (this.logArgs) {
            List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
            for (String string : args) {
                log.info((Object)sm.getString("versionLoggerListener.arg", new Object[]{string}));
            }
        }
        if (this.logEnv) {
            sortedMap = new TreeMap<String, String>(System.getenv());
            for (Map.Entry entry : sortedMap.entrySet()) {
                log.info((Object)sm.getString("versionLoggerListener.env", new Object[]{entry.getKey(), entry.getValue()}));
            }
        }
        if (this.logProps) {
            sortedMap = new TreeMap();
            for (Map.Entry entry : System.getProperties().entrySet()) {
                sortedMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
            for (Map.Entry entry : sortedMap.entrySet()) {
                log.info((Object)sm.getString("versionLoggerListener.prop", new Object[]{entry.getKey(), entry.getValue()}));
            }
        }
    }
}

