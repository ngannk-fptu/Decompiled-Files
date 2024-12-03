/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFRelation;

public class XWPFPictureData
extends POIXMLDocumentPart {
    private static final int DEFAULT_MAX_IMAGE_SIZE = 100000000;
    private static int MAX_IMAGE_SIZE = 100000000;
    protected static final POIXMLRelation[] RELATIONS = new POIXMLRelation[14];
    private Long checksum;

    public static void setMaxImageSize(int length) {
        MAX_IMAGE_SIZE = length;
    }

    public static int getMaxImageSize() {
        return MAX_IMAGE_SIZE;
    }

    protected XWPFPictureData() {
    }

    public XWPFPictureData(PackagePart part) {
        super(part);
    }

    @Override
    protected void onDocumentRead() throws IOException {
        super.onDocumentRead();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public byte[] getData() {
        try (InputStream stream = this.getPackagePart().getInputStream();){
            byte[] byArray = IOUtils.toByteArrayWithMaxLength(stream, XWPFPictureData.getMaxImageSize());
            return byArray;
        }
        catch (IOException e) {
            throw new POIXMLException(e);
        }
    }

    public String getFileName() {
        String name = this.getPackagePart().getPartName().getName();
        return name.substring(name.lastIndexOf(47) + 1);
    }

    public String suggestFileExtension() {
        return this.getPackagePart().getPartName().getExtension();
    }

    public int getPictureType() {
        String contentType = this.getPackagePart().getContentType();
        for (int i = 0; i < RELATIONS.length; ++i) {
            if (RELATIONS[i] == null || !RELATIONS[i].getContentType().equals(contentType)) continue;
            return i;
        }
        return 0;
    }

    public PictureType getPictureTypeEnum() {
        return PictureType.findByOoxmlId(this.getPictureType());
    }

    public Long getChecksum() {
        if (this.checksum == null) {
            try (InputStream is = this.getPackagePart().getInputStream();){
                this.checksum = IOUtils.calculateChecksum(is);
            }
            catch (IOException e) {
                throw new POIXMLException(e);
            }
        }
        return this.checksum;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof XWPFPictureData)) {
            return false;
        }
        XWPFPictureData picData = (XWPFPictureData)obj;
        PackagePart foreignPackagePart = picData.getPackagePart();
        PackagePart ownPackagePart = this.getPackagePart();
        if (foreignPackagePart != null && ownPackagePart == null || foreignPackagePart == null && ownPackagePart != null) {
            return false;
        }
        if (ownPackagePart != null) {
            OPCPackage foreignPackage = foreignPackagePart.getPackage();
            OPCPackage ownPackage = ownPackagePart.getPackage();
            if (foreignPackage != null && ownPackage == null || foreignPackage == null && ownPackage != null) {
                return false;
            }
            if (ownPackage != null && !ownPackage.equals(foreignPackage)) {
                return false;
            }
        }
        Long foreignChecksum = picData.getChecksum();
        Long localChecksum = this.getChecksum();
        if (localChecksum == null ? foreignChecksum != null : !localChecksum.equals(foreignChecksum)) {
            return false;
        }
        return Arrays.equals(this.getData(), picData.getData());
    }

    public int hashCode() {
        Long checksum = this.getChecksum();
        return checksum == null ? super.hashCode() : checksum.hashCode();
    }

    @Override
    protected void prepareForCommit() {
    }

    static {
        XWPFPictureData.RELATIONS[PictureType.EMF.ooxmlId] = XWPFRelation.IMAGE_EMF;
        XWPFPictureData.RELATIONS[PictureType.WMF.ooxmlId] = XWPFRelation.IMAGE_WMF;
        XWPFPictureData.RELATIONS[PictureType.PICT.ooxmlId] = XWPFRelation.IMAGE_PICT;
        XWPFPictureData.RELATIONS[PictureType.JPEG.ooxmlId] = XWPFRelation.IMAGE_JPEG;
        XWPFPictureData.RELATIONS[PictureType.PNG.ooxmlId] = XWPFRelation.IMAGE_PNG;
        XWPFPictureData.RELATIONS[PictureType.DIB.ooxmlId] = XWPFRelation.IMAGE_DIB;
        XWPFPictureData.RELATIONS[PictureType.GIF.ooxmlId] = XWPFRelation.IMAGE_GIF;
        XWPFPictureData.RELATIONS[PictureType.TIFF.ooxmlId] = XWPFRelation.IMAGE_TIFF;
        XWPFPictureData.RELATIONS[PictureType.EPS.ooxmlId] = XWPFRelation.IMAGE_EPS;
        XWPFPictureData.RELATIONS[PictureType.BMP.ooxmlId] = XWPFRelation.IMAGE_BMP;
        XWPFPictureData.RELATIONS[PictureType.WPG.ooxmlId] = XWPFRelation.IMAGE_WPG;
        XWPFPictureData.RELATIONS[PictureType.WDP.ooxmlId] = XWPFRelation.HDPHOTO_WDP;
    }
}

