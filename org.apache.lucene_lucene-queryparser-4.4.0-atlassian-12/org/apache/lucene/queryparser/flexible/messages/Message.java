/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.messages;

import java.util.Locale;

public interface Message {
    public String getKey();

    public Object[] getArguments();

    public String getLocalizedMessage();

    public String getLocalizedMessage(Locale var1);
}

