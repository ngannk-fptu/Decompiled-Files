/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import com.google.inject.internal.asm.$AnnotationVisitor;
import com.google.inject.internal.asm.$Attribute;
import com.google.inject.internal.asm.$ByteVector;
import com.google.inject.internal.asm.$ClassVisitor;
import com.google.inject.internal.asm.$ClassWriter;
import com.google.inject.internal.asm.$Context;
import com.google.inject.internal.asm.$FieldVisitor;
import com.google.inject.internal.asm.$Handle;
import com.google.inject.internal.asm.$Item;
import com.google.inject.internal.asm.$Label;
import com.google.inject.internal.asm.$MethodVisitor;
import com.google.inject.internal.asm.$MethodWriter;
import com.google.inject.internal.asm.$Opcodes;
import com.google.inject.internal.asm.$Type;
import com.google.inject.internal.asm.$TypePath;
import java.io.IOException;
import java.io.InputStream;

public class $ClassReader {
    public static final int SKIP_CODE = 1;
    public static final int SKIP_DEBUG = 2;
    public static final int SKIP_FRAMES = 4;
    public static final int EXPAND_FRAMES = 8;
    public final byte[] b;
    private final int[] a;
    private final String[] c;
    private final int d;
    public final int header;

    public $ClassReader(byte[] byArray) {
        this(byArray, 0, byArray.length);
    }

    public $ClassReader(byte[] byArray, int n, int n2) {
        this.b = byArray;
        if (this.readShort(n + 6) > 52) {
            throw new IllegalArgumentException();
        }
        this.a = new int[this.readUnsignedShort(n + 8)];
        int n3 = this.a.length;
        this.c = new String[n3];
        int n4 = 0;
        int n5 = n + 10;
        for (int i = 1; i < n3; ++i) {
            int n6;
            this.a[i] = n5 + 1;
            switch (byArray[n5]) {
                case 3: 
                case 4: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 18: {
                    n6 = 5;
                    break;
                }
                case 5: 
                case 6: {
                    n6 = 9;
                    ++i;
                    break;
                }
                case 1: {
                    n6 = 3 + this.readUnsignedShort(n5 + 1);
                    if (n6 <= n4) break;
                    n4 = n6;
                    break;
                }
                case 15: {
                    n6 = 4;
                    break;
                }
                default: {
                    n6 = 3;
                }
            }
            n5 += n6;
        }
        this.d = n4;
        this.header = n5;
    }

    public int getAccess() {
        return this.readUnsignedShort(this.header);
    }

    public String getClassName() {
        return this.readClass(this.header + 2, new char[this.d]);
    }

    public String getSuperName() {
        return this.readClass(this.header + 4, new char[this.d]);
    }

    public String[] getInterfaces() {
        int n = this.header + 6;
        int n2 = this.readUnsignedShort(n);
        String[] stringArray = new String[n2];
        if (n2 > 0) {
            char[] cArray = new char[this.d];
            for (int i = 0; i < n2; ++i) {
                stringArray[i] = this.readClass(n += 2, cArray);
            }
        }
        return stringArray;
    }

    void a($ClassWriter $ClassWriter) {
        int n;
        char[] cArray = new char[this.d];
        int n2 = this.a.length;
        $Item[] $ItemArray = new $Item[n2];
        for (n = 1; n < n2; ++n) {
            int n3;
            int n4 = this.a[n];
            byte by = this.b[n4 - 1];
            $Item $Item = new $Item(n);
            switch (by) {
                case 9: 
                case 10: 
                case 11: {
                    int n5 = this.a[this.readUnsignedShort(n4 + 2)];
                    $Item.a(by, this.readClass(n4, cArray), this.readUTF8(n5, cArray), this.readUTF8(n5 + 2, cArray));
                    break;
                }
                case 3: {
                    $Item.a(this.readInt(n4));
                    break;
                }
                case 4: {
                    $Item.a(Float.intBitsToFloat(this.readInt(n4)));
                    break;
                }
                case 12: {
                    $Item.a(by, this.readUTF8(n4, cArray), this.readUTF8(n4 + 2, cArray), null);
                    break;
                }
                case 5: {
                    $Item.a(this.readLong(n4));
                    ++n;
                    break;
                }
                case 6: {
                    $Item.a(Double.longBitsToDouble(this.readLong(n4)));
                    ++n;
                    break;
                }
                case 1: {
                    String string = this.c[n];
                    if (string == null) {
                        n4 = this.a[n];
                        string = this.c[n] = this.a(n4 + 2, this.readUnsignedShort(n4), cArray);
                    }
                    $Item.a(by, string, null, null);
                    break;
                }
                case 15: {
                    n3 = this.a[this.readUnsignedShort(n4 + 1)];
                    int n5 = this.a[this.readUnsignedShort(n3 + 2)];
                    $Item.a(20 + this.readByte(n4), this.readClass(n3, cArray), this.readUTF8(n5, cArray), this.readUTF8(n5 + 2, cArray));
                    break;
                }
                case 18: {
                    if ($ClassWriter.A == null) {
                        this.a($ClassWriter, $ItemArray, cArray);
                    }
                    int n5 = this.a[this.readUnsignedShort(n4 + 2)];
                    $Item.a(this.readUTF8(n5, cArray), this.readUTF8(n5 + 2, cArray), this.readUnsignedShort(n4));
                    break;
                }
                default: {
                    $Item.a(by, this.readUTF8(n4, cArray), null, null);
                }
            }
            n3 = $Item.j % $ItemArray.length;
            $Item.k = $ItemArray[n3];
            $ItemArray[n3] = $Item;
        }
        n = this.a[1] - 1;
        $ClassWriter.d.putByteArray(this.b, n, this.header - n);
        $ClassWriter.e = $ItemArray;
        $ClassWriter.f = (int)(0.75 * (double)n2);
        $ClassWriter.c = n2;
    }

    private void a($ClassWriter $ClassWriter, $Item[] $ItemArray, char[] cArray) {
        int n;
        int n2;
        int n3 = this.a();
        boolean bl = false;
        for (n2 = this.readUnsignedShort(n3); n2 > 0; --n2) {
            String string = this.readUTF8(n3 + 2, cArray);
            if ("BootstrapMethods".equals(string)) {
                bl = true;
                break;
            }
            n3 += 6 + this.readInt(n3 + 4);
        }
        if (!bl) {
            return;
        }
        n2 = this.readUnsignedShort(n3 + 8);
        int n4 = n3 + 10;
        for (n = 0; n < n2; ++n) {
            int n5 = n4 - n3 - 10;
            int n6 = this.readConst(this.readUnsignedShort(n4), cArray).hashCode();
            for (int i = this.readUnsignedShort(n4 + 2); i > 0; --i) {
                n6 ^= this.readConst(this.readUnsignedShort(n4 + 4), cArray).hashCode();
                n4 += 2;
            }
            n4 += 4;
            $Item $Item = new $Item(n);
            $Item.a(n5, n6 & Integer.MAX_VALUE);
            int n7 = $Item.j % $ItemArray.length;
            $Item.k = $ItemArray[n7];
            $ItemArray[n7] = $Item;
        }
        n = this.readInt(n3 + 4);
        $ByteVector $ByteVector = new $ByteVector(n + 62);
        $ByteVector.putByteArray(this.b, n3 + 10, n - 2);
        $ClassWriter.z = n2;
        $ClassWriter.A = $ByteVector;
    }

    public $ClassReader(InputStream inputStream) throws IOException {
        this($ClassReader.a(inputStream, false));
    }

    public $ClassReader(String string) throws IOException {
        this($ClassReader.a(ClassLoader.getSystemResourceAsStream(string.replace('.', '/') + ".class"), true));
    }

