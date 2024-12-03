/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.action.impl;

import com.atlassian.troubleshooting.stp.action.AbstractSupportToolsAction;
import com.atlassian.troubleshooting.stp.action.SupportToolsAction;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import javax.annotation.Nonnull;

public class TabsAction
extends AbstractSupportToolsAction {
    public static final String ACTION_NAME = "tabs";
    private final SupportApplicationInfo info;

    public TabsAction(SupportApplicationInfo info) {
        super(ACTION_NAME, null, null, "templates/html/tabs.vm");
        this.info = info;
    }

    @Override
    @Nonnull
    public SupportToolsAction newInstance() {
        return new TabsAction(this.info);
    }

    public String getApplicationName() {
        return this.info.getApplicationName();
    }

    public String getApplicationVersion() {
        return this.info.getApplicationVersion();
    }
}

