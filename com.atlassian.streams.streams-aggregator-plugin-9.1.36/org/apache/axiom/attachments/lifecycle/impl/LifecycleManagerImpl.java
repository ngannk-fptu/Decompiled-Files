/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.attachments.lifecycle.impl;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.attachments.lifecycle.impl.FileAccessor;
import org.apache.axiom.attachments.lifecycle.impl.VMShutdownHook;
import org.apache.axiom.util.UIDGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LifecycleManagerImpl
implements LifecycleManager {
    private static final Log log = LogFactory.getLog(LifecycleManagerImpl.class);
    private static Hashtable table = new Hashtable();
    private VMShutdownHook hook = null;

    public FileAccessor create(String attachmentDir) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Start Create()");
        }
        File file = null;
        File dir = null;
        if (attachmentDir != null && !(dir = new File(attachmentDir)).exists()) {
            dir.mkdirs();
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Given Attachment File Cache Location " + dir + " should be a directory.");
        }
        String id = UIDGenerator.generateUID();
        String fileString = "axiom" + id + ".att";
        file = new File(dir, fileString);
        FileAccessor fa = new FileAccessor(this, file);
        table.put(fileString, fa);
        this.deleteOnExit(file);
        if (log.isDebugEnabled()) {
            log.debug((Object)"End Create()");
        }
        return fa;
    }

    public void delete(File file) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Start delete()");
        }
        if (file != null && file.exists()) {
            table.remove(file);
            if (log.isDebugEnabled()) {
                log.debug((Object)"invoking file.delete()");
            }
            if (file.delete()) {
                VMShutdownHook hook;
                if (log.isDebugEnabled()) {
                    log.debug((Object)"delete() successful");
                }
                if ((hook = VMShutdownHook.hook()).isRegistered()) {
                    hook.remove(file);
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)"File Purged and removed from Shutdown Hook Collection");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"Cannot delete file, set to delete on VM shutdown");
                }
                this.deleteOnExit(file);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"End delete()");
        }
    }

    public void deleteOnExit(File file) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Start deleteOnExit()");
        }
        if (this.hook == null) {
            this.hook = this.RegisterVMShutdownHook();
        }
        if (file != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Invoking deleteOnExit() for file = " + file.getAbsolutePath()));
            }
            this.hook.add(file);
            table.remove(file);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"End deleteOnExit()");
        }
    }

    public void deleteOnTimeInterval(int interval, File file) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Start deleteOnTimeInterval()");
        }
        Thread t = new Thread(new FileDeletor(interval, file));
        t.setDaemon(true);
        t.start();
        if (log.isDebugEnabled()) {
            log.debug((Object)"End deleteOnTimeInterval()");
        }
    }

    private VMShutdownHook RegisterVMShutdownHook() throws RuntimeException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Start RegisterVMShutdownHook()");
        }
        try {
            this.hook = (VMShutdownHook)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws SecurityException, IllegalStateException, IllegalArgumentException {
                    VMShutdownHook hook = VMShutdownHook.hook();
                    if (!hook.isRegistered()) {
                        Runtime.getRuntime().addShutdownHook(hook);
                        hook.setRegistered(true);
                    }
                    return hook;
                }
            });
        }
        catch (PrivilegedActionException e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Exception thrown from AccessController: " + e));
                log.debug((Object)"VM Shutdown Hook not registered.");
            }
            throw new RuntimeException(e);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit RegisterVMShutdownHook()");
        }
        return this.hook;
    }

    public FileAccessor getFileAccessor(String fileName) throws IOException {
        return (FileAccessor)table.get(fileName);
    }

    public class FileDeletor
    implements Runnable {
        int interval;
        File _file;

        public FileDeletor(int interval, File file) {
            this.interval = interval;
            this._file = file;
        }

        public void run() {
            block3: {
                try {
                    Thread.sleep(this.interval * 1000);
                    if (this._file.exists()) {
                        table.remove(this._file);
                        this._file.delete();
                    }
                }
                catch (InterruptedException e) {
                    if (!log.isDebugEnabled()) break block3;
                    log.warn((Object)("InterruptedException occured " + e.getMessage()));
                }
            }
        }
    }
}

