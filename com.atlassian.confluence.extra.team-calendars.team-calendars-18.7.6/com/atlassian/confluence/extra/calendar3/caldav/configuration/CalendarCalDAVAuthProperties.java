/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.caldav.configuration;

import org.bedework.caldav.server.sysinterface.CalDAVAuthProperties;
import org.springframework.stereotype.Component;

@Component
public class CalendarCalDAVAuthProperties
implements CalDAVAuthProperties {
    @Override
    public void setMaxUserEntitySize(Integer integer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getMaxUserEntitySize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxInstances(Integer integer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getMaxInstances() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxAttendeesPerInstance(Integer integer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getMaxAttendeesPerInstance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMinDateTime(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMinDateTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxDateTime(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMaxDateTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDefaultFBPeriod(Integer integer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getDefaultFBPeriod() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxFBPeriod(Integer integer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getMaxFBPeriod() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDefaultWebCalPeriod(Integer integer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getDefaultWebCalPeriod() {
        return -1;
    }

    @Override
    public void setMaxWebCalPeriod(Integer integer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getMaxWebCalPeriod() {
        return -1;
    }

    @Override
    public void setDirectoryBrowsingDisallowed(boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getDirectoryBrowsingDisallowed() {
        return false;
    }
}

