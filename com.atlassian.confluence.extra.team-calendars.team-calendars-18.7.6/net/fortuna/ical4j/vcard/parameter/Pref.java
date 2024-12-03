/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.parameter;

import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.ParameterFactory;

public final class Pref
extends Parameter {
    private static final long serialVersionUID = -6246880477846039737L;
    public static final Pref PREF = new Pref();
    public static final ParameterFactory<Pref> FACTORY = new Factory();
    private final Integer level;

    public Pref(String value) {
        this(Integer.valueOf(value));
    }

    public Pref(Integer level) {
        super(Parameter.Id.PREF);
        if (level <= 0) {
            throw new IllegalArgumentException("The level of preferredness must be a positive integer");
        }
        this.level = level;
    }

    private Pref() {
        super(Parameter.Id.PREF);
        this.level = null;
    }

    public String getValue() {
        if (this.level != null) {
            return this.level.toString();
        }
        return null;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements ParameterFactory<Pref> {
        private Factory() {
        }

        @Override
        public Pref createParameter(String value) {
            if (value == null && CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed")) {
                return PREF;
            }
            return new Pref(value);
        }
    }
}

