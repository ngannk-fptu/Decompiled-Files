/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.page.async.types;

public interface ConfluenceEntityUpdated {
    public Long getOriginalId();

    public Integer getOriginalVersion();

    public Long getCurrentId();

    public Integer getCurrentVersion();
}

