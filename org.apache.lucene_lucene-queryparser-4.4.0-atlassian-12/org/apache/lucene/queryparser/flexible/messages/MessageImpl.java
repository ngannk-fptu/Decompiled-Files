/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.messages;

import java.util.Locale;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.messages.NLS;

public class MessageImpl
implements Message {
    private String key;
    private Object[] arguments = new Object[0];

    public MessageImpl(String key) {
        this.key = key;
    }

    public MessageImpl(String key, Object ... args) {
        this(key);
        this.arguments = args;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getLocalizedMessage() {
        return this.getLocalizedMessage(Locale.getDefault());
    }

    @Override
    public String getLocalizedMessage(Locale locale) {
        return NLS.getLocalizedMessage(this.getKey(), locale, this.getArguments());
    }

    public String toString() {
        Object[] args = this.getArguments();
        StringBuilder sb = new StringBuilder(this.getKey());
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                sb.append(i == 0 ? " " : ", ").append(args[i]);
            }
        }
        return sb.toString();
    }
}

