/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.fugue.Pair
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.fugue.Pair;

public class AttachmentMediaTypeConverter {
    public static String getIconResourceName(Attachment input) {
        return (String)AttachmentMediaTypeConverter.getAttachmentIcon(input).right() + "-icon";
    }

    public static String getFileTypeI18nKey(Attachment input) {
        return (String)AttachmentMediaTypeConverter.getAttachmentIcon(input).left();
    }

    private static Pair<String, String> getAttachmentIcon(Attachment attachment) {
        String fileName = attachment.getFileName() != null ? attachment.getFileName() : attachment.getDisplayTitle();
        String mediaType = attachment.getMediaType();
        if (fileName.endsWith(".pdf") || mediaType.equals("application/pdf")) {
            return new Pair((Object)"pdf.file", (Object)"pdf");
        }
        if (mediaType.startsWith("image/gif") || fileName.endsWith(".gif")) {
            return new Pair((Object)"gif.file", (Object)"image");
        }
        if (mediaType.startsWith("image/jpeg") || fileName.endsWith(".jpeg")) {
            return new Pair((Object)"jpeg.file", (Object)"image");
        }
        if (mediaType.startsWith("image/jpeg") || fileName.endsWith(".jpg")) {
            return new Pair((Object)"jpeg.file", (Object)"image");
        }
        if (mediaType.startsWith("image/png") || fileName.endsWith(".png")) {
            return new Pair((Object)"png.file", (Object)"image");
        }
        if (mediaType.startsWith("text/xml") || fileName.endsWith(".xml")) {
            return new Pair((Object)"xml.file", (Object)"xml");
        }
        if (mediaType.startsWith("text/html") || fileName.endsWith(".html")) {
            return new Pair((Object)"html.file", (Object)"xml");
        }
        if (fileName.endsWith(".java") || fileName.endsWith(".jar")) {
            return new Pair((Object)"java.file", (Object)"java");
        }
        if (mediaType.startsWith("text/plain")) {
            return new Pair((Object)"text.file", (Object)"generic");
        }
        if (mediaType.startsWith("application") && mediaType.contains("zip")) {
            return new Pair((Object)"zip.file", (Object)"zip");
        }
        if (fileName.endsWith(".xlt")) {
            return new Pair((Object)"excel97.template.file", (Object)"xls");
        }
        if (fileName.endsWith(".pot")) {
            return new Pair((Object)"powerpoint97.template.file", (Object)"ppt");
        }
        if (fileName.endsWith(".dot")) {
            return new Pair((Object)"word97.template.file", (Object)"doc");
        }
        if (fileName.endsWith(".xls") || mediaType.startsWith("application/vnd.ms-excel")) {
            return new Pair((Object)"excel97.file", (Object)"xls");
        }
        if (fileName.endsWith(".ppt") || mediaType.startsWith("application/vnd.ms-powerpoint")) {
            return new Pair((Object)"powerpoint97.file", (Object)"ppt");
        }
        if (fileName.endsWith(".doc") || mediaType.startsWith("application/msword")) {
            return new Pair((Object)"word97.file", (Object)"doc");
        }
        if (fileName.endsWith(".xlsm")) {
            return new Pair((Object)"excel.macro.file", (Object)"xls");
        }
        if (fileName.endsWith(".xlsx") || mediaType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return new Pair((Object)"excel.file", (Object)"xls");
        }
        if (fileName.endsWith(".xlst") || mediaType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.template")) {
            return new Pair((Object)"excel.template.file", (Object)"xls");
        }
        if (fileName.endsWith(".pptm")) {
            return new Pair((Object)"powerpoint.macro.file", (Object)"ppt");
        }
        if (fileName.endsWith(".pptx") || mediaType.startsWith("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
            return new Pair((Object)"powerpoint.file", (Object)"ppt");
        }
        if (fileName.endsWith(".ppsx") || mediaType.startsWith("application/vnd.openxmlformats-officedocument.presentationml.slideshow")) {
            return new Pair((Object)"powerpoint.slideshow.file", (Object)"ppt");
        }
        if (fileName.endsWith(".potx") || mediaType.startsWith("application/vnd.openxmlformats-officedocument.presentationml.template")) {
            return new Pair((Object)"powerpoint.template.file", (Object)"ppt");
        }
        if (fileName.endsWith(".docx") || mediaType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return new Pair((Object)"word.file", (Object)"doc");
        }
        if (fileName.endsWith(".dotx") || mediaType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.template")) {
            return new Pair((Object)"word.template.file", (Object)"doc");
        }
        if (fileName.endsWith(".swf") || mediaType.startsWith("application/x-shockwave-flash")) {
            return new Pair((Object)"multimedia.file", (Object)"multimedia");
        }
        if (fileName.endsWith(".mov") || mediaType.startsWith("video/quicktime")) {
            return new Pair((Object)"multimedia.file", (Object)"multimedia");
        }
        if (fileName.endsWith(".wma") || mediaType.startsWith("audio/x-ms-wma")) {
            return new Pair((Object)"multimedia.file", (Object)"multimedia");
        }
        if (fileName.endsWith(".wmv") || mediaType.startsWith("audio/x-ms-wmv")) {
            return new Pair((Object)"multimedia.file", (Object)"multimedia");
        }
        if (fileName.endsWith(".mpeg") || mediaType.startsWith("video/mpeg")) {
            return new Pair((Object)"multimedia.file", (Object)"multimedia");
        }
        if (fileName.endsWith(".mpg") || mediaType.startsWith("video/mpeg")) {
            return new Pair((Object)"multimedia.file", (Object)"multimedia");
        }
        if (fileName.endsWith(".ram") || mediaType.startsWith("audio/x-pn-realaudio")) {
            return new Pair((Object)"multimedia.file", (Object)"multimedia");
        }
        if (fileName.endsWith(".rm") || mediaType.startsWith("application/vnd.rn-realmedia")) {
            return new Pair((Object)"multimedia.file", (Object)"multimedia");
        }
        if (fileName.endsWith(".mp3") || mediaType.startsWith("audio/mpeg")) {
            return new Pair((Object)"multimedia.file", (Object)"multimedia");
        }
        if (fileName.endsWith(".avi") || mediaType.startsWith("video/x-msvideo")) {
            return new Pair((Object)"multimedia.file", (Object)"multimedia");
        }
        return new Pair((Object)"unknown.file", (Object)"generic");
    }
}

