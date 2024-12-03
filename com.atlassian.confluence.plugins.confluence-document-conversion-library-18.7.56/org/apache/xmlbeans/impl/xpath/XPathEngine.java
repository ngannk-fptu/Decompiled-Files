/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xpath;

import org.apache.xmlbeans.impl.store.Cur;

public interface XPathEngine {
    public void release();

    public boolean next(Cur var1);
}

