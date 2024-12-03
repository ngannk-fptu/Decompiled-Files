/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

public interface ConditionalFormattingThreshold {
    public RangeType getRangeType();

    public void setRangeType(RangeType var1);

    public String getFormula();

    public void setFormula(String var1);

    public Double getValue();

    public void setValue(Double var1);

    public static enum RangeType {
        NUMBER(1, "num"),
        MIN(2, "min"),
        MAX(3, "max"),
        PERCENT(4, "percent"),
        PERCENTILE(5, "percentile"),
        UNALLOCATED(6, null),
        FORMULA(7, "formula");

        public final int id;
        public final String name;

        public String toString() {
            return this.id + " - " + this.name;
        }

        public static RangeType byId(int id) {
            if (id <= 0 || id > RangeType.values().length) {
                return null;
            }
            return RangeType.values()[id - 1];
        }

        public static RangeType byName(String name) {
            for (RangeType t : RangeType.values()) {
                if (t.name == null && name == null) {
                    return t;
                }
                if (t.name == null || !t.name.equals(name)) continue;
                return t;
            }
            return null;
        }

        private RangeType(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

