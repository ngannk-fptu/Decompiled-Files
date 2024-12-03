/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.ical4j.property;

import com.atlassian.confluence.extra.calendar3.ical4j.property.BooleanProperty;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.XProperty;

public class GreenHopperSprintStatus
extends BooleanProperty {
    public static final String NAME = "X-GREENHOPPER-SPRINT-CLOSED";
    public static final GreenHopperSprintStatus OPEN = new ImmutableGreenHopperSprintStatus(false);
    public static final GreenHopperSprintStatus CLOSED = new ImmutableGreenHopperSprintStatus(true);

    public GreenHopperSprintStatus() {
        super(NAME, new XProperty.Factory(NAME));
    }

    @Override
    public Property copy() throws IOException, URISyntaxException, ParseException {
        GreenHopperSprintStatus copy = new GreenHopperSprintStatus();
        ((Property)copy).setValue(this.getValue());
        return copy;
    }

    private static class ImmutableGreenHopperSprintStatus
    extends GreenHopperSprintStatus {
        private ImmutableGreenHopperSprintStatus(boolean closed) {
            super.setValue(String.valueOf(closed));
        }

        @Override
        public void setValue(String aValue) {
            throw new UnsupportedOperationException("This property is not mutable.");
        }

        @Override
        public Property copy() {
            return this;
        }
    }
}

