/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId
 */
package com.atlassian.gadgets.directory.internal;

import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId;

public interface DirectoryUrlBuilder {
    public String buildDirectoryResourceUrl();

    public String buildDirectoryGadgetResourceUrl(ExternalGadgetSpecId var1);

    public String buildSubscribedGadgetFeedsUrl();

    public String buildSubscribedGadgetFeedUrl(String var1);
}

