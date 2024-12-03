/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestCase
 *  junit.framework.TestSuite
 */
package org.radeox.test.regex;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.radeox.regex.MatchResult;
import org.radeox.regex.OroCompiler;
import org.radeox.regex.OroMatcher;
import org.radeox.regex.Pattern;
import org.radeox.regex.Substitution;

public class OroMatcherTest
extends TestCase {
    static /* synthetic */ Class class$org$radeox$test$regex$OroMatcherTest;

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$regex$OroMatcherTest == null ? (class$org$radeox$test$regex$OroMatcherTest = OroMatcherTest.class$("org.radeox.test.regex.OroMatcherTest")) : class$org$radeox$test$regex$OroMatcherTest);
    }

    public void testSubstituteWithoutVariables() {
        OroCompiler compiler = new OroCompiler();
        Pattern pattern = compiler.compile("A");
        OroMatcher matcher = new OroMatcher("A", pattern);
        String substituted = matcher.substitute("B");
        OroMatcherTest.assertEquals((String)"Correct substitution without variables.", (String)"B", (String)substituted);
    }

    public void testSubstituteWithVariables() {
        OroCompiler compiler = new OroCompiler();
        Pattern pattern = compiler.compile("(A)");
        OroMatcher matcher = new OroMatcher("BAB", pattern);
        String substituted = matcher.substitute("C$1C");
        OroMatcherTest.assertEquals((String)"Correct substitution with variables.", (String)"BCACB", (String)substituted);
    }

    public void testSubstitutionWithSubstitution() {
        OroCompiler compiler = new OroCompiler();
        Pattern pattern = compiler.compile("(A)");
        OroMatcher matcher = new OroMatcher("BABAB", pattern);
        String substituted = matcher.substitute(new Substitution(){

            public void handleMatch(StringBuffer buffer, MatchResult result) {
                String match = result.group(1);
                buffer.append(match.toLowerCase());
            }
        });
        OroMatcherTest.assertEquals((String)"Correct substitution with substitution.", (String)"BaBaB", (String)substituted);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

