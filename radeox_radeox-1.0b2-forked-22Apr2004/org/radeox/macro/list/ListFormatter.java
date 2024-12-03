/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.list;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import org.radeox.util.Linkable;

public interface ListFormatter {
    public String getName();

    public void format(Writer var1, Linkable var2, String var3, Collection var4, String var5, boolean var6) throws IOException;
}

