/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d.ps;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xmlgraphics.java2d.ps.PSGraphics2D;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.PSProcSets;

public abstract class AbstractPSDocumentGraphics2D
extends PSGraphics2D {
    protected static final Integer ZERO = 0;
    protected int width;
    protected int height;
    protected float viewportWidth;
    protected float viewportHeight;
    protected int pagecount;
    protected boolean pagePending;
    protected Shape initialClip;
    protected AffineTransform initialTransform;

    AbstractPSDocumentGraphics2D(boolean textAsShapes) {
        super(textAsShapes);
    }

    public void setupDocument(OutputStream stream, int width, int height) throws IOException {
        this.width = width;
        this.height = height;
        this.pagecount = 0;
        this.pagePending = false;
        this.setPSGenerator(new PSGenerator(stream));
        this.writeFileHeader();
    }

    protected abstract void writeFileHeader() throws IOException;

    public AbstractPSDocumentGraphics2D(boolean textAsShapes, OutputStream stream, int width, int height) throws IOException {
        this(textAsShapes);
        this.setupDocument(stream, width, height);
    }

    public void setViewportDimension(float w, float h) throws IOException {
        this.viewportWidth = w;
        this.viewportHeight = h;
    }

    public void setBackgroundColor(Color col) {
    }

    public int getPageCount() {
        return this.pagecount;
    }

    public void nextPage() throws IOException {
        this.closePage();
    }

    protected void closePage() throws IOException {
        if (!this.pagePending) {
            return;
        }
        this.writePageTrailer();
        this.pagePending = false;
    }

    protected abstract void writePageHeader() throws IOException;

    protected abstract void writePageTrailer() throws IOException;

    protected void writeProcSets() throws IOException {
        PSProcSets.writeStdProcSet(this.gen);
        PSProcSets.writeEPSProcSet(this.gen);
    }

    @Override
    public void preparePainting() {
        if (this.pagePending) {
            return;
        }
        try {
            this.startPage();
        }
        catch (IOException ioe) {
            this.handleIOException(ioe);
        }
    }

    protected void startPage() throws IOException {
        if (this.pagePending) {
            throw new IllegalStateException("Close page first before starting another");
        }
        ++this.pagecount;
        if (this.initialTransform == null) {
            this.initialTransform = this.getTransform();
            this.initialClip = this.getClip();
        } else {
            this.setTransform(this.initialTransform);
            this.setClip(this.initialClip);
        }
        this.writePageHeader();
        AffineTransform at = (this.viewportWidth != (float)this.width || this.viewportHeight != (float)this.height) && this.viewportWidth > 0.0f && this.viewportHeight > 0.0f ? new AffineTransform((float)this.width / this.viewportWidth, 0.0f, 0.0f, -1.0f * ((float)this.height / this.viewportHeight), 0.0f, this.height) : new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, this.height);
        this.gen.writeln(this.gen.formatMatrix(at) + " " + this.gen.mapCommand("concat"));
        this.gen.writeDSCComment("EndPageSetup");
        this.pagePending = true;
    }

    public void finish() throws IOException {
        if (this.pagePending) {
            this.closePage();
        }
        this.gen.writeDSCComment("Trailer");
        this.gen.writeDSCComment("Pages", this.pagecount);
        this.gen.writeDSCComment("EOF");
        this.gen.flush();
    }

    public AbstractPSDocumentGraphics2D(AbstractPSDocumentGraphics2D g) {
        super(g);
    }
}

