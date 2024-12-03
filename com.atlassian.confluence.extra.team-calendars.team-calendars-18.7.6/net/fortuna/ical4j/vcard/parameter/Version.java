/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.parameter;

import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.ParameterFactory;

public final class Version
extends Parameter {
    private static final long serialVersionUID = 12345L;
    public static final ParameterFactory<Version> FACTORY = new Factory();
    private String value;

    public Version(String value) {
        super(Parameter.Id.VERSION);
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements ParameterFactory<Version> {
        private Factory() {
        }

        @Override
        public Version createParameter(String value) {
            return new Version(value);
        }
    }
}

