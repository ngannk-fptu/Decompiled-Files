/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

import java.util.Collection;
import org.bouncycastle.util.StreamParsingException;

public interface StreamParser {
    public Object read() throws StreamParsingException;

    public Collection readAll() throws StreamParsingException;
}

