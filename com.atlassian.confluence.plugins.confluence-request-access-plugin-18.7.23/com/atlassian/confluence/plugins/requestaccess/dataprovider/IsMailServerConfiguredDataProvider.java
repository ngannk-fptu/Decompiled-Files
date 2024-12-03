/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.wrapped.JsonableBoolean
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.requestaccess.dataprovider;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.json.marshal.wrapped.JsonableBoolean;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IsMailServerConfiguredDataProvider
implements WebResourceDataProvider {
    private final MailServerManager mailServerManager;

    @Autowired
    public IsMailServerConfiguredDataProvider(MailServerManager mailServerManager) {
        this.mailServerManager = mailServerManager;
    }

    public Jsonable get() {
        return new JsonableBoolean(Boolean.valueOf(this.mailServerManager.isDefaultSMTPMailServerDefined()));
    }
}

