/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.convert;

public enum FileFormat {
    PDF(new String[]{"application/pdf", "application/x-pdf", "application/acrobat", "applications/vnd.pdf", "text/pdf", "text/x-pdf"}),
    XPS(new String[]{"application/vnd.ms-xpsdocument"}),
    DOC(new String[]{"application/msword"}, "DOT"),
    DOCX(new String[]{"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.openxmlformats-officedocument.wordprocessingml.template", "application/vnd.ms-word.document.macroEnabled.12", "application/vnd.ms-word.template.macroEnabled.12"}, "DOCM", "DOTX", "DOTM"),
    ODT(new String[]{"application/vnd.oasis.opendocument.text", "application/vnd.oasis.opendocument.text-template", "application/vnd.oasis.opendocument.text-master", "application/x-vnd.oasis.opendocument.text", "application/x-vnd.oasis.opendocument.text-template", "application/x-vnd.oasis.opendocument.text-master"}, "OTT", "ODM"),
    HTML(new String[]{"text/html"}),
    TXT(new String[]{"text/plain", "application/txt"}),
    RTF(new String[]{"application/rtf", "application/x-rtf", "text/rtf", "text/richtext"}),
    XLS(new String[]{"application/vnd.ms-excel"}, "XLT", "XLA", "XLSB"),
    XLSX(new String[]{"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.openxmlformats-officedocument.spreadsheetml.template", "application/vnd.ms-excel.sheet.macroEnabled.12", "application/vnd.ms-excel.template.macroEnabled.12", "application/vnd.ms-excel.addin.macroEnabled.12"}, "XLSM", "XLTX", "XLAM", "XLTM"),
    ODS(new String[]{"application/vnd.oasis.opendocument.spreadsheet", "application/vnd.oasis.opendocument.spreadsheet-template"}, "OTS"),
    PPT(new String[]{"application/vnd.ms-powerpoint"}, "POT", "PPS"),
    PPTX(new String[]{"application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.openxmlformats-officedocument.presentationml.template", "application/vnd.openxmlformats-officedocument.presentationml.slideshow", "application/vnd.ms-powerpoint.presentation.macroEnabled.12", "application/vnd.ms-powerpoint.template.macroEnabled.12", "application/vnd.ms-powerpoint.slideshow.macroEnabled.12"}, "PPTM", "POTX", "POTM", "PPSX", "PPSM"),
    ODP(new String[]{"application/vnd.oasis.opendocument.presentation", "application/x-vnd.oasis.opendocument.presentation"}),
    GIF(new String[]{"image/gif"}),
    PNG(new String[]{"image/png", "application/png", "application/x-png"}),
    JPG(new String[]{"image/jpeg", "image/jpg", "image/jp_", "application/jpg", "application/x-jpg"}, "JPEG"),
    PSD(new String[]{"image/photoshop", "image/x-photoshop", "image/psd", "application/photoshop", "application/psd", "zz-application/zz-winassoc-psd"}),
    TIF(new String[]{"image/tif", "image/x-tif", "image/tiff", "image/x-tiff", "application/tif", "application/x-tif", "application/tiff", "application/x-tiff"}, "TIFF"),
    WMF(new String[]{"application/x-msmetafile", "application/wmf", "application/x-wmf", "image/wmf", "image/x-wmf", "image/x-win-metafile", "zz-application/zz-winassoc-wmf"}),
    EMF(new String[]{"application/emf", "application/x-emf", "image/x-emf", "image/x-mgx-emf"}),
    ICNS(new String[]{"image/x-apple-icons", "image/icns", "application/icns"}),
    ICO(new String[]{"image/ico", "image/x-icon", "application/ico", "application/x-ico"}),
    MP3(new String[]{"audio/mpeg"}),
    MP4(new String[]{"video/mp4"});

    private final String[] alias;
    private final String[] mimeTypes;

    private FileFormat(String[] mimeTypes) {
        this.mimeTypes = mimeTypes;
        this.alias = null;
    }

    private FileFormat(String[] mimeTypes, String ... alias) {
        this.mimeTypes = mimeTypes;
        this.alias = alias;
    }

    public static FileFormat fromFileName(String name) {
        if (name == null) {
            return null;
        }
        String newName = name.trim().toLowerCase();
        String ext = newName.substring(newName.lastIndexOf(46) + 1);
        String extUpper = ext.toUpperCase();
        try {
            return FileFormat.valueOf(extUpper);
        }
        catch (Exception ex) {
            for (FileFormat fileFormat : FileFormat.values()) {
                String[] aliases = fileFormat.alias;
                if (aliases == null || aliases.length == 0) continue;
                for (String alias : aliases) {
                    if (!extUpper.equals(alias)) continue;
                    return fileFormat;
                }
            }
            return null;
        }
    }

    public static FileFormat fromMimeType(String mimeType) {
        if (mimeType == null) {
            return null;
        }
        String newMimeType = mimeType.trim().toLowerCase();
        for (FileFormat fileFormat : FileFormat.values()) {
            String[] mimeTypes;
            for (String entry : mimeTypes = fileFormat.mimeTypes) {
                if (!entry.equals(newMimeType)) continue;
                return fileFormat;
            }
        }
        return null;
    }

    public String getDefaultMimeType() {
        return this.mimeTypes[0];
    }
}

