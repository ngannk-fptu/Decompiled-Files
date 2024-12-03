/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.util.pattern.CaptureTheRestPathElement;
import org.springframework.web.util.pattern.CaptureVariablePathElement;
import org.springframework.web.util.pattern.LiteralPathElement;
import org.springframework.web.util.pattern.PathElement;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import org.springframework.web.util.pattern.PatternParseException;
import org.springframework.web.util.pattern.RegexPathElement;
import org.springframework.web.util.pattern.SeparatorPathElement;
import org.springframework.web.util.pattern.SingleCharWildcardedPathElement;
import org.springframework.web.util.pattern.WildcardPathElement;
import org.springframework.web.util.pattern.WildcardTheRestPathElement;

class InternalPathPatternParser {
    private final PathPatternParser parser;
    private char[] pathPatternData = new char[0];
    private int pathPatternLength;
    int pos;
    private int singleCharWildcardCount;
    private boolean wildcard = false;
    private boolean isCaptureTheRestVariable = false;
    private boolean insideVariableCapture = false;
    private int variableCaptureCount = 0;
    private int pathElementStart;
    private int variableCaptureStart;
    @Nullable
    private List<String> capturedVariableNames;
    @Nullable
    private PathElement headPE;
    @Nullable
    private PathElement currentPE;

    InternalPathPatternParser(PathPatternParser parentParser) {
        this.parser = parentParser;
    }

