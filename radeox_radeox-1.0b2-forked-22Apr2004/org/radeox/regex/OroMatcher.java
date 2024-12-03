/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.oro.text.regex.Pattern
 *  org.apache.oro.text.regex.PatternMatcher
 *  org.apache.oro.text.regex.Perl5Matcher
 *  org.apache.oro.text.regex.Perl5Substitution
 *  org.apache.oro.text.regex.Substitution
 *  org.apache.oro.text.regex.Util
 */
package org.radeox.regex;

import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Substitution;
import org.apache.oro.text.regex.Util;
import org.radeox.regex.Matcher;
import org.radeox.regex.OroActionSubstitution;
import org.radeox.regex.OroPattern;
import org.radeox.regex.Pattern;
import org.radeox.regex.Substitution;

public class OroMatcher
extends Matcher {
    private OroPattern pattern;
    private String input;
    private Perl5Matcher internalMatcher;

    public OroMatcher(String input, Pattern pattern) {
        this.input = input;
        this.pattern = (OroPattern)pattern;
        this.internalMatcher = new Perl5Matcher();
    }

    public String substitute(Substitution substitution) {
        return Util.substitute((PatternMatcher)this.internalMatcher, (org.apache.oro.text.regex.Pattern)this.pattern.getPattern(), (org.apache.oro.text.regex.Substitution)new OroActionSubstitution(substitution), (String)this.input, (int)-1);
    }

    public String substitute(String substitution) {
        return Util.substitute((PatternMatcher)this.internalMatcher, (org.apache.oro.text.regex.Pattern)this.pattern.getPattern(), (org.apache.oro.text.regex.Substitution)new Perl5Substitution(substitution, 0), (String)this.input, (int)-1);
    }

    protected Perl5Matcher getMatcher() {
        return this.internalMatcher;
    }

    public boolean contains() {
        return this.internalMatcher.contains(this.input, this.pattern.getPattern());
    }

    public boolean matches() {
        return this.internalMatcher.matches(this.input, this.pattern.getPattern());
    }
}

