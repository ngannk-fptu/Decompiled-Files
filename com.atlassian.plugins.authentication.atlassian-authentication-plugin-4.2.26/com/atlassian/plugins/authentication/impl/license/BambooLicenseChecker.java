/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent
 */
package com.atlassian.plugins.authentication.impl.license;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugins.authentication.impl.license.ProductLicenseChecker;
import java.util.Set;

@BambooComponent
public class BambooLicenseChecker
implements ProductLicenseChecker {
    @Override
    public boolean areSlotsAvailable(Set<String> groupNames) {
        return true;
    }
}

