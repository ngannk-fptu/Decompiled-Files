/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser;

import org.apache.abdera.parser.Parser;
import org.apache.abdera.util.NamedItem;

public interface NamedParser
extends Parser,
NamedItem {
    public String[] getInputFormats();

    public boolean parsesFormat(String var1);
}

