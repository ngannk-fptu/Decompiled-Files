/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.action.impl;

import com.atlassian.troubleshooting.stp.ValidationLog;
import com.atlassian.troubleshooting.stp.action.AbstractSupportToolsAction;
import com.atlassian.troubleshooting.stp.action.SupportToolsAction;
import com.atlassian.troubleshooting.stp.properties.PropertyStore;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.servlet.SafeHttpServletRequest;
import com.atlassian.troubleshooting.stp.spi.SupportDataDetail;
import java.util.Map;
import javax.annotation.Nonnull;

public class SystemInfoAction
extends AbstractSupportToolsAction {
    private final SupportApplicationInfo info;

    public SystemInfoAction(SupportApplicationInfo info) {
        super("system-info", "stp.troubleshooting.title", "stp.system.info.tool.title", "templates/html/system-info.vm");
        this.info = info;
    }

    @Override
    public void prepare(Map<String, Object> context, SafeHttpServletRequest request, ValidationLog validationLog) {
        context.put("info", this.info);
        PropertyStore props = this.info.loadProperties(SupportDataDetail.DETAILED);
        context.put("props", props);
    }

    @Override
    @Nonnull
    public SupportToolsAction newInstance() {
        return new SystemInfoAction(this.info);
    }
}

