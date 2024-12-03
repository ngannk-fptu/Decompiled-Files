/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.SavableAttachment
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.service;

import com.atlassian.confluence.pages.SavableAttachment;
import com.atlassian.confluence.plugins.hipchat.emoticons.content.entity.CustomEmoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.exception.EmoticonException;
import java.util.Collection;
import java.util.Map;

public interface CustomEmoticonService {
    public CustomEmoticon create(CustomEmoticon var1, SavableAttachment var2) throws EmoticonException;

    public Iterable<CustomEmoticon> list();

    public Collection<CustomEmoticon> findByShortcut(String ... var1);

    public Map<String, Long> findIDByShortcut(String ... var1);

    public void delete(String var1);

    public void cleanupInvalidEmoticon();
}

