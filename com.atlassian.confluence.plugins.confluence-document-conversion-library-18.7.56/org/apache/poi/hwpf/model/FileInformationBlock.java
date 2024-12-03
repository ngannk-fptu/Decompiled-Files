/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.FIBFieldHandler;
import org.apache.poi.hwpf.model.FSPADocumentPart;
import org.apache.poi.hwpf.model.FibBase;
import org.apache.poi.hwpf.model.FibRgLw;
import org.apache.poi.hwpf.model.FibRgLw95;
import org.apache.poi.hwpf.model.FibRgLw97;
import org.apache.poi.hwpf.model.FibRgW97;
import org.apache.poi.hwpf.model.FieldsDocumentPart;
import org.apache.poi.hwpf.model.NoteType;
import org.apache.poi.hwpf.model.SubdocumentType;
import org.apache.poi.hwpf.model.types.FibBaseAbstractType;
import org.apache.poi.hwpf.model.types.FibRgLw97AbstractType;
import org.apache.poi.hwpf.model.types.FibRgW97AbstractType;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class FileInformationBlock {
    private static final Logger LOG = LogManager.getLogger(FileInformationBlock.class);
    private final FibBase _fibBase;
    private final int _csw;
    private final FibRgW97 _fibRgW;
    private final int _cslw;
    private final FibRgLw _fibRgLw;
    private int _cbRgFcLcb;
    private FIBFieldHandler _fieldHandler;
    private final int _cswNew;
    private final int _nFibNew;
    private final byte[] _fibRgCswNew;

    public FileInformationBlock(byte[] mainDocument) {
        int offset = 0;
        this._fibBase = new FibBase(mainDocument, offset);
        offset = FibBaseAbstractType.getSize();
        assert (offset == 32);
        this._csw = LittleEndian.getUShort(mainDocument, offset);
        this._fibRgW = new FibRgW97(mainDocument, offset += 2);
        assert ((offset += FibRgW97AbstractType.getSize()) == 62);
        this._cslw = LittleEndian.getUShort(mainDocument, offset);
        offset += 2;
        if (this._fibBase.getNFib() < 105) {
            this._fibRgLw = new FibRgLw95(mainDocument, offset);
            offset += FibRgLw97AbstractType.getSize();
            this._cbRgFcLcb = 74;
            offset += this._cbRgFcLcb * 4 * 2;
            this._cswNew = 0;
            this._nFibNew = -1;
            this._fibRgCswNew = new byte[0];
            return;
        }
        this._fibRgLw = new FibRgLw97(mainDocument, offset);
        assert ((offset += FibRgLw97AbstractType.getSize()) == 152);
        this._cbRgFcLcb = LittleEndian.getUShort(mainDocument, offset);
        offset += 2;
        this._cswNew = LittleEndian.getUShort(mainDocument, offset += this._cbRgFcLcb * 4 * 2);
        offset += 2;
        if (this._cswNew != 0) {
            this._nFibNew = LittleEndian.getUShort(mainDocument, offset);
            int fibRgCswNewLength = (this._cswNew - 1) * 2;
            this._fibRgCswNew = IOUtils.safelyClone(mainDocument, offset += 2, fibRgCswNewLength, HWPFDocument.getMaxRecordLength());
        } else {
            this._nFibNew = -1;
            this._fibRgCswNew = new byte[0];
        }
        this.assertCbRgFcLcb();
        this.assertCswNew();
    }

    private void assertCbRgFcLcb() {
        int nfib = this.getNFib();
        String nfibHex = String.format(Locale.ROOT, "%04X", nfib);
        switch (nfib) {
            case 113: {
                break;
            }
            case 190: 
            case 191: 
            case 192: 
            case 193: 
            case 194: 
            case 195: {
                FileInformationBlock.assertCbRgFcLcb(nfibHex, 93, "0x005D", this._cbRgFcLcb);
                break;
            }
            case 216: 
            case 217: {
                FileInformationBlock.assertCbRgFcLcb(nfibHex, 108, "0x006C", this._cbRgFcLcb);
                break;
            }
            case 257: {
                FileInformationBlock.assertCbRgFcLcb("0x0101", 136, "0x0088", this._cbRgFcLcb);
                break;
            }
            case 267: 
            case 268: {
                FileInformationBlock.assertCbRgFcLcb(nfibHex, 164, "0x00A4", this._cbRgFcLcb);
                break;
            }
            case 274: {
                FileInformationBlock.assertCbRgFcLcb("0x0112", 183, "0x00B7", this._cbRgFcLcb);
                break;
            }
            default: {
                LOG.atWarn().log("Invalid file format version number: {}({})", (Object)Unbox.box(nfib), (Object)nfibHex);
            }
        }
    }

    private static void assertCbRgFcLcb(String strNFib, int expectedCbRgFcLcb, String strCbRgFcLcb, int cbRgFcLcb) {
        if (cbRgFcLcb == expectedCbRgFcLcb) {
            return;
        }
        LOG.atWarn().log("Since FIB.nFib == {} value of FIB.cbRgFcLcb MUST be {}, not 0x{}", (Object)strNFib, (Object)strCbRgFcLcb, (Object)Integer.toHexString(cbRgFcLcb));
    }

    private void assertCswNew() {
        switch (this.getNFib()) {
            case 193: {
                FileInformationBlock.assertCswNew("0x00C1", 0, "0x0000", this._cswNew);
                break;
            }
            case 217: {
                FileInformationBlock.assertCswNew("0x00D9", 2, "0x0002", this._cswNew);
                break;
            }
            case 257: {
                FileInformationBlock.assertCswNew("0x0101", 2, "0x0002", this._cswNew);
                break;
            }
            case 268: {
                FileInformationBlock.assertCswNew("0x010C", 2, "0x0002", this._cswNew);
                break;
            }
            case 274: {
                FileInformationBlock.assertCswNew("0x0112", 5, "0x0005", this._cswNew);
                break;
            }
            default: {
                LOG.atWarn().log("Invalid file format version number: {}", (Object)Unbox.box(this.getNFib()));
            }
        }
    }

    private static void assertCswNew(String strNFib, int expectedCswNew, String strExpectedCswNew, int cswNew) {
        if (cswNew == expectedCswNew) {
            return;
        }
        LOG.atWarn().log("Since FIB.nFib == {} value of FIB.cswNew MUST be {}, not 0x{}", (Object)strNFib, (Object)strExpectedCswNew, (Object)Integer.toHexString(cswNew));
    }

    public void fillVariableFields(byte[] mainDocument, byte[] tableStream) {
        HashSet<Integer> knownFieldSet = new HashSet<Integer>();
        knownFieldSet.add(1);
        knownFieldSet.add(33);
        knownFieldSet.add(31);
        knownFieldSet.add(12);
        knownFieldSet.add(13);
        knownFieldSet.add(6);
        knownFieldSet.add(73);
        knownFieldSet.add(74);
        for (FieldsDocumentPart fieldsDocumentPart : FieldsDocumentPart.values()) {
            knownFieldSet.add(fieldsDocumentPart.getFibFieldsField());
        }
        knownFieldSet.add(22);
        knownFieldSet.add(23);
        knownFieldSet.add(21);
        for (Enum enum_ : NoteType.values()) {
            knownFieldSet.add(((NoteType)enum_).getFibDescriptorsFieldIndex());
            knownFieldSet.add(((NoteType)enum_).getFibTextPositionsFieldIndex());
        }
        knownFieldSet.add(15);
        knownFieldSet.add(51);
        knownFieldSet.add(71);
        knownFieldSet.add(87);
        this._fieldHandler = new FIBFieldHandler(mainDocument, 154, this._cbRgFcLcb, tableStream, knownFieldSet, true);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this._fibBase);
        stringBuilder.append("[FIB2]\n");
        stringBuilder.append("\tSubdocuments info:\n");
        for (SubdocumentType subdocumentType : SubdocumentType.values()) {
            stringBuilder.append("\t\t");
            stringBuilder.append((Object)subdocumentType);
            stringBuilder.append(" has length of ");
            stringBuilder.append(this.getSubdocumentTextStreamLength(subdocumentType));
            stringBuilder.append(" char(s)\n");
        }
        stringBuilder.append("\tFields PLCF info:\n");
        for (Enum enum_ : FieldsDocumentPart.values()) {
            stringBuilder.append("\t\t");
            stringBuilder.append(enum_);
            stringBuilder.append(": PLCF starts at ");
            stringBuilder.append(this.getFieldsPlcfOffset((FieldsDocumentPart)enum_));
            stringBuilder.append(" and have length of ");
            stringBuilder.append(this.getFieldsPlcfLength((FieldsDocumentPart)enum_));
            stringBuilder.append("\n");
        }
        stringBuilder.append("\tNotes PLCF info:\n");
        for (Enum enum_ : NoteType.values()) {
            stringBuilder.append("\t\t");
            stringBuilder.append(enum_);
            stringBuilder.append(": descriptions starts ");
            stringBuilder.append(this.getNotesDescriptorsOffset((NoteType)enum_));
            stringBuilder.append(" and have length of ");
            stringBuilder.append(this.getNotesDescriptorsSize((NoteType)enum_));
            stringBuilder.append(" bytes\n");
            stringBuilder.append("\t\t");
            stringBuilder.append(enum_);
            stringBuilder.append(": text positions starts ");
            stringBuilder.append(this.getNotesTextPositionsOffset((NoteType)enum_));
            stringBuilder.append(" and have length of ");
            stringBuilder.append(this.getNotesTextPositionsSize((NoteType)enum_));
            stringBuilder.append(" bytes\n");
        }
        stringBuilder.append(this._fieldHandler);
        try {
            stringBuilder.append("\tJava reflection info:\n");
            for (Method method : FileInformationBlock.class.getMethods()) {
                if (!method.getName().startsWith("get") || !Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers()) || method.getParameterTypes().length > 0) continue;
                stringBuilder.append("\t\t");
                stringBuilder.append(method.getName());
                stringBuilder.append(" => ");
                stringBuilder.append(method.invoke((Object)this, new Object[0]));
                stringBuilder.append("\n");
            }
        }
        catch (Exception exc) {
            stringBuilder.append("(exc: ").append(exc.getMessage()).append(")");
        }
        stringBuilder.append("[/FIB2]\n");
        return stringBuilder.toString();
    }

    public int getNFib() {
        if (this._cswNew == 0) {
            return this._fibBase.getNFib();
        }
        return this._nFibNew;
    }

    public int getFcDop() {
        return this._fieldHandler.getFieldOffset(31);
    }

    public void setFcDop(int fcDop) {
        this._fieldHandler.setFieldOffset(31, fcDop);
    }

    public int getLcbDop() {
        return this._fieldHandler.getFieldSize(31);
    }

    public void setLcbDop(int lcbDop) {
        this._fieldHandler.setFieldSize(31, lcbDop);
    }

    public int getFcStshf() {
        return this._fieldHandler.getFieldOffset(1);
    }

    public int getLcbStshf() {
        return this._fieldHandler.getFieldSize(1);
    }

    public void setFcStshf(int fcStshf) {
        this._fieldHandler.setFieldOffset(1, fcStshf);
    }

    public void setLcbStshf(int lcbStshf) {
        this._fieldHandler.setFieldSize(1, lcbStshf);
    }

    public int getFcClx() {
        return this._fieldHandler.getFieldOffset(33);
    }

    public int getLcbClx() {
        return this._fieldHandler.getFieldSize(33);
    }

    public void setFcClx(int fcClx) {
        this._fieldHandler.setFieldOffset(33, fcClx);
    }

    public void setLcbClx(int lcbClx) {
        this._fieldHandler.setFieldSize(33, lcbClx);
    }

    public int getFcPlcfbteChpx() {
        return this._fieldHandler.getFieldOffset(12);
    }

    public int getLcbPlcfbteChpx() {
        return this._fieldHandler.getFieldSize(12);
    }

    public void setFcPlcfbteChpx(int fcPlcfBteChpx) {
        this._fieldHandler.setFieldOffset(12, fcPlcfBteChpx);
    }

    public void setLcbPlcfbteChpx(int lcbPlcfBteChpx) {
        this._fieldHandler.setFieldSize(12, lcbPlcfBteChpx);
    }

    public int getFcPlcfbtePapx() {
        return this._fieldHandler.getFieldOffset(13);
    }

    public int getLcbPlcfbtePapx() {
        return this._fieldHandler.getFieldSize(13);
    }

    public void setFcPlcfbtePapx(int fcPlcfBtePapx) {
        this._fieldHandler.setFieldOffset(13, fcPlcfBtePapx);
    }

    public void setLcbPlcfbtePapx(int lcbPlcfBtePapx) {
        this._fieldHandler.setFieldSize(13, lcbPlcfBtePapx);
    }

    public int getFcPlcfsed() {
        return this._fieldHandler.getFieldOffset(6);
    }

    public int getLcbPlcfsed() {
        return this._fieldHandler.getFieldSize(6);
    }

    public void setFcPlcfsed(int fcPlcfSed) {
        this._fieldHandler.setFieldOffset(6, fcPlcfSed);
    }

    public void setLcbPlcfsed(int lcbPlcfSed) {
        this._fieldHandler.setFieldSize(6, lcbPlcfSed);
    }

    @Deprecated
    public int getFcPlcfLst() {
        return this._fieldHandler.getFieldOffset(73);
    }

    public int getFcPlfLst() {
        return this._fieldHandler.getFieldOffset(73);
    }

    @Deprecated
    public int getLcbPlcfLst() {
        return this._fieldHandler.getFieldSize(73);
    }

    public int getLcbPlfLst() {
        return this._fieldHandler.getFieldSize(73);
    }

    @Deprecated
    public void setFcPlcfLst(int fcPlcfLst) {
        this._fieldHandler.setFieldOffset(73, fcPlcfLst);
    }

    public void setFcPlfLst(int fcPlfLst) {
        this._fieldHandler.setFieldOffset(73, fcPlfLst);
    }

    @Deprecated
    public void setLcbPlcfLst(int lcbPlcfLst) {
        this._fieldHandler.setFieldSize(73, lcbPlcfLst);
    }

    public void setLcbPlfLst(int lcbPlfLst) {
        this._fieldHandler.setFieldSize(73, lcbPlfLst);
    }

    public int getFcPlfLfo() {
        return this._fieldHandler.getFieldOffset(74);
    }

    public int getLcbPlfLfo() {
        return this._fieldHandler.getFieldSize(74);
    }

    public int getFcSttbfbkmk() {
        return this._fieldHandler.getFieldOffset(21);
    }

    public void setFcSttbfbkmk(int offset) {
        this._fieldHandler.setFieldOffset(21, offset);
    }

    public int getLcbSttbfbkmk() {
        return this._fieldHandler.getFieldSize(21);
    }

    public void setLcbSttbfbkmk(int length) {
        this._fieldHandler.setFieldSize(21, length);
    }

    public int getFcPlcfbkf() {
        return this._fieldHandler.getFieldOffset(22);
    }

    public void setFcPlcfbkf(int offset) {
        this._fieldHandler.setFieldOffset(22, offset);
    }

    public int getLcbPlcfbkf() {
        return this._fieldHandler.getFieldSize(22);
    }

    public void setLcbPlcfbkf(int length) {
        this._fieldHandler.setFieldSize(22, length);
    }

    public int getFcPlcfbkl() {
        return this._fieldHandler.getFieldOffset(23);
    }

    public void setFcPlcfbkl(int offset) {
        this._fieldHandler.setFieldOffset(23, offset);
    }

    public int getLcbPlcfbkl() {
        return this._fieldHandler.getFieldSize(23);
    }

    public void setLcbPlcfbkl(int length) {
        this._fieldHandler.setFieldSize(23, length);
    }

    public void setFcPlfLfo(int fcPlfLfo) {
        this._fieldHandler.setFieldOffset(74, fcPlfLfo);
    }

    public void setLcbPlfLfo(int lcbPlfLfo) {
        this._fieldHandler.setFieldSize(74, lcbPlfLfo);
    }

    public int getFcSttbfffn() {
        return this._fieldHandler.getFieldOffset(15);
    }

    public int getLcbSttbfffn() {
        return this._fieldHandler.getFieldSize(15);
    }

    public void setFcSttbfffn(int fcSttbFffn) {
        this._fieldHandler.setFieldOffset(15, fcSttbFffn);
    }

    public void setLcbSttbfffn(int lcbSttbFffn) {
        this._fieldHandler.setFieldSize(15, lcbSttbFffn);
    }

    public int getFcSttbfRMark() {
        return this._fieldHandler.getFieldOffset(51);
    }

    public int getLcbSttbfRMark() {
        return this._fieldHandler.getFieldSize(51);
    }

    public void setFcSttbfRMark(int fcSttbfRMark) {
        this._fieldHandler.setFieldOffset(51, fcSttbfRMark);
    }

    public void setLcbSttbfRMark(int lcbSttbfRMark) {
        this._fieldHandler.setFieldSize(51, lcbSttbfRMark);
    }

    public int getPlcfHddOffset() {
        return this._fieldHandler.getFieldOffset(11);
    }

    public int getPlcfHddSize() {
        return this._fieldHandler.getFieldSize(11);
    }

    public void setPlcfHddOffset(int fcPlcfHdd) {
        this._fieldHandler.setFieldOffset(11, fcPlcfHdd);
    }

    public void setPlcfHddSize(int lcbPlcfHdd) {
        this._fieldHandler.setFieldSize(11, lcbPlcfHdd);
    }

    public int getFcSttbSavedBy() {
        return this._fieldHandler.getFieldOffset(71);
    }

    public int getLcbSttbSavedBy() {
        return this._fieldHandler.getFieldSize(71);
    }

    public void setFcSttbSavedBy(int fcSttbSavedBy) {
        this._fieldHandler.setFieldOffset(71, fcSttbSavedBy);
    }

    public void setLcbSttbSavedBy(int fcSttbSavedBy) {
        this._fieldHandler.setFieldSize(71, fcSttbSavedBy);
    }

    public int getModifiedLow() {
        return this._fieldHandler.getFieldOffset(74);
    }

    public int getModifiedHigh() {
        return this._fieldHandler.getFieldSize(74);
    }

    public void setModifiedLow(int modifiedLow) {
        this._fieldHandler.setFieldOffset(74, modifiedLow);
    }

    public void setModifiedHigh(int modifiedHigh) {
        this._fieldHandler.setFieldSize(74, modifiedHigh);
    }

    public int getCbMac() {
        return this._fibRgLw.getCbMac();
    }

    public void setCbMac(int cbMac) {
        this._fibRgLw.setCbMac(cbMac);
    }

    public int getSubdocumentTextStreamLength(SubdocumentType type) {
        if (type == null) {
            throw new IllegalArgumentException("argument 'type' is null");
        }
        return this._fibRgLw.getSubdocumentTextStreamLength(type);
    }

    public void setSubdocumentTextStreamLength(SubdocumentType type, int length) {
        if (type == null) {
            throw new IllegalArgumentException("argument 'type' is null");
        }
        if (length < 0) {
            throw new IllegalArgumentException("Subdocument length can't be less than 0 (passed value is " + length + "). If there is no subdocument length must be set to zero.");
        }
        this._fibRgLw.setSubdocumentTextStreamLength(type, length);
    }

    public void clearOffsetsSizes() {
        this._fieldHandler.clearFields();
    }

    public int getFieldsPlcfOffset(FieldsDocumentPart part) {
        return this._fieldHandler.getFieldOffset(part.getFibFieldsField());
    }

    public int getFieldsPlcfLength(FieldsDocumentPart part) {
        return this._fieldHandler.getFieldSize(part.getFibFieldsField());
    }

    public void setFieldsPlcfOffset(FieldsDocumentPart part, int offset) {
        this._fieldHandler.setFieldOffset(part.getFibFieldsField(), offset);
    }

    public void setFieldsPlcfLength(FieldsDocumentPart part, int length) {
        this._fieldHandler.setFieldSize(part.getFibFieldsField(), length);
    }

    @Deprecated
    public int getFcPlcffldAtn() {
        return this._fieldHandler.getFieldOffset(19);
    }

    @Deprecated
    public int getLcbPlcffldAtn() {
        return this._fieldHandler.getFieldSize(19);
    }

    @Deprecated
    public void setFcPlcffldAtn(int offset) {
        this._fieldHandler.setFieldOffset(19, offset);
    }

    @Deprecated
    public void setLcbPlcffldAtn(int size) {
        this._fieldHandler.setFieldSize(19, size);
    }

    @Deprecated
    public int getFcPlcffldEdn() {
        return this._fieldHandler.getFieldOffset(48);
    }

    @Deprecated
    public int getLcbPlcffldEdn() {
        return this._fieldHandler.getFieldSize(48);
    }

    @Deprecated
    public void setFcPlcffldEdn(int offset) {
        this._fieldHandler.setFieldOffset(48, offset);
    }

    @Deprecated
    public void setLcbPlcffldEdn(int size) {
        this._fieldHandler.setFieldSize(48, size);
    }

    @Deprecated
    public int getFcPlcffldFtn() {
        return this._fieldHandler.getFieldOffset(18);
    }

    @Deprecated
    public int getLcbPlcffldFtn() {
        return this._fieldHandler.getFieldSize(18);
    }

    @Deprecated
    public void setFcPlcffldFtn(int offset) {
        this._fieldHandler.setFieldOffset(18, offset);
    }

    @Deprecated
    public void setLcbPlcffldFtn(int size) {
        this._fieldHandler.setFieldSize(18, size);
    }

    @Deprecated
    public int getFcPlcffldHdr() {
        return this._fieldHandler.getFieldOffset(17);
    }

    @Deprecated
    public int getLcbPlcffldHdr() {
        return this._fieldHandler.getFieldSize(17);
    }

    @Deprecated
    public void setFcPlcffldHdr(int offset) {
        this._fieldHandler.setFieldOffset(17, offset);
    }

    @Deprecated
    public void setLcbPlcffldHdr(int size) {
        this._fieldHandler.setFieldSize(17, size);
    }

    @Deprecated
    public int getFcPlcffldHdrtxbx() {
        return this._fieldHandler.getFieldOffset(59);
    }

    @Deprecated
    public int getLcbPlcffldHdrtxbx() {
        return this._fieldHandler.getFieldSize(59);
    }

    @Deprecated
    public void setFcPlcffldHdrtxbx(int offset) {
        this._fieldHandler.setFieldOffset(59, offset);
    }

    @Deprecated
    public void setLcbPlcffldHdrtxbx(int size) {
        this._fieldHandler.setFieldSize(59, size);
    }

    @Deprecated
    public int getFcPlcffldMom() {
        return this._fieldHandler.getFieldOffset(16);
    }

    @Deprecated
    public int getLcbPlcffldMom() {
        return this._fieldHandler.getFieldSize(16);
    }

    @Deprecated
    public void setFcPlcffldMom(int offset) {
        this._fieldHandler.setFieldOffset(16, offset);
    }

    @Deprecated
    public void setLcbPlcffldMom(int size) {
        this._fieldHandler.setFieldSize(16, size);
    }

    @Deprecated
    public int getFcPlcffldTxbx() {
        return this._fieldHandler.getFieldOffset(57);
    }

    @Deprecated
    public int getLcbPlcffldTxbx() {
        return this._fieldHandler.getFieldSize(57);
    }

    @Deprecated
    public void setFcPlcffldTxbx(int offset) {
        this._fieldHandler.setFieldOffset(57, offset);
    }

    @Deprecated
    public void setLcbPlcffldTxbx(int size) {
        this._fieldHandler.setFieldSize(57, size);
    }

    public int getFSPAPlcfOffset(FSPADocumentPart part) {
        return this._fieldHandler.getFieldOffset(part.getFibFieldsField());
    }

    public int getFSPAPlcfLength(FSPADocumentPart part) {
        return this._fieldHandler.getFieldSize(part.getFibFieldsField());
    }

    public void setFSPAPlcfOffset(FSPADocumentPart part, int offset) {
        this._fieldHandler.setFieldOffset(part.getFibFieldsField(), offset);
    }

    public void setFSPAPlcfLength(FSPADocumentPart part, int length) {
        this._fieldHandler.setFieldSize(part.getFibFieldsField(), length);
    }

    public int getFcDggInfo() {
        return this._fieldHandler.getFieldOffset(50);
    }

    public int getLcbDggInfo() {
        return this._fieldHandler.getFieldSize(50);
    }

    public int getNotesDescriptorsOffset(NoteType noteType) {
        return this._fieldHandler.getFieldOffset(noteType.getFibDescriptorsFieldIndex());
    }

    public void setNotesDescriptorsOffset(NoteType noteType, int offset) {
        this._fieldHandler.setFieldOffset(noteType.getFibDescriptorsFieldIndex(), offset);
    }

    public int getNotesDescriptorsSize(NoteType noteType) {
        return this._fieldHandler.getFieldSize(noteType.getFibDescriptorsFieldIndex());
    }

    public void setNotesDescriptorsSize(NoteType noteType, int offset) {
        this._fieldHandler.setFieldSize(noteType.getFibDescriptorsFieldIndex(), offset);
    }

    public int getNotesTextPositionsOffset(NoteType noteType) {
        return this._fieldHandler.getFieldOffset(noteType.getFibTextPositionsFieldIndex());
    }

    public void setNotesTextPositionsOffset(NoteType noteType, int offset) {
        this._fieldHandler.setFieldOffset(noteType.getFibTextPositionsFieldIndex(), offset);
    }

    public int getNotesTextPositionsSize(NoteType noteType) {
        return this._fieldHandler.getFieldSize(noteType.getFibTextPositionsFieldIndex());
    }

    public void setNotesTextPositionsSize(NoteType noteType, int offset) {
        this._fieldHandler.setFieldSize(noteType.getFibTextPositionsFieldIndex(), offset);
    }

    public void writeTo(byte[] mainStream, ByteArrayOutputStream tableStream) throws IOException {
        this._cbRgFcLcb = this._fieldHandler.getFieldsCount();
        this._fibBase.serialize(mainStream, 0);
        int offset = FibBaseAbstractType.getSize();
        LittleEndian.putUShort(mainStream, offset, this._csw);
        this._fibRgW.serialize(mainStream, offset += 2);
        LittleEndian.putUShort(mainStream, offset += FibRgW97AbstractType.getSize(), this._cslw);
        ((FibRgLw97)this._fibRgLw).serialize(mainStream, offset += 2);
        LittleEndian.putUShort(mainStream, offset += FibRgLw97AbstractType.getSize(), this._cbRgFcLcb);
        this._fieldHandler.writeTo(mainStream, offset += 2, tableStream);
        LittleEndian.putUShort(mainStream, offset += this._cbRgFcLcb * 4 * 2, this._cswNew);
        offset += 2;
        if (this._cswNew != 0) {
            LittleEndian.putUShort(mainStream, offset, this._nFibNew);
            System.arraycopy(this._fibRgCswNew, 0, mainStream, offset += 2, this._fibRgCswNew.length);
        }
    }

    public int getSize() {
        return FibBaseAbstractType.getSize() + 2 + FibRgW97AbstractType.getSize() + 2 + FibRgLw97AbstractType.getSize() + 2 + this._fieldHandler.sizeInBytes();
    }

    public FibBase getFibBase() {
        return this._fibBase;
    }
}

