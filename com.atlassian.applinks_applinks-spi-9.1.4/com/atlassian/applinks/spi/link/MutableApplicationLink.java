/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 */
package com.atlassian.applinks.spi.link;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;

public interface MutableApplicationLink
extends ApplicationLink {
    public void update(ApplicationLinkDetails var1);
}

