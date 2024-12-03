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
import net.fortuna.ical4j.validate.ValidationException;

public class Sequence
extends Property
implements Comparable<Sequence> {
    private static final long serialVersionUID = -1606972893204822853L;
    private int sequenceNo;

    public Sequence() {
        super("SEQUENCE", new Factory());
        this.sequenceNo = 0;
    }

    public Sequence(String aValue) {
        super("SEQUENCE", new Factory());
        this.setValue(aValue);
    }

    public Sequence(ParameterList aList, String aValue) {
        super("SEQUENCE", aList, new Factory());
        this.setValue(aValue);
    }

    public Sequence(int aSequenceNo) {
        super("SEQUENCE", new Factory());
        this.sequenceNo = aSequenceNo;
    }

    public Sequence(ParameterList aList, int aSequenceNo) {
        super("SEQUENCE", aList, new Factory());
        this.sequenceNo = aSequenceNo;
    }

    public final int getSequenceNo() {
        return this.sequenceNo;
    }

    @Override
    public final void setValue(String aValue) {
        this.sequenceNo = Integer.parseInt(aValue);
    }

    @Override
    public final String getValue() {
        return String.valueOf(this.getSequenceNo());
    }

    @Override
    public void validate() throws ValidationException {
    }

    @Override
    public int compareTo(Sequence o) {
        return Integer.compare(this.getSequenceNo(), o.getSequenceNo());
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("SEQUENCE");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Sequence(parameters, value);
        }

        public Property createProperty() {
            return new Sequence();
        }
    }
}

