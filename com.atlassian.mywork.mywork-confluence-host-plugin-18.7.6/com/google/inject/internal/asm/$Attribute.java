/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import com.google.inject.internal.asm.$ByteVector;
import com.google.inject.internal.asm.$ClassReader;
import com.google.inject.internal.asm.$ClassWriter;
import com.google.inject.internal.asm.$Label;

public class $Attribute {
    public final String type;
    byte[] b;
    $Attribute a;

    protected $Attribute(String string) {
        this.type = string;
    }

    public boolean isUnknown() {
        return true;
    }

    public boolean isCodeAttribute() {
        return false;
    }

    protected $Label[] getLabels() {
        return null;
    }

    protected $Attribute read($ClassReader $ClassReader, int n, int n2, char[] cArray, int n3, $Label[] $LabelArray) {
        $Attribute $Attribute = new $Attribute(this.type);
        $Attribute.b = new byte[n2];
        System.arraycopy($ClassReader.b, n, $Attribute.b, 0, n2);
        return $Attribute;
    }

    protected $ByteVector write($ClassWriter $ClassWriter, byte[] byArray, int n, int n2, int n3) {
        $ByteVector $ByteVector = new $ByteVector();
        $ByteVector.a = this.b;
        $ByteVector.b = this.b.length;
        return $ByteVector;
    }

    final int a() {
        int n = 0;
        $Attribute $Attribute = this;
        while ($Attribute != null) {
            ++n;
            $Attribute = $Attribute.a;
        }
        return n;
    }

    final int a($ClassWriter $ClassWriter, byte[] byArray, int n, int n2, int n3) {
        $Attribute $Attribute = this;
        int n4 = 0;
        while ($Attribute != null) {
            $ClassWriter.newUTF8($Attribute.type);
            n4 += $Attribute.write(($ClassWriter)$ClassWriter, (byte[])byArray, (int)n, (int)n2, (int)n3).b + 6;
            $Attribute = $Attribute.a;
        }
        return n4;
    }

    final void a($ClassWriter $ClassWriter, byte[] byArray, int n, int n2, int n3, $ByteVector $ByteVector) {
        $Attribute $Attribute = this;
        while ($Attribute != null) {
            $ByteVector $ByteVector2 = $Attribute.write($ClassWriter, byArray, n, n2, n3);
            $ByteVector.putShort($ClassWriter.newUTF8($Attribute.type)).putInt($ByteVector2.b);
            $ByteVector.putByteArray($ByteVector2.a, 0, $ByteVector2.b);
            $Attribute = $Attribute.a;
        }
    }
}

