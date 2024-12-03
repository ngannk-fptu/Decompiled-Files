/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.links;

import com.atlassian.confluence.content.render.xhtml.links.LinksUpdater;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.links.LinkManager;
import com.atlassian.confluence.links.RelatedContentRefactorer;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

public class DefaultRelatedContentRefactorer
implements RelatedContentRefactorer {
    private final PageManager pageManager;
    private final LinkManager linkManager;
    private final LinksUpdater linksUpdater;

    public DefaultRelatedContentRefactorer(PageManager pageManager, LinkManager linkManager, LinksUpdater linksUpdater) {
        this.pageManager = pageManager;
        this.linkManager = linkManager;
        this.linksUpdater = linksUpdater;
    }

    @Override
    public void updateReferrersForMovingPage(SpaceContentEntityObject referee, Space previousSpace, String previousTitle, List<Page> movedPageList) {
        this.updateReferrers(referee, previousSpace, previousTitle, movedPageList, true);
    }

    @Override
    public void updateReferrers(SpaceContentEntityObject referee, Space previousSpace, String previousTitle) {
        this.updateReferrers(referee, previousSpace, previousTitle, null, false);
    }

    private void updateReferrers(SpaceContentEntityObject renamedPage, Space previousSpace, String previousTitle, List<Page> movedPageList, boolean isMovingPage) {
        ArrayList list = movedPageList != null ? Lists.newArrayList(movedPageList) : Lists.newArrayList((Object[])new SpaceContentEntityObject[]{renamedPage});
        List<Page> otherMovedPages = Optional.ofNullable(movedPageList).map(Collection::stream).orElse(Stream.empty()).filter(p -> !p.equals(renamedPage)).collect(Collectors.toList());
        for (SpaceContentEntityObject entity : list) {
            SpaceContentEntityObject oldEntity = (SpaceContentEntityObject)entity.clone();
            oldEntity.setSpace(previousSpace);
            String oldEntityTitle = isMovingPage ? entity.getTitle() : previousTitle;
            oldEntity.setTitle(oldEntityTitle);
            if (!entity.isDraft()) {
                for (ContentEntityObject referringContent : this.linkManager.getReferringContent(oldEntity)) {
                    if (!BodyType.XHTML.equals(referringContent.getBodyContent().getBodyType())) {
                        return;
                    }
                    if (referringContent.equals(entity)) continue;
                    String expandedContent = "";
                    if (isMovingPage) {
                        if (referringContent instanceof SpaceContentEntityObject) {
                            expandedContent = this.getExpandedContent((SpaceContentEntityObject)referringContent, movedPageList, previousSpace);
                        } else if (referringContent instanceof Comment) {
                            expandedContent = this.getExpandedContent((Comment)referringContent, movedPageList, previousSpace);
                        }
                    }
                    if (expandedContent.equals("")) {
                        expandedContent = this.expandRelativeLinksInContent(referringContent);
                    }
                    LinksUpdater.PartialReferenceDetails oldDetails = LinksUpdater.PartialReferenceDetails.createReference(oldEntity);
                    LinksUpdater.PartialReferenceDetails newDetails = LinksUpdater.PartialReferenceDetails.createReference(entity);
                    for (Page movedPage : otherMovedPages) {
                        LinksUpdater.PartialReferenceDetails oldMovedPageDetails = LinksUpdater.PartialReferenceDetails.createReference(movedPage, previousSpace.getKey());
                        LinksUpdater.PartialReferenceDetails newMovedPageDetails = LinksUpdater.PartialReferenceDetails.createReference(movedPage);
                        expandedContent = this.linksUpdater.updateReferencesInContent(expandedContent, oldMovedPageDetails, newMovedPageDetails);
                    }
                    String updatedContent = this.linksUpdater.updateReferencesInContent(expandedContent, oldDetails, newDetails);
                    referringContent.setBodyAsString(updatedContent);
                    String contentVersion = isMovingPage ? updatedContent : this.contractAbsoluteLinksInContent(referringContent);
                    this.persistBodyModificationToContent(referringContent, contentVersion);
                }
            }
            this.updateReferences(entity, previousSpace, entity.getTitle(), isMovingPage, otherMovedPages);
        }
    }

    @Override
    public void updateReferrers(Attachment attachment, Attachment previousAttachment) {
        if (!(attachment.getContainer() instanceof SpaceContentEntityObject) || !(previousAttachment.getContainer() instanceof SpaceContentEntityObject)) {
            return;
        }
        SpaceContentEntityObject oldSceo = (SpaceContentEntityObject)previousAttachment.getContainer();
        for (ContentEntityObject referringContent : this.linkManager.getReferringContent(oldSceo)) {
            LinksUpdater.AttachmentReferenceDetails newDetails;
            LinksUpdater.AttachmentReferenceDetails oldDetails;
            if (!BodyType.XHTML.equals(referringContent.getBodyContent().getBodyType())) {
                return;
            }
            String expandedContent = this.expandRelativeLinksInContent(referringContent);
            String updatedContent = this.linksUpdater.updateAttachmentReferencesInContent(expandedContent, oldDetails = LinksUpdater.AttachmentReferenceDetails.createReference(previousAttachment), newDetails = LinksUpdater.AttachmentReferenceDetails.createReference(attachment));
            if (updatedContent.equals(expandedContent)) continue;
            this.persistBodyModificationToContent(referringContent, updatedContent);
        }
    }

    private String getExpandedContent(SpaceContentEntityObject referringContent, List<Page> movedPageList, Space previousSpace) {
        String expandedContent = "";
        if (movedPageList != null) {
            for (Page page : movedPageList) {
                if (!page.getSpaceKey().equals(referringContent.getSpaceKey()) || !page.getTitle().equals(referringContent.getTitle())) continue;
                SpaceContentEntityObject oldCeo = (SpaceContentEntityObject)referringContent.clone();
                oldCeo.setSpace(previousSpace);
                expandedContent = this.expandRelativeLinksInContent(oldCeo);
                break;
            }
        }
        return expandedContent;
    }

    private String getExpandedContent(Comment comment, List<Page> movedPageList, Space previousSpace) {
        String expandedContent = "";
        ContentEntityObject ownerSCEO = comment.getContainer();
        SpaceContentEntityObject oldSCEO = this.generateDummySCEOForComment(comment);
        if (oldSCEO != null) {
            if (movedPageList != null) {
                for (Page page : movedPageList) {
                    if (!page.getSpaceKey().equals(((SpaceContentEntityObject)ownerSCEO).getSpaceKey()) || !page.getTitle().equals(ownerSCEO.getTitle())) continue;
                    oldSCEO.setSpace(previousSpace);
                    break;
                }
            }
            expandedContent = this.expandRelativeLinksInContent(oldSCEO);
        }
        return expandedContent;
    }

    private String expandRelativeLinksInContent(ContentEntityObject referringContent) {
        if (referringContent instanceof SpaceContentEntityObject) {
            return this.linksUpdater.expandRelativeReferencesInContent((SpaceContentEntityObject)referringContent);
        }
        if (referringContent instanceof Comment) {
            return this.linksUpdater.expandRelativeReferencesInContent((Comment)referringContent);
        }
        return referringContent.getBodyAsString();
    }

    private String contractAbsoluteLinksInContent(ContentEntityObject referringContent) {
        if (referringContent instanceof SpaceContentEntityObject) {
            return this.linksUpdater.contractAbsoluteReferencesInContent((SpaceContentEntityObject)referringContent);
        }
        return referringContent.getBodyAsString();
    }

    @Override
    public void updateReferences(SpaceContentEntityObject content, Space previousSpace, String previousTitle) {
        this.updateReferences(content, previousSpace, previousTitle, false);
    }

    @Override
    public void updateReferencesForMovingPage(SpaceContentEntityObject content, Space previousSpace, String previousTitle) {
        this.updateReferences(content, previousSpace, previousTitle, true);
    }

    private void updateReferences(SpaceContentEntityObject content, Space previousSpace, String previousTitle, boolean isMovingPage) {
        this.updateReferences(content, previousSpace, previousTitle, isMovingPage, Collections.emptyList());
    }

    private void updateReferences(SpaceContentEntityObject content, Space previousSpace, String previousTitle, boolean isMovingPage, List<Page> movedPageList) {
        SpaceContentEntityObject oldSceo = (SpaceContentEntityObject)content.clone();
        oldSceo.setSpace(previousSpace);
        oldSceo.setTitle(previousTitle);
        String updatedContent = this.refactorReferencesInContent(oldSceo, content, isMovingPage, movedPageList);
        if (!StringUtils.equals((CharSequence)content.getBodyAsString(), (CharSequence)updatedContent)) {
            this.persistBodyModificationToContent(content, updatedContent);
        }
    }

    private void persistBodyModificationToContent(ContentEntityObject content, String bodyContent) {
        content.setBodyAsString(bodyContent);
        this.pageManager.saveContentEntity(content, this.getSaveContext(content));
    }

    private SaveContext getSaveContext(ContentEntityObject content) {
        return content.isDraft() ? DefaultSaveContext.DRAFT_REFACTORING : DefaultSaveContext.REFACTORING;
    }

    private String refactorReferencesInContent(SpaceContentEntityObject content, SpaceContentEntityObject referredToContent, boolean isMovingPage, List<Page> movedPageList) {
        String expandedContent = this.linksUpdater.expandRelativeReferencesInContent(content);
        LinksUpdater.PartialReferenceDetails oldDetails = LinksUpdater.PartialReferenceDetails.createReference(content);
        LinksUpdater.PartialReferenceDetails newDetails = LinksUpdater.PartialReferenceDetails.createReference(referredToContent);
        for (Page movedPage : movedPageList) {
            LinksUpdater.PartialReferenceDetails oldMovedPageDetails = LinksUpdater.PartialReferenceDetails.createReference(movedPage, content.getSpaceKey());
            LinksUpdater.PartialReferenceDetails newMovedPageDetails = LinksUpdater.PartialReferenceDetails.createReference(movedPage);
            expandedContent = this.linksUpdater.updateReferencesInContent(expandedContent, oldMovedPageDetails, newMovedPageDetails);
        }
        String updatedContent = this.linksUpdater.updateReferencesInContent(expandedContent, oldDetails, newDetails);
        if (isMovingPage) {
            return updatedContent;
        }
        SpaceContentEntityObject clone = (SpaceContentEntityObject)referredToContent.clone();
        clone.setBodyAsString(updatedContent);
        return this.refactorReferencesToBeRelative(clone);
    }

    @Override
    public String refactorReferencesToBeRelative(SpaceContentEntityObject content) {
        return this.linksUpdater.contractAbsoluteReferencesInContent(content);
    }

    @Override
    @Deprecated
    public void contractAbsoluteReferencesInContent(List<Page> movedPageList, Space previousSpace) {
        HashSet<String> updatedPages = new HashSet<String>();
        ArrayList<ContentEntityObject> contentEntityObjects = new ArrayList<ContentEntityObject>();
        for (Page page : movedPageList) {
            this.contractAbsoluteReferencesInContent(updatedPages, page);
            SpaceContentEntityObject oldSceo = (SpaceContentEntityObject)page.clone();
            oldSceo.setSpace(previousSpace);
            contentEntityObjects.add(oldSceo);
        }
        Collection<ContentEntityObject> referringContents = this.linkManager.getReferringContent(previousSpace.getKey(), contentEntityObjects);
        for (ContentEntityObject referringContent : referringContents) {
            this.contractAbsoluteReferencesInContent(updatedPages, referringContent);
        }
    }

    private void contractAbsoluteReferencesInContent(Set<String> updatedEntities, ContentEntityObject contentEntityObject) {
        String content;
        String canonicalContent;
        SpaceContentEntityObject sceo;
        String key = contentEntityObject.getType() + "-" + contentEntityObject.getId();
        if (updatedEntities.contains(key)) {
            return;
        }
        updatedEntities.add(key);
        String updatedContent = null;
        if (contentEntityObject instanceof SpaceContentEntityObject) {
            updatedContent = this.refactorReferencesToBeRelative((SpaceContentEntityObject)contentEntityObject);
        } else if (contentEntityObject instanceof Comment && (sceo = this.generateDummySCEOForComment((Comment)contentEntityObject)) != null) {
            updatedContent = this.refactorReferencesToBeRelative(sceo);
        }
        if (updatedContent != null && !(canonicalContent = this.linksUpdater.canonicalize(content = contentEntityObject.getBodyAsString())).equals(updatedContent)) {
            this.persistBodyModificationToContent(contentEntityObject, updatedContent);
        }
    }

    @Override
    public void contractAbsoluteReferencesInContent(List<Page> movedPageList) {
        HashSet<String> updatedPages = new HashSet<String>();
        Iterator<Page> iterator = movedPageList.iterator();
        while (iterator.hasNext()) {
            Page page;
            Page contentEntityObject = page = iterator.next();
            this.contractAbsoluteReferencesInContent(updatedPages, contentEntityObject);
            for (ContentEntityObject referringContent : this.linkManager.getReferringContent(contentEntityObject)) {
                this.contractAbsoluteReferencesInContent(updatedPages, referringContent);
            }
        }
    }

    private SpaceContentEntityObject generateDummySCEOForComment(Comment comment) {
        SpaceContentEntityObject sceo = new SpaceContentEntityObject(){

            @Override
            public String getType() {
                return "comment";
            }

            @Override
            public String getUrlPath() {
                return null;
            }
        };
        ContentEntityObject owner = comment.getContainer();
        if (owner instanceof SpaceContentEntityObject) {
            sceo.setSpace(((SpaceContentEntityObject)owner).getSpace());
            sceo.setTitle(owner.getTitle());
            sceo.setBodyAsString(comment.getBodyAsString());
            sceo.setId(owner.getId());
            return sceo;
        }
        return null;
    }
}

