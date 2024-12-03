/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import org.apache.poi.hslf.usermodel.HSLFAutoShape;
import org.apache.poi.hslf.usermodel.HSLFConnectorShape;
import org.apache.poi.hslf.usermodel.HSLFFreeformShape;
import org.apache.poi.hslf.usermodel.HSLFGroupShape;
import org.apache.poi.hslf.usermodel.HSLFObjectShape;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.ShapeContainer;

public interface HSLFShapeContainer
extends ShapeContainer<HSLFShape, HSLFTextParagraph> {
    public HSLFAutoShape createAutoShape();

    public HSLFFreeformShape createFreeform();

    public HSLFTextBox createTextBox();

    public HSLFConnectorShape createConnector();

    public HSLFGroupShape createGroup();

    public HSLFPictureShape createPicture(PictureData var1);

    public HSLFObjectShape createOleShape(PictureData var1);
}

