/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.internal.util.StringHelper;

public class DatabaseIdentifier
extends Identifier {
    protected DatabaseIdentifier(String text) {
        super(text);
    }

    public static DatabaseIdentifier toIdentifier(String text) {
        if (StringHelper.isEmpty(text)) {
            return null;
        }
        if (DatabaseIdentifier.isQuoted(text)) {
            String unquotedtext = text.substring(1, text.length() - 1);
            return new DatabaseIdentifier(unquotedtext);
        }
        return new DatabaseIdentifier(text);
    }
}

