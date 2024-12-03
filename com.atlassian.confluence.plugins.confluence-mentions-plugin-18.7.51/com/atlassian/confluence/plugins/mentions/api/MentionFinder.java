/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.BodyContent
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.mentions.api;

import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.ContentEntityObject;
import java.util.Set;

public interface MentionFinder {
    public Set<String> getMentionedUsernames(ContentEntityObject var1);

    public Set<String> getMentionedUsernames(BodyContent var1);

    public Set<String> getNewMentionedUsernames(BodyContent var1, BodyContent var2);
}

