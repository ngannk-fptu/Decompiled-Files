/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PDVisibleSignDesigner {
    private Float imageWidth;
    private Float imageHeight;
    private float xAxis;
    private float yAxis;
    private float pageHeight;
    private float pageWidth;
    private BufferedImage image;
    private String signatureFieldName = "sig";
    private byte[] formatterRectangleParams = new byte[]{0, 0, 100, 50};
    private int[] formatterRectangleParameters = new int[]{0, 0, 100, 50};
    private AffineTransform affineTransform = new AffineTransform();
    private float imageSizeInPercents;
    private int rotation = 0;

    public PDVisibleSignDesigner(String filename, InputStream imageStream, int page) throws IOException {
        this.readImageStream(imageStream);
        this.calculatePageSizeFromFile(filename, page);
    }

    public PDVisibleSignDesigner(InputStream documentStream, InputStream imageStream, int page) throws IOException {
        this.readImageStream(imageStream);
        this.calculatePageSizeFromStream(documentStream, page);
    }

    public PDVisibleSignDesigner(PDDocument document, InputStream imageStream, int page) throws IOException {
        this.readImageStream(imageStream);
        this.calculatePageSize(document, page);
    }

    public PDVisibleSignDesigner(String filename, BufferedImage image, int page) throws IOException {
        this.setImage(image);
        this.calculatePageSizeFromFile(filename, page);
    }

    public PDVisibleSignDesigner(InputStream documentStream, BufferedImage image, int page) throws IOException {
        this.setImage(image);
        this.calculatePageSizeFromStream(documentStream, page);
    }

    public PDVisibleSignDesigner(PDDocument document, BufferedImage image, int page) {
        this.setImage(image);
        this.calculatePageSize(document, page);
    }

    public PDVisibleSignDesigner(InputStream imageStream) throws IOException {
        this.readImageStream(imageStream);
    }

    private void calculatePageSizeFromFile(String filename, int page) throws IOException {
        PDDocument document = PDDocument.load(new File(filename));
        this.calculatePageSize(document, page);
        document.close();
    }

    private void calculatePageSizeFromStream(InputStream documentStream, int page) throws IOException {
        PDDocument document = PDDocument.load(documentStream);
        this.calculatePageSize(document, page);
        document.close();
    }

    private void calculatePageSize(PDDocument document, int page) {
        if (page < 1) {
            throw new IllegalArgumentException("First page of pdf is 1, not " + page);
        }
        PDPage firstPage = document.getPage(page - 1);
        PDRectangle mediaBox = firstPage.getMediaBox();
        this.pageHeight(mediaBox.getHeight());
        this.pageWidth = mediaBox.getWidth();
        this.imageSizeInPercents = 100.0f;
        this.rotation = firstPage.getRotation() % 360;
    }

    public PDVisibleSignDesigner adjustForRotation() {
        switch (this.rotation) {
            case 90: {
                float temp = this.yAxis;
                this.yAxis = this.pageHeight - this.xAxis - this.imageWidth.floatValue();
                this.xAxis = temp;
                this.affineTransform = new AffineTransform(0.0f, this.imageHeight.floatValue() / this.imageWidth.floatValue(), -this.imageWidth.floatValue() / this.imageHeight.floatValue(), 0.0f, this.imageWidth.floatValue(), 0.0f);
                temp = this.imageHeight.floatValue();
                this.imageHeight = this.imageWidth;
                this.imageWidth = Float.valueOf(temp);
                break;
            }
            case 180: {
                float newX = this.pageWidth - this.xAxis - this.imageWidth.floatValue();
                float newY = this.pageHeight - this.yAxis - this.imageHeight.floatValue();
                this.xAxis = newX;
                this.yAxis = newY;
                this.affineTransform = new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, this.imageWidth.floatValue(), this.imageHeight.floatValue());
                break;
            }
            case 270: {
                float temp = this.xAxis;
                this.xAxis = this.pageWidth - this.yAxis - this.imageHeight.floatValue();
                this.yAxis = temp;
                this.affineTransform = new AffineTransform(0.0f, -this.imageHeight.floatValue() / this.imageWidth.floatValue(), this.imageWidth.floatValue() / this.imageHeight.floatValue(), 0.0f, 0.0f, this.imageHeight.floatValue());
                temp = this.imageHeight.floatValue();
                this.imageHeight = this.imageWidth;
                this.imageWidth = Float.valueOf(temp);
                break;
            }
        }
        return this;
    }

    public PDVisibleSignDesigner signatureImage(String path) throws IOException {
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(path));
            this.readImageStream(in);
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(in);
            throw throwable;
        }
        IOUtils.closeQuietly(in);
        return this;
    }

    public PDVisibleSignDesigner zoom(float percent) {
        this.imageHeight = Float.valueOf(this.imageHeight.floatValue() + this.imageHeight.floatValue() * percent / 100.0f);
        this.imageWidth = Float.valueOf(this.imageWidth.floatValue() + this.imageWidth.floatValue() * percent / 100.0f);
        this.formatterRectangleParameters[2] = (int)this.imageWidth.floatValue();
        this.formatterRectangleParameters[3] = (int)this.imageHeight.floatValue();
        return this;
    }

    public PDVisibleSignDesigner coordinates(float x, float y) {
        this.xAxis(x);
        this.yAxis(y);
        return this;
    }

    public float getxAxis() {
        return this.xAxis;
    }

    public PDVisibleSignDesigner xAxis(float xAxis) {
        this.xAxis = xAxis;
        return this;
    }

    public float getyAxis() {
        return this.yAxis;
    }

    public PDVisibleSignDesigner yAxis(float yAxis) {
        this.yAxis = yAxis;
        return this;
    }

    public float getWidth() {
        return this.imageWidth.floatValue();
    }

    public PDVisibleSignDesigner width(float width) {
        this.imageWidth = Float.valueOf(width);
        this.formatterRectangleParameters[2] = (int)width;
        return this;
    }

    public float getHeight() {
        return this.imageHeight.floatValue();
    }

    public PDVisibleSignDesigner height(float height) {
        this.imageHeight = Float.valueOf(height);
        this.formatterRectangleParameters[3] = (int)height;
        return this;
    }

    protected float getTemplateHeight() {
        return this.getPageHeight();
    }

    private PDVisibleSignDesigner pageHeight(float templateHeight) {
        this.pageHeight = templateHeight;
        return this;
    }

    public String getSignatureFieldName() {
        return this.signatureFieldName;
    }

    public PDVisibleSignDesigner signatureFieldName(String signatureFieldName) {
        this.signatureFieldName = signatureFieldName;
        return this;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    private void readImageStream(InputStream stream) throws IOException {
        ImageIO.setUseCache(false);
        this.setImage(ImageIO.read(stream));
    }

    private void setImage(BufferedImage image) {
        this.image = image;
        this.imageHeight = Float.valueOf(image.getHeight());
        this.imageWidth = Float.valueOf(image.getWidth());
        this.formatterRectangleParameters[2] = image.getWidth();
        this.formatterRectangleParameters[3] = image.getHeight();
    }

    @Deprecated
    public byte[] getAffineTransformParams() {
        return new byte[]{(byte)this.affineTransform.getScaleX(), (byte)this.affineTransform.getShearY(), (byte)this.affineTransform.getShearX(), (byte)this.affineTransform.getScaleY(), (byte)this.affineTransform.getTranslateX(), (byte)this.affineTransform.getTranslateY()};
    }

    public AffineTransform getTransform() {
        return this.affineTransform;
    }

    @Deprecated
    public PDVisibleSignDesigner affineTransformParams(byte[] affineTransformParams) {
        this.affineTransform = new AffineTransform(affineTransformParams[0], affineTransformParams[1], affineTransformParams[2], affineTransformParams[3], affineTransformParams[4], affineTransformParams[5]);
        return this;
    }

    public PDVisibleSignDesigner transform(AffineTransform affineTransform) {
        this.affineTransform = new AffineTransform(affineTransform);
        return this;
    }

    @Deprecated
    public byte[] getFormatterRectangleParams() {
        return this.formatterRectangleParams;
    }

    public int[] getFormatterRectangleParameters() {
        return this.formatterRectangleParameters;
    }

    @Deprecated
    public PDVisibleSignDesigner formatterRectangleParams(byte[] formatterRectangleParams) {
        this.formatterRectangleParams = formatterRectangleParams;
        return this;
    }

    public PDVisibleSignDesigner formatterRectangleParameters(int[] formatterRectangleParameters) {
        this.formatterRectangleParameters = formatterRectangleParameters;
        return this;
    }

    public float getPageWidth() {
        return this.pageWidth;
    }

    public PDVisibleSignDesigner pageWidth(float pageWidth) {
        this.pageWidth = pageWidth;
        return this;
    }

    public float getPageHeight() {
        return this.pageHeight;
    }

    public float getImageSizeInPercents() {
        return this.imageSizeInPercents;
    }

    public void imageSizeInPercents(float imageSizeInPercents) {
        this.imageSizeInPercents = imageSizeInPercents;
    }

    public String getSignatureText() {
        throw new UnsupportedOperationException("That method is not yet implemented");
    }

    public PDVisibleSignDesigner signatureText(String signatureText) {
        throw new UnsupportedOperationException("That method is not yet implemented");
    }
}

