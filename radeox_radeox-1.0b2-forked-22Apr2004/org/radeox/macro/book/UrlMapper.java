/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.book;

import java.io.IOException;
import java.io.Writer;

public interface UrlMapper {
    public Writer appendTo(Writer var1) throws IOException;

    public Writer appendUrl(Writer var1, String var2) throws IOException;
}

