/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;

public final class HashtableOfPackage<P extends PackageBinding> {
    public char[][] keyTable;
    private PackageBinding[] valueTable;
    public int elementSize = 0;
    int threshold;

    public HashtableOfPackage() {
        this(3);
    }

    public HashtableOfPackage(int size) {
        this.threshold = size;
        int extraRoom = (int)((float)size * 1.75f);
        if (this.threshold == extraRoom) {
            ++extraRoom;
        }
        this.keyTable = new char[extraRoom][];
        this.valueTable = new PackageBinding[extraRoom];
    }

    public Iterable<P> values() {
        return Arrays.stream(this.valueTable).filter(Objects::nonNull).map(p -> {
            PackageBinding theP = p;
            return theP;
        }).collect(Collectors.toList());
    }

    public boolean containsKey(char[] key) {
        char[] currentKey;
        int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        int keyLength = key.length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
                return true;
            }
            if (++index != length) continue;
            index = 0;
        }
        return false;
    }

    public P get(char[] key) {
        char[] currentKey;
        int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        int keyLength = key.length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
                PackageBinding p = this.valueTable[index];
                return (P)p;
            }
            if (++index != length) continue;
            index = 0;
        }
        return null;
    }

    public PackageBinding put(char[] key, PackageBinding value) {
        char[] currentKey;
        int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        int keyLength = key.length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
                this.valueTable[index] = value;
                return this.valueTable[index];
            }
            if (++index != length) continue;
            index = 0;
        }
        this.keyTable[index] = key;
        this.valueTable[index] = value;
        if (++this.elementSize > this.threshold) {
            this.rehash();
        }
        return value;
    }

    private void rehash() {
        HashtableOfPackage<P> newHashtable = new HashtableOfPackage<P>(this.elementSize * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            char[] currentKey = this.keyTable[i];
            if (currentKey == null) continue;
            newHashtable.put(currentKey, this.valueTable[i]);
        }
        this.keyTable = newHashtable.keyTable;
        this.valueTable = newHashtable.valueTable;
        this.threshold = newHashtable.threshold;
    }

    public int size() {
        return this.elementSize;
    }

    public String toString() {
        String s = "";
        int i = 0;
        int length = this.valueTable.length;
        while (i < length) {
            PackageBinding pkg = this.valueTable[i];
            if (pkg != null) {
                s = String.valueOf(s) + pkg.toString() + "\n";
            }
            ++i;
        }
        return s;
    }
}

