/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event.emailscan;

public class EmailScanFinishedSuccessfullyEvent {
    private final long invalidEmailAddressesCount;
    private final long duplicatedEmailAddressesCount;

    public EmailScanFinishedSuccessfullyEvent(long invalidEmailAddressesCount, long duplicatedEmailAddressesCount) {
        this.invalidEmailAddressesCount = invalidEmailAddressesCount;
        this.duplicatedEmailAddressesCount = duplicatedEmailAddressesCount;
    }

    public long getInvalidEmailAddressesCount() {
        return this.invalidEmailAddressesCount;
    }

    public long getDuplicatedEmailAddressesCount() {
        return this.duplicatedEmailAddressesCount;
    }
}

