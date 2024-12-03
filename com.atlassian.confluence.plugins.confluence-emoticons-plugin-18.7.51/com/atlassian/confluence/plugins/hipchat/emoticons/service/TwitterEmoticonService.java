/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.service;

import com.atlassian.confluence.plugins.hipchat.emoticons.rest.AtlaskitEmoticonModel;
import java.util.Collection;

public interface TwitterEmoticonService {
    public String getImageFileContent(AtlaskitEmoticonModel var1);

    public Collection<AtlaskitEmoticonModel> list();

    public AtlaskitEmoticonModel findById(String var1);

    public String getResourceUrl(AtlaskitEmoticonModel var1);
}

