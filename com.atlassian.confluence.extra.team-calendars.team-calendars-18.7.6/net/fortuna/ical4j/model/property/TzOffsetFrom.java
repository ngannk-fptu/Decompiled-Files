/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.ZoneOffset;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.UtcOffset;
import net.fortuna.ical4j.model.ZoneOffsetAdapter;
import net.fortuna.ical4j.validate.ValidationException;

public class TzOffsetFrom
extends Property {
    private static final long serialVersionUID = 450274263165493502L;
    private ZoneOffsetAdapter offset;

    public TzOffsetFrom() {
        super("TZOFFSETFROM", new Factory());
    }

    public TzOffsetFrom(String aValue) {
        super("TZOFFSETFROM", new Factory());
        this.setValue(aValue);
    }

    public TzOffsetFrom(ParameterList aList, String aValue) {
        super("TZOFFSETFROM", aList, new Factory());
        this.setValue(aValue);
    }

    @Deprecated
    public TzOffsetFrom(UtcOffset anOffset) {
        this(ZoneOffsetAdapter.from(anOffset));
    }

    public TzOffsetFrom(ZoneOffset anOffset) {
        super("TZOFFSETFROM", new Factory());
        this.offset = new ZoneOffsetAdapter(anOffset);
    }

    @Deprecated
    public TzOffsetFrom(ParameterList aList, UtcOffset anOffset) {
        this(aList, ZoneOffsetAdapter.from(anOffset));
    }

    public TzOffsetFrom(ParameterList aList, ZoneOffset anOffset) {
        super("TZOFFSETFROM", aList, new Factory());
        this.offset = new ZoneOffsetAdapter(anOffset);
    }

    public final ZoneOffset getOffset() {
        return this.offset.getOffset();
    }

    @Override
    public final void setValue(String aValue) {
        this.offset = new ZoneOffsetAdapter(ZoneOffset.of(aValue));
    }

    @Override
    public final String getValue() {
        if (this.offset != null) {
            return this.offset.toString();
        }
        return "";
    }

    public final void setOffset(ZoneOffset offset) {
        this.offset = new ZoneOffsetAdapter(offset);
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("TZOFFSETFROM");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new TzOffsetFrom(parameters, value);
        }

        public Property createProperty() {
            return new TzOffsetFrom();
        }
    }
}

