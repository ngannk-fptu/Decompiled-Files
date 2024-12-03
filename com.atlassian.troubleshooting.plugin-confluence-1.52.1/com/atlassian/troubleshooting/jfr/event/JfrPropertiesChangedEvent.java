/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.event;

import java.util.Objects;

public class JfrPropertiesChangedEvent {
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && this.getClass() == o.getClass();
    }

    public int hashCode() {
        return Objects.hash(new Object[0]);
    }
}

