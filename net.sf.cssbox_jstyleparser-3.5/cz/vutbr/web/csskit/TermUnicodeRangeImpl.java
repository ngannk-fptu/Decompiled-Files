/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.TermUnicodeRange;
import cz.vutbr.web.csskit.TermImpl;

public class TermUnicodeRangeImpl
extends TermImpl<String>
implements TermUnicodeRange {
    public TermUnicodeRange setValue(String uri) {
        this.value = uri;
        return this;
    }
}

