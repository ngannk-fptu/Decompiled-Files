/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.selectors.BaseExtendSelector;

public class SizeSelector
extends BaseExtendSelector {
    private static final int KILO = 1000;
    private static final int KIBI = 1024;
    private static final int KIBI_POS = 4;
    private static final int MEGA = 1000000;
    private static final int MEGA_POS = 9;
    private static final int MEBI = 0x100000;
    private static final int MEBI_POS = 13;
    private static final long GIGA = 1000000000L;
    private static final int GIGA_POS = 18;
    private static final long GIBI = 0x40000000L;
    private static final int GIBI_POS = 22;
    private static final long TERA = 1000000000000L;
    private static final int TERA_POS = 27;
    private static final long TEBI = 0x10000000000L;
    private static final int TEBI_POS = 31;
    private static final int END_POS = 36;
    public static final String SIZE_KEY = "value";
    public static final String UNITS_KEY = "units";
    public static final String WHEN_KEY = "when";
    private long size = -1L;
    private long multiplier = 1L;
    private long sizelimit = -1L;
    private Comparison when = Comparison.EQUAL;

    @Override
    public String toString() {
        return String.format("{sizeselector value: %d compare: %s}", this.sizelimit, this.when.getValue());
    }

    public void setValue(long size) {
        this.size = size;
        if (this.multiplier != 0L && size > -1L) {
            this.sizelimit = size * this.multiplier;
        }
    }

    public void setUnits(ByteUnits units) {
        int i = units.getIndex();
        this.multiplier = 0L;
        if (i > -1 && i < 4) {
            this.multiplier = 1000L;
        } else if (i < 9) {
            this.multiplier = 1024L;
        } else if (i < 13) {
            this.multiplier = 1000000L;
        } else if (i < 18) {
            this.multiplier = 0x100000L;
        } else if (i < 22) {
            this.multiplier = 1000000000L;
        } else if (i < 27) {
            this.multiplier = 0x40000000L;
        } else if (i < 31) {
            this.multiplier = 1000000000000L;
        } else if (i < 36) {
            this.multiplier = 0x10000000000L;
        }
        if (this.multiplier > 0L && this.size > -1L) {
            this.sizelimit = this.size * this.multiplier;
        }
    }

    public void setWhen(SizeComparisons when) {
        this.when = when;
    }

    @Override
    public void setParameters(Parameter ... parameters) {
        super.setParameters(parameters);
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                String paramname = parameter.getName();
                if (SIZE_KEY.equalsIgnoreCase(paramname)) {
                    try {
                        this.setValue(Long.parseLong(parameter.getValue()));
                    }
                    catch (NumberFormatException nfe) {
                        this.setError("Invalid size setting " + parameter.getValue());
                    }
                    continue;
                }
                if (UNITS_KEY.equalsIgnoreCase(paramname)) {
                    ByteUnits units = new ByteUnits();
                    units.setValue(parameter.getValue());
                    this.setUnits(units);
                    continue;
                }
                if (WHEN_KEY.equalsIgnoreCase(paramname)) {
                    SizeComparisons scmp = new SizeComparisons();
                    scmp.setValue(parameter.getValue());
                    this.setWhen(scmp);
                    continue;
                }
                this.setError("Invalid parameter " + paramname);
            }
        }
    }

    @Override
    public void verifySettings() {
        if (this.size < 0L) {
            this.setError("The value attribute is required, and must be positive");
        } else if (this.multiplier < 1L) {
            this.setError("Invalid Units supplied, must be K,Ki,M,Mi,G,Gi,T,or Ti");
        } else if (this.sizelimit < 0L) {
            this.setError("Internal error: Code is not setting sizelimit correctly");
        }
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        this.validate();
        if (file.isDirectory()) {
            return true;
        }
        long diff = file.length() - this.sizelimit;
        return this.when.evaluate(diff == 0L ? 0 : (int)(diff / Math.abs(diff)));
    }

    public static class ByteUnits
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"K", "k", "kilo", "KILO", "Ki", "KI", "ki", "kibi", "KIBI", "M", "m", "mega", "MEGA", "Mi", "MI", "mi", "mebi", "MEBI", "G", "g", "giga", "GIGA", "Gi", "GI", "gi", "gibi", "GIBI", "T", "t", "tera", "TERA", "Ti", "TI", "ti", "tebi", "TEBI"};
        }
    }

    public static class SizeComparisons
    extends Comparison {
    }
}

