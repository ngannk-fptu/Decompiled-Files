/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json;

import java.io.IOException;
import net.minidev.json.JSONStreamAware;
import net.minidev.json.JSONStyle;

public interface JSONStreamAwareEx
extends JSONStreamAware {
    public void writeJSONString(Appendable var1, JSONStyle var2) throws IOException;
}

