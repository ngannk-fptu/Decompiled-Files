/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import org.apache.axis.types.Name;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLChar;

public class NCName
extends Name {
    public NCName() {
    }

    public NCName(String stValue) throws IllegalArgumentException {
        try {
            this.setValue(stValue);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(Messages.getMessage("badNCNameType00") + "data=[" + stValue + "]");
        }
    }

    public void setValue(String stValue) throws IllegalArgumentException {
        if (!NCName.isValid(stValue)) {
            throw new IllegalArgumentException(Messages.getMessage("badNCNameType00") + " data=[" + stValue + "]");
        }
        this.m_value = stValue;
    }

    public static boolean isValid(String stValue) {
        boolean bValid = true;
        for (int scan = 0; scan < stValue.length() && (bValid = scan == 0 ? XMLChar.isNCNameStart(stValue.charAt(scan)) : XMLChar.isNCName(stValue.charAt(scan))); ++scan) {
        }
        return bValid;
    }
}

