/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestSuite
 */
package org.radeox.test.macro;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.radeox.EngineManager;
import org.radeox.test.macro.MacroTestSupport;

public class TableMacroTest
extends MacroTestSupport {
    static /* synthetic */ Class class$org$radeox$test$macro$TableMacroTest;

    public TableMacroTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$TableMacroTest == null ? (class$org$radeox$test$macro$TableMacroTest = TableMacroTest.class$("org.radeox.test.macro.TableMacroTest")) : class$org$radeox$test$macro$TableMacroTest);
    }

    public void testTable() {
        String result = EngineManager.getInstance().render("{table}1|2\n3|4{table}", this.context);
        TableMacroTest.assertEquals((String)"<table class=\"wiki-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th>1</th><th>2</th></tr><tr class=\"table-odd\"><td>3</td><td>4</td></tr></table>", (String)result);
    }

    public void testEmptyHeader() {
        String result = EngineManager.getInstance().render("{table}|\n3|4{table}", this.context);
        TableMacroTest.assertEquals((String)"<table class=\"wiki-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th>&#160;</th><th>&#160;</th></tr><tr class=\"table-odd\"><td>3</td><td>4</td></tr></table>", (String)result);
    }

    public void testMultiTable() {
        String result = EngineManager.getInstance().render("{table}1|2\n3|4{table}\n{table}5|6\n7|8{table}", this.context);
        TableMacroTest.assertEquals((String)"<table class=\"wiki-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th>1</th><th>2</th></tr><tr class=\"table-odd\"><td>3</td><td>4</td></tr></table>\n<table class=\"wiki-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th>5</th><th>6</th></tr><tr class=\"table-odd\"><td>7</td><td>8</td></tr></table>", (String)result);
    }

    public void testCalcIntSum() {
        String result = EngineManager.getInstance().render("{table}1|2\n3|=SUM(A1:A2){table}", this.context);
        TableMacroTest.assertEquals((String)"<table class=\"wiki-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th>1</th><th>2</th></tr><tr class=\"table-odd\"><td>3</td><td>4</td></tr></table>", (String)result);
    }

    public void testCalcFloatSum() {
        String result = EngineManager.getInstance().render("{table}1|2\n3.0|=SUM(A1:A2){table}", this.context);
        TableMacroTest.assertEquals((String)"<table class=\"wiki-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th>1</th><th>2</th></tr><tr class=\"table-odd\"><td>3.0</td><td>4.0</td></tr></table>", (String)result);
    }

    public void testFloatAvg() {
        String result = EngineManager.getInstance().render("{table}1|2\n4|=AVG(A1:A2){table}", this.context);
        TableMacroTest.assertEquals((String)"<table class=\"wiki-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th>1</th><th>2</th></tr><tr class=\"table-odd\"><td>4</td><td>2.5</td></tr></table>", (String)result);
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

