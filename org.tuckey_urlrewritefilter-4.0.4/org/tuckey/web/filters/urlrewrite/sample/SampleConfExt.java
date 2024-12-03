/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.sample;

import java.io.InputStream;
import org.tuckey.web.filters.urlrewrite.Conf;
import org.w3c.dom.Document;

public class SampleConfExt
extends Conf {
    protected synchronized void loadDom(InputStream inputStream) {
        super.loadDom(inputStream);
    }

    protected void processConfDoc(Document doc) {
        super.processConfDoc(doc);
    }
}

