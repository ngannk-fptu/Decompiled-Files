/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

public enum PictureType {
    BMP("image/bmp", "bmp", new byte[][]{{66, 77}}),
    EMF("image/x-emf", "emf", new byte[][]{{1, 0, 0, 0}}),
    GIF("image/gif", "gif", new byte[][]{{71, 73, 70}}),
    JPEG("image/jpeg", "jpg", new byte[][]{{-1, -40}}),
    PICT("image/x-pict", ".pict", new byte[0][]),
    PNG("image/png", "png", new byte[][]{{-119, 80, 78, 71, 13, 10, 26, 10}}),
    TIFF("image/tiff", "tiff", new byte[][]{{73, 73, 42, 0}, {77, 77, 0, 42}}),
    UNKNOWN("image/unknown", "", new byte[0][]),
    WMF("image/x-wmf", "wmf", new byte[][]{{-41, -51, -58, -102, 0, 0}, {1, 0, 9, 0, 0, 3}});

    private String _extension;
    private String _mime;
    private byte[][] _signatures;

    public static PictureType findMatchingType(byte[] pictureContent) {
        for (PictureType pictureType : PictureType.values()) {
            for (byte[] signature : pictureType.getSignatures()) {
                if (!PictureType.matchSignature(pictureContent, signature)) continue;
                return pictureType;
            }
        }
        return UNKNOWN;
    }

    private static boolean matchSignature(byte[] pictureData, byte[] signature) {
        if (pictureData.length < signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; ++i) {
            if (pictureData[i] == signature[i]) continue;
            return false;
        }
        return true;
    }

    private PictureType(String mime, String extension, byte[][] signatures) {
        this._mime = mime;
        this._extension = extension;
        this._signatures = (byte[][])signatures.clone();
    }

    public String getExtension() {
        return this._extension;
    }

    public String getMime() {
        return this._mime;
    }

    public byte[][] getSignatures() {
        return this._signatures;
    }

    public boolean matchSignature(byte[] pictureData) {
        for (byte[] signature : this.getSignatures()) {
            if (!PictureType.matchSignature(signature, pictureData)) continue;
            return true;
        }
        return false;
    }
}

