/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.parameter;

import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.ParameterFactory;

public final class Encoding
extends Parameter {
    private static final long serialVersionUID = -6912042752317640817L;
    public static final Encoding B = new Encoding("b");
    public static final ParameterFactory<Encoding> FACTORY = new Factory();
    private final String value;

    public Encoding(String value) {
        super(Parameter.Id.ENCODING);
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements ParameterFactory<Encoding> {
        private Factory() {
        }

        @Override
        public Encoding createParameter(String value) {
            if (B.getValue().equals(value)) {
                return B;
            }
            return new Encoding(value);
        }
    }
}

