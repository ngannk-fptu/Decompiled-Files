/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.thready;

import com.atlassian.troubleshooting.stp.action.AbstractSupportToolsAction;
import com.atlassian.troubleshooting.stp.action.SupportToolsAction;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import java.util.Objects;
import javax.annotation.Nonnull;

public class DiagnosticSettingsAction
extends AbstractSupportToolsAction {
    public static final String ACTION_NAME = "diagnostic";
    private final SupportApplicationInfo applicationInfo;

    public DiagnosticSettingsAction(SupportApplicationInfo applicationInfo) {
        super(ACTION_NAME, "stp.troubleshooting.title", "stp.thready.tab.title", null);
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
    }

    @Override
    @Nonnull
    public SupportToolsAction newInstance() {
        return new DiagnosticSettingsAction(this.applicationInfo);
    }
}

