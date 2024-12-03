/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import org.apache.axis.types.Token;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLChar;

public class Name
extends Token {
    public Name() {
    }

    public Name(String stValue) throws IllegalArgumentException {
        try {
            this.setValue(stValue);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(Messages.getMessage("badNameType00") + "data=[" + stValue + "]");
        }
    }

    public void setValue(String stValue) throws IllegalArgumentException {
        if (!Name.isValid(stValue)) {
            throw new IllegalArgumentException(Messages.getMessage("badNameType00") + " data=[" + stValue + "]");
        }
        this.m_value = stValue;
    }

    public static boolean isValid(String stValue) {
        boolean bValid = true;
        for (int scan = 0; scan < stValue.length() && (bValid = scan == 0 ? XMLChar.isNameStart(stValue.charAt(scan)) : XMLChar.isName(stValue.charAt(scan))); ++scan) {
        }
        return bValid;
    }
}

