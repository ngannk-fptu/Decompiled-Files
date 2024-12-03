/*
 * Decompiled with CFR 0.152.
 */
package org.apache.oro.text.perl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import org.apache.oro.text.PatternCache;
import org.apache.oro.text.PatternCacheLRU;
import org.apache.oro.text.perl.MalformedPerl5PatternException;
import org.apache.oro.text.perl.ParsedSubstitutionEntry;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Substitution;
import org.apache.oro.text.regex.Substitution;
import org.apache.oro.text.regex.Util;
import org.apache.oro.util.Cache;
import org.apache.oro.util.CacheLRU;

public final class Perl5Util
implements MatchResult {
    private static final String __matchExpression = "m?(\\W)(.*)\\1([imsx]*)";
    private PatternCache __patternCache;
    private Cache __expressionCache;
    private Perl5Matcher __matcher;
    private Pattern __matchPattern;
    private MatchResult __lastMatch;
    private ArrayList __splitList = new ArrayList();
    private Object __originalInput;
    private int __inputBeginOffset;
    private int __inputEndOffset;
    private static final String __nullString = "";
    public static final int SPLIT_ALL = 0;

    public Perl5Util(PatternCache patternCache) {
        this.__matcher = new Perl5Matcher();
        this.__patternCache = patternCache;
        this.__expressionCache = new CacheLRU(patternCache.capacity());
        this.__compilePatterns();
    }

    public Perl5Util() {
        this(new PatternCacheLRU());
    }

    private void __compilePatterns() {
        Perl5Compiler perl5Compiler = new Perl5Compiler();
        try {
            this.__matchPattern = perl5Compiler.compile(__matchExpression, 16);
        }
        catch (MalformedPatternException malformedPatternException) {
            throw new RuntimeException(malformedPatternException.getMessage());
        }
    }

    private Pattern __parseMatchExpression(String string) throws MalformedPerl5PatternException {
        Object object = this.__expressionCache.getElement(string);
        try {
            if (object != null) {
                return (Pattern)object;
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (!this.__matcher.matches(string, this.__matchPattern)) {
            throw new MalformedPerl5PatternException("Invalid expression: " + string);
        }
        MatchResult matchResult = this.__matcher.getMatch();
        String string2 = matchResult.group(2);
        int n = 0;
        String string3 = matchResult.group(3);
        if (string3 != null) {
            int n2 = string3.length();
            block8: while (n2-- > 0) {
                switch (string3.charAt(n2)) {
                    case 'i': {
                        n |= 1;
                        continue block8;
                    }
                    case 'm': {
                        n |= 8;
                        continue block8;
                    }
                    case 's': {
                        n |= 0x10;
                        continue block8;
                    }
                    case 'x': {
                        n |= 0x20;
                        continue block8;
                    }
                }
                throw new MalformedPerl5PatternException("Invalid options: " + string3);
            }
        }
        Pattern pattern = this.__patternCache.getPattern(string2, n);
        this.__expressionCache.addElement(string, pattern);
        return pattern;
    }

    public synchronized boolean match(String string, char[] cArray) throws MalformedPerl5PatternException {
        this.__parseMatchExpression(string);
        boolean bl = this.__matcher.contains(cArray, this.__parseMatchExpression(string));
        if (bl) {
            this.__lastMatch = this.__matcher.getMatch();
            this.__originalInput = cArray;
            this.__inputBeginOffset = 0;
            this.__inputEndOffset = cArray.length;
        }
        return bl;
    }

    public synchronized boolean match(String string, String string2) throws MalformedPerl5PatternException {
        return this.match(string, string2.toCharArray());
    }

    public synchronized boolean match(String string, PatternMatcherInput patternMatcherInput) throws MalformedPerl5PatternException {
        boolean bl = this.__matcher.contains(patternMatcherInput, this.__parseMatchExpression(string));
        if (bl) {
            this.__lastMatch = this.__matcher.getMatch();
            this.__originalInput = patternMatcherInput.getInput();
            this.__inputBeginOffset = patternMatcherInput.getBeginOffset();
            this.__inputEndOffset = patternMatcherInput.getEndOffset();
        }
        return bl;
    }

    public synchronized MatchResult getMatch() {
        return this.__lastMatch;
    }

    public synchronized int substitute(StringBuffer stringBuffer, String string, String string2) throws MalformedPerl5PatternException {
        int n;
        char[] cArray;
        block23: {
            Object object = this.__expressionCache.getElement(string);
            if (object != null) {
                ParsedSubstitutionEntry parsedSubstitutionEntry;
                try {
                    parsedSubstitutionEntry = (ParsedSubstitutionEntry)object;
                }
                catch (ClassCastException classCastException) {
                    break block23;
                }
                int n2 = Util.substitute(stringBuffer, (PatternMatcher)this.__matcher, parsedSubstitutionEntry._pattern, (Substitution)parsedSubstitutionEntry._substitution, string2, parsedSubstitutionEntry._numSubstitutions);
                this.__lastMatch = this.__matcher.getMatch();
                return n2;
            }
        }
        if ((cArray = string.toCharArray()).length < 4 || cArray[0] != 's' || Character.isLetterOrDigit(cArray[1]) || cArray[1] == '-') {
            throw new MalformedPerl5PatternException("Invalid expression: " + string);
        }
        char c = cArray[1];
        int n3 = 2;
        int n4 = -1;
        int n5 = -1;
        boolean bl = false;
        for (n = n3; n < cArray.length; ++n) {
            if (cArray[n] == '\\') {
                bl = !bl;
                continue;
            }
            if (cArray[n] == c && !bl) {
                n5 = n;
                break;
            }
            if (!bl) continue;
            bl = !bl;
        }
        if (n5 == -1 || n5 == cArray.length - 1) {
            throw new MalformedPerl5PatternException("Invalid expression: " + string);
        }
        bl = false;
        boolean bl2 = true;
        StringBuffer stringBuffer2 = new StringBuffer(cArray.length - n5);
        for (n = n5 + 1; n < cArray.length; ++n) {
            if (cArray[n] == '\\') {
                boolean bl3 = bl = !bl;
                if (bl && n + 1 < cArray.length && cArray[n + 1] == c && string.lastIndexOf(c, cArray.length - 1) != n + 1) {
                    bl2 = false;
                    continue;
                }
            } else {
                if (cArray[n] == c && bl2) {
                    n4 = n;
                    break;
                }
                bl = false;
                bl2 = true;
            }
            stringBuffer2.append(cArray[n]);
        }
        if (n4 == -1) {
            throw new MalformedPerl5PatternException("Invalid expression: " + string);
        }
        int n6 = 0;
        int n7 = 1;
        int n8 = c != '\'' ? 0 : -1;
        block12: for (n = n4 + 1; n < cArray.length; ++n) {
            switch (cArray[n]) {
                case 'i': {
                    n6 |= 1;
                    continue block12;
                }
                case 'm': {
                    n6 |= 8;
                    continue block12;
                }
                case 's': {
                    n6 |= 0x10;
                    continue block12;
                }
                case 'x': {
                    n6 |= 0x20;
                    continue block12;
                }
                case 'g': {
                    n7 = -1;
                    continue block12;
                }
                case 'o': {
                    n8 = 1;
                    continue block12;
                }
                default: {
                    throw new MalformedPerl5PatternException("Invalid option: " + cArray[n]);
                }
            }
        }
        Pattern pattern = this.__patternCache.getPattern(new String(cArray, n3, n5 - n3), n6);
        Perl5Substitution perl5Substitution = new Perl5Substitution(stringBuffer2.toString(), n8);
        ParsedSubstitutionEntry parsedSubstitutionEntry = new ParsedSubstitutionEntry(pattern, perl5Substitution, n7);
        this.__expressionCache.addElement(string, parsedSubstitutionEntry);
        int n9 = Util.substitute(stringBuffer, (PatternMatcher)this.__matcher, pattern, (Substitution)perl5Substitution, string2, n7);
        this.__lastMatch = this.__matcher.getMatch();
        return n9;
    }

    public synchronized String substitute(String string, String string2) throws MalformedPerl5PatternException {
        StringBuffer stringBuffer = new StringBuffer();
        this.substitute(stringBuffer, string, string2);
        return stringBuffer.toString();
    }

    public synchronized void split(Collection collection, String string, String string2, int n) throws MalformedPerl5PatternException {
        String string3;
        MatchResult matchResult = null;
        Pattern pattern = this.__parseMatchExpression(string);
        PatternMatcherInput patternMatcherInput = new PatternMatcherInput(string2);
        int n2 = 0;
        while (--n != 0 && this.__matcher.contains(patternMatcherInput, pattern)) {
            matchResult = this.__matcher.getMatch();
            this.__splitList.add(string2.substring(n2, matchResult.beginOffset(0)));
            int n3 = matchResult.groups();
            if (n3 > 1) {
                for (int i = 1; i < n3; ++i) {
                    String string4 = matchResult.group(i);
                    if (string4 == null || string4.length() <= 0) continue;
                    this.__splitList.add(string4);
                }
            }
            n2 = matchResult.endOffset(0);
        }
        this.__splitList.add(string2.substring(n2, string2.length()));
        for (int i = this.__splitList.size() - 1; i >= 0 && (string3 = (String)this.__splitList.get(i)).length() == 0; --i) {
            this.__splitList.remove(i);
        }
        collection.addAll(this.__splitList);
        this.__splitList.clear();
        this.__lastMatch = matchResult;
    }

    public synchronized void split(Collection collection, String string, String string2) throws MalformedPerl5PatternException {
        this.split(collection, string, string2, 0);
    }

    public synchronized void split(Collection collection, String string) throws MalformedPerl5PatternException {
        this.split(collection, "/\\s+/", string);
    }

    public synchronized Vector split(String string, String string2, int n) throws MalformedPerl5PatternException {
        Vector vector = new Vector(20);
        this.split(vector, string, string2, n);
        return vector;
    }

    public synchronized Vector split(String string, String string2) throws MalformedPerl5PatternException {
        return this.split(string, string2, 0);
    }

    public synchronized Vector split(String string) throws MalformedPerl5PatternException {
        return this.split("/\\s+/", string);
    }

    public synchronized int length() {
        return this.__lastMatch.length();
    }

    public synchronized int groups() {
        return this.__lastMatch.groups();
    }

    public synchronized String group(int n) {
        return this.__lastMatch.group(n);
    }

    public synchronized int begin(int n) {
        return this.__lastMatch.begin(n);
    }

    public synchronized int end(int n) {
        return this.__lastMatch.end(n);
    }

    public synchronized int beginOffset(int n) {
        return this.__lastMatch.beginOffset(n);
    }

    public synchronized int endOffset(int n) {
        return this.__lastMatch.endOffset(n);
    }

    public synchronized String toString() {
        if (this.__lastMatch == null) {
            return null;
        }
        return ((Object)this.__lastMatch).toString();
    }

    public synchronized String preMatch() {
        if (this.__originalInput == null) {
            return __nullString;
        }
        int n = this.__lastMatch.beginOffset(0);
        if (n <= 0) {
            return __nullString;
        }
        if (this.__originalInput instanceof char[]) {
            char[] cArray = (char[])this.__originalInput;
            if (n > cArray.length) {
                n = cArray.length;
            }
            return new String(cArray, this.__inputBeginOffset, n);
        }
        if (this.__originalInput instanceof String) {
            String string = (String)this.__originalInput;
            if (n > string.length()) {
                n = string.length();
            }
            return string.substring(this.__inputBeginOffset, n);
        }
        return __nullString;
    }

    public synchronized String postMatch() {
        if (this.__originalInput == null) {
            return __nullString;
        }
        int n = this.__lastMatch.endOffset(0);
        if (n < 0) {
            return __nullString;
        }
        if (this.__originalInput instanceof char[]) {
            char[] cArray = (char[])this.__originalInput;
            if (n >= cArray.length) {
                return __nullString;
            }
            return new String(cArray, n, this.__inputEndOffset - n);
        }
        if (this.__originalInput instanceof String) {
            String string = (String)this.__originalInput;
            if (n >= string.length()) {
                return __nullString;
            }
            return string.substring(n, this.__inputEndOffset);
        }
        return __nullString;
    }

    public synchronized char[] preMatchCharArray() {
        char[] cArray = null;
        if (this.__originalInput == null) {
            return null;
        }
        int n = this.__lastMatch.beginOffset(0);
        if (n <= 0) {
            return null;
        }
        if (this.__originalInput instanceof char[]) {
            char[] cArray2 = (char[])this.__originalInput;
            if (n >= cArray2.length) {
                n = cArray2.length;
            }
            cArray = new char[n - this.__inputBeginOffset];
            System.arraycopy(cArray2, this.__inputBeginOffset, cArray, 0, cArray.length);
        } else if (this.__originalInput instanceof String) {
            String string = (String)this.__originalInput;
            if (n >= string.length()) {
                n = string.length();
            }
            cArray = new char[n - this.__inputBeginOffset];
            string.getChars(this.__inputBeginOffset, n, cArray, 0);
        }
        return cArray;
    }

    public synchronized char[] postMatchCharArray() {
        char[] cArray = null;
        if (this.__originalInput == null) {
            return null;
        }
        int n = this.__lastMatch.endOffset(0);
        if (n < 0) {
            return null;
        }
        if (this.__originalInput instanceof char[]) {
            char[] cArray2 = (char[])this.__originalInput;
            if (n >= cArray2.length) {
                return null;
            }
            int n2 = this.__inputEndOffset - n;
            cArray = new char[n2];
            System.arraycopy(cArray2, n, cArray, 0, n2);
        } else if (this.__originalInput instanceof String) {
            String string = (String)this.__originalInput;
            if (n >= this.__inputEndOffset) {
                return null;
            }
            cArray = new char[this.__inputEndOffset - n];
            string.getChars(n, this.__inputEndOffset, cArray, 0);
        }
        return cArray;
    }
}

