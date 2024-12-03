/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 */
package com.google.inject.internal.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.internal.asm.$AnnotationVisitor;
import com.google.inject.internal.asm.$ClassReader;
import com.google.inject.internal.asm.$ClassVisitor;
import com.google.inject.internal.asm.$FieldVisitor;
import com.google.inject.internal.asm.$Label;
import com.google.inject.internal.asm.$MethodVisitor;
import com.google.inject.internal.asm.$Type;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

final class LineNumbers {
    private final Class type;
    private final Map<String, Integer> lines = Maps.newHashMap();
    private String source;
    private int firstLine = Integer.MAX_VALUE;

    public LineNumbers(Class type) throws IOException {
        InputStream in;
        this.type = type;
        if (!type.isArray() && (in = type.getResourceAsStream("/" + type.getName().replace('.', '/') + ".class")) != null) {
            new $ClassReader(in).accept(new LineNumberReader(), 4);
        }
    }

    public String getSource() {
        return this.source;
    }

    public Integer getLineNumber(Member member) {
        Preconditions.checkArgument((this.type == member.getDeclaringClass() ? 1 : 0) != 0, (String)"Member %s belongs to %s, not %s", (Object[])new Object[]{member, member.getDeclaringClass(), this.type});
        return this.lines.get(this.memberKey(member));
    }

    public int getFirstLine() {
        return this.firstLine == Integer.MAX_VALUE ? 1 : this.firstLine;
    }

    private String memberKey(Member member) {
        Preconditions.checkNotNull((Object)member, (Object)"member");
        if (member instanceof Field) {
            return member.getName();
        }
        if (member instanceof Method) {
            return member.getName() + $Type.getMethodDescriptor((Method)member);
        }
        if (member instanceof Constructor) {
            StringBuilder sb = new StringBuilder().append("<init>(");
            for (Class<?> param : ((Constructor)member).getParameterTypes()) {
                sb.append($Type.getDescriptor(param));
            }
            return sb.append(")V").toString();
        }
        throw new IllegalArgumentException("Unsupported implementation class for Member, " + member.getClass());
    }

    private class LineNumberReader
    extends $ClassVisitor {
        private int line;
        private String pendingMethod;
        private String name;

        LineNumberReader() {
            super(262144);
            this.line = -1;
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.name = name;
        }

        public $MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if ((access & 2) != 0) {
                return null;
            }
            this.pendingMethod = name + desc;
            this.line = -1;
            return new LineNumberMethodVisitor();
        }

        public void visitSource(String source, String debug) {
            LineNumbers.this.source = source;
        }

        public void visitLineNumber(int line, $Label start) {
            if (line < LineNumbers.this.firstLine) {
                LineNumbers.this.firstLine = line;
            }
            this.line = line;
            if (this.pendingMethod != null) {
                LineNumbers.this.lines.put(this.pendingMethod, line);
                this.pendingMethod = null;
            }
        }

        public $FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return null;
        }

        public $AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return new LineNumberAnnotationVisitor();
        }

        public $AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
            return new LineNumberAnnotationVisitor();
        }

        class LineNumberAnnotationVisitor
        extends $AnnotationVisitor {
            LineNumberAnnotationVisitor() {
                super(262144);
            }

            public $AnnotationVisitor visitAnnotation(String name, String desc) {
                return this;
            }

            public $AnnotationVisitor visitArray(String name) {
                return this;
            }

            public void visitLocalVariable(String name, String desc, String signature, $Label start, $Label end, int index) {
            }
        }

        class LineNumberMethodVisitor
        extends $MethodVisitor {
            LineNumberMethodVisitor() {
                super(262144);
            }

            public $AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return new LineNumberAnnotationVisitor();
            }

            public $AnnotationVisitor visitAnnotationDefault() {
                return new LineNumberAnnotationVisitor();
            }

            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                if (opcode == 181 && LineNumberReader.this.name.equals(owner) && !LineNumbers.this.lines.containsKey(name) && LineNumberReader.this.line != -1) {
                    LineNumbers.this.lines.put(name, LineNumberReader.this.line);
                }
            }

            public void visitLineNumber(int line, $Label start) {
                LineNumberReader.this.visitLineNumber(line, start);
            }
        }
    }
}

