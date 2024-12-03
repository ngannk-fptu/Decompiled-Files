/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 */
package com.atlassian.confluence.impl.hibernate.bulk;

import com.atlassian.core.util.ProgressMeter;

public interface BulkActionReportAware {
    public void report(ProgressMeter var1, int var2, int var3, int var4);
}

