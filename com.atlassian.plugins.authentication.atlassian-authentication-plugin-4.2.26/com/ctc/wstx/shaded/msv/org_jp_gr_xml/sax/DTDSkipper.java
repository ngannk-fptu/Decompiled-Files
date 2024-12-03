/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_jp_gr_xml.sax;

import java.io.StringReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class DTDSkipper
implements EntityResolver {
    public InputSource resolveEntity(String s, String s1) {
        if (!s1.endsWith(".dtd")) {
            return null;
        }
        StringReader stringreader = new StringReader("");
        InputSource inputsource = new InputSource(stringreader);
        return inputsource;
    }
}

