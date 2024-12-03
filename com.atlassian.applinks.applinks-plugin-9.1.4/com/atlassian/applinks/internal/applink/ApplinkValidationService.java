/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.spi.link.ApplicationLinkDetails
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.applink;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.exception.ValidationException;
import com.atlassian.applinks.internal.common.permission.Unrestricted;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import javax.annotation.Nonnull;

@Unrestricted(value="This is a non-mutating API and hence is safe to use by anyone. Clients using this API should enforce appropriate permission levels suitable for their use case.")
public interface ApplinkValidationService {
    public void validateUpdate(@Nonnull ApplicationId var1, @Nonnull ApplicationLinkDetails var2) throws ValidationException, NoSuchApplinkException;

    public void validateUpdate(@Nonnull ReadOnlyApplicationLink var1, @Nonnull ApplicationLinkDetails var2) throws ValidationException;
}

