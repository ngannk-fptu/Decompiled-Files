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
import org.radeox.macro.list.ExampleListFormatter;
import org.radeox.test.macro.list.ListFormatterSupport;

public class ExampleListFormatterTest
extends ListFormatterSupport {
    static /* synthetic */ Class class$org$radeox$test$macro$list$ExampleListFormatterTest;

    public ExampleListFormatterTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$list$ExampleListFormatterTest == null ? (class$org$radeox$test$macro$list$ExampleListFormatterTest = ExampleListFormatterTest.class$("org.radeox.test.macro.list.ExampleListFormatterTest")) : class$org$radeox$test$macro$list$ExampleListFormatterTest);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.formatter = new ExampleListFormatter();
    }

    public void testSize() {
        List<String> c = Arrays.asList("test");
        try {
            this.formatter.format(this.writer, this.emptyLinkable, "", c, "", true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        ExampleListFormatterTest.assertEquals((String)"Size is rendered", (String)"<div class=\"list\"><div class=\"list-title\"> (1)</div><ol><li>test</li></ol></div>", (String)this.writer.toString());
    }

    public void testSingeItem() {
        List<String> c = Arrays.asList("test");
        try {
            this.formatter.format(this.writer, this.emptyLinkable, "", c, "", false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        ExampleListFormatterTest.assertEquals((String)"Single item is rendered", (String)"<div class=\"list\"><div class=\"list-title\"></div><ol><li>test</li></ol></div>", (String)this.writer.toString());
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

