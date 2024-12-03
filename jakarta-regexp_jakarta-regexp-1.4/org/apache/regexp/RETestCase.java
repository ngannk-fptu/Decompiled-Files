/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

import java.io.StringBufferInputStream;
import java.io.StringReader;
import org.apache.regexp.CharacterArrayCharacterIterator;
import org.apache.regexp.CharacterIterator;
import org.apache.regexp.RE;
import org.apache.regexp.RETest;
import org.apache.regexp.ReaderCharacterIterator;
import org.apache.regexp.StreamCharacterIterator;
import org.apache.regexp.StringCharacterIterator;

final class RETestCase {
    private final StringBuffer log = new StringBuffer();
    private final int number;
    private final String tag;
    private final String pattern;
    private final String toMatch;
    private final boolean badPattern;
    private final boolean shouldMatch;
    private final String[] parens;
    private final RETest test;
    private RE regexp;

    public RETestCase(RETest rETest, String string, String string2, String string3, boolean bl, boolean bl2, String[] stringArray) {
        this.number = ++rETest.testCount;
        this.test = rETest;
        this.tag = string;
        this.pattern = string2;
        this.toMatch = string3;
        this.badPattern = bl;
        this.shouldMatch = bl2;
        if (stringArray != null) {
            this.parens = new String[stringArray.length];
            int n = 0;
            while (n < stringArray.length) {
                this.parens[n] = stringArray[n];
                ++n;
            }
        } else {
            this.parens = null;
        }
    }

    public void runTest() {
        this.test.say(this.tag + "(" + this.number + "): " + this.pattern);
        if (this.testCreation()) {
            this.testMatch();
        }
    }

    boolean testCreation() {
        try {
            this.regexp = new RE();
            this.regexp.setProgram(this.test.compiler.compile(this.pattern));
            if (this.badPattern) {
                this.test.fail(this.log, "Was expected to be an error, but wasn't.");
                return false;
            }
            return true;
        }
        catch (Exception exception) {
            if (this.badPattern) {
                this.log.append("   Match: ERR\n");
                this.success("Produces an error (" + exception.toString() + "), as expected.");
                return false;
            }
            String string = exception.getMessage() == null ? exception.toString() : exception.getMessage();
            this.test.fail(this.log, "Produces an unexpected exception \"" + string + "\"");
            exception.printStackTrace();
        }
        catch (Error error) {
            this.test.fail(this.log, "Compiler threw fatal error \"" + error.getMessage() + "\"");
            error.printStackTrace();
        }
        return false;
    }

    private void testMatch() {
        this.log.append("   Match against: '" + this.toMatch + "'\n");
        try {
            boolean bl = this.regexp.match(this.toMatch);
            this.log.append("   Matched: " + (bl ? "YES" : "NO") + "\n");
            if (this.checkResult(bl) && (!this.shouldMatch || this.checkParens())) {
                this.log.append("   Match using StringCharacterIterator\n");
                if (!this.tryMatchUsingCI(new StringCharacterIterator(this.toMatch))) {
                    return;
                }
                this.log.append("   Match using CharacterArrayCharacterIterator\n");
                if (!this.tryMatchUsingCI(new CharacterArrayCharacterIterator(this.toMatch.toCharArray(), 0, this.toMatch.length()))) {
                    return;
                }
                this.log.append("   Match using StreamCharacterIterator\n");
                if (!this.tryMatchUsingCI(new StreamCharacterIterator(new StringBufferInputStream(this.toMatch)))) {
                    return;
                }
                this.log.append("   Match using ReaderCharacterIterator\n");
                if (!this.tryMatchUsingCI(new ReaderCharacterIterator(new StringReader(this.toMatch)))) {
                    return;
                }
            }
        }
        catch (Exception exception) {
            this.test.fail(this.log, "Matcher threw exception: " + exception.toString());
            exception.printStackTrace();
        }
        catch (Error error) {
            this.test.fail(this.log, "Matcher threw fatal error \"" + error.getMessage() + "\"");
            error.printStackTrace();
        }
    }

    private boolean checkResult(boolean bl) {
        if (bl == this.shouldMatch) {
            this.success((this.shouldMatch ? "Matched" : "Did not match") + " \"" + this.toMatch + "\", as expected:");
            return true;
        }
        if (this.shouldMatch) {
            this.test.fail(this.log, "Did not match \"" + this.toMatch + "\", when expected to.");
        } else {
            this.test.fail(this.log, "Matched \"" + this.toMatch + "\", when not expected to.");
        }
        return false;
    }

    private boolean checkParens() {
        this.log.append("   Paren count: " + this.regexp.getParenCount() + "\n");
        if (!this.assertEquals(this.log, "Wrong number of parens", this.parens.length, this.regexp.getParenCount())) {
            return false;
        }
        int n = 0;
        while (n < this.regexp.getParenCount()) {
            this.log.append("   Paren " + n + ": " + this.regexp.getParen(n) + "\n");
            if (!("null".equals(this.parens[n]) && this.regexp.getParen(n) == null || this.assertEquals(this.log, "Wrong register " + n, this.parens[n], this.regexp.getParen(n)))) {
                return false;
            }
            ++n;
        }
        return true;
    }

    boolean tryMatchUsingCI(CharacterIterator characterIterator) {
        try {
            boolean bl = this.regexp.match(characterIterator, 0);
            this.log.append("   Match: " + (bl ? "YES" : "NO") + "\n");
            return this.checkResult(bl) && (!this.shouldMatch || this.checkParens());
        }
        catch (Exception exception) {
            this.test.fail(this.log, "Matcher threw exception: " + exception.toString());
            exception.printStackTrace();
        }
        catch (Error error) {
            this.test.fail(this.log, "Matcher threw fatal error \"" + error.getMessage() + "\"");
            error.printStackTrace();
        }
        return false;
    }

    public boolean assertEquals(StringBuffer stringBuffer, String string, String string2, String string3) {
        if (string2 != null && !string2.equals(string3) || string3 != null && !string3.equals(string2)) {
            this.test.fail(stringBuffer, string + " (expected \"" + string2 + "\", actual \"" + string3 + "\")");
            return false;
        }
        return true;
    }

    public boolean assertEquals(StringBuffer stringBuffer, String string, int n, int n2) {
        if (n != n2) {
            this.test.fail(stringBuffer, string + " (expected \"" + n + "\", actual \"" + n2 + "\")");
            return false;
        }
        return true;
    }

    void success(String string) {
    }
}

