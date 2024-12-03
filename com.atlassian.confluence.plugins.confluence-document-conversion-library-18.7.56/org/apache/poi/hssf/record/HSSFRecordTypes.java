/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.poi.hssf.record.ArrayRecord;
import org.apache.poi.hssf.record.AutoFilterInfoRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BackupRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BookBoolRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BottomMarginRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.CFHeader12Record;
import org.apache.poi.hssf.record.CFHeaderRecord;
import org.apache.poi.hssf.record.CFRule12Record;
import org.apache.poi.hssf.record.CFRuleRecord;
import org.apache.poi.hssf.record.CRNCountRecord;
import org.apache.poi.hssf.record.CRNRecord;
import org.apache.poi.hssf.record.CalcCountRecord;
import org.apache.poi.hssf.record.CalcModeRecord;
import org.apache.poi.hssf.record.CodepageRecord;
import org.apache.poi.hssf.record.ColumnInfoRecord;
import org.apache.poi.hssf.record.ContinueRecord;
import org.apache.poi.hssf.record.CountryRecord;
import org.apache.poi.hssf.record.DBCellRecord;
import org.apache.poi.hssf.record.DConRefRecord;
import org.apache.poi.hssf.record.DSFRecord;
import org.apache.poi.hssf.record.DVALRecord;
import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.hssf.record.DateWindow1904Record;
import org.apache.poi.hssf.record.DefaultColWidthRecord;
import org.apache.poi.hssf.record.DefaultRowHeightRecord;
import org.apache.poi.hssf.record.DeltaRecord;
import org.apache.poi.hssf.record.DimensionsRecord;
import org.apache.poi.hssf.record.DrawingGroupRecord;
import org.apache.poi.hssf.record.DrawingRecord;
import org.apache.poi.hssf.record.DrawingSelectionRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.ExtSSTRecord;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.ExternSheetRecord;
import org.apache.poi.hssf.record.ExternalNameRecord;
import org.apache.poi.hssf.record.FeatHdrRecord;
import org.apache.poi.hssf.record.FeatRecord;
import org.apache.poi.hssf.record.FilePassRecord;
import org.apache.poi.hssf.record.FileSharingRecord;
import org.apache.poi.hssf.record.FnGroupCountRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.FooterRecord;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.GridsetRecord;
import org.apache.poi.hssf.record.GutsRecord;
import org.apache.poi.hssf.record.HCenterRecord;
import org.apache.poi.hssf.record.HeaderFooterRecord;
import org.apache.poi.hssf.record.HeaderRecord;
import org.apache.poi.hssf.record.HideObjRecord;
import org.apache.poi.hssf.record.HorizontalPageBreakRecord;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.record.IndexRecord;
import org.apache.poi.hssf.record.InterfaceEndRecord;
import org.apache.poi.hssf.record.InterfaceHdrRecord;
import org.apache.poi.hssf.record.IterationRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.LeftMarginRecord;
import org.apache.poi.hssf.record.MMSRecord;
import org.apache.poi.hssf.record.MergeCellsRecord;
import org.apache.poi.hssf.record.MulBlankRecord;
import org.apache.poi.hssf.record.MulRKRecord;
import org.apache.poi.hssf.record.NameCommentRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.ObjectProtectRecord;
import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.hssf.record.PaneRecord;
import org.apache.poi.hssf.record.PasswordRecord;
import org.apache.poi.hssf.record.PasswordRev4Record;
import org.apache.poi.hssf.record.PrecisionRecord;
import org.apache.poi.hssf.record.PrintGridlinesRecord;
import org.apache.poi.hssf.record.PrintHeadersRecord;
import org.apache.poi.hssf.record.PrintSetupRecord;
import org.apache.poi.hssf.record.ProtectRecord;
import org.apache.poi.hssf.record.ProtectionRev4Record;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.RecalcIdRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.RefModeRecord;
import org.apache.poi.hssf.record.RefreshAllRecord;
import org.apache.poi.hssf.record.RightMarginRecord;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SCLRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.SaveRecalcRecord;
import org.apache.poi.hssf.record.ScenarioProtectRecord;
import org.apache.poi.hssf.record.SelectionRecord;
import org.apache.poi.hssf.record.SharedFormulaRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.StyleRecord;
import org.apache.poi.hssf.record.SupBookRecord;
import org.apache.poi.hssf.record.TabIdRecord;
import org.apache.poi.hssf.record.TableRecord;
import org.apache.poi.hssf.record.TableStylesRecord;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.hssf.record.TopMarginRecord;
import org.apache.poi.hssf.record.UncalcedRecord;
import org.apache.poi.hssf.record.UnknownRecord;
import org.apache.poi.hssf.record.UseSelFSRecord;
import org.apache.poi.hssf.record.UserSViewBegin;
import org.apache.poi.hssf.record.UserSViewEnd;
import org.apache.poi.hssf.record.VCenterRecord;
import org.apache.poi.hssf.record.VerticalPageBreakRecord;
import org.apache.poi.hssf.record.WSBoolRecord;
import org.apache.poi.hssf.record.WindowOneRecord;
import org.apache.poi.hssf.record.WindowProtectRecord;
import org.apache.poi.hssf.record.WindowTwoRecord;
import org.apache.poi.hssf.record.WriteAccessRecord;
import org.apache.poi.hssf.record.WriteProtectRecord;
import org.apache.poi.hssf.record.chart.AreaFormatRecord;
import org.apache.poi.hssf.record.chart.AreaRecord;
import org.apache.poi.hssf.record.chart.AxisLineFormatRecord;
import org.apache.poi.hssf.record.chart.AxisOptionsRecord;
import org.apache.poi.hssf.record.chart.AxisParentRecord;
import org.apache.poi.hssf.record.chart.AxisRecord;
import org.apache.poi.hssf.record.chart.AxisUsedRecord;
import org.apache.poi.hssf.record.chart.BarRecord;
import org.apache.poi.hssf.record.chart.BeginRecord;
import org.apache.poi.hssf.record.chart.CatLabRecord;
import org.apache.poi.hssf.record.chart.CategorySeriesAxisRecord;
import org.apache.poi.hssf.record.chart.ChartEndBlockRecord;
import org.apache.poi.hssf.record.chart.ChartEndObjectRecord;
import org.apache.poi.hssf.record.chart.ChartFRTInfoRecord;
import org.apache.poi.hssf.record.chart.ChartFormatRecord;
import org.apache.poi.hssf.record.chart.ChartRecord;
import org.apache.poi.hssf.record.chart.ChartStartBlockRecord;
import org.apache.poi.hssf.record.chart.ChartStartObjectRecord;
import org.apache.poi.hssf.record.chart.ChartTitleFormatRecord;
import org.apache.poi.hssf.record.chart.DatRecord;
import org.apache.poi.hssf.record.chart.DataFormatRecord;
import org.apache.poi.hssf.record.chart.DataLabelExtensionRecord;
import org.apache.poi.hssf.record.chart.DefaultDataLabelTextPropertiesRecord;
import org.apache.poi.hssf.record.chart.EndRecord;
import org.apache.poi.hssf.record.chart.FontBasisRecord;
import org.apache.poi.hssf.record.chart.FontIndexRecord;
import org.apache.poi.hssf.record.chart.FrameRecord;
import org.apache.poi.hssf.record.chart.LegendRecord;
import org.apache.poi.hssf.record.chart.LineFormatRecord;
import org.apache.poi.hssf.record.chart.LinkedDataRecord;
import org.apache.poi.hssf.record.chart.NumberFormatIndexRecord;
import org.apache.poi.hssf.record.chart.ObjectLinkRecord;
import org.apache.poi.hssf.record.chart.PlotAreaRecord;
import org.apache.poi.hssf.record.chart.PlotGrowthRecord;
import org.apache.poi.hssf.record.chart.SeriesChartGroupIndexRecord;
import org.apache.poi.hssf.record.chart.SeriesIndexRecord;
import org.apache.poi.hssf.record.chart.SeriesLabelsRecord;
import org.apache.poi.hssf.record.chart.SeriesListRecord;
import org.apache.poi.hssf.record.chart.SeriesRecord;
import org.apache.poi.hssf.record.chart.SeriesTextRecord;
import org.apache.poi.hssf.record.chart.SheetPropertiesRecord;
import org.apache.poi.hssf.record.chart.TextRecord;
import org.apache.poi.hssf.record.chart.TickRecord;
import org.apache.poi.hssf.record.chart.UnitsRecord;
import org.apache.poi.hssf.record.chart.ValueRangeRecord;
import org.apache.poi.hssf.record.pivottable.DataItemRecord;
import org.apache.poi.hssf.record.pivottable.ExtendedPivotTableViewFieldsRecord;
import org.apache.poi.hssf.record.pivottable.PageItemRecord;
import org.apache.poi.hssf.record.pivottable.StreamIDRecord;
import org.apache.poi.hssf.record.pivottable.ViewDefinitionRecord;
import org.apache.poi.hssf.record.pivottable.ViewFieldsRecord;
import org.apache.poi.hssf.record.pivottable.ViewSourceRecord;

