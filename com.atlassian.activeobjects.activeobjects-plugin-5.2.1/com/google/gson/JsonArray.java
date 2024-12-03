/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.Escaper;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class JsonArray
extends JsonElement
implements Iterable<JsonElement> {
    private final List<JsonElement> elements = new ArrayList<JsonElement>();

    public void add(JsonElement element) {
        if (element == null) {
            element = JsonNull.createJsonNull();
        }
        this.elements.add(element);
    }

    public void addAll(JsonArray array) {
        this.elements.addAll(array.elements);
    }

    void reverse() {
        Collections.reverse(this.elements);
    }

    public int size() {
        return this.elements.size();
    }

    @Override
    public Iterator<JsonElement> iterator() {
        return this.elements.iterator();
    }

    public JsonElement get(int i) {
        return this.elements.get(i);
    }

    @Override
    public Number getAsNumber() {
        if (this.elements.size() == 1) {
            return this.elements.get(0).getAsNumber();
        }
        throw new IllegalStateException();
    }

    @Override
    public String getAsString() {
        if (this.elements.size() == 1) {
            return this.elements.get(0).getAsString();
        }
        throw new IllegalStateException();
    }

    @Override
    public double getAsDouble() {
        if (this.elements.size() == 1) {
            return this.elements.get(0).getAsDouble();
        }
        throw new IllegalStateException();
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        if (this.elements.size() == 1) {
            return this.elements.get(0).getAsBigDecimal();
        }
        throw new IllegalStateException();
    }

    @Override
    public BigInteger getAsBigInteger() {
        if (this.elements.size() == 1) {
            return this.elements.get(0).getAsBigInteger();
        }
        throw new IllegalStateException();
    }

    @Override
    public float getAsFloat() {
        if (this.elements.size() == 1) {
            return this.elements.get(0).getAsFloat();
        }
        throw new IllegalStateException();
    }

    @Override
    public long getAsLong() {
        if (this.elements.size() == 1) {
            return this.elements.get(0).getAsLong();
        }
        throw new IllegalStateException();
    }

    @Override
    public int getAsInt() {
        if (this.elements.size() == 1) {
            return this.elements.get(0).getAsInt();
        }
        throw new IllegalStateException();
    }

    @Override
    public byte getAsByte() {
        if (this.elements.size() == 1) {
            return this.elements.get(0).getAsByte();
        }
        throw new IllegalStateException();
    }

    @Override
    public char getAsCharacter() {
        if (this.elements.size() == 1) {
            return this.elements.get(0).getAsCharacter();
        }
        throw new IllegalStateException();
    }

    @Override
    public short getAsShort() {
        if (this.elements.size() == 1) {
            return this.elements.get(0).getAsShort();
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean getAsBoolean() {
        if (this.elements.size() == 1) {
            return this.elements.get(0).getAsBoolean();
        }
        throw new IllegalStateException();
    }

    @Override
    Object getAsObject() {
        if (this.elements.size() == 1) {
            return this.elements.get(0).getAsObject();
        }
        throw new IllegalStateException();
    }

    public boolean equals(Object o) {
        return o == this || o instanceof JsonArray && ((Object)((JsonArray)o).elements).equals(this.elements);
    }

    public int hashCode() {
        return ((Object)this.elements).hashCode();
    }

    @Override
    protected void toString(Appendable sb, Escaper escaper) throws IOException {
        sb.append('[');
        boolean first = true;
        for (JsonElement element : this.elements) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            element.toString(sb, escaper);
        }
        sb.append(']');
    }
}

