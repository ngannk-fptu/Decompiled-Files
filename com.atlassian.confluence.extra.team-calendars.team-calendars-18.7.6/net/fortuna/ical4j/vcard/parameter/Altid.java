/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.parameter;

import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.ParameterFactory;

public final class Altid
extends Parameter {
    private static final long serialVersionUID = 12345L;
    public static final ParameterFactory<Altid> FACTORY = new Factory();
    private String value;

    public Altid(String value) {
        super(Parameter.Id.ALTID);
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements ParameterFactory<Altid> {
        private Factory() {
        }

        @Override
        public Altid createParameter(String value) {
            return new Altid(value);
        }
    }
}

