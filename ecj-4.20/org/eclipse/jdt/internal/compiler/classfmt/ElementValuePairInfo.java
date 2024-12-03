/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import java.util.Arrays;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;

public class ElementValuePairInfo
implements IBinaryElementValuePair {
    static final ElementValuePairInfo[] NoMembers = new ElementValuePairInfo[0];
    private char[] name;
    private Object value;

    public ElementValuePairInfo(char[] name, Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public char[] getName() {
        return this.name;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.name);
        buffer.append('=');
        if (this.value instanceof Object[]) {
            Object[] values = (Object[])this.value;
            buffer.append('{');
            int i = 0;
            int l = values.length;
            while (i < l) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(values[i]);
                ++i;
            }
            buffer.append('}');
        } else {
            buffer.append(this.value);
        }
        return buffer.toString();
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + CharOperation.hashCode(this.name);
        result = 31 * result + (this.value == null ? 0 : this.value.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ElementValuePairInfo other = (ElementValuePairInfo)obj;
        if (!Arrays.equals(this.name, other.name)) {
            return false;
        }
        return !(this.value == null ? other.value != null : !this.value.equals(other.value));
    }
}

