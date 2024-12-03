/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Countif;
import org.apache.poi.util.Internal;

@Internal
public final class LookupUtils {
    private static Map<Integer, MatchMode> matchModeMap = new HashMap<Integer, MatchMode>();
    private static Map<Integer, SearchMode> searchModeMap = new HashMap<Integer, SearchMode>();

    public static MatchMode matchMode(int m) {
        MatchMode mode = matchModeMap.get(m);
        if (mode == null) {
            throw new IllegalArgumentException("unknown match mode " + m);
        }
        return mode;
    }

    public static SearchMode searchMode(int s) {
        SearchMode mode = searchModeMap.get(s);
        if (mode == null) {
            throw new IllegalArgumentException("unknown search mode " + s);
        }
        return mode;
    }

    public static ValueVector createRowVector(TwoDEval tableArray, int relativeRowIndex) {
        return new RowVector(tableArray, relativeRowIndex);
    }

    public static ValueVector createColumnVector(TwoDEval tableArray, int relativeColumnIndex) {
        return new ColumnVector(tableArray, relativeColumnIndex);
    }

    public static ValueVector createVector(TwoDEval ae) {
        if (ae.isColumn()) {
            return LookupUtils.createColumnVector(ae, 0);
        }
        if (ae.isRow()) {
            return LookupUtils.createRowVector(ae, 0);
        }
        return null;
    }

    public static ValueVector createVector(RefEval re) {
        return new SheetVector(re);
    }

    public static int resolveRowOrColIndexArg(ValueEval rowColIndexArg, int srcCellRow, int srcCellCol) throws EvaluationException {
        StringEval se;
        String strVal;
        Double dVal;
        ValueEval veRowColIndexArg;
        if (rowColIndexArg == null) {
            throw new IllegalArgumentException("argument must not be null");
        }
        try {
            veRowColIndexArg = OperandResolver.getSingleValue(rowColIndexArg, srcCellRow, (short)srcCellCol);
        }
        catch (EvaluationException e) {
            throw EvaluationException.invalidRef();
        }
        if (veRowColIndexArg instanceof StringEval && (dVal = OperandResolver.parseDouble(strVal = (se = (StringEval)veRowColIndexArg).getStringValue())) == null) {
            throw EvaluationException.invalidRef();
        }
        int oneBasedIndex = OperandResolver.coerceValueToInt(veRowColIndexArg);
        if (oneBasedIndex < 1) {
            throw EvaluationException.invalidValue();
        }
        return oneBasedIndex - 1;
    }

    public static TwoDEval resolveTableArrayArg(ValueEval eval) throws EvaluationException {
        if (eval instanceof TwoDEval) {
            return (TwoDEval)eval;
        }
        if (eval instanceof RefEval) {
            RefEval refEval = (RefEval)eval;
            return refEval.offset(0, 0, 0, 0);
        }
        throw EvaluationException.invalidValue();
    }

    public static boolean resolveRangeLookupArg(ValueEval rangeLookupArg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval valEval = OperandResolver.getSingleValue(rangeLookupArg, srcCellRow, srcCellCol);
        if (valEval == MissingArgEval.instance) {
            return false;
        }
        if (valEval instanceof BlankEval) {
            return false;
        }
        if (valEval instanceof BoolEval) {
            BoolEval boolEval = (BoolEval)valEval;
            return boolEval.getBooleanValue();
        }
        if (valEval instanceof StringEval) {
            String stringValue = ((StringEval)valEval).getStringValue();
            if (stringValue.length() < 1) {
                throw EvaluationException.invalidValue();
            }
            Boolean b = Countif.parseBoolean(stringValue);
            if (b != null) {
                return b;
            }
            throw EvaluationException.invalidValue();
        }
        if (valEval instanceof NumericValueEval) {
            NumericValueEval nve = (NumericValueEval)valEval;
            return 0.0 != nve.getNumberValue();
        }
        throw new RuntimeException("Unexpected eval type (" + valEval + ")");
    }

    public static int lookupFirstIndexOfValue(ValueEval lookupValue, ValueVector vector, boolean isRangeLookup) throws EvaluationException {
        LookupValueComparer lookupComparer = LookupUtils.createLookupComparer(lookupValue, isRangeLookup, false);
        int result = isRangeLookup ? LookupUtils.performBinarySearch(vector, lookupComparer) : LookupUtils.lookupFirstIndexOfValue(lookupComparer, vector, MatchMode.ExactMatch);
        if (result < 0) {
            throw new EvaluationException(ErrorEval.NA);
        }
        return result;
    }

