/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.ParseException;
import net.fortuna.ical4j.data.CalendarParser;
import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.data.UnfoldingReader;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarParserImpl
implements CalendarParser {
    private static final int IGNORE_BEGINNING_NON_WORD_COUNT = 10;
    private static final int WORD_CHAR_START = 32;
    private static final int WORD_CHAR_END = 255;
    private static final int WHITESPACE_CHAR_START = 0;
    private static final int WHITESPACE_CHAR_END = 20;
    private static final String UNEXPECTED_TOKEN_MESSAGE = "Expected [{0}], read [{1}]";
    private Logger log = LoggerFactory.getLogger(CalendarParserImpl.class);
    private final ComponentListParser componentListParser = new ComponentListParser();
    private final ComponentParser componentParser = new ComponentParser();
    private final PropertyListParser propertyListParser = new PropertyListParser();
    private final PropertyParser propertyParser = new PropertyParser();
    private final ParameterListParser paramListParser = new ParameterListParser();
    private final ParameterParser paramParser = new ParameterParser();

    @Override
    public final void parse(InputStream in, ContentHandler handler) throws IOException, ParserException {
        this.parse(new InputStreamReader(in), handler);
    }

    private void parseCalendar(StreamTokenizer tokeniser, Reader in, ContentHandler handler) throws IOException, ParseException, URISyntaxException, ParserException {
        this.assertToken(tokeniser, in, 58);
        this.assertToken(tokeniser, in, "VCALENDAR", true, false);
        this.assertToken(tokeniser, in, 10);
        handler.startCalendar();
        this.absorbWhitespace(tokeniser, in);
        this.propertyListParser.parse(tokeniser, in, handler);
        this.componentListParser.parse(tokeniser, in, handler);
        this.assertToken(tokeniser, in, 58);
        this.assertToken(tokeniser, in, "VCALENDAR", true, false);
        handler.endCalendar();
    }

    @Override
    public final void parse(Reader in, ContentHandler handler) throws IOException, ParserException {
        StreamTokenizer tokeniser = new StreamTokenizer(in);
        try {
            tokeniser.resetSyntax();
            tokeniser.wordChars(32, 255);
            tokeniser.whitespaceChars(0, 20);
            tokeniser.ordinaryChar(58);
            tokeniser.ordinaryChar(59);
            tokeniser.ordinaryChar(61);
            tokeniser.ordinaryChar(9);
            tokeniser.eolIsSignificant(true);
            tokeniser.whitespaceChars(0, 0);
            tokeniser.quoteChar(34);
            this.parseCalendarList(tokeniser, in, handler);
        }
        catch (IOException | RuntimeException | URISyntaxException | ParseException e) {
            if (e instanceof IOException) {
                throw (IOException)e;
            }
            if (e instanceof ParserException) {
                throw (ParserException)e;
            }
            throw new ParserException(e.getMessage(), this.getLineNumber(tokeniser, in), e);
        }
    }

    private void parseCalendarList(StreamTokenizer tokeniser, Reader in, ContentHandler handler) throws IOException, ParseException, URISyntaxException, ParserException {
        int ntok = this.assertToken(tokeniser, in, "BEGIN", false, true);
        while (ntok != -1) {
            this.parseCalendar(tokeniser, in, handler);
            this.absorbWhitespace(tokeniser, in);
            ntok = this.nextToken(tokeniser, in, true);
        }
    }

    private int assertToken(StreamTokenizer tokeniser, Reader in, int token) throws IOException, ParserException {
        int ntok = this.nextToken(tokeniser, in);
        if (ntok != token) {
            throw new ParserException(MessageFormat.format(UNEXPECTED_TOKEN_MESSAGE, token, tokeniser.ttype), this.getLineNumber(tokeniser, in));
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("[" + token + "]");
        }
        return ntok;
    }

    private int assertToken(StreamTokenizer tokeniser, Reader in, String token) throws IOException, ParserException {
        return this.assertToken(tokeniser, in, token, false, false);
    }

    private int assertToken(StreamTokenizer tokeniser, Reader in, String token, boolean ignoreCase, boolean isBeginToken) throws IOException, ParserException {
        String sval;
        int ntok;
        if (isBeginToken) {
            ntok = this.skipNewLines(tokeniser, in, token);
            sval = this.getSvalIgnoringBom(tokeniser, in, token);
        } else {
            ntok = this.assertToken(tokeniser, in, -3);
            sval = tokeniser.sval;
        }
        if (ignoreCase ? !token.equalsIgnoreCase(sval) : !token.equals(sval)) {
            throw new ParserException(MessageFormat.format(UNEXPECTED_TOKEN_MESSAGE, token, sval), this.getLineNumber(tokeniser, in));
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("[" + token + "]");
        }
        return ntok;
    }

    private int skipNewLines(StreamTokenizer tokeniser, Reader in, String token) throws ParserException, IOException {
        int i = 0;
        while (true) {
            try {
                return this.assertToken(tokeniser, in, -3);
            }
            catch (ParserException exc) {
                if (i == 10) {
                    throw exc;
                }
                ++i;
                continue;
            }
            break;
        }
    }

    private String getSvalIgnoringBom(StreamTokenizer tokeniser, Reader in, String token) {
        if (tokeniser.sval != null && tokeniser.sval.contains(token)) {
            return token;
        }
        return tokeniser.sval;
    }

    private void absorbWhitespace(StreamTokenizer tokeniser, Reader in) throws IOException, ParserException {
        while (this.nextToken(tokeniser, in, true) == 10) {
            if (!this.log.isTraceEnabled()) continue;
            this.log.trace("Absorbing extra whitespace..");
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace("Aborting: absorbing extra whitespace complete");
        }
        tokeniser.pushBack();
    }

    private int getLineNumber(StreamTokenizer tokeniser, Reader in) {
        int line = tokeniser.lineno();
        if (tokeniser.ttype == 10) {
            --line;
        }
        if (in instanceof UnfoldingReader) {
            int unfolded = ((UnfoldingReader)in).getLinesUnfolded();
            line += unfolded;
        }
        return line;
    }

    private int nextToken(StreamTokenizer tokeniser, Reader in) throws IOException, ParserException {
        return this.nextToken(tokeniser, in, false);
    }

    private int nextToken(StreamTokenizer tokeniser, Reader in, boolean ignoreEOF) throws IOException, ParserException {
        int token = tokeniser.nextToken();
        if (!ignoreEOF && token == -1) {
            throw new ParserException("Unexpected end of file", this.getLineNumber(tokeniser, in));
        }
        return token;
    }

    private class ComponentParser {
        private ComponentParser() {
        }

        private void parse(StreamTokenizer tokeniser, Reader in, ContentHandler handler) throws IOException, ParseException, URISyntaxException, ParserException {
            CalendarParserImpl.this.assertToken(tokeniser, in, 58);
            CalendarParserImpl.this.assertToken(tokeniser, in, -3);
            String name = tokeniser.sval;
            handler.startComponent(name);
            CalendarParserImpl.this.assertToken(tokeniser, in, 10);
            CalendarParserImpl.this.absorbWhitespace(tokeniser, in);
            CalendarParserImpl.this.propertyListParser.parse(tokeniser, in, handler);
            CalendarParserImpl.this.assertToken(tokeniser, in, 58);
            CalendarParserImpl.this.assertToken(tokeniser, in, name);
            CalendarParserImpl.this.assertToken(tokeniser, in, 10);
            handler.endComponent(name);
        }
    }

    private class ComponentListParser {
        private ComponentListParser() {
        }

        private void parse(StreamTokenizer tokeniser, Reader in, ContentHandler handler) throws IOException, ParseException, URISyntaxException, ParserException {
            while ("BEGIN".equals(tokeniser.sval)) {
                CalendarParserImpl.this.componentParser.parse(tokeniser, in, handler);
                CalendarParserImpl.this.absorbWhitespace(tokeniser, in);
                CalendarParserImpl.this.nextToken(tokeniser, in, false);
            }
        }
    }

    private class ParameterParser {
        private ParameterParser() {
        }

        private void parse(StreamTokenizer tokeniser, Reader in, ContentHandler handler) throws IOException, ParserException, URISyntaxException {
            CalendarParserImpl.this.assertToken(tokeniser, in, -3);
            String paramName = tokeniser.sval;
            if (CalendarParserImpl.this.log.isDebugEnabled()) {
                CalendarParserImpl.this.log.debug("Parameter [" + paramName + "]");
            }
            CalendarParserImpl.this.assertToken(tokeniser, in, 61);
            StringBuilder paramValue = new StringBuilder();
            if (CalendarParserImpl.this.nextToken(tokeniser, in) == 34) {
                paramValue.append('\"');
                paramValue.append(tokeniser.sval);
                paramValue.append('\"');
            } else if (tokeniser.sval != null) {
                paramValue.append(tokeniser.sval);
                int nextToken = CalendarParserImpl.this.nextToken(tokeniser, in);
                while (nextToken != 59 && nextToken != 58 && nextToken != 44) {
                    if (tokeniser.ttype == -3) {
                        paramValue.append(tokeniser.sval);
                    } else {
                        paramValue.append((char)tokeniser.ttype);
                    }
                    nextToken = CalendarParserImpl.this.nextToken(tokeniser, in);
                }
                tokeniser.pushBack();
            } else if (tokeniser.sval == null) {
                tokeniser.pushBack();
            }
            try {
                handler.parameter(paramName, paramValue.toString());
            }
            catch (ClassCastException cce) {
                throw new ParserException("Error parsing parameter", CalendarParserImpl.this.getLineNumber(tokeniser, in), cce);
            }
        }
    }

    private class ParameterListParser {
        private ParameterListParser() {
        }

        public void parse(StreamTokenizer tokeniser, Reader in, ContentHandler handler) throws IOException, ParserException, URISyntaxException {
            while (CalendarParserImpl.this.nextToken(tokeniser, in) == 59) {
                CalendarParserImpl.this.paramParser.parse(tokeniser, in, handler);
            }
        }
    }

    private class PropertyParser {
        private static final String PARSE_DEBUG_MESSAGE = "Property [{0}]";
        private static final String PARSE_EXCEPTION_MESSAGE = "Property [{0}]";

        private PropertyParser() {
        }

        private void parse(StreamTokenizer tokeniser, Reader in, ContentHandler handler) throws IOException, ParserException, URISyntaxException, ParseException {
            String name = tokeniser.sval;
            if (CalendarParserImpl.this.log.isDebugEnabled()) {
                CalendarParserImpl.this.log.debug(MessageFormat.format("Property [{0}]", name));
            }
            handler.startProperty(name);
            CalendarParserImpl.this.paramListParser.parse(tokeniser, in, handler);
            StringBuilder value = new StringBuilder();
            tokeniser.ordinaryChar(34);
            int nextToken = CalendarParserImpl.this.nextToken(tokeniser, in);
            while (nextToken != 10) {
                if (tokeniser.ttype == -3) {
                    value.append(tokeniser.sval);
                } else {
                    value.append((char)tokeniser.ttype);
                }
                nextToken = CalendarParserImpl.this.nextToken(tokeniser, in);
            }
            tokeniser.quoteChar(34);
            try {
                handler.propertyValue(value.toString());
            }
            catch (ParseException e) {
                ParseException eNew = new ParseException("[" + name + "] " + e.getMessage(), e.getErrorOffset());
                eNew.initCause(e);
                throw eNew;
            }
            handler.endProperty(name);
        }
    }

    private class PropertyListParser {
        private PropertyListParser() {
        }

        public void parse(StreamTokenizer tokeniser, Reader in, ContentHandler handler) throws IOException, ParseException, URISyntaxException, ParserException {
            CalendarParserImpl.this.assertToken(tokeniser, in, -3);
            while (!"END".equals(tokeniser.sval)) {
                if ("BEGIN".equals(tokeniser.sval)) {
                    CalendarParserImpl.this.componentParser.parse(tokeniser, in, handler);
                } else if (tokeniser.sval != null) {
                    CalendarParserImpl.this.propertyParser.parse(tokeniser, in, handler);
                } else if (!CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed")) {
                    throw new ParserException("Invalid property name", CalendarParserImpl.this.getLineNumber(tokeniser, in));
                }
                CalendarParserImpl.this.absorbWhitespace(tokeniser, in);
                CalendarParserImpl.this.nextToken(tokeniser, in, false);
            }
        }
    }
}

