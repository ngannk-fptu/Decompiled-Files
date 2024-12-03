/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherBlipRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.FSPATable;
import org.apache.poi.hwpf.model.OfficeArtContent;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class PicturesTable {
    private static final Logger LOG = LogManager.getLogger(PicturesTable.class);
    static final int TYPE_IMAGE = 8;
    static final int TYPE_IMAGE_WORD2000 = 0;
    static final int TYPE_IMAGE_PASTED_FROM_CLIPBOARD = 10;
    static final int TYPE_IMAGE_PASTED_FROM_CLIPBOARD_WORD2000 = 2;
    static final int TYPE_HORIZONTAL_LINE = 14;
    static final int BLOCK_TYPE_OFFSET = 14;
    static final int MM_MODE_TYPE_OFFSET = 6;
    private final HWPFDocument _document;
    private final byte[] _dataStream;
    private final byte[] _mainStream;
    @Deprecated
    private FSPATable _fspa;
    @Deprecated
    private OfficeArtContent _dgg;

    @Deprecated
    public PicturesTable(HWPFDocument _document, byte[] _dataStream, byte[] _mainStream, FSPATable fspa, OfficeArtContent dgg) {
        this._document = _document;
        this._dataStream = _dataStream;
        this._mainStream = _mainStream;
        this._fspa = fspa;
        this._dgg = dgg;
    }

    public PicturesTable(HWPFDocument _document, byte[] _dataStream, byte[] _mainStream) {
        this._document = _document;
        this._dataStream = _dataStream;
        this._mainStream = _mainStream;
    }

    public boolean hasPicture(CharacterRun run) {
        if (run == null) {
            return false;
        }
        if (run.isSpecialCharacter() && !run.isObj() && !run.isOle2() && !run.isData() && ("\u0001".equals(run.text()) || "\u0001\u0015".equals(run.text()))) {
            return this.isBlockContainsImage(run.getPicOffset());
        }
        return false;
    }

    public boolean hasEscherPicture(CharacterRun run) {
        return run.isSpecialCharacter() && !run.isObj() && !run.isOle2() && !run.isData() && run.text().startsWith("\b");
    }

    public boolean hasHorizontalLine(CharacterRun run) {
        if (run.isSpecialCharacter() && "\u0001".equals(run.text())) {
            return this.isBlockContainsHorizontalLine(run.getPicOffset());
        }
        return false;
    }

    private boolean isPictureRecognized(short blockType, short mappingModeOfMETAFILEPICT) {
        return blockType == 8 || blockType == 10 || blockType == 0 && mappingModeOfMETAFILEPICT == 100 || blockType == 2 && mappingModeOfMETAFILEPICT == 100;
    }

    private static short getBlockType(byte[] dataStream, int pictOffset) {
        return LittleEndian.getShort(dataStream, pictOffset + 14);
    }

    private static short getMmMode(byte[] dataStream, int pictOffset) {
        return LittleEndian.getShort(dataStream, pictOffset + 6);
    }

    public Picture extractPicture(CharacterRun run, boolean fillBytes) {
        if (this.hasPicture(run)) {
            return new Picture(run.getPicOffset(), this._dataStream, fillBytes);
        }
        return null;
    }

    private void searchForPictures(List<EscherRecord> escherRecords, List<Picture> pictures) {
        for (EscherRecord escherRecord : escherRecords) {
            if (!(escherRecord instanceof EscherBSERecord)) continue;
            EscherBSERecord bse = (EscherBSERecord)escherRecord;
            EscherBlipRecord blip = bse.getBlipRecord();
            if (blip != null) {
                pictures.add(new Picture(blip));
                continue;
            }
            if (bse.getOffset() <= 0) continue;
            try {
                DefaultEscherRecordFactory recordFactory = new DefaultEscherRecordFactory();
                EscherRecord record = recordFactory.createRecord(this._mainStream, bse.getOffset());
                if (!(record instanceof EscherBlipRecord)) continue;
                record.fillFields(this._mainStream, bse.getOffset(), recordFactory);
                blip = (EscherBlipRecord)record;
                pictures.add(new Picture(blip));
            }
            catch (Exception exc) {
                LOG.atWarn().withThrowable(exc).log("Unable to load picture from BLIP record at offset #{}", (Object)Unbox.box(bse.getOffset()));
            }
        }
    }

    public List<Picture> getAllPictures() {
        ArrayList<Picture> pictures = new ArrayList<Picture>();
        Range range = this._document.getOverallRange();
        for (int i = 0; i < range.numCharacterRuns(); ++i) {
            Picture picture;
            CharacterRun run = range.getCharacterRun(i);
            if (run == null || (picture = this.extractPicture(run, false)) == null) continue;
            pictures.add(picture);
        }
        EscherContainerRecord bStore = this._dgg.getBStoreContainer();
        if (bStore != null) {
            this.searchForPictures(bStore.getChildRecords(), pictures);
        }
        return pictures;
    }

    private boolean isBlockContainsImage(int i) {
        return this.isPictureRecognized(PicturesTable.getBlockType(this._dataStream, i), PicturesTable.getMmMode(this._dataStream, i));
    }

    private boolean isBlockContainsHorizontalLine(int i) {
        return PicturesTable.getBlockType(this._dataStream, i) == 14 && PicturesTable.getMmMode(this._dataStream, i) == 100;
    }
}

