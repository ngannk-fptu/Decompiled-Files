/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.parameter;

import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.ParameterFactory;

public final class Calscale
extends Parameter {
    private static final long serialVersionUID = 12345L;
    public static final ParameterFactory<Calscale> FACTORY = new Factory();
    private String value;

    public Calscale(String value) {
        super(Parameter.Id.CALSCALE);
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements ParameterFactory<Calscale> {
        private Factory() {
        }

        @Override
        public Calscale createParameter(String value) {
            return new Calscale(value);
        }
    }
}

