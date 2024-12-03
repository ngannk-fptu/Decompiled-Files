/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.spi.link.MutableApplicationLink
 */
package com.atlassian.applinks.core.link;

import com.atlassian.applinks.spi.link.MutableApplicationLink;

public interface InternalApplicationLink
extends MutableApplicationLink {
    public void setPrimaryFlag(boolean var1);

    public void setSystem(boolean var1);
}

