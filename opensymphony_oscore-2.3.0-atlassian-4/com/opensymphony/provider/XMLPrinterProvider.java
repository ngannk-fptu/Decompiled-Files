/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.provider;

import com.opensymphony.provider.Provider;
import java.io.IOException;
import java.io.Writer;
import org.w3c.dom.Document;

public interface XMLPrinterProvider
extends Provider {
    public void print(Document var1, Writer var2) throws IOException;
}

