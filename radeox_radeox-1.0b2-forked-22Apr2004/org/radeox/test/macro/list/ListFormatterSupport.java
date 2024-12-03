/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.TestCase
 */
package org.radeox.test.macro.list;

import java.io.StringWriter;
import java.io.Writer;
import junit.framework.TestCase;
import org.radeox.macro.list.ListFormatter;
import org.radeox.util.Linkable;

public class ListFormatterSupport
extends TestCase {
    protected ListFormatter formatter;
    protected Writer writer;
    protected Linkable emptyLinkable = new Linkable(){

        public String getLink() {
            return "";
        }
    };

    public ListFormatterSupport(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.writer = new StringWriter();
    }
}

