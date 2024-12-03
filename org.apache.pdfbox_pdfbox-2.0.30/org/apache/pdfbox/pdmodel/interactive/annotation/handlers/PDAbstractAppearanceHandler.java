/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationSquareCircle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceEntry;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler;

public abstract class PDAbstractAppearanceHandler
implements PDAppearanceHandler {
    private final PDAnnotation annotation;
    protected PDDocument document;
    protected static final Set<String> SHORT_STYLES = PDAbstractAppearanceHandler.createShortStyles();
    static final double ARROW_ANGLE = Math.toRadians(30.0);
    protected static final Set<String> INTERIOR_COLOR_STYLES = PDAbstractAppearanceHandler.createInteriorColorStyles();
    protected static final Set<String> ANGLED_STYLES = PDAbstractAppearanceHandler.createAngledStyles();

    public PDAbstractAppearanceHandler(PDAnnotation annotation) {
        this(annotation, null);
    }

    public PDAbstractAppearanceHandler(PDAnnotation annotation, PDDocument document) {
        this.annotation = annotation;
        this.document = document;
    }

    @Override
    public void generateAppearanceStreams() {
        this.generateNormalAppearance();
        this.generateRolloverAppearance();
        this.generateDownAppearance();
    }

    PDAnnotation getAnnotation() {
        return this.annotation;
    }

    PDColor getColor() {
        return this.annotation.getColor();
    }

    PDRectangle getRectangle() {
        return this.annotation.getRectangle();
    }

    protected COSStream createCOSStream() {
        return this.document == null ? new COSStream() : this.document.getDocument().createCOSStream();
    }

    PDAppearanceDictionary getAppearance() {
        PDAppearanceDictionary appearanceDictionary = this.annotation.getAppearance();
        if (appearanceDictionary == null) {
            appearanceDictionary = new PDAppearanceDictionary();
            this.annotation.setAppearance(appearanceDictionary);
        }
        return appearanceDictionary;
    }

    PDAppearanceContentStream getNormalAppearanceAsContentStream() throws IOException {
        return this.getNormalAppearanceAsContentStream(false);
    }

    PDAppearanceContentStream getNormalAppearanceAsContentStream(boolean compress) throws IOException {
        PDAppearanceEntry appearanceEntry = this.getNormalAppearance();
        return this.getAppearanceEntryAsContentStream(appearanceEntry, compress);
    }

    PDAppearanceEntry getDownAppearance() {
        PDAppearanceDictionary appearanceDictionary = this.getAppearance();
        PDAppearanceEntry downAppearanceEntry = appearanceDictionary.getDownAppearance();
        if (downAppearanceEntry.isSubDictionary()) {
            downAppearanceEntry = new PDAppearanceEntry(this.createCOSStream());
            appearanceDictionary.setDownAppearance(downAppearanceEntry);
        }
        return downAppearanceEntry;
    }

    PDAppearanceEntry getRolloverAppearance() {
        PDAppearanceDictionary appearanceDictionary = this.getAppearance();
        PDAppearanceEntry rolloverAppearanceEntry = appearanceDictionary.getRolloverAppearance();
        if (rolloverAppearanceEntry.isSubDictionary()) {
            rolloverAppearanceEntry = new PDAppearanceEntry(this.createCOSStream());
            appearanceDictionary.setRolloverAppearance(rolloverAppearanceEntry);
        }
        return rolloverAppearanceEntry;
    }

    PDRectangle getPaddedRectangle(PDRectangle rectangle, float padding) {
        return new PDRectangle(rectangle.getLowerLeftX() + padding, rectangle.getLowerLeftY() + padding, rectangle.getWidth() - 2.0f * padding, rectangle.getHeight() - 2.0f * padding);
    }

    PDRectangle addRectDifferences(PDRectangle rectangle, float[] differences) {
        if (differences == null || differences.length != 4) {
            return rectangle;
        }
        return new PDRectangle(rectangle.getLowerLeftX() - differences[0], rectangle.getLowerLeftY() - differences[1], rectangle.getWidth() + differences[0] + differences[2], rectangle.getHeight() + differences[1] + differences[3]);
    }

    PDRectangle applyRectDifferences(PDRectangle rectangle, float[] differences) {
        if (differences == null || differences.length != 4) {
            return rectangle;
        }
        return new PDRectangle(rectangle.getLowerLeftX() + differences[0], rectangle.getLowerLeftY() + differences[1], rectangle.getWidth() - differences[0] - differences[2], rectangle.getHeight() - differences[1] - differences[3]);
    }

    void setOpacity(PDAppearanceContentStream contentStream, float opacity) throws IOException {
        if (opacity < 1.0f) {
            PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
            gs.setStrokingAlphaConstant(Float.valueOf(opacity));
            gs.setNonStrokingAlphaConstant(Float.valueOf(opacity));
            contentStream.setGraphicsStateParameters(gs);
        }
    }

    void drawStyle(String style, PDAppearanceContentStream cs, float x, float y, float width, boolean hasStroke, boolean hasBackground, boolean ending) throws IOException {
        int sign;
        int n = sign = ending ? -1 : 1;
        if ("OpenArrow".equals(style) || "ClosedArrow".equals(style)) {
            this.drawArrow(cs, x + (float)sign * width, y, (float)sign * width * 9.0f);
        } else if ("Butt".equals(style)) {
            cs.moveTo(x, y - width * 3.0f);
            cs.lineTo(x, y + width * 3.0f);
        } else if ("Diamond".equals(style)) {
            this.drawDiamond(cs, x, y, width * 3.0f);
        } else if ("Square".equals(style)) {
            cs.addRect(x - width * 3.0f, y - width * 3.0f, width * 6.0f, width * 6.0f);
        } else if ("Circle".equals(style)) {
            this.drawCircle(cs, x, y, width * 3.0f);
        } else if ("ROpenArrow".equals(style) || "RClosedArrow".equals(style)) {
            this.drawArrow(cs, x + (float)(-sign) * width, y, (float)(-sign) * width * 9.0f);
        } else if ("Slash".equals(style)) {
            float width9 = width * 9.0f;
            cs.moveTo(x + (float)(Math.cos(Math.toRadians(60.0)) * (double)width9), y + (float)(Math.sin(Math.toRadians(60.0)) * (double)width9));
            cs.lineTo(x + (float)(Math.cos(Math.toRadians(240.0)) * (double)width9), y + (float)(Math.sin(Math.toRadians(240.0)) * (double)width9));
        }
        if ("RClosedArrow".equals(style) || "ClosedArrow".equals(style)) {
            cs.closePath();
        }
        cs.drawShape(width, hasStroke, INTERIOR_COLOR_STYLES.contains(style) && hasBackground);
    }

    void drawArrow(PDAppearanceContentStream cs, float x, float y, float len) throws IOException {
        float armX = x + (float)(Math.cos(ARROW_ANGLE) * (double)len);
        float armYdelta = (float)(Math.sin(ARROW_ANGLE) * (double)len);
        cs.moveTo(armX, y + armYdelta);
        cs.lineTo(x, y);
        cs.lineTo(armX, y - armYdelta);
    }

    void drawDiamond(PDAppearanceContentStream cs, float x, float y, float r) throws IOException {
        cs.moveTo(x - r, y);
        cs.lineTo(x, y + r);
        cs.lineTo(x + r, y);
        cs.lineTo(x, y - r);
        cs.closePath();
    }

    void drawCircle(PDAppearanceContentStream cs, float x, float y, float r) throws IOException {
        float magic = r * 0.551784f;
        cs.moveTo(x, y + r);
        cs.curveTo(x + magic, y + r, x + r, y + magic, x + r, y);
        cs.curveTo(x + r, y - magic, x + magic, y - r, x, y - r);
        cs.curveTo(x - magic, y - r, x - r, y - magic, x - r, y);
        cs.curveTo(x - r, y + magic, x - magic, y + r, x, y + r);
        cs.closePath();
    }

    void drawCircle2(PDAppearanceContentStream cs, float x, float y, float r) throws IOException {
        float magic = r * 0.551784f;
        cs.moveTo(x, y + r);
        cs.curveTo(x - magic, y + r, x - r, y + magic, x - r, y);
        cs.curveTo(x - r, y - magic, x - magic, y - r, x, y - r);
        cs.curveTo(x + magic, y - r, x + r, y - magic, x + r, y);
        cs.curveTo(x + r, y + magic, x + magic, y + r, x, y + r);
        cs.closePath();
    }

    private static Set<String> createShortStyles() {
        HashSet<String> shortStyles = new HashSet<String>();
        shortStyles.add("OpenArrow");
        shortStyles.add("ClosedArrow");
        shortStyles.add("Square");
        shortStyles.add("Circle");
        shortStyles.add("Diamond");
        return Collections.unmodifiableSet(shortStyles);
    }

    private static Set<String> createInteriorColorStyles() {
        HashSet<String> interiorColorStyles = new HashSet<String>();
        interiorColorStyles.add("ClosedArrow");
        interiorColorStyles.add("Circle");
        interiorColorStyles.add("Diamond");
        interiorColorStyles.add("RClosedArrow");
        interiorColorStyles.add("Square");
        return Collections.unmodifiableSet(interiorColorStyles);
    }

    private static Set<String> createAngledStyles() {
        HashSet<String> angledStyles = new HashSet<String>();
        angledStyles.add("ClosedArrow");
        angledStyles.add("OpenArrow");
        angledStyles.add("RClosedArrow");
        angledStyles.add("ROpenArrow");
        angledStyles.add("Butt");
        angledStyles.add("Slash");
        return Collections.unmodifiableSet(angledStyles);
    }

    private PDAppearanceEntry getNormalAppearance() {
        PDAppearanceDictionary appearanceDictionary = this.getAppearance();
        PDAppearanceEntry normalAppearanceEntry = appearanceDictionary.getNormalAppearance();
        if (normalAppearanceEntry == null || normalAppearanceEntry.isSubDictionary()) {
            normalAppearanceEntry = new PDAppearanceEntry(this.createCOSStream());
            appearanceDictionary.setNormalAppearance(normalAppearanceEntry);
        }
        return normalAppearanceEntry;
    }

    private PDAppearanceContentStream getAppearanceEntryAsContentStream(PDAppearanceEntry appearanceEntry, boolean compress) throws IOException {
        PDAppearanceStream appearanceStream = appearanceEntry.getAppearanceStream();
        this.setTransformationMatrix(appearanceStream);
        PDResources resources = appearanceStream.getResources();
        if (resources == null) {
            resources = new PDResources();
            appearanceStream.setResources(resources);
        }
        return new PDAppearanceContentStream(appearanceStream, compress);
    }

    private void setTransformationMatrix(PDAppearanceStream appearanceStream) {
        PDRectangle bbox = this.getRectangle();
        appearanceStream.setBBox(bbox);
        AffineTransform transform = AffineTransform.getTranslateInstance(-bbox.getLowerLeftX(), -bbox.getLowerLeftY());
        appearanceStream.setMatrix(transform);
    }

    PDRectangle handleBorderBox(PDAnnotationSquareCircle annotation, float lineWidth) {
        PDRectangle borderBox;
        float[] rectDifferences = annotation.getRectDifferences();
        if (rectDifferences.length == 0) {
            borderBox = this.getPaddedRectangle(this.getRectangle(), lineWidth / 2.0f);
            annotation.setRectDifferences(lineWidth / 2.0f);
            annotation.setRectangle(this.addRectDifferences(this.getRectangle(), annotation.getRectDifferences()));
            PDRectangle rect = this.getRectangle();
            PDAppearanceStream appearanceStream = annotation.getNormalAppearanceStream();
            AffineTransform transform = AffineTransform.getTranslateInstance(-rect.getLowerLeftX(), -rect.getLowerLeftY());
            appearanceStream.setBBox(rect);
            appearanceStream.setMatrix(transform);
        } else {
            borderBox = this.applyRectDifferences(this.getRectangle(), rectDifferences);
            borderBox = this.getPaddedRectangle(borderBox, lineWidth / 2.0f);
        }
        return borderBox;
    }
}