    public static int xlookupIndexOfValue(ValueEval lookupValue, ValueVector vector, MatchMode matchMode, SearchMode searchMode) throws EvaluationException {
        ValueEval modifiedLookup = lookupValue;
        if (lookupValue instanceof StringEval && (matchMode == MatchMode.ExactMatchFallbackToLargerValue || matchMode == MatchMode.ExactMatchFallbackToSmallerValue)) {
            String lookupText = ((StringEval)lookupValue).getStringValue();
            StringBuilder sb = new StringBuilder(lookupText.length());
            boolean containsWildcard = false;
            for (char c : lookupText.toCharArray()) {
                switch (c) {
                    case '*': 
                    case '?': 
                    case '~': {
                        containsWildcard = true;
                        break;
                    }
                    default: {
                        sb.append(c);
                    }
                }
                if (containsWildcard) break;
            }
            if (containsWildcard) {
                modifiedLookup = new StringEval(sb.toString());
            }
        }
        LookupValueComparer lookupComparer = LookupUtils.createTolerantLookupComparer(modifiedLookup, matchMode != MatchMode.WildcardMatch, true);
        int result = searchMode == SearchMode.BinarySearchForward ? LookupUtils.binarySearchIndexOfValue(lookupComparer, vector, matchMode, false) : (searchMode == SearchMode.BinarySearchBackward ? LookupUtils.binarySearchIndexOfValue(lookupComparer, vector, matchMode, true) : (searchMode == SearchMode.IterateBackward ? LookupUtils.lookupLastIndexOfValue(lookupComparer, vector, matchMode) : LookupUtils.lookupFirstIndexOfValue(lookupComparer, vector, matchMode)));
        if (result < 0) {
            throw new EvaluationException(ErrorEval.NA);
        }
        return result;
    }

    private static int lookupFirstIndexOfValue(LookupValueComparer lookupComparer, ValueVector vector, MatchMode matchMode) {
        return LookupUtils.lookupIndexOfValue(lookupComparer, vector, matchMode, false);
    }

    private static int lookupLastIndexOfValue(LookupValueComparer lookupComparer, ValueVector vector, MatchMode matchMode) {
        return LookupUtils.lookupIndexOfValue(lookupComparer, vector, matchMode, true);
    }

    private static int lookupIndexOfValue(LookupValueComparer lookupComparer, ValueVector vector, MatchMode matchMode, boolean reverse) {
        Iterator<Integer> idxIter;
        int bestMatchIdx = -1;
        ValueEval bestMatchEval = null;
        Iterator<Integer> iterator = idxIter = reverse ? vector.reverseIndexIterator() : vector.indexIterator();
        block4: while (idxIter.hasNext()) {
            int i = idxIter.next();
            ValueEval valueEval = vector.getItem(i);
            CompareResult result = lookupComparer.compareTo(valueEval);
            if (result.isEqual()) {
                return i;
            }
            switch (matchMode) {
                case ExactMatchFallbackToLargerValue: {
                    if (!result.isLessThan()) break;
                    if (bestMatchEval == null) {
                        bestMatchIdx = i;
                        bestMatchEval = valueEval;
                        break;
                    }
                    LookupValueComparer matchComparer = LookupUtils.createTolerantLookupComparer(valueEval, true, true);
                    if (!matchComparer.compareTo(bestMatchEval).isLessThan()) continue block4;
                    bestMatchIdx = i;
                    bestMatchEval = valueEval;
                    break;
                }
                case ExactMatchFallbackToSmallerValue: {
                    if (!result.isGreaterThan()) break;
                    if (bestMatchEval == null) {
                        bestMatchIdx = i;
                        bestMatchEval = valueEval;
                        break;
                    }
                    LookupValueComparer matchComparer = LookupUtils.createTolerantLookupComparer(valueEval, true, true);
                    if (!matchComparer.compareTo(bestMatchEval).isGreaterThan()) break;
                    bestMatchIdx = i;
                    bestMatchEval = valueEval;
                }
            }
        }
        return bestMatchIdx;
    }

