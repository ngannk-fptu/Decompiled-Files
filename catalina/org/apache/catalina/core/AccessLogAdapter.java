/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.core;

import java.util.Arrays;
import java.util.Objects;
import org.apache.catalina.AccessLog;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

public class AccessLogAdapter
implements AccessLog {
    private AccessLog[] logs;

    public AccessLogAdapter(AccessLog log) {
        Objects.requireNonNull(log);
        this.logs = new AccessLog[]{log};
    }

    public void add(AccessLog log) {
        Objects.requireNonNull(log);
        AccessLog[] newArray = Arrays.copyOf(this.logs, this.logs.length + 1);
        newArray[newArray.length - 1] = log;
        this.logs = newArray;
    }

    @Override
    public void log(Request request, Response response, long time) {
        for (AccessLog log : this.logs) {
            log.log(request, response, time);
        }
    }

    @Override
    public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
    }

    @Override
    public boolean getRequestAttributesEnabled() {
        return false;
    }
}

