/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import org.apache.axis.types.Token;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLChar;

public class NMToken
extends Token {
    public NMToken() {
    }

    public NMToken(String stValue) throws IllegalArgumentException {
        try {
            this.setValue(stValue);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(Messages.getMessage("badNmtoken00") + "data=[" + stValue + "]");
        }
    }

    public static boolean isValid(String stValue) {
        for (int scan = 0; scan < stValue.length(); ++scan) {
            if (XMLChar.isName(stValue.charAt(scan))) continue;
            return false;
        }
        return true;
    }
}

