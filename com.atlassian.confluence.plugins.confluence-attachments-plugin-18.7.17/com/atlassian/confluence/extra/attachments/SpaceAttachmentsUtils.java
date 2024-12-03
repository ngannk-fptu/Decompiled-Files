/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 */
package com.atlassian.confluence.extra.attachments;

import com.atlassian.confluence.extra.attachments.SpaceAttachments;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import java.util.Set;

public interface SpaceAttachmentsUtils {
    public static final int COUNT_ON_EACH_PAGE = 20;

    public SpaceAttachments getAttachmentList(String var1, int var2, int var3, int var4, String var5, String var6, Set<String> var7) throws InvalidSearchException;
}

