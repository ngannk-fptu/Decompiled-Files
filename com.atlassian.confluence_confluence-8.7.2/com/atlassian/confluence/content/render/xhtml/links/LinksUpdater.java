/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.content.render.xhtml.links;

import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public interface LinksUpdater {
    public String updateReferencesInContent(String var1, PartialReferenceDetails var2, PartialReferenceDetails var3);

    public String updateReferencesInContent(String var1, Map<PartialReferenceDetails, PartialReferenceDetails> var2);

    public String updateAttachmentReferencesInContent(String var1, AttachmentReferenceDetails var2, AttachmentReferenceDetails var3);

    public String updateAttachmentReferencesInContent(String var1, Map<AttachmentReferenceDetails, AttachmentReferenceDetails> var2);

    public String expandRelativeReferencesInContent(SpaceContentEntityObject var1);

    public String expandRelativeReferencesInContent(Comment var1);

    public String contractAbsoluteReferencesInContent(SpaceContentEntityObject var1);

    public String canonicalize(String var1);

    public static class AttachmentReferenceDetails
    extends PartialReferenceDetails {
        private final String attachmentName;

        public static AttachmentReferenceDetails createReference(Attachment attachment) {
            SpaceContentEntityObject ceo = (SpaceContentEntityObject)attachment.getContainer();
            if (ceo == null) {
                throw new IllegalArgumentException("Attachment container cannot be null");
            }
            if (ceo instanceof BlogPost) {
                String postingDate = XhtmlConstants.DATE_FORMATS.getPostingDayFormat().format(((BlogPost)ceo).getPostingDate());
                return new AttachmentReferenceDetails(attachment.getFileName(), postingDate, ceo.getTitle(), ceo.getSpaceKey());
            }
            return new AttachmentReferenceDetails(attachment.getFileName(), ceo.getTitle(), ceo.getSpaceKey());
        }

        private AttachmentReferenceDetails(String attachmentName, String postingDate, String contentTitle, String spaceKey) {
            super(spaceKey, contentTitle, postingDate);
            this.attachmentName = attachmentName;
        }

        private AttachmentReferenceDetails(String attachmentName, String contentTitle, String spaceKey) {
            this(attachmentName, null, contentTitle, spaceKey);
        }

        public String getAttachmentName() {
            return this.attachmentName;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o == this) {
                return true;
            }
            if (o.getClass() != this.getClass()) {
                return false;
            }
            AttachmentReferenceDetails rhs = (AttachmentReferenceDetails)o;
            return new EqualsBuilder().appendSuper(super.equals(o)).append((Object)this.attachmentName, (Object)rhs.attachmentName).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(1, 5).append((Object)this.attachmentName).toHashCode();
        }
    }

    public static class PartialReferenceDetails {
        private final String spaceKey;
        private final String title;
        private final String postingDate;

        public static PartialReferenceDetails createReference(SpaceContentEntityObject ceo) {
            return PartialReferenceDetails.createReference(ceo, ceo.getSpaceKey());
        }

        public static PartialReferenceDetails createReference(SpaceContentEntityObject ceo, String spaceKey) {
            if (ceo instanceof BlogPost) {
                String postingDay = XhtmlConstants.DATE_FORMATS.getPostingDayFormat().format(((BlogPost)ceo).getPostingDate().getTime());
                return new PartialReferenceDetails(spaceKey, ceo.getTitle(), postingDay);
            }
            return new PartialReferenceDetails(spaceKey, ceo.getTitle());
        }

        private PartialReferenceDetails(String spaceKey, String title) {
            this(spaceKey, title, null);
        }

        private PartialReferenceDetails(String spaceKey, String title, String postingDate) {
            if (StringUtils.isBlank((CharSequence)spaceKey)) {
                throw new IllegalArgumentException("spaceKey cannot be null or empty.");
            }
            if (StringUtils.isBlank((CharSequence)title)) {
                throw new IllegalArgumentException("title cannot be null or empty.");
            }
            this.spaceKey = spaceKey;
            this.title = title;
            this.postingDate = postingDate;
        }

        public boolean isReferenceBlogPost() {
            return StringUtils.isNotBlank((CharSequence)this.postingDate);
        }

        public String getPostingDate() {
            return this.postingDate;
        }

        public String getSpaceKey() {
            return this.spaceKey;
        }

        public String getTitle() {
            return this.title;
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o == this) {
                return true;
            }
            if (o.getClass() != this.getClass()) {
                return false;
            }
            PartialReferenceDetails rhs = (PartialReferenceDetails)o;
            return new EqualsBuilder().appendSuper(super.equals(o)).append((Object)this.spaceKey, (Object)rhs.spaceKey).append((Object)this.title, (Object)rhs.title).append((Object)this.postingDate, (Object)rhs.postingDate).isEquals();
        }

        public int hashCode() {
            return new HashCodeBuilder(31, 5).append((Object)this.spaceKey).append((Object)this.title).append((Object)this.postingDate).toHashCode();
        }
    }
}

