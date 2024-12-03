/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.remoting.davex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.server.remoting.davex.ProtectedItemRemoveHandler;
import org.apache.jackrabbit.server.remoting.davex.ProtectedRemoveConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ProtectedRemoveManager {
    private static final Logger log = LoggerFactory.getLogger(ProtectedRemoveManager.class);
    private List<ProtectedItemRemoveHandler> handlers = new ArrayList<ProtectedItemRemoveHandler>();

    ProtectedRemoveManager() {
    }

    ProtectedRemoveManager(String config) throws IOException {
        if (config == null) {
            log.warn("protectedhandlers-config is missing -> DIFF processing can fail for the Remove operation if the content toremove is protected!");
        } else {
            File file = new File(config);
            if (file.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    this.load(fis);
                }
                catch (FileNotFoundException e) {
                    throw new IOException(e.getMessage(), e);
                }
            } else if (!config.isEmpty()) {
                ProtectedItemRemoveHandler handler = this.createHandler(config);
                this.addHandler(handler);
            } else {
                log.debug("Fail to locate the protected-item-remove-handler properties file.");
            }
        }
    }

    void load(InputStream fis) throws IOException {
        ProtectedRemoveConfig prConfig = new ProtectedRemoveConfig(this);
        prConfig.parse(fis);
    }

    boolean remove(Session session, String itemPath) throws RepositoryException {
        for (ProtectedItemRemoveHandler handler : this.handlers) {
            if (!handler.remove(session, itemPath)) continue;
            return true;
        }
        return false;
    }

    ProtectedItemRemoveHandler createHandler(String className) {
        ProtectedItemRemoveHandler irHandler = null;
        try {
            Class<?> irHandlerClass;
            if (!className.isEmpty() && ProtectedItemRemoveHandler.class.isAssignableFrom(irHandlerClass = Class.forName(className))) {
                irHandler = (ProtectedItemRemoveHandler)irHandlerClass.newInstance();
            }
        }
        catch (ClassNotFoundException e) {
            log.error(e.getMessage(), (Throwable)e);
        }
        catch (InstantiationException e) {
            log.error(e.getMessage(), (Throwable)e);
        }
        catch (IllegalAccessException e) {
            log.error(e.getMessage(), (Throwable)e);
        }
        return irHandler;
    }

    void addHandler(ProtectedItemRemoveHandler instance) {
        if (instance != null) {
            this.handlers.add(instance);
        }
    }
}

