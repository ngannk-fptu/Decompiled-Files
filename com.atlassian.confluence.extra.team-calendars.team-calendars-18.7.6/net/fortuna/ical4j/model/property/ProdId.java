/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class ProdId
extends Property
implements Escapable {
    private static final long serialVersionUID = -2433059917714523286L;
    private String value;

    public ProdId() {
        super("PRODID", new Factory());
    }

    public ProdId(String aValue) {
        super("PRODID", new Factory());
        this.setValue(aValue);
    }

    public ProdId(ParameterList aList, String aValue) {
        super("PRODID", aList, new Factory());
        this.setValue(aValue);
    }

    @Override
    public final void setValue(String aValue) {
        this.value = aValue;
    }

    @Override
    public final String getValue() {
        return this.value;
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("PRODID");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new ProdId(parameters, value);
        }

        public Property createProperty() {
            return new ProdId();
        }
    }
}

