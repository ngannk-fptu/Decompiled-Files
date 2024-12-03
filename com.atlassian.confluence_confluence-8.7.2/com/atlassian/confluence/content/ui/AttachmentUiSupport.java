/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.ui;

import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class AttachmentUiSupport
implements ContentUiSupport<Attachment> {
    private static final Map<String, AttachmentInfo> fileExtensionMap;
    private static final Map<String, AttachmentInfo> mimeTypeMap;
    private final WebResourceUrlProvider webResourceUrlProvider;

    public AttachmentUiSupport(WebResourceUrlProvider webResourceUrlProvider) {
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    @Override
    public String getIconFilePath(Attachment content, int size) {
        return this.getPathImpl(content);
    }

    @Override
    public String getIconPath(Attachment content, int size) {
        return this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + this.getPathImpl(content);
    }

    private String getPathImpl(Attachment content) {
        String fileExtension = this.getFileExtension(content.getTitle());
        String mimeType = content.getMediaType();
        return AttachmentUiSupport.getAttachmentInfo(mimeType, fileExtension).getIconFilePath();
    }

    @Override
    public String getLegacyIconPath(String contentType, int size) {
        return this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + AttachmentInfo.UNKNOWN.getIconFilePath();
    }

    @Override
    public String getIconCssClass(Attachment content) {
        String fileExtension = this.getFileExtension(content.getTitle());
        String mimeType = content.getMediaType();
        return AttachmentUiSupport.getAttachmentInfo(mimeType, fileExtension).getCssClass();
    }

    @Override
    public String getContentCssClass(String contentType, String contentPluginKey) {
        return AttachmentInfo.UNKNOWN.getCssClass();
    }

    @Override
    public String getContentCssClass(Attachment content) {
        String fileExtension = this.getFileExtension(content.getTitle());
        String mimeType = content.getMediaType();
        return AttachmentUiSupport.getAttachmentInfo(mimeType, fileExtension).getContentCssClass();
    }

    @Override
    public String getIconCssClass(SearchResult result) {
        String fileExtension = this.getFileExtension(result.getDisplayTitle());
        String mimeType = result.getField("attachment-mime-type");
        return AttachmentUiSupport.getAttachmentInfo(mimeType, fileExtension).getCssClass();
    }

    @Override
    public String getContentTypeI18NKey(Attachment content) {
        String fileExtension = this.getFileExtension(content.getTitle());
        String mimeType = content.getMediaType();
        return AttachmentUiSupport.getAttachmentInfo(mimeType, fileExtension).getI18nKey();
    }

    @Override
    public String getContentTypeI18NKey(SearchResult result) {
        String fileExtension = this.getFileExtension(result.getDisplayTitle());
        String mimeType = result.getField("attachment-mime-type");
        return AttachmentUiSupport.getAttachmentInfo(mimeType, fileExtension).getI18nKey();
    }

    private String getFileExtension(String filename) {
        return StringUtils.substringAfterLast((String)filename, (String)".");
    }

    @Deprecated
    public static AttachmentInfo getAttachmentInfo(String mimeType, String fileExtension) {
        AttachmentInfo attachmentInfo = mimeTypeMap.get(mimeType);
        if (attachmentInfo == null) {
            attachmentInfo = fileExtensionMap.get(fileExtension);
        }
        if (attachmentInfo == null) {
            attachmentInfo = AttachmentInfo.UNKNOWN;
        }
        return attachmentInfo;
    }

    static {
        ImmutableMap.Builder fileExtensionMapBuilder = ImmutableMap.builder();
        fileExtensionMapBuilder.put((Object)"pdf", (Object)AttachmentInfo.PDF);
        fileExtensionMapBuilder.put((Object)"gif", (Object)AttachmentInfo.GIF);
        fileExtensionMapBuilder.put((Object)"jpeg", (Object)AttachmentInfo.JPEG);
        fileExtensionMapBuilder.put((Object)"jpg", (Object)AttachmentInfo.JPEG);
        fileExtensionMapBuilder.put((Object)"png", (Object)AttachmentInfo.PNG);
        fileExtensionMapBuilder.put((Object)"xml", (Object)AttachmentInfo.XML);
        fileExtensionMapBuilder.put((Object)"html", (Object)AttachmentInfo.HTML);
        fileExtensionMapBuilder.put((Object)"css", (Object)AttachmentInfo.CSS);
        fileExtensionMapBuilder.put((Object)"js", (Object)AttachmentInfo.JS);
        fileExtensionMapBuilder.put((Object)"java", (Object)AttachmentInfo.JAVA);
        fileExtensionMapBuilder.put((Object)"jar", (Object)AttachmentInfo.JAR);
        fileExtensionMapBuilder.put((Object)"war", (Object)AttachmentInfo.JAR);
        fileExtensionMapBuilder.put((Object)"ear", (Object)AttachmentInfo.JAR);
        fileExtensionMapBuilder.put((Object)"zip", (Object)AttachmentInfo.ZIP);
        fileExtensionMapBuilder.put((Object)"xlt", (Object)AttachmentInfo.EXCEL97_TEMPLATE);
        fileExtensionMapBuilder.put((Object)"pot", (Object)AttachmentInfo.POWERPOINT97_TEMPLATE);
        fileExtensionMapBuilder.put((Object)"dot", (Object)AttachmentInfo.WORD97_TEMPLATE);
        fileExtensionMapBuilder.put((Object)"xls", (Object)AttachmentInfo.XLS);
        fileExtensionMapBuilder.put((Object)"ppt", (Object)AttachmentInfo.PPT);
        fileExtensionMapBuilder.put((Object)"doc", (Object)AttachmentInfo.DOC);
        fileExtensionMapBuilder.put((Object)"xlsm", (Object)AttachmentInfo.EXCEL_MACRO);
        fileExtensionMapBuilder.put((Object)"xlsx", (Object)AttachmentInfo.XLSX);
        fileExtensionMapBuilder.put((Object)"xlst", (Object)AttachmentInfo.EXCEL_TEMPLATE);
        fileExtensionMapBuilder.put((Object)"pptm", (Object)AttachmentInfo.POWERPOINT_MACRO);
        fileExtensionMapBuilder.put((Object)"pptx", (Object)AttachmentInfo.PPTX);
        fileExtensionMapBuilder.put((Object)"ppsx", (Object)AttachmentInfo.POWERPOINT_SLIDESHOW);
        fileExtensionMapBuilder.put((Object)"potx", (Object)AttachmentInfo.POWERPOINT_TEMPLATE);
        fileExtensionMapBuilder.put((Object)"docx", (Object)AttachmentInfo.DOCX);
        fileExtensionMapBuilder.put((Object)"dotx", (Object)AttachmentInfo.WORD_TEMPLATE);
        fileExtensionMapBuilder.put((Object)"swf", (Object)AttachmentInfo.MULTIMEDIA);
        fileExtensionMapBuilder.put((Object)"mov", (Object)AttachmentInfo.MULTIMEDIA);
        fileExtensionMapBuilder.put((Object)"wma", (Object)AttachmentInfo.MULTIMEDIA);
        fileExtensionMapBuilder.put((Object)"wmv", (Object)AttachmentInfo.MULTIMEDIA);
        fileExtensionMapBuilder.put((Object)"mpeg", (Object)AttachmentInfo.MULTIMEDIA);
        fileExtensionMapBuilder.put((Object)"mpg", (Object)AttachmentInfo.MULTIMEDIA);
        fileExtensionMapBuilder.put((Object)"ram", (Object)AttachmentInfo.MULTIMEDIA);
        fileExtensionMapBuilder.put((Object)"rm", (Object)AttachmentInfo.MULTIMEDIA);
        fileExtensionMapBuilder.put((Object)"mp3", (Object)AttachmentInfo.MULTIMEDIA);
        fileExtensionMapBuilder.put((Object)"mp4", (Object)AttachmentInfo.MULTIMEDIA);
        fileExtensionMapBuilder.put((Object)"avi", (Object)AttachmentInfo.MULTIMEDIA);
        ImmutableMap.Builder mimeTypeMapBuilder = ImmutableMap.builder();
        mimeTypeMapBuilder.put((Object)"application/pdf", (Object)AttachmentInfo.PDF);
        mimeTypeMapBuilder.put((Object)"image/gif", (Object)AttachmentInfo.GIF);
        mimeTypeMapBuilder.put((Object)"image/jpeg", (Object)AttachmentInfo.JPEG);
        mimeTypeMapBuilder.put((Object)"image/png", (Object)AttachmentInfo.PNG);
        mimeTypeMapBuilder.put((Object)"text/xml", (Object)AttachmentInfo.XML);
        mimeTypeMapBuilder.put((Object)"text/html", (Object)AttachmentInfo.HTML);
        mimeTypeMapBuilder.put((Object)"text/css", (Object)AttachmentInfo.CSS);
        mimeTypeMapBuilder.put((Object)"text/javascript", (Object)AttachmentInfo.JS);
        mimeTypeMapBuilder.put((Object)"text/plain", (Object)AttachmentInfo.TEXT);
        mimeTypeMapBuilder.put((Object)"application/zip", (Object)AttachmentInfo.ZIP);
        mimeTypeMapBuilder.put((Object)"application/x-zip", (Object)AttachmentInfo.ZIP);
        mimeTypeMapBuilder.put((Object)"application/x-zip-compressed", (Object)AttachmentInfo.ZIP);
        mimeTypeMapBuilder.put((Object)"application/java-archive", (Object)AttachmentInfo.JAR);
        mimeTypeMapBuilder.put((Object)"application/vnd.ms-excel", (Object)AttachmentInfo.XLS);
        mimeTypeMapBuilder.put((Object)"application/vnd.ms-powerpoint", (Object)AttachmentInfo.PPT);
        mimeTypeMapBuilder.put((Object)"application/msword", (Object)AttachmentInfo.DOC);
        mimeTypeMapBuilder.put((Object)"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", (Object)AttachmentInfo.XLSX);
        mimeTypeMapBuilder.put((Object)"application/vnd.openxmlformats-officedocument.spreadsheetml.template", (Object)AttachmentInfo.EXCEL_TEMPLATE);
        mimeTypeMapBuilder.put((Object)"application/vnd.openxmlformats-officedocument.presentationml.presentation", (Object)AttachmentInfo.PPTX);
        mimeTypeMapBuilder.put((Object)"application/vnd.openxmlformats-officedocument.presentationml.slideshow", (Object)AttachmentInfo.POWERPOINT_SLIDESHOW);
        mimeTypeMapBuilder.put((Object)"application/vnd.openxmlformats-officedocument.presentationml.template", (Object)AttachmentInfo.POWERPOINT_TEMPLATE);
        mimeTypeMapBuilder.put((Object)"application/vnd.openxmlformats-officedocument.wordprocessingml.document", (Object)AttachmentInfo.DOCX);
        mimeTypeMapBuilder.put((Object)"application/vnd.openxmlformats-officedocument.wordprocessingml.template", (Object)AttachmentInfo.WORD_TEMPLATE);
        mimeTypeMapBuilder.put((Object)"application/x-shockwave-flash", (Object)AttachmentInfo.MULTIMEDIA);
        mimeTypeMapBuilder.put((Object)"video/quicktime", (Object)AttachmentInfo.MULTIMEDIA);
        mimeTypeMapBuilder.put((Object)"audio/x-ms-wma", (Object)AttachmentInfo.MULTIMEDIA);
        mimeTypeMapBuilder.put((Object)"audio/x-ms-wmv", (Object)AttachmentInfo.MULTIMEDIA);
        mimeTypeMapBuilder.put((Object)"video/mpeg", (Object)AttachmentInfo.MULTIMEDIA);
        mimeTypeMapBuilder.put((Object)"audio/x-pn-realaudio", (Object)AttachmentInfo.MULTIMEDIA);
        mimeTypeMapBuilder.put((Object)"application/vnd.rn-realmedia", (Object)AttachmentInfo.MULTIMEDIA);
        mimeTypeMapBuilder.put((Object)"audio/mpeg", (Object)AttachmentInfo.MULTIMEDIA);
        mimeTypeMapBuilder.put((Object)"video/x-msvideo", (Object)AttachmentInfo.MULTIMEDIA);
        mimeTypeMapBuilder.put((Object)"video/mp4", (Object)AttachmentInfo.MULTIMEDIA);
        mimeTypeMapBuilder.put((Object)"audio/mp4", (Object)AttachmentInfo.MULTIMEDIA);
        mimeTypeMapBuilder.put((Object)"audio/mp3", (Object)AttachmentInfo.MULTIMEDIA);
        fileExtensionMap = fileExtensionMapBuilder.build();
        mimeTypeMap = mimeTypeMapBuilder.build();
    }

    @Deprecated
    public static class AttachmentInfo {
        public static final AttachmentInfo PDF = new AttachmentInfo("pdf", "pdf.file");
        public static final AttachmentInfo GIF = new AttachmentInfo("image", "gif.file");
        public static final AttachmentInfo JPEG = new AttachmentInfo("image", "jpeg.file");
        public static final AttachmentInfo PNG = new AttachmentInfo("image", "png.file");
        public static final AttachmentInfo XML = new AttachmentInfo("xml", "xml.file");
        public static final AttachmentInfo HTML = new AttachmentInfo("html", "html.file");
        public static final AttachmentInfo CSS = new AttachmentInfo("css", "css.file");
        public static final AttachmentInfo JS = new AttachmentInfo("js", "js.file");
        public static final AttachmentInfo JAVA = new AttachmentInfo("java", "java.file");
        public static final AttachmentInfo JAR = new AttachmentInfo("jar", "jar.file");
        public static final AttachmentInfo TEXT = new AttachmentInfo("text", "text.file");
        public static final AttachmentInfo ZIP = new AttachmentInfo("zip", "zip.file");
        public static final AttachmentInfo EXCEL97_TEMPLATE = new AttachmentInfo("excel97-template", "excel97.template.file");
        public static final AttachmentInfo POWERPOINT97_TEMPLATE = new AttachmentInfo("powerpoint97-template", "powerpoint97.template.file");
        public static final AttachmentInfo WORD97_TEMPLATE = new AttachmentInfo("word97-template", "word97.template.file");
        public static final AttachmentInfo XLS = new AttachmentInfo("excel97", "excel97.file");
        public static final AttachmentInfo PPT = new AttachmentInfo("powerpoint97", "powerpoint97.file");
        public static final AttachmentInfo DOC = new AttachmentInfo("word97", "word97.file");
        public static final AttachmentInfo EXCEL_MACRO = new AttachmentInfo("excel-macro", "excel.macro.file");
        public static final AttachmentInfo XLSX = new AttachmentInfo("excel", "excel.file");
        public static final AttachmentInfo EXCEL_TEMPLATE = new AttachmentInfo("excel-template", "excel.template.file");
        public static final AttachmentInfo POWERPOINT_MACRO = new AttachmentInfo("powerpoint-macro", "powerpoint.macro.file");
        public static final AttachmentInfo PPTX = new AttachmentInfo("powerpoint", "powerpoint.file");
        public static final AttachmentInfo POWERPOINT_SLIDESHOW = new AttachmentInfo("powerpoint-slideshow", "powerpoint.slideshow.file");
        public static final AttachmentInfo POWERPOINT_TEMPLATE = new AttachmentInfo("powerpoint-template", "powerpoint.template.file");
        public static final AttachmentInfo DOCX = new AttachmentInfo("word", "word.file");
        public static final AttachmentInfo WORD_TEMPLATE = new AttachmentInfo("word-template", "word.template.file");
        public static final AttachmentInfo MULTIMEDIA = new AttachmentInfo("multimedia", "multimedia.file");
        public static final AttachmentInfo UNKNOWN = new AttachmentInfo("unknown", "unknown.file");
        private final String id;
        private final String i18nKey;

        private AttachmentInfo(String id, String i18nKey) {
            this.id = id;
            this.i18nKey = i18nKey;
        }

        public String getCssClass() {
            return "content-type-attachment-" + this.id;
        }

        public String getContentCssClass() {
            return "content-type-attachment-" + this.id;
        }

        public String getIdentifier() {
            return this.id;
        }

        public String getIconFilePath() {
            return "/images/icons/attachments/" + this.id + "_16.png";
        }

        public String getI18nKey() {
            return this.i18nKey;
        }
    }
}

