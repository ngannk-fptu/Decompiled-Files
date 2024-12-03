/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.ApplicationVersionSpecifier;
import com.atlassian.marketplace.client.api.ApplicationVersionsQuery;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.model.Application;
import com.atlassian.marketplace.client.model.ApplicationVersion;
import java.util.Optional;

public interface Applications {
    public Optional<Application> safeGetByKey(ApplicationKey var1) throws MpacException;

    public Optional<ApplicationVersion> safeGetVersion(ApplicationKey var1, ApplicationVersionSpecifier var2) throws MpacException;

    public Page<ApplicationVersion> getVersions(ApplicationKey var1, ApplicationVersionsQuery var2) throws MpacException;

    public ApplicationVersion createVersion(ApplicationKey var1, ApplicationVersion var2) throws MpacException;

    public ApplicationVersion updateVersion(ApplicationVersion var1, ApplicationVersion var2) throws MpacException;
}

