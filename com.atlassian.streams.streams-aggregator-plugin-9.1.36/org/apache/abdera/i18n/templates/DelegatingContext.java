/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.templates;

import java.util.Iterator;
import org.apache.abdera.i18n.templates.CachingContext;
import org.apache.abdera.i18n.templates.Context;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class DelegatingContext
extends CachingContext {
    protected final Context subcontext;

    protected DelegatingContext(Context subcontext) {
        this.subcontext = subcontext;
    }

    @Override
    protected <T> T resolveActual(String var) {
        return this.subcontext.resolve(var);
    }

    @Override
    public Iterator<String> iterator() {
        return this.subcontext.iterator();
    }
}

