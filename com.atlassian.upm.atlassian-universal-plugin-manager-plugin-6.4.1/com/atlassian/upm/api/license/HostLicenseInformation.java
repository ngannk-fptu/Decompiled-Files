/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.license;

import com.atlassian.upm.api.util.Option;

public interface HostLicenseInformation {
    public Option<String> hostSen();

    public boolean isDataCenter();

    public Option<Integer> getEdition();

    public boolean isEvaluation();
}

