/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.data;

import java.nio.charset.Charset;
import net.fortuna.ical4j.util.CompatibilityHints;

public abstract class AbstractOutputter {
    protected static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private boolean validating;
    protected int foldLength;

    public AbstractOutputter() {
        this(true);
    }

    public AbstractOutputter(boolean validating) {
        this(validating, CompatibilityHints.isHintEnabled("ical4j.compatibility.outlook") ? 75 : 73);
    }

    public AbstractOutputter(boolean validating, int foldLength) {
        this.validating = validating;
        this.foldLength = foldLength;
    }

    public final boolean isValidating() {
        return this.validating;
    }

    public final void setValidating(boolean validating) {
        this.validating = validating;
    }
}

