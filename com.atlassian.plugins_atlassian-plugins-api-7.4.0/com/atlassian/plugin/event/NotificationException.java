/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.event;

import com.atlassian.plugin.PluginException;
import java.util.Collections;
import java.util.List;

public class NotificationException
extends PluginException {
    private final List<Throwable> allCauses;

    public NotificationException(Throwable cause) {
        super(cause);
        this.allCauses = Collections.singletonList(cause);
    }

    public NotificationException(List<Throwable> causes) {
        super(causes.get(0));
        this.allCauses = Collections.unmodifiableList(causes);
    }

    public List<Throwable> getAllCauses() {
        return this.allCauses;
    }
}

