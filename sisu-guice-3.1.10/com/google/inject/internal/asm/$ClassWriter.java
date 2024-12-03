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
import com.google.inject.internal.asm.$Handle;
import com.google.inject.internal.asm.$Item;
import com.google.inject.internal.asm.$MethodVisitor;
import com.google.inject.internal.asm.$MethodWriter;
import com.google.inject.internal.asm.$Type;
import com.google.inject.internal.asm.$TypePath;

/*
 * Duplicate member names - consider using --renamedupmembers true
 */
public class $ClassWriter
extends $ClassVisitor {
    public static final int COMPUTE_MAXS = 1;
    public static final int COMPUTE_FRAMES = 2;
    static final byte[] a;
    $ClassReader M;
    int b;
    int c = 1;
    final $ByteVector d = new $ByteVector();
    $Item[] e = new $Item[256];
    int f = (int)(0.75 * (double)this.e.length);
    final $Item g = new $Item();
    final $Item h = new $Item();
    final $Item i = new $Item();
    final $Item j = new $Item();
    $Item[] H;
    private short G;
    private int k;
    private int l;
    String I;
    private int m;
    private int n;
    private int o;
    private int[] p;
    private int q;
    private $ByteVector r;
    private int s;
    private int t;
    private $AnnotationWriter u;
    private $AnnotationWriter v;
    private $AnnotationWriter N;
    private $AnnotationWriter O;
    private $Attribute w;
    private int x;
    private $ByteVector y;
    int z;
    $ByteVector A;
    $FieldWriter B;
    $FieldWriter C;
    $MethodWriter D;
    $MethodWriter E;
    private boolean K;
    private boolean J;
    boolean L;

    public $ClassWriter(int n) {
        super(327680);
        this.K = (n & 1) != 0;
        this.J = (n & 2) != 0;
    }

    public $ClassWriter($ClassReader $ClassReader, int n) {
        this(n);
        $ClassReader.a(this);
        this.M = $ClassReader;
    }

    public final void visit(int n, int n2, String string, String string2, String string3, String[] stringArray) {
        this.b = n;
        this.k = n2;
        this.l = this.newClass(string);
        this.I = string;
        if (string2 != null) {
            this.m = this.newUTF8(string2);
        }
        int n3 = this.n = string3 == null ? 0 : this.newClass(string3);
        if (stringArray != null && stringArray.length > 0) {
            this.o = stringArray.length;
            this.p = new int[this.o];
            for (int i = 0; i < this.o; ++i) {
                this.p[i] = this.newClass(stringArray[i]);
            }
        }
    }

    public final void visitSource(String string, String string2) {
        if (string != null) {
            this.q = this.newUTF8(string);
        }
        if (string2 != null) {
            this.r = new $ByteVector().putUTF8(string2);
        }
    }

    public final void visitOuterClass(String string, String string2, String string3) {
        this.s = this.newClass(string);
        if (string2 != null && string3 != null) {
            this.t = this.newNameType(string2, string3);
        }
    }

    public final $AnnotationVisitor visitAnnotation(String string, boolean bl) {
        $ByteVector $ByteVector = new $ByteVector();
        $ByteVector.putShort(this.newUTF8(string)).putShort(0);
        $AnnotationWriter $AnnotationWriter = new $AnnotationWriter(this, true, $ByteVector, $ByteVector, 2);
        if (bl) {
            $AnnotationWriter.g = this.u;
            this.u = $AnnotationWriter;
        } else {
            $AnnotationWriter.g = this.v;
            this.v = $AnnotationWriter;
        }
        return $AnnotationWriter;
    }

    public final $AnnotationVisitor visitTypeAnnotation(int n, $TypePath $TypePath, String string, boolean bl) {
        $ByteVector $ByteVector = new $ByteVector();
        $AnnotationWriter.a(n, $TypePath, $ByteVector);
        $ByteVector.putShort(this.newUTF8(string)).putShort(0);
        $AnnotationWriter $AnnotationWriter = new $AnnotationWriter(this, true, $ByteVector, $ByteVector, $ByteVector.b - 2);
        if (bl) {
            $AnnotationWriter.g = this.N;
            this.N = $AnnotationWriter;
        } else {
            $AnnotationWriter.g = this.O;
            this.O = $AnnotationWriter;
        }
        return $AnnotationWriter;
    }

    public final void visitAttribute($Attribute $Attribute) {
        $Attribute.a = this.w;
        this.w = $Attribute;
    }

    public final void visitInnerClass(String string, String string2, String string3, int n) {
        if (this.y == null) {
            this.y = new $ByteVector();
        }
        ++this.x;
        this.y.putShort(string == null ? 0 : this.newClass(string));
        this.y.putShort(string2 == null ? 0 : this.newClass(string2));
        this.y.putShort(string3 == null ? 0 : this.newUTF8(string3));
        this.y.putShort(n);
    }

    public final $FieldVisitor visitField(int n, String string, String string2, String string3, Object object) {
        return new $FieldWriter(this, n, string, string2, string3, object);
    }

    public final $MethodVisitor visitMethod(int n, String string, String string2, String string3, String[] stringArray) {
        return new $MethodWriter(this, n, string, string2, string3, stringArray, this.K, this.J);
    }

    public final void visitEnd() {
    }

    public byte[] toByteArray() {
        int n;
        if (this.c > 65535) {
            throw new RuntimeException("Class file too large!");
        }
        int n2 = 24 + 2 * this.o;
        int n3 = 0;
        $FieldWriter $FieldWriter = this.B;
        while ($FieldWriter != null) {
            ++n3;
            n2 += $FieldWriter.a();
            $FieldWriter = ($FieldWriter)$FieldWriter.fv;
        }
        int n4 = 0;
        $MethodWriter $MethodWriter = this.D;
        while ($MethodWriter != null) {
            ++n4;
            n2 += $MethodWriter.a();
            $MethodWriter = ($MethodWriter)$MethodWriter.mv;
        }
        int n5 = 0;
        if (this.A != null) {
            ++n5;
            n2 += 8 + this.A.b;
            this.newUTF8("BootstrapMethods");
        }
        if (this.m != 0) {
            ++n5;
            n2 += 8;
            this.newUTF8("Signature");
        }
        if (this.q != 0) {
            ++n5;
            n2 += 8;
            this.newUTF8("SourceFile");
        }
        if (this.r != null) {
            ++n5;
            n2 += this.r.b + 4;
            this.newUTF8("SourceDebugExtension");
        }
        if (this.s != 0) {
            ++n5;
            n2 += 10;
            this.newUTF8("EnclosingMethod");
        }
        if ((this.k & 0x20000) != 0) {
            ++n5;
            n2 += 6;
            this.newUTF8("Deprecated");
        }
        if ((this.k & 0x1000) != 0 && ((this.b & 0xFFFF) < 49 || (this.k & 0x40000) != 0)) {
            ++n5;
            n2 += 6;
            this.newUTF8("Synthetic");
        }
        if (this.y != null) {
            ++n5;
            n2 += 8 + this.y.b;
            this.newUTF8("InnerClasses");
        }
        if (this.u != null) {
            ++n5;
            n2 += 8 + this.u.a();
            this.newUTF8("RuntimeVisibleAnnotations");
        }
        if (this.v != null) {
            ++n5;
            n2 += 8 + this.v.a();
            this.newUTF8("RuntimeInvisibleAnnotations");
        }
        if (this.N != null) {
            ++n5;
            n2 += 8 + this.N.a();
            this.newUTF8("RuntimeVisibleTypeAnnotations");
        }
        if (this.O != null) {
            ++n5;
            n2 += 8 + this.O.a();
            this.newUTF8("RuntimeInvisibleTypeAnnotations");
        }
        if (this.w != null) {
            n5 += this.w.a();
            n2 += this.w.a(this, null, 0, -1, -1);
        }
        $ByteVector $ByteVector = new $ByteVector(n2 += this.d.b);
        $ByteVector.putInt(-889275714).putInt(this.b);
        $ByteVector.putShort(this.c).putByteArray(this.d.a, 0, this.d.b);
        int n6 = 0x60000 | (this.k & 0x40000) / 64;
        $ByteVector.putShort(this.k & ~n6).putShort(this.l).putShort(this.n);
        $ByteVector.putShort(this.o);
        for (n = 0; n < this.o; ++n) {
            $ByteVector.putShort(this.p[n]);
        }
        $ByteVector.putShort(n3);
        $FieldWriter = this.B;
        while ($FieldWriter != null) {
            $FieldWriter.a($ByteVector);
            $FieldWriter = ($FieldWriter)$FieldWriter.fv;
        }
        $ByteVector.putShort(n4);
        $MethodWriter = this.D;
        while ($MethodWriter != null) {
            $MethodWriter.a($ByteVector);
            $MethodWriter = ($MethodWriter)$MethodWriter.mv;
        }
        $ByteVector.putShort(n5);
        if (this.A != null) {
            $ByteVector.putShort(this.newUTF8("BootstrapMethods"));
            $ByteVector.putInt(this.A.b + 2).putShort(this.z);
            $ByteVector.putByteArray(this.A.a, 0, this.A.b);
        }
        if (this.m != 0) {
            $ByteVector.putShort(this.newUTF8("Signature")).putInt(2).putShort(this.m);
        }
        if (this.q != 0) {
            $ByteVector.putShort(this.newUTF8("SourceFile")).putInt(2).putShort(this.q);
        }
        if (this.r != null) {
            n = this.r.b - 2;
            $ByteVector.putShort(this.newUTF8("SourceDebugExtension")).putInt(n);
            $ByteVector.putByteArray(this.r.a, 2, n);
        }
        if (this.s != 0) {
            $ByteVector.putShort(this.newUTF8("EnclosingMethod")).putInt(4);
            $ByteVector.putShort(this.s).putShort(this.t);
        }
        if ((this.k & 0x20000) != 0) {
            $ByteVector.putShort(this.newUTF8("Deprecated")).putInt(0);
        }
        if ((this.k & 0x1000) != 0 && ((this.b & 0xFFFF) < 49 || (this.k & 0x40000) != 0)) {
            $ByteVector.putShort(this.newUTF8("Synthetic")).putInt(0);
        }
        if (this.y != null) {
            $ByteVector.putShort(this.newUTF8("InnerClasses"));
            $ByteVector.putInt(this.y.b + 2).putShort(this.x);
            $ByteVector.putByteArray(this.y.a, 0, this.y.b);
        }
        if (this.u != null) {
            $ByteVector.putShort(this.newUTF8("RuntimeVisibleAnnotations"));
            this.u.a($ByteVector);
        }
        if (this.v != null) {
            $ByteVector.putShort(this.newUTF8("RuntimeInvisibleAnnotations"));
            this.v.a($ByteVector);
        }
        if (this.N != null) {
            $ByteVector.putShort(this.newUTF8("RuntimeVisibleTypeAnnotations"));
            this.N.a($ByteVector);
        }
        if (this.O != null) {
            $ByteVector.putShort(this.newUTF8("RuntimeInvisibleTypeAnnotations"));
            this.O.a($ByteVector);
        }
        if (this.w != null) {
            this.w.a(this, null, 0, -1, -1, $ByteVector);
        }
        if (this.L) {
            this.u = null;
            this.v = null;
            this.w = null;
            this.x = 0;
            this.y = null;
            this.z = 0;
            this.A = null;
            this.B = null;
            this.C = null;
            this.D = null;
            this.E = null;
            this.K = false;
            this.J = true;
            this.L = false;
            new $ClassReader($ByteVector.a).accept(this, 4);
            return this.toByteArray();
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
            int n = $Type.getSort();
            if (n == 10) {
                return this.a($Type.getInternalName());
            }
            if (n == 11) {
                return this.c($Type.getDescriptor());
            }
            return this.a($Type.getDescriptor());
        }
        if (object instanceof $Handle) {
            $Handle $Handle = ($Handle)object;
            return this.a($Handle.a, $Handle.b, $Handle.c, $Handle.d);
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

    $Item c(String string) {
        this.h.a(16, string, null, null);
        $Item $Item = this.a(this.h);
        if ($Item == null) {
            this.d.b(16, this.newUTF8(string));
            $Item = new $Item(this.c++, this.h);
            this.b($Item);
        }
        return $Item;
    }

    public int newMethodType(String string) {
        return this.c((String)string).a;
    }

    $Item a(int n, String string, String string2, String string3) {
        this.j.a(20 + n, string, string2, string3);
        $Item $Item = this.a(this.j);
        if ($Item == null) {
            if (n <= 4) {
                this.b(15, n, this.newField(string, string2, string3));
            } else {
                this.b(15, n, this.newMethod(string, string2, string3, n == 9));
            }
            $Item = new $Item(this.c++, this.j);
            this.b($Item);
        }
        return $Item;
    }

    public int newHandle(int n, String string, String string2, String string3) {
        return this.a((int)n, (String)string, (String)string2, (String)string3).a;
    }

    $Item a(String string, String string2, $Handle $Handle, Object ... objectArray) {
        int n;
        $ByteVector $ByteVector = this.A;
        if ($ByteVector == null) {
            $ByteVector = this.A = new $ByteVector();
        }
        int n2 = $ByteVector.b;
        int n3 = $Handle.hashCode();
        $ByteVector.putShort(this.newHandle($Handle.a, $Handle.b, $Handle.c, $Handle.d));
        int n4 = objectArray.length;
        $ByteVector.putShort(n4);
        for (int i = 0; i < n4; ++i) {
            Object object = objectArray[i];
            n3 ^= object.hashCode();
            $ByteVector.putShort(this.newConst(object));
        }
        byte[] byArray = $ByteVector.a;
        int n5 = 2 + n4 << 1;
        $Item $Item = this.e[(n3 &= Integer.MAX_VALUE) % this.e.length];
        block1: while ($Item != null) {
            if ($Item.b != 33 || $Item.j != n3) {
                $Item = $Item.k;
                continue;
            }
            n = $Item.c;
            for (int i = 0; i < n5; ++i) {
                if (byArray[n2 + i] == byArray[n + i]) continue;
                $Item = $Item.k;
                continue block1;
            }
        }
        if ($Item != null) {
            n = $Item.a;
            $ByteVector.b = n2;
        } else {
            n = this.z++;
            $Item = new $Item(n);
            $Item.a(n2, n3);
            this.b($Item);
        }
        this.i.a(string, string2, n);
        $Item = this.a(this.i);
        if ($Item == null) {
            this.a(18, n, this.newNameType(string, string2));
            $Item = new $Item(this.c++, this.i);
            this.b($Item);
        }
        return $Item;
    }

    public int newInvokeDynamic(String string, String string2, $Handle $Handle, Object ... objectArray) {
        return this.a((String)string, (String)string2, ($Handle)$Handle, (Object[])objectArray).a;
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
            this.c += 2;
            this.b($Item);
        }
        return $Item;
    }

    $Item a(double d) {
        this.g.a(d);
        $Item $Item = this.a(this.g);
        if ($Item == null) {
            this.d.putByte(6).putLong(this.g.d);
            $Item = new $Item(this.c, this.g);
            this.c += 2;
            this.b($Item);
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
        return this.a((String)string, (String)string2).a;
    }

    $Item a(String string, String string2) {
        this.h.a(12, string, string2, null);
        $Item $Item = this.a(this.h);
        if ($Item == null) {
            this.a(12, this.newUTF8(string), this.newUTF8(string2));
            $Item = new $Item(this.c++, this.h);
            this.b($Item);
        }
        return $Item;
    }

    int c(String string) {
        this.g.a(30, string, null, null);
        $Item $Item = this.a(this.g);
        if ($Item == null) {
            $Item = this.c(this.g);
        }
        return $Item.a;
    }

    int a(String string, int n) {
        this.g.b = 31;
        this.g.c = n;
        this.g.g = string;
        this.g.j = Integer.MAX_VALUE & 31 + string.hashCode() + n;
        $Item $Item = this.a(this.g);
        if ($Item == null) {
            $Item = this.c(this.g);
        }
        return $Item.a;
    }

    private $Item c($Item $Item) {
        this.G = (short)(this.G + 1);
        $Item $Item2 = new $Item(this.G, this.g);
        this.b($Item2);
        if (this.H == null) {
            this.H = new $Item[16];
        }
        if (this.G == this.H.length) {
            $Item[] $ItemArray = new $Item[2 * this.H.length];
            System.arraycopy(this.H, 0, $ItemArray, 0, this.H.length);
            this.H = $ItemArray;
        }
        this.H[this.G] = $Item2;
        return $Item2;
    }

    int a(int n, int n2) {
        this.h.b = 32;
        this.h.d = (long)n | (long)n2 << 32;
        this.h.j = Integer.MAX_VALUE & 32 + n + n2;
        $Item $Item = this.a(this.h);
        if ($Item == null) {
            String string = this.H[n].g;
            String string2 = this.H[n2].g;
            this.h.c = this.c(this.getCommonSuperClass(string, string2));
            $Item = new $Item(0, this.h);
            this.b($Item);
        }
        return $Item.c;
    }

    protected String getCommonSuperClass(String string, String string2) {
        Class<?> clazz;
        Class<?> clazz2;
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            clazz2 = Class.forName(string.replace('/', '.'), false, classLoader);
            clazz = Class.forName(string2.replace('/', '.'), false, classLoader);
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
        while (!($Item2 == null || $Item2.b == $Item.b && $Item.a($Item2))) {
            $Item2 = $Item2.k;
        }
        return $Item2;
    }

    private void b($Item $Item) {
        int n;
        if (this.c + this.G > this.f) {
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

    private void b(int n, int n2, int n3) {
        this.d.a(n, n2).putShort(n3);
    }

    static {
        byte[] byArray = new byte[220];
        String string = "AAAAAAAAAAAAAAAABCLMMDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAANAAAAAAAAAAAAAAAAAAAAJJJJJJJJJJJJJJJJDOPAAAAAAGGGGGGGHIFBFAAFFAARQJJKKJJJJJJJJJJJJJJJJJJ";
        for (int i = 0; i < byArray.length; ++i) {
            byArray[i] = (byte)(string.charAt(i) - 65);
        }
        a = byArray;
    }
}

