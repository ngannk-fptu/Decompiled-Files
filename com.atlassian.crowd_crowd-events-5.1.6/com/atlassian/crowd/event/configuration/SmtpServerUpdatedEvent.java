/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.mail.SMTPServer
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.event.configuration;

import com.atlassian.crowd.util.mail.SMTPServer;
import java.util.Objects;
import javax.annotation.Nullable;

public class SmtpServerUpdatedEvent {
    private final SMTPServer oldValue;
    private final SMTPServer newValue;

    public SmtpServerUpdatedEvent(@Nullable SMTPServer oldValue, @Nullable SMTPServer newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Nullable
    public SMTPServer getOldValue() {
        return this.oldValue;
    }

    @Nullable
    public SMTPServer getNewValue() {
        return this.newValue;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SmtpServerUpdatedEvent that = (SmtpServerUpdatedEvent)o;
        return Objects.equals(this.oldValue, that.oldValue) && Objects.equals(this.newValue, that.newValue);
    }

    public int hashCode() {
        return Objects.hash(this.oldValue, this.newValue);
    }
}

