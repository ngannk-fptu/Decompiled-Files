/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.image;

import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

public interface PDImage
extends COSObjectable {
    public BufferedImage getImage() throws IOException;

    public WritableRaster getRawRaster() throws IOException;

    public BufferedImage getRawImage() throws IOException;

    public BufferedImage getImage(Rectangle var1, int var2) throws IOException;

    public BufferedImage getStencilImage(Paint var1) throws IOException;

    public InputStream createInputStream() throws IOException;

    public InputStream createInputStream(List<String> var1) throws IOException;

    public InputStream createInputStream(DecodeOptions var1) throws IOException;

    public boolean isEmpty();

    public boolean isStencil();

    public void setStencil(boolean var1);

    public int getBitsPerComponent();

    public void setBitsPerComponent(int var1);

    public PDColorSpace getColorSpace() throws IOException;

    public void setColorSpace(PDColorSpace var1);

    public int getHeight();

    public void setHeight(int var1);

    public int getWidth();

    public void setWidth(int var1);

    public void setDecode(COSArray var1);

    public COSArray getDecode();

    public boolean getInterpolate();

    public void setInterpolate(boolean var1);

    public String getSuffix();
}

