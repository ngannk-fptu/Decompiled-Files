/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.poi.ddf.EscherBoolProperty;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherDgRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherRGBProperty;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherRecordTypes;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.ddf.EscherSpgrRecord;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.hslf.record.BinaryTagDataBlob;
import org.apache.poi.hslf.record.CString;
import org.apache.poi.hslf.record.EscherTextboxWrapper;
import org.apache.poi.hslf.record.HSLFEscherClientDataRecord;
import org.apache.poi.hslf.record.HSLFEscherRecordFactory;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.StyleTextProp9Atom;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndian;

public final class PPDrawing
extends RecordAtom
implements Iterable<EscherRecord> {
    private final byte[] _header;
    private long _type;
    private EscherTextboxWrapper[] textboxWrappers;
    private final EscherContainerRecord dgContainer = new EscherContainerRecord();
    private EscherDgRecord dg;

    public List<EscherRecord> getEscherRecords() {
        return Collections.singletonList(this.dgContainer);
    }

    @Override
    public Iterator<EscherRecord> iterator() {
        return this.getEscherRecords().iterator();
    }

    @Override
    public Spliterator<EscherRecord> spliterator() {
        return this.getEscherRecords().spliterator();
    }

    public EscherTextboxWrapper[] getTextboxWrappers() {
        return this.textboxWrappers;
    }

    public PPDrawing() {
        this._header = new byte[8];
        LittleEndian.putUShort(this._header, 0, 15);
        LittleEndian.putUShort(this._header, 2, RecordTypes.PPDrawing.typeID);
        LittleEndian.putInt(this._header, 4, 0);
        this.textboxWrappers = new EscherTextboxWrapper[0];
        this.create();
    }

    PPDrawing(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._type = LittleEndian.getUShort(this._header, 2);
        HSLFEscherRecordFactory erf = new HSLFEscherRecordFactory();
        this.dgContainer.fillFields(source, start + 8, erf);
        if (this.dgContainer.getRecordId() != EscherRecordTypes.DG_CONTAINER.typeID) {
            throw new IllegalArgumentException("Unexpected record type: " + this.dgContainer.getRecordId());
        }
        this.dg = (EscherDgRecord)this.dgContainer.getChildById(EscherRecordTypes.DG.typeID);
        this.textboxWrappers = (EscherTextboxWrapper[])Stream.of(this.dgContainer).flatMap(PPDrawing.findEscherContainer(EscherRecordTypes.SPGR_CONTAINER)).flatMap(PPDrawing.findEscherContainer(EscherRecordTypes.SP_CONTAINER)).flatMap(PPDrawing::getTextboxHelper).toArray(EscherTextboxWrapper[]::new);
    }

    private static Stream<EscherTextboxWrapper> getTextboxHelper(EscherContainerRecord spContainer) {
        Optional oTB = PPDrawing.firstEscherRecord(spContainer, EscherRecordTypes.CLIENT_TEXTBOX);
        if (!oTB.isPresent()) {
            return Stream.empty();
        }
        EscherTextboxWrapper tbw = new EscherTextboxWrapper((EscherTextboxRecord)oTB.get());
        PPDrawing.findInSpContainer(spContainer).ifPresent(tbw::setStyleTextProp9Atom);
        Optional<EscherSpRecord> oSP = PPDrawing.firstEscherRecord(spContainer, EscherRecordTypes.SP);
        oSP.map(EscherSpRecord::getShapeId).ifPresent(tbw::setShapeId);
        return Stream.of(tbw);
    }

    private static Optional<StyleTextProp9Atom> findInSpContainer(EscherContainerRecord spContainer) {
        Optional<HSLFEscherClientDataRecord> oCD = PPDrawing.firstEscherRecord(spContainer, EscherRecordTypes.CLIENT_DATA);
        return oCD.map(HSLFEscherClientDataRecord::getHSLFChildRecords).map(Collection::stream).orElseGet(Stream::empty).filter(PPDrawing.sameHSLF(RecordTypes.ProgTags)).flatMap(r -> Stream.of(r.getChildRecords())).filter(PPDrawing.sameHSLF(RecordTypes.ProgBinaryTag)).flatMap(PPDrawing::findInProgBinaryTag).findFirst();
    }

    private static Stream<StyleTextProp9Atom> findInProgBinaryTag(Record r) {
        BinaryTagDataBlob blob;
        StyleTextProp9Atom prop9;
        Record[] ch = r.getChildRecords();
        if (ch != null && ch.length == 2 && ch[0] instanceof CString && ch[1] instanceof BinaryTagDataBlob && "___PPT9".equals(((CString)ch[0]).getText()) && (prop9 = (StyleTextProp9Atom)(blob = (BinaryTagDataBlob)ch[1]).findFirstOfType(RecordTypes.StyleTextProp9Atom.typeID)) != null) {
            return Stream.of(prop9);
        }
        return Stream.empty();
    }

    @Override
    public long getRecordType() {
        return this._type;
    }

    @Override
    public Record[] getChildRecords() {
        return null;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        for (EscherTextboxWrapper w : this.textboxWrappers) {
            w.writeOut(null);
        }
        int newSize = 0;
        LittleEndian.putInt(this._header, 4, newSize += this.dgContainer.getRecordSize());
        out.write(this._header);
        byte[] b = new byte[newSize];
        int done = 0;
        this.dgContainer.serialize(done, b);
        out.write(b);
    }

    private void create() {
        this.dgContainer.setRecordId(EscherContainerRecord.DG_CONTAINER);
        this.dgContainer.setOptions((short)15);
        this.dg = new EscherDgRecord();
        this.dg.setOptions((short)16);
        this.dg.setNumShapes(1);
        this.dgContainer.addChildRecord(this.dg);
        EscherContainerRecord spgrContainer = new EscherContainerRecord();
        spgrContainer.setOptions((short)15);
        spgrContainer.setRecordId(EscherContainerRecord.SPGR_CONTAINER);
        EscherContainerRecord spContainer = new EscherContainerRecord();
        spContainer.setOptions((short)15);
        spContainer.setRecordId(EscherContainerRecord.SP_CONTAINER);
        EscherSpgrRecord spgr = new EscherSpgrRecord();
        spgr.setOptions((short)1);
        spContainer.addChildRecord(spgr);
        EscherSpRecord sp = new EscherSpRecord();
        sp.setOptions((short)((ShapeType.NOT_PRIMITIVE.nativeId << 4) + 2));
        sp.setFlags(5);
        spContainer.addChildRecord(sp);
        spgrContainer.addChildRecord(spContainer);
        this.dgContainer.addChildRecord(spgrContainer);
        spContainer = new EscherContainerRecord();
        spContainer.setOptions((short)15);
        spContainer.setRecordId(EscherContainerRecord.SP_CONTAINER);
        sp = new EscherSpRecord();
        sp.setOptions((short)((ShapeType.RECT.nativeId << 4) + 2));
        sp.setFlags(3072);
        spContainer.addChildRecord(sp);
        EscherOptRecord opt = new EscherOptRecord();
        opt.setRecordId(EscherOptRecord.RECORD_ID);
        opt.addEscherProperty(new EscherRGBProperty(EscherPropertyTypes.FILL__FILLCOLOR, 0x8000000));
        opt.addEscherProperty(new EscherRGBProperty(EscherPropertyTypes.FILL__FILLBACKCOLOR, 0x8000005));
        opt.addEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.FILL__RECTRIGHT, 10064750));
        opt.addEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.FILL__RECTBOTTOM, 7778750));
        opt.addEscherProperty(new EscherBoolProperty(EscherPropertyTypes.FILL__NOFILLHITTEST, 0x120012));
        opt.addEscherProperty(new EscherBoolProperty(EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH, 524288));
        opt.addEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.SHAPE__BLACKANDWHITESETTINGS, 9));
        opt.addEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.SHAPE__BACKGROUNDSHAPE, 65537));
        spContainer.addChildRecord(opt);
        this.dgContainer.addChildRecord(spContainer);
    }

    public void addTextboxWrapper(EscherTextboxWrapper txtbox) {
        EscherTextboxWrapper[] tw = new EscherTextboxWrapper[this.textboxWrappers.length + 1];
        System.arraycopy(this.textboxWrappers, 0, tw, 0, this.textboxWrappers.length);
        tw[this.textboxWrappers.length] = txtbox;
        this.textboxWrappers = tw;
    }

    public EscherContainerRecord getDgContainer() {
        return this.dgContainer;
    }

    public EscherDgRecord getEscherDgRecord() {
        return this.dg;
    }

    public StyleTextProp9Atom[] getNumberedListInfo() {
        return (StyleTextProp9Atom[])Stream.of(this.dgContainer).flatMap(PPDrawing.findEscherContainer(EscherRecordTypes.SPGR_CONTAINER)).flatMap(PPDrawing.findEscherContainer(EscherRecordTypes.SP_CONTAINER)).map(PPDrawing::findInSpContainer).filter(Optional::isPresent).map(Optional::get).toArray(StyleTextProp9Atom[]::new);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("escherRecords", this::getEscherRecords);
    }

    private static Predicate<Record> sameHSLF(RecordTypes type) {
        return p -> p.getRecordType() == (long)type.typeID;
    }

    private static Predicate<EscherRecord> sameEscher(EscherRecordTypes type) {
        return p -> p.getRecordId() == type.typeID;
    }

    private static <T extends EscherRecord> Optional<T> firstEscherRecord(Iterable<EscherRecord> container, EscherRecordTypes type) {
        return StreamSupport.stream(container.spliterator(), false).filter(PPDrawing.sameEscher(type)).map(o -> o).findFirst();
    }

    private static Function<EscherContainerRecord, Stream<EscherContainerRecord>> findEscherContainer(EscherRecordTypes type) {
        return r -> r.getChildContainers().stream().filter(PPDrawing.sameEscher(type));
    }
}

