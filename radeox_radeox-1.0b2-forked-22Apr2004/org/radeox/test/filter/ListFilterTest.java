/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestSuite
 */
package org.radeox.test.filter;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.radeox.filter.ListFilter;
import org.radeox.test.filter.FilterTestSupport;

public class ListFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$ListFilterTest;

    public ListFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new ListFilter();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$ListFilterTest == null ? (class$org$radeox$test$filter$ListFilterTest = ListFilterTest.class$("org.radeox.test.filter.ListFilterTest")) : class$org$radeox$test$filter$ListFilterTest);
    }

    public void testListsWithStrike() {
        ListFilterTest.assertEquals((String)"<ul class=\"minus\">\n<li>test</li>\n<li>test</li>\n<li>test</li>\n</ul>", (String)this.filter.filter("- test\n- test\n\n-----\n\n- test", this.context));
    }

    public void testUnnumberedListTwoItems() {
        ListFilterTest.assertEquals((String)"<ul class=\"minus\">\n<li>test</li>\n<li>test</li>\n</ul>", (String)this.filter.filter("- test\n- test", this.context));
    }

    public void testUnnumberedList() {
        ListFilterTest.assertEquals((String)"<ul class=\"minus\">\n<li>test</li>\n</ul>", (String)this.filter.filter("- test", this.context));
    }

    public void testOrderedList() {
        ListFilterTest.assertEquals((String)"<ol>\n<li>test</li>\n<li>test</li>\n<li>test</li>\n</ol>", (String)this.filter.filter("1. test\n1. test\n 1. test", this.context));
    }

    public void testSimpleNestedList() {
        ListFilterTest.assertEquals((String)"<ul class=\"minus\">\n<li>test</li>\n<ul class=\"minus\">\n<li>test</li>\n<li>test</li>\n</ul>\n<li>test</li>\n</ul>", (String)this.filter.filter("- test\r\n-- test\r\n-- test\r\n- test", this.context));
    }

    public void testNestedList() {
        ListFilterTest.assertEquals((String)"<ul class=\"minus\">\n<li>test</li>\n<ol class=\"alpha\">\n<li>test</li>\n<li>test</li>\n</ol>\n<li>test</li>\n</ul>", (String)this.filter.filter("- test\n-a. test\n-a. test\n- test", this.context));
    }

    public void testSequentialLists() {
        ListFilterTest.assertEquals((String)"<ul class=\"minus\">\n<li>test</li>\n</ul>TEXT\n<ul class=\"minus\">\n<li>test</li>\n</ul>", (String)this.filter.filter("- test\nTEXT\n- test", this.context));
    }

    public void testListWithLinks() {
        ListFilterTest.assertEquals((String)"<ul class=\"minus\">\n<li>[test]</li>\n<li>[test1]</li>\n<li>[test test2]</li>\n</ul>", (String)this.filter.filter("- [test]\n- [test1]\n- [test test2]\n", this.context));
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

