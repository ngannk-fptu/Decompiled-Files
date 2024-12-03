/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula;

import org.apache.poi.util.Internal;

@Internal
enum OperatorEnum {
    NO_COMPARISON(OperatorEnum::noComp, false),
    BETWEEN(OperatorEnum::between, false),
    NOT_BETWEEN(OperatorEnum::notBetween, true),
    EQUAL(OperatorEnum::equalCheck, false),
    NOT_EQUAL(OperatorEnum::notEqual, true),
    GREATER_THAN(OperatorEnum::greaterThan, false),
    LESS_THAN(OperatorEnum::lessThan, false),
    GREATER_OR_EQUAL(OperatorEnum::greaterOrEqual, false),
    LESS_OR_EQUAL(OperatorEnum::lessOrEqual, false);

    private final CompareOp compareOp;
    private final boolean validForIncompatibleTypes;

    private OperatorEnum(CompareOp compareOp, boolean validForIncompatibleTypes) {
        this.compareOp = compareOp;
        this.validForIncompatibleTypes = validForIncompatibleTypes;
    }

    <C extends Comparable<C>> boolean isValid(C cellValue, C v1, C v2) {
        return this.compareOp.isValid(cellValue, v1, v2);
    }

    boolean isValidForIncompatibleTypes() {
        return this.validForIncompatibleTypes;
    }

    private static <C extends Comparable<C>> boolean noComp(C cellValue, C v1, C v2) {
        return false;
    }

    private static <C extends Comparable<C>> boolean between(C cellValue, C v1, C v2) {
        if (v1 == null) {
            if (cellValue instanceof Number) {
                double n1 = 0.0;
                double n2 = v2 == null ? 0.0 : ((Number)((Object)v2)).doubleValue();
                return Double.compare(((Number)((Object)cellValue)).doubleValue(), n1) >= 0 && Double.compare(((Number)((Object)cellValue)).doubleValue(), n2) <= 0;
            }
            if (cellValue instanceof String) {
                String n1 = "";
                String n2 = v2 == null ? "" : (String)((Object)v2);
                return ((String)((Object)cellValue)).compareToIgnoreCase(n1) >= 0 && ((String)((Object)cellValue)).compareToIgnoreCase(n2) <= 0;
            }
            if (cellValue instanceof Boolean) {
                return false;
            }
            return false;
        }
        return cellValue.compareTo(v1) >= 0 && cellValue.compareTo(v2) <= 0;
    }

    private static <C extends Comparable<C>> boolean notBetween(C cellValue, C v1, C v2) {
        if (v1 == null) {
            if (cellValue instanceof Number) {
                double n1 = 0.0;
                double n2 = v2 == null ? 0.0 : ((Number)((Object)v2)).doubleValue();
                return Double.compare(((Number)((Object)cellValue)).doubleValue(), n1) < 0 || Double.compare(((Number)((Object)cellValue)).doubleValue(), n2) > 0;
            }
            if (cellValue instanceof String) {
                String n1 = "";
                String n2 = v2 == null ? "" : (String)((Object)v2);
                return ((String)((Object)cellValue)).compareToIgnoreCase(n1) < 0 || ((String)((Object)cellValue)).compareToIgnoreCase(n2) > 0;
            }
            return cellValue instanceof Boolean;
        }
        return cellValue.compareTo(v1) < 0 || cellValue.compareTo(v2) > 0;
    }

    private static <C extends Comparable<C>> boolean equalCheck(C cellValue, C v1, C v2) {
        if (v1 == null) {
            if (cellValue instanceof Number) {
                return Double.compare(((Number)((Object)cellValue)).doubleValue(), 0.0) == 0;
            }
            if (cellValue instanceof String) {
                return false;
            }
            if (cellValue instanceof Boolean) {
                return false;
            }
            return false;
        }
        if (cellValue instanceof String) {
            return cellValue.toString().compareToIgnoreCase(v1.toString()) == 0;
        }
        return cellValue.compareTo(v1) == 0;
    }

    private static <C extends Comparable<C>> boolean notEqual(C cellValue, C v1, C v2) {
        if (v1 == null) {
            return true;
        }
        if (cellValue instanceof String) {
            return cellValue.toString().compareToIgnoreCase(v1.toString()) == 0;
        }
        return cellValue.compareTo(v1) != 0;
    }

    private static <C extends Comparable<C>> boolean greaterThan(C cellValue, C v1, C v2) {
        if (v1 == null) {
            if (cellValue instanceof Number) {
                return Double.compare(((Number)((Object)cellValue)).doubleValue(), 0.0) > 0;
            }
            if (cellValue instanceof String) {
                return true;
            }
            return cellValue instanceof Boolean;
        }
        return cellValue.compareTo(v1) > 0;
    }

    private static <C extends Comparable<C>> boolean lessThan(C cellValue, C v1, C v2) {
        if (v1 == null) {
            if (cellValue instanceof Number) {
                return Double.compare(((Number)((Object)cellValue)).doubleValue(), 0.0) < 0;
            }
            if (cellValue instanceof String) {
                return false;
            }
            if (cellValue instanceof Boolean) {
                return false;
            }
            return false;
        }
        return cellValue.compareTo(v1) < 0;
    }

    private static <C extends Comparable<C>> boolean greaterOrEqual(C cellValue, C v1, C v2) {
        if (v1 == null) {
            if (cellValue instanceof Number) {
                return Double.compare(((Number)((Object)cellValue)).doubleValue(), 0.0) >= 0;
            }
            if (cellValue instanceof String) {
                return true;
            }
            return cellValue instanceof Boolean;
        }
        return cellValue.compareTo(v1) >= 0;
    }

    private static <C extends Comparable<C>> boolean lessOrEqual(C cellValue, C v1, C v2) {
        if (v1 == null) {
            if (cellValue instanceof Number) {
                return Double.compare(((Number)((Object)cellValue)).doubleValue(), 0.0) <= 0;
            }
            if (cellValue instanceof String) {
                return false;
            }
            if (cellValue instanceof Boolean) {
                return false;
            }
            return false;
        }
        return cellValue.compareTo(v1) <= 0;
    }

    private static interface CompareOp {
        public <C extends Comparable<C>> boolean isValid(C var1, C var2, C var3);
    }
}