public enum HSSFRecordTypes {
    UNKNOWN(-1, UnknownRecord.class, UnknownRecord::new, false),
    FORMULA(6, FormulaRecord.class, FormulaRecord::new),
    EOF(10, EOFRecord.class, EOFRecord::new),
    CALC_COUNT(12, CalcCountRecord.class, CalcCountRecord::new),
    CALC_MODE(13, CalcModeRecord.class, CalcModeRecord::new),
    PRECISION(14, PrecisionRecord.class, PrecisionRecord::new),
    REF_MODE(15, RefModeRecord.class, RefModeRecord::new),
    DELTA(16, DeltaRecord.class, DeltaRecord::new),
    ITERATION(17, IterationRecord.class, IterationRecord::new),
    PROTECT(18, ProtectRecord.class, ProtectRecord::new),
    PASSWORD(19, PasswordRecord.class, PasswordRecord::new),
    HEADER(20, HeaderRecord.class, HeaderRecord::new),
    FOOTER(21, FooterRecord.class, FooterRecord::new),
    EXTERN_SHEET(23, ExternSheetRecord.class, ExternSheetRecord::new),
    NAME(24, NameRecord.class, NameRecord::new),
    WINDOW_PROTECT(25, WindowProtectRecord.class, WindowProtectRecord::new),
    VERTICAL_PAGE_BREAK(26, VerticalPageBreakRecord.class, VerticalPageBreakRecord::new),
    HORIZONTAL_PAGE_BREAK(27, HorizontalPageBreakRecord.class, HorizontalPageBreakRecord::new),
    NOTE(28, NoteRecord.class, NoteRecord::new),
    SELECTION(29, SelectionRecord.class, SelectionRecord::new),
    DATE_WINDOW_1904(34, DateWindow1904Record.class, DateWindow1904Record::new),
    EXTERNAL_NAME(35, ExternalNameRecord.class, ExternalNameRecord::new),
    LEFT_MARGIN(38, LeftMarginRecord.class, LeftMarginRecord::new),
    RIGHT_MARGIN(39, RightMarginRecord.class, RightMarginRecord::new),
    TOP_MARGIN(40, TopMarginRecord.class, TopMarginRecord::new),
    BOTTOM_MARGIN(41, BottomMarginRecord.class, BottomMarginRecord::new),
    PRINT_HEADERS(42, PrintHeadersRecord.class, PrintHeadersRecord::new),
    PRINT_GRIDLINES(43, PrintGridlinesRecord.class, PrintGridlinesRecord::new),
    FILE_PASS(47, FilePassRecord.class, FilePassRecord::new),
    FONT(49, FontRecord.class, FontRecord::new),
    CONTINUE(60, ContinueRecord.class, ContinueRecord::new),
    WINDOW_ONE(61, WindowOneRecord.class, WindowOneRecord::new),
    BACKUP(64, BackupRecord.class, BackupRecord::new),
    PANE(65, PaneRecord.class, PaneRecord::new),
    CODEPAGE(66, CodepageRecord.class, CodepageRecord::new),
    DCON_REF(81, DConRefRecord.class, DConRefRecord::new),
    DEFAULT_COL_WIDTH(85, DefaultColWidthRecord.class, DefaultColWidthRecord::new),
    CRN_COUNT(89, CRNCountRecord.class, CRNCountRecord::new),
    CRN(90, CRNRecord.class, CRNRecord::new),
    WRITE_ACCESS(92, WriteAccessRecord.class, WriteAccessRecord::new),
    FILE_SHARING(91, FileSharingRecord.class, FileSharingRecord::new),
    OBJ(93, ObjRecord.class, ObjRecord::new),
    UNCALCED(94, UncalcedRecord.class, UncalcedRecord::new),
    SAVE_RECALC(95, SaveRecalcRecord.class, SaveRecalcRecord::new),
    OBJECT_PROTECT(99, ObjectProtectRecord.class, ObjectProtectRecord::new),
    COLUMN_INFO(125, ColumnInfoRecord.class, ColumnInfoRecord::new),
    GUTS(128, GutsRecord.class, GutsRecord::new),
    WS_BOOL(129, WSBoolRecord.class, WSBoolRecord::new),
    GRIDSET(130, GridsetRecord.class, GridsetRecord::new),
    H_CENTER(131, HCenterRecord.class, HCenterRecord::new),
    V_CENTER(132, VCenterRecord.class, VCenterRecord::new),
    BOUND_SHEET(133, BoundSheetRecord.class, BoundSheetRecord::new),
    WRITE_PROTECT(134, WriteProtectRecord.class, WriteProtectRecord::new),
    COUNTRY(140, CountryRecord.class, CountryRecord::new),
    HIDE_OBJ(141, HideObjRecord.class, HideObjRecord::new),
    PALETTE(146, PaletteRecord.class, PaletteRecord::new),
    FN_GROUP_COUNT(156, FnGroupCountRecord.class, FnGroupCountRecord::new),
    AUTO_FILTER_INFO(157, AutoFilterInfoRecord.class, AutoFilterInfoRecord::new),
    SCL(160, SCLRecord.class, SCLRecord::new, false),
    PRINT_SETUP(161, PrintSetupRecord.class, PrintSetupRecord::new),
    VIEW_DEFINITION(176, ViewDefinitionRecord.class, ViewDefinitionRecord::new),
    VIEW_FIELDS(177, ViewFieldsRecord.class, ViewFieldsRecord::new),
    PAGE_ITEM(182, PageItemRecord.class, PageItemRecord::new),
    MUL_BLANK(190, MulBlankRecord.class, MulBlankRecord::new),
    MUL_RK(189, MulRKRecord.class, MulRKRecord::new),
    MMS(193, MMSRecord.class, MMSRecord::new),
    DATA_ITEM(197, DataItemRecord.class, DataItemRecord::new),
    STREAM_ID(213, StreamIDRecord.class, StreamIDRecord::new),
    DB_CELL(215, DBCellRecord.class, DBCellRecord::new),
    BOOK_BOOL(218, BookBoolRecord.class, BookBoolRecord::new),
    SCENARIO_PROTECT(221, ScenarioProtectRecord.class, ScenarioProtectRecord::new),
    EXTENDED_FORMAT(224, ExtendedFormatRecord.class, ExtendedFormatRecord::new),
    INTERFACE_HDR(225, InterfaceHdrRecord.class, InterfaceHdrRecord::new),
    INTERFACE_END(226, InterfaceEndRecord.class, InterfaceEndRecord::create),
    VIEW_SOURCE(227, ViewSourceRecord.class, ViewSourceRecord::new),
    MERGE_CELLS(229, MergeCellsRecord.class, MergeCellsRecord::new),
    DRAWING_GROUP(235, DrawingGroupRecord.class, DrawingGroupRecord::new),
    DRAWING(236, DrawingRecord.class, DrawingRecord::new),
    DRAWING_SELECTION(237, DrawingSelectionRecord.class, DrawingSelectionRecord::new),
    SST(252, SSTRecord.class, SSTRecord::new),
    LABEL_SST(253, LabelSSTRecord.class, LabelSSTRecord::new),
    EXT_SST(255, ExtSSTRecord.class, ExtSSTRecord::new),
    EXTENDED_PIVOT_TABLE_VIEW_FIELDS(256, ExtendedPivotTableViewFieldsRecord.class, ExtendedPivotTableViewFieldsRecord::new),
    TAB_ID(317, TabIdRecord.class, TabIdRecord::new),
    USE_SEL_FS(352, UseSelFSRecord.class, UseSelFSRecord::new),
    DSF(353, DSFRecord.class, DSFRecord::new),
    USER_SVIEW_BEGIN(426, UserSViewBegin.class, UserSViewBegin::new),
    USER_SVIEW_END(427, UserSViewEnd.class, UserSViewEnd::new),
    SUP_BOOK(430, SupBookRecord.class, SupBookRecord::new),
    PROTECTION_REV_4(431, ProtectionRev4Record.class, ProtectionRev4Record::new),
    CF_HEADER(432, CFHeaderRecord.class, CFHeaderRecord::new),
    CF_RULE(433, CFRuleRecord.class, CFRuleRecord::new),
    DVAL(434, DVALRecord.class, DVALRecord::new),
    TEXT_OBJECT(438, TextObjectRecord.class, TextObjectRecord::new),
    REFRESH_ALL(439, RefreshAllRecord.class, RefreshAllRecord::new),
    HYPERLINK(440, HyperlinkRecord.class, HyperlinkRecord::new),
    PASSWORD_REV_4(444, PasswordRev4Record.class, PasswordRev4Record::new),
    DV(446, DVRecord.class, DVRecord::new),
    RECALC_ID(449, RecalcIdRecord.class, RecalcIdRecord::new),
    DIMENSIONS(512, DimensionsRecord.class, DimensionsRecord::new),
    BLANK(513, BlankRecord.class, BlankRecord::new),
    NUMBER(515, NumberRecord.class, NumberRecord::new),
    LABEL(516, LabelRecord.class, LabelRecord::new),
    BOOL_ERR(517, BoolErrRecord.class, BoolErrRecord::new),
    STRING(519, StringRecord.class, StringRecord::new),
    ROW(520, RowRecord.class, RowRecord::new),
    INDEX(523, IndexRecord.class, IndexRecord::new),
    ARRAY(545, ArrayRecord.class, ArrayRecord::new),
    DEFAULT_ROW_HEIGHT(549, DefaultRowHeightRecord.class, DefaultRowHeightRecord::new),
    TABLE(566, TableRecord.class, TableRecord::new),
    WINDOW_TWO(574, WindowTwoRecord.class, WindowTwoRecord::new),
    RK(638, RKRecord.class, RKRecord::new),
    STYLE(659, StyleRecord.class, StyleRecord::new),
    FORMAT(1054, FormatRecord.class, FormatRecord::new),
    SHARED_FORMULA(1212, SharedFormulaRecord.class, SharedFormulaRecord::new),
    BOF(2057, BOFRecord.class, BOFRecord::new),
    CHART_FRT_INFO(2128, ChartFRTInfoRecord.class, ChartFRTInfoRecord::new),
    CHART_START_BLOCK(2130, ChartStartBlockRecord.class, ChartStartBlockRecord::new),
    CHART_END_BLOCK(2131, ChartEndBlockRecord.class, ChartEndBlockRecord::new),
    CHART_START_OBJECT(2132, ChartStartObjectRecord.class, ChartStartObjectRecord::new),
    CHART_END_OBJECT(2133, ChartEndObjectRecord.class, ChartEndObjectRecord::new),
    CAT_LAB(2134, CatLabRecord.class, CatLabRecord::new),
    FEAT_HDR(2151, FeatHdrRecord.class, FeatHdrRecord::new),
    FEAT(2152, FeatRecord.class, FeatRecord::new),
    DATA_LABEL_EXTENSION(2154, DataLabelExtensionRecord.class, DataLabelExtensionRecord::new, false),
    CF_HEADER_12(2169, CFHeader12Record.class, CFHeader12Record::new),
    CF_RULE_12(2170, CFRule12Record.class, CFRule12Record::new),
    TABLE_STYLES(2190, TableStylesRecord.class, TableStylesRecord::new),
    NAME_COMMENT(2196, NameCommentRecord.class, NameCommentRecord::new),
    HEADER_FOOTER(2204, HeaderFooterRecord.class, HeaderFooterRecord::new),
    UNITS(4097, UnitsRecord.class, UnitsRecord::new, false),
    CHART(4098, ChartRecord.class, ChartRecord::new),
    SERIES(4099, SeriesRecord.class, SeriesRecord::new),
    DATA_FORMAT(4102, DataFormatRecord.class, DataFormatRecord::new),
    LINE_FORMAT(4103, LineFormatRecord.class, LineFormatRecord::new, false),
    AREA_FORMAT(4106, AreaFormatRecord.class, AreaFormatRecord::new, false),
    SERIES_LABELS(4108, SeriesLabelsRecord.class, SeriesLabelsRecord::new, false),
    SERIES_TEXT(4109, SeriesTextRecord.class, SeriesTextRecord::new),
    CHART_FORMAT(4116, ChartFormatRecord.class, ChartFormatRecord::new, false),
    LEGEND(4117, LegendRecord.class, LegendRecord::new),
    SERIES_LIST(4118, SeriesListRecord.class, SeriesListRecord::new, false),
    BAR(4119, BarRecord.class, BarRecord::new, false),
    AREA(4122, AreaRecord.class, AreaRecord::new),
    AXIS(4125, AxisRecord.class, AxisRecord::new, false),
    TICK(4126, TickRecord.class, TickRecord::new, false),
    VALUE_RANGE(4127, ValueRangeRecord.class, ValueRangeRecord::new),
    CATEGORY_SERIES_AXIS(4128, CategorySeriesAxisRecord.class, CategorySeriesAxisRecord::new, false),
    AXIS_LINE_FORMAT(4129, AxisLineFormatRecord.class, AxisLineFormatRecord::new, false),
    DEFAULT_DATA_LABEL_TEXT_PROPERTIES(4132, DefaultDataLabelTextPropertiesRecord.class, DefaultDataLabelTextPropertiesRecord::new, false),
    TEXT(4133, TextRecord.class, TextRecord::new, false),
    FONT_INDEX(4134, FontIndexRecord.class, FontIndexRecord::new, false),
    OBJECT_LINK(4135, ObjectLinkRecord.class, ObjectLinkRecord::new, false),
    FRAME(4146, FrameRecord.class, FrameRecord::new, false),
    BEGIN(4147, BeginRecord.class, BeginRecord::new),
    END(4148, EndRecord.class, EndRecord::new),
    PLOT_AREA(4149, PlotAreaRecord.class, PlotAreaRecord::new, false),
    AXIS_PARENT(4161, AxisParentRecord.class, AxisParentRecord::new, false),
    SHEET_PROPERTIES(4164, SheetPropertiesRecord.class, SheetPropertiesRecord::new, false),
    SERIES_CHART_GROUP_INDEX(4165, SeriesChartGroupIndexRecord.class, SeriesChartGroupIndexRecord::new),
    AXIS_USED(4166, AxisUsedRecord.class, AxisUsedRecord::new, false),
    NUMBER_FORMAT_INDEX(4174, NumberFormatIndexRecord.class, NumberFormatIndexRecord::new, false),
    CHART_TITLE_FORMAT(4176, ChartTitleFormatRecord.class, ChartTitleFormatRecord::new),
    LINKED_DATA(4177, LinkedDataRecord.class, LinkedDataRecord::new),
    FONT_BASIS(4192, FontBasisRecord.class, FontBasisRecord::new, false),
    AXIS_OPTIONS(4194, AxisOptionsRecord.class, AxisOptionsRecord::new, false),
    DAT(4195, DatRecord.class, DatRecord::new, false),
    PLOT_GROWTH(4196, PlotGrowthRecord.class, PlotGrowthRecord::new, false),
    SERIES_INDEX(4197, SeriesIndexRecord.class, SeriesIndexRecord::new, false),
    ESCHER_AGGREGATE(9876, EscherAggregate.class, in -> new EscherAggregate(true));

