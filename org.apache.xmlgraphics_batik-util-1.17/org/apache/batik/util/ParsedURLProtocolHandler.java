/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.ParsedURLData;

public interface ParsedURLProtocolHandler {
    public String getProtocolHandled();

    public ParsedURLData parseURL(String var1);

    public ParsedURLData parseURL(ParsedURL var1, String var2);
}

