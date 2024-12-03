/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFRelation;

public class XSSFPictureData
extends POIXMLDocumentPart
implements PictureData {
    private static final int DEFAULT_MAX_IMAGE_SIZE = 100000000;
    private static int MAX_IMAGE_SIZE = 100000000;
    protected static final POIXMLRelation[] RELATIONS = new POIXMLRelation[13];

    public static void setMaxImageSize(int length) {
        MAX_IMAGE_SIZE = length;
    }

    public static int getMaxImageSize() {
        return MAX_IMAGE_SIZE;
    }

    protected XSSFPictureData() {
    }

    protected XSSFPictureData(PackagePart part) {
        super(part);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public byte[] getData() {
        try (InputStream inputStream = this.getPackagePart().getInputStream();){
            byte[] byArray = IOUtils.toByteArrayWithMaxLength(inputStream, XSSFPictureData.getMaxImageSize());
            return byArray;
        }
        catch (IOException e) {
            throw new POIXMLException(e);
        }
    }

    @Override
    public String suggestFileExtension() {
        return this.getPackagePart().getPartName().getExtension();
    }

    @Override
    public int getPictureType() {
        String contentType = this.getPackagePart().getContentType();
        for (int i = 0; i < RELATIONS.length; ++i) {
            if (RELATIONS[i] == null || !RELATIONS[i].getContentType().equals(contentType)) continue;
            return i;
        }
        return 0;
    }

    @Override
    public String getMimeType() {
        return this.getPackagePart().getContentType();
    }

    @Override
    protected void prepareForCommit() {
    }

    static {
        XSSFPictureData.RELATIONS[2] = XSSFRelation.IMAGE_EMF;
        XSSFPictureData.RELATIONS[3] = XSSFRelation.IMAGE_WMF;
        XSSFPictureData.RELATIONS[4] = XSSFRelation.IMAGE_PICT;
        XSSFPictureData.RELATIONS[5] = XSSFRelation.IMAGE_JPEG;
        XSSFPictureData.RELATIONS[6] = XSSFRelation.IMAGE_PNG;
        XSSFPictureData.RELATIONS[7] = XSSFRelation.IMAGE_DIB;
        XSSFPictureData.RELATIONS[8] = XSSFRelation.IMAGE_GIF;
        XSSFPictureData.RELATIONS[9] = XSSFRelation.IMAGE_TIFF;
        XSSFPictureData.RELATIONS[10] = XSSFRelation.IMAGE_EPS;
        XSSFPictureData.RELATIONS[11] = XSSFRelation.IMAGE_BMP;
        XSSFPictureData.RELATIONS[12] = XSSFRelation.IMAGE_WPG;
    }
}

