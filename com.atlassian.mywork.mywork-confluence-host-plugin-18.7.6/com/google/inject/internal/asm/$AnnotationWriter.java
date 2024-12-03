/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import com.google.inject.internal.asm.$AnnotationVisitor;
import com.google.inject.internal.asm.$ByteVector;
import com.google.inject.internal.asm.$ClassWriter;
import com.google.inject.internal.asm.$Item;
import com.google.inject.internal.asm.$Type;

final class $AnnotationWriter
implements $AnnotationVisitor {
    private final $ClassWriter a;
    private int b;
    private final boolean c;
    private final $ByteVector d;
    private final $ByteVector e;
    private final int f;
    $AnnotationWriter g;
    $AnnotationWriter h;

    $AnnotationWriter($ClassWriter classWriter, boolean bl, $ByteVector byteVector, $ByteVector byteVector2, int n) {
        this.a = classWriter;
        this.c = bl;
        this.d = byteVector;
        this.e = byteVector2;
        this.f = n;
    }

    public void visit(String string, Object object) {
        ++this.b;
        if (this.c) {
            this.d.putShort(this.a.newUTF8(string));
        }
        if (object instanceof String) {
            this.d.b(115, this.a.newUTF8((String)object));
        } else if (object instanceof Byte) {
            this.d.b(66, this.a.a((int)((Byte)object).byteValue()).a);
        } else if (object instanceof Boolean) {
            int n = (Boolean)object != false ? 1 : 0;
            this.d.b(90, this.a.a((int)n).a);
        } else if (object instanceof Character) {
            this.d.b(67, this.a.a((int)((Character)object).charValue()).a);
        } else if (object instanceof Short) {
            this.d.b(83, this.a.a((int)((Short)object).shortValue()).a);
        } else if (object instanceof $Type) {
            this.d.b(99, this.a.newUTF8((($Type)object).getDescriptor()));
        } else if (object instanceof byte[]) {
            byte[] byArray = (byte[])object;
            this.d.b(91, byArray.length);
            for (int i = 0; i < byArray.length; ++i) {
                this.d.b(66, this.a.a((int)byArray[i]).a);
            }
        } else if (object instanceof boolean[]) {
            boolean[] blArray = (boolean[])object;
            this.d.b(91, blArray.length);
            for (int i = 0; i < blArray.length; ++i) {
                this.d.b(90, this.a.a((int)(blArray[i] ? 1 : 0)).a);
            }
        } else if (object instanceof short[]) {
            short[] sArray = (short[])object;
            this.d.b(91, sArray.length);
            for (int i = 0; i < sArray.length; ++i) {
                this.d.b(83, this.a.a((int)sArray[i]).a);
            }
        } else if (object instanceof char[]) {
            char[] cArray = (char[])object;
            this.d.b(91, cArray.length);
            for (int i = 0; i < cArray.length; ++i) {
                this.d.b(67, this.a.a((int)cArray[i]).a);
            }
        } else if (object instanceof int[]) {
            int[] nArray = (int[])object;
            this.d.b(91, nArray.length);
            for (int i = 0; i < nArray.length; ++i) {
                this.d.b(73, this.a.a((int)nArray[i]).a);
            }
        } else if (object instanceof long[]) {
            long[] lArray = (long[])object;
            this.d.b(91, lArray.length);
            for (int i = 0; i < lArray.length; ++i) {
                this.d.b(74, this.a.a((long)lArray[i]).a);
            }
        } else if (object instanceof float[]) {
            float[] fArray = (float[])object;
            this.d.b(91, fArray.length);
            for (int i = 0; i < fArray.length; ++i) {
                this.d.b(70, this.a.a((float)fArray[i]).a);
            }
        } else if (object instanceof double[]) {
            double[] dArray = (double[])object;
            this.d.b(91, dArray.length);
            for (int i = 0; i < dArray.length; ++i) {
                this.d.b(68, this.a.a((double)dArray[i]).a);
            }
        } else {
            $Item $Item = this.a.a(object);
            this.d.b(".s.IFJDCS".charAt($Item.b), $Item.a);
        }
    }

    public void visitEnum(String string, String string2, String string3) {
        ++this.b;
        if (this.c) {
            this.d.putShort(this.a.newUTF8(string));
        }
        this.d.b(101, this.a.newUTF8(string2)).putShort(this.a.newUTF8(string3));
    }

    public $AnnotationVisitor visitAnnotation(String string, String string2) {
        ++this.b;
        if (this.c) {
            this.d.putShort(this.a.newUTF8(string));
        }
        this.d.b(64, this.a.newUTF8(string2)).putShort(0);
        return new $AnnotationWriter(this.a, true, this.d, this.d, this.d.b - 2);
    }

    public $AnnotationVisitor visitArray(String string) {
        ++this.b;
        if (this.c) {
            this.d.putShort(this.a.newUTF8(string));
        }
        this.d.b(91, 0);
        return new $AnnotationWriter(this.a, false, this.d, this.d, this.d.b - 2);
    }

    public void visitEnd() {
        if (this.e != null) {
            byte[] byArray = this.e.a;
            byArray[this.f] = (byte)(this.b >>> 8);
            byArray[this.f + 1] = (byte)this.b;
        }
    }

    int a() {
        int n = 0;
        $AnnotationWriter $AnnotationWriter = this;
        while ($AnnotationWriter != null) {
            n += $AnnotationWriter.d.b;
            $AnnotationWriter = $AnnotationWriter.g;
        }
        return n;
    }

    void a($ByteVector $ByteVector) {
        int n = 0;
        int n2 = 2;
        $AnnotationWriter $AnnotationWriter = this;
        $AnnotationWriter $AnnotationWriter2 = null;
        while ($AnnotationWriter != null) {
            ++n;
            n2 += $AnnotationWriter.d.b;
            $AnnotationWriter.visitEnd();
            $AnnotationWriter.h = $AnnotationWriter2;
            $AnnotationWriter2 = $AnnotationWriter;
            $AnnotationWriter = $AnnotationWriter.g;
        }
        $ByteVector.putInt(n2);
        $ByteVector.putShort(n);
        $AnnotationWriter = $AnnotationWriter2;
        while ($AnnotationWriter != null) {
            $ByteVector.putByteArray($AnnotationWriter.d.a, 0, $AnnotationWriter.d.b);
            $AnnotationWriter = $AnnotationWriter.h;
        }
    }

    static void a($AnnotationWriter[] $AnnotationWriterArray, int n, $ByteVector $ByteVector) {
        int n2;
        int n3 = 1 + 2 * ($AnnotationWriterArray.length - n);
        for (n2 = n; n2 < $AnnotationWriterArray.length; ++n2) {
            n3 += $AnnotationWriterArray[n2] == null ? 0 : $AnnotationWriterArray[n2].a();
        }
        $ByteVector.putInt(n3).putByte($AnnotationWriterArray.length - n);
        for (n2 = n; n2 < $AnnotationWriterArray.length; ++n2) {
            $AnnotationWriter $AnnotationWriter = $AnnotationWriterArray[n2];
            $AnnotationWriter $AnnotationWriter2 = null;
            int n4 = 0;
            while ($AnnotationWriter != null) {
                ++n4;
                $AnnotationWriter.visitEnd();
                $AnnotationWriter.h = $AnnotationWriter2;
                $AnnotationWriter2 = $AnnotationWriter;
                $AnnotationWriter = $AnnotationWriter.g;
            }
            $ByteVector.putShort(n4);
            $AnnotationWriter = $AnnotationWriter2;
            while ($AnnotationWriter != null) {
                $ByteVector.putByteArray($AnnotationWriter.d.a, 0, $AnnotationWriter.d.b);
                $AnnotationWriter = $AnnotationWriter.h;
            }
        }
    }
}

