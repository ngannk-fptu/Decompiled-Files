/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.upm.license.internal.HostLicenseEventReader;

public class DefaultHostLicenseEventReader
implements HostLicenseEventReader {
    @Override
    public boolean isHostLicenseUpdated(Object event) {
        return false;
    }
}

