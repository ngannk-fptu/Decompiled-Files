/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.deploy;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.catalina.ha.deploy.FileChangeListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class WarWatcher {
    private static final Log log = LogFactory.getLog(WarWatcher.class);
    private static final StringManager sm = StringManager.getManager(WarWatcher.class);
    protected final File watchDir;
    protected final FileChangeListener listener;
    protected final Map<String, WarInfo> currentStatus = new HashMap<String, WarInfo>();

    public WarWatcher(FileChangeListener listener, File watchDir) {
        this.listener = listener;
        this.watchDir = watchDir;
    }

    public void check() {
        File[] list;
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("warWatcher.checkingWars", new Object[]{this.watchDir}));
        }
        if ((list = this.watchDir.listFiles(new WarFilter())) == null) {
            log.warn((Object)sm.getString("warWatcher.cantListWatchDir", new Object[]{this.watchDir}));
            list = new File[]{};
        }
        for (File file : list) {
            if (!file.exists()) {
                log.warn((Object)sm.getString("warWatcher.listedFileDoesNotExist", new Object[]{file, this.watchDir}));
            }
            this.addWarInfo(file);
        }
        Iterator<Map.Entry<String, WarInfo>> i = this.currentStatus.entrySet().iterator();
        while (i.hasNext()) {
            int check;
            Map.Entry<String, WarInfo> entry = i.next();
            WarInfo info = entry.getValue();
            if (log.isTraceEnabled()) {
                log.trace((Object)sm.getString("warWatcher.checkingWar", new Object[]{info.getWar()}));
            }
            if ((check = info.check()) == 1) {
                this.listener.fileModified(info.getWar());
            } else if (check == -1) {
                this.listener.fileRemoved(info.getWar());
                i.remove();
            }
            if (!log.isTraceEnabled()) continue;
            log.trace((Object)sm.getString("warWatcher.checkWarResult", new Object[]{check, info.getWar()}));
        }
    }

    protected void addWarInfo(File warfile) {
        WarInfo info = this.currentStatus.get(warfile.getAbsolutePath());
        if (info == null) {
            info = new WarInfo(warfile);
            info.setLastState(-1);
            this.currentStatus.put(warfile.getAbsolutePath(), info);
        }
    }

    public void clear() {
        this.currentStatus.clear();
    }

    protected static class WarFilter
    implements FilenameFilter {
        protected WarFilter() {
        }

        @Override
        public boolean accept(File path, String name) {
            if (name == null) {
                return false;
            }
            return name.endsWith(".war");
        }
    }

    protected static class WarInfo {
        protected final File war;
        protected long lastChecked = 0L;
        protected long lastState = 0L;

        public WarInfo(File war) {
            this.war = war;
            this.lastChecked = war.lastModified();
            if (!war.exists()) {
                this.lastState = -1L;
            }
        }

        public boolean modified() {
            return this.war.exists() && this.war.lastModified() > this.lastChecked;
        }

        public boolean exists() {
            return this.war.exists();
        }

        public int check() {
            int result = 0;
            if (this.modified()) {
                result = 1;
                this.lastState = result;
            } else if (!this.exists() && this.lastState != -1L) {
                result = -1;
                this.lastState = result;
            } else if (this.lastState == -1L && this.exists()) {
                result = 1;
                this.lastState = result;
            }
            this.lastChecked = System.currentTimeMillis();
            return result;
        }

        public File getWar() {
            return this.war;
        }

        public int hashCode() {
            return this.war.getAbsolutePath().hashCode();
        }

        public boolean equals(Object other) {
            if (other instanceof WarInfo) {
                WarInfo wo = (WarInfo)other;
                return wo.getWar().equals(this.getWar());
            }
            return false;
        }

        protected void setLastState(int lastState) {
            this.lastState = lastState;
        }
    }
}