    private static byte[] a(InputStream inputStream, boolean bl) throws IOException {
        if (inputStream == null) {
            throw new IOException("Class not found");
        }
        try {
            byte[] byArray = new byte[inputStream.available()];
            int n = 0;
            while (true) {
                byte[] byArray2;
                int n2;
                if ((n2 = inputStream.read(byArray, n, byArray.length - n)) == -1) {
                    byte[] byArray3;
                    if (n < byArray.length) {
                        byArray3 = new byte[n];
                        System.arraycopy(byArray, 0, byArray3, 0, n);
                        byArray = byArray3;
                    }
                    byArray3 = byArray;
                    return byArray3;
                }
                if ((n += n2) != byArray.length) continue;
                int n3 = inputStream.read();
                if (n3 < 0) {
                    byArray2 = byArray;
                    return byArray2;
                }
                byArray2 = new byte[byArray.length + 1000];
                System.arraycopy(byArray, 0, byArray2, 0, n);
                byArray2[n++] = (byte)n3;
                byArray = byArray2;
            }
        }
        finally {
            if (bl) {
                inputStream.close();
            }
        }
    }

    public void accept($ClassVisitor $ClassVisitor, int n) {
        this.accept($ClassVisitor, new $Attribute[0], n);
    }

    public void accept($ClassVisitor $ClassVisitor, $Attribute[] $AttributeArray, int n) {
        int n2;
        int n3 = this.header;
        char[] cArray = new char[this.d];
        $Context $Context = new $Context();
        $Context.a = $AttributeArray;
        $Context.b = n;
        $Context.c = cArray;
        int n4 = this.readUnsignedShort(n3);
        String string = this.readClass(n3 + 2, cArray);
        String string2 = this.readClass(n3 + 4, cArray);
        String[] stringArray = new String[this.readUnsignedShort(n3 + 6)];
        n3 += 8;
        for (int i = 0; i < stringArray.length; ++i) {
            stringArray[i] = this.readClass(n3, cArray);
            n3 += 2;
        }
        String string3 = null;
        String string4 = null;
        String string5 = null;
        String string6 = null;
        String string7 = null;
        String string8 = null;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        int n8 = 0;
        int n9 = 0;
        $Attribute $Attribute = null;
        n3 = this.a();
        for (n2 = this.readUnsignedShort(n3); n2 > 0; --n2) {
            String string9 = this.readUTF8(n3 + 2, cArray);
            if ("SourceFile".equals(string9)) {
                string4 = this.readUTF8(n3 + 8, cArray);
            } else if ("InnerClasses".equals(string9)) {
                n9 = n3 + 8;
            } else if ("EnclosingMethod".equals(string9)) {
                string6 = this.readClass(n3 + 8, cArray);
                int n10 = this.readUnsignedShort(n3 + 10);
                if (n10 != 0) {
                    string7 = this.readUTF8(this.a[n10], cArray);
                    string8 = this.readUTF8(this.a[n10] + 2, cArray);
                }
            } else if ("Signature".equals(string9)) {
                string3 = this.readUTF8(n3 + 8, cArray);
            } else if ("RuntimeVisibleAnnotations".equals(string9)) {
                n5 = n3 + 8;
            } else if ("RuntimeVisibleTypeAnnotations".equals(string9)) {
                n7 = n3 + 8;
            } else if ("Deprecated".equals(string9)) {
                n4 |= 0x20000;
            } else if ("Synthetic".equals(string9)) {
                n4 |= 0x41000;
            } else if ("SourceDebugExtension".equals(string9)) {
                int n11 = this.readInt(n3 + 4);
                string5 = this.a(n3 + 8, n11, new char[n11]);
            } else if ("RuntimeInvisibleAnnotations".equals(string9)) {
                n6 = n3 + 8;
            } else if ("RuntimeInvisibleTypeAnnotations".equals(string9)) {
                n8 = n3 + 8;
            } else if ("BootstrapMethods".equals(string9)) {
                int[] nArray = new int[this.readUnsignedShort(n3 + 8)];
                int n12 = n3 + 10;
                for (int i = 0; i < nArray.length; ++i) {
                    nArray[i] = n12;
                    n12 += 2 + this.readUnsignedShort(n12 + 2) << 1;
                }
                $Context.d = nArray;
            } else {
                $Attribute $Attribute2 = this.a($AttributeArray, string9, n3 + 8, this.readInt(n3 + 4), cArray, -1, null);
                if ($Attribute2 != null) {
                    $Attribute2.a = $Attribute;
                    $Attribute = $Attribute2;
                }
            }
            n3 += 6 + this.readInt(n3 + 4);
        }
        $ClassVisitor.visit(this.readInt(this.a[1] - 7), n4, string, string3, string2, stringArray);
        if ((n & 2) == 0 && (string4 != null || string5 != null)) {
            $ClassVisitor.visitSource(string4, string5);
        }
        if (string6 != null) {
            $ClassVisitor.visitOuterClass(string6, string7, string8);
        }
        if (n5 != 0) {
            int n13 = n5 + 2;
            for (n2 = this.readUnsignedShort(n5); n2 > 0; --n2) {
                n13 = this.a(n13 + 2, cArray, true, $ClassVisitor.visitAnnotation(this.readUTF8(n13, cArray), true));
            }
        }
        if (n6 != 0) {
            int n14 = n6 + 2;
            for (n2 = this.readUnsignedShort(n6); n2 > 0; --n2) {
                n14 = this.a(n14 + 2, cArray, true, $ClassVisitor.visitAnnotation(this.readUTF8(n14, cArray), false));
            }
        }
        if (n7 != 0) {
            int n15 = n7 + 2;
            for (n2 = this.readUnsignedShort(n7); n2 > 0; --n2) {
                n15 = this.a($Context, n15);
                n15 = this.a(n15 + 2, cArray, true, $ClassVisitor.visitTypeAnnotation($Context.i, $Context.j, this.readUTF8(n15, cArray), true));
            }
        }
        if (n8 != 0) {
            int n16 = n8 + 2;
            for (n2 = this.readUnsignedShort(n8); n2 > 0; --n2) {
                n16 = this.a($Context, n16);
                n16 = this.a(n16 + 2, cArray, true, $ClassVisitor.visitTypeAnnotation($Context.i, $Context.j, this.readUTF8(n16, cArray), false));
            }
        }
        while ($Attribute != null) {
            $Attribute $Attribute3 = $Attribute.a;
            $Attribute.a = null;
            $ClassVisitor.visitAttribute($Attribute);
            $Attribute = $Attribute3;
        }
        if (n9 != 0) {
            n2 = n9 + 2;
            for (int i = this.readUnsignedShort(n9); i > 0; --i) {
                $ClassVisitor.visitInnerClass(this.readClass(n2, cArray), this.readClass(n2 + 2, cArray), this.readUTF8(n2 + 4, cArray), this.readUnsignedShort(n2 + 6));
                n2 += 8;
            }
        }
        n3 = this.header + 10 + 2 * stringArray.length;
        for (n2 = this.readUnsignedShort(n3 - 2); n2 > 0; --n2) {
            n3 = this.a($ClassVisitor, $Context, n3);
        }
        for (n2 = this.readUnsignedShort((n3 += 2) - 2); n2 > 0; --n2) {
            n3 = this.b($ClassVisitor, $Context, n3);
        }
        $ClassVisitor.visitEnd();
    }

