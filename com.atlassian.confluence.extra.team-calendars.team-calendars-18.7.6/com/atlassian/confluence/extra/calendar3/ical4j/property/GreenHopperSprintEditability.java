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

public class GreenHopperSprintEditability
extends BooleanProperty {
    public static final String NAME = "X-GREENHOPPER-SPRINT-EDITABLE";
    public static final GreenHopperSprintEditability EDITABLE = new ImmutableGreenHopperSprintEditability(true);
    public static final GreenHopperSprintEditability NOT_EDITABLE = new ImmutableGreenHopperSprintEditability(false);

    public GreenHopperSprintEditability() {
        super(NAME, new XProperty.Factory(NAME));
    }

    @Override
    public Property copy() throws IOException, URISyntaxException, ParseException {
        GreenHopperSprintEditability copied = new GreenHopperSprintEditability();
        ((Property)copied).setValue(this.getValue());
        return copied;
    }

    private static class ImmutableGreenHopperSprintEditability
    extends GreenHopperSprintEditability {
        private ImmutableGreenHopperSprintEditability(boolean editable) {
            super.setValue(String.valueOf(editable));
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

