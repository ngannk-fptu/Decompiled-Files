/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.data;

import net.fortuna.ical4j.data.CalendarParser;
import net.fortuna.ical4j.data.CalendarParserFactory;
import net.fortuna.ical4j.data.HCalendarParser;

public class HCalendarParserFactory
extends CalendarParserFactory {
    @Override
    public CalendarParser get() {
        return new HCalendarParser();
    }
}

