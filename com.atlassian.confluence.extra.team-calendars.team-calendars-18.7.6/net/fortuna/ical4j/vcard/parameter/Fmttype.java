/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.parameter;

import java.text.ParseException;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.ParameterFactory;

public final class Fmttype
extends Parameter {
    private static final long serialVersionUID = 12345L;
    public static final ParameterFactory<Fmttype> FACTORY = new Factory();
    private String value;
    private String type;
    private String subtype;

    public Fmttype(String value) throws ParseException {
        super(Parameter.Id.FMTTYPE);
        this.value = value;
        String[] segments = value.split("/", -1);
        if (segments.length != 2) {
            throw new ParseException("Value must be type \"/\" subtype", 0);
        }
        this.type = segments[0];
        this.subtype = segments[1];
    }

    public String getValue() {
        return this.value;
    }

    public String getType() {
        return this.type;
    }

    public String getSubtype() {
        return this.subtype;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements ParameterFactory<Fmttype> {
        private Factory() {
        }

        @Override
        public Fmttype createParameter(String value) {
            try {
                return new Fmttype(value);
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }
}

