/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import javax.json.JsonValue;

public interface JsonString
extends JsonValue {
    public String getString();

    public CharSequence getChars();

    public boolean equals(Object var1);

    public int hashCode();
}

