/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.links;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.links.OutgoingLinkMeta;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface LinkManager {
    public static final String CAMELCASE_LINK_PATTERN = "([^a-zA-Z0-9!/\\[]|^)([A-Z])([a-z]+([A-Z][a-zA-Z0-9]+)+)(([^a-zA-Z0-9!\\]])|\r?\n|$)";
    public static final String NORMAL_LINK_PATTERN = "(\\[)([\\p{L}&[^\\[\\]\\p{Space}]][\\p{L}&[^\\[\\]]]*)\\]";

    public void saveLink(OutgoingLink var1);

    public void removeLink(OutgoingLink var1);

    @Transactional(readOnly=true)
    public List getIncomingLinksToContent(ContentEntityObject var1);

    @Transactional(readOnly=true)
    public Stream<OutgoingLinkMeta> countIncomingLinksForContents(SpaceContentEntityObject var1, SpaceContentEntityObject var2);

    @Transactional(readOnly=true)
    public int countPagesWithIncomingLinks(SpaceContentEntityObject var1);

    public void updateOutgoingLinks(ContentEntityObject var1);

    @Transactional(readOnly=true)
    public Collection<ContentEntityObject> getReferringContent(ContentEntityObject var1);

    @Transactional(readOnly=true)
    public Collection<ContentEntityObject> getReferringContent(String var1, List<ContentEntityObject> var2);

    public void removeCorruptOutgoingLinks();
}