    private int a($ClassVisitor $ClassVisitor, $Context $Context, int n) {
        int n2;
        char[] cArray = $Context.c;
        int n3 = this.readUnsignedShort(n);
        String string = this.readUTF8(n + 2, cArray);
        String string2 = this.readUTF8(n + 4, cArray);
        n += 6;
        String string3 = null;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        Object object = null;
        $Attribute $Attribute = null;
        for (int i = this.readUnsignedShort(n); i > 0; --i) {
            String string4 = this.readUTF8(n + 2, cArray);
            if ("ConstantValue".equals(string4)) {
                n2 = this.readUnsignedShort(n + 8);
                object = n2 == 0 ? null : this.readConst(n2, cArray);
            } else if ("Signature".equals(string4)) {
                string3 = this.readUTF8(n + 8, cArray);
            } else if ("Deprecated".equals(string4)) {
                n3 |= 0x20000;
            } else if ("Synthetic".equals(string4)) {
                n3 |= 0x41000;
            } else if ("RuntimeVisibleAnnotations".equals(string4)) {
                n4 = n + 8;
            } else if ("RuntimeVisibleTypeAnnotations".equals(string4)) {
                n6 = n + 8;
            } else if ("RuntimeInvisibleAnnotations".equals(string4)) {
                n5 = n + 8;
            } else if ("RuntimeInvisibleTypeAnnotations".equals(string4)) {
                n7 = n + 8;
            } else {
                $Attribute $Attribute2 = this.a($Context.a, string4, n + 8, this.readInt(n + 4), cArray, -1, null);
                if ($Attribute2 != null) {
                    $Attribute2.a = $Attribute;
                    $Attribute = $Attribute2;
                }
            }
            n += 6 + this.readInt(n + 4);
        }
        n += 2;
        $FieldVisitor $FieldVisitor = $ClassVisitor.visitField(n3, string, string2, string3, object);
        if ($FieldVisitor == null) {
            return n;
        }
        if (n4 != 0) {
            n2 = n4 + 2;
            for (int i = this.readUnsignedShort(n4); i > 0; --i) {
                n2 = this.a(n2 + 2, cArray, true, $FieldVisitor.visitAnnotation(this.readUTF8(n2, cArray), true));
            }
        }
        if (n5 != 0) {
            n2 = n5 + 2;
            for (int i = this.readUnsignedShort(n5); i > 0; --i) {
                n2 = this.a(n2 + 2, cArray, true, $FieldVisitor.visitAnnotation(this.readUTF8(n2, cArray), false));
            }
        }
        if (n6 != 0) {
            n2 = n6 + 2;
            for (int i = this.readUnsignedShort(n6); i > 0; --i) {
                n2 = this.a($Context, n2);
                n2 = this.a(n2 + 2, cArray, true, $FieldVisitor.visitTypeAnnotation($Context.i, $Context.j, this.readUTF8(n2, cArray), true));
            }
        }
        if (n7 != 0) {
            n2 = n7 + 2;
            for (int i = this.readUnsignedShort(n7); i > 0; --i) {
                n2 = this.a($Context, n2);
                n2 = this.a(n2 + 2, cArray, true, $FieldVisitor.visitTypeAnnotation($Context.i, $Context.j, this.readUTF8(n2, cArray), false));
            }
        }
        while ($Attribute != null) {
            $Attribute $Attribute3 = $Attribute.a;
            $Attribute.a = null;
            $FieldVisitor.visitAttribute($Attribute);
            $Attribute = $Attribute3;
        }
        $FieldVisitor.visitEnd();
        return n;
    }

    private int b($ClassVisitor $ClassVisitor, $Context $Context, int n) {
        int n2;
        Object object;
        char[] cArray = $Context.c;
        $Context.e = this.readUnsignedShort(n);
        $Context.f = this.readUTF8(n + 2, cArray);
        $Context.g = this.readUTF8(n + 4, cArray);
        n += 6;
        int n3 = 0;
        int n4 = 0;
        String[] stringArray = null;
        String string = null;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        int n8 = 0;
        int n9 = 0;
        int n10 = 0;
        int n11 = 0;
        int n12 = 0;
        int n13 = n;
        $Attribute $Attribute = null;
        for (int i = this.readUnsignedShort(n); i > 0; --i) {
            object = this.readUTF8(n + 2, cArray);
            if ("Code".equals(object)) {
                if (($Context.b & 1) == 0) {
                    n3 = n + 8;
                }
            } else if ("Exceptions".equals(object)) {
                stringArray = new String[this.readUnsignedShort(n + 8)];
                n4 = n + 10;
                for (n2 = 0; n2 < stringArray.length; ++n2) {
                    stringArray[n2] = this.readClass(n4, cArray);
                    n4 += 2;
                }
            } else if ("Signature".equals(object)) {
                string = this.readUTF8(n + 8, cArray);
            } else if ("Deprecated".equals(object)) {
                $Context.e |= 0x20000;
            } else if ("RuntimeVisibleAnnotations".equals(object)) {
                n6 = n + 8;
            } else if ("RuntimeVisibleTypeAnnotations".equals(object)) {
                n8 = n + 8;
            } else if ("AnnotationDefault".equals(object)) {
                n10 = n + 8;
            } else if ("Synthetic".equals(object)) {
                $Context.e |= 0x41000;
            } else if ("RuntimeInvisibleAnnotations".equals(object)) {
                n7 = n + 8;
            } else if ("RuntimeInvisibleTypeAnnotations".equals(object)) {
                n9 = n + 8;
            } else if ("RuntimeVisibleParameterAnnotations".equals(object)) {
                n11 = n + 8;
            } else if ("RuntimeInvisibleParameterAnnotations".equals(object)) {
                n12 = n + 8;
            } else if ("MethodParameters".equals(object)) {
                n5 = n + 8;
            } else {
                $Attribute $Attribute2 = this.a($Context.a, (String)object, n + 8, this.readInt(n + 4), cArray, -1, null);
                if ($Attribute2 != null) {
                    $Attribute2.a = $Attribute;
                    $Attribute = $Attribute2;
                }
            }
            n += 6 + this.readInt(n + 4);
        }
        n += 2;
        $MethodVisitor $MethodVisitor = $ClassVisitor.visitMethod($Context.e, $Context.f, $Context.g, string, stringArray);
        if ($MethodVisitor == null) {
            return n;
        }
        if ($MethodVisitor instanceof $MethodWriter) {
            object = ($MethodWriter)$MethodVisitor;
            if ((($MethodWriter)object).b.M == this && string == (($MethodWriter)object).g) {
                n2 = 0;
                if (stringArray == null) {
                    n2 = (($MethodWriter)object).j == 0 ? 1 : 0;
                } else if (stringArray.length == (($MethodWriter)object).j) {
                    n2 = 1;
                    for (int i = stringArray.length - 1; i >= 0; --i) {
                        if ((($MethodWriter)object).k[i] == this.readUnsignedShort(n4 -= 2)) continue;
                        n2 = 0;
                        break;
                    }
                }
                if (n2 != 0) {
                    (($MethodWriter)object).h = n13;
                    (($MethodWriter)object).i = n - n13;
                    return n;
                }
            }
        }
        if (n5 != 0) {
            int n14 = this.b[n5] & 0xFF;
            n2 = n5 + 1;
            while (n14 > 0) {
                $MethodVisitor.visitParameter(this.readUTF8(n2, cArray), this.readUnsignedShort(n2 + 2));
                --n14;
                n2 += 4;
            }
        }
        if (n10 != 0) {
            $AnnotationVisitor $AnnotationVisitor = $MethodVisitor.visitAnnotationDefault();
            this.a(n10, cArray, null, $AnnotationVisitor);
            if ($AnnotationVisitor != null) {
                $AnnotationVisitor.visitEnd();
            }
        }
        if (n6 != 0) {
            n2 = n6 + 2;
            for (int i = this.readUnsignedShort(n6); i > 0; --i) {
                n2 = this.a(n2 + 2, cArray, true, $MethodVisitor.visitAnnotation(this.readUTF8(n2, cArray), true));
            }
        }
        if (n7 != 0) {
            n2 = n7 + 2;
            for (int i = this.readUnsignedShort(n7); i > 0; --i) {
                n2 = this.a(n2 + 2, cArray, true, $MethodVisitor.visitAnnotation(this.readUTF8(n2, cArray), false));
            }
        }
        if (n8 != 0) {
            n2 = n8 + 2;
            for (int i = this.readUnsignedShort(n8); i > 0; --i) {
                n2 = this.a($Context, n2);
                n2 = this.a(n2 + 2, cArray, true, $MethodVisitor.visitTypeAnnotation($Context.i, $Context.j, this.readUTF8(n2, cArray), true));
            }
        }
        if (n9 != 0) {
            n2 = n9 + 2;
            for (int i = this.readUnsignedShort(n9); i > 0; --i) {
                n2 = this.a($Context, n2);
                n2 = this.a(n2 + 2, cArray, true, $MethodVisitor.visitTypeAnnotation($Context.i, $Context.j, this.readUTF8(n2, cArray), false));
            }
        }
        if (n11 != 0) {
            this.b($MethodVisitor, $Context, n11, true);
        }
        if (n12 != 0) {
            this.b($MethodVisitor, $Context, n12, false);
        }
        while ($Attribute != null) {
            $Attribute $Attribute3 = $Attribute.a;
            $Attribute.a = null;
            $MethodVisitor.visitAttribute($Attribute);
            $Attribute = $Attribute3;
        }
        if (n3 != 0) {
            $MethodVisitor.visitCode();
            this.a($MethodVisitor, $Context, n3);
        }
        $MethodVisitor.visitEnd();
        return n;
    }

