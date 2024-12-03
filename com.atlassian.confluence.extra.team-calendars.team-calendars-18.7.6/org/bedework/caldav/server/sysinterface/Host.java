/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server.sysinterface;

import java.util.List;

public interface Host {
    public String getHostname();

    public int getPort();

    public boolean getSecure();

    public boolean getLocalService();

    public String getCaldavUrl();

    public String getCaldavPrincipal();

    public String getCaldavCredentials();

    public String getIScheduleUrl();

    public String getISchedulePrincipal();

    public String getIScheduleCredentials();

    public List<String> getDkimPublicKeys();

    public boolean getIScheduleUsePublicKey();

    public String getFbUrl();

    public boolean getSupportsBedework();

    public boolean getSupportsCaldav();

    public boolean getSupportsISchedule();

    public boolean getSupportsFreebusy();
}