    public PathPattern parse(String pathPattern) throws PatternParseException {
        Assert.notNull((Object)pathPattern, "Path pattern must not be null");
        this.pathPatternData = pathPattern.toCharArray();
        this.pathPatternLength = this.pathPatternData.length;
        this.headPE = null;
        this.currentPE = null;
        this.capturedVariableNames = null;
        this.pathElementStart = -1;
        this.pos = 0;
        this.resetPathElementState();
        while (this.pos < this.pathPatternLength) {
            char ch = this.pathPatternData[this.pos];
            if (ch == this.parser.getSeparator()) {
                if (this.pathElementStart != -1) {
                    this.pushPathElement(this.createPathElement());
                }
                if (this.peekDoubleWildcard()) {
                    this.pushPathElement(new WildcardTheRestPathElement(this.pos, this.parser.getSeparator()));
                    this.pos += 2;
                } else {
                    this.pushPathElement(new SeparatorPathElement(this.pos, this.parser.getSeparator()));
                }
            } else {
                if (this.pathElementStart == -1) {
                    this.pathElementStart = this.pos;
                }
                if (ch == '?') {
                    ++this.singleCharWildcardCount;
                } else if (ch == '{') {
                    if (this.insideVariableCapture) {
                        throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.ILLEGAL_NESTED_CAPTURE, new Object[0]);
                    }
                    this.insideVariableCapture = true;
                    this.variableCaptureStart = this.pos;
                } else if (ch == '}') {
                    if (!this.insideVariableCapture) {
                        throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.MISSING_OPEN_CAPTURE, new Object[0]);
                    }
                    this.insideVariableCapture = false;
                    if (this.isCaptureTheRestVariable && this.pos + 1 < this.pathPatternLength) {
                        throw new PatternParseException(this.pos + 1, this.pathPatternData, PatternParseException.PatternMessage.NO_MORE_DATA_EXPECTED_AFTER_CAPTURE_THE_REST, new Object[0]);
                    }
                    ++this.variableCaptureCount;
                } else if (ch == ':') {
                    if (this.insideVariableCapture && !this.isCaptureTheRestVariable) {
                        this.skipCaptureRegex();
                        this.insideVariableCapture = false;
                        ++this.variableCaptureCount;
                    }
                } else if (ch == '*') {
                    if (this.insideVariableCapture && this.variableCaptureStart == this.pos - 1) {
                        this.isCaptureTheRestVariable = true;
                    }
                    this.wildcard = true;
                }
                if (this.insideVariableCapture) {
                    if (this.variableCaptureStart + 1 + (this.isCaptureTheRestVariable ? 1 : 0) == this.pos && !Character.isJavaIdentifierStart(ch)) {
                        throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.ILLEGAL_CHARACTER_AT_START_OF_CAPTURE_DESCRIPTOR, Character.toString(ch));
                    }
                    if (this.pos > this.variableCaptureStart + 1 + (this.isCaptureTheRestVariable ? 1 : 0) && !Character.isJavaIdentifierPart(ch)) {
                        throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.ILLEGAL_CHARACTER_IN_CAPTURE_DESCRIPTOR, Character.toString(ch));
                    }
                }
            }
            ++this.pos;
        }
        if (this.pathElementStart != -1) {
            this.pushPathElement(this.createPathElement());
        }
        return new PathPattern(pathPattern, this.parser, this.headPE);
    }

    private void skipCaptureRegex() {
        ++this.pos;
        int regexStart = this.pos;
        int curlyBracketDepth = 0;
        boolean previousBackslash = false;
        while (this.pos < this.pathPatternLength) {
            char ch = this.pathPatternData[this.pos];
            if (ch == '\\' && !previousBackslash) {
                ++this.pos;
                previousBackslash = true;
                continue;
            }
            if (ch == '{' && !previousBackslash) {
                ++curlyBracketDepth;
            } else if (ch == '}' && !previousBackslash) {
                if (curlyBracketDepth == 0) {
                    if (regexStart == this.pos) {
                        throw new PatternParseException(regexStart, this.pathPatternData, PatternParseException.PatternMessage.MISSING_REGEX_CONSTRAINT, new Object[0]);
                    }
                    return;
                }
                --curlyBracketDepth;
            }
            if (ch == this.parser.getSeparator() && !previousBackslash) {
                throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.MISSING_CLOSE_CAPTURE, new Object[0]);
            }
            ++this.pos;
            previousBackslash = false;
        }
        throw new PatternParseException(this.pos - 1, this.pathPatternData, PatternParseException.PatternMessage.MISSING_CLOSE_CAPTURE, new Object[0]);
    }

    private boolean peekDoubleWildcard() {
        if (this.pos + 2 >= this.pathPatternLength) {
            return false;
        }
        if (this.pathPatternData[this.pos + 1] != '*' || this.pathPatternData[this.pos + 2] != '*') {
            return false;
        }
        return this.pos + 3 == this.pathPatternLength;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void pushPathElement(PathElement newPathElement) {
        if (newPathElement instanceof CaptureTheRestPathElement) {
            if (this.currentPE == null) {
                this.headPE = newPathElement;
                this.currentPE = newPathElement;
            } else {
                if (!(this.currentPE instanceof SeparatorPathElement)) throw new IllegalStateException("Expected SeparatorPathElement but was " + this.currentPE);
                PathElement peBeforeSeparator = this.currentPE.prev;
                if (peBeforeSeparator == null) {
                    this.headPE = newPathElement;
                    newPathElement.prev = null;
                } else {
                    peBeforeSeparator.next = newPathElement;
                    newPathElement.prev = peBeforeSeparator;
                }
                this.currentPE = newPathElement;
            }
        } else if (this.headPE == null) {
            this.headPE = newPathElement;
            this.currentPE = newPathElement;
        } else if (this.currentPE != null) {
            this.currentPE.next = newPathElement;
            newPathElement.prev = this.currentPE;
            this.currentPE = newPathElement;
        }
        this.resetPathElementState();
    }

    private char[] getPathElementText() {
        char[] pathElementText = new char[this.pos - this.pathElementStart];
        System.arraycopy(this.pathPatternData, this.pathElementStart, pathElementText, 0, this.pos - this.pathElementStart);
        return pathElementText;
    }

    private PathElement createPathElement() {
        if (this.insideVariableCapture) {
            throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.MISSING_CLOSE_CAPTURE, new Object[0]);
        }
        PathElement newPE = null;
        if (this.variableCaptureCount > 0) {
            if (this.variableCaptureCount == 1 && this.pathElementStart == this.variableCaptureStart && this.pathPatternData[this.pos - 1] == '}') {
                if (this.isCaptureTheRestVariable) {
                    newPE = new CaptureTheRestPathElement(this.pathElementStart, this.getPathElementText(), this.parser.getSeparator());
                } else {
                    try {
                        newPE = new CaptureVariablePathElement(this.pathElementStart, this.getPathElementText(), this.parser.isCaseSensitive(), this.parser.getSeparator());
                    }
                    catch (PatternSyntaxException pse) {
                        throw new PatternParseException(pse, this.findRegexStart(this.pathPatternData, this.pathElementStart) + pse.getIndex(), this.pathPatternData, PatternParseException.PatternMessage.REGEX_PATTERN_SYNTAX_EXCEPTION, new Object[0]);
                    }
                    this.recordCapturedVariable(this.pathElementStart, ((CaptureVariablePathElement)newPE).getVariableName());
                }
            } else {
                if (this.isCaptureTheRestVariable) {
                    throw new PatternParseException(this.pathElementStart, this.pathPatternData, PatternParseException.PatternMessage.CAPTURE_ALL_IS_STANDALONE_CONSTRUCT, new Object[0]);
                }
                RegexPathElement newRegexSection = new RegexPathElement(this.pathElementStart, this.getPathElementText(), this.parser.isCaseSensitive(), this.pathPatternData, this.parser.getSeparator());
                for (String variableName : newRegexSection.getVariableNames()) {
                    this.recordCapturedVariable(this.pathElementStart, variableName);
                }
                newPE = newRegexSection;
            }
        } else {
            newPE = this.wildcard ? (this.pos - 1 == this.pathElementStart ? new WildcardPathElement(this.pathElementStart, this.parser.getSeparator()) : new RegexPathElement(this.pathElementStart, this.getPathElementText(), this.parser.isCaseSensitive(), this.pathPatternData, this.parser.getSeparator())) : (this.singleCharWildcardCount != 0 ? new SingleCharWildcardedPathElement(this.pathElementStart, this.getPathElementText(), this.singleCharWildcardCount, this.parser.isCaseSensitive(), this.parser.getSeparator()) : new LiteralPathElement(this.pathElementStart, this.getPathElementText(), this.parser.isCaseSensitive(), this.parser.getSeparator()));
        }
        return newPE;
    }

    private int findRegexStart(char[] data, int offset) {
        for (int pos = offset; pos < data.length; ++pos) {
            if (data[pos] != ':') continue;
            return pos + 1;
        }
        return -1;
    }

    private void resetPathElementState() {
        this.pathElementStart = -1;
        this.singleCharWildcardCount = 0;
        this.insideVariableCapture = false;
        this.variableCaptureCount = 0;
        this.wildcard = false;
        this.isCaptureTheRestVariable = false;
        this.variableCaptureStart = -1;
    }

    private void recordCapturedVariable(int pos, String variableName) {
        if (this.capturedVariableNames == null) {
            this.capturedVariableNames = new ArrayList<String>();
        }
        if (this.capturedVariableNames.contains(variableName)) {
            throw new PatternParseException(pos, this.pathPatternData, PatternParseException.PatternMessage.ILLEGAL_DOUBLE_CAPTURE, variableName);
        }
        this.capturedVariableNames.add(variableName);
    }
}

