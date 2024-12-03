/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import java.util.Date;

@Deprecated
public interface ApplicationCertificate {
    public Date getCreationTime();

    public String getUserName();

    public String getApplicationID();

    public Integer getProtocolVersion();
}

