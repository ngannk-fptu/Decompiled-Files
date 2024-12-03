/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.caldav;

import com.atlassian.confluence.user.ConfluenceUser;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import javax.annotation.Nonnull;
import org.bedework.access.AccessPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CalendarAccessPrincipal
implements AccessPrincipal {
    private static final Logger log = LoggerFactory.getLogger(CalendarAccessPrincipal.class);
    private final ConfluenceUser confluenceUser;

    public CalendarAccessPrincipal(@Nonnull ConfluenceUser confluenceUser) {
        this.confluenceUser = confluenceUser;
    }

    @Override
    public int getKind() {
        return 1;
    }

    @Override
    public void setUnauthenticated(boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getUnauthenticated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAccount(String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAccount() {
        return this.confluenceUser.getName();
    }

    @Override
    public String getAclAccount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPrincipalRef(String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPrincipalRef() {
        return String.format("/principals/users/%s", this.urlEncode(this.confluenceUser.getName()));
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException exception) {
            log.error(exception.getMessage(), (Throwable)exception);
            return value;
        }
    }

    @Override
    public void setGroupNames(Collection<String> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getGroupNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDescription(String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException();
    }
}

