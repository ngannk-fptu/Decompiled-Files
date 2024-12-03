/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonFactory
 *  com.fasterxml.jackson.core.JsonGenerator$Feature
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonParser$Feature
 *  com.fasterxml.jackson.core.JsonToken
 */
package org.bedework.util.calendar;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.ParseException;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.CalendarException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Geo;
import net.fortuna.ical4j.model.property.RequestStatus;
import org.bedework.util.calendar.BuildState;
import org.bedework.util.calendar.ContentHandlerImpl;
import org.bedework.util.calendar.XcalUtil;

public class JsonCalendarBuilder {
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final JsonFactory jsonFactory = new JsonFactory();
    private final TimeZoneRegistry tzRegistry;
    private String lastComponent;
    private String lastProperty;

    public JsonCalendarBuilder(TimeZoneRegistry tzRegistry) {
        this.tzRegistry = tzRegistry;
    }

    public Calendar build(InputStream in) throws IOException, ParserException {
        return this.build(new InputStreamReader(in, DEFAULT_CHARSET));
    }

    public Calendar build(Reader in) throws IOException, ParserException {
        BuildState bs = new BuildState(this.tzRegistry);
        bs.setContentHandler(new ContentHandlerImpl(bs));
        try {
            JsonParser parser = jsonFactory.createParser(in);
            this.process(parser, bs);
        }
        catch (Throwable t) {
            throw new ParserException(t.getMessage(), 0, t);
        }
        if (bs.getDatesMissingTimezones().size() > 0 && this.tzRegistry != null) {
            this.resolveTimezones(bs);
        }
        return bs.getCalendars().iterator().next();
    }

    private void process(JsonParser parser, BuildState bs) throws ParserException {
        try {
            this.arrayStart(parser);
            String ctype = this.textField(parser);
            if (!ctype.equals("vcalendar")) {
                this.throwException("Expected vcalendar: found " + ctype, parser);
            }
            this.lastComponent = "vcalendar";
            bs.setCalendar(null);
            this.processVcalendar(parser, bs);
            if (bs.getCalendar() != null) {
                bs.getCalendars().add(bs.getCalendar());
            }
            this.arrayEnd(parser);
        }
        catch (Throwable t) {
            this.handleException(t, parser);
        }
    }

    private void processVcalendar(JsonParser parser, BuildState bs) throws ParserException {
        bs.getContentHandler().startCalendar();
        this.processProperties(parser, bs);
        this.processCalcomps(parser, bs);
    }

    private void processProperties(JsonParser parser, BuildState bs) throws ParserException {
        this.arrayStart(parser);
        while (!this.testArrayEnd(parser)) {
            this.processProperty(parser, bs);
        }
    }

    private void processCalcomps(JsonParser parser, BuildState bs) throws ParserException {
        this.arrayStart(parser);
        while (!this.testArrayEnd(parser)) {
            this.processComponent(parser, bs);
        }
    }

    private void processComponent(JsonParser parser, BuildState bs) throws ParserException {
        String cname;
        this.currentArrayStart(parser);
        this.lastComponent = cname = this.textField(parser).toUpperCase();
        bs.getContentHandler().startComponent(cname);
        this.processProperties(parser, bs);
        this.processCalcomps(parser, bs);
        bs.getContentHandler().endComponent(cname);
        this.arrayEnd(parser);
    }

    private void processProperty(JsonParser parser, BuildState bs) throws ParserException {
        String name;
        this.currentArrayStart(parser);
        this.lastProperty = name = this.textField(parser);
        bs.getContentHandler().startProperty(name);
        this.processParameters(parser, bs);
        boolean parseArrayEnd = this.processValue(parser, bs, this.textField(parser));
        bs.getContentHandler().endProperty(name);
        if (parseArrayEnd) {
            this.arrayEnd(parser);
        }
    }

    private void processParameters(JsonParser parser, BuildState bs) throws ParserException {
        this.objectStart(parser);
        while (!this.testObjectEnd(parser)) {
            this.processParameter(parser, bs);
        }
    }

