/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.spi.application.NonAppLinksApplicationType
 *  com.atlassian.applinks.spi.application.TypeId
 */
package com.atlassian.applinks.application.subversion;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.spi.application.NonAppLinksApplicationType;
import com.atlassian.applinks.spi.application.TypeId;
import java.net.URI;

@Deprecated
public class SubversionApplicationType
implements ApplicationType,
NonAppLinksApplicationType {
    static final TypeId TYPE_ID = new TypeId("subversion");

    public TypeId getId() {
        return TYPE_ID;
    }

    public String getI18nKey() {
        return "applinks.subversion";
    }

    public URI getIconUrl() {
        return null;
    }
}