    private void a($MethodVisitor $MethodVisitor, $Context $Context, int n) {
        int n2;
        int n3;
        Object object;
        int n4;
        int n5;
        byte[] byArray = this.b;
        char[] cArray = $Context.c;
        int n6 = this.readUnsignedShort(n);
        int n7 = this.readUnsignedShort(n + 2);
        int n8 = this.readInt(n + 4);
        int n9 = n += 8;
        int n10 = n + n8;
        $Context.h = new $Label[n8 + 2];
        $Label[] $LabelArray = $Context.h;
        this.readLabel(n8 + 1, $LabelArray);
        block29: while (n < n10) {
            n5 = n - n9;
            int n11 = byArray[n] & 0xFF;
            switch ($ClassWriter.a[n11]) {
                case 0: 
                case 4: {
                    ++n;
                    continue block29;
                }
                case 9: {
                    this.readLabel(n5 + this.readShort(n + 1), $LabelArray);
                    n += 3;
                    continue block29;
                }
                case 10: {
                    this.readLabel(n5 + this.readInt(n + 1), $LabelArray);
                    n += 5;
                    continue block29;
                }
                case 17: {
                    n11 = byArray[n + 1] & 0xFF;
                    if (n11 == 132) {
                        n += 6;
                        continue block29;
                    }
                    n += 4;
                    continue block29;
                }
                case 14: {
                    int n12;
                    n = n + 4 - (n5 & 3);
                    this.readLabel(n5 + this.readInt(n), $LabelArray);
                    for (n12 = this.readInt(n + 8) - this.readInt(n + 4) + 1; n12 > 0; --n12) {
                        this.readLabel(n5 + this.readInt(n + 12), $LabelArray);
                        n += 4;
                    }
                    n += 12;
                    continue block29;
                }
                case 15: {
                    int n12;
                    n = n + 4 - (n5 & 3);
                    this.readLabel(n5 + this.readInt(n), $LabelArray);
                    for (n12 = this.readInt(n + 4); n12 > 0; --n12) {
                        this.readLabel(n5 + this.readInt(n + 12), $LabelArray);
                        n += 8;
                    }
                    n += 8;
                    continue block29;
                }
                case 1: 
                case 3: 
                case 11: {
                    n += 2;
                    continue block29;
                }
                case 2: 
                case 5: 
                case 6: 
                case 12: 
                case 13: {
                    n += 3;
                    continue block29;
                }
                case 7: 
                case 8: {
                    n += 5;
                    continue block29;
                }
            }
            n += 4;
        }
        for (n5 = this.readUnsignedShort(n); n5 > 0; --n5) {
            $Label $Label = this.readLabel(this.readUnsignedShort(n + 2), $LabelArray);
            $Label $Label2 = this.readLabel(this.readUnsignedShort(n + 4), $LabelArray);
            $Label $Label3 = this.readLabel(this.readUnsignedShort(n + 6), $LabelArray);
            String string = this.readUTF8(this.a[this.readUnsignedShort(n + 8)], cArray);
            $MethodVisitor.visitTryCatchBlock($Label, $Label2, $Label3, string);
            n += 8;
        }
        n += 2;
        int[] nArray = null;
        int[] nArray2 = null;
        int n13 = 0;
        int n14 = 0;
        int n15 = -1;
        int n16 = -1;
        int n17 = 0;
        int n18 = 0;
        boolean bl = true;
        boolean bl2 = ($Context.b & 8) != 0;
        int n19 = 0;
        int n20 = 0;
        int n21 = 0;
        $Context $Context2 = null;
        $Attribute $Attribute = null;
        for (n4 = this.readUnsignedShort(n); n4 > 0; --n4) {
            int n22;
            object = this.readUTF8(n + 2, cArray);
            if ("LocalVariableTable".equals(object)) {
                if (($Context.b & 2) == 0) {
                    n17 = n + 8;
                    n3 = n;
                    for (n2 = this.readUnsignedShort(n + 8); n2 > 0; --n2) {
                        n22 = this.readUnsignedShort(n3 + 10);
                        if ($LabelArray[n22] == null) {
                            this.readLabel((int)n22, ($Label[])$LabelArray).a |= 1;
                        }
                        if ($LabelArray[n22 += this.readUnsignedShort(n3 + 12)] == null) {
                            this.readLabel((int)n22, ($Label[])$LabelArray).a |= 1;
                        }
                        n3 += 10;
                    }
                }
            } else if ("LocalVariableTypeTable".equals(object)) {
                n18 = n + 8;
            } else if ("LineNumberTable".equals(object)) {
                if (($Context.b & 2) == 0) {
                    n3 = n;
                    for (n2 = this.readUnsignedShort(n + 8); n2 > 0; --n2) {
                        n22 = this.readUnsignedShort(n3 + 10);
                        if ($LabelArray[n22] == null) {
                            this.readLabel((int)n22, ($Label[])$LabelArray).a |= 1;
                        }
                        $LabelArray[n22].b = this.readUnsignedShort(n3 + 12);
                        n3 += 4;
                    }
                }
            } else if ("RuntimeVisibleTypeAnnotations".equals(object)) {
                nArray = this.a($MethodVisitor, $Context, n + 8, true);
                n15 = nArray.length == 0 || this.readByte(nArray[0]) < 67 ? -1 : this.readUnsignedShort(nArray[0] + 1);
            } else if ("RuntimeInvisibleTypeAnnotations".equals(object)) {
                nArray2 = this.a($MethodVisitor, $Context, n + 8, false);
                n16 = nArray2.length == 0 || this.readByte(nArray2[0]) < 67 ? -1 : this.readUnsignedShort(nArray2[0] + 1);
            } else if ("StackMapTable".equals(object)) {
                if (($Context.b & 4) == 0) {
                    n19 = n + 10;
                    n20 = this.readInt(n + 4);
                    n21 = this.readUnsignedShort(n + 8);
                }
            } else if ("StackMap".equals(object)) {
                if (($Context.b & 4) == 0) {
                    bl = false;
                    n19 = n + 10;
                    n20 = this.readInt(n + 4);
                    n21 = this.readUnsignedShort(n + 8);
                }
            } else {
                for (n2 = 0; n2 < $Context.a.length; ++n2) {
                    $Attribute $Attribute2;
                    if (!$Context.a[n2].type.equals(object) || ($Attribute2 = $Context.a[n2].read(this, n + 8, this.readInt(n + 4), cArray, n9 - 8, $LabelArray)) == null) continue;
                    $Attribute2.a = $Attribute;
                    $Attribute = $Attribute2;
                }
            }
            n += 6 + this.readInt(n + 4);
        }
        n += 2;
        if (n19 != 0) {
            $Context2 = $Context;
            $Context2.o = -1;
            $Context2.p = 0;
            $Context2.q = 0;
            $Context2.r = 0;
            $Context2.t = 0;
            $Context2.s = new Object[n7];
            $Context2.u = new Object[n6];
            if (bl2) {
                this.a($Context);
            }
            for (n4 = n19; n4 < n19 + n20 - 2; ++n4) {
                int n23;
                if (byArray[n4] != 8 || (n23 = this.readUnsignedShort(n4 + 1)) < 0 || n23 >= n8 || (byArray[n9 + n23] & 0xFF) != 187) continue;
                this.readLabel(n23, $LabelArray);
            }
        }
        n = n9;
        while (n < n10) {
            n4 = n - n9;
            object = $LabelArray[n4];
            if (object != null) {
                $MethodVisitor.visitLabel(($Label)object);
                if (($Context.b & 2) == 0 && (($Label)object).b > 0) {
                    $MethodVisitor.visitLineNumber((($Label)object).b, ($Label)object);
                }
            }
            while ($Context2 != null && ($Context2.o == n4 || $Context2.o == -1)) {
                if ($Context2.o != -1) {
                    if (!bl || bl2) {
                        $MethodVisitor.visitFrame(-1, $Context2.q, $Context2.s, $Context2.t, $Context2.u);
                    } else {
                        $MethodVisitor.visitFrame($Context2.p, $Context2.r, $Context2.s, $Context2.t, $Context2.u);
                    }
                }
                if (n21 > 0) {
                    n19 = this.a(n19, bl, bl2, $Context2);
                    --n21;
                    continue;
                }
                $Context2 = null;
            }
            n2 = byArray[n] & 0xFF;
            switch ($ClassWriter.a[n2]) {
                case 0: {
                    $MethodVisitor.visitInsn(n2);
                    ++n;
                    break;
                }
                case 4: {
                    if (n2 > 54) {
                        $MethodVisitor.visitVarInsn(54 + ((n2 -= 59) >> 2), n2 & 3);
                    } else {
                        $MethodVisitor.visitVarInsn(21 + ((n2 -= 26) >> 2), n2 & 3);
                    }
                    ++n;
                    break;
                }
                case 9: {
                    $MethodVisitor.visitJumpInsn(n2, $LabelArray[n4 + this.readShort(n + 1)]);
                    n += 3;
                    break;
                }
                case 10: {
                    $MethodVisitor.visitJumpInsn(n2 - 33, $LabelArray[n4 + this.readInt(n + 1)]);
                    n += 5;
                    break;
                }
                case 17: {
                    n2 = byArray[n + 1] & 0xFF;
                    if (n2 == 132) {
                        $MethodVisitor.visitIincInsn(this.readUnsignedShort(n + 2), this.readShort(n + 4));
                        n += 6;
                        break;
                    }
                    $MethodVisitor.visitVarInsn(n2, this.readUnsignedShort(n + 2));
                    n += 4;
                    break;
                }
                case 14: {
                    n = n + 4 - (n4 & 3);
                    n3 = n4 + this.readInt(n);
                    int n24 = this.readInt(n + 4);
                    int n25 = this.readInt(n + 8);
                    $Label[] $LabelArray2 = new $Label[n25 - n24 + 1];
                    n += 12;
                    for (int i = 0; i < $LabelArray2.length; ++i) {
                        $LabelArray2[i] = $LabelArray[n4 + this.readInt(n)];
                        n += 4;
                    }
                    $MethodVisitor.visitTableSwitchInsn(n24, n25, $LabelArray[n3], $LabelArray2);
                    break;
                }
                case 15: {
                    n = n + 4 - (n4 & 3);
                    n3 = n4 + this.readInt(n);
                    int n26 = this.readInt(n + 4);
                    int[] nArray3 = new int[n26];
                    $Label[] $LabelArray3 = new $Label[n26];
                    n += 8;
                    for (int i = 0; i < n26; ++i) {
                        nArray3[i] = this.readInt(n);
                        $LabelArray3[i] = $LabelArray[n4 + this.readInt(n + 4)];
                        n += 8;
                    }
                    $MethodVisitor.visitLookupSwitchInsn($LabelArray[n3], nArray3, $LabelArray3);
                    break;
                }
                case 3: {
                    $MethodVisitor.visitVarInsn(n2, byArray[n + 1] & 0xFF);
                    n += 2;
                    break;
                }
                case 1: {
                    $MethodVisitor.visitIntInsn(n2, byArray[n + 1]);
                    n += 2;
                    break;
                }
                case 2: {
                    $MethodVisitor.visitIntInsn(n2, this.readShort(n + 1));
                    n += 3;
                    break;
                }
                case 11: {
                    $MethodVisitor.visitLdcInsn(this.readConst(byArray[n + 1] & 0xFF, cArray));
                    n += 2;
                    break;
                }
                case 12: {
                    $MethodVisitor.visitLdcInsn(this.readConst(this.readUnsignedShort(n + 1), cArray));
                    n += 3;
                    break;
                }
                case 6: 
                case 7: {
                    n3 = this.a[this.readUnsignedShort(n + 1)];
                    String string = this.readClass(n3, cArray);
                    n3 = this.a[this.readUnsignedShort(n3 + 2)];
                    String string2 = this.readUTF8(n3, cArray);
                    String string3 = this.readUTF8(n3 + 2, cArray);
                    if (n2 < 182) {
                        $MethodVisitor.visitFieldInsn(n2, string, string2, string3);
                    } else {
                        $MethodVisitor.visitMethodInsn(n2, string, string2, string3);
                    }
                    if (n2 == 185) {
                        n += 5;
                        break;
                    }
                    n += 3;
                    break;
                }
                case 8: {
                    n3 = this.a[this.readUnsignedShort(n + 1)];
                    int n27 = $Context.d[this.readUnsignedShort(n3)];
                    $Handle $Handle = ($Handle)this.readConst(this.readUnsignedShort(n27), cArray);
                    int n28 = this.readUnsignedShort(n27 + 2);
                    Object[] objectArray = new Object[n28];
                    n27 += 4;
                    for (int i = 0; i < n28; ++i) {
                        objectArray[i] = this.readConst(this.readUnsignedShort(n27), cArray);
                        n27 += 2;
                    }
                    n3 = this.a[this.readUnsignedShort(n3 + 2)];
                    String string = this.readUTF8(n3, cArray);
                    String string4 = this.readUTF8(n3 + 2, cArray);
                    $MethodVisitor.visitInvokeDynamicInsn(string, string4, $Handle, objectArray);
                    n += 5;
                    break;
                }
                case 5: {
                    $MethodVisitor.visitTypeInsn(n2, this.readClass(n + 1, cArray));
                    n += 3;
                    break;
                }
                case 13: {
                    $MethodVisitor.visitIincInsn(byArray[n + 1] & 0xFF, byArray[n + 2]);
                    n += 3;
                    break;
                }
                default: {
                    $MethodVisitor.visitMultiANewArrayInsn(this.readClass(n + 1, cArray), byArray[n + 3] & 0xFF);
                    n += 4;
                }
            }
            while (nArray != null && n13 < nArray.length && n15 <= n4) {
                if (n15 == n4) {
                    n3 = this.a($Context, nArray[n13]);
                    this.a(n3 + 2, cArray, true, $MethodVisitor.visitInsnAnnotation($Context.i, $Context.j, this.readUTF8(n3, cArray), true));
                }
                n15 = ++n13 >= nArray.length || this.readByte(nArray[n13]) < 67 ? -1 : this.readUnsignedShort(nArray[n13] + 1);
            }
            while (nArray2 != null && n14 < nArray2.length && n16 <= n4) {
                if (n16 == n4) {
                    n3 = this.a($Context, nArray2[n14]);
                    this.a(n3 + 2, cArray, true, $MethodVisitor.visitInsnAnnotation($Context.i, $Context.j, this.readUTF8(n3, cArray), false));
                }
                n16 = ++n14 >= nArray2.length || this.readByte(nArray2[n14]) < 67 ? -1 : this.readUnsignedShort(nArray2[n14] + 1);
            }
        }
        if ($LabelArray[n8] != null) {
            $MethodVisitor.visitLabel($LabelArray[n8]);
        }
        if (($Context.b & 2) == 0 && n17 != 0) {
            int[] nArray4 = null;
            if (n18 != 0) {
                n = n18 + 2;
                nArray4 = new int[this.readUnsignedShort(n18) * 3];
                int n29 = nArray4.length;
                while (n29 > 0) {
                    nArray4[--n29] = n + 6;
                    nArray4[--n29] = this.readUnsignedShort(n + 8);
                    nArray4[--n29] = this.readUnsignedShort(n);
                    n += 10;
                }
            }
            n = n17 + 2;
            for (int i = this.readUnsignedShort(n17); i > 0; --i) {
                n2 = this.readUnsignedShort(n);
                n3 = this.readUnsignedShort(n + 2);
                int n30 = this.readUnsignedShort(n + 8);
                String string = null;
                if (nArray4 != null) {
                    for (int j = 0; j < nArray4.length; j += 3) {
                        if (nArray4[j] != n2 || nArray4[j + 1] != n30) continue;
                        string = this.readUTF8(nArray4[j + 2], cArray);
                        break;
                    }
                }
                $MethodVisitor.visitLocalVariable(this.readUTF8(n + 4, cArray), this.readUTF8(n + 6, cArray), string, $LabelArray[n2], $LabelArray[n2 + n3], n30);
                n += 10;
            }
        }
        if (nArray != null) {
            for (int i = 0; i < nArray.length; ++i) {
                if (this.readByte((int)nArray[i]) >> 1 != 32) continue;
                int n31 = this.a($Context, nArray[i]);
                n31 = this.a(n31 + 2, cArray, true, $MethodVisitor.visitLocalVariableAnnotation($Context.i, $Context.j, $Context.l, $Context.m, $Context.n, this.readUTF8(n31, cArray), true));
            }
        }
        if (nArray2 != null) {
            for (int i = 0; i < nArray2.length; ++i) {
                if (this.readByte((int)nArray2[i]) >> 1 != 32) continue;
                int n32 = this.a($Context, nArray2[i]);
                n32 = this.a(n32 + 2, cArray, true, $MethodVisitor.visitLocalVariableAnnotation($Context.i, $Context.j, $Context.l, $Context.m, $Context.n, this.readUTF8(n32, cArray), false));
            }
        }
        while ($Attribute != null) {
            $Attribute $Attribute3 = $Attribute.a;
            $Attribute.a = null;
            $MethodVisitor.visitAttribute($Attribute);
            $Attribute = $Attribute3;
        }
        $MethodVisitor.visitMaxs(n6, n7);
    }

