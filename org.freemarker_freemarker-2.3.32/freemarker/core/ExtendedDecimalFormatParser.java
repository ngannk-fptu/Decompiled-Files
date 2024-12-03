/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.template.utility.StringUtil;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

class ExtendedDecimalFormatParser {
    private static final String PARAM_ROUNDING_MODE = "roundingMode";
    private static final String PARAM_MULTIPIER = "multipier";
    private static final String PARAM_MULTIPLIER = "multiplier";
    private static final String PARAM_DECIMAL_SEPARATOR = "decimalSeparator";
    private static final String PARAM_MONETARY_DECIMAL_SEPARATOR = "monetaryDecimalSeparator";
    private static final String PARAM_GROUP_SEPARATOR = "groupingSeparator";
    private static final String PARAM_EXPONENT_SEPARATOR = "exponentSeparator";
    private static final String PARAM_MINUS_SIGN = "minusSign";
    private static final String PARAM_INFINITY = "infinity";
    private static final String PARAM_NAN = "nan";
    private static final String PARAM_PERCENT = "percent";
    private static final String PARAM_PER_MILL = "perMill";
    private static final String PARAM_ZERO_DIGIT = "zeroDigit";
    private static final String PARAM_CURRENCY_CODE = "currencyCode";
    private static final String PARAM_CURRENCY_SYMBOL = "currencySymbol";
    private static final String PARAM_VALUE_RND_UP = "up";
    private static final String PARAM_VALUE_RND_DOWN = "down";
    private static final String PARAM_VALUE_RND_CEILING = "ceiling";
    private static final String PARAM_VALUE_RND_FLOOR = "floor";
    private static final String PARAM_VALUE_RND_HALF_DOWN = "halfDown";
    private static final String PARAM_VALUE_RND_HALF_EVEN = "halfEven";
    private static final String PARAM_VALUE_RND_HALF_UP = "halfUp";
    private static final String PARAM_VALUE_RND_UNNECESSARY = "unnecessary";
    private static final HashMap<String, ? extends ParameterHandler> PARAM_HANDLERS;
    private static final String SNIP_MARK = "[...]";
    private static final int MAX_QUOTATION_LENGTH = 10;
    private final String src;
    private int pos = 0;
    private final DecimalFormatSymbols symbols;
    private RoundingMode roundingMode;
    private Integer multiplier;

    static DecimalFormat parse(String formatString, Locale locale) throws ParseException {
        return new ExtendedDecimalFormatParser(formatString, locale).parse();
    }

