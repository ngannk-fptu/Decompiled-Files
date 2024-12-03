/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package org.osgi.service.blueprint.container;

import org.osgi.framework.Bundle;

public class BlueprintEvent {
    public static final int CREATING = 1;
    public static final int CREATED = 2;
    public static final int DESTROYING = 3;
    public static final int DESTROYED = 4;
    public static final int FAILURE = 5;
    public static final int GRACE_PERIOD = 6;
    public static final int WAITING = 7;
    private final int type;
    private final long timestamp;
    private final Bundle bundle;
    private final Bundle extenderBundle;
    private final String[] dependencies;
    private final Throwable cause;
    private final boolean replay;

    public BlueprintEvent(int type, Bundle bundle, Bundle extenderBundle) {
        this(type, bundle, extenderBundle, null, null);
    }

    public BlueprintEvent(int type, Bundle bundle, Bundle extenderBundle, String[] dependencies) {
        this(type, bundle, extenderBundle, dependencies, null);
    }

    public BlueprintEvent(int type, Bundle bundle, Bundle extenderBundle, Throwable cause) {
        this(type, bundle, extenderBundle, null, cause);
    }

    public BlueprintEvent(int type, Bundle bundle, Bundle extenderBundle, String[] dependencies, Throwable cause) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.bundle = bundle;
        this.extenderBundle = extenderBundle;
        this.dependencies = dependencies;
        this.cause = cause;
        this.replay = false;
        if (bundle == null) {
            throw new NullPointerException("bundle must not be null");
        }
        if (extenderBundle == null) {
            throw new NullPointerException("extenderBundle must not be null");
        }
        switch (type) {
            case 6: 
            case 7: {
                if (dependencies == null) {
                    throw new NullPointerException("dependencies must not be null");
                }
                if (dependencies.length != 0) break;
                throw new IllegalArgumentException("dependencies must not be length zero");
            }
            case 5: {
                if (dependencies == null || dependencies.length != 0) break;
                throw new IllegalArgumentException("dependencies must not be length zero");
            }
            default: {
                if (dependencies == null) break;
                throw new IllegalArgumentException("dependencies must be null");
            }
        }
    }

    public BlueprintEvent(BlueprintEvent event, boolean replay) {
        this.type = event.type;
        this.timestamp = event.timestamp;
        this.bundle = event.bundle;
        this.extenderBundle = event.extenderBundle;
        this.dependencies = event.dependencies;
        this.cause = event.cause;
        this.replay = replay;
    }

    public int getType() {
        return this.type;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public Bundle getBundle() {
        return this.bundle;
    }

    public Bundle getExtenderBundle() {
        return this.extenderBundle;
    }

    public String[] getDependencies() {
        return this.dependencies == null ? null : (String[])this.dependencies.clone();
    }

    public Throwable getCause() {
        return this.cause;
    }

    public boolean isReplay() {
        return this.replay;
    }
}

