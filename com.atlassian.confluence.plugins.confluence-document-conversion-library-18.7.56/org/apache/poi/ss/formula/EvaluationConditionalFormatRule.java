/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.apache.poi.ss.formula.ConditionalFormattingEvaluator;
import org.apache.poi.ss.formula.DataValidationEvaluator;
import org.apache.poi.ss.formula.OperatorEnum;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.AggregateFunction;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ConditionFilterData;
import org.apache.poi.ss.usermodel.ConditionFilterType;
import org.apache.poi.ss.usermodel.ConditionType;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.ExcelNumberFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.StringUtil;

public class EvaluationConditionalFormatRule
implements Comparable<EvaluationConditionalFormatRule> {
    private final WorkbookEvaluator workbookEvaluator;
    private final Sheet sheet;
    private final ConditionalFormatting formatting;
    private final ConditionalFormattingRule rule;
    private final CellRangeAddress[] regions;
    private CellRangeAddress topLeftRegion;
    private final Map<CellRangeAddress, Set<ValueAndFormat>> meaningfulRegionValues = new HashMap<CellRangeAddress, Set<ValueAndFormat>>();
    private final int priority;
    private final int formattingIndex;
    private final int ruleIndex;
    private final String formula1;
    private final String formula2;
    private final String text;
    private final String lowerText;
    private final OperatorEnum operator;
    private final ConditionType type;
    private final ExcelNumberFormat numberFormat;
    private final DecimalFormat decimalTextFormat;

    public EvaluationConditionalFormatRule(WorkbookEvaluator workbookEvaluator, Sheet sheet, ConditionalFormatting formatting, int formattingIndex, ConditionalFormattingRule rule, int ruleIndex, CellRangeAddress[] regions) {
        this.workbookEvaluator = workbookEvaluator;
        this.sheet = sheet;
        this.formatting = formatting;
        this.rule = rule;
        this.formattingIndex = formattingIndex;
        this.ruleIndex = ruleIndex;
        this.priority = rule.getPriority();
        this.regions = regions;
        for (CellRangeAddress region : regions) {
            if (this.topLeftRegion == null) {
                this.topLeftRegion = region;
                continue;
            }
            if (region.getFirstColumn() >= this.topLeftRegion.getFirstColumn() && region.getFirstRow() >= this.topLeftRegion.getFirstRow()) continue;
            this.topLeftRegion = region;
        }
        this.formula1 = rule.getFormula1();
        this.formula2 = rule.getFormula2();
        this.text = rule.getText();
        this.lowerText = this.text == null ? null : this.text.toLowerCase(LocaleUtil.getUserLocale());
        this.numberFormat = rule.getNumberFormat();
        this.operator = OperatorEnum.values()[rule.getComparisonOperation()];
        this.type = rule.getConditionType();
        this.decimalTextFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        this.decimalTextFormat.setMaximumFractionDigits(340);
    }

    public Sheet getSheet() {
        return this.sheet;
    }

    public ConditionalFormatting getFormatting() {
        return this.formatting;
    }

    public int getFormattingIndex() {
        return this.formattingIndex;
    }

    public ExcelNumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    public ConditionalFormattingRule getRule() {
        return this.rule;
    }

    public int getRuleIndex() {
        return this.ruleIndex;
    }

    public CellRangeAddress[] getRegions() {
        return this.regions;
    }

    public int getPriority() {
        return this.priority;
    }

    public String getFormula1() {
        return this.formula1;
    }

    public String getFormula2() {
        return this.formula2;
    }

    public String getText() {
        return this.text;
    }

    public OperatorEnum getOperator() {
        return this.operator;
    }

    public ConditionType getType() {
        return this.type;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }
        EvaluationConditionalFormatRule r = (EvaluationConditionalFormatRule)obj;
        return this.getSheet().getSheetName().equalsIgnoreCase(r.getSheet().getSheetName()) && this.getFormattingIndex() == r.getFormattingIndex() && this.getRuleIndex() == r.getRuleIndex();
    }

    @Override
    public int compareTo(EvaluationConditionalFormatRule o) {
        int y;
        int cmp = this.getSheet().getSheetName().compareToIgnoreCase(o.getSheet().getSheetName());
        if (cmp != 0) {
            return cmp;
        }
        int x = this.getPriority();
        cmp = Integer.compare(x, y = o.getPriority());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(this.getFormattingIndex(), o.getFormattingIndex());
        if (cmp != 0) {
            return cmp;
        }
        return Integer.compare(this.getRuleIndex(), o.getRuleIndex());
    }

    public int hashCode() {
        return Objects.hash(this.sheet.getSheetName(), this.formattingIndex, this.ruleIndex);
    }

    boolean matches(CellReference ref) {
        CellRangeAddress region = null;
        for (CellRangeAddress r : this.regions) {
            if (!r.isInRange(ref)) continue;
            region = r;
            break;
        }
        if (region == null) {
            return false;
        }
        ConditionType ruleType = this.getRule().getConditionType();
        if (ruleType.equals(ConditionType.COLOR_SCALE) || ruleType.equals(ConditionType.DATA_BAR) || ruleType.equals(ConditionType.ICON_SET)) {
            return true;
        }
        Cell cell = null;
        Row row = this.sheet.getRow(ref.getRow());
        if (row != null) {
            cell = row.getCell(ref.getCol());
        }
        if (ruleType.equals(ConditionType.CELL_VALUE_IS)) {
            if (cell == null) {
                return false;
            }
            return this.checkValue(cell, this.topLeftRegion);
        }
        if (ruleType.equals(ConditionType.FORMULA)) {
            return this.checkFormula(ref, this.topLeftRegion);
        }
        if (ruleType.equals(ConditionType.FILTER)) {
            return this.checkFilter(cell, ref, this.topLeftRegion);
        }
        return false;
    }

    private boolean checkValue(Cell cell, CellRangeAddress region) {
        if (cell == null || DataValidationEvaluator.isType(cell, CellType.BLANK) || DataValidationEvaluator.isType(cell, CellType.ERROR) || DataValidationEvaluator.isType(cell, CellType.STRING) && (cell.getStringCellValue() == null || cell.getStringCellValue().isEmpty())) {
            return false;
        }
        ValueEval eval = this.unwrapEval(this.workbookEvaluator.evaluate(this.rule.getFormula1(), ConditionalFormattingEvaluator.getRef(cell), region));
        String f2 = this.rule.getFormula2();
        ValueEval eval2 = BlankEval.instance;
        if (f2 != null && f2.length() > 0) {
            eval2 = this.unwrapEval(this.workbookEvaluator.evaluate(f2, ConditionalFormattingEvaluator.getRef(cell), region));
        }
        if (DataValidationEvaluator.isType(cell, CellType.BOOLEAN) && (eval == BlankEval.instance || eval instanceof BoolEval) && (eval2 == BlankEval.instance || eval2 instanceof BoolEval)) {
            return this.operator.isValid(cell.getBooleanCellValue(), eval == BlankEval.instance ? null : Boolean.valueOf(((BoolEval)eval).getBooleanValue()), eval2 == BlankEval.instance ? null : Boolean.valueOf(((BoolEval)eval2).getBooleanValue()));
        }
        if (DataValidationEvaluator.isType(cell, CellType.NUMERIC) && (eval == BlankEval.instance || eval instanceof NumberEval) && (eval2 == BlankEval.instance || eval2 instanceof NumberEval)) {
            return this.operator.isValid(cell.getNumericCellValue(), eval == BlankEval.instance ? null : Double.valueOf(((NumberEval)eval).getNumberValue()), eval2 == BlankEval.instance ? null : Double.valueOf(((NumberEval)eval2).getNumberValue()));
        }
        if (DataValidationEvaluator.isType(cell, CellType.STRING) && (eval == BlankEval.instance || eval instanceof StringEval) && (eval2 == BlankEval.instance || eval2 instanceof StringEval)) {
            return this.operator.isValid(cell.getStringCellValue(), eval == BlankEval.instance ? null : ((StringEval)eval).getStringValue(), eval2 == BlankEval.instance ? null : ((StringEval)eval2).getStringValue());
        }
        return this.operator.isValidForIncompatibleTypes();
    }

    private ValueEval unwrapEval(ValueEval eval) {
        ValueEval comp = eval;
        while (comp instanceof RefEval) {
            RefEval ref = (RefEval)comp;
            comp = ref.getInnerValueEval(ref.getFirstSheetIndex());
        }
        return comp;
    }

    private boolean checkFormula(CellReference ref, CellRangeAddress region) {
        ValueEval comp = this.unwrapEval(this.workbookEvaluator.evaluate(this.rule.getFormula1(), ref, region));
        if (comp instanceof BlankEval) {
            return true;
        }
        if (comp instanceof ErrorEval) {
            return false;
        }
        if (comp instanceof BoolEval) {
            return ((BoolEval)comp).getBooleanValue();
        }
        if (comp instanceof NumberEval) {
            return ((NumberEval)comp).getNumberValue() != 0.0;
        }
        return false;
    }

    private boolean checkFilter(Cell cell, CellReference ref, CellRangeAddress region) {
        ConditionFilterType filterType = this.rule.getConditionFilterType();
        if (filterType == null) {
            return false;
        }
        ValueAndFormat cv = this.getCellValue(cell);
        switch (filterType) {
            case FILTER: {
                return false;
            }
            case TOP_10: {
                if (!cv.isNumber()) {
                    return false;
                }
                return this.getMeaningfulValues(region, false, this::evaluateTop10).contains(cv);
            }
            case UNIQUE_VALUES: {
                return this.getMeaningfulValues(region, true, this::evaluateUniqueValues).contains(cv);
            }
            case DUPLICATE_VALUES: {
                return this.getMeaningfulValues(region, true, this::evaluateDuplicateValues).contains(cv);
            }
            case ABOVE_AVERAGE: {
                Double val;
                ConditionFilterData conf = this.rule.getFilterConfiguration();
                ArrayList<ValueAndFormat> values = new ArrayList<ValueAndFormat>(this.getMeaningfulValues(region, false, this::evaluateAboveAverage));
                Double d = val = cv.isNumber() ? cv.getValue() : null;
                if (val == null) {
                    return false;
                }
                double avg = ((ValueAndFormat)values.get(0)).value;
                double stdDev = ((ValueAndFormat)values.get(1)).value;
                Double comp = conf.getStdDev() > 0 ? avg + (double)(conf.getAboveAverage() ? 1 : -1) * stdDev * (double)conf.getStdDev() : avg;
                OperatorEnum op = conf.getAboveAverage() ? (conf.getEqualAverage() ? OperatorEnum.GREATER_OR_EQUAL : OperatorEnum.GREATER_THAN) : (conf.getEqualAverage() ? OperatorEnum.LESS_OR_EQUAL : OperatorEnum.LESS_THAN);
                return op.isValid(val, comp, null);
            }
            case CONTAINS_TEXT: {
                return this.text != null && cv.toString().toLowerCase(LocaleUtil.getUserLocale()).contains(this.lowerText);
            }
            case NOT_CONTAINS_TEXT: {
                return this.text == null || !cv.toString().toLowerCase(LocaleUtil.getUserLocale()).contains(this.lowerText);
            }
            case BEGINS_WITH: {
                return cv.toString().toLowerCase(LocaleUtil.getUserLocale()).startsWith(this.lowerText);
            }
            case ENDS_WITH: {
                return cv.toString().toLowerCase(LocaleUtil.getUserLocale()).endsWith(this.lowerText);
            }
            case CONTAINS_BLANKS: {
                try {
                    String v = cv.getString();
                    return StringUtil.isBlank(v);
                }
                catch (Exception e) {
                    return false;
                }
            }
            case NOT_CONTAINS_BLANKS: {
                try {
                    String v = cv.getString();
                    return StringUtil.isNotBlank(v);
                }
                catch (Exception e) {
                    return true;
                }
            }
            case CONTAINS_ERRORS: {
                return cell != null && DataValidationEvaluator.isType(cell, CellType.ERROR);
            }
            case NOT_CONTAINS_ERRORS: {
                return cell == null || !DataValidationEvaluator.isType(cell, CellType.ERROR);
            }
            case TIME_PERIOD: {
                return this.checkFormula(ref, region);
            }
        }
        return false;
    }

    private Set<ValueAndFormat> evaluateTop10(List<ValueAndFormat> allValues) {
        ConditionFilterData conf = this.rule.getFilterConfiguration();
        if (!conf.getBottom()) {
            allValues.sort(Collections.reverseOrder());
        } else {
            Collections.sort(allValues);
        }
        int limit = Math.toIntExact(conf.getRank());
        if (conf.getPercent()) {
            limit = allValues.size() * limit / 100;
        }
        if (allValues.size() <= limit) {
            return new HashSet<ValueAndFormat>(allValues);
        }
        return new HashSet<ValueAndFormat>(allValues.subList(0, limit));
    }

    private Set<ValueAndFormat> evaluateUniqueValues(List<ValueAndFormat> allValues) {
        Collections.sort(allValues);
        HashSet<ValueAndFormat> unique = new HashSet<ValueAndFormat>();
        for (int i = 0; i < allValues.size(); ++i) {
            ValueAndFormat v = allValues.get(i);
            if (i < allValues.size() - 1 && v.equals(allValues.get(i + 1)) || i > 0 && i == allValues.size() - 1 && v.equals(allValues.get(i - 1))) {
                ++i;
                continue;
            }
            unique.add(v);
        }
        return unique;
    }

    public Set<ValueAndFormat> evaluateDuplicateValues(List<ValueAndFormat> allValues) {
        Collections.sort(allValues);
        HashSet<ValueAndFormat> dup = new HashSet<ValueAndFormat>();
        for (int i = 0; i < allValues.size(); ++i) {
            ValueAndFormat v = allValues.get(i);
            if ((i >= allValues.size() - 1 || !v.equals(allValues.get(i + 1))) && (i <= 0 || i != allValues.size() - 1 || !v.equals(allValues.get(i - 1)))) continue;
            dup.add(v);
            ++i;
        }
        return dup;
    }

    private Set<ValueAndFormat> evaluateAboveAverage(List<ValueAndFormat> allValues) {
        double total = 0.0;
        ValueEval[] pop = new ValueEval[allValues.size()];
        for (int i = 0; i < allValues.size(); ++i) {
            ValueAndFormat v = allValues.get(i);
            total += v.value.doubleValue();
            pop[i] = new NumberEval(v.value);
        }
        LinkedHashSet<ValueAndFormat> avgSet = new LinkedHashSet<ValueAndFormat>(1);
        avgSet.add(new ValueAndFormat(allValues.isEmpty() ? 0.0 : total / (double)allValues.size(), null, this.decimalTextFormat));
        double stdDev = allValues.size() <= 1 ? 0.0 : ((NumberEval)AggregateFunction.STDEV.evaluate(pop, 0, 0)).getNumberValue();
        avgSet.add(new ValueAndFormat(stdDev, null, this.decimalTextFormat));
        return avgSet;
    }

    private Set<ValueAndFormat> getMeaningfulValues(CellRangeAddress region, boolean withText, Function<List<ValueAndFormat>, Set<ValueAndFormat>> func) {
        Set<ValueAndFormat> values = this.meaningfulRegionValues.get(region);
        if (values != null) {
            return values;
        }
        ArrayList<ValueAndFormat> allValues = new ArrayList<ValueAndFormat>((region.getLastColumn() - region.getFirstColumn() + 1) * (region.getLastRow() - region.getFirstRow() + 1));
        for (int r = region.getFirstRow(); r <= region.getLastRow(); ++r) {
            Row row = this.sheet.getRow(r);
            if (row == null) continue;
            for (int c = region.getFirstColumn(); c <= region.getLastColumn(); ++c) {
                Cell cell = row.getCell(c);
                ValueAndFormat cv = this.getCellValue(cell);
                if (!withText && !cv.isNumber()) continue;
                allValues.add(cv);
            }
        }
        values = func.apply(allValues);
        this.meaningfulRegionValues.put(region, values);
        return values;
    }

    private ValueAndFormat getCellValue(Cell cell) {
        if (cell != null) {
            String format = cell.getCellStyle().getDataFormatString();
            CellType type = cell.getCellType();
            if (type == CellType.FORMULA) {
                type = cell.getCachedFormulaResultType();
            }
            switch (type) {
                case NUMERIC: {
                    return new ValueAndFormat(cell.getNumericCellValue(), format, this.decimalTextFormat);
                }
                case STRING: 
                case BOOLEAN: {
                    return new ValueAndFormat(cell.getStringCellValue(), format);
                }
            }
        }
        return new ValueAndFormat("", "");
    }

    protected static class ValueAndFormat
    implements Comparable<ValueAndFormat> {
        private final Double value;
        private final String string;
        private final String format;
        private final DecimalFormat decimalTextFormat;

        public ValueAndFormat(Double value, String format, DecimalFormat df) {
            this.value = value;
            this.format = format;
            this.string = null;
            this.decimalTextFormat = df;
        }

        public ValueAndFormat(String value, String format) {
            this.value = null;
            this.format = format;
            this.string = value;
            this.decimalTextFormat = null;
        }

        public boolean isNumber() {
            return this.value != null;
        }

        public Double getValue() {
            return this.value;
        }

        public String getString() {
            return this.string;
        }

        public String toString() {
            if (this.isNumber()) {
                return this.decimalTextFormat.format(this.getValue());
            }
            return this.getString();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ValueAndFormat)) {
                return false;
            }
            ValueAndFormat o = (ValueAndFormat)obj;
            return Objects.equals(this.value, o.value) && Objects.equals(this.format, o.format) && Objects.equals(this.string, o.string);
        }

        @Override
        public int compareTo(ValueAndFormat o) {
            int cmp;
            if (this.value == null && o.value != null) {
                return 1;
            }
            if (o.value == null && this.value != null) {
                return -1;
            }
            int n = cmp = this.value == null ? 0 : this.value.compareTo(o.value);
            if (cmp != 0) {
                return cmp;
            }
            if (this.string == null && o.string != null) {
                return 1;
            }
            if (o.string == null && this.string != null) {
                return -1;
            }
            return this.string == null ? 0 : this.string.compareTo(o.string);
        }

        public int hashCode() {
            return Objects.hash(this.string, this.value, this.format);
        }
    }
}

