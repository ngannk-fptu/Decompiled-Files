/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.TemplateElement;
import freemarker.template.TemplateException;

abstract class Interpolation
extends TemplateElement {
    Interpolation() {
    }

    protected abstract String dump(boolean var1, boolean var2);

    @Override
    protected final String dump(boolean canonical) {
        return this.dump(canonical, false);
    }

    final String getCanonicalFormInStringLiteral() {
        return this.dump(true, true);
    }

    protected abstract Object calculateInterpolatedStringOrMarkup(Environment var1) throws TemplateException;

    @Override
    boolean isShownInStackTrace() {
        return true;
    }
}

