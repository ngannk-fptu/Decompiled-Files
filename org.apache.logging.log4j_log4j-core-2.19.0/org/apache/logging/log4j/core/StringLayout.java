/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core;

import java.nio.charset.Charset;
import org.apache.logging.log4j.core.Layout;

public interface StringLayout
extends Layout<String> {
    public Charset getCharset();
}

