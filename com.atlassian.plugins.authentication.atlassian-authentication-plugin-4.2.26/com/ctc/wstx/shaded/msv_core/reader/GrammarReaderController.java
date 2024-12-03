/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

import org.xml.sax.EntityResolver;
import org.xml.sax.Locator;

public interface GrammarReaderController
extends EntityResolver {
    public void warning(Locator[] var1, String var2);

    public void error(Locator[] var1, String var2, Exception var3);
}

