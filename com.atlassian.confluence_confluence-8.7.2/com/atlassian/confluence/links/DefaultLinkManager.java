/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.LinkResolver
 *  com.atlassian.renderer.v2.macro.MacroManager
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.links;

import com.atlassian.confluence.content.render.xhtml.links.AttachmentLinksUpdater;
import com.atlassian.confluence.content.render.xhtml.links.LinksUpdater;
import com.atlassian.confluence.content.render.xhtml.links.OutgoingLinksExtractor;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.links.LinkManager;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.links.OutgoingLinkMeta;
import com.atlassian.confluence.links.persistence.dao.LinkDao;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.renderer.links.LinkResolver;
import com.atlassian.renderer.v2.macro.MacroManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLinkManager
implements LinkManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultLinkManager.class);
    private LinkDao linkDao;
    private OutgoingLinksExtractor outgoingLinksExtractor;

    public void setLinkDao(LinkDao linkDao) {
        this.linkDao = linkDao;
    }

    @Deprecated
    public void setLinkResolver(LinkResolver linkResolver) {
    }

    @Override
    public void removeLink(OutgoingLink link) {
        link.getSourceContent().removeOutgoingLink(link);
        this.linkDao.remove(link);
    }

    @Override
    public void saveLink(OutgoingLink link) {
        link.getSourceContent().addOutgoingLink(link);
        this.linkDao.save(link);
    }

    @Override
    public List<OutgoingLink> getIncomingLinksToContent(ContentEntityObject content) {
        List links = this.linkDao.getLinksTo(content);
        if (links != null) {
            links = links.stream().filter(link -> link.getSourceContent().getNameForComparison() != null).collect(Collectors.toCollection(ArrayList::new));
            links.sort((o1, o2) -> {
                int result;
                block2: {
                    result = 1;
                    try {
                        result = o1.getSourceContent().compareTo(o2.getSourceContent());
                    }
                    catch (ClassCastException e) {
                        if (!log.isDebugEnabled()) break block2;
                        log.debug("Error comparing " + o1 + " with " + o2, (Throwable)e);
                    }
                }
                return result;
            });
        }
        return links;
    }

    @Override
    public Stream<OutgoingLinkMeta> countIncomingLinksForContents(SpaceContentEntityObject rootPage, SpaceContentEntityObject parentPage) {
        return this.linkDao.countIncomingLinksForContents(rootPage, parentPage, dbObj -> {
            Object[] rawObj = (Object[])dbObj;
            return new OutgoingLinkMeta((Long)rawObj[0], (String)rawObj[1], (String)rawObj[2], ((Integer)rawObj[3]).intValue());
        });
    }

    @Override
    public int countPagesWithIncomingLinks(SpaceContentEntityObject rootPage) {
        return this.linkDao.countPagesWithIncomingLinks(rootPage);
    }

    @Override
    public void updateOutgoingLinks(ContentEntityObject content) {
        if (content == null) {
            return;
        }
        int n = content.getOutgoingLinks().size();
        for (int i = 0; i < n; ++i) {
            this.removeLink(content.getOutgoingLinks().get(0));
        }
        if (!content.isLatestVersion() && !content.isDraft()) {
            return;
        }
        Set<OutgoingLink> outgoingLinks = this.outgoingLinksExtractor.extract(content);
        for (OutgoingLink outgoingLink : outgoingLinks) {
            String url = outgoingLink.getDestinationPageTitle();
            if (StringUtils.isNotEmpty((CharSequence)url) && url.length() > 255) {
                outgoingLink.setDestinationPageTitle(url.substring(0, 255));
            }
            this.saveLink(outgoingLink);
        }
    }

    @Override
    public Collection<ContentEntityObject> getReferringContent(ContentEntityObject content) {
        return this.linkDao.getReferringContent(content);
    }

    @Override
    public Collection<ContentEntityObject> getReferringContent(String spaceKey, List<ContentEntityObject> collection) {
        return this.linkDao.getReferringContent(spaceKey, collection);
    }

    @Override
    public void removeCorruptOutgoingLinks() {
        this.linkDao.removeCorruptOutgoingLinks();
    }

    @Deprecated
    public void setSettingsManager(SettingsManager settingsManager) {
    }

    @Deprecated
    public void setMacroManager(MacroManager macroManager) {
    }

    public void setOutgoingLinksExtractor(OutgoingLinksExtractor outgoingLinksExtractor) {
        this.outgoingLinksExtractor = outgoingLinksExtractor;
    }

    @Deprecated
    public void setLinksUpdater(LinksUpdater linksUpdater) {
    }

    @Deprecated
    public void setAttachmentLinksUpdater(AttachmentLinksUpdater attachmentLinksUpdater) {
    }
}

