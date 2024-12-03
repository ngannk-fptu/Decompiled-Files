/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.container;

public class EventConstants {
    public static final String TYPE = "type";
    public static final String EVENT = "event";
    public static final String TIMESTAMP = "timestamp";
    public static final String BUNDLE = "bundle";
    public static final String BUNDLE_ID = "bundle.id";
    public static final String BUNDLE_SYMBOLICNAME = "bundle.symbolicName";
    public static final String BUNDLE_VERSION = "bundle.version";
    public static final String EXTENDER_BUNDLE = "extender.bundle";
    public static final String EXTENDER_BUNDLE_ID = "extender.bundle.id";
    public static final String EXTENDER_BUNDLE_SYMBOLICNAME = "extender.bundle.symbolicName";
    public static final String EXTENDER_BUNDLE_VERSION = "extender.bundle.version";
    public static final String DEPENDENCIES = "dependencies";
    public static final String CAUSE = "cause";
    public static final String TOPIC_BLUEPRINT_EVENTS = "org/osgi/service/blueprint/container";
    public static final String TOPIC_CREATING = "org/osgi/service/blueprint/container/CREATING";
    public static final String TOPIC_CREATED = "org/osgi/service/blueprint/container/CREATED";
    public static final String TOPIC_DESTROYING = "org/osgi/service/blueprint/container/DESTROYING";
    public static final String TOPIC_DESTROYED = "org/osgi/service/blueprint/container/DESTROYED";
    public static final String TOPIC_FAILURE = "org/osgi/service/blueprint/container/FAILURE";
    public static final String TOPIC_GRACE_PERIOD = "org/osgi/service/blueprint/container/GRACE_PERIOD";
    public static final String TOPIC_WAITING = "org/osgi/service/blueprint/container/WAITING";

    private EventConstants() {
    }
}

