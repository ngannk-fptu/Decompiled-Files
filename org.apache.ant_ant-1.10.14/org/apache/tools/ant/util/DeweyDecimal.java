/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DeweyDecimal
implements Comparable<DeweyDecimal> {
    private final int[] components;

    public DeweyDecimal(int[] components) {
        this.components = new int[components.length];
        System.arraycopy(components, 0, this.components, 0, components.length);
    }

    public DeweyDecimal(String string) throws NumberFormatException {
        StringTokenizer tokenizer = new StringTokenizer(string, ".", true);
        int size = tokenizer.countTokens();
        this.components = new int[(size + 1) / 2];
        for (int i = 0; i < this.components.length; ++i) {
            String component = tokenizer.nextToken();
            if (component.isEmpty()) {
                throw new NumberFormatException("Empty component in string");
            }
            this.components[i] = Integer.parseInt(component);
            if (!tokenizer.hasMoreTokens()) continue;
            tokenizer.nextToken();
            if (tokenizer.hasMoreTokens()) continue;
            throw new NumberFormatException("DeweyDecimal ended in a '.'");
        }
    }

    public int getSize() {
        return this.components.length;
    }

    public int get(int index) {
        return this.components[index];
    }

    public boolean isEqual(DeweyDecimal other) {
        int max = Math.max(other.components.length, this.components.length);
        for (int i = 0; i < max; ++i) {
            int component2;
            int component1 = i < this.components.length ? this.components[i] : 0;
            int n = component2 = i < other.components.length ? other.components[i] : 0;
            if (component2 == component1) continue;
            return false;
        }
        return true;
    }

    public boolean isLessThan(DeweyDecimal other) {
        return !this.isGreaterThanOrEqual(other);
    }

    public boolean isLessThanOrEqual(DeweyDecimal other) {
        return !this.isGreaterThan(other);
    }

    public boolean isGreaterThan(DeweyDecimal other) {
        int max = Math.max(other.components.length, this.components.length);
        for (int i = 0; i < max; ++i) {
            int component2;
            int component1 = i < this.components.length ? this.components[i] : 0;
            int n = component2 = i < other.components.length ? other.components[i] : 0;
            if (component2 > component1) {
                return false;
            }
            if (component2 >= component1) continue;
            return true;
        }
        return false;
    }

    public boolean isGreaterThanOrEqual(DeweyDecimal other) {
        int max = Math.max(other.components.length, this.components.length);
        for (int i = 0; i < max; ++i) {
            int component2;
            int component1 = i < this.components.length ? this.components[i] : 0;
            int n = component2 = i < other.components.length ? other.components[i] : 0;
            if (component2 > component1) {
                return false;
            }
            if (component2 >= component1) continue;
            return true;
        }
        return true;
    }

    public String toString() {
        return IntStream.of(this.components).mapToObj(Integer::toString).collect(Collectors.joining("."));
    }

    @Override
    public int compareTo(DeweyDecimal other) {
        int max = Math.max(other.components.length, this.components.length);
        for (int i = 0; i < max; ++i) {
            int component2;
            int component1 = i < this.components.length ? this.components[i] : 0;
            int n = component2 = i < other.components.length ? other.components[i] : 0;
            if (component1 == component2) continue;
            return component1 - component2;
        }
        return 0;
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof DeweyDecimal && this.isEqual((DeweyDecimal)o);
    }
}

