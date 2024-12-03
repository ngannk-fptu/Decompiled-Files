/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.license.LicenseCountService
 *  com.google.common.base.Supplier
 */
package com.atlassian.upm.core.impl;

import com.atlassian.jira.license.LicenseCountService;
import com.atlassian.upm.core.impl.AbstractApplicationDescriptor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.google.common.base.Supplier;

public class JiraApplicationDescriptor
extends AbstractApplicationDescriptor {
    public JiraApplicationDescriptor(UpmAppManager upmAppManager, LicenseCountService licenseCountService) {
        super(upmAppManager, (Supplier<Integer>)((Supplier)() -> ((LicenseCountService)licenseCountService).totalBillableUsers()));
    }
}

