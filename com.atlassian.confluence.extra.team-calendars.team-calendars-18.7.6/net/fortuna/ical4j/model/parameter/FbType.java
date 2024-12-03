/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

public class FbType
extends Parameter {
    private static final long serialVersionUID = -2217689716824679375L;
    private static final String VALUE_FREE = "FREE";
    private static final String VALUE_BUSY = "BUSY";
    private static final String VALUE_BUSY_UNAVAILABLE = "BUSY-UNAVAILABLE";
    private static final String VALUE_BUSY_TENTATIVE = "BUSY-TENTATIVE";
    public static final FbType FREE = new FbType("FREE");
    public static final FbType BUSY = new FbType("BUSY");
    public static final FbType BUSY_UNAVAILABLE = new FbType("BUSY-UNAVAILABLE");
    public static final FbType BUSY_TENTATIVE = new FbType("BUSY-TENTATIVE");
    private String value;

    public FbType(String aValue) {
        super("FBTYPE", new Factory());
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
            super("FBTYPE");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            FbType parameter = new FbType(value);
            if (FREE.equals(parameter)) {
                parameter = FREE;
            } else if (BUSY.equals(parameter)) {
                parameter = BUSY;
            } else if (BUSY_TENTATIVE.equals(parameter)) {
                parameter = BUSY_TENTATIVE;
            } else if (BUSY_UNAVAILABLE.equals(parameter)) {
                parameter = BUSY_UNAVAILABLE;
            }
            return parameter;
        }
    }
}

