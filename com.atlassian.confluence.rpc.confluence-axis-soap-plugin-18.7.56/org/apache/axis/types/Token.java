/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import org.apache.axis.types.NormalizedString;
import org.apache.axis.utils.Messages;

public class Token
extends NormalizedString {
    public Token() {
    }

    public Token(String stValue) throws IllegalArgumentException {
        try {
            this.setValue(stValue);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(Messages.getMessage("badToken00") + "data=[" + stValue + "]");
        }
    }

    public static boolean isValid(String stValue) {
        if (stValue == null || stValue.length() == 0) {
            return true;
        }
        if (stValue.charAt(0) == ' ') {
            return false;
        }
        if (stValue.charAt(stValue.length() - 1) == ' ') {
            return false;
        }
        block4: for (int scan = 0; scan < stValue.length(); ++scan) {
            char cDigit = stValue.charAt(scan);
            switch (cDigit) {
                case '\t': 
                case '\n': {
                    return false;
                }
                case ' ': {
                    if (scan + 1 >= stValue.length() || stValue.charAt(scan + 1) != ' ') continue block4;
                    return false;
                }
            }
        }
        return true;
    }

    public void setValue(String stValue) throws IllegalArgumentException {
        if (!Token.isValid(stValue)) {
            throw new IllegalArgumentException(Messages.getMessage("badToken00") + " data=[" + stValue + "]");
        }
        this.m_value = stValue;
    }
}

