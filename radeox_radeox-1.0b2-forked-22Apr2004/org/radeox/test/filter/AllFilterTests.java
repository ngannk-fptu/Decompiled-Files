/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestCase
 *  junit.framework.TestSuite
 */
package org.radeox.test.filter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllFilterTests
extends TestCase {
    static /* synthetic */ Class class$org$radeox$test$filter$BasicRegexTest;
    static /* synthetic */ Class class$org$radeox$test$filter$ItalicFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$BoldFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$KeyFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$NewlineFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$LineFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$TypographyFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$HtmlRemoveFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$StrikeThroughFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$UrlFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$ParamFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$FilterPipeTest;
    static /* synthetic */ Class class$org$radeox$test$filter$EscapeFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$InterWikiTest;
    static /* synthetic */ Class class$org$radeox$test$filter$LinkTestFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$WikiLinkFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$SmileyFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$ListFilterTest;
    static /* synthetic */ Class class$org$radeox$test$filter$HeadingFilterTest;

    public AllFilterTests(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite s = new TestSuite();
        s.addTestSuite(class$org$radeox$test$filter$BasicRegexTest == null ? (class$org$radeox$test$filter$BasicRegexTest = AllFilterTests.class$("org.radeox.test.filter.BasicRegexTest")) : class$org$radeox$test$filter$BasicRegexTest);
        s.addTestSuite(class$org$radeox$test$filter$ItalicFilterTest == null ? (class$org$radeox$test$filter$ItalicFilterTest = AllFilterTests.class$("org.radeox.test.filter.ItalicFilterTest")) : class$org$radeox$test$filter$ItalicFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$BoldFilterTest == null ? (class$org$radeox$test$filter$BoldFilterTest = AllFilterTests.class$("org.radeox.test.filter.BoldFilterTest")) : class$org$radeox$test$filter$BoldFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$KeyFilterTest == null ? (class$org$radeox$test$filter$KeyFilterTest = AllFilterTests.class$("org.radeox.test.filter.KeyFilterTest")) : class$org$radeox$test$filter$KeyFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$NewlineFilterTest == null ? (class$org$radeox$test$filter$NewlineFilterTest = AllFilterTests.class$("org.radeox.test.filter.NewlineFilterTest")) : class$org$radeox$test$filter$NewlineFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$LineFilterTest == null ? (class$org$radeox$test$filter$LineFilterTest = AllFilterTests.class$("org.radeox.test.filter.LineFilterTest")) : class$org$radeox$test$filter$LineFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$TypographyFilterTest == null ? (class$org$radeox$test$filter$TypographyFilterTest = AllFilterTests.class$("org.radeox.test.filter.TypographyFilterTest")) : class$org$radeox$test$filter$TypographyFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$HtmlRemoveFilterTest == null ? (class$org$radeox$test$filter$HtmlRemoveFilterTest = AllFilterTests.class$("org.radeox.test.filter.HtmlRemoveFilterTest")) : class$org$radeox$test$filter$HtmlRemoveFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$StrikeThroughFilterTest == null ? (class$org$radeox$test$filter$StrikeThroughFilterTest = AllFilterTests.class$("org.radeox.test.filter.StrikeThroughFilterTest")) : class$org$radeox$test$filter$StrikeThroughFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$UrlFilterTest == null ? (class$org$radeox$test$filter$UrlFilterTest = AllFilterTests.class$("org.radeox.test.filter.UrlFilterTest")) : class$org$radeox$test$filter$UrlFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$ParamFilterTest == null ? (class$org$radeox$test$filter$ParamFilterTest = AllFilterTests.class$("org.radeox.test.filter.ParamFilterTest")) : class$org$radeox$test$filter$ParamFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$FilterPipeTest == null ? (class$org$radeox$test$filter$FilterPipeTest = AllFilterTests.class$("org.radeox.test.filter.FilterPipeTest")) : class$org$radeox$test$filter$FilterPipeTest);
        s.addTestSuite(class$org$radeox$test$filter$EscapeFilterTest == null ? (class$org$radeox$test$filter$EscapeFilterTest = AllFilterTests.class$("org.radeox.test.filter.EscapeFilterTest")) : class$org$radeox$test$filter$EscapeFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$InterWikiTest == null ? (class$org$radeox$test$filter$InterWikiTest = AllFilterTests.class$("org.radeox.test.filter.InterWikiTest")) : class$org$radeox$test$filter$InterWikiTest);
        s.addTestSuite(class$org$radeox$test$filter$LinkTestFilterTest == null ? (class$org$radeox$test$filter$LinkTestFilterTest = AllFilterTests.class$("org.radeox.test.filter.LinkTestFilterTest")) : class$org$radeox$test$filter$LinkTestFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$WikiLinkFilterTest == null ? (class$org$radeox$test$filter$WikiLinkFilterTest = AllFilterTests.class$("org.radeox.test.filter.WikiLinkFilterTest")) : class$org$radeox$test$filter$WikiLinkFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$SmileyFilterTest == null ? (class$org$radeox$test$filter$SmileyFilterTest = AllFilterTests.class$("org.radeox.test.filter.SmileyFilterTest")) : class$org$radeox$test$filter$SmileyFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$ListFilterTest == null ? (class$org$radeox$test$filter$ListFilterTest = AllFilterTests.class$("org.radeox.test.filter.ListFilterTest")) : class$org$radeox$test$filter$ListFilterTest);
        s.addTestSuite(class$org$radeox$test$filter$HeadingFilterTest == null ? (class$org$radeox$test$filter$HeadingFilterTest = AllFilterTests.class$("org.radeox.test.filter.HeadingFilterTest")) : class$org$radeox$test$filter$HeadingFilterTest);
        return s;
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

