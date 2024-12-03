/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import java.io.InputStream;
import java.io.Reader;

interface YamlDocumentLoader {
    public Object loadFromInputStream(InputStream var1);

    public Object loadFromReader(Reader var1);

    public Object loadFromString(String var1);
}