    private static int binarySearchIndexOfValue(LookupValueComparer lookupComparer, ValueVector vector, MatchMode matchMode, boolean reverse) {
        int bestMatchIdx = -1;
        ValueEval bestMatchEval = null;
        HashSet<Integer> alreadySearched = new HashSet<Integer>();
        BinarySearchIndexes bsi = new BinarySearchIndexes(vector.getSize());
        int i;
        while ((i = bsi.getMidIx()) >= 0 && !alreadySearched.contains(i)) {
            alreadySearched.add(i);
            ValueEval valueEval = vector.getItem(i);
            CompareResult result = lookupComparer.compareTo(valueEval);
            if (result.isEqual()) {
                return i;
            }
            switch (matchMode) {
                case ExactMatchFallbackToLargerValue: {
                    if (!result.isLessThan()) break;
                    if (bestMatchEval == null) {
                        bestMatchIdx = i;
                        bestMatchEval = valueEval;
                        break;
                    }
                    LookupValueComparer matchComparer = LookupUtils.createTolerantLookupComparer(valueEval, true, true);
                    if (!matchComparer.compareTo(bestMatchEval).isLessThan()) break;
                    bestMatchIdx = i;
                    bestMatchEval = valueEval;
                    break;
                }
                case ExactMatchFallbackToSmallerValue: {
                    if (!result.isGreaterThan()) break;
                    if (bestMatchEval == null) {
                        bestMatchIdx = i;
                        bestMatchEval = valueEval;
                        break;
                    }
                    LookupValueComparer matchComparer = LookupUtils.createTolerantLookupComparer(valueEval, true, true);
                    if (!matchComparer.compareTo(bestMatchEval).isGreaterThan()) break;
                    bestMatchIdx = i;
                    bestMatchEval = valueEval;
                }
            }
            if (result.isTypeMismatch()) {
                int newIdx = LookupUtils.handleMidValueTypeMismatch(lookupComparer, vector, bsi, i, reverse);
                if (newIdx < 0) continue;
                return newIdx;
            }
            if (reverse) {
                bsi.narrowSearch(i, result.isGreaterThan());
                continue;
            }
            bsi.narrowSearch(i, result.isLessThan());
        }
        return bestMatchIdx;
    }

    private static int performBinarySearch(ValueVector vector, LookupValueComparer lookupComparer) {
        BinarySearchIndexes bsi = new BinarySearchIndexes(vector.getSize());
        int midIx;
        while ((midIx = bsi.getMidIx()) >= 0) {
            CompareResult cr = lookupComparer.compareTo(vector.getItem(midIx));
            if (cr.isTypeMismatch()) {
                int newMidIx = LookupUtils.handleMidValueTypeMismatch(lookupComparer, vector, bsi, midIx, false);
                if (newMidIx < 0) continue;
                midIx = newMidIx;
                cr = lookupComparer.compareTo(vector.getItem(midIx));
            }
            if (cr.isEqual()) {
                return LookupUtils.findLastIndexInRunOfEqualValues(lookupComparer, vector, midIx, bsi.getHighIx());
            }
            bsi.narrowSearch(midIx, cr.isLessThan());
        }
        return bsi.getLowIx();
    }

    private static int handleMidValueTypeMismatch(LookupValueComparer lookupComparer, ValueVector vector, BinarySearchIndexes bsi, int midIx, boolean reverse) {
        CompareResult cr;
        int newMid = midIx;
        int highIx = bsi.getHighIx();
        do {
            if (++newMid == highIx) {
                bsi.narrowSearch(midIx, true);
                return -1;
            }
            cr = lookupComparer.compareTo(vector.getItem(newMid));
            if (cr.isLessThan() && !reverse && newMid == highIx - 1) {
                bsi.narrowSearch(midIx, true);
                return -1;
            }
            if (!cr.isGreaterThan() || !reverse || newMid != highIx - 1) continue;
            bsi.narrowSearch(midIx, true);
            return -1;
        } while (cr.isTypeMismatch());
        if (cr.isEqual()) {
            return newMid;
        }
        if (reverse) {
            bsi.narrowSearch(newMid, cr.isGreaterThan());
        } else {
            bsi.narrowSearch(newMid, cr.isLessThan());
        }
        return -1;
    }