    private void processParameter(JsonParser parser, BuildState bs) throws ParserException {
        try {
            bs.getContentHandler().parameter(this.currentFieldName(parser), this.textField(parser));
        }
        catch (Throwable t) {
            this.handleException(t, parser);
        }
    }

    private boolean processValue(JsonParser parser, BuildState bs, String type) throws ParserException {
        try {
            if (bs.getProperty() instanceof Geo) {
                this.arrayStart(parser);
                StringBuilder sb = new StringBuilder();
                sb.append(String.valueOf(this.floatField(parser)));
                sb.append(",");
                sb.append(String.valueOf(this.floatField(parser)));
                this.arrayEnd(parser);
                bs.getContentHandler().propertyValue(sb.toString());
                return true;
            }
            if (bs.getProperty() instanceof RequestStatus) {
                this.arrayStart(parser);
                StringBuilder sb = new StringBuilder();
                sb.append(this.textField(parser));
                sb.append(",");
                sb.append(this.textField(parser));
                if (!this.testArrayEnd(parser)) {
                    sb.append(",");
                    sb.append(this.currentTextField(parser));
                    this.arrayEnd(parser);
                }
                bs.getContentHandler().propertyValue(sb.toString());
                return true;
            }
            if (type.equals("recur")) {
                StringBuilder sb = new StringBuilder();
                String delim = "";
                this.objectStart(parser);
                while (!this.testObjectEnd(parser)) {
                    sb.append(delim);
                    delim = ";";
                    String recurEl = this.currentFieldName(parser);
                    sb.append(recurEl.toUpperCase());
                    sb.append("=");
                    sb.append(this.recurElVal(parser, recurEl));
                }
                bs.getContentHandler().propertyValue(sb.toString());
                return true;
            }
            if (type.equals("boolean")) {
                bs.getContentHandler().propertyValue(String.valueOf(this.booleanField(parser)));
                return true;
            }
            if (type.equals("utc-offset")) {
                bs.getContentHandler().propertyValue(XcalUtil.getIcalUtcOffset(this.textField(parser)));
                return true;
            }
            if (type.equals("binary") || type.equals("cal-address") || type.equals("duration") || type.equals("text") || type.equals("uri")) {
                StringBuilder res = new StringBuilder();
                String delim = "";
                while (!this.testArrayEnd(parser)) {
                    res.append(delim);
                    delim = ",";
                    res.append(this.currentTextField(parser));
                }
                bs.getContentHandler().propertyValue(res.toString());
                return false;
            }
            if (type.equals("integer")) {
                bs.getContentHandler().propertyValue(String.valueOf(this.intField(parser)));
                return true;
            }
            if (type.equals("float")) {
                bs.getContentHandler().propertyValue(String.valueOf(this.intField(parser)));
                return true;
            }
            if (type.equals("date") || type.equals("date-time") || type.equals("time")) {
                bs.getContentHandler().propertyValue(XcalUtil.getIcalFormatDateTime(this.textField(parser)));
                return true;
            }
            if (type.equals("time")) {
                bs.getContentHandler().propertyValue(XcalUtil.getIcalFormatTime(this.textField(parser)));
                return true;
            }
            if (type.equals("period")) {
                String[] parts = this.textField(parser).split("/");
                StringBuilder sb = new StringBuilder();
                sb.append(XcalUtil.getIcalFormatDateTime(parts[0]));
                if (parts[1].toUpperCase().startsWith("P")) {
                    sb.append(parts[1]);
                } else {
                    sb.append(XcalUtil.getIcalFormatDateTime(parts[1]));
                }
                bs.getContentHandler().propertyValue(sb.toString());
                return true;
            }
        }
        catch (URISyntaxException e) {
            throw new ParserException(e.getMessage(), 0, e);
        }
        catch (ParseException e) {
            throw new ParserException(e.getMessage(), 0, e);
        }
        catch (IOException e) {
            throw new ParserException(e.getMessage(), 0, e);
        }
        this.throwException("Bad property", parser);
        return false;
    }