    private int[] a($MethodVisitor $MethodVisitor, $Context $Context, int n, boolean bl) {
        char[] cArray = $Context.c;
        int[] nArray = new int[this.readUnsignedShort(n)];
        n += 2;
        for (int i = 0; i < nArray.length; ++i) {
            int n2;
            nArray[i] = n;
            int n3 = this.readInt(n);
            switch (n3 >>> 24) {
                case 0: 
                case 1: 
                case 22: {
                    n += 2;
                    break;
                }
                case 19: 
                case 20: 
                case 21: {
                    ++n;
                    break;
                }
                case 64: 
                case 65: {
                    for (n2 = this.readUnsignedShort(n + 1); n2 > 0; --n2) {
                        int n4 = this.readUnsignedShort(n + 3);
                        int n5 = this.readUnsignedShort(n + 5);
                        this.readLabel(n4, $Context.h);
                        this.readLabel(n4 + n5, $Context.h);
                        n += 6;
                    }
                    n += 3;
                    break;
                }
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 75: {
                    n += 4;
                    break;
                }
                default: {
                    n += 3;
                }
            }
            n2 = this.readByte(n);
            if (n3 >>> 24 == 66) {
                $TypePath $TypePath = n2 == 0 ? null : new $TypePath(this.b, n);
                n += 1 + 2 * n2;
                n = this.a(n + 2, cArray, true, $MethodVisitor.visitTryCatchAnnotation(n3, $TypePath, this.readUTF8(n, cArray), bl));
                continue;
            }
            n = this.a(n + 3 + 2 * n2, cArray, true, null);
        }
        return nArray;
    }

