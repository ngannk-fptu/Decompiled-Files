/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import java.util.Arrays;
import org.eclipse.jdt.core.compiler.CharOperation;

public class EnumConstantSignature {
    char[] typeName;
    char[] constName;

    public EnumConstantSignature(char[] typeName, char[] constName) {
        this.typeName = typeName;
        this.constName = constName;
    }

    public char[] getTypeName() {
        return this.typeName;
    }

    public char[] getEnumConstantName() {
        return this.constName;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.typeName);
        buffer.append('.');
        buffer.append(this.constName);
        return buffer.toString();
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + CharOperation.hashCode(this.constName);
        result = 31 * result + CharOperation.hashCode(this.typeName);
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
        EnumConstantSignature other = (EnumConstantSignature)obj;
        if (!Arrays.equals(this.constName, other.constName)) {
            return false;
        }
        return Arrays.equals(this.typeName, other.typeName);
    }
}