    private String recurElVal(JsonParser parser, String el) throws ParserException {
        if (el.equals("freq")) {
            return this.textField(parser);
        }
        if (el.equals("wkst")) {
            return this.textField(parser);
        }
        if (el.equals("until")) {
            return this.textField(parser);
        }
        if (el.equals("count")) {
            return String.valueOf(this.intField(parser));
        }
        if (el.equals("interval")) {
            return String.valueOf(this.intField(parser));
        }
        if (el.equals("bymonth")) {
            return this.intList(parser);
        }
        if (el.equals("byweekno")) {
            return this.intList(parser);
        }
        if (el.equals("byyearday")) {
            return this.intList(parser);
        }
        if (el.equals("bymonthday")) {
            return this.intList(parser);
        }
        if (el.equals("byday")) {
            return this.textList(parser);
        }
        if (el.equals("byhour")) {
            return this.intList(parser);
        }
        if (el.equals("byminute")) {
            return this.intList(parser);
        }
        if (el.equals("bysecond")) {
            return this.intList(parser);
        }
        if (el.equals("bysetpos")) {
            return this.intList(parser);
        }
        this.throwException("Unexpected recur field " + el, parser);
        return null;
    }

    public final TimeZoneRegistry getRegistry() {
        return this.tzRegistry;
    }

    private void resolveTimezones(BuildState bs) throws IOException {
        for (Property property : bs.getDatesMissingTimezones()) {
            TimeZone timezone;
            Object tzParam = property.getParameter("TZID");
            if (tzParam == null || (timezone = this.tzRegistry.getTimeZone(((Content)tzParam).getValue())) == null) continue;
            String strDate = property.getValue();
            if (property instanceof DateProperty) {
                ((DateProperty)property).setTimeZone(timezone);
            } else if (property instanceof DateListProperty) {
                ((DateListProperty)property).setTimeZone(timezone);
            }
            try {
                property.setValue(strDate);
            }
            catch (ParseException e) {
                throw new CalendarException(e);
            }
            catch (URISyntaxException e) {
                throw new CalendarException(e);
            }
        }
    }

    private void throwException(String msg, JsonParser parser) throws ParserException {
        StringBuilder augmented = new StringBuilder(msg);
        if (this.lastComponent != null) {
            augmented.append("; last component=");
            augmented.append(this.lastComponent);
        }
        if (this.lastProperty != null) {
            augmented.append("; last property=");
            augmented.append(this.lastProperty);
        }
        this.handleException(new Throwable(augmented.toString()), parser);
    }

    private Object handleException(Throwable t, JsonParser parser) throws ParserException {
        if (t instanceof ParserException) {
            throw (ParserException)t;
        }
        try {
            int lnr = parser.getCurrentLocation().getLineNr();
            throw new ParserException(t.getLocalizedMessage(), lnr);
        }
        catch (ParserException pe) {
            throw pe;
        }
        catch (Throwable t1) {
            throw new ParserException(t.getLocalizedMessage(), -1);
        }
    }

    private void arrayStart(JsonParser parser) throws ParserException {
        this.expectToken(parser, JsonToken.START_ARRAY, "Expected array start");
    }

    private void arrayEnd(JsonParser parser) throws ParserException {
        this.expectToken(parser, JsonToken.END_ARRAY, "Expected array end");
    }

    private void currentArrayStart(JsonParser parser) throws ParserException {
        this.expectCurrentToken(parser, JsonToken.START_ARRAY, "Expected array start");
    }

    private boolean testNextArrayStart(JsonParser parser) throws ParserException {
        return this.testToken(parser, JsonToken.START_ARRAY);
    }

    private boolean testArrayEnd(JsonParser parser) throws ParserException {
        return this.testToken(parser, JsonToken.END_ARRAY);
    }

    private void objectStart(JsonParser parser) throws ParserException {
        this.expectToken(parser, JsonToken.START_OBJECT, "Expected object start");
    }

    private boolean testObjectEnd(JsonParser parser) throws ParserException {
        return this.testToken(parser, JsonToken.END_OBJECT);
    }

