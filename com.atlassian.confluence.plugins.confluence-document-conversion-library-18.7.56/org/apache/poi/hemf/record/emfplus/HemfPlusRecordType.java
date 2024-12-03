/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emfplus;

import java.util.function.Supplier;
import org.apache.poi.hemf.record.emfplus.HemfPlusDraw;
import org.apache.poi.hemf.record.emfplus.HemfPlusHeader;
import org.apache.poi.hemf.record.emfplus.HemfPlusMisc;
import org.apache.poi.hemf.record.emfplus.HemfPlusObject;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecord;
import org.apache.poi.hemf.record.emfplus.UnimplementedHemfPlusRecord;
import org.apache.poi.util.Internal;

@Internal
public enum HemfPlusRecordType {
    header(16385L, HemfPlusHeader::new),
    eof(16386L, HemfPlusMisc.EmfPlusEOF::new),
    comment(16387L, UnimplementedHemfPlusRecord::new),
    getDC(16388L, HemfPlusMisc.EmfPlusGetDC::new),
    multiFormatStart(16389L, UnimplementedHemfPlusRecord::new),
    multiFormatSection(16390L, UnimplementedHemfPlusRecord::new),
    multiFormatEnd(16391L, UnimplementedHemfPlusRecord::new),
    object(16392L, HemfPlusObject.EmfPlusObject::new),
    clear(16393L, UnimplementedHemfPlusRecord::new),
    fillRects(16394L, HemfPlusDraw.EmfPlusFillRects::new),
    drawRects(16395L, HemfPlusDraw.EmfPlusDrawRects::new),
    fillPolygon(16396L, UnimplementedHemfPlusRecord::new),
    drawLines(16397L, UnimplementedHemfPlusRecord::new),
    fillEllipse(16398L, UnimplementedHemfPlusRecord::new),
    drawEllipse(16399L, UnimplementedHemfPlusRecord::new),
    fillPie(16400L, UnimplementedHemfPlusRecord::new),
    drawPie(16401L, UnimplementedHemfPlusRecord::new),
    drawArc(16402L, UnimplementedHemfPlusRecord::new),
    fillRegion(16403L, HemfPlusDraw.EmfPlusFillRegion::new),
    fillPath(16404L, HemfPlusDraw.EmfPlusFillPath::new),
    drawPath(16405L, HemfPlusDraw.EmfPlusDrawPath::new),
    fillClosedCurve(16406L, UnimplementedHemfPlusRecord::new),
    drawClosedCurve(16407L, UnimplementedHemfPlusRecord::new),
    drawCurve(16408L, UnimplementedHemfPlusRecord::new),
    drawBeziers(16409L, UnimplementedHemfPlusRecord::new),
    drawImage(16410L, HemfPlusDraw.EmfPlusDrawImage::new),
    drawImagePoints(16411L, HemfPlusDraw.EmfPlusDrawImagePoints::new),
    drawString(16412L, UnimplementedHemfPlusRecord::new),
    setRenderingOrigin(16413L, HemfPlusMisc.EmfPlusSetRenderingOrigin::new),
    setAntiAliasMode(16414L, HemfPlusMisc.EmfPlusSetAntiAliasMode::new),
    setTextRenderingHint(16415L, HemfPlusMisc.EmfPlusSetTextRenderingHint::new),
    setTextContrast(16416L, UnimplementedHemfPlusRecord::new),
    setInterpolationMode(16417L, HemfPlusMisc.EmfPlusSetInterpolationMode::new),
    setPixelOffsetMode(16418L, HemfPlusMisc.EmfPlusSetPixelOffsetMode::new),
    setCompositingMode(16419L, HemfPlusMisc.EmfPlusSetCompositingMode::new),
    setCompositingQuality(16420L, HemfPlusMisc.EmfPlusSetCompositingQuality::new),
    save(16421L, HemfPlusMisc.EmfPlusSave::new),
    restore(16422L, HemfPlusMisc.EmfPlusRestore::new),
    beginContainer(16423L, UnimplementedHemfPlusRecord::new),
    beginContainerNoParams(1064L, UnimplementedHemfPlusRecord::new),
    endContainer(16425L, UnimplementedHemfPlusRecord::new),
    setWorldTransform(16426L, HemfPlusMisc.EmfPlusSetWorldTransform::new),
    resetWorldTransform(16427L, HemfPlusMisc.EmfPlusResetWorldTransform::new),
    multiplyWorldTransform(16428L, HemfPlusMisc.EmfPlusMultiplyWorldTransform::new),
    translateWorldTransform(16429L, UnimplementedHemfPlusRecord::new),
    scaleWorldTransform(16430L, UnimplementedHemfPlusRecord::new),
    rotateWorldTransform(16431L, UnimplementedHemfPlusRecord::new),
    setPageTransform(16432L, HemfPlusMisc.EmfPlusSetPageTransform::new),
    resetClip(16433L, HemfPlusMisc.EmfPlusResetClip::new),
    setClipRect(16434L, HemfPlusMisc.EmfPlusSetClipRect::new),
    setClipRegion(16435L, HemfPlusMisc.EmfPlusSetClipRegion::new),
    setClipPath(16436L, HemfPlusMisc.EmfPlusSetClipPath::new),
    offsetClip(16437L, UnimplementedHemfPlusRecord::new),
    drawDriverString(16438L, HemfPlusDraw.EmfPlusDrawDriverString::new),
    strokeFillPath(16439L, UnimplementedHemfPlusRecord::new),
    serializableObject(16440L, UnimplementedHemfPlusRecord::new),
    setTSGraphics(16441L, UnimplementedHemfPlusRecord::new),
    setTSClip(16442L, UnimplementedHemfPlusRecord::new);

    public final long id;
    public final Supplier<? extends HemfPlusRecord> constructor;

    private HemfPlusRecordType(long id, Supplier<? extends HemfPlusRecord> constructor) {
        this.id = id;
        this.constructor = constructor;
    }

    public static HemfPlusRecordType getById(long id) {
        for (HemfPlusRecordType wrt : HemfPlusRecordType.values()) {
            if (wrt.id != id) continue;
            return wrt;
        }
        return null;
    }
}

