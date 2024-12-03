/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictiveness
 *  javax.annotation.concurrent.Immutable
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.whitelist.ui;

import com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictiveness;
import javax.annotation.concurrent.Immutable;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect
@Immutable
public class WhitelistSettingsBean {
    private final ApplicationLinkRestrictiveness applicationLinkRestrictiveness;

    @JsonCreator
    public WhitelistSettingsBean(@JsonProperty(value="applicationLinkRestrictiveness") ApplicationLinkRestrictiveness applicationLinkRestrictiveness) {
        this.applicationLinkRestrictiveness = applicationLinkRestrictiveness;
    }

    public ApplicationLinkRestrictiveness getApplicationLinkRestrictiveness() {
        return this.applicationLinkRestrictiveness;
    }
}

