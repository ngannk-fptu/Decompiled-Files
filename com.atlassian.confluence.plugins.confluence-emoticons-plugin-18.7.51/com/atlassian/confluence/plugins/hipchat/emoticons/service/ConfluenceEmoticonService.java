/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.service;

import com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.rest.AtlaskitEmoticonModel;
import java.util.List;
import java.util.Map;

public interface ConfluenceEmoticonService {
    public Map<Emoticon, String> list();

    public List<AtlaskitEmoticonModel> orderedList();
}

