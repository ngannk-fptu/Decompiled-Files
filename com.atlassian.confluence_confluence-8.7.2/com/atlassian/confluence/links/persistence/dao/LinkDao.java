/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.ObjectDao
 */
package com.atlassian.confluence.links.persistence.dao;

import bucket.core.persistence.ObjectDao;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.links.OutgoingLink;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface LinkDao
extends ObjectDao {
    public <T> Stream<T> countIncomingLinksForContents(SpaceContentEntityObject var1, SpaceContentEntityObject var2, Function<Object, T> var3);

    public int countPagesWithIncomingLinks(SpaceContentEntityObject var1);

    public List<OutgoingLink> getLinksTo(ContentEntityObject var1);

    public List<ContentEntityObject> getReferringContent(ContentEntityObject var1);

    public List<ContentEntityObject> getReferringContent(String var1, List<ContentEntityObject> var2);

    public void removeCorruptOutgoingLinks();
}

