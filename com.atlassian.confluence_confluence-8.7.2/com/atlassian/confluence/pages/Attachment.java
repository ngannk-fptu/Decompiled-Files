/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.search.v2.SearchableAttachment
 *  com.atlassian.core.util.FileSize
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.pages;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.HasLinkWikiMarkup;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.VersionChildOwnerPolicy;
import com.atlassian.confluence.pages.AttachmentUtils;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.search.v2.SearchableAttachment;
import com.atlassian.confluence.setup.settings.GlobalDescription;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.util.ConfluenceRenderUtils;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.core.util.FileSize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Attachment
extends SpaceContentEntityObject
implements SearchableAttachment,
Addressable,
HasLinkWikiMarkup,
ContentConvertible,
Contained<ContentEntityObject> {
    public static final String PROP_MEDIA_TYPE = "MEDIA_TYPE";
    public static final String PROP_FILESIZE = "FILESIZE";
    public static final String PROP_MINOR_EDIT = "MINOR_EDIT";
    public static final String PROP_HIDDEN = "HIDDEN";
    public static final String PROP_FILE_STORE_ID = "FILESTORE_ID";
    public static final String CONTENT_TYPE = "attachment";
    public static final String PROFILE_PICTURE_COMMENT = "Uploaded Profile Picture";
    public static final String API_REVISION = "api";
    public static final String API_REVISION_V2 = "v2";
    public static final String DOWNLOAD_PATH_BASE = "/download/attachments/".intern();
    private static final String DEFAULT_MEDIA_TYPE = "application/octet-stream";
    private Collection imageDetailsDTO;

    public Attachment() {
    }

    public Attachment(@NonNull String fileName, @NonNull String mediaType, long fileSize, String versionComment, boolean minorEdit) {
        this.setMediaType(mediaType);
        this.setFileSize(fileSize);
        this.setMinorEdit(minorEdit);
        this.setHidden(false);
        this.setFileName(fileName);
        this.setVersionComment(versionComment);
    }

    public Attachment(@NonNull String fileName, @NonNull String mediaType, long fileSize, String versionComment) {
        this(fileName, mediaType, fileSize, versionComment, false);
    }

    public @NonNull String getFileName() {
        return this.getTitle();
    }

    public void setFileName(@NonNull String fileName) {
        Objects.requireNonNull(fileName);
        this.setTitle(fileName);
    }

    public @NonNull String getMediaType() {
        String mediaType = this.getProperties().getStringProperty(PROP_MEDIA_TYPE);
        return mediaType != null ? mediaType : DEFAULT_MEDIA_TYPE;
    }

    public void setMediaType(@NonNull String mediaType) {
        Objects.requireNonNull(mediaType);
        this.getProperties().setStringProperty(PROP_MEDIA_TYPE, mediaType);
    }

    @Deprecated
    public @NonNull String getContentType() {
        return this.getMediaType();
    }

    @Deprecated
    public void setContentType(@NonNull String contentType) {
        this.setMediaType(contentType);
    }

    @Override
    public @Nullable ContentEntityObject getContainer() {
        return this.getContainerContent();
    }

    public void setContainer(ContentEntityObject content) {
        this.setContainerContent(content);
    }

    @Deprecated
    public String getComment() {
        return this.getVersionComment();
    }

    public boolean isMinorEdit() {
        return this.getProperties().getLongProperty(PROP_MINOR_EDIT, 0L) != 0L;
    }

    public void setMinorEdit(boolean minorEdit) {
        this.getProperties().setLongProperty(PROP_MINOR_EDIT, minorEdit ? 1L : 0L);
    }

    public String getFileStoreId() {
        return this.getProperties().getStringProperty(PROP_FILE_STORE_ID);
    }

    public void setFileStoreId(String fileStoreId) {
        this.getProperties().setStringProperty(PROP_FILE_STORE_ID, fileStoreId);
    }

    public boolean isHidden() {
        return this.getProperties().getLongProperty(PROP_HIDDEN, 0L) != 0L;
    }

    public void setHidden(boolean hidden) {
        this.getProperties().setLongProperty(PROP_HIDDEN, hidden ? 1L : 0L);
    }

    public long getFileSize() {
        return this.getProperties().getLongProperty(PROP_FILESIZE, 0L);
    }

    public void setFileSize(long fileSize) {
        this.getProperties().setLongProperty(PROP_FILESIZE, fileSize);
    }

    public String getNiceFileSize() {
        return FileSize.format((long)this.getFileSize());
    }

    public String getNiceType() {
        return Attachment.getDescriptionForMimeType(this.getMediaType(), this.getFileExtension());
    }

    @Override
    public String getType() {
        return CONTENT_TYPE;
    }

    @Override
    public String getDisplayTitle() {
        return this.getFileName();
    }

    @Override
    public @Nullable String getUrlPath() {
        if (GeneralUtil.getParentPageOrBlog(this) == null) {
            return Objects.requireNonNull(this.getContainer()).getAttachmentUrlPath(this);
        }
        return GeneralUtil.getAttachmentUrl(this);
    }

    @Override
    public Collection<Searchable> getSearchableDependants() {
        return ImmutableList.copyOf((Iterable)Iterables.concat(this.getComments(), this.getAttachments()));
    }

    public boolean isUserProfilePicture() {
        String versionComment = this.getVersionComment();
        if (StringUtils.isNotEmpty((CharSequence)versionComment)) {
            ContentEntityObject container = this.getContainer();
            return versionComment.equals(PROFILE_PICTURE_COMMENT) && container != null && "userinfo".equals(container.getType());
        }
        return false;
    }

    @Override
    public boolean isIndexable() {
        ContentEntityObject content = this.getContainer();
        if (content == null || content instanceof Draft || content instanceof GlobalDescription || this.isHidden()) {
            return false;
        }
        return super.isIndexable() && content.isCurrent();
    }

    public String getDownloadPath() {
        return this.getDownloadPath(ConfluenceRenderUtils.getAttachmentsPathForContent(this.getContainer()), true);
    }

    public String getDownloadPathWithoutVersion() {
        return this.getDownloadPath(ConfluenceRenderUtils.getAttachmentsPathForContent(this.getContainer()), false);
    }

    public String getDownloadPathWithoutVersionOrApiRevision() {
        return this.getDownloadPath(ConfluenceRenderUtils.getAttachmentsPathForContent(this.getContainer()), false, false);
    }

    public String getDownloadPath(String attachmentPath, boolean addVersionInfo) {
        return this.getDownloadPath(attachmentPath, addVersionInfo, true);
    }

    private String getDownloadPath(String attachmentPath, boolean addVersionInfo, boolean addApiRevision) {
        String fileName = HtmlUtil.urlEncode(this.getFileName(), "UTF-8").replaceAll("\\+", "%20");
        Object path = attachmentPath == null ? "" : attachmentPath + "/";
        UrlBuilder builder = new UrlBuilder((String)path + fileName, "UTF-8");
        if (addVersionInfo) {
            builder.add("version", this.getVersion());
            if (this.getLastModificationDate() != null) {
                builder.add("modificationDate", this.getLastModificationDate().getTime());
            }
        }
        if (addApiRevision) {
            builder.add(API_REVISION, API_REVISION_V2);
        }
        return builder.toString();
    }

    public String getDownloadPathWithoutEncoding() {
        return DOWNLOAD_PATH_BASE + this.getContainer().getId() + "/" + this.getFileName();
    }

    public String getExportPath() {
        Object fileExtension = this.getFileExtension();
        if (!((String)fileExtension).matches("[a-zA-Z0-9]+")) {
            fileExtension = "";
        }
        if (((String)fileExtension).length() != 0) {
            fileExtension = "." + (String)fileExtension;
        }
        return "attachments/" + this.getContainer().getId() + "/" + this.getId() + (String)fileExtension;
    }

    public String getExportPathForThumbnail() {
        return "attachments/thumbnails/" + this.getContainer().getId() + "/" + this.getId();
    }

    @Deprecated
    public InputStream getContentsAsStream() throws IOException {
        return AttachmentUtils.getLatestAttachmentStream(this);
    }

    @Override
    public String toString() {
        return "Attachment: " + this.getFileName() + " v." + this.getVersion() + " (" + this.getId() + ") " + this.getLastModifierName();
    }

    @Override
    public String getNameForComparison() {
        return this.getTitle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attachment)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Attachment that = (Attachment)o;
        return this.getFileSize() == that.getFileSize() && this.isMinorEdit() == that.isMinorEdit() && Objects.equals(this.getContainer(), that.getContainer()) && this.isHidden() == that.isHidden() && this.getMediaType().equals(that.getMediaType());
    }

    @Override
    public int hashCode() {
        ContentEntityObject container = this.getContainer();
        int result = super.hashCode();
        result = 31 * result + this.getMediaType().hashCode();
        result = 31 * result + (container != null ? container.hashCode() : 0);
        result = 31 * result + (int)(this.getFileSize() ^ this.getFileSize() >>> 32);
        result = 31 * result + (this.isMinorEdit() ? 1 : 0);
        result = 31 * result + (this.isHidden() ? 1 : 0);
        return result;
    }

    public String getFileExtension() {
        String fileName = this.getFileName();
        if (fileName == null) {
            return "";
        }
        int indexOfDot = fileName.lastIndexOf(".");
        if (indexOfDot == -1) {
            return "";
        }
        return fileName.substring(indexOfDot + 1).toLowerCase();
    }

    public static String getDescriptionForMimeType(String mimeType, String fileExtension) {
        Type type = Type.getForMimeType(mimeType, fileExtension);
        return type == null ? null : type.getDescription();
    }

    @Override
    public Space getSpace() {
        ContentEntityObject content = this.getContainer();
        if (content instanceof Spaced) {
            return ((Spaced)((Object)content)).getSpace();
        }
        return super.getSpace();
    }

    @Override
    public String getLinkWikiMarkup() {
        ContentEntityObject parent = this.getContainer();
        if (parent instanceof HasLinkWikiMarkup) {
            String markup = ((HasLinkWikiMarkup)((Object)parent)).getLinkWikiMarkup();
            String markupBase = markup.substring(1, markup.length() - 1);
            return String.format("[%s^%s]", markupBase, this.getFileName());
        }
        if (parent instanceof Draft) {
            return String.format("[^%s]", this.getFileName());
        }
        throw new IllegalStateException("Can't get wiki-markup link for attachment: " + this + " with parent: " + parent);
    }

    protected Collection getImageDetailsDTO() {
        return this.imageDetailsDTO;
    }

    protected void setImageDetailsDTO(Set imageDetailDTOs) {
        this.imageDetailsDTO = imageDetailDTOs;
    }

    @Override
    public Object clone() {
        Attachment attachment = (Attachment)super.clone();
        attachment.setImageDetailsDTO(new HashSet());
        return attachment;
    }

    public Attachment copy() {
        return (Attachment)this.clone();
    }

    @Override
    public VersionChildOwnerPolicy getVersionChildPolicy(ContentType contentType) {
        return contentType.equals((Object)ContentType.COMMENT) || contentType.equals((Object)ContentType.ATTACHMENT) ? VersionChildOwnerPolicy.originalVersion : VersionChildOwnerPolicy.currentVersion;
    }

    @Override
    public ContentType getContentTypeObject() {
        return ContentType.ATTACHMENT;
    }

    @Override
    public ContentId getContentId() {
        return ContentId.of((ContentType)ContentType.ATTACHMENT, (long)this.getId());
    }

    @Override
    public boolean shouldConvertToContent() {
        return true;
    }

    public Attachment copyLatestVersion() {
        Attachment attachmentCopy = new Attachment();
        attachmentCopy.setImageDetailsDTO(new HashSet());
        attachmentCopy.setFileName(this.getFileName());
        attachmentCopy.setFileSize(this.getFileSize());
        attachmentCopy.setContentPropertiesFrom(this);
        attachmentCopy.setContentType(this.getContentType());
        return attachmentCopy;
    }

    public static enum Type {
        PDF("PDF Document", null, new String[]{"application/pdf"}, new String[]{"pdf"}),
        IMAGE("Image", new String[]{"image"}, null, new String[]{"png"}),
        MULTIMEDIA("Multimedia", new String[]{"audio", "video/quicktime", "application/x-shockwave-flash", "video/mp4", "application/vnd.rn-realmedia", "video/x-msvideo", "application/x-oleobject"}, null, new String[]{"rm", "ram", "mpeg", "mpg", "wmv", "wma"}),
        XML("XML File", null, new String[]{"text/xml"}, null),
        HTML("HTML Document", null, new String[]{"text/html"}, null),
        JS("JavaScript File", null, new String[]{"application/javascript", "application/x-javascript", "text/javascript"}, new String[]{"js"}),
        CSS("CSS File", null, new String[]{"text/css"}, new String[]{"css"}),
        TEXT("Text File", null, new String[]{"text/plain"}, null),
        WORD("Word Document", null, new String[]{"application/msword"}, new String[]{"doc", "docx"}),
        EXCEL("Excel Spreadsheet", null, new String[]{"application/vnd.ms-excel"}, new String[]{"xls", "xlsx"}),
        POWERPOINT("PowerPoint Presentation", null, new String[]{"application/vnd.ms-powerpoint"}, new String[]{"ppt", "pptx"}),
        JAVA_SOURCE("Java Source File", null, null, new String[]{"java"}),
        JAVA_ARCHIVE("Java Archive", null, null, new String[]{"jar", "war", "ear"}),
        ZIP("Zip Archive", null, null, new String[]{"zip"});

        private final String description;
        private final String[] mimeTypesStartsWith;
        private final String[] mimeTypes;
        private final String[] extensions;
        private static final Map<String, Type> mimeTypeStartsWithMap;
        private static final Map<String, Type> mimeTypeMap;
        private static final Map<String, Type> extensionMap;

        private Type(String description, String[] mimeTypesStartsWith, String[] mimeTypes, String[] extensions) {
            this.description = description;
            this.mimeTypesStartsWith = mimeTypesStartsWith;
            this.mimeTypes = mimeTypes;
            this.extensions = extensions;
        }

        public static Type getForMimeType(String mimeType, String fileExtension) {
            Type type = mimeTypeMap.get(mimeType);
            if (type == null) {
                type = extensionMap.get(fileExtension);
            }
            if (type == null && StringUtils.isNotBlank((CharSequence)mimeType)) {
                for (Map.Entry<String, Type> entry : mimeTypeStartsWithMap.entrySet()) {
                    if (!mimeType.startsWith(entry.getKey())) continue;
                    return entry.getValue();
                }
            }
            return type;
        }

        public String getDescription() {
            return this.description;
        }

        public static Set<Type> getTypes(Set<String> typeStrs) {
            HashSet<Type> types = new HashSet<Type>();
            if (typeStrs != null) {
                for (String typeStr : typeStrs) {
                    Type type = Type.valueOf(typeStr.toUpperCase());
                    if (type != null) {
                        types.add(type);
                        continue;
                    }
                    throw new IllegalArgumentException("Type string is not a known Attachment.Type : " + typeStr);
                }
            }
            return types;
        }

        static {
            mimeTypeStartsWithMap = new HashMap<String, Type>();
            mimeTypeMap = new HashMap<String, Type>();
            extensionMap = new HashMap<String, Type>();
            for (Type type : Type.values()) {
                if (type.mimeTypesStartsWith != null) {
                    for (String mimeType : type.mimeTypesStartsWith) {
                        mimeTypeStartsWithMap.put(mimeType, type);
                    }
                }
                if (type.mimeTypes != null) {
                    for (String mimeType : type.mimeTypes) {
                        mimeTypeMap.put(mimeType, type);
                    }
                }
                if (type.extensions == null) continue;
                for (String extension : type.extensions) {
                    extensionMap.put(extension, type);
                }
            }
        }
    }
}

