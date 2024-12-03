/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package net.fortuna.ical4j.model.parameter;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import org.apache.commons.lang3.StringUtils;

public class Display
extends Parameter {
    private static final long serialVersionUID = 1L;
    private static final String PARAMETER_NAME = "DISPLAY";
    private final String[] values;

    public Display(String value) {
        super(PARAMETER_NAME, new Factory());
        String[] valueStrings;
        for (String valueString : valueStrings = value.split(",")) {
            try {
                Value.valueOf(valueString);
            }
            catch (IllegalArgumentException iae) {
                if (valueString.startsWith("X-")) continue;
                throw iae;
            }
        }
        this.values = valueStrings;
    }

    @Override
    public String getValue() {
        return StringUtils.join((Object[])this.values, (String)",");
    }

    public static class Factory
    extends Content.Factory
    implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(Display.PARAMETER_NAME);
        }

        public Parameter createParameter(String value) {
            return new Display(value);
        }
    }

    public static enum Value {
        BADGE,
        GRAPHIC,
        FULLSIZE,
        THUMBNAIL;

    }
}

