/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.io.Serializable;
import org.apache.axis.utils.Messages;

public class NormalizedString
implements Serializable {
    String m_value = null;

    public NormalizedString() {
    }

    public NormalizedString(String stValue) throws IllegalArgumentException {
        this.setValue(stValue);
    }

    public void setValue(String stValue) throws IllegalArgumentException {
        if (!NormalizedString.isValid(stValue)) {
            throw new IllegalArgumentException(Messages.getMessage("badNormalizedString00") + " data=[" + stValue + "]");
        }
        this.m_value = stValue;
    }

    public String toString() {
        return this.m_value;
    }

    public int hashCode() {
        return this.m_value.hashCode();
    }

    public static boolean isValid(String stValue) {
        for (int scan = 0; scan < stValue.length(); ++scan) {
            char cDigit = stValue.charAt(scan);
            switch (cDigit) {
                case '\t': 
                case '\n': 
                case '\r': {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean equals(Object object) {
        String s1 = object.toString();
        return s1.equals(this.m_value);
    }
}

