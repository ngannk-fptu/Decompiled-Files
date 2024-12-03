/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.attachments;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class AttachmentCacheMonitor {
    static Log log = LogFactory.getLog((String)AttachmentCacheMonitor.class.getName());
    private int attachmentTimeoutSeconds;
    private int refreshSeconds;
    public static final String ATTACHMENT_TIMEOUT_PROPERTY = "org.apache.axiom.attachments.tempfile.expiration";
    private HashMap files;
    private Long priorDeleteMillis;
    private Timer timer;
    private static AttachmentCacheMonitor _singleton = null;

    public static synchronized AttachmentCacheMonitor getAttachmentCacheMonitor() {
        if (_singleton == null) {
            _singleton = new AttachmentCacheMonitor();
        }
        return _singleton;
    }

    private AttachmentCacheMonitor() {
        block4: {
            this.attachmentTimeoutSeconds = 0;
            this.refreshSeconds = 0;
            this.files = new HashMap();
            this.priorDeleteMillis = this.getTime();
            this.timer = null;
            String value = "";
            try {
                value = System.getProperty(ATTACHMENT_TIMEOUT_PROPERTY, "0");
                this.attachmentTimeoutSeconds = Integer.valueOf(value);
            }
            catch (Throwable t) {
                if (!log.isDebugEnabled()) break block4;
                log.debug((Object)("The value of " + value + " was not valid. The default " + this.attachmentTimeoutSeconds + " will be used instead."));
            }
        }
        this.refreshSeconds = this.attachmentTimeoutSeconds / 2;
        if (log.isDebugEnabled()) {
            log.debug((Object)"Custom Property Key =  org.apache.axiom.attachments.tempfile.expiration");
            log.debug((Object)("              Value = " + this.attachmentTimeoutSeconds));
        }
        if (this.refreshSeconds > 0) {
            this.timer = new Timer(true);
            this.timer.schedule((TimerTask)new CleanupFilesTask(), this.refreshSeconds * 1000, (long)(this.refreshSeconds * 1000));
        }
    }

    public synchronized int getTimeout() {
        return this.attachmentTimeoutSeconds;
    }

    public synchronized void setTimeout(int timeout) {
        if (timeout == this.attachmentTimeoutSeconds) {
            return;
        }
        this.attachmentTimeoutSeconds = timeout;
        this.refreshSeconds = this.attachmentTimeoutSeconds / 2;
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        if (this.refreshSeconds > 0) {
            this.timer = new Timer(true);
            this.timer.schedule((TimerTask)new CleanupFilesTask(), this.refreshSeconds * 1000, (long)(this.refreshSeconds * 1000));
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("New timeout = " + this.attachmentTimeoutSeconds));
            log.debug((Object)("New refresh = " + this.refreshSeconds));
        }
    }

    public void register(String fileName) {
        if (this.attachmentTimeoutSeconds > 0) {
            this._register(fileName);
            this._checkForAgedFiles();
        }
    }

    public void access(String fileName) {
        if (this.attachmentTimeoutSeconds > 0) {
            this._access(fileName);
            this._checkForAgedFiles();
        }
    }

    public void checkForAgedFiles() {
        if (this.attachmentTimeoutSeconds > 0) {
            this._checkForAgedFiles();
        }
    }

    private synchronized void _register(String fileName) {
        Long currentTime = this.getTime();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Register file " + fileName));
            log.debug((Object)("Time = " + currentTime));
        }
        this.files.put(fileName, currentTime);
    }

    private synchronized void _access(String fileName) {
        Long currentTime = this.getTime();
        Long priorTime = (Long)this.files.get(fileName);
        if (priorTime != null) {
            this.files.put(fileName, currentTime);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Access file " + fileName));
                log.debug((Object)("Old Time = " + priorTime));
                log.debug((Object)("New Time = " + currentTime));
            }
        } else if (log.isDebugEnabled()) {
            log.debug((Object)("The following file was already deleted and is no longer available: " + fileName));
            log.debug((Object)("The value of org.apache.axiom.attachments.tempfile.expiration is " + this.attachmentTimeoutSeconds));
        }
    }

    private synchronized void _checkForAgedFiles() {
        Long currentTime = this.getTime();
        if (this.isExpired(this.priorDeleteMillis, currentTime, this.refreshSeconds)) {
            Iterator it = this.files.keySet().iterator();
            while (it.hasNext()) {
                String fileName = (String)it.next();
                Long lastAccess = (Long)this.files.get(fileName);
                if (!this.isExpired(lastAccess, currentTime, this.attachmentTimeoutSeconds)) continue;
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Expired file " + fileName));
                    log.debug((Object)("Old Time = " + lastAccess));
                    log.debug((Object)("New Time = " + currentTime));
                    log.debug((Object)("Elapsed Time (ms) = " + (currentTime - lastAccess)));
                }
                this.deleteFile(fileName);
                it.remove();
            }
            this.priorDeleteMillis = currentTime;
        }
    }

    private boolean deleteFile(final String fileName) {
        Boolean privRet = (Boolean)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return AttachmentCacheMonitor.this._deleteFile(fileName);
            }
        });
        return privRet;
    }

    private Boolean _deleteFile(String fileName) {
        boolean ret = false;
        File file = new File(fileName);
        if (file.exists()) {
            ret = file.delete();
            if (log.isDebugEnabled()) {
                log.debug((Object)("Deletion Successful ? " + ret));
            }
        } else if (log.isDebugEnabled()) {
            log.debug((Object)("This file no longer exists = " + fileName));
        }
        return new Boolean(ret);
    }

    private Long getTime() {
        return new Long(System.currentTimeMillis());
    }

    private boolean isExpired(Long oldTimeMillis, Long newTimeMillis, int thresholdSecs) {
        long elapse = newTimeMillis - oldTimeMillis;
        return elapse > (long)(thresholdSecs * 1000);
    }

    private class CleanupFilesTask
    extends TimerTask {
        private CleanupFilesTask() {
        }

        public void run() {
            AttachmentCacheMonitor.this.checkForAgedFiles();
        }
    }
}