    private static int findLastIndexInRunOfEqualValues(LookupValueComparer lookupComparer, ValueVector vector, int firstFoundIndex, int maxIx) {
        for (int i = firstFoundIndex + 1; i < maxIx; ++i) {
            if (lookupComparer.compareTo(vector.getItem(i)).isEqual()) continue;
            return i - 1;
        }
        return maxIx - 1;
    }

    static LookupValueComparer createLookupComparer(ValueEval lookupValue, boolean matchExact, boolean isMatchFunction) {
        if (lookupValue == BlankEval.instance) {
            return new NumberLookupComparer(NumberEval.ZERO);
        }
        if (lookupValue instanceof StringEval) {
            return new StringLookupComparer((StringEval)lookupValue, matchExact, isMatchFunction);
        }
        if (lookupValue instanceof NumberEval) {
            return new NumberLookupComparer((NumberEval)lookupValue);
        }
        if (lookupValue instanceof BoolEval) {
            return new BooleanLookupComparer((BoolEval)lookupValue);
        }
        throw new IllegalArgumentException("Bad lookup value type (" + lookupValue.getClass().getName() + ")");
    }

    private static LookupValueComparer createTolerantLookupComparer(ValueEval lookupValue, boolean matchExact, boolean isMatchFunction) {
        if (lookupValue == BlankEval.instance) {
            return new TolerantStringLookupComparer((ValueEval)new StringEval(""), matchExact, isMatchFunction);
        }
        if (lookupValue instanceof BoolEval) {
            return new BooleanLookupComparer((BoolEval)lookupValue);
        }
        if (matchExact && lookupValue instanceof NumberEval) {
            return new NumberLookupComparer((NumberEval)lookupValue);
        }
        return new TolerantStringLookupComparer(lookupValue, matchExact, isMatchFunction);
    }

    static {
        for (MatchMode matchMode : MatchMode.values()) {
            matchModeMap.put(matchMode.getIntValue(), matchMode);
        }
        for (Enum enum_ : SearchMode.values()) {
            searchModeMap.put(((SearchMode)enum_).getIntValue(), (SearchMode)enum_);
        }
    }

    private static final class BinarySearchIndexes {
        private int _lowIx = -1;
        private int _highIx;

        public BinarySearchIndexes(int highIx) {
            this._highIx = highIx;
        }

        public int getMidIx() {
            int ixDiff = this._highIx - this._lowIx;
            if (ixDiff < 2) {
                return -1;
            }
            return this._lowIx + ixDiff / 2;
        }

        public int getLowIx() {
            return this._lowIx;
        }

        public int getHighIx() {
            return this._highIx;
        }

        public void narrowSearch(int midIx, boolean isLessThan) {
            if (isLessThan) {
                this._highIx = midIx;
            } else {
                this._lowIx = midIx;
            }
        }
    }

    private static final class BooleanLookupComparer
    extends LookupValueComparerBase {
        private final boolean _value;

        protected BooleanLookupComparer(BoolEval be) {
            super(be);
            this._value = be.getBooleanValue();
        }

        @Override
        protected CompareResult compareSameType(ValueEval other) {
            BoolEval be = (BoolEval)other;
            boolean otherVal = be.getBooleanValue();
            if (this._value == otherVal) {
                return CompareResult.EQUAL;
            }
            if (this._value) {
                return CompareResult.GREATER_THAN;
            }
            return CompareResult.LESS_THAN;
        }

        @Override
        protected String getValueAsString() {
            return String.valueOf(this._value);
        }
    }

    private static final class NumberLookupComparer
    extends LookupValueComparerBase {
        private final double _value;

        protected NumberLookupComparer(NumberEval ne) {
            super(ne);
            this._value = ne.getNumberValue();
        }

        @Override
        protected CompareResult compareSameType(ValueEval other) {
            NumberEval ne = (NumberEval)other;
            return CompareResult.valueOf(Double.compare(this._value, ne.getNumberValue()));
        }

        @Override
        protected String getValueAsString() {
            return String.valueOf(this._value);
        }
    }

    private static final class TolerantStringLookupComparer
    extends StringLookupComparer {
        static StringEval convertToStringEval(ValueEval eval) {
            if (eval instanceof StringEval) {
                return (StringEval)eval;
            }
            String sv = OperandResolver.coerceValueToString(eval);
            return new StringEval(sv);
        }

        protected TolerantStringLookupComparer(ValueEval eval, boolean matchExact, boolean isMatchFunction) {
            super(TolerantStringLookupComparer.convertToStringEval(eval), matchExact, isMatchFunction);
        }

        @Override
        protected String convertToString(ValueEval other) {
            return OperandResolver.coerceValueToString(other);
        }
    }

