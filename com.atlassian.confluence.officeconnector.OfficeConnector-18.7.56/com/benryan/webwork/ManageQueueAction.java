/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 */
package com.benryan.webwork;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import com.benryan.components.DefaultSlideCacheManager;
import com.benryan.components.SlideCacheManager;
import com.benryan.webwork.ManageQueueData;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

public class ManageQueueAction
extends ConfluenceActionSupport {
    private Set<ManageQueueData> beingConverted;
    private long attachmentId;
    private SlideCacheManager slideManager;
    private static final int HOUR_IN_MILLIS = 3600000;
    private static final int MINUTE_IN_MILLIS = 60000;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        this.beingConverted = new LinkedHashSet<ManageQueueData>();
        for (DefaultSlideCacheManager.QueueData taskData : this.slideManager.getBeingConvertedKeys()) {
            this.beingConverted.add(new ManageQueueData(taskData));
        }
        return "success";
    }

    public Collection<ManageQueueData> getBeingConverted() {
        return this.beingConverted;
    }

    public boolean isQueueEmpty() {
        return this.beingConverted != null && this.beingConverted.isEmpty();
    }

    public String getTimeDiff(Date date) {
        if (date == null) {
            return null;
        }
        long now = System.currentTimeMillis();
        int diff = (int)(now - date.getTime());
        String hours = String.valueOf(diff / 3600000);
        String minutes = String.valueOf((diff %= 3600000) / 60000);
        String seconds = String.valueOf((diff %= 60000) / 1000);
        return this.normalizeDigits(hours) + hours + ":" + this.normalizeDigits(minutes) + minutes + ":" + this.normalizeDigits(seconds) + seconds;
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String removeFromQueue() {
        this.slideManager.removeFromQueue(this.attachmentId);
        return "success";
    }

    public void setSlideCacheManager(SlideCacheManager manager) {
        this.slideManager = manager;
    }

    private String normalizeDigits(String unit) {
        return unit.length() == 1 ? "0" : "";
    }

    public void setAttachmentId(long attachmentId) {
        this.attachmentId = attachmentId;
    }
}

