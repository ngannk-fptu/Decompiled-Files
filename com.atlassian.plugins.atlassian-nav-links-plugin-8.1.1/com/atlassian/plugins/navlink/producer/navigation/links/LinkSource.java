/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.atlassian.plugins.navlink.producer.navigation.links;

import com.atlassian.plugins.navlink.producer.navigation.links.SourceType;
import com.google.common.base.Objects;

public abstract class LinkSource {
    private final String id;
    private final SourceType type;

    private LinkSource(String id, SourceType type) {
        this.id = id;
        this.type = type;
    }

    public static LinkSource local(String id) {
        return new Local(id);
    }

    public static LinkSource localDefault() {
        return Local.DEFAULT;
    }

    public static LinkSource remote(String id) {
        return new Remote(id);
    }

    public static LinkSource remoteDefault() {
        return Remote.DEFAULT;
    }

    public static LinkSource unknown() {
        return Unknown.INSTANCE;
    }

    public final String id() {
        return this.id;
    }

    public final SourceType type() {
        return this.type;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.id, this.type});
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LinkSource)) {
            return false;
        }
        LinkSource that = (LinkSource)obj;
        return Objects.equal((Object)this.id, (Object)that.id) && Objects.equal((Object)((Object)this.type), (Object)((Object)that.type));
    }

    public String toString() {
        return "LinkSource{id='" + this.id + '\'' + ", type=" + (Object)((Object)this.type) + '}';
    }

    private static final class Unknown
    extends LinkSource {
        private static final Unknown INSTANCE = new Unknown();

        private Unknown() {
            super(null, SourceType.UNKNOWN);
        }
    }

    private static final class Remote
    extends LinkSource {
        private static final Remote DEFAULT = new Remote(null);

        private Remote(String id) {
            super(id, SourceType.REMOTE);
        }
    }

    private static final class Local
    extends LinkSource {
        private static final Local DEFAULT = new Local(null);

        private Local(String id) {
            super(id, SourceType.LOCAL);
        }
    }
}

