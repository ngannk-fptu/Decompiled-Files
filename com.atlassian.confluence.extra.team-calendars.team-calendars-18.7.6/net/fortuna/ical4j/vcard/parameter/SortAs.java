/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.parameter;

import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.ParameterFactory;

public final class SortAs
extends Parameter {
    private static final long serialVersionUID = 12345L;
    public static final ParameterFactory<SortAs> FACTORY = new Factory();
    private String value;
    private String[] segments;

    public SortAs(String value) {
        super(Parameter.Id.SORT_AS);
        this.value = value;
        this.segments = value.split(";", -1);
    }

    public String getValue() {
        return this.value;
    }

    public String[] getSegments() {
        return this.segments;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements ParameterFactory<SortAs> {
        private Factory() {
        }

        @Override
        public SortAs createParameter(String value) {
            return new SortAs(value);
        }
    }
}

