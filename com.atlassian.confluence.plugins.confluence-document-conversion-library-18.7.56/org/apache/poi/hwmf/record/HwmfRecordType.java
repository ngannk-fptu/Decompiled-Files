/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.util.function.Supplier;
import org.apache.poi.hwmf.record.HwmfDraw;
import org.apache.poi.hwmf.record.HwmfEscape;
import org.apache.poi.hwmf.record.HwmfFill;
import org.apache.poi.hwmf.record.HwmfMisc;
import org.apache.poi.hwmf.record.HwmfPalette;
import org.apache.poi.hwmf.record.HwmfRecord;
import org.apache.poi.hwmf.record.HwmfText;
import org.apache.poi.hwmf.record.HwmfWindowing;

public enum HwmfRecordType {
    eof(0, null),
    animatePalette(1078, HwmfPalette.WmfAnimatePalette::new),
    arc(2071, HwmfDraw.WmfArc::new),
    bitBlt(2338, HwmfFill.WmfBitBlt::new),
    chord(2096, HwmfDraw.WmfChord::new),
    createBrushIndirect(764, HwmfMisc.WmfCreateBrushIndirect::new),
    createFontIndirect(763, HwmfText.WmfCreateFontIndirect::new),
    createPalette(247, HwmfPalette.WmfCreatePalette::new),
    createPatternBrush(505, HwmfMisc.WmfCreatePatternBrush::new),
    createPenIndirect(762, HwmfMisc.WmfCreatePenIndirect::new),
    createRegion(1791, HwmfWindowing.WmfCreateRegion::new),
    deleteObject(496, HwmfMisc.WmfDeleteObject::new),
    dibBitBlt(2368, HwmfFill.WmfDibBitBlt::new),
    dibCreatePatternBrush(322, HwmfMisc.WmfDibCreatePatternBrush::new),
    dibStretchBlt(2881, HwmfFill.WmfDibStretchBlt::new),
    ellipse(1048, HwmfDraw.WmfEllipse::new),
    escape(1574, HwmfEscape::new),
    excludeClipRect(1045, HwmfWindowing.WmfExcludeClipRect::new),
    extFloodFill(1352, HwmfFill.WmfExtFloodFill::new),
    extTextOut(2610, HwmfText.WmfExtTextOut::new),
    fillRegion(552, HwmfFill.WmfFillRegion::new),
    floodFill(1049, HwmfFill.WmfFloodFill::new),
    frameRegion(1065, HwmfDraw.WmfFrameRegion::new),
    intersectClipRect(1046, HwmfWindowing.WmfIntersectClipRect::new),
    invertRegion(298, HwmfFill.WmfInvertRegion::new),
    lineTo(531, HwmfDraw.WmfLineTo::new),
    moveTo(532, HwmfDraw.WmfMoveTo::new),
    offsetClipRgn(544, HwmfWindowing.WmfOffsetClipRgn::new),
    offsetViewportOrg(529, HwmfWindowing.WmfOffsetViewportOrg::new),
    offsetWindowOrg(527, HwmfWindowing.WmfOffsetWindowOrg::new),
    paintRegion(299, HwmfFill.WmfPaintRegion::new),
    patBlt(1565, HwmfFill.WmfPatBlt::new),
    pie(2074, HwmfDraw.WmfPie::new),
    polygon(804, HwmfDraw.WmfPolygon::new),
    polyline(805, HwmfDraw.WmfPolyline::new),
    polyPolygon(1336, HwmfDraw.WmfPolyPolygon::new),
    realizePalette(53, HwmfPalette.WmfRealizePalette::new),
    rectangle(1051, HwmfDraw.WmfRectangle::new),
    resizePalette(313, HwmfPalette.WmfResizePalette::new),
    restoreDc(295, HwmfMisc.WmfRestoreDc::new),
    roundRect(1564, HwmfDraw.WmfRoundRect::new),
    saveDc(30, HwmfMisc.WmfSaveDc::new),
    scaleViewportExt(1042, HwmfWindowing.WmfScaleViewportExt::new),
    scaleWindowExt(1040, HwmfWindowing.WmfScaleWindowExt::new),
    selectClipRegion(300, HwmfWindowing.WmfSelectClipRegion::new),
    selectObject(301, HwmfDraw.WmfSelectObject::new),
    selectPalette(564, HwmfPalette.WmfSelectPalette::new),
    setBkColor(513, HwmfMisc.WmfSetBkColor::new),
    setBkMode(258, HwmfMisc.WmfSetBkMode::new),
    setDibToDev(3379, HwmfFill.WmfSetDibToDev::new),
    setLayout(329, HwmfMisc.WmfSetLayout::new),
    setMapMode(259, HwmfMisc.WmfSetMapMode::new),
    setMapperFlags(561, HwmfMisc.WmfSetMapperFlags::new),
    setPalEntries(55, HwmfPalette.WmfSetPaletteEntries::new),
    setPixel(1055, HwmfDraw.WmfSetPixel::new),
    setPolyFillMode(262, HwmfFill.WmfSetPolyfillMode::new),
    setRelabs(261, HwmfMisc.WmfSetRelabs::new),
    setRop2(260, HwmfMisc.WmfSetRop2::new),
    setStretchBltMode(263, HwmfMisc.WmfSetStretchBltMode::new),
    setTextAlign(302, HwmfText.WmfSetTextAlign::new),
    setTextCharExtra(264, HwmfText.WmfSetTextCharExtra::new),
    setTextColor(521, HwmfText.WmfSetTextColor::new),
    setTextJustification(522, HwmfText.WmfSetTextJustification::new),
    setViewportExt(526, HwmfWindowing.WmfSetViewportExt::new),
    setViewportOrg(525, HwmfWindowing.WmfSetViewportOrg::new),
    setWindowExt(524, HwmfWindowing.WmfSetWindowExt::new),
    setWindowOrg(523, HwmfWindowing.WmfSetWindowOrg::new),
    stretchBlt(2851, HwmfFill.WmfStretchBlt::new),
    stretchDib(3907, HwmfFill.WmfStretchDib::new),
    textOut(1313, HwmfText.WmfTextOut::new);

    public final int id;
    public final Supplier<? extends HwmfRecord> constructor;

    private HwmfRecordType(int id, Supplier<? extends HwmfRecord> constructor) {
        this.id = id;
        this.constructor = constructor;
    }

    public static HwmfRecordType getById(int id) {
        for (HwmfRecordType wrt : HwmfRecordType.values()) {
            if (wrt.id != id) continue;
            return wrt;
        }
        return null;
    }
}