    private static class StringLookupComparer
    extends LookupValueComparerBase {
        protected final String _value;
        protected final Pattern _wildCardPattern;
        protected final boolean _matchExact;
        protected final boolean _isMatchFunction;

        protected StringLookupComparer(StringEval se, boolean matchExact, boolean isMatchFunction) {
            super(se);
            this._value = se.getStringValue();
            this._wildCardPattern = Countif.StringMatcher.getWildCardPattern(this._value);
            this._matchExact = matchExact;
            this._isMatchFunction = isMatchFunction;
        }

        protected String convertToString(ValueEval other) {
            StringEval se = (StringEval)other;
            return se.getStringValue();
        }

        @Override
        protected CompareResult compareSameType(ValueEval other) {
            String stringValue = this.convertToString(other);
            if (this._wildCardPattern != null && (this._isMatchFunction || !this._matchExact)) {
                Matcher matcher = this._wildCardPattern.matcher(stringValue);
                boolean matches = matcher.matches();
                return CompareResult.valueOf(matches);
            }
            return CompareResult.valueOf(this._value.compareToIgnoreCase(stringValue));
        }

        @Override
        protected String getValueAsString() {
            return this._value;
        }
    }

    private static abstract class LookupValueComparerBase
    implements LookupValueComparer {
        private final Class<? extends ValueEval> _targetClass;

        protected LookupValueComparerBase(ValueEval targetValue) {
            if (targetValue == null) {
                throw new RuntimeException("targetValue cannot be null");
            }
            this._targetClass = targetValue.getClass();
        }

        @Override
        public final CompareResult compareTo(ValueEval other) {
            if (other == null) {
                throw new RuntimeException("compare to value cannot be null");
            }
            if (this._targetClass != other.getClass()) {
                return CompareResult.TYPE_MISMATCH;
            }
            return this.compareSameType(other);
        }

        public String toString() {
            return this.getClass().getName() + " [" + this.getValueAsString() + "]";
        }

        protected abstract CompareResult compareSameType(ValueEval var1);

        protected abstract String getValueAsString();
    }

    public static interface LookupValueComparer {
        public CompareResult compareTo(ValueEval var1);
    }

    public static final class CompareResult {
        private final boolean _isTypeMismatch;
        private final boolean _isLessThan;
        private final boolean _isEqual;
        private final boolean _isGreaterThan;
        public static final CompareResult TYPE_MISMATCH = new CompareResult(true, 0);
        public static final CompareResult LESS_THAN = new CompareResult(false, -1);
        public static final CompareResult EQUAL = new CompareResult(false, 0);
        public static final CompareResult GREATER_THAN = new CompareResult(false, 1);

        private CompareResult(boolean isTypeMismatch, int simpleCompareResult) {
            if (isTypeMismatch) {
                this._isTypeMismatch = true;
                this._isLessThan = false;
                this._isEqual = false;
                this._isGreaterThan = false;
            } else {
                this._isTypeMismatch = false;
                this._isLessThan = simpleCompareResult < 0;
                this._isEqual = simpleCompareResult == 0;
                this._isGreaterThan = simpleCompareResult > 0;
            }
        }

        public static CompareResult valueOf(int simpleCompareResult) {
            if (simpleCompareResult < 0) {
                return LESS_THAN;
            }
            if (simpleCompareResult > 0) {
                return GREATER_THAN;
            }
            return EQUAL;
        }

        public static CompareResult valueOf(boolean matches) {
            if (matches) {
                return EQUAL;
            }
            return LESS_THAN;
        }

        public boolean isTypeMismatch() {
            return this._isTypeMismatch;
        }

        public boolean isLessThan() {
            return this._isLessThan;
        }

        public boolean isEqual() {
            return this._isEqual;
        }

        public boolean isGreaterThan() {
            return this._isGreaterThan;
        }

        public String toString() {
            return this.getClass().getName() + " [" + this.formatAsString() + "]";
        }

