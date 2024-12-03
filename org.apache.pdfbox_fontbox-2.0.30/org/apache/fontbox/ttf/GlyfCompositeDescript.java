/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.GlyfCompositeComp;
import org.apache.fontbox.ttf.GlyfDescript;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphDescription;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.TTFDataStream;

public class GlyfCompositeDescript
extends GlyfDescript {
    private static final Log LOG = LogFactory.getLog(GlyfCompositeDescript.class);
    private final List<GlyfCompositeComp> components = new ArrayList<GlyfCompositeComp>();
    private final Map<Integer, GlyphDescription> descriptions = new HashMap<Integer, GlyphDescription>();
    private GlyphTable glyphTable = null;
    private boolean beingResolved = false;
    private boolean resolved = false;
    private int pointCount = -1;
    private int contourCount = -1;

    GlyfCompositeDescript(TTFDataStream bais, GlyphTable glyphTable) throws IOException {
        super((short)-1, bais);
        GlyfCompositeComp comp;
        this.glyphTable = glyphTable;
        do {
            comp = new GlyfCompositeComp(bais);
            this.components.add(comp);
        } while ((comp.getFlags() & 0x20) != 0);
        if ((comp.getFlags() & 0x100) != 0) {
            this.readInstructions(bais, bais.readUnsignedShort());
        }
        this.initDescriptions();
    }

    @Override
    public void resolve() {
        if (this.resolved) {
            return;
        }
        if (this.beingResolved) {
            LOG.error((Object)"Circular reference in GlyfCompositeDesc");
            return;
        }
        this.beingResolved = true;
        int firstIndex = 0;
        int firstContour = 0;
        for (GlyfCompositeComp comp : this.components) {
            comp.setFirstIndex(firstIndex);
            comp.setFirstContour(firstContour);
            GlyphDescription desc = this.descriptions.get(comp.getGlyphIndex());
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
            GlyphDescription gd = this.descriptions.get(c.getGlyphIndex());
            return gd.getEndPtOfContours(i - c.getFirstContour()) + c.getFirstIndex();
        }
        return 0;
    }

    @Override
    public byte getFlags(int i) {
        GlyfCompositeComp c = this.getCompositeComp(i);
        if (c != null) {
            GlyphDescription gd = this.descriptions.get(c.getGlyphIndex());
            return gd.getFlags(i - c.getFirstIndex());
        }
        return 0;
    }

    @Override
    public short getXCoordinate(int i) {
        GlyfCompositeComp c = this.getCompositeComp(i);
        if (c != null) {
            GlyphDescription gd = this.descriptions.get(c.getGlyphIndex());
            int n = i - c.getFirstIndex();
            short x = gd.getXCoordinate(n);
            short y = gd.getYCoordinate(n);
            return (short)(c.scaleX(x, y) + c.getXTranslate());
        }
        return 0;
    }

    @Override
    public short getYCoordinate(int i) {
        GlyfCompositeComp c = this.getCompositeComp(i);
        if (c != null) {
            GlyphDescription gd = this.descriptions.get(c.getGlyphIndex());
            int n = i - c.getFirstIndex();
            short x = gd.getXCoordinate(n);
            short y = gd.getYCoordinate(n);
            return (short)(c.scaleY(x, y) + c.getYTranslate());
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
            LOG.error((Object)"getPointCount called on unresolved GlyfCompositeDescript");
        }
        if (this.pointCount < 0) {
            GlyfCompositeComp c = this.components.get(this.components.size() - 1);
            GlyphDescription gd = this.descriptions.get(c.getGlyphIndex());
            if (gd == null) {
                LOG.error((Object)("GlyphDescription for index " + c.getGlyphIndex() + " is null, returning 0"));
                this.pointCount = 0;
            } else {
                this.pointCount = c.getFirstIndex() + gd.getPointCount();
            }
        }
        return this.pointCount;
    }

    @Override
    public int getContourCount() {
        if (!this.resolved) {
            LOG.error((Object)"getContourCount called on unresolved GlyfCompositeDescript");
        }
        if (this.contourCount < 0) {
            GlyfCompositeComp c = this.components.get(this.components.size() - 1);
            GlyphDescription gd = this.descriptions.get(c.getGlyphIndex());
            if (gd == null) {
                LOG.error((Object)("missing glyph description for index " + c.getGlyphIndex()));
                this.contourCount = 0;
            } else {
                this.contourCount = c.getFirstContour() + gd.getContourCount();
            }
        }
        return this.contourCount;
    }

    public int getComponentCount() {
        return this.components.size();
    }

    public List<GlyfCompositeComp> getComponents() {
        return Collections.unmodifiableList(this.components);
    }

    private GlyfCompositeComp getCompositeComp(int i) {
        for (GlyfCompositeComp c : this.components) {
            GlyphDescription gd = this.descriptions.get(c.getGlyphIndex());
            if (c.getFirstIndex() > i || gd == null || i >= c.getFirstIndex() + gd.getPointCount()) continue;
            return c;
        }
        return null;
    }

    private GlyfCompositeComp getCompositeCompEndPt(int i) {
        for (GlyfCompositeComp c : this.components) {
            GlyphDescription gd = this.descriptions.get(c.getGlyphIndex());
            if (c.getFirstContour() > i || gd == null || i >= c.getFirstContour() + gd.getContourCount()) continue;
            return c;
        }
        return null;
    }

    private void initDescriptions() {
        for (GlyfCompositeComp component : this.components) {
            try {
                int index = component.getGlyphIndex();
                GlyphData glyph = this.glyphTable.getGlyph(index);
                if (glyph == null) continue;
                this.descriptions.put(index, glyph.getDescription());
            }
            catch (IOException e) {
                LOG.error((Object)e);
            }
        }
    }
}

