/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.util.function.Supplier;
import java.util.regex.Pattern;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.StringValueEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Countif;
import org.apache.poi.ss.formula.functions.DAverage;
import org.apache.poi.ss.formula.functions.DCount;
import org.apache.poi.ss.formula.functions.DCountA;
import org.apache.poi.ss.formula.functions.DGet;
import org.apache.poi.ss.formula.functions.DMax;
import org.apache.poi.ss.formula.functions.DMin;
import org.apache.poi.ss.formula.functions.DProduct;
import org.apache.poi.ss.formula.functions.DStdev;
import org.apache.poi.ss.formula.functions.DStdevp;
import org.apache.poi.ss.formula.functions.DSum;
import org.apache.poi.ss.formula.functions.DVar;
import org.apache.poi.ss.formula.functions.DVarp;
import org.apache.poi.ss.formula.functions.Function3Arg;
import org.apache.poi.ss.formula.functions.IDStarAlgorithm;
import org.apache.poi.ss.util.NumberComparer;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LocaleUtil;

@Internal
public final class DStarRunner
implements Function3Arg {
    private final DStarAlgorithmEnum algoType;

    public DStarRunner(DStarAlgorithmEnum algorithm) {
        this.algoType = algorithm;
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        if (args.length == 3) {
            return this.evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2]);
        }
        return ErrorEval.VALUE_INVALID;
    }

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval database, ValueEval filterColumn, ValueEval conditionDatabase) {
        int fc;
        IDStarAlgorithm algorithm;
        AreaEval cdb;
        AreaEval db;
        block10: {
            if (!(database instanceof AreaEval) || !(conditionDatabase instanceof AreaEval)) {
                return ErrorEval.VALUE_INVALID;
            }
            db = (AreaEval)database;
            cdb = (AreaEval)conditionDatabase;
            algorithm = this.algoType.newInstance();
            fc = -1;
            try {
                filterColumn = OperandResolver.getSingleValue(filterColumn, srcRowIndex, srcColumnIndex);
                fc = filterColumn instanceof NumericValueEval ? (int)Math.round(((NumericValueEval)filterColumn).getNumberValue()) - 1 : DStarRunner.getColumnForName(filterColumn, db);
                if (fc == -1 && !algorithm.allowEmptyMatchField()) {
                    return ErrorEval.VALUE_INVALID;
                }
            }
            catch (EvaluationException e) {
                if (!algorithm.allowEmptyMatchField()) {
                    return e.getErrorEval();
                }
            }
            catch (Exception e) {
                if (algorithm.allowEmptyMatchField()) break block10;
                return ErrorEval.VALUE_INVALID;
            }
        }
        int height = db.getHeight();
        for (int row = 1; row < height; ++row) {
            boolean shouldContinue;
            boolean matches;
            try {
                matches = DStarRunner.fulfillsConditions(db, row, cdb);
            }
            catch (EvaluationException e) {
                return ErrorEval.VALUE_INVALID;
            }
            if (!matches) continue;
            ValueEval currentValueEval = DStarRunner.resolveReference(db, row, fc);
            if (fc < 0 && algorithm.allowEmptyMatchField() && !(currentValueEval instanceof NumericValueEval)) {
                currentValueEval = NumberEval.ZERO;
            }
            if (!(shouldContinue = algorithm.processMatch(currentValueEval))) break;
        }
        return algorithm.getResult();
    }

    private static int getColumnForName(ValueEval nameValueEval, AreaEval db) throws EvaluationException {
        if (nameValueEval instanceof NumericValueEval) {
            int columnNo = OperandResolver.coerceValueToInt(nameValueEval) - 1;
            if (columnNo < 0 || columnNo >= db.getWidth()) {
                return -1;
            }
            return columnNo;
        }
        String name = OperandResolver.coerceValueToString(nameValueEval);
        return DStarRunner.getColumnForString(db, name);
    }

    private static int getColumnForString(AreaEval db, String name) {
        int resultColumn = -1;
        int width = db.getWidth();
        for (int column = 0; column < width; ++column) {
            String columnName;
            ValueEval columnNameValueEval = DStarRunner.resolveReference(db, 0, column);
            if (columnNameValueEval instanceof BlankEval || columnNameValueEval instanceof ErrorEval || !name.equalsIgnoreCase(columnName = OperandResolver.coerceValueToString(columnNameValueEval))) continue;
            resultColumn = column;
            break;
        }
        return resultColumn;
    }

    private static boolean fulfillsConditions(AreaEval db, int row, AreaEval cdb) throws EvaluationException {
        int height = cdb.getHeight();
        for (int conditionRow = 1; conditionRow < height; ++conditionRow) {
            boolean matches = true;
            int width = cdb.getWidth();
            for (int column = 0; column < width; ++column) {
                boolean columnCondition = true;
                ValueEval condition = DStarRunner.resolveReference(cdb, conditionRow, column);
                if (condition instanceof BlankEval) continue;
                ValueEval targetHeader = DStarRunner.resolveReference(cdb, 0, column);
                if (!(targetHeader instanceof StringValueEval)) {
                    throw new EvaluationException(ErrorEval.VALUE_INVALID);
                }
                if (DStarRunner.getColumnForName(targetHeader, db) == -1) {
                    columnCondition = false;
                }
                if (columnCondition) {
                    ValueEval value = DStarRunner.resolveReference(db, row, DStarRunner.getColumnForName(targetHeader, db));
                    if (DStarRunner.testNormalCondition(value, condition)) continue;
                    matches = false;
                    break;
                }
                if (OperandResolver.coerceValueToString(condition).isEmpty()) {
                    throw new EvaluationException(ErrorEval.VALUE_INVALID);
                }
                throw new NotImplementedException("D* function with formula conditions");
            }
            if (!matches) continue;
            return true;
        }
        return false;
    }

    private static boolean testNormalCondition(ValueEval value, ValueEval condition) throws EvaluationException {
        if (condition instanceof StringEval) {
            String conditionString = ((StringEval)condition).getStringValue();
            if (conditionString.startsWith("<")) {
                String number = conditionString.substring(1);
                if (number.startsWith("=")) {
                    number = number.substring(1);
                    return DStarRunner.testNumericCondition(value, operator.smallerEqualThan, number);
                }
                return DStarRunner.testNumericCondition(value, operator.smallerThan, number);
            }
            if (conditionString.startsWith(">")) {
                String number = conditionString.substring(1);
                if (number.startsWith("=")) {
                    number = number.substring(1);
                    return DStarRunner.testNumericCondition(value, operator.largerEqualThan, number);
                }
                return DStarRunner.testNumericCondition(value, operator.largerThan, number);
            }
            if (conditionString.startsWith("=")) {
                boolean itsANumber;
                String stringOrNumber = conditionString.substring(1);
                if (stringOrNumber.isEmpty()) {
                    return value instanceof BlankEval;
                }
                try {
                    Integer.parseInt(stringOrNumber);
                    itsANumber = true;
                }
                catch (NumberFormatException e) {
                    try {
                        Double.parseDouble(stringOrNumber);
                        itsANumber = true;
                    }
                    catch (NumberFormatException e2) {
                        itsANumber = false;
                    }
                }
                if (itsANumber) {
                    return DStarRunner.testNumericCondition(value, operator.equal, stringOrNumber);
                }
                String valueString = value instanceof BlankEval ? "" : OperandResolver.coerceValueToString(value);
                return stringOrNumber.equalsIgnoreCase(valueString);
            }
            if (conditionString.isEmpty()) {
                return value instanceof StringEval;
            }
            String valueString = value instanceof BlankEval ? "" : OperandResolver.coerceValueToString(value);
            String lowerValue = valueString.toLowerCase(LocaleUtil.getUserLocale());
            String lowerCondition = conditionString.toLowerCase(LocaleUtil.getUserLocale());
            Pattern pattern = Countif.StringMatcher.getWildCardPattern(lowerCondition);
            if (pattern == null) {
                return lowerValue.startsWith(lowerCondition);
            }
            return pattern.matcher(lowerValue).matches();
        }
        if (condition instanceof NumericValueEval) {
            double conditionNumber = ((NumericValueEval)condition).getNumberValue();
            Double valueNumber = DStarRunner.getNumberFromValueEval(value);
            return valueNumber != null && conditionNumber == valueNumber;
        }
        if (condition instanceof ErrorEval) {
            if (value instanceof ErrorEval) {
                return ((ErrorEval)condition).getErrorCode() == ((ErrorEval)value).getErrorCode();
            }
            return false;
        }
        return false;
    }

    private static boolean testNumericCondition(ValueEval valueEval, operator op, String condition) throws EvaluationException {
        double conditionValue;
        if (!(valueEval instanceof NumericValueEval)) {
            return false;
        }
        double value = ((NumericValueEval)valueEval).getNumberValue();
        try {
            conditionValue = Integer.parseInt(condition);
        }
        catch (NumberFormatException e) {
            try {
                conditionValue = Double.parseDouble(condition);
            }
            catch (NumberFormatException e2) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
        }
        int result = NumberComparer.compare(value, conditionValue);
        switch (op) {
            case largerThan: {
                return result > 0;
            }
            case largerEqualThan: {
                return result >= 0;
            }
            case smallerThan: {
                return result < 0;
            }
            case smallerEqualThan: {
                return result <= 0;
            }
            case equal: {
                return result == 0;
            }
        }
        return false;
    }

    private static Double getNumberFromValueEval(ValueEval value) {
        if (value instanceof NumericValueEval) {
            return ((NumericValueEval)value).getNumberValue();
        }
        if (value instanceof StringValueEval) {
            String stringValue = ((StringValueEval)value).getStringValue();
            try {
                return Double.parseDouble(stringValue);
            }
            catch (NumberFormatException e2) {
                return null;
            }
        }
        return null;
    }

    private static ValueEval resolveReference(AreaEval db, int dbRow, int dbCol) {
        try {
            return OperandResolver.getSingleValue(db.getValue(dbRow, dbCol), db.getFirstRow() + dbRow, db.getFirstColumn() + dbCol);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static enum operator {
        largerThan,
        largerEqualThan,
        smallerThan,
        smallerEqualThan,
        equal;

    }

    public static enum DStarAlgorithmEnum {
        DGET(DGet::new),
        DMIN(DMin::new),
        DMAX(DMax::new),
        DSUM(DSum::new),
        DCOUNT(DCount::new),
        DCOUNTA(DCountA::new),
        DAVERAGE(DAverage::new),
        DSTDEV(DStdev::new),
        DSTDEVP(DStdevp::new),
        DVAR(DVar::new),
        DVARP(DVarp::new),
        DPRODUCT(DProduct::new);

        private final Supplier<IDStarAlgorithm> implSupplier;

        private DStarAlgorithmEnum(Supplier<IDStarAlgorithm> implSupplier) {
            this.implSupplier = implSupplier;
        }

        public IDStarAlgorithm newInstance() {
            return this.implSupplier.get();
        }
    }
}

