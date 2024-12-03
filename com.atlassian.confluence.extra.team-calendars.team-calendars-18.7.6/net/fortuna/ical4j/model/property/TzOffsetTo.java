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

public class TzOffsetTo
extends Property {
    private static final long serialVersionUID = 8213874575051177732L;
    private ZoneOffsetAdapter offset;

    public TzOffsetTo() {
        super("TZOFFSETTO", new Factory());
    }

    public TzOffsetTo(String value) {
        super("TZOFFSETTO", new Factory());
        this.setValue(value);
    }

    public TzOffsetTo(ParameterList aList, String aValue) {
        super("TZOFFSETTO", aList, new Factory());
        this.setValue(aValue);
    }

    @Deprecated
    public TzOffsetTo(UtcOffset anOffset) {
        this(ZoneOffsetAdapter.from(anOffset));
    }

    public TzOffsetTo(ZoneOffset anOffset) {
        super("TZOFFSETTO", new Factory());
        this.offset = new ZoneOffsetAdapter(anOffset);
    }

    @Deprecated
    public TzOffsetTo(ParameterList aList, UtcOffset anOffset) {
        this(aList, ZoneOffsetAdapter.from(anOffset));
    }

    public TzOffsetTo(ParameterList aList, ZoneOffset anOffset) {
        super("TZOFFSETTO", aList, new Factory());
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
            super("TZOFFSETTO");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new TzOffsetTo(parameters, value);
        }

        public Property createProperty() {
            return new TzOffsetTo();
        }
    }
}

