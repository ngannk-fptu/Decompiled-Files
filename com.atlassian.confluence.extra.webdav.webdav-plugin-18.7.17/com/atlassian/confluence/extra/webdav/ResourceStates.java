/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.spaces.Space
 *  org.apache.commons.lang3.BooleanUtils
 */
package com.atlassian.confluence.extra.webdav;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.spaces.Space;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.BooleanUtils;

public class ResourceStates
implements Serializable {
    private static final String WEBDAV_SPACE_PROP_DESC = "confluence.extra.webdav.description";
    private static final String WEBDAV_CONTENT_PROP_CONTENT = "confluence.extra.webdav.content";
    private static final String WEBDAV_CONTENT_PROP_CONTENT_MARKUP = "confluence.extra.webdav.content.wikimarkup";
    private static final String WEBDAV_CONTENT_PROP_CONTENT_URL = "confluence.extra.webdav.content.url";
    private static final String WEBDAV_CONTENT_PROP_CONTENT_VERSIONS = "confluence.extra.webdav.content.versions";
    private static final String WEBDAV_CONTENT_PROP_CONTENT_VERSIONS_README = "confluence.extra.webdav.content.versions.readme";
    private static final String WEBDAV_CONTENT_PROP_CONTENT_VERSION_TXT_PREFIX = "confluence.extra.webdav.content.versions.";
    private static final String WEBDAV_CONTENT_PROP_CONTENT_EXPORTS = "confluence.extra.webdav.content.exports";
    private static final String WEBDAV_CONTENT_PROP_CONTENT_EXPORTS_PDF = "confluence.extra.webdav.content.exports.pdf";
    private static final String WEBDAV_CONTENT_PROP_CONTENT_EXPORTS_WORD = "confluence.extra.webdav.content.exports.word";
    private static final String WEBDAV_CONTENT_PROP_CONTENT_EXPORTS_README = "confluence.extra.webdav.content.exports.readme";
    private static final String WEBDAV_CONTENT_PROP_ATTACHMENT_PREFIX = "confluence.extra.webdav.content.attachment.";
    private Map<AbstractAttributePlaceholder, String> contentAttributePlaceHolderToValueMap = new HashMap<AbstractAttributePlaceholder, String>();

    private void setSpaceProperty(Space space, String property, String value) {
        if (null != space) {
            this.contentAttributePlaceHolderToValueMap.put(new SpaceAttributePlaceHolder(space, property), value);
        }
    }

    private String getSpaceProperty(Space space, String property) {
        return null != space ? this.contentAttributePlaceHolderToValueMap.get(new SpaceAttributePlaceHolder(space, property)) : null;
    }

    private void setContentProperty(ContentEntityObject ceo, String property, String value) {
        if (null != ceo) {
            this.contentAttributePlaceHolderToValueMap.put(new ContentAttributePlaceHolder(ceo, property), value);
        }
    }

    private String getContentProperty(ContentEntityObject ceo, String property) {
        return null != ceo ? this.contentAttributePlaceHolderToValueMap.get(new ContentAttributePlaceHolder(ceo, property)) : null;
    }

    public void hideSpaceDescription(Space space) {
        if (null != space) {
            this.setSpaceProperty(space, WEBDAV_SPACE_PROP_DESC, Boolean.TRUE.toString());
        }
    }

    public void unhideSpaceDescription(Space space) {
        if (null != space) {
            this.setSpaceProperty(space, WEBDAV_SPACE_PROP_DESC, Boolean.FALSE.toString());
        }
    }

    public boolean isSpaceDescriptionHidden(Space space) {
        return BooleanUtils.toBoolean((String)this.getSpaceProperty(space, WEBDAV_SPACE_PROP_DESC));
    }

    public void hideAttachment(Attachment attachment) {
        if (null != attachment) {
            this.setContentProperty(attachment.getContainer(), WEBDAV_CONTENT_PROP_ATTACHMENT_PREFIX + attachment.getFileName(), Boolean.TRUE.toString());
        }
    }

    public void unhideAttachment(Attachment attachment) {
        if (null != attachment) {
            this.setContentProperty(attachment.getContainer(), WEBDAV_CONTENT_PROP_ATTACHMENT_PREFIX + attachment.getFileName(), Boolean.FALSE.toString());
        }
    }

    public boolean isAttachmentHidden(Attachment attachment) {
        return BooleanUtils.toBoolean((String)this.getContentProperty(attachment.getContainer(), WEBDAV_CONTENT_PROP_ATTACHMENT_PREFIX + attachment.getFileName()));
    }

    public void hideContent(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT, Boolean.TRUE.toString());
    }

    public void unhideContent(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT, Boolean.FALSE.toString());
    }

    public boolean isContentHidden(ContentEntityObject ceo) {
        return BooleanUtils.toBoolean((String)this.getContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT));
    }

    public void hideContentMarkup(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_MARKUP, Boolean.TRUE.toString());
    }

    public void unhideContentMarkup(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_MARKUP, Boolean.FALSE.toString());
    }

    public boolean isContentMarkupHidden(ContentEntityObject ceo) {
        return BooleanUtils.toBoolean((String)this.getContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_MARKUP));
    }

    public void hideContentUrl(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_URL, Boolean.TRUE.toString());
    }

    public void unhideContentUrl(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_URL, Boolean.FALSE.toString());
    }

    public boolean isContentUrlHidden(ContentEntityObject ceo) {
        return BooleanUtils.toBoolean((String)this.getContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_URL));
    }

    public void hideContentVersions(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_VERSIONS, Boolean.TRUE.toString());
    }

    public void unhideContentVersions(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_VERSIONS, Boolean.FALSE.toString());
    }

    public boolean isContentVersionsHidden(ContentEntityObject ceo) {
        return BooleanUtils.toBoolean((String)this.getContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_VERSIONS));
    }

    public void hideContentVersionText(ContentEntityObject ceo, String version) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_VERSION_TXT_PREFIX + version, Boolean.TRUE.toString());
    }

    public void unhideContentVersionText(ContentEntityObject ceo, String version) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_VERSION_TXT_PREFIX + version, Boolean.FALSE.toString());
    }

    public boolean isContentVersionTextHidden(ContentEntityObject ceo, String version) {
        return BooleanUtils.toBoolean((String)this.getContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_VERSION_TXT_PREFIX + version));
    }

    public void hideContentVersionsReadme(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_VERSIONS_README, Boolean.TRUE.toString());
    }

    public void unhideContentVersionsReadme(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_VERSIONS_README, Boolean.FALSE.toString());
    }

    public boolean isContentVersionsReadmeHidden(ContentEntityObject ceo) {
        return BooleanUtils.toBoolean((String)this.getContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_VERSIONS_README));
    }

    public void hideContentExports(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_EXPORTS, Boolean.TRUE.toString());
    }

    public void unhideContentExports(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_EXPORTS, Boolean.FALSE.toString());
    }

    public boolean isContentExportsHidden(ContentEntityObject ceo) {
        return BooleanUtils.toBoolean((String)this.getContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_EXPORTS));
    }

    public void hideContentPdfExport(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_EXPORTS_PDF, Boolean.TRUE.toString());
    }

    public void unhideContentPdfExport(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_EXPORTS_PDF, Boolean.FALSE.toString());
    }

    public boolean isContentPdfExportHidden(ContentEntityObject ceo) {
        return BooleanUtils.toBoolean((String)this.getContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_EXPORTS_PDF));
    }

    public void hideContentWordExport(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_EXPORTS_WORD, Boolean.TRUE.toString());
    }

    public void unhideContentWordExport(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_EXPORTS_WORD, Boolean.FALSE.toString());
    }

    public boolean isContentWordExportHidden(ContentEntityObject ceo) {
        return BooleanUtils.toBoolean((String)this.getContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_EXPORTS_WORD));
    }

    public void hideContentExportsReadme(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_EXPORTS_README, Boolean.TRUE.toString());
    }

    public void unhideContentExportsReadme(ContentEntityObject ceo) {
        this.setContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_EXPORTS_README, Boolean.FALSE.toString());
    }

    public boolean isContentExportsReadmeHidden(ContentEntityObject ceo) {
        return BooleanUtils.toBoolean((String)this.getContentProperty(ceo, WEBDAV_CONTENT_PROP_CONTENT_EXPORTS_README));
    }

    public void resetContentAttributes(ContentEntityObject ceo) {
        Iterator<AbstractAttributePlaceholder> i = this.contentAttributePlaceHolderToValueMap.keySet().iterator();
        while (i.hasNext()) {
            ContentAttributePlaceHolder contentAttributePlaceHolder;
            AbstractAttributePlaceholder abstractAttributePlaceholder = i.next();
            if (!(abstractAttributePlaceholder instanceof ContentAttributePlaceHolder) || (contentAttributePlaceHolder = (ContentAttributePlaceHolder)abstractAttributePlaceholder).getContentId() != ceo.getId()) continue;
            i.remove();
        }
    }

    private static class SpaceAttributePlaceHolder
    extends AbstractAttributePlaceholder {
        protected String spaceKey;

        SpaceAttributePlaceHolder(Space space, String attributeIdentifer) {
            super(attributeIdentifer);
            this.spaceKey = space.getKey();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            SpaceAttributePlaceHolder that = (SpaceAttributePlaceHolder)o;
            return Objects.equals(this.spaceKey, that.spaceKey);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.attributeIdentifer, this.spaceKey);
        }
    }

    private static class ContentAttributePlaceHolder
    extends AbstractAttributePlaceholder {
        long contentId;

        ContentAttributePlaceHolder(ContentEntityObject ceo, String attributeIdentifer) {
            super(attributeIdentifer);
            this.contentId = ceo.getId();
        }

        long getContentId() {
            return this.contentId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            ContentAttributePlaceHolder that = (ContentAttributePlaceHolder)o;
            return this.contentId == that.contentId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.attributeIdentifer, this.contentId);
        }
    }

    private static abstract class AbstractAttributePlaceholder
    implements Serializable {
        String attributeIdentifer;

        AbstractAttributePlaceholder(String attributeIdentifer) {
            this.attributeIdentifer = attributeIdentifer;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof AbstractAttributePlaceholder)) {
                return false;
            }
            AbstractAttributePlaceholder that = (AbstractAttributePlaceholder)o;
            return Objects.equals(this.attributeIdentifer, that.attributeIdentifer);
        }

        public int hashCode() {
            return Objects.hash(this.attributeIdentifer);
        }
    }
}

