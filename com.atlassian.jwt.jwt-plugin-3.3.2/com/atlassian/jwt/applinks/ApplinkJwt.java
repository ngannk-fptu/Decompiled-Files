/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 */
package com.atlassian.jwt.applinks;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.jwt.Jwt;

@Deprecated
public interface ApplinkJwt {
    public Jwt getJwt();

    public ApplicationLink getPeer();
}

