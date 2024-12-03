/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.http;

import java.util.Set;

public interface ComplianceViolation {
    public String getName();

    public String getURL();

    public String getDescription();

    default public boolean isAllowedBy(Mode mode) {
        return mode.allows(this);
    }

    public static interface Mode {
        public String getName();

        public boolean allows(ComplianceViolation var1);

        public Set<? extends ComplianceViolation> getKnown();

        public Set<? extends ComplianceViolation> getAllowed();
    }

    public static interface Listener {
        default public void onComplianceViolation(Mode mode, ComplianceViolation violation, String details) {
        }
    }
}