    private int a($Context $Context, int n) {
        int n2;
        int n3 = this.readInt(n);
        switch (n3 >>> 24) {
            case 0: 
            case 1: 
            case 22: {
                n3 &= 0xFFFF0000;
                n += 2;
                break;
            }
            case 19: 
            case 20: 
            case 21: {
                n3 &= 0xFF000000;
                ++n;
                break;
            }
            case 64: 
            case 65: {
                n3 &= 0xFF000000;
                n2 = this.readUnsignedShort(n + 1);
                $Context.l = new $Label[n2];
                $Context.m = new $Label[n2];
                $Context.n = new int[n2];
                n += 3;
                for (int i = 0; i < n2; ++i) {
                    int n4 = this.readUnsignedShort(n);
                    int n5 = this.readUnsignedShort(n + 2);
                    $Context.l[i] = this.readLabel(n4, $Context.h);
                    $Context.m[i] = this.readLabel(n4 + n5, $Context.h);
                    $Context.n[i] = this.readUnsignedShort(n + 4);
                    n += 6;
                }
                break;
            }
            case 71: 
            case 72: 
            case 73: 
            case 74: 
            case 75: {
                n3 &= 0xFF0000FF;
                n += 4;
                break;
            }
            default: {
                n3 &= n3 >>> 24 < 67 ? -256 : -16777216;
                n += 3;
            }
        }
        n2 = this.readByte(n);
        $Context.i = n3;
        $Context.j = n2 == 0 ? null : new $TypePath(this.b, n);
        return n + 1 + 2 * n2;
    }

    private void b($MethodVisitor $MethodVisitor, $Context $Context, int n, boolean bl) {
        $AnnotationVisitor $AnnotationVisitor;
        int n2;
        int n3 = this.b[n++] & 0xFF;
        int n4 = $Type.getArgumentTypes($Context.g).length - n3;
        for (n2 = 0; n2 < n4; ++n2) {
            $AnnotationVisitor = $MethodVisitor.visitParameterAnnotation(n2, "Ljava/lang/Synthetic;", false);
            if ($AnnotationVisitor == null) continue;
            $AnnotationVisitor.visitEnd();
        }
        char[] cArray = $Context.c;
        while (n2 < n3 + n4) {
            int n5 = this.readUnsignedShort(n);
            n += 2;
            while (n5 > 0) {
                $AnnotationVisitor = $MethodVisitor.visitParameterAnnotation(n2, this.readUTF8(n, cArray), bl);
                n = this.a(n + 2, cArray, true, $AnnotationVisitor);
                --n5;
            }
            ++n2;
        }
    }

