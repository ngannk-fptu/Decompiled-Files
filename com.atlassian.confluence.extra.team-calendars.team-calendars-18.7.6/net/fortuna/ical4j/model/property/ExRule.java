/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.validate.ValidationException;

public class ExRule
extends Property {
    private static final long serialVersionUID = -9171193801247139294L;
    private Recur recur;

    public ExRule() {
        super("EXRULE", new Factory());
        this.recur = new Recur(Recur.Frequency.DAILY, 1);
    }

    public ExRule(ParameterList aList, String aValue) throws ParseException {
        super("EXRULE", aList, new Factory());
        this.setValue(aValue);
    }

    public ExRule(Recur aRecur) {
        super("EXRULE", new Factory());
        this.recur = aRecur;
    }

    public ExRule(ParameterList aList, Recur aRecur) {
        super("EXRULE", aList, new Factory());
        this.recur = aRecur;
    }

    public final Recur getRecur() {
        return this.recur;
    }

    @Override
    public final void setValue(String aValue) throws ParseException {
        this.recur = new Recur(aValue);
    }

    @Override
    public final String getValue() {
        return this.getRecur().toString();
    }

    public final void setRecur(Recur recur) {
        this.recur = recur;
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("EXRULE");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new ExRule(parameters, value);
        }

        public Property createProperty() {
            return new ExRule();
        }
    }
}

