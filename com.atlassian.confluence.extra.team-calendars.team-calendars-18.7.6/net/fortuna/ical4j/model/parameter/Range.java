/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Strings;

public class Range
extends Parameter {
    private static final long serialVersionUID = -3057531444558393776L;
    private static final String VALUE_THISANDPRIOR = "THISANDPRIOR";
    private static final String VALUE_THISANDFUTURE = "THISANDFUTURE";
    public static final Range THISANDPRIOR = new Range("THISANDPRIOR");
    public static final Range THISANDFUTURE = new Range("THISANDFUTURE");
    private String value;

    public Range(String aValue) {
        super("RANGE", new Factory());
        this.value = Strings.unquote(aValue);
        if (!(CompatibilityHints.isHintEnabled("ical4j.compatibility.notes") || VALUE_THISANDPRIOR.equals(this.value) || VALUE_THISANDFUTURE.equals(this.value))) {
            throw new IllegalArgumentException("Invalid value [" + this.value + "]");
        }
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
            super("RANGE");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            Range parameter = new Range(value);
            if (THISANDFUTURE.equals(parameter)) {
                parameter = THISANDFUTURE;
            } else if (THISANDPRIOR.equals(parameter)) {
                parameter = THISANDPRIOR;
            }
            return parameter;
        }
    }
}

