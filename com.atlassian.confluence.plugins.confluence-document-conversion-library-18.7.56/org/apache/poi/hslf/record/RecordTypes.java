/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.hslf.record.AnimationInfo;
import org.apache.poi.hslf.record.AnimationInfoAtom;
import org.apache.poi.hslf.record.BinaryTagDataBlob;
import org.apache.poi.hslf.record.CString;
import org.apache.poi.hslf.record.ColorSchemeAtom;
import org.apache.poi.hslf.record.Comment2000;
import org.apache.poi.hslf.record.Comment2000Atom;
import org.apache.poi.hslf.record.DateTimeMCAtom;
import org.apache.poi.hslf.record.DocInfoListContainer;
import org.apache.poi.hslf.record.Document;
import org.apache.poi.hslf.record.DocumentAtom;
import org.apache.poi.hslf.record.DocumentEncryptionAtom;
import org.apache.poi.hslf.record.DummyPositionSensitiveRecordWithChildren;
import org.apache.poi.hslf.record.Environment;
import org.apache.poi.hslf.record.ExAviMovie;
import org.apache.poi.hslf.record.ExControl;
import org.apache.poi.hslf.record.ExControlAtom;
import org.apache.poi.hslf.record.ExEmbed;
import org.apache.poi.hslf.record.ExEmbedAtom;
import org.apache.poi.hslf.record.ExHyperlink;
import org.apache.poi.hslf.record.ExHyperlinkAtom;
import org.apache.poi.hslf.record.ExMCIMovie;
import org.apache.poi.hslf.record.ExMediaAtom;
import org.apache.poi.hslf.record.ExObjList;
import org.apache.poi.hslf.record.ExObjListAtom;
import org.apache.poi.hslf.record.ExObjRefAtom;
import org.apache.poi.hslf.record.ExOleObjAtom;
import org.apache.poi.hslf.record.ExOleObjStg;
import org.apache.poi.hslf.record.ExVideoContainer;
import org.apache.poi.hslf.record.FontCollection;
import org.apache.poi.hslf.record.FontEmbeddedData;
import org.apache.poi.hslf.record.FontEntityAtom;
import org.apache.poi.hslf.record.HeadersFootersAtom;
import org.apache.poi.hslf.record.HeadersFootersContainer;
import org.apache.poi.hslf.record.InteractiveInfo;
import org.apache.poi.hslf.record.InteractiveInfoAtom;
import org.apache.poi.hslf.record.MainMaster;
import org.apache.poi.hslf.record.MasterTextPropAtom;
import org.apache.poi.hslf.record.Notes;
import org.apache.poi.hslf.record.NotesAtom;
import org.apache.poi.hslf.record.OEPlaceholderAtom;
import org.apache.poi.hslf.record.OutlineTextRefAtom;
import org.apache.poi.hslf.record.PPDrawing;
import org.apache.poi.hslf.record.PPDrawingGroup;
import org.apache.poi.hslf.record.PersistPtrHolder;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RoundTripHFPlaceholder12;
import org.apache.poi.hslf.record.SSSlideInfoAtom;
import org.apache.poi.hslf.record.Slide;
import org.apache.poi.hslf.record.SlideAtom;
import org.apache.poi.hslf.record.SlideListWithText;
import org.apache.poi.hslf.record.SlidePersistAtom;
import org.apache.poi.hslf.record.Sound;
import org.apache.poi.hslf.record.SoundCollection;
import org.apache.poi.hslf.record.SoundData;
import org.apache.poi.hslf.record.StyleTextProp9Atom;
import org.apache.poi.hslf.record.StyleTextPropAtom;
import org.apache.poi.hslf.record.TextBytesAtom;
import org.apache.poi.hslf.record.TextCharsAtom;
import org.apache.poi.hslf.record.TextHeaderAtom;
import org.apache.poi.hslf.record.TextRulerAtom;
import org.apache.poi.hslf.record.TextSpecInfoAtom;
import org.apache.poi.hslf.record.TxInteractiveInfoAtom;
import org.apache.poi.hslf.record.TxMasterStyleAtom;
import org.apache.poi.hslf.record.UnknownRecordPlaceholder;
import org.apache.poi.hslf.record.UserEditAtom;
import org.apache.poi.hslf.record.VBAInfoAtom;
import org.apache.poi.hslf.record.VBAInfoContainer;

