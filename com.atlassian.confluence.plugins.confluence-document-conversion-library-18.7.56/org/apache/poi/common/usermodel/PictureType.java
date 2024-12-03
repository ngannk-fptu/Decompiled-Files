/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.common.usermodel;

import java.util.HashMap;
import org.apache.poi.poifs.filesystem.FileMagic;

public enum PictureType {
    EMF("image/x-emf", ".emf", 2),
    WMF("image/x-wmf", ".wmf", 3),
    PICT("image/x-pict", ".pict", 4),
    JPEG("image/jpeg", ".jpg", 5),
    PNG("image/png", ".png", 6),
    DIB("image/dib", ".dib", 7),
    GIF("image/gif", ".gif", 8),
    TIFF("image/tiff", ".tif", 9),
    EPS("image/x-eps", ".eps", 10),
    BMP("image/x-ms-bmp", ".bmp", 11),
    WPG("image/x-wpg", ".wpg", 12),
    WDP("image/vnd.ms-photo", ".wdp", 13),
    SVG("image/svg+xml", ".svg", -1),
    UNKNOWN("", ".dat", -1),
    ERROR("", ".dat", -1),
    CMYKJPEG("image/jpeg", ".jpg", -1),
    CLIENT("", ".dat", -1);

    private static final HashMap<Integer, PictureType> PICTURE_TYPE_BY_OOXML_ID;
    public final String contentType;
    public final String extension;
    public final int ooxmlId;

    private PictureType(String contentType, String extension, int ooxmlId) {
        this.contentType = contentType;
        this.extension = extension;
        this.ooxmlId = ooxmlId;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getExtension() {
        return this.extension;
    }

    public int getOoxmlId() {
        return this.ooxmlId;
    }

    public static PictureType valueOf(FileMagic fm) {
        switch (fm) {
            case BMP: {
                return BMP;
            }
            case GIF: {
                return GIF;
            }
            case JPEG: {
                return JPEG;
            }
            case PNG: {
                return PNG;
            }
            case XML: {
                return SVG;
            }
            case WMF: {
                return WMF;
            }
            case EMF: {
                return EMF;
            }
            case TIFF: {
                return TIFF;
            }
        }
        return UNKNOWN;
    }

    public static PictureType findByOoxmlId(int ooxmlId) {
        return PICTURE_TYPE_BY_OOXML_ID.get(ooxmlId);
    }

    static {
        PICTURE_TYPE_BY_OOXML_ID = new HashMap();
        for (PictureType pictureType : PictureType.values()) {
            if (pictureType.ooxmlId < -1) continue;
            PICTURE_TYPE_BY_OOXML_ID.put(pictureType.ooxmlId, pictureType);
        }
    }
}

