/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class JniLifecycleListener
implements LifecycleListener {
    private static final Log log = LogFactory.getLog(JniLifecycleListener.class);
    protected static final StringManager sm = StringManager.getManager(JniLifecycleListener.class);
    private String libraryName = "";
    private String libraryPath = "";

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        if ("before_start".equals(event.getType())) {
            if (!this.libraryName.isEmpty()) {
                System.loadLibrary(this.libraryName);
                log.info((Object)sm.getString("jniLifecycleListener.load.name", new Object[]{this.libraryName}));
            } else if (!this.libraryPath.isEmpty()) {
                System.load(this.libraryPath);
                log.info((Object)sm.getString("jniLifecycleListener.load.path", new Object[]{this.libraryPath}));
            } else {
                throw new IllegalArgumentException(sm.getString("jniLifecycleListener.missingPathOrName"));
            }
        }
    }

    public void setLibraryName(String libraryName) {
        if (!this.libraryPath.isEmpty()) {
            throw new IllegalArgumentException(sm.getString("jniLifecycleListener.bothPathAndName"));
        }
        this.libraryName = libraryName;
    }

    public String getLibraryName() {
        return this.libraryName;
    }

    public void setLibraryPath(String libraryPath) {
        if (!this.libraryName.isEmpty()) {
            throw new IllegalArgumentException(sm.getString("jniLifecycleListener.bothPathAndName"));
        }
        this.libraryPath = libraryPath;
    }

    public String getLibraryPath() {
        return this.libraryPath;
    }
}

