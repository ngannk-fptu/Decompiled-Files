/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emf;

import java.util.function.Supplier;
import org.apache.poi.hemf.record.emf.HemfComment;
import org.apache.poi.hemf.record.emf.HemfDraw;
import org.apache.poi.hemf.record.emf.HemfFill;
import org.apache.poi.hemf.record.emf.HemfHeader;
import org.apache.poi.hemf.record.emf.HemfMisc;
import org.apache.poi.hemf.record.emf.HemfPalette;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emf.HemfText;
import org.apache.poi.hemf.record.emf.HemfWindowing;
import org.apache.poi.hemf.record.emf.UnimplementedHemfRecord;
import org.apache.poi.util.Internal;

@Internal
public enum HemfRecordType {
    header(1L, HemfHeader::new),
    polyBezier(2L, HemfDraw.EmfPolyBezier::new),
    polygon(3L, HemfDraw.EmfPolygon::new),
    polyline(4L, HemfDraw.EmfPolyline::new),
    polyBezierTo(5L, HemfDraw.EmfPolyBezierTo::new),
    polylineTo(6L, HemfDraw.EmfPolylineTo::new),
    polyPolyline(7L, HemfDraw.EmfPolyPolyline::new),
    polyPolygon(8L, HemfDraw.EmfPolyPolygon::new),
    setWindowExtEx(9L, HemfWindowing.EmfSetWindowExtEx::new),
    setWindowOrgEx(10L, HemfWindowing.EmfSetWindowOrgEx::new),
    setViewportExtEx(11L, HemfWindowing.EmfSetViewportExtEx::new),
    setViewportOrgEx(12L, HemfWindowing.EmfSetViewportOrgEx::new),
    setBrushOrgEx(13L, HemfMisc.EmfSetBrushOrgEx::new),
    eof(14L, HemfMisc.EmfEof::new),
    setPixelV(15L, HemfDraw.EmfSetPixelV::new),
    setMapperFlags(16L, HemfMisc.EmfSetMapperFlags::new),
    setMapMode(17L, HemfMisc.EmfSetMapMode::new),
    setBkMode(18L, HemfMisc.EmfSetBkMode::new),
    setPolyfillMode(19L, HemfFill.EmfSetPolyfillMode::new),
    setRop2(20L, HemfMisc.EmfSetRop2::new),
    setStretchBltMode(21L, HemfMisc.EmfSetStretchBltMode::new),
    setTextAlign(22L, HemfText.EmfSetTextAlign::new),
    setcoloradjustment(23L, UnimplementedHemfRecord::new),
    setTextColor(24L, HemfText.EmfSetTextColor::new),
    setBkColor(25L, HemfMisc.EmfSetBkColor::new),
    setOffsetClipRgn(26L, HemfWindowing.EmfSetOffsetClipRgn::new),
    setMoveToEx(27L, HemfDraw.EmfSetMoveToEx::new),
    setmetargn(28L, UnimplementedHemfRecord::new),
    setExcludeClipRect(29L, HemfWindowing.EmfSetExcludeClipRect::new),
    setIntersectClipRect(30L, HemfWindowing.EmfSetIntersectClipRect::new),
    scaleViewportExtEx(31L, HemfWindowing.EmfScaleViewportExtEx::new),
    scaleWindowExtEx(32L, HemfWindowing.EmfScaleWindowExtEx::new),
    saveDc(33L, HemfMisc.EmfSaveDc::new),
    restoreDc(34L, HemfMisc.EmfRestoreDc::new),
    setWorldTransform(35L, HemfMisc.EmfSetWorldTransform::new),
    modifyWorldTransform(36L, HemfMisc.EmfModifyWorldTransform::new),
    selectObject(37L, HemfDraw.EmfSelectObject::new),
    createPen(38L, HemfMisc.EmfCreatePen::new),
    createBrushIndirect(39L, HemfMisc.EmfCreateBrushIndirect::new),
    deleteobject(40L, HemfMisc.EmfDeleteObject::new),
    anglearc(41L, UnimplementedHemfRecord::new),
    ellipse(42L, HemfDraw.EmfEllipse::new),
    rectangle(43L, HemfDraw.EmfRectangle::new),
    roundRect(44L, HemfDraw.EmfRoundRect::new),
    arc(45L, HemfDraw.EmfArc::new),
    chord(46L, HemfDraw.EmfChord::new),
    pie(47L, HemfDraw.EmfPie::new),
    selectPalette(48L, HemfPalette.EmfSelectPalette::new),
    createPalette(49L, HemfPalette.EmfCreatePalette::new),
    setPaletteEntries(50L, HemfPalette.EmfSetPaletteEntries::new),
    resizePalette(51L, HemfPalette.EmfResizePalette::new),
    realizePalette(52L, HemfPalette.EmfRealizePalette::new),
    extFloodFill(53L, HemfFill.EmfExtFloodFill::new),
    lineTo(54L, HemfDraw.EmfLineTo::new),
    arcTo(55L, HemfDraw.EmfArcTo::new),
    polyDraw(56L, HemfDraw.EmfPolyDraw::new),
    setarcdirection(57L, UnimplementedHemfRecord::new),
    setMiterLimit(58L, HemfMisc.EmfSetMiterLimit::new),
    beginPath(59L, HemfDraw.EmfBeginPath::new),
    endPath(60L, HemfDraw.EmfEndPath::new),
    closeFigure(61L, HemfDraw.EmfCloseFigure::new),
    fillPath(62L, HemfDraw.EmfFillPath::new),
    strokeAndFillPath(63L, HemfDraw.EmfStrokeAndFillPath::new),
    strokePath(64L, HemfDraw.EmfStrokePath::new),
    flattenPath(65L, HemfDraw.EmfFlattenPath::new),
    widenPath(66L, HemfDraw.EmfWidenPath::new),
    selectClipPath(67L, HemfWindowing.EmfSelectClipPath::new),
    abortPath(68L, HemfDraw.EmfAbortPath::new),
    comment(70L, HemfComment.EmfComment::new),
    fillRgn(71L, HemfFill.EmfFillRgn::new),
    frameRgn(72L, HemfFill.EmfFrameRgn::new),
    invertRgn(73L, HemfFill.EmfInvertRgn::new),
    paintRgn(74L, HemfFill.EmfPaintRgn::new),
    extSelectClipRgn(75L, HemfFill.EmfExtSelectClipRgn::new),
    bitBlt(76L, HemfFill.EmfBitBlt::new),
    stretchBlt(77L, HemfFill.EmfStretchBlt::new),
    maskblt(78L, UnimplementedHemfRecord::new),
    plgblt(79L, UnimplementedHemfRecord::new),
    setDiBitsToDevice(80L, HemfFill.EmfSetDiBitsToDevice::new),
    stretchDiBits(81L, HemfFill.EmfStretchDiBits::new),
    extCreateFontIndirectW(82L, HemfText.EmfExtCreateFontIndirectW::new),
    extTextOutA(83L, HemfText.EmfExtTextOutA::new),
    extTextOutW(84L, HemfText.EmfExtTextOutW::new),
    polyBezier16(85L, HemfDraw.EmfPolyBezier16::new),
    polygon16(86L, HemfDraw.EmfPolygon16::new),
    polyline16(87L, HemfDraw.EmfPolyline16::new),
    polyBezierTo16(88L, HemfDraw.EmfPolyBezierTo16::new),
    polylineTo16(89L, HemfDraw.EmfPolylineTo16::new),
    polyPolyline16(90L, HemfDraw.EmfPolyPolyline16::new),
    polyPolygon16(91L, HemfDraw.EmfPolyPolygon16::new),
    polyDraw16(92L, HemfDraw.EmfPolyDraw16::new),
    createMonoBrush(93L, HemfMisc.EmfCreateMonoBrush::new),
    createDibPatternBrushPt(94L, HemfMisc.EmfCreateDibPatternBrushPt::new),
    extCreatePen(95L, HemfMisc.EmfExtCreatePen::new),
    polytextouta(96L, HemfText.PolyTextOutA::new),
    polytextoutw(97L, HemfText.PolyTextOutW::new),
    seticmmode(98L, HemfPalette.EmfSetIcmMode::new),
    createcolorspace(99L, UnimplementedHemfRecord::new),
    setcolorspace(100L, UnimplementedHemfRecord::new),
    deletecolorspace(101L, UnimplementedHemfRecord::new),
    glsrecord(102L, UnimplementedHemfRecord::new),
    glsboundedrecord(103L, UnimplementedHemfRecord::new),
    pixelformat(104L, UnimplementedHemfRecord::new),
    drawescape(105L, UnimplementedHemfRecord::new),
    extescape(106L, UnimplementedHemfRecord::new),
    smalltextout(108L, UnimplementedHemfRecord::new),
    forceufimapping(109L, UnimplementedHemfRecord::new),
    namedescape(110L, UnimplementedHemfRecord::new),
    colorcorrectpalette(111L, UnimplementedHemfRecord::new),
    seticmprofilea(112L, UnimplementedHemfRecord::new),
    seticmprofilew(113L, UnimplementedHemfRecord::new),
    alphaBlend(114L, HemfFill.EmfAlphaBlend::new),
    setlayout(115L, UnimplementedHemfRecord::new),
    transparentblt(116L, UnimplementedHemfRecord::new),
    gradientfill(118L, UnimplementedHemfRecord::new),
    setlinkdufis(119L, UnimplementedHemfRecord::new),
    settextjustification(120L, HemfText.SetTextJustification::new),
    colormatchtargetw(121L, UnimplementedHemfRecord::new),
    createcolorspacew(122L, UnimplementedHemfRecord::new);

    public final long id;
    public final Supplier<? extends HemfRecord> constructor;

    private HemfRecordType(long id, Supplier<? extends HemfRecord> constructor) {
        this.id = id;
        this.constructor = constructor;
    }

    public static HemfRecordType getById(long id) {
        for (HemfRecordType wrt : HemfRecordType.values()) {
            if (wrt.id != id) continue;
            return wrt;
        }
        return null;
    }
}

