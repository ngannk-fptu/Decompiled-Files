/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.batik.svggen.font.table.GlyfCompositeComp;
import org.apache.batik.svggen.font.table.GlyfDescript;
import org.apache.batik.svggen.font.table.GlyfTable;

public class GlyfCompositeDescript
extends GlyfDescript {
    private List components = new ArrayList();
    protected boolean beingResolved = false;
    protected boolean resolved = false;

    public GlyfCompositeDescript(GlyfTable parentTable, ByteArrayInputStream bais) {
        super(parentTable, (short)-1, bais);
        GlyfCompositeComp comp;
        do {
            comp = new GlyfCompositeComp(bais);
            this.components.add(comp);
        } while ((comp.getFlags() & 0x20) != 0);
        if ((comp.getFlags() & 0x100) != 0) {
            this.readInstructions(bais, bais.read() << 8 | bais.read());
        }
    }

    @Override
    public void resolve() {
        if (this.resolved) {
            return;
        }
        if (this.beingResolved) {
            System.err.println("Circular reference in GlyfCompositeDesc");
            return;
        }
        this.beingResolved = true;
        int firstIndex = 0;
        int firstContour = 0;
        for (Object component : this.components) {
            GlyfCompositeComp comp = (GlyfCompositeComp)component;
            comp.setFirstIndex(firstIndex);
            comp.setFirstContour(firstContour);
            GlyfDescript desc = this.parentTable.getDescription(comp.getGlyphIndex());
            if (desc == null) continue;
            desc.resolve();
            firstIndex += desc.getPointCount();
            firstContour += desc.getContourCount();
        }
        this.resolved = true;
        this.beingResolved = false;
    }

    @Override
    public int getEndPtOfContours(int i) {
        GlyfCompositeComp c = this.getCompositeCompEndPt(i);
        if (c != null) {
            GlyfDescript gd = this.parentTable.getDescription(c.getGlyphIndex());
            return gd.getEndPtOfContours(i - c.getFirstContour()) + c.getFirstIndex();
        }
        return 0;
    }

    @Override
    public byte getFlags(int i) {
        GlyfCompositeComp c = this.getCompositeComp(i);
        if (c != null) {
            GlyfDescript gd = this.parentTable.getDescription(c.getGlyphIndex());
            return gd.getFlags(i - c.getFirstIndex());
        }
        return 0;
    }

    @Override
    public short getXCoordinate(int i) {
        GlyfCompositeComp c = this.getCompositeComp(i);
        if (c != null) {
            GlyfDescript gd = this.parentTable.getDescription(c.getGlyphIndex());
            int n = i - c.getFirstIndex();
            short x = gd.getXCoordinate(n);
            short y = gd.getYCoordinate(n);
            short x1 = (short)c.scaleX(x, y);
            x1 = (short)(x1 + c.getXTranslate());
            return x1;
        }
        return 0;
    }

    @Override
    public short getYCoordinate(int i) {
        GlyfCompositeComp c = this.getCompositeComp(i);
        if (c != null) {
            GlyfDescript gd = this.parentTable.getDescription(c.getGlyphIndex());
            int n = i - c.getFirstIndex();
            short x = gd.getXCoordinate(n);
            short y = gd.getYCoordinate(n);
            short y1 = (short)c.scaleY(x, y);
            y1 = (short)(y1 + c.getYTranslate());
            return y1;
        }
        return 0;
    }

    @Override
    public boolean isComposite() {
        return true;
    }

    @Override
    public int getPointCount() {
        if (!this.resolved) {
            System.err.println("getPointCount called on unresolved GlyfCompositeDescript");
        }
        GlyfCompositeComp c = (GlyfCompositeComp)this.components.get(this.components.size() - 1);
        return c.getFirstIndex() + this.parentTable.getDescription(c.getGlyphIndex()).getPointCount();
    }

    @Override
    public int getContourCount() {
        if (!this.resolved) {
            System.err.println("getContourCount called on unresolved GlyfCompositeDescript");
        }
        GlyfCompositeComp c = (GlyfCompositeComp)this.components.get(this.components.size() - 1);
        return c.getFirstContour() + this.parentTable.getDescription(c.getGlyphIndex()).getContourCount();
    }

    public int getComponentIndex(int i) {
        return ((GlyfCompositeComp)this.components.get(i)).getFirstIndex();
    }

    public int getComponentCount() {
        return this.components.size();
    }

    protected GlyfCompositeComp getCompositeComp(int i) {
        for (Object component : this.components) {
            GlyfCompositeComp c = (GlyfCompositeComp)component;
            GlyfDescript gd = this.parentTable.getDescription(c.getGlyphIndex());
            if (c.getFirstIndex() > i || i >= c.getFirstIndex() + gd.getPointCount()) continue;
            return c;
        }
        return null;
    }

    protected GlyfCompositeComp getCompositeCompEndPt(int i) {
        for (Object component : this.components) {
            GlyfCompositeComp c = (GlyfCompositeComp)component;
            GlyfDescript gd = this.parentTable.getDescription(c.getGlyphIndex());
            if (c.getFirstContour() > i || i >= c.getFirstContour() + gd.getContourCount()) continue;
            return c;
        }
        return null;
    }
}

