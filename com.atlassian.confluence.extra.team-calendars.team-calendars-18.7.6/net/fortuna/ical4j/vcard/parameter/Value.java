/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.parameter;

import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.ParameterFactory;

public final class Value
extends Parameter {
    private static final long serialVersionUID = -4161095052661786246L;
    public static final Value TEXT = new Value("text");
    public static final Value URI = new Value("uri");
    public static final Value DATE = new Value("date");
    public static final Value TIME = new Value("time");
    public static final Value DATE_TIME = new Value("date-time");
    public static final Value DATE_AND_OR_TIME = new Value("date-and-or-time");
    public static final Value TIMESTAMP = new Value("timestamp");
    public static final Value BOOLEAN = new Value("boolean");
    public static final Value INTEGER = new Value("integer");
    public static final Value FLOAT = new Value("float");
    public static final Value BINARY = new Value("binary");
    public static final Value LANGUAGE_TAG = new Value("language-tag");
    public static final Value UTC_OFFSET = new Value("utc-offset");
    public static final Value DURATION = new Value("duration");
    public static final ParameterFactory<Value> FACTORY = new Factory();
    private final String value;

    public Value(String value) {
        super(Parameter.Id.VALUE);
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements ParameterFactory<Value> {
        private Factory() {
        }

        @Override
        public Value createParameter(String value) {
            Value parameter = null;
            parameter = TEXT.getValue().equals(value) ? TEXT : (URI.getValue().equals(value) ? URI : (DATE.getValue().equals(value) ? DATE : (TIME.getValue().equals(value) ? TIME : (DATE_TIME.getValue().equals(value) ? DATE_TIME : (DATE_AND_OR_TIME.getValue().equals(value) ? DATE_AND_OR_TIME : (TIMESTAMP.getValue().equals(value) ? TIMESTAMP : (BOOLEAN.getValue().equals(value) ? BOOLEAN : (INTEGER.getValue().equals(value) ? INTEGER : (FLOAT.getValue().equals(value) ? FLOAT : (BINARY.getValue().equals(value) ? BINARY : (LANGUAGE_TAG.getValue().equals(value) ? LANGUAGE_TAG : (DURATION.getValue().equals(value) ? DURATION : (UTC_OFFSET.getValue().equals(value) ? UTC_OFFSET : new Value(value))))))))))))));
            return parameter;
        }
    }
}

