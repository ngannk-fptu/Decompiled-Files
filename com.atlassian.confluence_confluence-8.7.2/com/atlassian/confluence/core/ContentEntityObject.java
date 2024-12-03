/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.ObjectDao
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.impl.hibernate.Hibernate
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.renderer.util.RendererUtil
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.user.User
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.checkerframework.checker.nullness.qual.EnsuresNonNullIf
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core;

import bucket.core.persistence.ObjectDao;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.content.Content;
import com.atlassian.confluence.content.ContentProperties;
import com.atlassian.confluence.content.ContentProperty;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.AbstractLabelableEntityObject;
import com.atlassian.confluence.core.AbstractVersionedEntityObject;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.VersionChildOwnerPolicy;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.impl.hibernate.Hibernate;
import com.atlassian.confluence.impl.util.collections.SetAsList;
import com.atlassian.confluence.internal.persistence.ObjectDaoInternal;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HTMLSearchableTextUtil;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.renderer.util.RendererUtil;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.user.User;
import com.google.common.collect.Iterables;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public abstract class ContentEntityObject
extends AbstractLabelableEntityObject
implements Searchable,
Comparable<ContentEntityObject>,
Addressable,
Content,
RelatableEntity {
    public static final String CURRENT = "current";
    public static final String DELETED = "deleted";
    public static final String CREATED = "created";
    public static final String MODIFIED = "modified";
    public static final String DRAFT = "draft";
    public static final String SHARE_ID = "share-id";
    public static final String SYNC_REV = "sync-rev";
    public static final String DUMMY_SYNC_REV = "dummy-sync-rev";
    public static final String SYNC_REV_SOURCE = "sync-rev-source";
    public static final String COLLABORATIVE_EDITING_UUID = "synchrony-collaborative-editor-UUID";
    private static final String TRASH_DATE = "trash-date";
    public static final String DELETED_BY = "deleted-by";
    public static final String DELETED_BY_ANON_VALUE = "anonymousUser";
    @Deprecated
    public static final String LIMITED_MODE_SYNC_REV_SOURCE = "limited";
    public static final String SYNCHRONY_SYNC_REV_SOURCE = "synchrony";
    public static final String SYNCHRONY_ACK_SYNC_REV_SOURCE = "synchrony-ack";
    public static final String CONTENT_RESTORED_SYNC_REV_SOURCE = "restored";
    public static final String CONFLUENCE_RECOVERY = "confluence-recovery";
    public static final String CONFLUENCE_RECOVERY_WITH_EXTERNAL_CHANGE = "confluence-recovery-with-external-change";
    public static final String SYNCHRONY_RECOVERY = "synchrony-recovery";
    public static final String SYNCHRONY_RECOVERY_WITH_EXTERNAL_CHANGE = "synchrony-recovery-with-external-change";
    private static final Logger log = LoggerFactory.getLogger(ContentEntityObject.class);
    private static final int MAX_EXCERPT_LENGTH = 255;
    private static final long serialVersionUID = -6886184863545352562L;
    private String title;
    private String lowerTitle;
    private List<BodyContent> bodyContents = new ArrayList<BodyContent>();
    private String versionComment = "";
    private List<OutgoingLink> outgoingLinks = new ArrayList<OutgoingLink>();
    private String contentStatus = "current";
    private List<Attachment> attachments = new ArrayList<Attachment>();
    private ContentEntityObject containerContent;
    private @Nullable Long originalVersionId;
    private List<ContentPermissionSet> contentPermissionSets = new ArrayList<ContentPermissionSet>();
    private ContentProperties contentProperties = new ContentProperties(new ArrayList<ContentProperty>());
    private List<ContentEntityObject> historicalVersions = new ArrayList<ContentEntityObject>();
    private Set<Comment> comments = new LinkedHashSet<Comment>();
    private List<CustomContentEntityObject> customContent = new ArrayList<CustomContentEntityObject>();

    protected ContentEntityObject() {
    }

    @Override
    public abstract String getType();

    public ContentTypeEnum getTypeEnum() {
        return ContentTypeEnum.getByRepresentation(this.getType());
    }

    public String getIdAsString() {
        return Long.toString(this.getId());
    }

    @Override
    public String getDisplayTitle() {
        return this.getTitle();
    }

    @Override
    public abstract String getUrlPath();

    public String getAttachmentUrlPath(Attachment attachment) {
        ContentEntityObject container = attachment.getContainer();
        if (container == null || container.getId() != this.getId()) {
            throw new IllegalArgumentException("Attachment " + attachment + " is not attached to " + this);
        }
        return this.getAttachmentsUrlPath();
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.lowerTitle = StringUtils.lowerCase((String)title);
    }

    public String getLowerTitle() {
        return this.lowerTitle;
    }

    private void setLowerTitle(String lowerTitle) {
        this.lowerTitle = lowerTitle;
    }

    public BodyType getDefaultBodyType() {
        return BodyType.XHTML;
    }

    public BodyContent getBodyContent() {
        return this.getBodyContents().isEmpty() ? new BodyContent(this, "", this.getDefaultBodyType()) : new BodyContent(this.getBodyContents().get(0));
    }

    public BodyContent getBodyContent(BodyType expectedBodyType) {
        BodyContent bodyContent = this.getBodyContent();
        BodyType bodyType = bodyContent.getBodyType();
        if (!(bodyType == null || expectedBodyType != null && expectedBodyType.equals(bodyType))) {
            throw new UnsupportedOperationException("The body of this ContentEntityObject ('" + StringUtils.defaultString((String)this.getTitle()) + "') was '" + bodyType + "' but was expected to be '" + expectedBodyType + "'");
        }
        return bodyContent;
    }

    public void setBodyContent(BodyContent bodyContent) {
        if (bodyContent == null || bodyContent.getBody() == null) {
            this.bodyContents.clear();
        } else {
            bodyContent.setContent(this);
            if (this.bodyContents.isEmpty()) {
                this.bodyContents.add(new BodyContent(bodyContent));
            } else {
                BodyContent current = this.bodyContents.get(0);
                current.shallowCopy(bodyContent);
                this.bodyContents.set(0, current);
            }
        }
    }

    public String getBodyAsString() {
        return this.getBodyContent(this.getDefaultBodyType()).getBody();
    }

    public void setBodyAsString(String content) {
        BodyContent bodyContent = this.getBodyContent(this.getDefaultBodyType());
        bodyContent.setBody(content);
        this.setBodyContent(bodyContent);
    }

    public List<BodyContent> getBodyContents() {
        return this.bodyContents;
    }

    public void setBodyContents(List<BodyContent> bodyContents) {
        this.bodyContents = bodyContents;
    }

    private List<ContentEntityObject> getHistoricalVersions() {
        return this.historicalVersions;
    }

    private void setHistoricalVersions(List<ContentEntityObject> historicalVersions) {
        this.historicalVersions = historicalVersions;
    }

    public List<OutgoingLink> getOutgoingLinks() {
        return this.outgoingLinks;
    }

    public void setOutgoingLinks(List<OutgoingLink> outgoingLinks) {
        this.outgoingLinks = outgoingLinks;
    }

    public void addOutgoingLink(OutgoingLink link) {
        this.getOutgoingLinks().add(link);
    }

    public void removeOutgoingLink(OutgoingLink link) {
        this.getOutgoingLinks().remove(link);
    }

    @Override
    public void setOriginalVersion(Versioned originalVersion) {
        super.setOriginalVersion(originalVersion);
        if (originalVersion instanceof AbstractVersionedEntityObject) {
            this.setOriginalVersionId(((AbstractVersionedEntityObject)originalVersion).getId());
        }
    }

    @Override
    public void convertToHistoricalVersion() {
        super.convertToHistoricalVersion();
        this.contentProperties = ContentProperties.deepClone(this.contentProperties);
        this.setOutgoingLinks(new ArrayList<OutgoingLink>());
        this.setAttachments(new ArrayList<Attachment>());
        this.setLabellings(new ArrayList<Labelling>());
        this.setContentPermissionSets(new ArrayList<ContentPermissionSet>());
        this.setComments(new ArrayList<Comment>());
    }

    @Override
    @Deprecated
    public void applyChildVersioningPolicy(Versioned versionToPromote, ObjectDao dao) {
        super.applyChildVersioningPolicy(versionToPromote, dao);
        this.applyChildVersioningPolicyInternal(versionToPromote, (ObjectDaoInternal)dao);
    }

    @Override
    public void applyChildVersioningPolicy(@Nullable Versioned versionToPromote, ObjectDaoInternal<?> dao) {
        super.applyChildVersioningPolicy(versionToPromote, dao);
        this.applyChildVersioningPolicyInternal(versionToPromote, dao);
    }

    private void applyChildVersioningPolicyInternal(Versioned versionToPromote, ObjectDaoInternal<?> dao) {
        if (versionToPromote == null) {
            return;
        }
        ContentEntityObject ceoToPromote = (ContentEntityObject)versionToPromote;
        if (this.getVersionChildPolicy(ContentType.COMMENT) == VersionChildOwnerPolicy.originalVersion) {
            this.moveVersionedComments(ceoToPromote, dao);
        }
        if (this.getVersionChildPolicy(ContentType.ATTACHMENT) == VersionChildOwnerPolicy.originalVersion) {
            this.moveVersionedAttachments(ceoToPromote, dao);
        }
        this.moveCustomContent(ceoToPromote, dao);
    }

    private void moveVersionedComments(ContentEntityObject ceoToMoveCommentsFrom, ObjectDaoInternal<Comment> dao) {
        for (Comment comment : this.getComments()) {
            dao.removeEntity(comment);
        }
        this.setComments(new ArrayList<Comment>(ceoToMoveCommentsFrom.comments));
        for (Comment comment : this.comments) {
            comment.setContainer(this);
        }
        ceoToMoveCommentsFrom.setComments(new ArrayList<Comment>());
    }

    private void moveVersionedAttachments(ContentEntityObject ceoToMoveAttachmentsFrom, ObjectDaoInternal<Attachment> dao) {
        for (Attachment attachment : this.getAttachments()) {
            dao.removeEntity(attachment);
        }
        this.setAttachments(ceoToMoveAttachmentsFrom.attachments);
        for (Attachment attachment : this.attachments) {
            attachment.setContainer(this);
        }
        ceoToMoveAttachmentsFrom.setAttachments(new ArrayList<Attachment>());
    }

    private void moveCustomContent(ContentEntityObject ceoToMoveCustomContentFrom, ObjectDaoInternal<CustomContentEntityObject> dao) {
        ArrayList<CustomContentEntityObject> customContentToDelete = new ArrayList<CustomContentEntityObject>(this.customContent);
        for (CustomContentEntityObject cceo : customContentToDelete) {
            if (this.getVersionChildPolicy(cceo.getContentTypeObject()) != VersionChildOwnerPolicy.originalVersion) continue;
            this.removeCustomContent(cceo);
            dao.removeEntity(cceo);
        }
        ArrayList<CustomContentEntityObject> customContentToMove = new ArrayList<CustomContentEntityObject>(ceoToMoveCustomContentFrom.getCustomContent());
        for (CustomContentEntityObject cceo : customContentToMove) {
            if (this.getVersionChildPolicy(cceo.getContentTypeObject()) != VersionChildOwnerPolicy.originalVersion) continue;
            ceoToMoveCustomContentFrom.removeCustomContent(cceo);
            this.addCustomContent(cceo);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass((Object)this) != Hibernate.getClass((Object)o)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ContentEntityObject that = (ContentEntityObject)o;
        return this.getTitle() != null ? this.getTitle().equalsIgnoreCase(that.getTitle()) : that.getTitle() == null;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.getTitle() != null ? this.getTitle().toLowerCase().hashCode() : 0);
        return result;
    }

    public String toString() {
        return this.getType() + ": " + this.getDisplayTitle() + " v." + this.getVersion() + " (" + this.getId() + ")";
    }

    public abstract String getNameForComparison();

    @Override
    public final int compareTo(ContentEntityObject otherEntity) {
        int comparison = this.getNameForComparison().compareToIgnoreCase(otherEntity.getNameForComparison());
        long thisCreationDate = this.getCreationDate() != null ? this.getCreationDate().getTime() : 0L;
        long otherCreationDate = otherEntity.getCreationDate() != null ? otherEntity.getCreationDate().getTime() : 0L;
        return comparison == 0 ? Long.compare(thisCreationDate, otherCreationDate) : comparison;
    }

    public Collection<Searchable> getSearchableDependants() {
        return new ArrayList<Searchable>(this.getAttachmentManager().getLatestVersionsOfAttachmentsWithAnyStatus(this));
    }

    public final PageContext toPageContext() {
        return new PageContext(this);
    }

    public boolean isIndexable() {
        return this.isLatestVersion() && (this.isCurrent() || this.isDraft());
    }

    @Deprecated
    public List<ContentPermission> getPermissions() {
        ArrayList<ContentPermission> result = new ArrayList<ContentPermission>();
        for (ContentPermissionSet contentPermissionSet : this.getContentPermissionSets()) {
            for (ContentPermission permission : contentPermissionSet) {
                result.add(permission);
            }
        }
        return result;
    }

    @Deprecated
    public synchronized ContentPermission getContentPermission(String permissionType) {
        ContentPermissionSet permissionSet = this.getContentPermissionSet(permissionType);
        if (permissionSet != null && permissionSet.size() > 0) {
            return permissionSet.iterator().next();
        }
        return null;
    }

    @Deprecated
    public boolean sharedAccessAllowed(String shareId) {
        return this.isDraft() && (!this.isLatestVersion() || this.getShareId().equals(shareId));
    }

    public ContentStatus getContentStatusObject() {
        if (this.isDeleted()) {
            return ContentStatus.TRASHED;
        }
        if (this.isDraft()) {
            return ContentStatus.DRAFT;
        }
        return this.isLatestVersion() ? ContentStatus.CURRENT : ContentStatus.HISTORICAL;
    }

    public String getContentStatus() {
        return this.contentStatus;
    }

    public void setContentStatus(String contentStatus) {
        this.contentStatus = contentStatus;
    }

    public boolean isCurrent() {
        return CURRENT.equals(this.getContentStatus());
    }

    public boolean isDeleted() {
        return DELETED.equals(this.getContentStatus());
    }

    public boolean isDraft() {
        return DRAFT.equals(this.getContentStatus());
    }

    public boolean sharedAccessAllowed(User user) {
        return !this.isDraft() || !this.isLatestVersion() || this.wasCreatedBy(user) || this.isSharedWith(user);
    }

    private boolean isSharedWith(User user) {
        ContentPermissionSet sharedPermissions = this.getContentPermissionSet("Share");
        return sharedPermissions != null && sharedPermissions.isPermitted(user);
    }

    public String getShareId() {
        return this.getProperties().getStringProperty(SHARE_ID);
    }

    public void setShareId(String shareId) {
        if (StringUtils.isNotBlank((CharSequence)shareId)) {
            this.getProperties().setStringProperty(SHARE_ID, shareId);
        }
    }

    public String getSynchronyRevision() {
        try {
            String syncRev = this.getProperties().getStringProperty(SYNC_REV);
            if (!this.validSyncRev(syncRev)) {
                this.getProperties().removeProperty(SYNC_REV);
                return null;
            }
            return syncRev;
        }
        catch (IllegalArgumentException e) {
            this.getProperties().removeProperty(SYNC_REV);
            return null;
        }
    }

    public void setSynchronyRevision(String synchronyRevision) {
        if (synchronyRevision == null) {
            return;
        }
        if (!this.validSyncRev(synchronyRevision)) {
            log.error("Attempted to set invalid sync rev '{}' to content entity object id {}", (Object)synchronyRevision, (Object)this.getContentId());
            throw new IllegalArgumentException("Invalid synchrony revision");
        }
        this.getProperties().setStringProperty(SYNC_REV, synchronyRevision);
    }

    private boolean validSyncRev(String synchronyRevision) {
        if (StringUtils.isBlank((CharSequence)synchronyRevision)) {
            return false;
        }
        if (DUMMY_SYNC_REV.equals(synchronyRevision)) {
            return true;
        }
        String[] parts = synchronyRevision.split("\\.");
        return parts.length >= 2 && StringUtils.isNotBlank((CharSequence)parts[parts.length - 2]) && NumberUtils.isCreatable((String)parts[parts.length - 1]);
    }

    public String getSynchronyRevisionSource() {
        return this.getProperties().getStringProperty(SYNC_REV_SOURCE);
    }

    public Optional<Instant> getTrashDate() {
        long trashDate = this.getProperties().getLongProperty(TRASH_DATE, -1L);
        return Optional.ofNullable(trashDate == -1L ? null : Instant.ofEpochMilli(trashDate));
    }

    public void setSynchronyRevisionSource(String synchronyRevisionSource) {
        if (StringUtils.isNotBlank((CharSequence)synchronyRevisionSource)) {
            this.getProperties().setStringProperty(SYNC_REV_SOURCE, synchronyRevisionSource);
        }
    }

    public String getCollaborativeEditingUuid() {
        return this.getProperties().getStringProperty(COLLABORATIVE_EDITING_UUID);
    }

    public void setCollaborativeEditingUuid(String uuid) {
        if (StringUtils.isNotBlank((CharSequence)uuid)) {
            this.getProperties().setStringProperty(COLLABORATIVE_EDITING_UUID, uuid);
        }
    }

    public boolean isUnpublished() {
        return this.isDraft() && this.isLatestVersion() || Content.UNSET.equals(((ContentEntityObject)this.getLatestVersion()).getId());
    }

    public boolean wasCreatedBy(User user) {
        ConfluenceUser creator = this.getCreator();
        if (user == null) {
            return creator == null;
        }
        return user.equals(creator);
    }

    public void trash() {
        this.trash(System.currentTimeMillis());
    }

    protected void trash(long trashTimestamp) {
        this.setContentStatus(DELETED);
        ConfluenceUser authenticatedUser = AuthenticatedUserThreadLocal.get();
        if (authenticatedUser != null) {
            this.contentProperties.setStringProperty(DELETED_BY, authenticatedUser.getKey().getStringValue());
        } else {
            this.contentProperties.setStringProperty(DELETED_BY, DELETED_BY_ANON_VALUE);
        }
        if (this.getProperties().getLongProperty(TRASH_DATE, -1L) == -1L) {
            this.getProperties().setLongProperty(TRASH_DATE, trashTimestamp);
        }
        this.trashDependents(trashTimestamp);
    }

    protected void trashDependents() {
        this.trashDependents(System.currentTimeMillis());
    }

    private void trashDependents(long timestamp) {
        for (ContentEntityObject contentEntityObject : this.getDependents()) {
            contentEntityObject.trash(timestamp);
        }
    }

    public void restore() {
        this.setContentStatus(CURRENT);
        this.contentProperties.removeProperty(DELETED_BY);
        this.getProperties().removeProperty(TRASH_DATE);
        this.restoreDependents();
    }

    protected void restoreDependents() {
        for (ContentEntityObject contentEntityObject : this.getDependents()) {
            contentEntityObject.restore();
        }
    }

    private Iterable<? extends ContentEntityObject> getDependents() {
        return Iterables.concat(this.getComments(), this.getCustomContent(), this.getAttachments());
    }

    public List<Attachment> getAttachments() {
        return this.attachments;
    }

    @Deprecated
    public List<Attachment> getLatestVersionsOfAttachments() {
        return this.getAttachmentManager().getLatestVersionsOfAttachments(this);
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void addAttachment(Attachment attachment) {
        this.getAttachments().add(attachment);
        attachment.setContainer(this);
    }

    public void removeAttachment(Attachment attachment) {
        attachment.setContainer(null);
        this.getAttachments().remove(attachment);
    }

    public String getBodyAsStringWithoutMarkup() {
        BodyContent bodyContent = this.getBodyContent();
        String rawText = null;
        if (bodyContent.getBodyType() == BodyType.WIKI || bodyContent.getBodyType() == BodyType.RAW) {
            rawText = RendererUtil.stripBasicMarkup((String)bodyContent.getBody());
        } else if (bodyContent.getBodyType() == BodyType.XHTML) {
            try {
                ContentEntityObject owner;
                String pageTitle = this.getType().equals("comment") ? ((owner = ((Comment)this).getContainer()) != null ? owner.getTitle() : "") : this.getTitle();
                rawText = HTMLSearchableTextUtil.stripTags(pageTitle, bodyContent.getBody());
            }
            catch (SAXException e) {
                log.debug("error exctracting excerpt", (Throwable)e);
            }
        }
        return rawText;
    }

    public String getExcerpt() {
        String strippedContent = StringUtils.normalizeSpace((String)this.getBodyAsStringWithoutMarkup());
        return StringUtils.left((String)strippedContent, (int)255);
    }

    public String getAttachmentsUrlPath() {
        return this.getUrlPath();
    }

    public Attachment getAttachmentNamed(String fileName) {
        for (Attachment attachment : this.getAttachments()) {
            if (!attachment.getFileName().equalsIgnoreCase(fileName)) continue;
            return attachment;
        }
        return null;
    }

    public String getVersionComment() {
        return this.versionComment;
    }

    public void setVersionComment(String versionComment) {
        this.versionComment = versionComment;
    }

    public boolean isVersionCommentAvailable() {
        return StringUtils.isNotEmpty((CharSequence)this.getVersionComment()) && this.getVersionComment().trim().length() > 0;
    }

    @Override
    public Object clone() {
        ContentEntityObject clone;
        try {
            clone = (ContentEntityObject)super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
        ArrayList<BodyContent> newBodyContents = new ArrayList<BodyContent>(1);
        if (!clone.getBodyContents().isEmpty()) {
            BodyContent newBodyContent = (BodyContent)clone.getBodyContents().get(0).clone();
            newBodyContent.setId(0L);
            newBodyContent.setContent(clone);
            newBodyContents.add(newBodyContent);
        }
        clone.setBodyContents(newBodyContents);
        clone.historicalVersions = new ArrayList<ContentEntityObject>();
        clone.comments = new LinkedHashSet<Comment>();
        clone.customContent = new ArrayList<CustomContentEntityObject>();
        clone.contentProperties = ContentProperties.deepClone(this.contentProperties);
        return clone;
    }

    @HtmlSafe
    public String getRenderedVersionComment() {
        return VersionHistorySummary.renderVersionComment(this.getVersionComment());
    }

    public ContentPermissionSet getContentPermissionSet(String type) {
        for (ContentPermissionSet permissionSet : this.contentPermissionSets) {
            if (type == null || !type.equals(permissionSet.getType())) continue;
            return permissionSet;
        }
        return null;
    }

    public boolean hasPermissions(String type) {
        return this.getContentPermissionSet(type) != null;
    }

    public boolean hasContentPermissions() {
        for (ContentPermissionSet permissionSet : this.contentPermissionSets) {
            if ("Share".equals(permissionSet.getType())) continue;
            return true;
        }
        return false;
    }

    private List<ContentPermissionSet> getContentPermissionSets() {
        return this.contentPermissionSets;
    }

    private void setContentPermissionSets(List<ContentPermissionSet> contentPermissionSets) {
        this.contentPermissionSets = contentPermissionSets;
    }

    private List<ContentProperty> getContentProperties() {
        return this.contentProperties.asList();
    }

    private void setContentProperties(List<ContentProperty> contentProperties) {
        this.contentProperties = new ContentProperties(contentProperties);
    }

    public void addPermission(ContentPermission permission) {
        String type = permission.getType();
        ContentPermissionSet set = this.getContentPermissionSet(type);
        if (set == null) {
            set = new ContentPermissionSet(type, this);
            this.contentPermissionSets.add(set);
        }
        set.addContentPermission(permission);
    }

    public void removeContentPermissionSet(ContentPermissionSet set) {
        this.contentPermissionSets.remove(set);
        set.setOwningContent(null);
    }

    public List<Comment> getComments() {
        return this.comments != null ? new SetAsList<Comment>(this.comments) : null;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments != null ? new LinkedHashSet<Comment>(comments) : null;
    }

    public void addComment(Comment comment) {
        if (!this.getComments().contains(comment)) {
            this.getComments().add(comment);
        }
        comment.setContainer(this);
    }

    public void removeComment(Comment comment) {
        comment.reparentChildren(comment.getParent());
        if (comment.getParent() != null) {
            comment.getParent().removeChild(comment);
        }
        this.getComments().remove(comment);
    }

    public List<CustomContentEntityObject> getCustomContent() {
        return this.customContent;
    }

    public void addCustomContent(CustomContentEntityObject customContentEntityObject) {
        this.getCustomContent().add(customContentEntityObject);
        customContentEntityObject.setContainer(this);
    }

    public void removeCustomContent(CustomContentEntityObject customContentEntityObject) {
        customContentEntityObject.setContainer(null);
        this.getCustomContent().remove(customContentEntityObject);
    }

    private void setCustomContent(List<CustomContentEntityObject> customContent) {
        this.customContent = customContent;
    }

    @Override
    public ContentEntityObject getEntity() {
        return this;
    }

    @EnsuresNonNullIf(expression={"attachment.getContainer()"}, result=true)
    protected void ensureAttachmentBelongsToContent(Attachment attachment) {
        ContentEntityObject container = attachment.getContainer();
        if (container == null || container.getId() != this.getId()) {
            throw new IllegalArgumentException("Content " + this + " can not determine URL for attachment: " + attachment + " because it belongs to " + container);
        }
    }

    public ContentId getContentId() {
        return null;
    }

    public ContentSelector getSelector() {
        return ContentSelector.builder().id(((ContentEntityObject)this.getLatestVersion()).getContentId()).status(this.getContentStatusObject()).version(this.getVersion()).build();
    }

    public ContentProperties getProperties() {
        return this.contentProperties;
    }

    protected @Nullable ContentEntityObject getContainerContent() {
        return this.containerContent;
    }

    protected void setContainerContent(@Nullable ContentEntityObject container) {
        this.containerContent = container;
    }

    protected void replaceContentProperties(ContentProperties propertiesToClone) {
        List<ContentProperty> savedProperties = this.getContentProperties();
        savedProperties.clear();
        savedProperties.addAll(ContentProperties.deepClone(propertiesToClone).asList());
    }

    public void setContentPropertiesFrom(ContentEntityObject ceo) {
        this.replaceContentProperties(ceo.getProperties());
    }

    public Long getOriginalVersionId() {
        return this.originalVersionId;
    }

    protected void setOriginalVersionId(@Nullable Long originalVersionId) {
        this.originalVersionId = originalVersionId;
    }

    public long getLatestVersionId() {
        return this.originalVersionId == null ? this.getId() : this.originalVersionId.longValue();
    }

    protected AttachmentManager getAttachmentManager() {
        LazyComponentReference attachmentManager = new LazyComponentReference("attachmentManager");
        return Optional.ofNullable((AttachmentManager)attachmentManager.get()).orElseThrow(() -> new IllegalStateException("attachmentManager bean not found"));
    }
}

