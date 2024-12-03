/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.model.HeadersFooters;
import org.apache.poi.hslf.record.CString;
import org.apache.poi.hslf.record.DateTimeMCAtom;
import org.apache.poi.hslf.record.EscherTextboxWrapper;
import org.apache.poi.hslf.record.HSLFEscherClientDataRecord;
import org.apache.poi.hslf.record.HeadersFootersAtom;
import org.apache.poi.hslf.record.OEPlaceholderAtom;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.RoundTripHFPlaceholder12;
import org.apache.poi.hslf.record.TextSpecInfoAtom;
import org.apache.poi.hslf.record.TextSpecInfoRun;
import org.apache.poi.hslf.usermodel.HSLFNotes;
import org.apache.poi.hslf.usermodel.HSLFPlaceholderDetails;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFSimpleShape;
import org.apache.poi.hslf.usermodel.HSLFSlideMaster;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.hslf.util.LocaleDateFormat;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.PlaceholderDetails;
import org.apache.poi.util.LocaleID;
import org.apache.poi.util.LocaleUtil;

public class HSLFShapePlaceholderDetails
extends HSLFPlaceholderDetails {
    private final PlaceholderContainer source;
    final HSLFSimpleShape shape;
    private OEPlaceholderAtom oePlaceholderAtom;
    private RoundTripHFPlaceholder12 roundTripHFPlaceholder12;
    private DateTimeMCAtom localDateTime;

    HSLFShapePlaceholderDetails(HSLFSimpleShape shape) {
        super(shape.getSheet(), null);
        this.shape = shape;
        HSLFSheet sheet = shape.getSheet();
        this.source = sheet instanceof HSLFSlideMaster ? PlaceholderContainer.master : (sheet instanceof HSLFNotes ? PlaceholderContainer.notes : (sheet instanceof MasterSheet ? PlaceholderContainer.notesMaster : PlaceholderContainer.slide));
    }

    @Override
    public Placeholder getPlaceholder() {
        int phId;
        this.updatePlaceholderAtom(false);
        if (this.oePlaceholderAtom != null) {
            phId = this.oePlaceholderAtom.getPlaceholderId();
        } else if (this.roundTripHFPlaceholder12 != null) {
            phId = this.roundTripHFPlaceholder12.getPlaceholderId();
        } else {
            if (this.localDateTime != null) {
                return Placeholder.DATETIME;
            }
            return null;
        }
        switch (this.source) {
            case slide: {
                return Placeholder.lookupNativeSlide(phId);
            }
            default: {
                return Placeholder.lookupNativeSlideMaster(phId);
            }
            case notes: {
                return Placeholder.lookupNativeNotes(phId);
            }
            case notesMaster: 
        }
        return Placeholder.lookupNativeNotesMaster(phId);
    }

    @Override
    public void setPlaceholder(Placeholder placeholder) {
        EscherSpRecord spRecord = (EscherSpRecord)this.shape.getEscherChild(EscherSpRecord.RECORD_ID);
        int flags = spRecord.getFlags();
        flags = placeholder == null ? (flags ^= 0x20) : (flags |= 0x220);
        spRecord.setFlags(flags);
        this.shape.setEscherProperty(EscherPropertyTypes.PROTECTION__LOCKAGAINSTGROUPING, placeholder == null ? -1 : 262144);
        if (placeholder == null) {
            this.removePlaceholder();
            return;
        }
        this.updatePlaceholderAtom(true);
        byte phId = this.getPlaceholderId(placeholder);
        this.oePlaceholderAtom.setPlaceholderId(phId);
        this.roundTripHFPlaceholder12.setPlaceholderId(phId);
    }

    @Override
    public PlaceholderDetails.PlaceholderSize getSize() {
        Placeholder ph = this.getPlaceholder();
        if (ph == null) {
            return null;
        }
        int size = this.oePlaceholderAtom != null ? this.oePlaceholderAtom.getPlaceholderSize() : 1;
        switch (size) {
            case 0: {
                return PlaceholderDetails.PlaceholderSize.full;
            }
            default: {
                return PlaceholderDetails.PlaceholderSize.half;
            }
            case 2: 
        }
        return PlaceholderDetails.PlaceholderSize.quarter;
    }

    @Override
    public void setSize(PlaceholderDetails.PlaceholderSize size) {
        byte ph_size;
        Placeholder ph = this.getPlaceholder();
        if (ph == null || size == null) {
            return;
        }
        this.updatePlaceholderAtom(true);
        switch (size) {
            case full: {
                ph_size = 0;
                break;
            }
            default: {
                ph_size = 1;
                break;
            }
            case quarter: {
                ph_size = 2;
            }
        }
        this.oePlaceholderAtom.setPlaceholderSize(ph_size);
    }

    private byte getPlaceholderId(Placeholder placeholder) {
        byte phId;
        switch (this.source) {
            default: {
                phId = (byte)placeholder.nativeSlideId;
                break;
            }
            case master: {
                phId = (byte)placeholder.nativeSlideMasterId;
                break;
            }
            case notes: {
                phId = (byte)placeholder.nativeNotesId;
                break;
            }
            case notesMaster: {
                phId = (byte)placeholder.nativeNotesMasterId;
            }
        }
        if (phId == -2) {
            throw new HSLFException("Placeholder " + placeholder.name() + " not supported for this sheet type (" + this.shape.getSheet().getClass() + ")");
        }
        return phId;
    }

    private void removePlaceholder() {
        HSLFEscherClientDataRecord clientData = this.shape.getClientData(false);
        if (clientData != null) {
            clientData.removeChild(OEPlaceholderAtom.class);
            clientData.removeChild(RoundTripHFPlaceholder12.class);
            if (clientData.getChildRecords().isEmpty()) {
                this.shape.getSpContainer().removeChildRecord(clientData);
            }
        }
        this.oePlaceholderAtom = null;
        this.roundTripHFPlaceholder12 = null;
    }

    private void updatePlaceholderAtom(boolean create) {
        HSLFEscherClientDataRecord clientData;
        EscherTextboxWrapper txtBox;
        this.localDateTime = null;
        if (this.shape instanceof HSLFTextBox && (txtBox = ((HSLFTextBox)this.shape).getEscherTextboxWrapper()) != null) {
            this.localDateTime = (DateTimeMCAtom)txtBox.findFirstOfType(RecordTypes.DateTimeMCAtom.typeID);
        }
        if ((clientData = this.shape.getClientData(create)) == null) {
            this.oePlaceholderAtom = null;
            this.roundTripHFPlaceholder12 = null;
            if (!create) {
                return;
            }
            throw new HSLFException("Placeholder aren't allowed for shape type: " + this.shape.getClass().getSimpleName());
        }
        for (Record record : clientData.getHSLFChildRecords()) {
            if (record instanceof OEPlaceholderAtom) {
                this.oePlaceholderAtom = (OEPlaceholderAtom)record;
                continue;
            }
            if (!(record instanceof RoundTripHFPlaceholder12)) continue;
            this.roundTripHFPlaceholder12 = (RoundTripHFPlaceholder12)record;
        }
        if (!create) {
            return;
        }
        if (this.oePlaceholderAtom == null) {
            this.oePlaceholderAtom = new OEPlaceholderAtom();
            this.oePlaceholderAtom.setPlaceholderSize((byte)0);
            this.oePlaceholderAtom.setPlacementId(-1);
            clientData.addChild(this.oePlaceholderAtom);
        }
        if (this.roundTripHFPlaceholder12 == null) {
            this.roundTripHFPlaceholder12 = new RoundTripHFPlaceholder12();
            clientData.addChild(this.roundTripHFPlaceholder12);
        }
    }

    @Override
    public String getUserDate() {
        HeadersFooters hf = this.shape.getSheet().getHeadersFooters();
        CString uda = hf.getUserDateAtom();
        return hf.isUserDateVisible() && uda != null ? uda.getText() : null;
    }

    @Override
    public DateTimeFormatter getDateFormat() {
        int formatId;
        if (this.localDateTime != null) {
            formatId = this.localDateTime.getIndex();
        } else {
            HeadersFootersAtom hfAtom = this.shape.getSheet().getHeadersFooters().getContainer().getHeadersFootersAtom();
            formatId = hfAtom.getFormatId();
        }
        LocaleID def = LocaleID.lookupByLanguageTag(LocaleUtil.getUserLocale().toLanguageTag());
        LocaleID lcid = Stream.of(((HSLFTextShape)this.shape).getTextParagraphs().get(0).getRecords()).filter(r -> r instanceof TextSpecInfoAtom).findFirst().map(r -> ((TextSpecInfoAtom)r).getTextSpecInfoRuns()[0]).map(TextSpecInfoRun::getLangId).flatMap(lid -> Optional.ofNullable(LocaleID.lookupByLcid(lid.shortValue()))).orElse(def != null ? def : LocaleID.EN_US);
        return LocaleDateFormat.map(lcid, formatId, LocaleDateFormat.MapFormatId.PPT);
    }

    private static enum PlaceholderContainer {
        slide,
        master,
        notes,
        notesMaster;

    }
}

