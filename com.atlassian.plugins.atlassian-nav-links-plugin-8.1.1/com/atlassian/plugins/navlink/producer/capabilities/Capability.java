/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Strings
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.plugins.navlink.producer.capabilities;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import javax.annotation.concurrent.Immutable;

@Immutable
public class Capability {
    private String type;
    private String name;
    private String url;

    public Capability(String type, String name, String url) {
        this.type = Strings.nullToEmpty((String)type);
        this.name = name;
        this.url = url;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.type, this.name, this.url});
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Capability)) {
            return false;
        }
        Capability that = (Capability)obj;
        return Objects.equal((Object)this.type, (Object)that.type) && Objects.equal((Object)this.name, (Object)that.name) && Objects.equal((Object)this.url, (Object)that.url);
    }

    public String toString() {
        return "Capability{type='" + this.type + '\'' + ", name='" + this.name + '\'' + ", url='" + this.url + '\'' + '}';
    }
}

