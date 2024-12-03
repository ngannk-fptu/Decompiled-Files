/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.util;

import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

public class IgnoreController
implements GrammarReaderController {
    public void warning(Locator[] locs, String errorMessage) {
    }

    public void error(Locator[] locs, String errorMessage, Exception nestedException) {
    }

    public InputSource resolveEntity(String p, String s) {
        return null;
    }
}

