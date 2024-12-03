/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import com.google.inject.internal.asm.$AnnotationVisitor;
import com.google.inject.internal.asm.$AnnotationWriter;
import com.google.inject.internal.asm.$Attribute;
import com.google.inject.internal.asm.$ByteVector;
import com.google.inject.internal.asm.$ClassWriter;
import com.google.inject.internal.asm.$Edge;
import com.google.inject.internal.asm.$Frame;
import com.google.inject.internal.asm.$Handle;
import com.google.inject.internal.asm.$Handler;
import com.google.inject.internal.asm.$Item;
import com.google.inject.internal.asm.$Label;
import com.google.inject.internal.asm.$MethodVisitor;
import com.google.inject.internal.asm.$Type;
import com.google.inject.internal.asm.$TypePath;

class $MethodWriter
extends $MethodVisitor {
    final $ClassWriter b;
    private int c;
    private final int d;
    private final int e;
    private final String f;
    String g;
    int h;
    int i;
    int j;
    int[] k;
    private $ByteVector l;
    private $AnnotationWriter m;
    private $AnnotationWriter n;
    private $AnnotationWriter U;
    private $AnnotationWriter V;
    private $AnnotationWriter[] o;
    private $AnnotationWriter[] p;
    private int S;
    private $Attribute q;
    private $ByteVector r = new $ByteVector();
    private int s;
    private int t;
    private int T;
    private int u;
    private $ByteVector v;
    private int w;
    private int[] x;
    private int[] z;
    private int A;
    private $Handler B;
    private $Handler C;
    private int Z;
    private $ByteVector $;
    private int D;
    private $ByteVector E;
    private int F;
    private $ByteVector G;
    private int H;
    private $ByteVector I;
    private int Y;
    private $AnnotationWriter W;
    private $AnnotationWriter X;
    private $Attribute J;
    private boolean K;
    private int L;
    private final int M;
    private $Label N;
    private $Label O;
    private $Label P;
    private int Q;
    private int R;

    $MethodWriter($ClassWriter $ClassWriter, int n, String string, String string2, String string3, String[] stringArray, boolean bl, boolean bl2) {
        super(327680);
        int n2;
        if ($ClassWriter.D == null) {
            $ClassWriter.D = this;
        } else {
            $ClassWriter.E.mv = this;
        }
        $ClassWriter.E = this;
        this.b = $ClassWriter;
        this.c = n;
        if ("<init>".equals(string)) {
            this.c |= 0x80000;
        }
        this.d = $ClassWriter.newUTF8(string);
        this.e = $ClassWriter.newUTF8(string2);
        this.f = string2;
        this.g = string3;
        if (stringArray != null && stringArray.length > 0) {
            this.j = stringArray.length;
            this.k = new int[this.j];
            for (n2 = 0; n2 < this.j; ++n2) {
                this.k[n2] = $ClassWriter.newClass(stringArray[n2]);
            }
        }
        int n3 = bl2 ? 0 : (this.M = bl ? 1 : 2);
        if (bl || bl2) {
            n2 = $Type.getArgumentsAndReturnSizes(this.f) >> 2;
            if ((n & 8) != 0) {
                --n2;
            }
            this.t = n2;
            this.T = n2;
            this.N = new $Label();
            this.N.a |= 8;
            this.visitLabel(this.N);
        }
    }

    public void visitParameter(String string, int n) {
        if (this.$ == null) {
            this.$ = new $ByteVector();
        }
        ++this.Z;
        this.$.putShort(string == null ? 0 : this.b.newUTF8(string)).putShort(n);
    }

    public $AnnotationVisitor visitAnnotationDefault() {
        this.l = new $ByteVector();
        return new $AnnotationWriter(this.b, false, this.l, null, 0);
    }

    public $AnnotationVisitor visitAnnotation(String string, boolean bl) {
        $ByteVector $ByteVector = new $ByteVector();
        $ByteVector.putShort(this.b.newUTF8(string)).putShort(0);
        $AnnotationWriter $AnnotationWriter = new $AnnotationWriter(this.b, true, $ByteVector, $ByteVector, 2);
        if (bl) {
            $AnnotationWriter.g = this.m;
            this.m = $AnnotationWriter;
        } else {
            $AnnotationWriter.g = this.n;
            this.n = $AnnotationWriter;
        }
        return $AnnotationWriter;
    }

    public $AnnotationVisitor visitTypeAnnotation(int n, $TypePath $TypePath, String string, boolean bl) {
        $ByteVector $ByteVector = new $ByteVector();
        $AnnotationWriter.a(n, $TypePath, $ByteVector);
        $ByteVector.putShort(this.b.newUTF8(string)).putShort(0);
        $AnnotationWriter $AnnotationWriter = new $AnnotationWriter(this.b, true, $ByteVector, $ByteVector, $ByteVector.b - 2);
        if (bl) {
            $AnnotationWriter.g = this.U;
            this.U = $AnnotationWriter;
        } else {
            $AnnotationWriter.g = this.V;
            this.V = $AnnotationWriter;
        }
        return $AnnotationWriter;
    }

    public $AnnotationVisitor visitParameterAnnotation(int n, String string, boolean bl) {
        $ByteVector $ByteVector = new $ByteVector();
        if ("Ljava/lang/Synthetic;".equals(string)) {
            this.S = Math.max(this.S, n + 1);
            return new $AnnotationWriter(this.b, false, $ByteVector, null, 0);
        }
        $ByteVector.putShort(this.b.newUTF8(string)).putShort(0);
        $AnnotationWriter $AnnotationWriter = new $AnnotationWriter(this.b, true, $ByteVector, $ByteVector, 2);
        if (bl) {
            if (this.o == null) {
                this.o = new $AnnotationWriter[$Type.getArgumentTypes(this.f).length];
            }
            $AnnotationWriter.g = this.o[n];
            this.o[n] = $AnnotationWriter;
        } else {
            if (this.p == null) {
                this.p = new $AnnotationWriter[$Type.getArgumentTypes(this.f).length];
            }
            $AnnotationWriter.g = this.p[n];
            this.p[n] = $AnnotationWriter;
        }
        return $AnnotationWriter;
    }

    public void visitAttribute($Attribute $Attribute) {
        if ($Attribute.isCodeAttribute()) {
            $Attribute.a = this.J;
            this.J = $Attribute;
        } else {
            $Attribute.a = this.q;
            this.q = $Attribute;
        }
    }

    public void visitCode() {
    }

    public void visitFrame(int n, int n2, Object[] objectArray, int n3, Object[] objectArray2) {
        if (this.M == 0) {
            return;
        }
        if (n == -1) {
            int n4;
            if (this.x == null) {
                this.f();
            }
            this.T = n2;
            int n5 = this.a(this.r.b, n2, n3);
            for (n4 = 0; n4 < n2; ++n4) {
                this.z[n5++] = objectArray[n4] instanceof String ? 0x1700000 | this.b.c((String)objectArray[n4]) : (objectArray[n4] instanceof Integer ? (Integer)objectArray[n4] : 0x1800000 | this.b.a("", (($Label)objectArray[n4]).c));
            }
            for (n4 = 0; n4 < n3; ++n4) {
                this.z[n5++] = objectArray2[n4] instanceof String ? 0x1700000 | this.b.c((String)objectArray2[n4]) : (objectArray2[n4] instanceof Integer ? (Integer)objectArray2[n4] : 0x1800000 | this.b.a("", (($Label)objectArray2[n4]).c));
            }
            this.b();
        } else {
            int n6;
            if (this.v == null) {
                this.v = new $ByteVector();
                n6 = this.r.b;
            } else {
                n6 = this.r.b - this.w - 1;
                if (n6 < 0) {
                    if (n == 3) {
                        return;
                    }
                    throw new IllegalStateException();
                }
            }
            switch (n) {
                case 0: {
                    int n7;
                    this.T = n2;
                    this.v.putByte(255).putShort(n6).putShort(n2);
                    for (n7 = 0; n7 < n2; ++n7) {
                        this.a(objectArray[n7]);
                    }
                    this.v.putShort(n3);
                    for (n7 = 0; n7 < n3; ++n7) {
                        this.a(objectArray2[n7]);
                    }
                    break;
                }
                case 1: {
                    this.T += n2;
                    this.v.putByte(251 + n2).putShort(n6);
                    for (int i = 0; i < n2; ++i) {
                        this.a(objectArray[i]);
                    }
                    break;
                }
                case 2: {
                    this.T -= n2;
                    this.v.putByte(251 - n2).putShort(n6);
                    break;
                }
                case 3: {
                    if (n6 < 64) {
                        this.v.putByte(n6);
                        break;
                    }
                    this.v.putByte(251).putShort(n6);
                    break;
                }
                case 4: {
                    if (n6 < 64) {
                        this.v.putByte(64 + n6);
                    } else {
                        this.v.putByte(247).putShort(n6);
                    }
                    this.a(objectArray2[0]);
                }
            }
            this.w = this.r.b;
            ++this.u;
        }
        this.s = Math.max(this.s, n3);
        this.t = Math.max(this.t, this.T);
    }

    public void visitInsn(int n) {
        this.Y = this.r.b;
        this.r.putByte(n);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, null, null);
            } else {
                int n2 = this.Q + $Frame.a[n];
                if (n2 > this.R) {
                    this.R = n2;
                }
                this.Q = n2;
            }
            if (n >= 172 && n <= 177 || n == 191) {
                this.e();
            }
        }
    }

    public void visitIntInsn(int n, int n2) {
        this.Y = this.r.b;
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, n2, null, null);
            } else if (n != 188) {
                int n3 = this.Q + 1;
                if (n3 > this.R) {
                    this.R = n3;
                }
                this.Q = n3;
            }
        }
        if (n == 17) {
            this.r.b(n, n2);
        } else {
            this.r.a(n, n2);
        }
    }

    public void visitVarInsn(int n, int n2) {
        int n3;
        this.Y = this.r.b;
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, n2, null, null);
            } else if (n == 169) {
                this.P.a |= 0x100;
                this.P.f = this.Q;
                this.e();
            } else {
                n3 = this.Q + $Frame.a[n];
                if (n3 > this.R) {
                    this.R = n3;
                }
                this.Q = n3;
            }
        }
        if (this.M != 2 && (n3 = n == 22 || n == 24 || n == 55 || n == 57 ? n2 + 2 : n2 + 1) > this.t) {
            this.t = n3;
        }
        if (n2 < 4 && n != 169) {
            n3 = n < 54 ? 26 + (n - 21 << 2) + n2 : 59 + (n - 54 << 2) + n2;
            this.r.putByte(n3);
        } else if (n2 >= 256) {
            this.r.putByte(196).b(n, n2);
        } else {
            this.r.a(n, n2);
        }
        if (n >= 54 && this.M == 0 && this.A > 0) {
            this.visitLabel(new $Label());
        }
    }

    public void visitTypeInsn(int n, String string) {
        this.Y = this.r.b;
        $Item $Item = this.b.a(string);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, this.r.b, this.b, $Item);
            } else if (n == 187) {
                int n2 = this.Q + 1;
                if (n2 > this.R) {
                    this.R = n2;
                }
                this.Q = n2;
            }
        }
        this.r.b(n, $Item.a);
    }

    public void visitFieldInsn(int n, String string, String string2, String string3) {
        this.Y = this.r.b;
        $Item $Item = this.b.a(string, string2, string3);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, this.b, $Item);
            } else {
                int n2;
                char c = string3.charAt(0);
                switch (n) {
                    case 178: {
                        n2 = this.Q + (c == 'D' || c == 'J' ? 2 : 1);
                        break;
                    }
                    case 179: {
                        n2 = this.Q + (c == 'D' || c == 'J' ? -2 : -1);
                        break;
                    }
                    case 180: {
                        n2 = this.Q + (c == 'D' || c == 'J' ? 1 : 0);
                        break;
                    }
                    default: {
                        n2 = this.Q + (c == 'D' || c == 'J' ? -3 : -2);
                    }
                }
                if (n2 > this.R) {
                    this.R = n2;
                }
                this.Q = n2;
            }
        }
        this.r.b(n, $Item.a);
    }

    public void visitMethodInsn(int n, String string, String string2, String string3) {
        this.Y = this.r.b;
        boolean bl = n == 185;
        $Item $Item = this.b.a(string, string2, string3, bl);
        int n2 = $Item.c;
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, this.b, $Item);
            } else {
                int n3;
                if (n2 == 0) {
                    $Item.c = n2 = $Type.getArgumentsAndReturnSizes(string3);
                }
                if ((n3 = n == 184 ? this.Q - (n2 >> 2) + (n2 & 3) + 1 : this.Q - (n2 >> 2) + (n2 & 3)) > this.R) {
                    this.R = n3;
                }
                this.Q = n3;
            }
        }
        if (bl) {
            if (n2 == 0) {
                $Item.c = n2 = $Type.getArgumentsAndReturnSizes(string3);
            }
            this.r.b(185, $Item.a).a(n2 >> 2, 0);
        } else {
            this.r.b(n, $Item.a);
        }
    }

    public void visitInvokeDynamicInsn(String string, String string2, $Handle $Handle, Object ... objectArray) {
        this.Y = this.r.b;
        $Item $Item = this.b.a(string, string2, $Handle, objectArray);
        int n = $Item.c;
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(186, 0, this.b, $Item);
            } else {
                int n2;
                if (n == 0) {
                    $Item.c = n = $Type.getArgumentsAndReturnSizes(string2);
                }
                if ((n2 = this.Q - (n >> 2) + (n & 3) + 1) > this.R) {
                    this.R = n2;
                }
                this.Q = n2;
            }
        }
        this.r.b(186, $Item.a);
        this.r.putShort(0);
    }

    public void visitJumpInsn(int n, $Label $Label) {
        this.Y = this.r.b;
        $Label $Label2 = null;
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, null, null);
                $Label.a().a |= 0x10;
                this.a(0, $Label);
                if (n != 167) {
                    $Label2 = new $Label();
                }
            } else if (n == 168) {
                if (($Label.a & 0x200) == 0) {
                    $Label.a |= 0x200;
                    ++this.L;
                }
                this.P.a |= 0x80;
                this.a(this.Q + 1, $Label);
                $Label2 = new $Label();
            } else {
                this.Q += $Frame.a[n];
                this.a(this.Q, $Label);
            }
        }
        if (($Label.a & 2) != 0 && $Label.c - this.r.b < Short.MIN_VALUE) {
            if (n == 167) {
                this.r.putByte(200);
            } else if (n == 168) {
                this.r.putByte(201);
            } else {
                if ($Label2 != null) {
                    $Label2.a |= 0x10;
                }
                this.r.putByte(n <= 166 ? (n + 1 ^ 1) - 1 : n ^ 1);
                this.r.putShort(8);
                this.r.putByte(200);
            }
            $Label.a(this, this.r, this.r.b - 1, true);
        } else {
            this.r.putByte(n);
            $Label.a(this, this.r, this.r.b - 1, false);
        }
        if (this.P != null) {
            if ($Label2 != null) {
                this.visitLabel($Label2);
            }
            if (n == 167) {
                this.e();
            }
        }
    }

    public void visitLabel($Label $Label) {
        this.K |= $Label.a(this, this.r.b, this.r.a);
        if (($Label.a & 1) != 0) {
            return;
        }
        if (this.M == 0) {
            if (this.P != null) {
                if ($Label.c == this.P.c) {
                    this.P.a |= $Label.a & 0x10;
                    $Label.h = this.P.h;
                    return;
                }
                this.a(0, $Label);
            }
            this.P = $Label;
            if ($Label.h == null) {
                $Label.h = new $Frame();
                $Label.h.b = $Label;
            }
            if (this.O != null) {
                if ($Label.c == this.O.c) {
                    this.O.a |= $Label.a & 0x10;
                    $Label.h = this.O.h;
                    this.P = this.O;
                    return;
                }
                this.O.i = $Label;
            }
            this.O = $Label;
        } else if (this.M == 1) {
            if (this.P != null) {
                this.P.g = this.R;
                this.a(this.Q, $Label);
            }
            this.P = $Label;
            this.Q = 0;
            this.R = 0;
            if (this.O != null) {
                this.O.i = $Label;
            }
            this.O = $Label;
        }
    }

    public void visitLdcInsn(Object object) {
        int n;
        this.Y = this.r.b;
        $Item $Item = this.b.a(object);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(18, 0, this.b, $Item);
            } else {
                n = $Item.b == 5 || $Item.b == 6 ? this.Q + 2 : this.Q + 1;
                if (n > this.R) {
                    this.R = n;
                }
                this.Q = n;
            }
        }
        n = $Item.a;
        if ($Item.b == 5 || $Item.b == 6) {
            this.r.b(20, n);
        } else if (n >= 256) {
            this.r.b(19, n);
        } else {
            this.r.a(18, n);
        }
    }

    public void visitIincInsn(int n, int n2) {
        int n3;
        this.Y = this.r.b;
        if (this.P != null && this.M == 0) {
            this.P.h.a(132, n, null, null);
        }
        if (this.M != 2 && (n3 = n + 1) > this.t) {
            this.t = n3;
        }
        if (n > 255 || n2 > 127 || n2 < -128) {
            this.r.putByte(196).b(132, n).putShort(n2);
        } else {
            this.r.putByte(132).a(n, n2);
        }
    }

    public void visitTableSwitchInsn(int n, int n2, $Label $Label, $Label ... $LabelArray) {
        this.Y = this.r.b;
        int n3 = this.r.b;
        this.r.putByte(170);
        this.r.putByteArray(null, 0, (4 - this.r.b % 4) % 4);
        $Label.a(this, this.r, n3, true);
        this.r.putInt(n).putInt(n2);
        for (int i = 0; i < $LabelArray.length; ++i) {
            $LabelArray[i].a(this, this.r, n3, true);
        }
        this.a($Label, $LabelArray);
    }

    public void visitLookupSwitchInsn($Label $Label, int[] nArray, $Label[] $LabelArray) {
        this.Y = this.r.b;
        int n = this.r.b;
        this.r.putByte(171);
        this.r.putByteArray(null, 0, (4 - this.r.b % 4) % 4);
        $Label.a(this, this.r, n, true);
        this.r.putInt($LabelArray.length);
        for (int i = 0; i < $LabelArray.length; ++i) {
            this.r.putInt(nArray[i]);
            $LabelArray[i].a(this, this.r, n, true);
        }
        this.a($Label, $LabelArray);
    }

    private void a($Label $Label, $Label[] $LabelArray) {
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(171, 0, null, null);
                this.a(0, $Label);
                $Label.a().a |= 0x10;
                for (int i = 0; i < $LabelArray.length; ++i) {
                    this.a(0, $LabelArray[i]);
                    $LabelArray[i].a().a |= 0x10;
                }
            } else {
                --this.Q;
                this.a(this.Q, $Label);
                for (int i = 0; i < $LabelArray.length; ++i) {
                    this.a(this.Q, $LabelArray[i]);
                }
            }
            this.e();
        }
    }

    public void visitMultiANewArrayInsn(String string, int n) {
        this.Y = this.r.b;
        $Item $Item = this.b.a(string);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(197, n, this.b, $Item);
            } else {
                this.Q += 1 - n;
            }
        }
        this.r.b(197, $Item.a).putByte(n);
    }

    public $AnnotationVisitor visitInsnAnnotation(int n, $TypePath $TypePath, String string, boolean bl) {
        $ByteVector $ByteVector = new $ByteVector();
        n = n & 0xFF0000FF | this.Y << 8;
        $AnnotationWriter.a(n, $TypePath, $ByteVector);
        $ByteVector.putShort(this.b.newUTF8(string)).putShort(0);
        $AnnotationWriter $AnnotationWriter = new $AnnotationWriter(this.b, true, $ByteVector, $ByteVector, $ByteVector.b - 2);
        if (bl) {
            $AnnotationWriter.g = this.W;
            this.W = $AnnotationWriter;
        } else {
            $AnnotationWriter.g = this.X;
            this.X = $AnnotationWriter;
        }
        return $AnnotationWriter;
    }

    public void visitTryCatchBlock($Label $Label, $Label $Label2, $Label $Label3, String string) {
        ++this.A;
        $Handler $Handler = new $Handler();
        $Handler.a = $Label;
        $Handler.b = $Label2;
        $Handler.c = $Label3;
        $Handler.d = string;
        int n = $Handler.e = string != null ? this.b.newClass(string) : 0;
        if (this.C == null) {
            this.B = $Handler;
        } else {
            this.C.f = $Handler;
        }
        this.C = $Handler;
    }

    public $AnnotationVisitor visitTryCatchAnnotation(int n, $TypePath $TypePath, String string, boolean bl) {
        $ByteVector $ByteVector = new $ByteVector();
        $AnnotationWriter.a(n, $TypePath, $ByteVector);
        $ByteVector.putShort(this.b.newUTF8(string)).putShort(0);
        $AnnotationWriter $AnnotationWriter = new $AnnotationWriter(this.b, true, $ByteVector, $ByteVector, $ByteVector.b - 2);
        if (bl) {
            $AnnotationWriter.g = this.W;
            this.W = $AnnotationWriter;
        } else {
            $AnnotationWriter.g = this.X;
            this.X = $AnnotationWriter;
        }
        return $AnnotationWriter;
    }

    public void visitLocalVariable(String string, String string2, String string3, $Label $Label, $Label $Label2, int n) {
        char c;
        int n2;
        if (string3 != null) {
            if (this.G == null) {
                this.G = new $ByteVector();
            }
            ++this.F;
            this.G.putShort($Label.c).putShort($Label2.c - $Label.c).putShort(this.b.newUTF8(string)).putShort(this.b.newUTF8(string3)).putShort(n);
        }
        if (this.E == null) {
            this.E = new $ByteVector();
        }
        ++this.D;
        this.E.putShort($Label.c).putShort($Label2.c - $Label.c).putShort(this.b.newUTF8(string)).putShort(this.b.newUTF8(string2)).putShort(n);
        if (this.M != 2 && (n2 = n + ((c = string2.charAt(0)) == 'J' || c == 'D' ? 2 : 1)) > this.t) {
            this.t = n2;
        }
    }

    public $AnnotationVisitor visitLocalVariableAnnotation(int n, $TypePath $TypePath, $Label[] $LabelArray, $Label[] $LabelArray2, int[] nArray, String string, boolean bl) {
        int n2;
        $ByteVector $ByteVector = new $ByteVector();
        $ByteVector.putByte(n >>> 24).putShort($LabelArray.length);
        for (n2 = 0; n2 < $LabelArray.length; ++n2) {
            $ByteVector.putShort($LabelArray[n2].c).putShort($LabelArray2[n2].c - $LabelArray[n2].c).putShort(nArray[n2]);
        }
        if ($TypePath == null) {
            $ByteVector.putByte(0);
        } else {
            n2 = $TypePath.a[$TypePath.b] * 2 + 1;
            $ByteVector.putByteArray($TypePath.a, $TypePath.b, n2);
        }
        $ByteVector.putShort(this.b.newUTF8(string)).putShort(0);
        $AnnotationWriter $AnnotationWriter = new $AnnotationWriter(this.b, true, $ByteVector, $ByteVector, $ByteVector.b - 2);
        if (bl) {
            $AnnotationWriter.g = this.W;
            this.W = $AnnotationWriter;
        } else {
            $AnnotationWriter.g = this.X;
            this.X = $AnnotationWriter;
        }
        return $AnnotationWriter;
    }

    public void visitLineNumber(int n, $Label $Label) {
        if (this.I == null) {
            this.I = new $ByteVector();
        }
        ++this.H;
        this.I.putShort($Label.c);
        this.I.putShort(n);
    }

    public void visitMaxs(int n, int n2) {
        if (this.M == 0) {
            int n3;
            Object object;
            $Type[] $TypeArray;
            Object object2;
            $Handler $Handler = this.B;
            while ($Handler != null) {
                object2 = $Handler.a.a();
                $TypeArray = $Handler.c.a();
                $Label $Label = $Handler.b.a();
                object = $Handler.d == null ? "java/lang/Throwable" : $Handler.d;
                int n4 = 0x1700000 | this.b.c((String)object);
                $TypeArray.a |= 0x10;
                while (object2 != $Label) {
                    $Edge $Edge = new $Edge();
                    $Edge.a = n4;
                    $Edge.b = $TypeArray;
                    $Edge.c = (($Label)object2).j;
                    (($Label)object2).j = $Edge;
                    object2 = (($Label)object2).i;
                }
                $Handler = $Handler.f;
            }
            object2 = this.N.h;
            $TypeArray = $Type.getArgumentTypes(this.f);
            (($Frame)object2).a(this.b, this.c, $TypeArray, this.t);
            this.b(($Frame)object2);
            int n5 = 0;
            object = this.N;
            while (object != null) {
                Object object3 = object;
                object = (($Label)object).k;
                (($Label)object3).k = null;
                object2 = (($Label)object3).h;
                if (((($Label)object3).a & 0x10) != 0) {
                    (($Label)object3).a |= 0x20;
                }
                (($Label)object3).a |= 0x40;
                int n6 = (($Frame)object2).d.length + (($Label)object3).g;
                if (n6 > n5) {
                    n5 = n6;
                }
                $Edge $Edge = (($Label)object3).j;
                while ($Edge != null) {
                    $Label $Label = $Edge.b.a();
                    n3 = (($Frame)object2).a(this.b, $Label.h, $Edge.a) ? 1 : 0;
                    if (n3 != 0 && $Label.k == null) {
                        $Label.k = object;
                        object = $Label;
                    }
                    $Edge = $Edge.c;
                }
            }
            $Label $Label = this.N;
            while ($Label != null) {
                int n7;
                $Label $Label2;
                int n8;
                object2 = $Label.h;
                if (($Label.a & 0x20) != 0) {
                    this.b(($Frame)object2);
                }
                if (($Label.a & 0x40) == 0 && (n8 = (($Label2 = $Label.i) == null ? this.r.b : $Label2.c) - 1) >= (n7 = $Label.c)) {
                    n5 = Math.max(n5, 1);
                    for (n3 = n7; n3 < n8; ++n3) {
                        this.r.a[n3] = 0;
                    }
                    this.r.a[n8] = -65;
                    n3 = this.a(n7, 0, 1);
                    this.z[n3] = 0x1700000 | this.b.c("java/lang/Throwable");
                    this.b();
                    this.B = $Handler.a(this.B, $Label, $Label2);
                }
                $Label = $Label.i;
            }
            $Handler = this.B;
            this.A = 0;
            while ($Handler != null) {
                ++this.A;
                $Handler = $Handler.f;
            }
            this.s = n5;
        } else if (this.M == 1) {
            Object object;
            $Label $Label;
            $Label $Label3;
            $Handler $Handler = this.B;
            while ($Handler != null) {
                $Label $Label4 = $Handler.a;
                $Label3 = $Handler.c;
                $Label = $Handler.b;
                while ($Label4 != $Label) {
                    object = new $Edge();
                    (($Edge)object).a = Integer.MAX_VALUE;
                    (($Edge)object).b = $Label3;
                    if (($Label4.a & 0x80) == 0) {
                        (($Edge)object).c = $Label4.j;
                        $Label4.j = object;
                    } else {
                        (($Edge)object).c = $Label4.j.c.c;
                        $Label4.j.c.c = object;
                    }
                    $Label4 = $Label4.i;
                }
                $Handler = $Handler.f;
            }
            if (this.L > 0) {
                int n9 = 0;
                this.N.b(null, 1L, this.L);
                $Label3 = this.N;
                while ($Label3 != null) {
                    if (($Label3.a & 0x80) != 0) {
                        $Label = $Label3.j.c.b;
                        if (($Label.a & 0x400) == 0) {
                            $Label.b(null, (long)(++n9) / 32L << 32 | 1L << n9 % 32, this.L);
                        }
                    }
                    $Label3 = $Label3.i;
                }
                $Label3 = this.N;
                while ($Label3 != null) {
                    if (($Label3.a & 0x80) != 0) {
                        $Label = this.N;
                        while ($Label != null) {
                            $Label.a &= 0xFFFFF7FF;
                            $Label = $Label.i;
                        }
                        object = $Label3.j.c.b;
                        (($Label)object).b($Label3, 0L, this.L);
                    }
                    $Label3 = $Label3.i;
                }
            }
            int n10 = 0;
            $Label3 = this.N;
            while ($Label3 != null) {
                $Label = $Label3;
                $Label3 = $Label3.k;
                int n11 = $Label.f;
                int n12 = n11 + $Label.g;
                if (n12 > n10) {
                    n10 = n12;
                }
                $Edge $Edge = $Label.j;
                if (($Label.a & 0x80) != 0) {
                    $Edge = $Edge.c;
                }
                while ($Edge != null) {
                    $Label = $Edge.b;
                    if (($Label.a & 8) == 0) {
                        $Label.f = $Edge.a == Integer.MAX_VALUE ? 1 : n11 + $Edge.a;
                        $Label.a |= 8;
                        $Label.k = $Label3;
                        $Label3 = $Label;
                    }
                    $Edge = $Edge.c;
                }
            }
            this.s = Math.max(n, n10);
        } else {
            this.s = n;
            this.t = n2;
        }
    }

    public void visitEnd() {
    }

    private void a(int n, $Label $Label) {
        $Edge $Edge = new $Edge();
        $Edge.a = n;
        $Edge.b = $Label;
        $Edge.c = this.P.j;
        this.P.j = $Edge;
    }

    private void e() {
        if (this.M == 0) {
            $Label $Label = new $Label();
            $Label.h = new $Frame();
            $Label.h.b = $Label;
            $Label.a(this, this.r.b, this.r.a);
            this.O.i = $Label;
            this.O = $Label;
        } else {
            this.P.g = this.R;
        }
        this.P = null;
    }

    private void b($Frame $Frame) {
        int n;
        int n2;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int[] nArray = $Frame.c;
        int[] nArray2 = $Frame.d;
        for (n2 = 0; n2 < nArray.length; ++n2) {
            n = nArray[n2];
            if (n == 0x1000000) {
                ++n3;
            } else {
                n4 += n3 + 1;
                n3 = 0;
            }
            if (n != 0x1000004 && n != 0x1000003) continue;
            ++n2;
        }
        for (n2 = 0; n2 < nArray2.length; ++n2) {
            n = nArray2[n2];
            ++n5;
            if (n != 0x1000004 && n != 0x1000003) continue;
            ++n2;
        }
        int n6 = this.a($Frame.b.c, n4, n5);
        n2 = 0;
        while (n4 > 0) {
            n = nArray[n2];
            this.z[n6++] = n;
            if (n == 0x1000004 || n == 0x1000003) {
                ++n2;
            }
            ++n2;
            --n4;
        }
        for (n2 = 0; n2 < nArray2.length; ++n2) {
            n = nArray2[n2];
            this.z[n6++] = n;
            if (n != 0x1000004 && n != 0x1000003) continue;
            ++n2;
        }
        this.b();
    }

    private void f() {
        int n = this.a(0, this.f.length() + 1, 0);
        if ((this.c & 8) == 0) {
            this.z[n++] = (this.c & 0x80000) == 0 ? 0x1700000 | this.b.c(this.b.I) : 6;
        }
        int n2 = 1;
        block8: while (true) {
            int n3 = n2;
            switch (this.f.charAt(n2++)) {
                case 'B': 
                case 'C': 
                case 'I': 
                case 'S': 
                case 'Z': {
                    this.z[n++] = 1;
                    continue block8;
                }
                case 'F': {
                    this.z[n++] = 2;
                    continue block8;
                }
                case 'J': {
                    this.z[n++] = 4;
                    continue block8;
                }
                case 'D': {
                    this.z[n++] = 3;
                    continue block8;
                }
                case '[': {
                    while (this.f.charAt(n2) == '[') {
                        ++n2;
                    }
                    if (this.f.charAt(n2) == 'L') {
                        ++n2;
                        while (this.f.charAt(n2) != ';') {
                            ++n2;
                        }
                    }
                    this.z[n++] = 0x1700000 | this.b.c(this.f.substring(n3, ++n2));
                    continue block8;
                }
                case 'L': {
                    while (this.f.charAt(n2) != ';') {
                        ++n2;
                    }
                    this.z[n++] = 0x1700000 | this.b.c(this.f.substring(n3 + 1, n2++));
                    continue block8;
                }
            }
            break;
        }
        this.z[1] = n - 3;
        this.b();
    }

    private int a(int n, int n2, int n3) {
        int n4 = 3 + n2 + n3;
        if (this.z == null || this.z.length < n4) {
            this.z = new int[n4];
        }
        this.z[0] = n;
        this.z[1] = n2;
        this.z[2] = n3;
        return 3;
    }

    private void b() {
        if (this.x != null) {
            if (this.v == null) {
                this.v = new $ByteVector();
            }
            this.c();
            ++this.u;
        }
        this.x = this.z;
        this.z = null;
    }

    private void c() {
        int n = this.z[1];
        int n2 = this.z[2];
        if ((this.b.b & 0xFFFF) < 50) {
            this.v.putShort(this.z[0]).putShort(n);
            this.a(3, 3 + n);
            this.v.putShort(n2);
            this.a(3 + n, 3 + n + n2);
            return;
        }
        int n3 = this.x[1];
        int n4 = 255;
        int n5 = 0;
        int n6 = this.u == 0 ? this.z[0] : this.z[0] - this.x[0] - 1;
        if (n2 == 0) {
            n5 = n - n3;
            switch (n5) {
                case -3: 
                case -2: 
                case -1: {
                    n4 = 248;
                    n3 = n;
                    break;
                }
                case 0: {
                    n4 = n6 < 64 ? 0 : 251;
                    break;
                }
                case 1: 
                case 2: 
                case 3: {
                    n4 = 252;
                }
            }
        } else if (n == n3 && n2 == 1) {
            int n7 = n4 = n6 < 63 ? 64 : 247;
        }
        if (n4 != 255) {
            int n8 = 3;
            for (int i = 0; i < n3; ++i) {
                if (this.z[n8] != this.x[n8]) {
                    n4 = 255;
                    break;
                }
                ++n8;
            }
        }
        switch (n4) {
            case 0: {
                this.v.putByte(n6);
                break;
            }
            case 64: {
                this.v.putByte(64 + n6);
                this.a(3 + n, 4 + n);
                break;
            }
            case 247: {
                this.v.putByte(247).putShort(n6);
                this.a(3 + n, 4 + n);
                break;
            }
            case 251: {
                this.v.putByte(251).putShort(n6);
                break;
            }
            case 248: {
                this.v.putByte(251 + n5).putShort(n6);
                break;
            }
            case 252: {
                this.v.putByte(251 + n5).putShort(n6);
                this.a(3 + n3, 3 + n);
                break;
            }
            default: {
                this.v.putByte(255).putShort(n6).putShort(n);
                this.a(3, 3 + n);
                this.v.putShort(n2);
                this.a(3 + n, 3 + n + n2);
            }
        }
    }

    private void a(int n, int n2) {
        for (int i = n; i < n2; ++i) {
            int n3 = this.z[i];
            int n4 = n3 & 0xF0000000;
            if (n4 == 0) {
                int n5 = n3 & 0xFFFFF;
                switch (n3 & 0xFF00000) {
                    case 0x1700000: {
                        this.v.putByte(7).putShort(this.b.newClass(this.b.H[n5].g));
                        break;
                    }
                    case 0x1800000: {
                        this.v.putByte(8).putShort(this.b.H[n5].c);
                        break;
                    }
                    default: {
                        this.v.putByte(n5);
                        break;
                    }
                }
                continue;
            }
            StringBuffer stringBuffer = new StringBuffer();
            n4 >>= 28;
            while (n4-- > 0) {
                stringBuffer.append('[');
            }
            if ((n3 & 0xFF00000) == 0x1700000) {
                stringBuffer.append('L');
                stringBuffer.append(this.b.H[n3 & 0xFFFFF].g);
                stringBuffer.append(';');
            } else {
                switch (n3 & 0xF) {
                    case 1: {
                        stringBuffer.append('I');
                        break;
                    }
                    case 2: {
                        stringBuffer.append('F');
                        break;
                    }
                    case 3: {
                        stringBuffer.append('D');
                        break;
                    }
                    case 9: {
                        stringBuffer.append('Z');
                        break;
                    }
                    case 10: {
                        stringBuffer.append('B');
                        break;
                    }
                    case 11: {
                        stringBuffer.append('C');
                        break;
                    }
                    case 12: {
                        stringBuffer.append('S');
                        break;
                    }
                    default: {
                        stringBuffer.append('J');
                    }
                }
            }
            this.v.putByte(7).putShort(this.b.newClass(stringBuffer.toString()));
        }
    }

    private void a(Object object) {
        if (object instanceof String) {
            this.v.putByte(7).putShort(this.b.newClass((String)object));
        } else if (object instanceof Integer) {
            this.v.putByte((Integer)object);
        } else {
            this.v.putByte(8).putShort((($Label)object).c);
        }
    }

    final int a() {
        int n;
        if (this.h != 0) {
            return 6 + this.i;
        }
        if (this.K) {
            this.d();
        }
        int n2 = 8;
        if (this.r.b > 0) {
            if (this.r.b > 65536) {
                throw new RuntimeException("Method code too large!");
            }
            this.b.newUTF8("Code");
            n2 += 18 + this.r.b + 8 * this.A;
            if (this.E != null) {
                this.b.newUTF8("LocalVariableTable");
                n2 += 8 + this.E.b;
            }
            if (this.G != null) {
                this.b.newUTF8("LocalVariableTypeTable");
                n2 += 8 + this.G.b;
            }
            if (this.I != null) {
                this.b.newUTF8("LineNumberTable");
                n2 += 8 + this.I.b;
            }
            if (this.v != null) {
                n = (this.b.b & 0xFFFF) >= 50 ? 1 : 0;
                this.b.newUTF8(n != 0 ? "StackMapTable" : "StackMap");
                n2 += 8 + this.v.b;
            }
            if (this.W != null) {
                this.b.newUTF8("RuntimeVisibleTypeAnnotations");
                n2 += 8 + this.W.a();
            }
            if (this.X != null) {
                this.b.newUTF8("RuntimeInvisibleTypeAnnotations");
                n2 += 8 + this.X.a();
            }
            if (this.J != null) {
                n2 += this.J.a(this.b, this.r.a, this.r.b, this.s, this.t);
            }
        }
        if (this.j > 0) {
            this.b.newUTF8("Exceptions");
            n2 += 8 + 2 * this.j;
        }
        if ((this.c & 0x1000) != 0 && ((this.b.b & 0xFFFF) < 49 || (this.c & 0x40000) != 0)) {
            this.b.newUTF8("Synthetic");
            n2 += 6;
        }
        if ((this.c & 0x20000) != 0) {
            this.b.newUTF8("Deprecated");
            n2 += 6;
        }
        if (this.g != null) {
            this.b.newUTF8("Signature");
            this.b.newUTF8(this.g);
            n2 += 8;
        }
        if (this.$ != null) {
            this.b.newUTF8("MethodParameters");
            n2 += 7 + this.$.b;
        }
        if (this.l != null) {
            this.b.newUTF8("AnnotationDefault");
            n2 += 6 + this.l.b;
        }
        if (this.m != null) {
            this.b.newUTF8("RuntimeVisibleAnnotations");
            n2 += 8 + this.m.a();
        }
        if (this.n != null) {
            this.b.newUTF8("RuntimeInvisibleAnnotations");
            n2 += 8 + this.n.a();
        }
        if (this.U != null) {
            this.b.newUTF8("RuntimeVisibleTypeAnnotations");
            n2 += 8 + this.U.a();
        }
        if (this.V != null) {
            this.b.newUTF8("RuntimeInvisibleTypeAnnotations");
            n2 += 8 + this.V.a();
        }
        if (this.o != null) {
            this.b.newUTF8("RuntimeVisibleParameterAnnotations");
            n2 += 7 + 2 * (this.o.length - this.S);
            for (n = this.o.length - 1; n >= this.S; --n) {
                n2 += this.o[n] == null ? 0 : this.o[n].a();
            }
        }
        if (this.p != null) {
            this.b.newUTF8("RuntimeInvisibleParameterAnnotations");
            n2 += 7 + 2 * (this.p.length - this.S);
            for (n = this.p.length - 1; n >= this.S; --n) {
                n2 += this.p[n] == null ? 0 : this.p[n].a();
            }
        }
        if (this.q != null) {
            n2 += this.q.a(this.b, null, 0, -1, -1);
        }
        return n2;
    }

    final void a($ByteVector $ByteVector) {
        int n;
        int n2 = 64;
        int n3 = 0xE0000 | (this.c & 0x40000) / 64;
        $ByteVector.putShort(this.c & ~n3).putShort(this.d).putShort(this.e);
        if (this.h != 0) {
            $ByteVector.putByteArray(this.b.M.b, this.h, this.i);
            return;
        }
        int n4 = 0;
        if (this.r.b > 0) {
            ++n4;
        }
        if (this.j > 0) {
            ++n4;
        }
        if ((this.c & 0x1000) != 0 && ((this.b.b & 0xFFFF) < 49 || (this.c & 0x40000) != 0)) {
            ++n4;
        }
        if ((this.c & 0x20000) != 0) {
            ++n4;
        }
        if (this.g != null) {
            ++n4;
        }
        if (this.$ != null) {
            ++n4;
        }
        if (this.l != null) {
            ++n4;
        }
        if (this.m != null) {
            ++n4;
        }
        if (this.n != null) {
            ++n4;
        }
        if (this.U != null) {
            ++n4;
        }
        if (this.V != null) {
            ++n4;
        }
        if (this.o != null) {
            ++n4;
        }
        if (this.p != null) {
            ++n4;
        }
        if (this.q != null) {
            n4 += this.q.a();
        }
        $ByteVector.putShort(n4);
        if (this.r.b > 0) {
            n = 12 + this.r.b + 8 * this.A;
            if (this.E != null) {
                n += 8 + this.E.b;
            }
            if (this.G != null) {
                n += 8 + this.G.b;
            }
            if (this.I != null) {
                n += 8 + this.I.b;
            }
            if (this.v != null) {
                n += 8 + this.v.b;
            }
            if (this.W != null) {
                n += 8 + this.W.a();
            }
            if (this.X != null) {
                n += 8 + this.X.a();
            }
            if (this.J != null) {
                n += this.J.a(this.b, this.r.a, this.r.b, this.s, this.t);
            }
            $ByteVector.putShort(this.b.newUTF8("Code")).putInt(n);
            $ByteVector.putShort(this.s).putShort(this.t);
            $ByteVector.putInt(this.r.b).putByteArray(this.r.a, 0, this.r.b);
            $ByteVector.putShort(this.A);
            if (this.A > 0) {
                $Handler $Handler = this.B;
                while ($Handler != null) {
                    $ByteVector.putShort($Handler.a.c).putShort($Handler.b.c).putShort($Handler.c.c).putShort($Handler.e);
                    $Handler = $Handler.f;
                }
            }
            n4 = 0;
            if (this.E != null) {
                ++n4;
            }
            if (this.G != null) {
                ++n4;
            }
            if (this.I != null) {
                ++n4;
            }
            if (this.v != null) {
                ++n4;
            }
            if (this.W != null) {
                ++n4;
            }
            if (this.X != null) {
                ++n4;
            }
            if (this.J != null) {
                n4 += this.J.a();
            }
            $ByteVector.putShort(n4);
            if (this.E != null) {
                $ByteVector.putShort(this.b.newUTF8("LocalVariableTable"));
                $ByteVector.putInt(this.E.b + 2).putShort(this.D);
                $ByteVector.putByteArray(this.E.a, 0, this.E.b);
            }
            if (this.G != null) {
                $ByteVector.putShort(this.b.newUTF8("LocalVariableTypeTable"));
                $ByteVector.putInt(this.G.b + 2).putShort(this.F);
                $ByteVector.putByteArray(this.G.a, 0, this.G.b);
            }
            if (this.I != null) {
                $ByteVector.putShort(this.b.newUTF8("LineNumberTable"));
                $ByteVector.putInt(this.I.b + 2).putShort(this.H);
                $ByteVector.putByteArray(this.I.a, 0, this.I.b);
            }
            if (this.v != null) {
                boolean bl = (this.b.b & 0xFFFF) >= 50;
                $ByteVector.putShort(this.b.newUTF8(bl ? "StackMapTable" : "StackMap"));
                $ByteVector.putInt(this.v.b + 2).putShort(this.u);
                $ByteVector.putByteArray(this.v.a, 0, this.v.b);
            }
            if (this.W != null) {
                $ByteVector.putShort(this.b.newUTF8("RuntimeVisibleTypeAnnotations"));
                this.W.a($ByteVector);
            }
            if (this.X != null) {
                $ByteVector.putShort(this.b.newUTF8("RuntimeInvisibleTypeAnnotations"));
                this.X.a($ByteVector);
            }
            if (this.J != null) {
                this.J.a(this.b, this.r.a, this.r.b, this.t, this.s, $ByteVector);
            }
        }
        if (this.j > 0) {
            $ByteVector.putShort(this.b.newUTF8("Exceptions")).putInt(2 * this.j + 2);
            $ByteVector.putShort(this.j);
            for (n = 0; n < this.j; ++n) {
                $ByteVector.putShort(this.k[n]);
            }
        }
        if ((this.c & 0x1000) != 0 && ((this.b.b & 0xFFFF) < 49 || (this.c & 0x40000) != 0)) {
            $ByteVector.putShort(this.b.newUTF8("Synthetic")).putInt(0);
        }
        if ((this.c & 0x20000) != 0) {
            $ByteVector.putShort(this.b.newUTF8("Deprecated")).putInt(0);
        }
        if (this.g != null) {
            $ByteVector.putShort(this.b.newUTF8("Signature")).putInt(2).putShort(this.b.newUTF8(this.g));
        }
        if (this.$ != null) {
            $ByteVector.putShort(this.b.newUTF8("MethodParameters"));
            $ByteVector.putInt(this.$.b + 1).putByte(this.Z);
            $ByteVector.putByteArray(this.$.a, 0, this.$.b);
        }
        if (this.l != null) {
            $ByteVector.putShort(this.b.newUTF8("AnnotationDefault"));
            $ByteVector.putInt(this.l.b);
            $ByteVector.putByteArray(this.l.a, 0, this.l.b);
        }
        if (this.m != null) {
            $ByteVector.putShort(this.b.newUTF8("RuntimeVisibleAnnotations"));
            this.m.a($ByteVector);
        }
        if (this.n != null) {
            $ByteVector.putShort(this.b.newUTF8("RuntimeInvisibleAnnotations"));
            this.n.a($ByteVector);
        }
        if (this.U != null) {
            $ByteVector.putShort(this.b.newUTF8("RuntimeVisibleTypeAnnotations"));
            this.U.a($ByteVector);
        }
        if (this.V != null) {
            $ByteVector.putShort(this.b.newUTF8("RuntimeInvisibleTypeAnnotations"));
            this.V.a($ByteVector);
        }
        if (this.o != null) {
            $ByteVector.putShort(this.b.newUTF8("RuntimeVisibleParameterAnnotations"));
            $AnnotationWriter.a(this.o, this.S, $ByteVector);
        }
        if (this.p != null) {
            $ByteVector.putShort(this.b.newUTF8("RuntimeInvisibleParameterAnnotations"));
            $AnnotationWriter.a(this.p, this.S, $ByteVector);
        }
        if (this.q != null) {
            this.q.a(this.b, null, 0, -1, -1, $ByteVector);
        }
    }

    private void d() {
        int n;
        Object object;
        Object object2;
        int n2;
        int n3;
        int n4;
        int n5;
        byte[] byArray = this.r.a;
        Object object3 = new int[]{};
        int[] nArray = new int[]{};
        boolean[] blArray = new boolean[this.r.b];
        int n6 = 3;
        do {
            if (n6 == 3) {
                n6 = 2;
            }
            n5 = 0;
            while (n5 < byArray.length) {
                int n7 = byArray[n5] & 0xFF;
                n4 = 0;
                switch ($ClassWriter.a[n7]) {
                    case 0: 
                    case 4: {
                        ++n5;
                        break;
                    }
                    case 9: {
                        if (n7 > 201) {
                            n7 = n7 < 218 ? n7 - 49 : n7 - 20;
                            n3 = n5 + $MethodWriter.c(byArray, n5 + 1);
                        } else {
                            n3 = n5 + $MethodWriter.b(byArray, n5 + 1);
                        }
                        n2 = $MethodWriter.a(object3, nArray, n5, n3);
                        if (!(n2 >= Short.MIN_VALUE && n2 <= Short.MAX_VALUE || blArray[n5])) {
                            n4 = n7 == 167 || n7 == 168 ? 2 : 5;
                            blArray[n5] = true;
                        }
                        n5 += 3;
                        break;
                    }
                    case 10: {
                        n5 += 5;
                        break;
                    }
                    case 14: {
                        if (n6 == 1) {
                            n2 = $MethodWriter.a(object3, nArray, 0, n5);
                            n4 = -(n2 & 3);
                        } else if (!blArray[n5]) {
                            n4 = n5 & 3;
                            blArray[n5] = true;
                        }
                        n5 = n5 + 4 - (n5 & 3);
                        n5 += 4 * ($MethodWriter.a(byArray, n5 + 8) - $MethodWriter.a(byArray, n5 + 4) + 1) + 12;
                        break;
                    }
                    case 15: {
                        if (n6 == 1) {
                            n2 = $MethodWriter.a(object3, nArray, 0, n5);
                            n4 = -(n2 & 3);
                        } else if (!blArray[n5]) {
                            n4 = n5 & 3;
                            blArray[n5] = true;
                        }
                        n5 = n5 + 4 - (n5 & 3);
                        n5 += 8 * $MethodWriter.a(byArray, n5 + 4) + 8;
                        break;
                    }
                    case 17: {
                        n7 = byArray[n5 + 1] & 0xFF;
                        if (n7 == 132) {
                            n5 += 6;
                            break;
                        }
                        n5 += 4;
                        break;
                    }
                    case 1: 
                    case 3: 
                    case 11: {
                        n5 += 2;
                        break;
                    }
                    case 2: 
                    case 5: 
                    case 6: 
                    case 12: 
                    case 13: {
                        n5 += 3;
                        break;
                    }
                    case 7: 
                    case 8: {
                        n5 += 5;
                        break;
                    }
                    default: {
                        n5 += 4;
                    }
                }
                if (n4 == 0) continue;
                object2 = new int[((int[])object3).length + 1];
                object = new int[nArray.length + 1];
                System.arraycopy(object3, 0, object2, 0, ((int[])object3).length);
                System.arraycopy(nArray, 0, object, 0, nArray.length);
                object2[((int[])object3).length] = n5;
                object[nArray.length] = n4;
                object3 = object2;
                nArray = object;
                if (n4 <= 0) continue;
                n6 = 3;
            }
            if (n6 >= 3) continue;
            --n6;
        } while (n6 != 0);
        $ByteVector $ByteVector = new $ByteVector(this.r.b);
        n5 = 0;
        block24: while (n5 < this.r.b) {
            n4 = byArray[n5] & 0xFF;
            switch ($ClassWriter.a[n4]) {
                case 0: 
                case 4: {
                    $ByteVector.putByte(n4);
                    ++n5;
                    continue block24;
                }
                case 9: {
                    if (n4 > 201) {
                        n4 = n4 < 218 ? n4 - 49 : n4 - 20;
                        n3 = n5 + $MethodWriter.c(byArray, n5 + 1);
                    } else {
                        n3 = n5 + $MethodWriter.b(byArray, n5 + 1);
                    }
                    n2 = $MethodWriter.a(object3, nArray, n5, n3);
                    if (blArray[n5]) {
                        if (n4 == 167) {
                            $ByteVector.putByte(200);
                        } else if (n4 == 168) {
                            $ByteVector.putByte(201);
                        } else {
                            $ByteVector.putByte(n4 <= 166 ? (n4 + 1 ^ 1) - 1 : n4 ^ 1);
                            $ByteVector.putShort(8);
                            $ByteVector.putByte(200);
                            n2 -= 3;
                        }
                        $ByteVector.putInt(n2);
                    } else {
                        $ByteVector.putByte(n4);
                        $ByteVector.putShort(n2);
                    }
                    n5 += 3;
                    continue block24;
                }
                case 10: {
                    n3 = n5 + $MethodWriter.a(byArray, n5 + 1);
                    n2 = $MethodWriter.a(object3, nArray, n5, n3);
                    $ByteVector.putByte(n4);
                    $ByteVector.putInt(n2);
                    n5 += 5;
                    continue block24;
                }
                case 14: {
                    int n8 = n5;
                    n5 = n5 + 4 - (n8 & 3);
                    $ByteVector.putByte(170);
                    $ByteVector.putByteArray(null, 0, (4 - $ByteVector.b % 4) % 4);
                    n3 = n8 + $MethodWriter.a(byArray, n5);
                    n2 = $MethodWriter.a(object3, nArray, n8, n3);
                    $ByteVector.putInt(n2);
                    int n9 = $MethodWriter.a(byArray, n5 += 4);
                    $ByteVector.putInt(n9);
                    $ByteVector.putInt($MethodWriter.a(byArray, (n5 += 4) - 4));
                    for (n9 = $MethodWriter.a(byArray, n5 += 4) - n9 + 1; n9 > 0; --n9) {
                        n3 = n8 + $MethodWriter.a(byArray, n5);
                        n5 += 4;
                        n2 = $MethodWriter.a(object3, nArray, n8, n3);
                        $ByteVector.putInt(n2);
                    }
                    continue block24;
                }
                case 15: {
                    int n9;
                    int n8 = n5;
                    n5 = n5 + 4 - (n8 & 3);
                    $ByteVector.putByte(171);
                    $ByteVector.putByteArray(null, 0, (4 - $ByteVector.b % 4) % 4);
                    n3 = n8 + $MethodWriter.a(byArray, n5);
                    n2 = $MethodWriter.a(object3, nArray, n8, n3);
                    $ByteVector.putInt(n2);
                    n5 += 4;
                    $ByteVector.putInt(n9);
                    for (n9 = $MethodWriter.a(byArray, n5 += 4); n9 > 0; --n9) {
                        $ByteVector.putInt($MethodWriter.a(byArray, n5));
                        n3 = n8 + $MethodWriter.a(byArray, n5 += 4);
                        n5 += 4;
                        n2 = $MethodWriter.a(object3, nArray, n8, n3);
                        $ByteVector.putInt(n2);
                    }
                    continue block24;
                }
                case 17: {
                    n4 = byArray[n5 + 1] & 0xFF;
                    if (n4 == 132) {
                        $ByteVector.putByteArray(byArray, n5, 6);
                        n5 += 6;
                        continue block24;
                    }
                    $ByteVector.putByteArray(byArray, n5, 4);
                    n5 += 4;
                    continue block24;
                }
                case 1: 
                case 3: 
                case 11: {
                    $ByteVector.putByteArray(byArray, n5, 2);
                    n5 += 2;
                    continue block24;
                }
                case 2: 
                case 5: 
                case 6: 
                case 12: 
                case 13: {
                    $ByteVector.putByteArray(byArray, n5, 3);
                    n5 += 3;
                    continue block24;
                }
                case 7: 
                case 8: {
                    $ByteVector.putByteArray(byArray, n5, 5);
                    n5 += 5;
                    continue block24;
                }
            }
            $ByteVector.putByteArray(byArray, n5, 4);
            n5 += 4;
        }
        if (this.u > 0) {
            if (this.M == 0) {
                this.u = 0;
                this.v = null;
                this.x = null;
                this.z = null;
                $Frame $Frame = new $Frame();
                $Frame.b = this.N;
                object2 = $Type.getArgumentTypes(this.f);
                $Frame.a(this.b, this.c, ($Type[])object2, this.t);
                this.b($Frame);
                object = this.N;
                while (object != null) {
                    n5 = object.c - 3;
                    if ((object.a & 0x20) != 0 || n5 >= 0 && blArray[n5]) {
                        $MethodWriter.a(object3, nArray, ($Label)object);
                        this.b(object.h);
                    }
                    object = object.i;
                }
            } else {
                this.b.L = true;
            }
        }
        $Handler $Handler = this.B;
        while ($Handler != null) {
            $MethodWriter.a(object3, nArray, $Handler.a);
            $MethodWriter.a(object3, nArray, $Handler.b);
            $MethodWriter.a(object3, nArray, $Handler.c);
            $Handler = $Handler.f;
        }
        for (n = 0; n < 2; ++n) {
            object2 = n == 0 ? this.E : this.G;
            if (object2 == null) continue;
            byArray = (($ByteVector)object2).a;
            for (n5 = 0; n5 < (($ByteVector)object2).b; n5 += 10) {
                n3 = $MethodWriter.c(byArray, n5);
                n2 = $MethodWriter.a(object3, nArray, 0, n3);
                $MethodWriter.a(byArray, n5, n2);
                n2 = $MethodWriter.a(object3, nArray, 0, n3 += $MethodWriter.c(byArray, n5 + 2)) - n2;
                $MethodWriter.a(byArray, n5 + 2, n2);
            }
        }
        if (this.I != null) {
            byArray = this.I.a;
            for (n5 = 0; n5 < this.I.b; n5 += 4) {
                $MethodWriter.a(byArray, n5, $MethodWriter.a(object3, nArray, 0, $MethodWriter.c(byArray, n5)));
            }
        }
        object2 = this.J;
        while (object2 != null) {
            object = (($Attribute)object2).getLabels();
            if (object != null) {
                for (n = ((int[])object).length - 1; n >= 0; --n) {
                    $MethodWriter.a(object3, nArray, ($Label)object[n]);
                }
            }
            object2 = (($Attribute)object2).a;
        }
        this.r = $ByteVector;
    }

    static int c(byte[] byArray, int n) {
        return (byArray[n] & 0xFF) << 8 | byArray[n + 1] & 0xFF;
    }

    static short b(byte[] byArray, int n) {
        return (short)((byArray[n] & 0xFF) << 8 | byArray[n + 1] & 0xFF);
    }

    static int a(byte[] byArray, int n) {
        return (byArray[n] & 0xFF) << 24 | (byArray[n + 1] & 0xFF) << 16 | (byArray[n + 2] & 0xFF) << 8 | byArray[n + 3] & 0xFF;
    }

    static void a(byte[] byArray, int n, int n2) {
        byArray[n] = (byte)(n2 >>> 8);
        byArray[n + 1] = (byte)n2;
    }

    static int a(int[] nArray, int[] nArray2, int n, int n2) {
        int n3 = n2 - n;
        for (int i = 0; i < nArray.length; ++i) {
            if (n < nArray[i] && nArray[i] <= n2) {
                n3 += nArray2[i];
                continue;
            }
            if (n2 >= nArray[i] || nArray[i] > n) continue;
            n3 -= nArray2[i];
        }
        return n3;
    }

    static void a(int[] nArray, int[] nArray2, $Label $Label) {
        if (($Label.a & 4) == 0) {
            $Label.c = $MethodWriter.a(nArray, nArray2, 0, $Label.c);
            $Label.a |= 4;
        }
    }
}

