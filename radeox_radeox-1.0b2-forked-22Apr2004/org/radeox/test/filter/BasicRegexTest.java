/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestCase
 *  junit.framework.TestSuite
 */
package org.radeox.test.filter;

import java.text.MessageFormat;
import java.util.regex.Pattern;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.radeox.engine.context.BaseInitialRenderContext;
import org.radeox.engine.context.BaseRenderContext;
import org.radeox.filter.HeadingFilter;
import org.radeox.filter.context.BaseFilterContext;
import org.radeox.filter.context.FilterContext;
import org.radeox.filter.regex.RegexReplaceFilter;
import org.radeox.filter.regex.RegexTokenFilter;
import org.radeox.macro.code.XmlCodeFilter;
import org.radeox.regex.Compiler;
import org.radeox.regex.MatchResult;
import org.radeox.regex.Matcher;

public class BasicRegexTest
extends TestCase {
    private static String BOLD_TEST_REGEX = "(^|>|[[:space:]]+)__(.*?)__([[:space:]]+|<|$)";
    private Compiler compiler;
    static /* synthetic */ Class class$org$radeox$test$filter$BasicRegexTest;

    public BasicRegexTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.compiler = Compiler.create();
        this.compiler.setMultiline(true);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$BasicRegexTest == null ? (class$org$radeox$test$filter$BasicRegexTest = BasicRegexTest.class$("org.radeox.test.filter.BasicRegexTest")) : class$org$radeox$test$filter$BasicRegexTest);
    }

    public void testStartEnd() {
        org.radeox.regex.Pattern p = this.compiler.compile("^A.*B$");
        Matcher m = Matcher.create("A1234567B", p);
        BasicRegexTest.assertTrue((String)"^...$ pattern found", (boolean)m.matches());
    }

    public void testHeading() {
        BaseFilterContext context = new BaseFilterContext();
        context.setRenderContext(new BaseRenderContext());
        HeadingFilter filter = new HeadingFilter();
        filter.setInitialContext(new BaseInitialRenderContext());
        BasicRegexTest.assertEquals((String)"Heading replaced", (String)"<h3 class=\"heading-1\">test</h3>", (String)filter.filter("1 test", context));
    }

    public void testByHandHeading() {
        RegexTokenFilter filter = new RegexTokenFilter(){

            public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
                String outputTemplate = "<h3 class=\"heading-{0}\">{1}</h3>";
                MessageFormat formatter = new MessageFormat("");
                formatter.applyPattern(outputTemplate);
                buffer.append(formatter.format(new Object[]{result.group(1).replace('.', '-'), result.group(3)}));
            }
        };
        filter.addRegex("^[\\p{Space}]*(1(\\.1)*)[\\p{Space}]+(.*?)$", "");
        BaseFilterContext context = new BaseFilterContext();
        context.setRenderContext(new BaseRenderContext());
        BasicRegexTest.assertEquals((String)"Heading replaced", (String)"<h3 class=\"heading-1\">testHand</h3>", (String)filter.filter("1 testHand", context));
    }

    public void testWordBorders() {
        org.radeox.regex.Pattern p = this.compiler.compile("\\bxsl\\b");
        Matcher m = Matcher.create("test xsl test", p);
        BasicRegexTest.assertTrue((String)"Word found", (boolean)m.contains());
        m = Matcher.create("testxsltest", p);
        BasicRegexTest.assertTrue((String)"Word not found", (!m.contains() ? 1 : 0) != 0);
    }

    public void testByHandUrl() {
        org.radeox.regex.Pattern p = this.compiler.compile("(http|ftp)s?://([-_.!~*';/?:@#&=+$,\\p{Alnum}])+");
        Matcher m = Matcher.create("http://snipsnap.org", p);
        BasicRegexTest.assertTrue((String)"A Url found", (boolean)m.matches());
    }

    public void testXmlCodeFilter() {
        org.radeox.regex.Pattern p = this.compiler.compile("\"(([^\"\\\\]|\\.)*)\"");
        Matcher m = Matcher.create("<xml attr=\"attr\"/>", p);
        BasicRegexTest.assertEquals((String)"Quote replaced", (String)"<xml attr=<span class=\"xml-quote\">\"attr\"</span>/>", (String)m.substitute("<span class=\"xml-quote\">\"$1\"</span>"));
        XmlCodeFilter xmlCodeFilter = new XmlCodeFilter();
        BaseFilterContext context = new BaseFilterContext();
        context.setRenderContext(new BaseRenderContext());
        BasicRegexTest.assertEquals((String)"XmlCodeFilter works", (String)"<xml a=<span class=\"xml-quote\">\"attr\"</span>><node>text</node></xml>", (String)xmlCodeFilter.filter("<xml a=\"attr\"><node>text</node></xml>", context));
    }

    public void testBackreference() {
        Pattern p = Pattern.compile("\\{([^:}]+)(?::([^\\}]*))?\\}(.*?)\\{\\1\\}", 8);
        java.util.regex.Matcher matcher = p.matcher("{code:xml}<xml a=\"attr\"><node>text</node></xml>{code}");
        BasicRegexTest.assertTrue((String)"A Backreference Regex found", (boolean)matcher.find());
        BasicRegexTest.assertNotNull((String)"Content not null", (Object)matcher.group(3));
        BasicRegexTest.assertEquals((String)"Content found", (String)"<xml a=\"attr\"><node>text</node></xml>", (String)matcher.group(3));
    }

    public void testRegexBasic() {
        org.radeox.regex.Pattern p = this.compiler.compile("A");
        Matcher m = Matcher.create("AB", p);
        BasicRegexTest.assertTrue((String)"A Regex found", (boolean)m.contains());
    }

    public void testMultiline() {
        this.compiler.setMultiline(false);
        org.radeox.regex.Pattern p = this.compiler.compile("A.*B");
        Matcher m = Matcher.create("A123\n456B", p);
        BasicRegexTest.assertTrue((String)"Multiline Regex found", (boolean)m.matches());
    }

    public void testByHandBold() {
        org.radeox.regex.Pattern p = this.compiler.compile(BOLD_TEST_REGEX);
        Matcher m = Matcher.create("__test__", p);
        BasicRegexTest.assertEquals((String)"Bold replaced by hand", (String)"<b>test</b>", (String)m.substitute("$1<b>$2</b>$3"));
    }

    public void testRegexFilterBold() {
        RegexReplaceFilter filter = new RegexReplaceFilter();
        filter.addRegex(BOLD_TEST_REGEX, "$1<b>$2</b>$3");
        BaseFilterContext context = new BaseFilterContext();
        context.setRenderContext(new BaseRenderContext());
        BasicRegexTest.assertEquals((String)"Bold replaced with RegexFilter", (String)"<b>test</b>", (String)filter.filter("__test__", context));
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

