/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework;

import java.util.EventObject;
import org.osgi.framework.Bundle;

public class FrameworkEvent
extends EventObject {
    static final long serialVersionUID = 207051004521261705L;
    private final Bundle bundle;
    private final Throwable throwable;
    private final int type;
    public static final int STARTED = 1;
    public static final int ERROR = 2;
    public static final int PACKAGES_REFRESHED = 4;
    public static final int STARTLEVEL_CHANGED = 8;
    public static final int WARNING = 16;
    public static final int INFO = 32;
    public static final int STOPPED = 64;
    public static final int STOPPED_UPDATE = 128;
    public static final int STOPPED_BOOTCLASSPATH_MODIFIED = 256;
    public static final int WAIT_TIMEDOUT = 512;
    public static final int STOPPED_SYSTEM_REFRESHED = 1024;

    public FrameworkEvent(int type, Object source) {
        super(source);
        this.type = type;
        this.bundle = null;
        this.throwable = null;
    }

    public FrameworkEvent(int type, Bundle bundle, Throwable throwable) {
        super(bundle);
        this.type = type;
        this.bundle = bundle;
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public Bundle getBundle() {
        return this.bundle;
    }

    public int getType() {
        return this.type;
    }
}

