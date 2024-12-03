/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.caldav.configuration;

import org.bedework.caldav.server.sysinterface.CalDAVSystemProperties;
import org.springframework.stereotype.Component;

@Component
public class CalendarCalDAVSystemProperties
implements CalDAVSystemProperties {
    private String featureFlags;
    private String adminContact;
    private String tzServerUri;

    @Override
    public void setFeatureFlags(String featureFlags) {
        this.featureFlags = featureFlags;
    }

    @Override
    public String getFeatureFlags() {
        return this.featureFlags;
    }

    @Override
    public void setAdminContact(String adminContact) {
        this.adminContact = adminContact;
    }

    @Override
    public String getAdminContact() {
        return this.adminContact;
    }

    @Override
    public void setTzServeruri(String tzServerUri) {
        this.tzServerUri = tzServerUri;
    }

    @Override
    public String getTzServeruri() {
        return this.tzServerUri;
    }

    @Override
    public void setTimezonesByReference(boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getTimezonesByReference() {
        return true;
    }

    @Override
    public void setIscheduleURI(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getIscheduleURI() {
        return null;
    }

    @Override
    public void setFburlServiceURI(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFburlServiceURI() {
        return null;
    }

    @Override
    public void setWebcalServiceURI(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getWebcalServiceURI() {
        return null;
    }

    @Override
    public void setCalSoapWsURI(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCalSoapWsURI() {
        return null;
    }

    @Override
    public void setVpollMaxItems(Integer integer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getVpollMaxItems() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setVpollMaxActive(Integer integer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getVpollMaxActive() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setVpollMaxVoters(Integer integer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getVpollMaxVoters() {
        throw new UnsupportedOperationException();
    }
}

