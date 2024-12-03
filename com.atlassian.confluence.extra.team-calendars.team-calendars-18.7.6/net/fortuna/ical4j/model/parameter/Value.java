/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

public class Value
extends Parameter {
    private static final long serialVersionUID = -7238642734500301768L;
    private static final String VALUE_BINARY = "BINARY";
    private static final String VALUE_BOOLEAN = "BOOLEAN";
    private static final String VALUE_CAL_ADDRESS = "CAL-ADDRESS";
    private static final String VALUE_DATE = "DATE";
    private static final String VALUE_DATE_TIME = "DATE-TIME";
    private static final String VALUE_DURATION = "DURATION";
    private static final String VALUE_FLOAT = "FLOAT";
    private static final String VALUE_INTEGER = "INTEGER";
    private static final String VALUE_PERIOD = "PERIOD";
    private static final String VALUE_RECUR = "RECUR";
    private static final String VALUE_TEXT = "TEXT";
    private static final String VALUE_TIME = "TIME";
    private static final String VALUE_URI = "URI";
    private static final String VALUE_UTC_OFFSET = "UTC-OFFSET";
    public static final Value BINARY = new Value("BINARY");
    public static final Value BOOLEAN = new Value("BOOLEAN");
    public static final Value CAL_ADDRESS = new Value("CAL-ADDRESS");
    public static final Value DATE = new Value("DATE");
    public static final Value DATE_TIME = new Value("DATE-TIME");
    public static final Value DURATION = new Value("DURATION");
    public static final Value FLOAT = new Value("FLOAT");
    public static final Value INTEGER = new Value("INTEGER");
    public static final Value PERIOD = new Value("PERIOD");
    public static final Value RECUR = new Value("RECUR");
    public static final Value TEXT = new Value("TEXT");
    public static final Value TIME = new Value("TIME");
    public static final Value URI = new Value("URI");
    public static final Value UTC_OFFSET = new Value("UTC-OFFSET");
    private String value;

    public Value(String aValue) {
        super("VALUE", new Factory());
        this.value = Strings.unquote(aValue);
    }

    @Override
    public final String getValue() {
        return this.value;
    }

    public static class Factory
    extends Content.Factory
    implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("VALUE");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            Value parameter = new Value(value);
            if (BINARY.equals(parameter)) {
                parameter = BINARY;
            } else if (BOOLEAN.equals(parameter)) {
                parameter = BOOLEAN;
            } else if (CAL_ADDRESS.equals(parameter)) {
                parameter = CAL_ADDRESS;
            } else if (DATE.equals(parameter)) {
                parameter = DATE;
            } else if (DATE_TIME.equals(parameter)) {
                parameter = DATE_TIME;
            } else if (DURATION.equals(parameter)) {
                parameter = DURATION;
            } else if (FLOAT.equals(parameter)) {
                parameter = FLOAT;
            } else if (INTEGER.equals(parameter)) {
                parameter = INTEGER;
            } else if (PERIOD.equals(parameter)) {
                parameter = PERIOD;
            } else if (RECUR.equals(parameter)) {
                parameter = RECUR;
            } else if (TEXT.equals(parameter)) {
                parameter = TEXT;
            } else if (TIME.equals(parameter)) {
                parameter = TIME;
            } else if (URI.equals(parameter)) {
                parameter = URI;
            } else if (UTC_OFFSET.equals(parameter)) {
                parameter = UTC_OFFSET;
            }
            return parameter;
        }
    }
}

