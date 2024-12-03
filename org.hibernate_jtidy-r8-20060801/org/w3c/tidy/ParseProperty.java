/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.tidy.Configuration;

public interface ParseProperty {
    public Object parse(String var1, String var2, Configuration var3);

    public String getType();

    public String getOptionValues();

    public String getFriendlyName(String var1, Object var2, Configuration var3);
}