    private int a(int n, char[] cArray, boolean bl, $AnnotationVisitor $AnnotationVisitor) {
        int n2 = this.readUnsignedShort(n);
        n += 2;
        if (bl) {
            while (n2 > 0) {
                n = this.a(n + 2, cArray, this.readUTF8(n, cArray), $AnnotationVisitor);
                --n2;
            }
        } else {
            while (n2 > 0) {
                n = this.a(n, cArray, null, $AnnotationVisitor);
                --n2;
            }
        }
        if ($AnnotationVisitor != null) {
            $AnnotationVisitor.visitEnd();
        }
        return n;
    }

    private int a(int n, char[] cArray, String string, $AnnotationVisitor $AnnotationVisitor) {
        if ($AnnotationVisitor == null) {
            switch (this.b[n] & 0xFF) {
                case 101: {
                    return n + 5;
                }
                case 64: {
                    return this.a(n + 3, cArray, true, null);
                }
                case 91: {
                    return this.a(n + 1, cArray, false, null);
                }
            }
            return n + 3;
        }
        block5 : switch (this.b[n++] & 0xFF) {
            case 68: 
            case 70: 
            case 73: 
            case 74: {
                $AnnotationVisitor.visit(string, this.readConst(this.readUnsignedShort(n), cArray));
                n += 2;
                break;
            }
            case 66: {
                $AnnotationVisitor.visit(string, new Byte((byte)this.readInt(this.a[this.readUnsignedShort(n)])));
                n += 2;
                break;
            }
            case 90: {
                $AnnotationVisitor.visit(string, this.readInt(this.a[this.readUnsignedShort(n)]) == 0 ? Boolean.FALSE : Boolean.TRUE);
                n += 2;
                break;
            }
            case 83: {
                $AnnotationVisitor.visit(string, new Short((short)this.readInt(this.a[this.readUnsignedShort(n)])));
                n += 2;
                break;
            }
            case 67: {
                $AnnotationVisitor.visit(string, new Character((char)this.readInt(this.a[this.readUnsignedShort(n)])));
                n += 2;
                break;
            }
            case 115: {
                $AnnotationVisitor.visit(string, this.readUTF8(n, cArray));
                n += 2;
                break;
            }
            case 101: {
                $AnnotationVisitor.visitEnum(string, this.readUTF8(n, cArray), this.readUTF8(n + 2, cArray));
                n += 4;
                break;
            }
            case 99: {
                $AnnotationVisitor.visit(string, $Type.getType(this.readUTF8(n, cArray)));
                n += 2;
                break;
            }
            case 64: {
                n = this.a(n + 2, cArray, true, $AnnotationVisitor.visitAnnotation(string, this.readUTF8(n, cArray)));
                break;
            }
            case 91: {
                int n2 = this.readUnsignedShort(n);
                n += 2;
                if (n2 == 0) {
                    return this.a(n - 2, cArray, false, $AnnotationVisitor.visitArray(string));
                }
                switch (this.b[n++] & 0xFF) {
                    case 66: {
                        byte[] byArray = new byte[n2];
                        for (int i = 0; i < n2; ++i) {
                            byArray[i] = (byte)this.readInt(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        $AnnotationVisitor.visit(string, byArray);
                        --n;
                        break block5;
                    }
                    case 90: {
                        boolean[] blArray = new boolean[n2];
                        for (int i = 0; i < n2; ++i) {
                            blArray[i] = this.readInt(this.a[this.readUnsignedShort(n)]) != 0;
                            n += 3;
                        }
                        $AnnotationVisitor.visit(string, blArray);
                        --n;
                        break block5;
                    }
                    case 83: {
                        short[] sArray = new short[n2];
                        for (int i = 0; i < n2; ++i) {
                            sArray[i] = (short)this.readInt(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        $AnnotationVisitor.visit(string, sArray);
                        --n;
                        break block5;
                    }
                    case 67: {
                        char[] cArray2 = new char[n2];
                        for (int i = 0; i < n2; ++i) {
                            cArray2[i] = (char)this.readInt(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        $AnnotationVisitor.visit(string, cArray2);
                        --n;
                        break block5;
                    }
                    case 73: {
                        int[] nArray = new int[n2];
                        for (int i = 0; i < n2; ++i) {
                            nArray[i] = this.readInt(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        $AnnotationVisitor.visit(string, nArray);
                        --n;
                        break block5;
                    }
                    case 74: {
                        long[] lArray = new long[n2];
                        for (int i = 0; i < n2; ++i) {
                            lArray[i] = this.readLong(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        $AnnotationVisitor.visit(string, lArray);
                        --n;
                        break block5;
                    }
                    case 70: {
                        float[] fArray = new float[n2];
                        for (int i = 0; i < n2; ++i) {
                            fArray[i] = Float.intBitsToFloat(this.readInt(this.a[this.readUnsignedShort(n)]));
                            n += 3;
                        }
                        $AnnotationVisitor.visit(string, fArray);
                        --n;
                        break block5;
                    }
                    case 68: {
                        double[] dArray = new double[n2];
                        for (int i = 0; i < n2; ++i) {
                            dArray[i] = Double.longBitsToDouble(this.readLong(this.a[this.readUnsignedShort(n)]));
                            n += 3;
                        }
                        $AnnotationVisitor.visit(string, dArray);
                        --n;
                        break block5;
                    }
                }
                n = this.a(n - 3, cArray, false, $AnnotationVisitor.visitArray(string));
            }
        }
        return n;
    }

    private void a($Context $Context) {
        String string = $Context.g;
        Object[] objectArray = $Context.s;
        int n = 0;
        if (($Context.e & 8) == 0) {
            objectArray[n++] = "<init>".equals($Context.f) ? $Opcodes.UNINITIALIZED_THIS : this.readClass(this.header + 2, $Context.c);
        }
        int n2 = 1;
        block8: while (true) {
            int n3 = n2;
            switch (string.charAt(n2++)) {
                case 'B': 
                case 'C': 
                case 'I': 
                case 'S': 
                case 'Z': {
                    objectArray[n++] = $Opcodes.INTEGER;
                    continue block8;
                }
                case 'F': {
                    objectArray[n++] = $Opcodes.FLOAT;
                    continue block8;
                }
                case 'J': {
                    objectArray[n++] = $Opcodes.LONG;
                    continue block8;
                }
                case 'D': {
                    objectArray[n++] = $Opcodes.DOUBLE;
                    continue block8;
                }
                case '[': {
                    while (string.charAt(n2) == '[') {
                        ++n2;
                    }
                    if (string.charAt(n2) == 'L') {
                        ++n2;
                        while (string.charAt(n2) != ';') {
                            ++n2;
                        }
                    }
                    objectArray[n++] = string.substring(n3, ++n2);
                    continue block8;
                }
                case 'L': {
                    while (string.charAt(n2) != ';') {
                        ++n2;
                    }
                    objectArray[n++] = string.substring(n3 + 1, n2++);
                    continue block8;
                }
            }
            break;
        }
        $Context.q = n;
    }

    private int a(int n, boolean bl, boolean bl2, $Context $Context) {
        int n2;
        int n3;
        char[] cArray = $Context.c;
        $Label[] $LabelArray = $Context.h;
        if (bl) {
            n3 = this.b[n++] & 0xFF;
        } else {
            n3 = 255;
            $Context.o = -1;
        }
        $Context.r = 0;
        if (n3 < 64) {
            n2 = n3;
            $Context.p = 3;
            $Context.t = 0;
        } else if (n3 < 128) {
            n2 = n3 - 64;
            n = this.a($Context.u, 0, n, cArray, $LabelArray);
            $Context.p = 4;
            $Context.t = 1;
        } else {
            n2 = this.readUnsignedShort(n);
            n += 2;
            if (n3 == 247) {
                n = this.a($Context.u, 0, n, cArray, $LabelArray);
                $Context.p = 4;
                $Context.t = 1;
            } else if (n3 >= 248 && n3 < 251) {
                $Context.p = 2;
                $Context.r = 251 - n3;
                $Context.q -= $Context.r;
                $Context.t = 0;
            } else if (n3 == 251) {
                $Context.p = 3;
                $Context.t = 0;
            } else if (n3 < 255) {
                int n4 = bl2 ? $Context.q : 0;
                for (int i = n3 - 251; i > 0; --i) {
                    n = this.a($Context.s, n4++, n, cArray, $LabelArray);
                }
                $Context.p = 1;
                $Context.r = n3 - 251;
                $Context.q += $Context.r;
                $Context.t = 0;
            } else {
                $Context.p = 0;
                int n5 = this.readUnsignedShort(n);
                n += 2;
                $Context.r = n5;
                $Context.q = n5;
                int n6 = 0;
                while (n5 > 0) {
                    n = this.a($Context.s, n6++, n, cArray, $LabelArray);
                    --n5;
                }
                n5 = this.readUnsignedShort(n);
                n += 2;
                $Context.t = n5;
                n6 = 0;
                while (n5 > 0) {
                    n = this.a($Context.u, n6++, n, cArray, $LabelArray);
                    --n5;
                }
            }
        }
        $Context.o += n2 + 1;
        this.readLabel($Context.o, $LabelArray);
        return n;
    }

    private int a(Object[] objectArray, int n, int n2, char[] cArray, $Label[] $LabelArray) {
        int n3 = this.b[n2++] & 0xFF;
        switch (n3) {
            case 0: {
                objectArray[n] = $Opcodes.TOP;
                break;
            }
            case 1: {
                objectArray[n] = $Opcodes.INTEGER;
                break;
            }
            case 2: {
                objectArray[n] = $Opcodes.FLOAT;
                break;
            }
            case 3: {
                objectArray[n] = $Opcodes.DOUBLE;
                break;
            }
            case 4: {
                objectArray[n] = $Opcodes.LONG;
                break;
            }
            case 5: {
                objectArray[n] = $Opcodes.NULL;
                break;
            }
            case 6: {
                objectArray[n] = $Opcodes.UNINITIALIZED_THIS;
                break;
            }
            case 7: {
                objectArray[n] = this.readClass(n2, cArray);
                n2 += 2;
                break;
            }
            default: {
                objectArray[n] = this.readLabel(this.readUnsignedShort(n2), $LabelArray);
                n2 += 2;
            }
        }
        return n2;
    }

    protected $Label readLabel(int n, $Label[] $LabelArray) {
        if ($LabelArray[n] == null) {
            $LabelArray[n] = new $Label();
        }
        return $LabelArray[n];
    }

    private int a() {
        int n;
        int n2;
        int n3 = this.header + 8 + this.readUnsignedShort(this.header + 6) * 2;
        for (n2 = this.readUnsignedShort(n3); n2 > 0; --n2) {
            for (n = this.readUnsignedShort(n3 + 8); n > 0; --n) {
                n3 += 6 + this.readInt(n3 + 12);
            }
            n3 += 8;
        }
        for (n2 = this.readUnsignedShort(n3 += 2); n2 > 0; --n2) {
            for (n = this.readUnsignedShort(n3 + 8); n > 0; --n) {
                n3 += 6 + this.readInt(n3 + 12);
            }
            n3 += 8;
        }
        return n3 + 2;
    }

    private $Attribute a($Attribute[] $AttributeArray, String string, int n, int n2, char[] cArray, int n3, $Label[] $LabelArray) {
        for (int i = 0; i < $AttributeArray.length; ++i) {
            if (!$AttributeArray[i].type.equals(string)) continue;
            return $AttributeArray[i].read(this, n, n2, cArray, n3, $LabelArray);
        }
        return new $Attribute(string).read(this, n, n2, null, -1, null);
    }

    public int getItemCount() {
        return this.a.length;
    }

    public int getItem(int n) {
        return this.a[n];
    }

    public int getMaxStringLength() {
        return this.d;
    }

    public int readByte(int n) {
        return this.b[n] & 0xFF;
    }

    public int readUnsignedShort(int n) {
        byte[] byArray = this.b;
        return (byArray[n] & 0xFF) << 8 | byArray[n + 1] & 0xFF;
    }

    public short readShort(int n) {
        byte[] byArray = this.b;
        return (short)((byArray[n] & 0xFF) << 8 | byArray[n + 1] & 0xFF);
    }

    public int readInt(int n) {
        byte[] byArray = this.b;
        return (byArray[n] & 0xFF) << 24 | (byArray[n + 1] & 0xFF) << 16 | (byArray[n + 2] & 0xFF) << 8 | byArray[n + 3] & 0xFF;
    }

    public long readLong(int n) {
        long l = this.readInt(n);
        long l2 = (long)this.readInt(n + 4) & 0xFFFFFFFFL;
        return l << 32 | l2;
    }

    public String readUTF8(int n, char[] cArray) {
        int n2 = this.readUnsignedShort(n);
        if (n == 0 || n2 == 0) {
            return null;
        }
        String string = this.c[n2];
        if (string != null) {
            return string;
        }
        n = this.a[n2];
        this.c[n2] = this.a(n + 2, this.readUnsignedShort(n), cArray);
        return this.c[n2];
    }

    private String a(int n, int n2, char[] cArray) {
        int n3 = n + n2;
        byte[] byArray = this.b;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        while (n < n3) {
            int n7 = byArray[n++];
            switch (n5) {
                case 0: {
                    if ((n7 &= 0xFF) < 128) {
                        cArray[n4++] = (char)n7;
                        break;
                    }
                    if (n7 < 224 && n7 > 191) {
                        n6 = (char)(n7 & 0x1F);
                        n5 = 1;
                        break;
                    }
                    n6 = (char)(n7 & 0xF);
                    n5 = 2;
                    break;
                }
                case 1: {
                    cArray[n4++] = (char)(n6 << 6 | n7 & 0x3F);
                    n5 = 0;
                    break;
                }
                case 2: {
                    n6 = (char)(n6 << 6 | n7 & 0x3F);
                    n5 = 1;
                }
            }
        }
        return new String(cArray, 0, n4);
    }

    public String readClass(int n, char[] cArray) {
        return this.readUTF8(this.a[this.readUnsignedShort(n)], cArray);
    }

    public Object readConst(int n, char[] cArray) {
        int n2 = this.a[n];
        switch (this.b[n2 - 1]) {
            case 3: {
                return new Integer(this.readInt(n2));
            }
            case 4: {
                return new Float(Float.intBitsToFloat(this.readInt(n2)));
            }
            case 5: {
                return new Long(this.readLong(n2));
            }
            case 6: {
                return new Double(Double.longBitsToDouble(this.readLong(n2)));
            }
            case 7: {
                return $Type.getObjectType(this.readUTF8(n2, cArray));
            }
            case 8: {
                return this.readUTF8(n2, cArray);
            }
            case 16: {
                return $Type.getMethodType(this.readUTF8(n2, cArray));
            }
        }
        int n3 = this.readByte(n2);
        int[] nArray = this.a;
        int n4 = nArray[this.readUnsignedShort(n2 + 1)];
        String string = this.readClass(n4, cArray);
        n4 = nArray[this.readUnsignedShort(n4 + 2)];
        String string2 = this.readUTF8(n4, cArray);
        String string3 = this.readUTF8(n4 + 2, cArray);
        return new $Handle(n3, string, string2, string3);
    }
}

