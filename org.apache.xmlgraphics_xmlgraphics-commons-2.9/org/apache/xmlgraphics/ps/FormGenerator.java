/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.PSResource;

public abstract class FormGenerator {
    private String formName;
    private String title;
    private Dimension2D dimensions;

    public FormGenerator(String formName, String title, Dimension2D dimensions) {
        this.formName = formName;
        this.title = title;
        this.dimensions = dimensions;
    }

    public String getFormName() {
        return this.formName;
    }

    public String getTitle() {
        return this.title;
    }

    public Dimension2D getDimensions() {
        return this.dimensions;
    }

    protected abstract void generatePaintProc(PSGenerator var1) throws IOException;

    protected void generateAdditionalDataStream(PSGenerator gen) throws IOException {
    }

    protected AffineTransform getMatrix() {
        return new AffineTransform();
    }

    protected Rectangle2D getBBox() {
        return new Rectangle2D.Double(0.0, 0.0, this.dimensions.getWidth(), this.dimensions.getHeight());
    }

    public PSResource generate(PSGenerator gen) throws IOException {
        if (gen.getPSLevel() < 2) {
            throw new UnsupportedOperationException("Forms require at least Level 2 PostScript");
        }
        gen.writeDSCComment("BeginResource", new Object[]{"form", this.getFormName()});
        if (this.title != null) {
            gen.writeDSCComment("Title", this.title);
        }
        gen.writeln("/" + this.formName);
        gen.writeln("<< /FormType 1");
        gen.writeln("  /BBox " + gen.formatRectangleToArray(this.getBBox()));
        gen.writeln("  /Matrix " + gen.formatMatrix(this.getMatrix()));
        gen.writeln("  /PaintProc {");
        gen.writeln("    pop");
        gen.writeln("    gsave");
        this.generatePaintProc(gen);
        gen.writeln("    grestore");
        gen.writeln("  } bind");
        gen.writeln(">> def");
        PSResource res = new PSResource("form", this.formName);
        try {
            this.generateAdditionalDataStream(gen);
        }
        finally {
            gen.writeDSCComment("EndResource");
            gen.getResourceTracker().registerSuppliedResource(res);
        }
        return res;
    }
}

