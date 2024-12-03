/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.oro.text.regex.MatchResult
 *  org.apache.oro.text.regex.Pattern
 *  org.apache.oro.text.regex.PatternMatcher
 *  org.apache.oro.text.regex.PatternMatcherInput
 *  org.apache.oro.text.regex.StringSubstitution
 */
package org.radeox.regex;

import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.StringSubstitution;
import org.radeox.regex.OroMatchResult;
import org.radeox.regex.Substitution;

public class OroActionSubstitution
extends StringSubstitution {
    private Substitution substitution;

    public OroActionSubstitution(Substitution substitution) {
        this.substitution = substitution;
    }

    public void appendSubstitution(StringBuffer stringBuffer, MatchResult matchResult, int i, PatternMatcherInput patternMatcherInput, PatternMatcher patternMatcher, Pattern pattern) {
        this.substitution.handleMatch(stringBuffer, new OroMatchResult(matchResult));
    }
}

