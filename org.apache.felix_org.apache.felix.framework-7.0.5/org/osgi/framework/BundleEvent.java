/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework;

import java.util.EventObject;
import org.osgi.framework.Bundle;

public class BundleEvent
extends EventObject {
    static final long serialVersionUID = 4080640865971756012L;
    private final Bundle bundle;
    private final int type;
    public static final int INSTALLED = 1;
    public static final int STARTED = 2;
    public static final int STOPPED = 4;
    public static final int UPDATED = 8;
    public static final int UNINSTALLED = 16;
    public static final int RESOLVED = 32;
    public static final int UNRESOLVED = 64;
    public static final int STARTING = 128;
    public static final int STOPPING = 256;
    public static final int LAZY_ACTIVATION = 512;
    private final Bundle origin;

    public BundleEvent(int type, Bundle bundle, Bundle origin) {
        super(bundle);
        if (origin == null) {
            throw new IllegalArgumentException("null origin");
        }
        this.bundle = bundle;
        this.type = type;
        this.origin = origin;
    }

    public BundleEvent(int type, Bundle bundle) {
        super(bundle);
        this.bundle = bundle;
        this.type = type;
        this.origin = bundle;
    }

    public Bundle getBundle() {
        return this.bundle;
    }

    public int getType() {
        return this.type;
    }

    public Bundle getOrigin() {
        return this.origin;
    }
}

