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
import org.radeox.macro.list.AtoZListFormatter;
import org.radeox.test.macro.list.ListFormatterSupport;

public class AtoZListFormatterTest
extends ListFormatterSupport {
    static /* synthetic */ Class class$org$radeox$test$macro$list$AtoZListFormatterTest;

    public AtoZListFormatterTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$list$AtoZListFormatterTest == null ? (class$org$radeox$test$macro$list$AtoZListFormatterTest = AtoZListFormatterTest.class$("org.radeox.test.macro.list.AtoZListFormatterTest")) : class$org$radeox$test$macro$list$AtoZListFormatterTest);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.formatter = new AtoZListFormatter();
    }

    public void testSingeItem() {
        List<String> c = Arrays.asList("test");
        try {
            this.formatter.format(this.writer, this.emptyLinkable, "", c, "", false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        AtoZListFormatterTest.assertEquals((String)"Single item is rendered", (String)"<table width=\"100%\" class=\"index-top\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th><b> &nbsp;<a href=\"#idxA\">A</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxB\">B</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxC\">C</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxD\">D</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxE\">E</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxF\">F</a></b></th><th>...</th><th>0&nbsp; </th></tr><tr><th><b> &nbsp;<a href=\"#idxG\">G</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxH\">H</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxI\">I</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxJ\">J</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxK\">K</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxL\">L</a></b></th><th>...</th><th>0&nbsp; </th></tr><tr><th><b> &nbsp;<a href=\"#idxM\">M</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxN\">N</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxO\">O</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxP\">P</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxQ\">Q</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxR\">R</a></b></th><th>...</th><th>0&nbsp; </th></tr><tr><th><b> &nbsp;<a href=\"#idxS\">S</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxT\">T</a></b></th><th>...</th><th>1&nbsp; </th><th><b> &nbsp;<a href=\"#idxU\">U</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxV\">V</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxW\">W</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxX\">X</a></b></th><th>...</th><th>0&nbsp; </th></tr><tr><th><b> &nbsp;<a href=\"#idxY\">Y</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxZ\">Z</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idx0-9\">0-9</a></b></th><th>...</th><th>0&nbsp; </th><th><b> &nbsp;<a href=\"#idxAT\">@</a></b></th><th>...</th><th>0&nbsp; </th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th></tr></table><div class=\"list-title\"></div><table width=\"100%\" class=\"index\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th><b><a name=\"idxA\"></a>A</b></th><th> </th><th><b><a name=\"idxB\"></a>B</b></th></tr><tr><th><b><a name=\"idxC\"></a>C</b></th><th> </th><th><b><a name=\"idxD\"></a>D</b></th></tr><tr><th><b><a name=\"idxE\"></a>E</b></th><th> </th><th><b><a name=\"idxF\"></a>F</b></th></tr><tr><th><b><a name=\"idxG\"></a>G</b></th><th> </th><th><b><a name=\"idxH\"></a>H</b></th></tr><tr><th><b><a name=\"idxI\"></a>I</b></th><th> </th><th><b><a name=\"idxJ\"></a>J</b></th></tr><tr><th><b><a name=\"idxK\"></a>K</b></th><th> </th><th><b><a name=\"idxL\"></a>L</b></th></tr><tr><th><b><a name=\"idxM\"></a>M</b></th><th> </th><th><b><a name=\"idxN\"></a>N</b></th></tr><tr><th><b><a name=\"idxO\"></a>O</b></th><th> </th><th><b><a name=\"idxP\"></a>P</b></th></tr><tr><th><b><a name=\"idxQ\"></a>Q</b></th><th> </th><th><b><a name=\"idxR\"></a>R</b></th></tr><tr><th><b><a name=\"idxS\"></a>S</b></th><th> </th><th><b><a name=\"idxT\"></a>T</b></th></tr><tr><td></td><td> </td><td>test</td></tr><tr><th><b><a name=\"idxU\"></a>U</b></th><th> </th><th><b><a name=\"idxV\"></a>V</b></th></tr><tr><th><b><a name=\"idxW\"></a>W</b></th><th> </th><th><b><a name=\"idxX\"></a>X</b></th></tr><tr><th><b><a name=\"idxY\"></a>Y</b></th><th> </th><th><b><a name=\"idxZ\"></a>Z</b></th></tr><tr><th><b><a name=\"idx0-9\"></a>0-9</b></th><th> </th><th><b><a name=\"idxAT\"></a>@</b></th></tr></table>", (String)this.writer.toString());
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

