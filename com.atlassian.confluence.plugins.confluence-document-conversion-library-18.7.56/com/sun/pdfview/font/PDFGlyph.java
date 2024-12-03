/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFShapeCmd;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class PDFGlyph {
    private char src;
    private String name;
    private Point2D advance;
    private GeneralPath shape;
    private PDFPage page;

    public PDFGlyph(char src, String name, GeneralPath shape, Point2D.Float advance) {
        this.shape = shape;
        this.advance = advance;
        this.src = src;
        this.name = name;
    }

    public PDFGlyph(char src, String name, PDFPage page, Point2D advance) {
        this.page = page;
        this.advance = advance;
        this.src = src;
        this.name = name;
    }

    public char getChar() {
        return this.src;
    }

    public String getName() {
        return this.name;
    }

    public GeneralPath getShape() {
        return this.shape;
    }

    public PDFPage getPage() {
        return this.page;
    }

    public Point2D addCommands(PDFPage cmds, AffineTransform transform, int mode) {
        if (this.shape != null) {
            GeneralPath outline = (GeneralPath)this.shape.createTransformedShape(transform);
            cmds.addCommand(new PDFShapeCmd(outline, mode));
        } else if (this.page != null) {
            cmds.addCommands(this.page, transform);
        }
        return this.advance;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(this.name);
        return str.toString();
    }
}

