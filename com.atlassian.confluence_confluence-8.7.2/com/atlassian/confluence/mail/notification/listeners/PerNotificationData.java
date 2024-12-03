/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.mail.notification.listeners;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

public class PerNotificationData {
    private List<List<String>> rightFooterLinks = new ArrayList<List<String>>();

    public void addRightFooterLink(String url, String labelKey) {
        this.rightFooterLinks.add((List<String>)ImmutableList.of((Object)url, (Object)labelKey));
    }

    public List<List<String>> getRightFooterLinks() {
        return ImmutableList.copyOf(this.rightFooterLinks);
    }
}