    private void expectToken(JsonParser parser, JsonToken expected, String message) throws ParserException {
        try {
            JsonToken t = parser.nextToken();
            if (t != expected) {
                this.throwException(message, parser);
            }
        }
        catch (ParserException pe) {
            throw pe;
        }
        catch (Throwable t) {
            this.handleException(t, parser);
        }
    }

    private void expectCurrentToken(JsonParser parser, JsonToken expected, String message) throws ParserException {
        try {
            JsonToken t = parser.getCurrentToken();
            if (t != expected) {
                this.throwException(message, parser);
            }
        }
        catch (ParserException pe) {
            throw pe;
        }
        catch (Throwable t) {
            this.handleException(t, parser);
        }
    }

    private boolean testCurrentToken(JsonParser parser, JsonToken expected) throws ParserException {
        try {
            JsonToken t = parser.getCurrentToken();
            return t == expected;
        }
        catch (Throwable t) {
            this.handleException(t, parser);
            return false;
        }
    }

    private boolean testToken(JsonParser parser, JsonToken expected) throws ParserException {
        try {
            JsonToken t = parser.nextToken();
            return t == expected;
        }
        catch (Throwable t) {
            return (Boolean)this.handleException(t, parser);
        }
    }

    private String textField(JsonParser parser) throws ParserException {
        this.expectToken(parser, JsonToken.VALUE_STRING, "Expected string field");
        try {
            return parser.getText();
        }
        catch (Throwable t) {
            return (String)this.handleException(t, parser);
        }
    }

    private String currentTextField(JsonParser parser) throws ParserException {
        try {
            return parser.getText();
        }
        catch (Throwable t) {
            return (String)this.handleException(t, parser);
        }
    }

    private int currentIntField(JsonParser parser) throws ParserException {
        try {
            return parser.getIntValue();
        }
        catch (Throwable t) {
            return (Integer)this.handleException(t, parser);
        }
    }

    private int intField(JsonParser parser) throws ParserException {
        this.expectToken(parser, JsonToken.VALUE_NUMBER_INT, "Expected integer field");
        try {
            return parser.getIntValue();
        }
        catch (Throwable t) {
            return (Integer)this.handleException(t, parser);
        }
    }

    private float floatField(JsonParser parser) throws ParserException {
        this.expectToken(parser, JsonToken.VALUE_NUMBER_FLOAT, "Expected float field");
        try {
            return parser.getFloatValue();
        }
        catch (Throwable t) {
            return ((Float)this.handleException(t, parser)).floatValue();
        }
    }

    private boolean booleanField(JsonParser parser) throws ParserException {
        try {
            if (parser.getCurrentToken() == JsonToken.VALUE_FALSE) {
                return false;
            }
            if (parser.getCurrentToken() == JsonToken.VALUE_TRUE) {
                return true;
            }
            this.throwException("expected boolean constant", parser);
            return false;
        }
        catch (Throwable t) {
            return (Boolean)this.handleException(t, parser);
        }
    }

    private String currentFieldName(JsonParser parser) throws ParserException {
        this.expectCurrentToken(parser, JsonToken.FIELD_NAME, "Expected field name");
        try {
            return parser.getText();
        }
        catch (Throwable t) {
            return (String)this.handleException(t, parser);
        }
    }

    private String textList(JsonParser parser) throws ParserException {
        if (!this.testNextArrayStart(parser)) {
            return this.currentTextField(parser);
        }
        StringBuilder sb = new StringBuilder();
        String delim = "";
        while (!this.testArrayEnd(parser)) {
            sb.append(delim);
            delim = ",";
            sb.append(this.currentTextField(parser));
        }
        return sb.toString();
    }

    private String intList(JsonParser parser) throws ParserException {
        if (!this.testNextArrayStart(parser)) {
            return String.valueOf(this.currentIntField(parser));
        }
        StringBuilder sb = new StringBuilder();
        String delim = "";
        while (!this.testArrayEnd(parser)) {
            sb.append(delim);
            delim = ",";
            sb.append(this.currentIntField(parser));
        }
        return sb.toString();
    }

    static {
        jsonFactory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        jsonFactory.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
        jsonFactory.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }
}

