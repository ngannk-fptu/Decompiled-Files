/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.mail.internet.InternetAddress
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import java.util.List;
import javax.mail.internet.InternetAddress;

@PublicApi
public interface EmailContentParser {
    public String parseSubject(String var1);

    public List<InternetAddress> getEmailAddressesFromContent(String var1);

    public String parseContent(ReceivedEmail var1);
}