        private String formatAsString() {
            if (this._isTypeMismatch) {
                return "TYPE_MISMATCH";
            }
            if (this._isLessThan) {
                return "LESS_THAN";
            }
            if (this._isEqual) {
                return "EQUAL";
            }
            if (this._isGreaterThan) {
                return "GREATER_THAN";
            }
            return "??error??";
        }
    }

    private static final class SheetVector
    implements ValueVector {
        private final RefEval _re;
        private final int _size;

        public SheetVector(RefEval re) {
            this._size = re.getNumberOfSheets();
            this._re = re;
        }

        @Override
        public ValueEval getItem(int index) {
            if (index >= this._size) {
                throw new ArrayIndexOutOfBoundsException("Specified index (" + index + ") is outside the allowed range (0.." + (this._size - 1) + ")");
            }
            int sheetIndex = this._re.getFirstSheetIndex() + index;
            return this._re.getInnerValueEval(sheetIndex);
        }

        @Override
        public int getSize() {
            return this._size;
        }
    }

    private static final class ColumnVector
    implements ValueVector {
        private final TwoDEval _tableArray;
        private final int _size;
        private final int _columnIndex;

        public ColumnVector(TwoDEval tableArray, int columnIndex) {
            this._columnIndex = columnIndex;
            int lastColIx = tableArray.getWidth() - 1;
            if (columnIndex < 0 || columnIndex > lastColIx) {
                throw new IllegalArgumentException("Specified column index (" + columnIndex + ") is outside the allowed range (0.." + lastColIx + ")");
            }
            this._tableArray = tableArray;
            this._size = this._tableArray.getHeight();
        }

        @Override
        public ValueEval getItem(int index) {
            if (index > this._size) {
                throw new ArrayIndexOutOfBoundsException("Specified index (" + index + ") is outside the allowed range (0.." + (this._size - 1) + ")");
            }
            return this._tableArray.getValue(index, this._columnIndex);
        }

        @Override
        public int getSize() {
            return this._size;
        }
    }

    private static final class RowVector
    implements ValueVector {
        private final TwoDEval _tableArray;
        private final int _size;
        private final int _rowIndex;

        public RowVector(TwoDEval tableArray, int rowIndex) {
            this._rowIndex = rowIndex;
            int lastRowIx = tableArray.getHeight() - 1;
            if (rowIndex < 0 || rowIndex > lastRowIx) {
                throw new IllegalArgumentException("Specified row index (" + rowIndex + ") is outside the allowed range (0.." + lastRowIx + ")");
            }
            this._tableArray = tableArray;
            this._size = tableArray.getWidth();
        }

        @Override
        public ValueEval getItem(int index) {
            if (index > this._size) {
                throw new ArrayIndexOutOfBoundsException("Specified index (" + index + ") is outside the allowed range (0.." + (this._size - 1) + ")");
            }
            return this._tableArray.getValue(this._rowIndex, index);
        }

        @Override
        public int getSize() {
            return this._size;
        }
    }

    public static interface ValueVector {
        public ValueEval getItem(int var1);

        public int getSize();

        default public Iterator<Integer> indexIterator() {
            return new Iterator<Integer>(){
                private int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < this.getSize();
                }

                @Override
                public Integer next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return this.pos++;
                }
            };
        }

        default public Iterator<Integer> reverseIndexIterator() {
            return new Iterator<Integer>(){
                private int pos;
                {
                    this.pos = this.getSize() - 1;
                }

                @Override
                public boolean hasNext() {
                    return this.pos > 0;
                }

                @Override
                public Integer next() {
                    --this.pos;
                    if (this.pos < 0) {
                        throw new NoSuchElementException();
                    }
                    return this.pos;
                }
            };
        }
    }

    public static enum SearchMode {
        IterateForward(1),
        IterateBackward(-1),
        BinarySearchForward(2),
        BinarySearchBackward(-2);

        private final int intValue;

        private SearchMode(int intValue) {
            this.intValue = intValue;
        }

        public int getIntValue() {
            return this.intValue;
        }
    }

    public static enum MatchMode {
        ExactMatch(0),
        ExactMatchFallbackToSmallerValue(-1),
        ExactMatchFallbackToLargerValue(1),
        WildcardMatch(2);

        private final int intValue;

        private MatchMode(int intValue) {
            this.intValue = intValue;
        }

        public int getIntValue() {
            return this.intValue;
        }
    }
}

