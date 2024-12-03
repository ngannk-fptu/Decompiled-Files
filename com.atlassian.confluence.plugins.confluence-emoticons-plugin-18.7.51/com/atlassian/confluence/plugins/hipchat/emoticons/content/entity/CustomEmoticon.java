/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.content.entity;

import java.util.Date;

public interface CustomEmoticon {
    public long getId();

    public String getShortcut();

    public String getURL();

    public String getName();

    public String getCreatorUserId();

    public Date getCreatedDate();
}

