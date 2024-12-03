/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.templates;

import org.apache.abdera.i18n.templates.Context;

public abstract class AbstractContext
implements Context {
    protected boolean iri = false;
    protected boolean normalizing = false;

    public boolean isIri() {
        return this.iri;
    }

    public void setIri(boolean isiri) {
        this.iri = isiri;
    }

    public boolean isNormalizing() {
        return this.normalizing;
    }

    public void setNormalizing(boolean normalizing) {
        this.normalizing = normalizing;
    }
}

