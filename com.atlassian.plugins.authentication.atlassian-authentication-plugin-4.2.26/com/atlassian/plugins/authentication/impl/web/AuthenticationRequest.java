/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.web;

import java.io.Serializable;

public interface AuthenticationRequest
extends Serializable {
    public String getSessionDataKey();

    public String getPublicId();

    public String getLoginRequestUrl();
}

