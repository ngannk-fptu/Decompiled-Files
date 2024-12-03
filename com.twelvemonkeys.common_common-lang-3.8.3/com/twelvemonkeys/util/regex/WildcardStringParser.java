/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.regex;

import java.io.PrintStream;

@Deprecated
public class WildcardStringParser {
    public static final char[] ALPHABET = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '\u00e6', '\u00f8', '\u00e5', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'N', 'M', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '\u00c6', '\u00d8', '\u00c5', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '_', '-'};
    public static final char FREE_RANGE_CHARACTER = '*';
    public static final char FREE_PASS_CHARACTER = '?';
    boolean initialized;
    String stringMask;
    WildcardStringParserState initialState;
    int totalNumberOfStringsParsed;
    boolean debugging;
    PrintStream out;

    public WildcardStringParser(String string) {
        this(string, false);
    }

    public WildcardStringParser(String string, boolean bl) {
        this(string, bl, System.out);
    }

    public WildcardStringParser(String string, boolean bl, PrintStream printStream) {
        this.stringMask = string;
        this.debugging = bl;
        this.out = printStream;
        this.initialized = this.buildAutomaton();
    }

    private boolean checkIfStateInWildcardRange(WildcardStringParserState wildcardStringParserState) {
        WildcardStringParserState wildcardStringParserState2 = wildcardStringParserState;
        while (wildcardStringParserState2.previousState != null) {
            wildcardStringParserState2 = wildcardStringParserState2.previousState;
            if (WildcardStringParser.isFreeRangeCharacter(wildcardStringParserState2.character)) {
                return true;
            }
            if (WildcardStringParser.isFreePassCharacter(wildcardStringParserState2.character)) continue;
            return false;
        }
        return false;
    }

    private boolean checkIfLastFreeRangeState(WildcardStringParserState wildcardStringParserState) {
        if (WildcardStringParser.isFreeRangeCharacter(wildcardStringParserState.character)) {
            return true;
        }
        return WildcardStringParser.isFreePassCharacter(wildcardStringParserState.character) && this.checkIfStateInWildcardRange(wildcardStringParserState);
    }

    private boolean isTrivialAutomaton() {
        for (int i = 0; i < this.stringMask.length(); ++i) {
            if (WildcardStringParser.isFreeRangeCharacter(this.stringMask.charAt(i))) continue;
            return false;
        }
        return true;
    }

    private boolean buildAutomaton() {
        WildcardStringParserState wildcardStringParserState = null;
        WildcardStringParserState wildcardStringParserState2 = null;
        WildcardStringParserState wildcardStringParserState3 = null;
        if (this.stringMask != null && this.stringMask.length() > 0) {
            wildcardStringParserState2 = new WildcardStringParserState(this.stringMask.charAt(0));
            wildcardStringParserState2.automatonStateNumber = 0;
            wildcardStringParserState2.previousState = null;
            if (this.checkIfLastFreeRangeState(wildcardStringParserState2)) {
                wildcardStringParserState3 = wildcardStringParserState2;
            }
        } else {
            System.err.println("string mask provided are null or empty - aborting!");
            return false;
        }
        this.initialState = wildcardStringParserState = wildcardStringParserState2;
        this.initialState.automatonStateNumber = 0;
        for (int i = 1; i < this.stringMask.length(); ++i) {
            char c = this.stringMask.charAt(i);
            if (!WildcardStringParser.isInAlphabet(c) && !WildcardStringParser.isWildcardCharacter(c)) {
                System.err.println("one or more characters in string mask are not legal characters - aborting!");
                return false;
            }
            wildcardStringParserState.lastFreeRangeState = wildcardStringParserState3;
            wildcardStringParserState2 = new WildcardStringParserState(c);
            wildcardStringParserState2.automatonStateNumber = i;
            wildcardStringParserState2.previousState = wildcardStringParserState;
            if (this.checkIfLastFreeRangeState(wildcardStringParserState2)) {
                wildcardStringParserState3 = wildcardStringParserState2;
            }
            wildcardStringParserState.nextState = wildcardStringParserState2;
            wildcardStringParserState = wildcardStringParserState2;
            if (wildcardStringParserState.automatonStateNumber != this.stringMask.length() - 1) continue;
            wildcardStringParserState.lastFreeRangeState = wildcardStringParserState3;
        }
        this.totalNumberOfStringsParsed = 0;
        return true;
    }

    public static boolean isInAlphabet(char c) {
        for (int i = 0; i < ALPHABET.length; ++i) {
            if (c != ALPHABET[i]) continue;
            return true;
        }
        return false;
    }

    public static boolean isFreeRangeCharacter(char c) {
        return c == '*';
    }

    public static boolean isFreePassCharacter(char c) {
        return c == '?';
    }

    public static boolean isWildcardCharacter(char c) {
        return WildcardStringParser.isFreeRangeCharacter(c) || WildcardStringParser.isFreePassCharacter(c);
    }

    public String getStringMask() {
        return this.stringMask;
    }

    public boolean parseString(String string) {
        if (this.debugging) {
            this.out.println("parsing \"" + string + "\"...");
        }
        ++this.totalNumberOfStringsParsed;
        if (string == null) {
            if (this.debugging) {
                this.out.println("string to be parsed is null - rejection!");
            }
            return false;
        }
        ParsableString parsableString = new ParsableString(string);
        if (!parsableString.checkString()) {
            if (this.debugging) {
                this.out.println("one or more characters in string to be parsed are not legal characters - rejection!");
            }
            return false;
        }
        if (!this.initialized) {
            System.err.println("automaton is not initialized - rejection!");
            return false;
        }
        if (this.isTrivialAutomaton()) {
            if (this.debugging) {
                this.out.println("automaton represents a trivial string mask (accepts all strings) - acceptance!");
            }
            return true;
        }
        if (parsableString.isEmpty()) {
            if (this.debugging) {
                this.out.println("string to be parsed is empty and not trivial automaton - rejection!");
            }
            return false;
        }
        boolean bl = false;
        int n = 0;
        int n2 = 0;
        WildcardStringParserState wildcardStringParserState = null;
        if (parsableString.charArray[0] != this.initialState.character && !WildcardStringParser.isWildcardCharacter(this.initialState.character)) {
            if (this.debugging) {
                this.out.println("cannot enter first automaton state - rejection!");
            }
            return false;
        }
        wildcardStringParserState = this.initialState;
        parsableString.index = 0;
        if (WildcardStringParser.isFreePassCharacter(wildcardStringParserState.character)) {
            ++n;
        }
        for (int i = 0; i < parsableString.length(); ++i) {
            if (this.debugging) {
                this.out.println();
            }
            if (this.debugging) {
                this.out.println("parsing - index number " + i + ", active char: '" + parsableString.getActiveChar() + "' char string index: " + parsableString.index + " number of chars since last free-range state: " + n2);
            }
            if (this.debugging) {
                this.out.println("parsing - state: " + wildcardStringParserState.automatonStateNumber + " '" + wildcardStringParserState.character + "' - no of free-pass chars read: " + n);
            }
            if (this.debugging) {
                this.out.println("parsing - hasPerformedFreeRangeMovement: " + bl);
            }
            if (wildcardStringParserState.nextState == null) {
                if (this.debugging) {
                    this.out.println("parsing - runnerState.nextState == null");
                }
                if (WildcardStringParser.isFreeRangeCharacter(wildcardStringParserState.character)) {
                    if (bl) {
                        if (parsableString.reachedEndOfString()) {
                            if (n > n2) {
                                if (this.debugging) {
                                    this.out.println("no subsequent state (final state) and the state represents '*' - end of parsing string, but not enough characters read - rejection!");
                                }
                                return false;
                            }
                            if (this.debugging) {
                                this.out.println("no subsequent state (final state) and the state represents '*' - end of parsing string and enough characters read - acceptance!");
                            }
                            return true;
                        }
                        if (n > n2) {
                            if (this.debugging) {
                                this.out.println("no subsequent state (final state) and the state represents '*' - not the end of parsing string and not enough characters read - read next character");
                            }
                            ++parsableString.index;
                            ++n2;
                            continue;
                        }
                        if (this.debugging) {
                            this.out.println("no subsequent state (final state) and the state represents '*' - not the end of parsing string, but enough characters read - acceptance!");
                        }
                        return true;
                    }
                    if (this.debugging) {
                        this.out.println("no subsequent state (final state) and the state represents '*' - no skipping performed - acceptance!");
                    }
                    return true;
                }
                if (parsableString.reachedEndOfString()) {
                    if (bl && n > n2) {
                        if (this.debugging) {
                            this.out.println("no subsequent state (final state) and skipping has been performed and end of parsing string, but not enough characters read - rejection!");
                        }
                        return false;
                    }
                    if (this.debugging) {
                        this.out.println("no subsequent state (final state) and the end of the string to test is reached - acceptance!");
                    }
                    return true;
                }
                if (!this.debugging) continue;
                this.out.println("parsing - escaping process...");
                continue;
            }
            if (this.debugging) {
                this.out.println("parsing - runnerState.nextState != null");
            }
            if (WildcardStringParser.isFreeRangeCharacter(wildcardStringParserState.character)) {
                n = 0;
                n2 = 0;
                WildcardStringParserState wildcardStringParserState2 = wildcardStringParserState.nextState;
                while (wildcardStringParserState2 != null && WildcardStringParser.isFreePassCharacter(wildcardStringParserState2.character)) {
                    wildcardStringParserState = wildcardStringParserState2;
                    bl = true;
                    ++n;
                    wildcardStringParserState2 = wildcardStringParserState2.nextState;
                }
                if (wildcardStringParserState.nextState == null) {
                    if (this.debugging) {
                        this.out.println();
                    }
                    if (this.debugging) {
                        this.out.println("parsing - index number " + i + ", active char: '" + parsableString.getActiveChar() + "' char string index: " + parsableString.index + " number of chars since last free-range state: " + n2);
                    }
                    if (this.debugging) {
                        this.out.println("parsing - state: " + wildcardStringParserState.automatonStateNumber + " '" + wildcardStringParserState.character + "' - no of free-pass chars read: " + n);
                    }
                    if (this.debugging) {
                        this.out.println("parsing - hasPerformedFreeRangeMovement: " + bl);
                    }
                    return bl && n >= n2;
                }
            }
            if (WildcardStringParser.isFreeRangeCharacter(wildcardStringParserState.nextState.character)) {
                wildcardStringParserState = wildcardStringParserState.nextState;
                ++parsableString.index;
                ++n2;
                continue;
            }
            if (WildcardStringParser.isFreePassCharacter(wildcardStringParserState.nextState.character)) {
                wildcardStringParserState = wildcardStringParserState.nextState;
                ++parsableString.index;
                ++n;
                ++n2;
                continue;
            }
            if (!parsableString.reachedEndOfString() && wildcardStringParserState.nextState.character == parsableString.getSubsequentChar()) {
                wildcardStringParserState = wildcardStringParserState.nextState;
                ++parsableString.index;
                ++n2;
                continue;
            }
            if (wildcardStringParserState.lastFreeRangeState != null) {
                wildcardStringParserState = wildcardStringParserState.lastFreeRangeState;
                ++parsableString.index;
                ++n2;
                continue;
            }
            if (this.debugging) {
                this.out.println("the next state does not represent the same character as the next character in the string to test, and there are no last-free-range-state - rejection!");
            }
            return false;
        }
        if (this.debugging) {
            this.out.println("finished reading parsing string and not at any final state - rejection!");
        }
        return false;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!this.initialized) {
            stringBuilder.append(this.getClass().getName());
            stringBuilder.append(":  Not initialized properly!");
            stringBuilder.append("\n");
            stringBuilder.append("\n");
        } else {
            WildcardStringParserState wildcardStringParserState = this.initialState;
            stringBuilder.append(this.getClass().getName());
            stringBuilder.append(":  String mask ");
            stringBuilder.append(this.stringMask);
            stringBuilder.append("\n");
            stringBuilder.append("\n");
            stringBuilder.append("      Automaton: ");
            while (wildcardStringParserState != null) {
                stringBuilder.append(wildcardStringParserState.automatonStateNumber);
                stringBuilder.append(": ");
                stringBuilder.append(wildcardStringParserState.character);
                stringBuilder.append(" (");
                if (wildcardStringParserState.lastFreeRangeState != null) {
                    stringBuilder.append(wildcardStringParserState.lastFreeRangeState.automatonStateNumber);
                } else {
                    stringBuilder.append("-");
                }
                stringBuilder.append(")");
                if (wildcardStringParserState.nextState != null) {
                    stringBuilder.append("   -->   ");
                }
                wildcardStringParserState = wildcardStringParserState.nextState;
            }
            stringBuilder.append("\n");
            stringBuilder.append("      Format: <state index>: <character> (<last free state>)");
            stringBuilder.append("\n");
            stringBuilder.append("      Number of strings parsed: ").append(this.totalNumberOfStringsParsed);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public boolean equals(Object object) {
        if (object instanceof WildcardStringParser) {
            WildcardStringParser wildcardStringParser = (WildcardStringParser)object;
            return wildcardStringParser.initialized == this.initialized && wildcardStringParser.stringMask == this.stringMask;
        }
        return super.equals(object);
    }

    public int hashCode() {
        return super.hashCode();
    }

    protected Object clone() throws CloneNotSupportedException {
        if (this.initialized) {
            return new WildcardStringParser(this.stringMask);
        }
        return null;
    }

    protected void finalize() throws Throwable {
    }

    class ParsableString {
        char[] charArray;
        int index;

        ParsableString(String string) {
            if (string != null) {
                this.charArray = string.toCharArray();
            }
            this.index = -1;
        }

        boolean reachedEndOfString() {
            return this.index == this.charArray.length - 1;
        }

        int length() {
            return this.charArray.length;
        }

        char getActiveChar() {
            if (this.index > -1 && this.index < this.charArray.length) {
                return this.charArray[this.index];
            }
            System.err.println(this.getClass().getName() + ": trying to access character outside character array!");
            return ' ';
        }

        char getSubsequentChar() {
            if (this.index > -1 && this.index + 1 < this.charArray.length) {
                return this.charArray[this.index + 1];
            }
            System.err.println(this.getClass().getName() + ": trying to access character outside character array!");
            return ' ';
        }

        boolean checkString() {
            if (!this.isEmpty()) {
                for (int i = 0; i < this.charArray.length; ++i) {
                    if (WildcardStringParser.isInAlphabet(this.charArray[i])) continue;
                    return false;
                }
            }
            return true;
        }

        boolean isEmpty() {
            return this.charArray == null || this.charArray.length == 0;
        }

        public String toString() {
            return new String(this.charArray);
        }
    }

    class WildcardStringParserState {
        int automatonStateNumber;
        char character;
        WildcardStringParserState previousState;
        WildcardStringParserState nextState;
        WildcardStringParserState lastFreeRangeState;

        public WildcardStringParserState(char c) {
            this.character = c;
        }
    }
}

