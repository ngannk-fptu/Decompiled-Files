/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.applinks.internal.web;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.exception.InvalidApplicationIdException;
import com.atlassian.applinks.internal.common.exception.InvalidRequestException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.permission.Unrestricted;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

@Unrestricted(value="Clients using this component are responsible for enforcing appropriate permissions")
public interface WebApplinkHelper {
    @Nonnull
    public ApplicationLink getApplicationLink(@Nonnull HttpServletRequest var1) throws InvalidRequestException, InvalidApplicationIdException, NoSuchApplinkException;
}

