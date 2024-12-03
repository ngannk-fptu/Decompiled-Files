/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import org.apache.axis.types.Token;
import org.apache.axis.utils.Messages;

public class Language
extends Token {
    public Language() {
    }

    public Language(String stValue) throws IllegalArgumentException {
        try {
            this.setValue(stValue);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(Messages.getMessage("badLanguage00") + "data=[" + stValue + "]");
        }
    }

    public static boolean isValid(String stValue) {
        return true;
    }
}

