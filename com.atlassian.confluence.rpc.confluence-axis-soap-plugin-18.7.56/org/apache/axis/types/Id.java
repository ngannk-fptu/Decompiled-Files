/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import org.apache.axis.types.NCName;
import org.apache.axis.utils.Messages;

public class Id
extends NCName {
    public Id() {
    }

    public Id(String stValue) throws IllegalArgumentException {
        try {
            this.setValue(stValue);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(Messages.getMessage("badIdType00") + "data=[" + stValue + "]");
        }
    }

    public void setValue(String stValue) throws IllegalArgumentException {
        if (!Id.isValid(stValue)) {
            throw new IllegalArgumentException(Messages.getMessage("badIdType00") + " data=[" + stValue + "]");
        }
        this.m_value = stValue;
    }

    public static boolean isValid(String stValue) {
        return NCName.isValid(stValue);
    }
}

