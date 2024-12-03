/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.common.usermodel.fonts.FontFamily;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.common.usermodel.fonts.FontPitch;
import org.apache.poi.hslf.record.FontEmbeddedData;
import org.apache.poi.hslf.record.FontEntityAtom;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.Internal;

public class HSLFFontInfo
implements FontInfo {
    private static final BitField FLAGS_EMBED_SUBSETTED = BitFieldFactory.getInstance(1);
    private static final BitField FLAGS_RENDER_FONTTYPE = BitFieldFactory.getInstance(7);
    private static final BitField FLAGS_NO_FONT_SUBSTITUTION = BitFieldFactory.getInstance(8);
    private int index = -1;
    private String typeface = "undefined";
    private FontCharset charset = FontCharset.ANSI;
    private FontRenderType renderType = FontRenderType.truetype;
    private FontFamily family = FontFamily.FF_SWISS;
    private FontPitch pitch = FontPitch.VARIABLE;
    private boolean isSubsetted;
    private boolean isSubstitutable = true;
    private final List<FontEmbeddedData> facets = new ArrayList<FontEmbeddedData>();
    private FontEntityAtom fontEntityAtom;

    public HSLFFontInfo(String typeface) {
        this.setTypeface(typeface);
    }

    public HSLFFontInfo(FontEntityAtom fontAtom) {
        this.fontEntityAtom = fontAtom;
        this.setIndex(fontAtom.getFontIndex());
        this.setTypeface(fontAtom.getFontName());
        this.setCharset(FontCharset.valueOf(fontAtom.getCharSet()));
        switch (FLAGS_RENDER_FONTTYPE.getValue(fontAtom.getFontType())) {
            case 1: {
                this.setRenderType(FontRenderType.raster);
                break;
            }
            case 2: {
                this.setRenderType(FontRenderType.device);
                break;
            }
            default: {
                this.setRenderType(FontRenderType.truetype);
            }
        }
        byte pitchAndFamily = (byte)fontAtom.getPitchAndFamily();
        this.setPitch(FontPitch.valueOfPitchFamily(pitchAndFamily));
        this.setFamily(FontFamily.valueOfPitchFamily(pitchAndFamily));
        this.setEmbedSubsetted(FLAGS_EMBED_SUBSETTED.isSet(fontAtom.getFontFlags()));
        this.setFontSubstitutable(!FLAGS_NO_FONT_SUBSTITUTION.isSet(fontAtom.getFontType()));
    }

    public HSLFFontInfo(FontInfo fontInfo) {
        this.setTypeface(fontInfo.getTypeface());
        this.setCharset(fontInfo.getCharset());
        this.setFamily(fontInfo.getFamily());
        this.setPitch(fontInfo.getPitch());
        if (fontInfo instanceof HSLFFontInfo) {
            HSLFFontInfo hFontInfo = (HSLFFontInfo)fontInfo;
            this.setRenderType(hFontInfo.getRenderType());
            this.setEmbedSubsetted(hFontInfo.isEmbedSubsetted());
            this.setFontSubstitutable(hFontInfo.isFontSubstitutable());
        }
    }

    @Override
    public Integer getIndex() {
        return this.index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String getTypeface() {
        return this.typeface;
    }

    @Override
    public void setTypeface(String typeface) {
        if (typeface == null || typeface.isEmpty()) {
            throw new IllegalArgumentException("typeface can't be null nor empty");
        }
        this.typeface = typeface;
    }

    @Override
    public void setCharset(FontCharset charset) {
        this.charset = charset == null ? FontCharset.ANSI : charset;
    }

    @Override
    public FontCharset getCharset() {
        return this.charset;
    }

    @Override
    public FontFamily getFamily() {
        return this.family;
    }

    @Override
    public void setFamily(FontFamily family) {
        this.family = family == null ? FontFamily.FF_SWISS : family;
    }

    @Override
    public FontPitch getPitch() {
        return this.pitch;
    }

    @Override
    public void setPitch(FontPitch pitch) {
        this.pitch = pitch == null ? FontPitch.VARIABLE : pitch;
    }

    public FontRenderType getRenderType() {
        return this.renderType;
    }

    public void setRenderType(FontRenderType renderType) {
        this.renderType = renderType == null ? FontRenderType.truetype : renderType;
    }

    public boolean isEmbedSubsetted() {
        return this.isSubsetted;
    }

    public void setEmbedSubsetted(boolean embedSubset) {
        this.isSubsetted = embedSubset;
    }

    public boolean isFontSubstitutable() {
        return this.isSubstitutable;
    }

    public void setFontSubstitutable(boolean isSubstitutable) {
        this.isSubstitutable = isSubstitutable;
    }

    public FontEntityAtom createRecord() {
        int typeFlag;
        FontEntityAtom fnt;
        assert (this.fontEntityAtom == null);
        this.fontEntityAtom = fnt = new FontEntityAtom();
        fnt.setFontIndex(this.getIndex() << 4);
        fnt.setFontName(this.getTypeface());
        fnt.setCharSet(this.getCharset().getNativeId());
        fnt.setFontFlags((byte)(this.isEmbedSubsetted() ? 1 : 0));
        switch (this.renderType) {
            case device: {
                typeFlag = FLAGS_RENDER_FONTTYPE.setValue(0, 1);
                break;
            }
            case raster: {
                typeFlag = FLAGS_RENDER_FONTTYPE.setValue(0, 2);
                break;
            }
            default: {
                typeFlag = FLAGS_RENDER_FONTTYPE.setValue(0, 4);
            }
        }
        typeFlag = FLAGS_NO_FONT_SUBSTITUTION.setBoolean(typeFlag, this.isFontSubstitutable());
        fnt.setFontType(typeFlag);
        fnt.setPitchAndFamily(FontPitch.getNativeId(this.pitch, this.family));
        return fnt;
    }

    public void addFacet(FontEmbeddedData facet) {
        this.facets.add(facet);
    }

    public List<FontEmbeddedData> getFacets() {
        return this.facets;
    }

    @Internal
    public FontEntityAtom getFontEntityAtom() {
        return this.fontEntityAtom;
    }

    public static enum FontRenderType {
        raster,
        device,
        truetype;

    }
}

