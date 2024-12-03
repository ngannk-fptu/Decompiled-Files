/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

import com.atlassian.upm.core.VersionAwareHostApplicationInformation;

public interface UpmHostApplicationInformation
extends VersionAwareHostApplicationInformation {
    public boolean isHostDataCenterEnabled();

    public AuiCapabilities getAuiCapabilities();

    public static class AuiCapabilities {
        private final boolean dialog2Available;

        public AuiCapabilities(boolean dialog2Available) {
            this.dialog2Available = dialog2Available;
        }

        public boolean isDialog2Available() {
            return this.dialog2Available;
        }
    }
}

