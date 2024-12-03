/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestSuite
 */
package org.radeox.test.macro.list;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.radeox.macro.list.SimpleList;
import org.radeox.test.macro.list.ListFormatterSupport;
import org.radeox.util.Linkable;
import org.radeox.util.Nameable;

public class SimpleListTest
extends ListFormatterSupport {
    static /* synthetic */ Class class$org$radeox$test$macro$list$SimpleListTest;

    public SimpleListTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$list$SimpleListTest == null ? (class$org$radeox$test$macro$list$SimpleListTest = SimpleListTest.class$("org.radeox.test.macro.list.SimpleListTest")) : class$org$radeox$test$macro$list$SimpleListTest);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.formatter = new SimpleList();
    }

    public void testNameable() {
        List<Nameable> c = Arrays.asList(new Nameable(){

            public String getName() {
                return "name:test";
            }
        });
        try {
            this.formatter.format(this.writer, this.emptyLinkable, "", c, "", false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        SimpleListTest.assertEquals((String)"Nameable is rendered", (String)"<div class=\"list\"><div class=\"list-title\"></div><blockquote>name:test</blockquote></div>", (String)this.writer.toString());
    }

    public void testLinkable() {
        List<Linkable> c = Arrays.asList(new Linkable(){

            public String getLink() {
                return "link:test";
            }
        });
        try {
            this.formatter.format(this.writer, this.emptyLinkable, "", c, "", false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        SimpleListTest.assertEquals((String)"Linkable is rendered", (String)"<div class=\"list\"><div class=\"list-title\"></div><blockquote>link:test</blockquote></div>", (String)this.writer.toString());
    }

    public void testSingeItem() {
        List<String> c = Arrays.asList("test");
        try {
            this.formatter.format(this.writer, this.emptyLinkable, "", c, "", false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        SimpleListTest.assertEquals((String)"Single item is rendered", (String)"<div class=\"list\"><div class=\"list-title\"></div><blockquote>test</blockquote></div>", (String)this.writer.toString());
    }

    public void testSize() {
        List<String> c = Arrays.asList("test");
        try {
            this.formatter.format(this.writer, this.emptyLinkable, "", c, "", true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        SimpleListTest.assertEquals((String)"Size is rendered", (String)"<div class=\"list\"><div class=\"list-title\"> (1)</div><blockquote>test</blockquote></div>", (String)this.writer.toString());
    }

    public void testEmpty() {
        List<String> c = Arrays.asList(new String[0]);
        try {
            this.formatter.format(this.writer, this.emptyLinkable, "", c, "No items", false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        SimpleListTest.assertEquals((String)"Empty list is rendered", (String)"<div class=\"list\"><div class=\"list-title\"></div>No items</div>", (String)this.writer.toString());
    }

    public void testTwoItems() {
        List<String> c = Arrays.asList("test1", "test2");
        try {
            this.formatter.format(this.writer, this.emptyLinkable, "", c, "", false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        SimpleListTest.assertEquals((String)"Two items are rendered", (String)"<div class=\"list\"><div class=\"list-title\"></div><blockquote>test1, test2</blockquote></div>", (String)this.writer.toString());
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