public enum RecordTypes {
    Unknown(0, null),
    UnknownRecordPlaceholder(-1, UnknownRecordPlaceholder::new),
    Document(1000, Document::new),
    DocumentAtom(1001, DocumentAtom::new),
    EndDocument(1002, null),
    Slide(1006, Slide::new),
    SlideAtom(1007, SlideAtom::new),
    Notes(1008, Notes::new),
    NotesAtom(1009, NotesAtom::new),
    Environment(1010, Environment::new),
    SlidePersistAtom(1011, SlidePersistAtom::new),
    SSlideLayoutAtom(1015, null),
    MainMaster(1016, MainMaster::new),
    SSSlideInfoAtom(1017, SSSlideInfoAtom::new),
    SlideViewInfo(1018, null),
    GuideAtom(1019, null),
    ViewInfo(1020, null),
    ViewInfoAtom(1021, null),
    SlideViewInfoAtom(1022, null),
    VBAInfo(1023, VBAInfoContainer::new),
    VBAInfoAtom(1024, VBAInfoAtom::new),
    SSDocInfoAtom(1025, null),
    Summary(1026, null),
    DocRoutingSlip(1030, null),
    OutlineViewInfo(1031, null),
    SorterViewInfo(1032, null),
    ExObjList(1033, ExObjList::new),
    ExObjListAtom(1034, ExObjListAtom::new),
    PPDrawingGroup(1035, PPDrawingGroup::new),
    PPDrawing(1036, PPDrawing::new),
    NamedShows(1040, null),
    NamedShow(1041, null),
    NamedShowSlides(1042, null),
    SheetProperties(1044, null),
    OriginalMainMasterId(1052, null),
    CompositeMasterId(1052, null),
    RoundTripContentMasterInfo12(1054, null),
    RoundTripShapeId12(1055, null),
    RoundTripHFPlaceholder12(1056, RoundTripHFPlaceholder12::new),
    RoundTripContentMasterId(1058, null),
    RoundTripOArtTextStyles12(1059, null),
    RoundTripShapeCheckSumForCustomLayouts12(1062, null),
    RoundTripNotesMasterTextStyles12(1063, null),
    RoundTripCustomTableStyles12(1064, null),
    List(2000, DocInfoListContainer::new),
    FontCollection(2005, FontCollection::new),
    BookmarkCollection(2019, null),
    SoundCollection(2020, SoundCollection::new),
    SoundCollAtom(2021, null),
    Sound(2022, Sound::new),
    SoundData(2023, SoundData::new),
    BookmarkSeedAtom(2025, null),
    ColorSchemeAtom(2032, ColorSchemeAtom::new),
    ExObjRefAtom(3009, ExObjRefAtom::new),
    OEPlaceholderAtom(3011, OEPlaceholderAtom::new),
    GPopublicintAtom(3024, null),
    GRatioAtom(3031, null),
    OutlineTextRefAtom(3998, OutlineTextRefAtom::new),
    TextHeaderAtom(3999, TextHeaderAtom::new),
    TextCharsAtom(4000, TextCharsAtom::new),
    StyleTextPropAtom(4001, StyleTextPropAtom::new),
    MasterTextPropAtom(4002, MasterTextPropAtom::new),
    TxMasterStyleAtom(4003, TxMasterStyleAtom::new),
    TxCFStyleAtom(4004, null),
    TxPFStyleAtom(4005, null),
    TextRulerAtom(4006, TextRulerAtom::new),
    TextBookmarkAtom(4007, null),
    TextBytesAtom(4008, TextBytesAtom::new),
    TxSIStyleAtom(4009, null),
    TextSpecInfoAtom(4010, TextSpecInfoAtom::new),
    DefaultRulerAtom(4011, null),
    StyleTextProp9Atom(4012, StyleTextProp9Atom::new),
    FontEntityAtom(4023, FontEntityAtom::new),
    FontEmbeddedData(4024, FontEmbeddedData::new),
    CString(4026, CString::new),
    MetaFile(4033, null),
    ExOleObjAtom(4035, ExOleObjAtom::new),
    SrKinsoku(4040, null),
    HandOut(4041, DummyPositionSensitiveRecordWithChildren::new),
    ExEmbed(4044, ExEmbed::new),
    ExEmbedAtom(4045, ExEmbedAtom::new),
    ExLink(4046, null),
    BookmarkEntityAtom(4048, null),
    ExLinkAtom(4049, null),
    SrKinsokuAtom(4050, null),
    ExHyperlinkAtom(4051, ExHyperlinkAtom::new),
    ExHyperlink(4055, ExHyperlink::new),
    SlideNumberMCAtom(4056, null),
    HeadersFooters(4057, HeadersFootersContainer::new),
    HeadersFootersAtom(4058, HeadersFootersAtom::new),
    TxInteractiveInfoAtom(4063, TxInteractiveInfoAtom::new),
    CharFormatAtom(4066, null),
    ParaFormatAtom(4067, null),
    RecolorInfoAtom(4071, null),
    ExQuickTimeMovie(4074, null),
    ExQuickTimeMovieData(4075, null),
    ExControl(4078, ExControl::new),
    SlideListWithText(4080, SlideListWithText::new),
    InteractiveInfo(4082, InteractiveInfo::new),
    InteractiveInfoAtom(4083, InteractiveInfoAtom::new),
    UserEditAtom(4085, UserEditAtom::new),
    CurrentUserAtom(4086, null),
    DateTimeMCAtom(4087, DateTimeMCAtom::new),
    GenericDateMCAtom(4088, null),
    FooterMCAtom(4090, null),
    ExControlAtom(4091, ExControlAtom::new),
    ExMediaAtom(4100, ExMediaAtom::new),
    ExVideoContainer(4101, ExVideoContainer::new),
    ExAviMovie(4102, ExAviMovie::new),
    ExMCIMovie(4103, ExMCIMovie::new),
    ExMIDIAudio(4109, null),
    ExCDAudio(4110, null),
    ExWAVAudioEmbedded(4111, null),
    ExWAVAudioLink(4112, null),
    ExOleObjStg(4113, ExOleObjStg::new),
    ExCDAudioAtom(4114, null),
    ExWAVAudioEmbeddedAtom(4115, null),
    AnimationInfo(4116, AnimationInfo::new),
    AnimationInfoAtom(4081, AnimationInfoAtom::new),
    RTFDateTimeMCAtom(4117, null),
    ProgTags(5000, DummyPositionSensitiveRecordWithChildren::new),
    ProgStringTag(5001, null),
    ProgBinaryTag(5002, DummyPositionSensitiveRecordWithChildren::new),
    BinaryTagData(5003, BinaryTagDataBlob::new),
    PrpublicintOptions(6000, null),
    PersistPtrFullBlock(6001, PersistPtrHolder::new),
    PersistPtrIncrementalBlock(6002, PersistPtrHolder::new),
    GScalingAtom(10001, null),
    GRColorAtom(10002, null),
    Comment2000(12000, Comment2000::new),
    Comment2000Atom(12001, Comment2000Atom::new),
    Comment2000Summary(12004, null),
    Comment2000SummaryAtom(12005, null),
    DocumentEncryptionAtom(12052, DocumentEncryptionAtom::new);

    private static final Map<Short, RecordTypes> LOOKUP;
    public final short typeID;
    public final RecordConstructor<?> recordConstructor;

    private RecordTypes(int typeID, RecordConstructor<?> recordConstructor) {
        this.typeID = (short)typeID;
        this.recordConstructor = recordConstructor;
    }

    public static RecordTypes forTypeID(int typeID) {
        RecordTypes rt = LOOKUP.get((short)typeID);
        return rt != null ? rt : UnknownRecordPlaceholder;
    }

    static {
        LOOKUP = new HashMap<Short, RecordTypes>();
        for (RecordTypes s : RecordTypes.values()) {
            LOOKUP.put(s.typeID, s);
        }
    }

    @FunctionalInterface
    public static interface RecordConstructor<T extends Record> {
        public T apply(byte[] var1, int var2, int var3);
    }
}