    private DecimalFormat parse() throws ParseException {
        DecimalFormat decimalFormat;
        String stdPattern = this.fetchStandardPattern();
        this.skipWS();
        this.parseFormatStringExtension();
        try {
            decimalFormat = new DecimalFormat(stdPattern, this.symbols);
        }
        catch (IllegalArgumentException e) {
            ParseException pe = new ParseException(e.getMessage(), 0);
            if (e.getCause() != null) {
                try {
                    e.initCause(e.getCause());
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            throw pe;
        }
        if (this.roundingMode != null) {
            decimalFormat.setRoundingMode(this.roundingMode);
        }
        if (this.multiplier != null) {
            decimalFormat.setMultiplier(this.multiplier);
        }
        return decimalFormat;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void parseFormatStringExtension() throws ParseException {
        int ln = this.src.length();
        if (this.pos == ln) {
            return;
        }
        String currencySymbol = null;
        while (true) {
            int namePos = this.pos;
            String name = this.fetchName();
            if (name == null) {
                throw this.newExpectedSgParseException("name");
            }
            this.skipWS();
            if (!this.fetchChar('=')) {
                throw this.newExpectedSgParseException("\"=\"");
            }
            this.skipWS();
            int valuePos = this.pos;
            String value = this.fetchValue();
            if (value == null) {
                throw this.newExpectedSgParseException("value");
            }
            int paramEndPos = this.pos;
            ParameterHandler handler = PARAM_HANDLERS.get(name);
            if (handler == null) {
                if (!name.equals(PARAM_CURRENCY_SYMBOL)) throw this.newUnknownParameterException(name, namePos);
                currencySymbol = value;
            } else {
                try {
                    handler.handle(this, value);
                }
                catch (InvalidParameterValueException e) {
                    throw this.newInvalidParameterValueException(name, value, valuePos, e);
                }
            }
            this.skipWS();
            if (this.fetchChar(',')) {
                this.skipWS();
                continue;
            }
            if (this.pos == ln) break;
            if (this.pos == paramEndPos) throw this.newExpectedSgParseException("parameter separator whitespace or comma");
        }
        if (currencySymbol == null) return;
        this.symbols.setCurrencySymbol(currencySymbol);
    }

    private ParseException newInvalidParameterValueException(String name, String value, int valuePos, InvalidParameterValueException e) {
        return new ParseException(StringUtil.jQuote(value) + " is an invalid value for the \"" + name + "\" parameter: " + e.message, valuePos);
    }

    private ParseException newUnknownParameterException(String name, int namePos) throws ParseException {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Unsupported parameter name, ").append(StringUtil.jQuote(name));
        sb.append(". The supported names are: ");
        Set<String> legalNames = PARAM_HANDLERS.keySet();
        Object[] legalNameArr = legalNames.toArray(new String[legalNames.size()]);
        Arrays.sort(legalNameArr);
        for (int i = 0; i < legalNameArr.length; ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append((String)legalNameArr[i]);
        }
        return new ParseException(sb.toString(), namePos);
    }

    private void skipWS() {
        int ln = this.src.length();
        while (this.pos < ln && this.isWS(this.src.charAt(this.pos))) {
            ++this.pos;
        }
    }

    private boolean fetchChar(char fetchedChar) {
        if (this.pos < this.src.length() && this.src.charAt(this.pos) == fetchedChar) {
            ++this.pos;
            return true;
        }
        return false;
    }

    private boolean isWS(char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '\u00a0';
    }

    private String fetchName() throws ParseException {
        int ln = this.src.length();
        int startPos = this.pos;
        boolean firstChar = true;
        while (this.pos < ln) {
            char c = this.src.charAt(this.pos);
            if (firstChar) {
                if (!Character.isJavaIdentifierStart(c)) break;
                firstChar = false;
            } else if (!Character.isJavaIdentifierPart(c)) break;
            ++this.pos;
        }
        return !firstChar ? this.src.substring(startPos, this.pos) : null;
    }

    /*
     * Enabled aggressive block sorting
     */
    private String fetchValue() throws ParseException {
        int ln = this.src.length();
        int startPos = this.pos;
        char openedQuot = '\u0000';
        boolean needsUnescaping = false;
        while (this.pos < ln) {
            block10: {
                char c;
                block11: {
                    c = this.src.charAt(this.pos);
                    if (c != '\'' && c != '\"') break block11;
                    if (openedQuot == '\u0000') {
                        if (startPos != this.pos) {
                            throw new ParseException("The " + c + " character can only be used for quoting values, but it was in the middle of an non-quoted value.", this.pos);
                        }
                        openedQuot = c;
                        break block10;
                    } else if (c == openedQuot) {
                        if (this.pos + 1 < ln && this.src.charAt(this.pos + 1) == openedQuot) {
                            ++this.pos;
                            needsUnescaping = true;
                            break block10;
                        } else {
                            String string;
                            String str = this.src.substring(startPos + 1, this.pos);
                            ++this.pos;
                            if (needsUnescaping) {
                                string = this.unescape(str, openedQuot);
                                return string;
                            }
                            string = str;
                            return string;
                        }
                    }
                    break block10;
                }
                if (openedQuot == '\u0000' && !Character.isJavaIdentifierPart(c)) break;
            }
            ++this.pos;
        }
        if (openedQuot != '\u0000') {
            throw new ParseException("The " + openedQuot + " quotation wasn't closed when the end of the source was reached.", this.pos);
        }
        if (startPos == this.pos) {
            return null;
        }
        String string = this.src.substring(startPos, this.pos);
        return string;
    }

    private String unescape(String s, char openedQuot) {
        return openedQuot == '\'' ? StringUtil.replace(s, "''", "'") : StringUtil.replace(s, "\"\"", "\"");
    }

    private String fetchStandardPattern() {
        String stdFormatStr;
        int pos;
        int ln = this.src.length();
        int semicolonCnt = 0;
        boolean quotedMode = false;
        for (pos = this.pos; pos < ln; ++pos) {
            char c = this.src.charAt(pos);
            if (c == ';' && !quotedMode) {
                if (++semicolonCnt != 2) continue;
                break;
            }
            if (c != '\'') continue;
            if (quotedMode) {
                if (pos + 1 < ln && this.src.charAt(pos + 1) == '\'') {
                    ++pos;
                    continue;
                }
                quotedMode = false;
                continue;
            }
            quotedMode = true;
        }
        if (semicolonCnt < 2) {
            stdFormatStr = this.src;
        } else {
            int stdEndPos = pos;
            if (this.src.charAt(pos - 1) == ';') {
                --stdEndPos;
            }
            stdFormatStr = this.src.substring(0, stdEndPos);
        }
        if (pos < ln) {
            ++pos;
        }
        this.pos = pos;
        return stdFormatStr;
    }

    private ExtendedDecimalFormatParser(String formatString, Locale locale) {
        this.src = formatString;
        this.symbols = DecimalFormatSymbols.getInstance(locale);
    }

    private ParseException newExpectedSgParseException(String expectedThing) {
        int qEndPos;
        int i;
        for (i = this.src.length() - 1; i >= 0 && Character.isWhitespace(this.src.charAt(i)); --i) {
        }
        int ln = i + 1;
        String quotation = this.pos < ln ? ((qEndPos = this.pos + 10) >= ln ? this.src.substring(this.pos, ln) : this.src.substring(this.pos, qEndPos - SNIP_MARK.length()) + SNIP_MARK) : null;
        return new ParseException("Expected a(n) " + expectedThing + " at position " + this.pos + " (0-based), but " + (quotation == null ? "reached the end of the input." : "found: " + quotation), this.pos);
    }

    static {
        HashMap<String, ParameterHandler> m = new HashMap<String, ParameterHandler>();
        m.put(PARAM_ROUNDING_MODE, new ParameterHandler(){

            @Override
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                RoundingMode parsedValue;
                if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_UP)) {
                    parsedValue = RoundingMode.UP;
                } else if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_DOWN)) {
                    parsedValue = RoundingMode.DOWN;
                } else if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_CEILING)) {
                    parsedValue = RoundingMode.CEILING;
                } else if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_FLOOR)) {
                    parsedValue = RoundingMode.FLOOR;
                } else if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_HALF_DOWN)) {
                    parsedValue = RoundingMode.HALF_DOWN;
                } else if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_HALF_EVEN)) {
                    parsedValue = RoundingMode.HALF_EVEN;
                } else if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_HALF_UP)) {
                    parsedValue = RoundingMode.HALF_UP;
                } else if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_UNNECESSARY)) {
                    parsedValue = RoundingMode.UNNECESSARY;
                } else {
                    throw new InvalidParameterValueException("Should be one of: up, down, ceiling, floor, halfDown, halfEven, unnecessary");
                }
                parser.roundingMode = parsedValue;
            }
        });
        ParameterHandler multiplierParamHandler = new ParameterHandler(){

            @Override
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                try {
                    parser.multiplier = Integer.valueOf(value);
                }
                catch (NumberFormatException e) {
                    throw new InvalidParameterValueException("Malformed integer.");
                }
            }
        };
        m.put(PARAM_MULTIPLIER, multiplierParamHandler);
        m.put(PARAM_MULTIPIER, multiplierParamHandler);
        m.put(PARAM_DECIMAL_SEPARATOR, new ParameterHandler(){

            @Override
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                if (value.length() != 1) {
                    throw new InvalidParameterValueException("Must contain exactly 1 character.");
                }
                parser.symbols.setDecimalSeparator(value.charAt(0));
            }
        });
        m.put(PARAM_MONETARY_DECIMAL_SEPARATOR, new ParameterHandler(){

            @Override
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                if (value.length() != 1) {
                    throw new InvalidParameterValueException("Must contain exactly 1 character.");
                }
                parser.symbols.setMonetaryDecimalSeparator(value.charAt(0));
            }
        });
        m.put(PARAM_GROUP_SEPARATOR, new ParameterHandler(){

            @Override
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                if (value.length() != 1) {
                    throw new InvalidParameterValueException("Must contain exactly 1 character.");
                }
                parser.symbols.setGroupingSeparator(value.charAt(0));
            }
        });
        m.put(PARAM_EXPONENT_SEPARATOR, new ParameterHandler(){

            @Override
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                parser.symbols.setExponentSeparator(value);
            }
        });
        m.put(PARAM_MINUS_SIGN, new ParameterHandler(){

            @Override
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                if (value.length() != 1) {
                    throw new InvalidParameterValueException("Must contain exactly 1 character.");
                }
                parser.symbols.setMinusSign(value.charAt(0));
            }
        });
        m.put(PARAM_INFINITY, new ParameterHandler(){

            @Override
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                parser.symbols.setInfinity(value);
            }
        });
        m.put(PARAM_NAN, new ParameterHandler(){

            @Override
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                parser.symbols.setNaN(value);
            }
        });
        m.put(PARAM_PERCENT, new ParameterHandler(){

            @Override
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                if (value.length() != 1) {
                    throw new InvalidParameterValueException("Must contain exactly 1 character.");
                }
                parser.symbols.setPercent(value.charAt(0));
            }
        });
        m.put(PARAM_PER_MILL, new ParameterHandler(){

            @Override
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                if (value.length() != 1) {
                    throw new InvalidParameterValueException("Must contain exactly 1 character.");
                }
                parser.symbols.setPerMill(value.charAt(0));
            }
        });
        m.put(PARAM_ZERO_DIGIT, new ParameterHandler(){

            @Override
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                if (value.length() != 1) {
                    throw new InvalidParameterValueException("Must contain exactly 1 character.");
                }
                parser.symbols.setZeroDigit(value.charAt(0));
            }
        });
        m.put(PARAM_CURRENCY_CODE, new ParameterHandler(){

            @Override
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                Currency currency;
                try {
                    currency = Currency.getInstance(value);
                }
                catch (IllegalArgumentException e) {
                    throw new InvalidParameterValueException("Not a known ISO 4217 code.");
                }
                parser.symbols.setCurrency(currency);
            }
        });
        PARAM_HANDLERS = m;
    }

    private static class InvalidParameterValueException
    extends Exception {
        private final String message;

        public InvalidParameterValueException(String message) {
            this.message = message;
        }
    }

    private static interface ParameterHandler {
        public void handle(ExtendedDecimalFormatParser var1, String var2) throws InvalidParameterValueException;
    }
}

