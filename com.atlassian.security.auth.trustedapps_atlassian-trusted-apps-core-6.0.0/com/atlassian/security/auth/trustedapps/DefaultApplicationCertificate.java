/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.ApplicationCertificate;
import com.atlassian.security.auth.trustedapps.Null;
import java.util.Date;

public class DefaultApplicationCertificate
implements ApplicationCertificate {
    private final String applicationID;
    private final String userName;
    private final long creationDate;
    private final Integer protocolVersion;

    public DefaultApplicationCertificate(String applicationID, String userName, long creationDate, Integer protocolVersion) {
        Null.not("applicationID", applicationID);
        Null.not("userName", userName);
        Null.not("protocolVersion", protocolVersion);
        this.applicationID = applicationID;
        this.userName = userName;
        this.creationDate = creationDate;
        this.protocolVersion = protocolVersion;
    }

    @Override
    public String getApplicationID() {
        return this.applicationID;
    }

    @Override
    public Date getCreationTime() {
        return new Date(this.creationDate);
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public Integer getProtocolVersion() {
        return this.protocolVersion;
    }
}