    private static final Map<Short, HSSFRecordTypes> LOOKUP;
    public final short sid;
    public final Class<? extends Record> clazz;
    public final RecordConstructor<? extends Record> recordConstructor;
    public final boolean parse;

    private HSSFRecordTypes(int sid, Class<? extends Record> clazz, RecordConstructor<? extends Record> recordConstructor) {
        this(sid, clazz, recordConstructor, true);
    }

    private HSSFRecordTypes(int sid, Class<? extends Record> clazz, RecordConstructor<? extends Record> recordConstructor, boolean parse) {
        this.sid = (short)sid;
        this.clazz = clazz;
        this.recordConstructor = recordConstructor;
        this.parse = parse;
    }

    public static HSSFRecordTypes forSID(int sid) {
        return LOOKUP.getOrDefault((short)sid, UNKNOWN);
    }

    public short getSid() {
        return this.sid;
    }

    public Class<? extends Record> getClazz() {
        return this.clazz;
    }

    public RecordConstructor<? extends Record> getRecordConstructor() {
        return this.recordConstructor;
    }

    public boolean isParseable() {
        return this.parse;
    }

    static {
        LOOKUP = Arrays.stream(HSSFRecordTypes.values()).collect(Collectors.toMap(HSSFRecordTypes::getSid, Function.identity()));
    }

    @FunctionalInterface
    public static interface RecordConstructor<T extends Record> {
        public T apply(RecordInputStream var1);
    }
}

