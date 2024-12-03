/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.attachments.lifecycle.impl;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VMShutdownHook
extends Thread {
    private static final Log log = LogFactory.getLog(VMShutdownHook.class);
    private static VMShutdownHook instance = null;
    private static Set files = Collections.synchronizedSet(new HashSet());
    private boolean isRegistered = false;

    static VMShutdownHook hook() {
        if (instance == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"creating VMShutdownHook");
            }
            instance = new VMShutdownHook();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"returning VMShutdownHook instance");
        }
        return instance;
    }

    private VMShutdownHook() {
    }

    void remove(File file) {
        if (file == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Removing File to Shutdown Hook Collection");
        }
        files.remove(file);
    }

    void add(File file) {
        if (file == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Adding File to Shutdown Hook Collection");
        }
        files.add(file);
    }

    public void run() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"JVM running VM Shutdown Hook");
        }
        for (File file : files) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Deleting File from Shutdown Hook Collection" + file.getAbsolutePath()));
            }
            file.delete();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"JVM Done running VM Shutdown Hook");
        }
    }

    public boolean isRegistered() {
        if (log.isDebugEnabled()) {
            if (!this.isRegistered) {
                log.debug((Object)"hook isRegistered= false");
            } else {
                log.debug((Object)"hook isRegistered= true");
            }
        }
        return this.isRegistered;
    }

    public void setRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }
}

