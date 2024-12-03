/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.BackgroundProcess;

public class BackgroundProcessManager {
    private final Log log = LogFactory.getLog(BackgroundProcessManager.class);
    private static final StringManager sm = StringManager.getManager(BackgroundProcessManager.class);
    private static final BackgroundProcessManager instance = new BackgroundProcessManager();
    private final Set<BackgroundProcess> processes = new HashSet<BackgroundProcess>();
    private final Object processesLock = new Object();
    private WsBackgroundThread wsBackgroundThread = null;

    public static BackgroundProcessManager getInstance() {
        return instance;
    }

    private BackgroundProcessManager() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void register(BackgroundProcess process) {
        Object object = this.processesLock;
        synchronized (object) {
            if (this.processes.size() == 0) {
                this.wsBackgroundThread = new WsBackgroundThread(this);
                this.wsBackgroundThread.setContextClassLoader(this.getClass().getClassLoader());
                this.wsBackgroundThread.setDaemon(true);
                this.wsBackgroundThread.start();
            }
            this.processes.add(process);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unregister(BackgroundProcess process) {
        Object object = this.processesLock;
        synchronized (object) {
            this.processes.remove(process);
            if (this.wsBackgroundThread != null && this.processes.size() == 0) {
                this.wsBackgroundThread.halt();
                this.wsBackgroundThread = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void process() {
        HashSet<BackgroundProcess> currentProcesses;
        Iterator iterator = this.processesLock;
        synchronized (iterator) {
            currentProcesses = new HashSet<BackgroundProcess>(this.processes);
        }
        for (BackgroundProcess process : currentProcesses) {
            try {
                process.backgroundProcess();
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.log.error((Object)sm.getString("backgroundProcessManager.processFailed"), t);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int getProcessCount() {
        Object object = this.processesLock;
        synchronized (object) {
            return this.processes.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void shutdown() {
        Object object = this.processesLock;
        synchronized (object) {
            this.processes.clear();
            if (this.wsBackgroundThread != null) {
                this.wsBackgroundThread.halt();
                this.wsBackgroundThread = null;
            }
        }
    }

    private static class WsBackgroundThread
    extends Thread {
        private final BackgroundProcessManager manager;
        private volatile boolean running = true;

        WsBackgroundThread(BackgroundProcessManager manager) {
            this.setName("WebSocket background processing");
            this.manager = manager;
        }

        @Override
        public void run() {
            while (this.running) {
                try {
                    WsBackgroundThread.sleep(1000L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                this.manager.process();
            }
        }

        public void halt() {
            this.setName("WebSocket background processing - stopping");
            this.running = false;
        }
    }
}

