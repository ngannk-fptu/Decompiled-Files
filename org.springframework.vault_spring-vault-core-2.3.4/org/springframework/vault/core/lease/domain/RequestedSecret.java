/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.vault.core.lease.domain;

import org.springframework.util.Assert;

public class RequestedSecret {
    private final String path;
    private final Mode mode;

    private RequestedSecret(String path, Mode mode) {
        Assert.hasText((String)path, (String)"Path must not be null or empty");
        Assert.isTrue((!path.startsWith("/") ? 1 : 0) != 0, (String)"Path name must not start with a slash (/)");
        this.path = path;
        this.mode = mode;
    }

    public static RequestedSecret renewable(String path) {
        return new RequestedSecret(path, Mode.RENEW);
    }

    public static RequestedSecret rotating(String path) {
        return new RequestedSecret(path, Mode.ROTATE);
    }

    public static RequestedSecret from(Mode mode, String path) {
        Assert.notNull((Object)((Object)mode), (String)"Mode must not be null");
        return mode == Mode.ROTATE ? RequestedSecret.rotating(path) : RequestedSecret.renewable(path);
    }

    public String getPath() {
        return this.path;
    }

    public Mode getMode() {
        return this.mode;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestedSecret)) {
            return false;
        }
        RequestedSecret that = (RequestedSecret)o;
        if (!this.path.equals(that.path)) {
            return false;
        }
        return this.mode == that.mode;
    }

    public int hashCode() {
        int result = this.path.hashCode();
        result = 31 * result + this.mode.hashCode();
        return result;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getSimpleName());
        sb.append(" [path='").append(this.path).append('\'');
        sb.append(", mode=").append((Object)this.mode);
        sb.append(']');
        return sb.toString();
    }

    public static enum Mode {
        RENEW,
        ROTATE;

    }
}

