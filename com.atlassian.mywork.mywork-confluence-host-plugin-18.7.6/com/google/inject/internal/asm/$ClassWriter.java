/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import com.google.inject.internal.asm.$AnnotationVisitor;
import com.google.inject.internal.asm.$AnnotationWriter;
import com.google.inject.internal.asm.$Attribute;
import com.google.inject.internal.asm.$ByteVector;
import com.google.inject.internal.asm.$ClassReader;
import com.google.inject.internal.asm.$ClassVisitor;
import com.google.inject.internal.asm.$FieldVisitor;
import com.google.inject.internal.asm.$FieldWriter;
import com.google.inject.internal.asm.$Item;
import com.google.inject.internal.asm.$MethodVisitor;
import com.google.inject.internal.asm.$MethodWriter;
import com.google.inject.internal.asm.$Type;

public class $ClassWriter
implements $ClassVisitor {
    public static final int COMPUTE_MAXS = 1;
    public static final int COMPUTE_FRAMES = 2;
    static final byte[] a;
    $ClassReader J;
    int b;
    int c = 1;
    final $ByteVector d = new $ByteVector();
    $Item[] e = new $Item[256];
    int f = (int)(0.75 * (double)this.e.length);
    final $Item g = new $Item();
    final $Item h = new $Item();
    final $Item i = new $Item();
    $Item[] E;
    private short D;
    private int j;
    private int k;
    String F;
    private int l;
    private int m;
    private int n;
    private int[] o;
    private int p;
    private $ByteVector q;
    private int r;
    private int s;
    private $AnnotationWriter t;
    private $AnnotationWriter u;
    private $Attribute v;
    private int w;
    private $ByteVector x;
    $FieldWriter y;
    $FieldWriter z;
    $MethodWriter A;
    $MethodWriter B;
    private final boolean H;
    private final boolean G;
    boolean I;

    public $ClassWriter(int n) {
        this.H = (n & 1) != 0;
        this.G = (n & 2) != 0;
    }

    public $ClassWriter($ClassReader $ClassReader, int n) {
        this(n);
        $ClassReader.a(this);
        this.J = $ClassReader;
    }

    public void visit(int n, int n2, String string, String string2, String string3, String[] stringArray) {
        this.b = n;
        this.j = n2;
        this.k = this.newClass(string);
        this.F = string;
        if (string2 != null) {
            this.l = this.newUTF8(string2);
        }
        int n3 = this.m = string3 == null ? 0 : this.newClass(string3);
        if (stringArray != null && stringArray.length > 0) {
            this.n = stringArray.length;
            this.o = new int[this.n];
            for (int i = 0; i < this.n; ++i) {
                this.o[i] = this.newClass(stringArray[i]);
            }
        }
    }

    public void visitSource(String string, String string2) {
        if (string != null) {
            this.p = this.newUTF8(string);
        }
        if (string2 != null) {
            this.q = new $ByteVector().putUTF8(string2);
        }
    }

    public void visitOuterClass(String string, String string2, String string3) {
        this.r = this.newClass(string);
        if (string2 != null && string3 != null) {
            this.s = this.newNameType(string2, string3);
        }
    }

    public $AnnotationVisitor visitAnnotation(String string, boolean bl) {
        $ByteVector $ByteVector = new $ByteVector();
        $ByteVector.putShort(this.newUTF8(string)).putShort(0);
        $AnnotationWriter $AnnotationWriter = new $AnnotationWriter(this, true, $ByteVector, $ByteVector, 2);
        if (bl) {
            $AnnotationWriter.g = this.t;
            this.t = $AnnotationWriter;
        } else {
            $AnnotationWriter.g = this.u;
            this.u = $AnnotationWriter;
        }
        return $AnnotationWriter;
    }

    public void visitAttribute($Attribute $Attribute) {
        $Attribute.a = this.v;
        this.v = $Attribute;
    }

    public void visitInnerClass(String string, String string2, String string3, int n) {
        if (this.x == null) {
            this.x = new $ByteVector();
        }
        ++this.w;
        this.x.putShort(string == null ? 0 : this.newClass(string));
        this.x.putShort(string2 == null ? 0 : this.newClass(string2));
        this.x.putShort(string3 == null ? 0 : this.newUTF8(string3));
        this.x.putShort(n);
    }

    public $FieldVisitor visitField(int n, String string, String string2, String string3, Object object) {
        return new $FieldWriter(this, n, string, string2, string3, object);
    }

    public $MethodVisitor visitMethod(int n, String string, String string2, String string3, String[] stringArray) {
        return new $MethodWriter(this, n, string, string2, string3, stringArray, this.H, this.G);
    }

    public void visitEnd() {
    }

    public byte[] toByteArray() {
        int n;
        int n2 = 24 + 2 * this.n;
        int n3 = 0;
        $FieldWriter $FieldWriter = this.y;
        while ($FieldWriter != null) {
            ++n3;
            n2 += $FieldWriter.a();
            $FieldWriter = $FieldWriter.a;
        }
        int n4 = 0;
        $MethodWriter $MethodWriter = this.A;
        while ($MethodWriter != null) {
            ++n4;
            n2 += $MethodWriter.a();
            $MethodWriter = $MethodWriter.a;
        }
        int n5 = 0;
        if (this.l != 0) {
            ++n5;
            n2 += 8;
            this.newUTF8("Signature");
        }
        if (this.p != 0) {
            ++n5;
            n2 += 8;
            this.newUTF8("SourceFile");
        }
        if (this.q != null) {
            ++n5;
            n2 += this.q.b + 4;
            this.newUTF8("SourceDebugExtension");
        }
        if (this.r != 0) {
            ++n5;
            n2 += 10;
            this.newUTF8("EnclosingMethod");
        }
        if ((this.j & 0x20000) != 0) {
            ++n5;
            n2 += 6;
            this.newUTF8("Deprecated");
        }
        if ((this.j & 0x1000) != 0 && (this.b & 0xFFFF) < 49) {
            ++n5;
            n2 += 6;
            this.newUTF8("Synthetic");
        }
        if (this.x != null) {
            ++n5;
            n2 += 8 + this.x.b;
            this.newUTF8("InnerClasses");
        }
        if (this.t != null) {
            ++n5;
            n2 += 8 + this.t.a();
            this.newUTF8("RuntimeVisibleAnnotations");
        }
        if (this.u != null) {
            ++n5;
            n2 += 8 + this.u.a();
            this.newUTF8("RuntimeInvisibleAnnotations");
        }
        if (this.v != null) {
            n5 += this.v.a();
            n2 += this.v.a(this, null, 0, -1, -1);
        }
        $ByteVector $ByteVector = new $ByteVector(n2 += this.d.b);
        $ByteVector.putInt(-889275714).putInt(this.b);
        $ByteVector.putShort(this.c).putByteArray(this.d.a, 0, this.d.b);
        $ByteVector.putShort(this.j).putShort(this.k).putShort(this.m);
        $ByteVector.putShort(this.n);
        for (n = 0; n < this.n; ++n) {
            $ByteVector.putShort(this.o[n]);
        }
        $ByteVector.putShort(n3);
        $FieldWriter = this.y;
        while ($FieldWriter != null) {
            $FieldWriter.a($ByteVector);
            $FieldWriter = $FieldWriter.a;
        }
        $ByteVector.putShort(n4);
        $MethodWriter = this.A;
        while ($MethodWriter != null) {
            $MethodWriter.a($ByteVector);
            $MethodWriter = $MethodWriter.a;
        }
        $ByteVector.putShort(n5);
        if (this.l != 0) {
            $ByteVector.putShort(this.newUTF8("Signature")).putInt(2).putShort(this.l);
        }
        if (this.p != 0) {
            $ByteVector.putShort(this.newUTF8("SourceFile")).putInt(2).putShort(this.p);
        }
        if (this.q != null) {
            n = this.q.b - 2;
            $ByteVector.putShort(this.newUTF8("SourceDebugExtension")).putInt(n);
            $ByteVector.putByteArray(this.q.a, 2, n);
        }
        if (this.r != 0) {
            $ByteVector.putShort(this.newUTF8("EnclosingMethod")).putInt(4);
            $ByteVector.putShort(this.r).putShort(this.s);
        }
        if ((this.j & 0x20000) != 0) {
            $ByteVector.putShort(this.newUTF8("Deprecated")).putInt(0);
        }
        if ((this.j & 0x1000) != 0 && (this.b & 0xFFFF) < 49) {
            $ByteVector.putShort(this.newUTF8("Synthetic")).putInt(0);
        }
        if (this.x != null) {
            $ByteVector.putShort(this.newUTF8("InnerClasses"));
            $ByteVector.putInt(this.x.b + 2).putShort(this.w);
            $ByteVector.putByteArray(this.x.a, 0, this.x.b);
        }
        if (this.t != null) {
            $ByteVector.putShort(this.newUTF8("RuntimeVisibleAnnotations"));
            this.t.a($ByteVector);
        }
        if (this.u != null) {
            $ByteVector.putShort(this.newUTF8("RuntimeInvisibleAnnotations"));
            this.u.a($ByteVector);
        }
        if (this.v != null) {
            this.v.a(this, null, 0, -1, -1, $ByteVector);
        }
        if (this.I) {
            $ClassWriter $ClassWriter = new $ClassWriter(2);
            new $ClassReader($ByteVector.a).accept($ClassWriter, 4);
            return $ClassWriter.toByteArray();
        }
        return $ByteVector.a;
    }

    $Item a(Object object) {
        if (object instanceof Integer) {
            int n = (Integer)object;
            return this.a(n);
        }
        if (object instanceof Byte) {
            int n = ((Byte)object).intValue();
            return this.a(n);
        }
        if (object instanceof Character) {
            char c = ((Character)object).charValue();
            return this.a(c);
        }
        if (object instanceof Short) {
            int n = ((Short)object).intValue();
            return this.a(n);
        }
        if (object instanceof Boolean) {
            int n = (Boolean)object != false ? 1 : 0;
            return this.a(n);
        }
        if (object instanceof Float) {
            float f = ((Float)object).floatValue();
            return this.a(f);
        }
        if (object instanceof Long) {
            long l = (Long)object;
            return this.a(l);
        }
        if (object instanceof Double) {
            double d = (Double)object;
            return this.a(d);
        }
        if (object instanceof String) {
            return this.b((String)object);
        }
        if (object instanceof $Type) {
            $Type $Type = ($Type)object;
            return this.a($Type.getSort() == 10 ? $Type.getInternalName() : $Type.getDescriptor());
        }
        throw new IllegalArgumentException("value " + object);
    }

    public int newConst(Object object) {
        return this.a((Object)object).a;
    }

    public int newUTF8(String string) {
        this.g.a(1, string, null, null);
        $Item $Item = this.a(this.g);
        if ($Item == null) {
            this.d.putByte(1).putUTF8(string);
            $Item = new $Item(this.c++, this.g);
            this.b($Item);
        }
        return $Item.a;
    }

    $Item a(String string) {
        this.h.a(7, string, null, null);
        $Item $Item = this.a(this.h);
        if ($Item == null) {
            this.d.b(7, this.newUTF8(string));
            $Item = new $Item(this.c++, this.h);
            this.b($Item);
        }
        return $Item;
    }

    public int newClass(String string) {
        return this.a((String)string).a;
    }

    $Item a(String string, String string2, String string3) {
        this.i.a(9, string, string2, string3);
        $Item $Item = this.a(this.i);
        if ($Item == null) {
            this.a(9, this.newClass(string), this.newNameType(string2, string3));
            $Item = new $Item(this.c++, this.i);
            this.b($Item);
        }
        return $Item;
    }

    public int newField(String string, String string2, String string3) {
        return this.a((String)string, (String)string2, (String)string3).a;
    }

    $Item a(String string, String string2, String string3, boolean bl) {
        int n = bl ? 11 : 10;
        this.i.a(n, string, string2, string3);
        $Item $Item = this.a(this.i);
        if ($Item == null) {
            this.a(n, this.newClass(string), this.newNameType(string2, string3));
            $Item = new $Item(this.c++, this.i);
            this.b($Item);
        }
        return $Item;
    }

    public int newMethod(String string, String string2, String string3, boolean bl) {
        return this.a((String)string, (String)string2, (String)string3, (boolean)bl).a;
    }

    $Item a(int n) {
        this.g.a(n);
        $Item $Item = this.a(this.g);
        if ($Item == null) {
            this.d.putByte(3).putInt(n);
            $Item = new $Item(this.c++, this.g);
            this.b($Item);
        }
        return $Item;
    }

    $Item a(float f) {
        this.g.a(f);
        $Item $Item = this.a(this.g);
        if ($Item == null) {
            this.d.putByte(4).putInt(this.g.c);
            $Item = new $Item(this.c++, this.g);
            this.b($Item);
        }
        return $Item;
    }

    $Item a(long l) {
        this.g.a(l);
        $Item $Item = this.a(this.g);
        if ($Item == null) {
            this.d.putByte(5).putLong(l);
            $Item = new $Item(this.c, this.g);
            this.b($Item);
            this.c += 2;
        }
        return $Item;
    }

    $Item a(double d) {
        this.g.a(d);
        $Item $Item = this.a(this.g);
        if ($Item == null) {
            this.d.putByte(6).putLong(this.g.d);
            $Item = new $Item(this.c, this.g);
            this.b($Item);
            this.c += 2;
        }
        return $Item;
    }

    private $Item b(String string) {
        this.h.a(8, string, null, null);
        $Item $Item = this.a(this.h);
        if ($Item == null) {
            this.d.b(8, this.newUTF8(string));
            $Item = new $Item(this.c++, this.h);
            this.b($Item);
        }
        return $Item;
    }

    public int newNameType(String string, String string2) {
        this.h.a(12, string, string2, null);
        $Item $Item = this.a(this.h);
        if ($Item == null) {
            this.a(12, this.newUTF8(string), this.newUTF8(string2));
            $Item = new $Item(this.c++, this.h);
            this.b($Item);
        }
        return $Item.a;
    }

    int c(String string) {
        this.g.a(13, string, null, null);
        $Item $Item = this.a(this.g);
        if ($Item == null) {
            $Item = this.c(this.g);
        }
        return $Item.a;
    }

    int a(String string, int n) {
        this.g.b = 14;
        this.g.c = n;
        this.g.g = string;
        this.g.j = Integer.MAX_VALUE & 14 + string.hashCode() + n;
        $Item $Item = this.a(this.g);
        if ($Item == null) {
            $Item = this.c(this.g);
        }
        return $Item.a;
    }

    private $Item c($Item $Item) {
        this.D = (short)(this.D + 1);
        $Item $Item2 = new $Item(this.D, this.g);
        this.b($Item2);
        if (this.E == null) {
            this.E = new $Item[16];
        }
        if (this.D == this.E.length) {
            $Item[] $ItemArray = new $Item[2 * this.E.length];
            System.arraycopy(this.E, 0, $ItemArray, 0, this.E.length);
            this.E = $ItemArray;
        }
        this.E[this.D] = $Item2;
        return $Item2;
    }

    int a(int n, int n2) {
        this.h.b = 15;
        this.h.d = (long)n | (long)n2 << 32;
        this.h.j = Integer.MAX_VALUE & 15 + n + n2;
        $Item $Item = this.a(this.h);
        if ($Item == null) {
            String string = this.E[n].g;
            String string2 = this.E[n2].g;
            this.h.c = this.c(this.getCommonSuperClass(string, string2));
            $Item = new $Item(0, this.h);
            this.b($Item);
        }
        return $Item.c;
    }

    protected String getCommonSuperClass(String string, String string2) {
        Class<?> clazz;
        Class<?> clazz2;
        try {
            clazz2 = Class.forName(string.replace('/', '.'));
            clazz = Class.forName(string2.replace('/', '.'));
        }
        catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        if (clazz2.isAssignableFrom(clazz)) {
            return string;
        }
        if (clazz.isAssignableFrom(clazz2)) {
            return string2;
        }
        if (clazz2.isInterface() || clazz.isInterface()) {
            return "java/lang/Object";
        }
        while (!(clazz2 = clazz2.getSuperclass()).isAssignableFrom(clazz)) {
        }
        return clazz2.getName().replace('.', '/');
    }

    private $Item a($Item $Item) {
        $Item $Item2 = this.e[$Item.j % this.e.length];
        while ($Item2 != null && !$Item.a($Item2)) {
            $Item2 = $Item2.k;
        }
        return $Item2;
    }

    private void b($Item $Item) {
        int n;
        if (this.c > this.f) {
            n = this.e.length;
            int n2 = n * 2 + 1;
            $Item[] $ItemArray = new $Item[n2];
            for (int i = n - 1; i >= 0; --i) {
                $Item $Item2 = this.e[i];
                while ($Item2 != null) {
                    int n3 = $Item2.j % $ItemArray.length;
                    $Item $Item3 = $Item2.k;
                    $Item2.k = $ItemArray[n3];
                    $ItemArray[n3] = $Item2;
                    $Item2 = $Item3;
                }
            }
            this.e = $ItemArray;
            this.f = (int)((double)n2 * 0.75);
        }
        n = $Item.j % this.e.length;
        $Item.k = this.e[n];
        this.e[n] = $Item;
    }

    private void a(int n, int n2, int n3) {
        this.d.b(n, n2).putShort(n3);
    }

    static {
        byte[] byArray = new byte[220];
        String string = "AAAAAAAAAAAAAAAABCKLLDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAIIIIIIIIIIIIIIIIDNOAAAAAAGGGGGGGHAFBFAAFFAAQPIIJJIIIIIIIIIIIIIIIIII";
        for (int i = 0; i < byArray.length; ++i) {
            byArray[i] = (byte)(string.charAt(i) - 65);
        }
        a = byArray;
    }
}

