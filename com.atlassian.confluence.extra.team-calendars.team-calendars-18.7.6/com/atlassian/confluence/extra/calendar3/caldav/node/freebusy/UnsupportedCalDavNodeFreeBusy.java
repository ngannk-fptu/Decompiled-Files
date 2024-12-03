/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.node.freebusy;

import com.atlassian.confluence.extra.calendar3.caldav.node.freebusy.CalDavNodeFreeBusySupport;
import java.util.Set;

public class UnsupportedCalDavNodeFreeBusy
implements CalDavNodeFreeBusySupport {
    @Override
    public void setAffectsFreeBusy(boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getAffectsFreeBusy() {
        return false;
    }

    @Override
    public boolean freeBusyAllowed() {
        return false;
    }

    @Override
    public Set<String> getAttendeeUris() {
        throw new UnsupportedOperationException();
    }
}

