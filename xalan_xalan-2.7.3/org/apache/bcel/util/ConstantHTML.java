/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.util.Class2HTML;

final class ConstantHTML {
    private final String className;
    private final String classPackage;
    private final ConstantPool constantPool;
    private final PrintWriter printWriter;
    private final String[] constantRef;
    private final Constant[] constants;
    private final Method[] methods;

    ConstantHTML(String dir, String className, String classPackage, Method[] methods, ConstantPool constantPool, Charset charset) throws FileNotFoundException, UnsupportedEncodingException {
        this.className = className;
        this.classPackage = classPackage;
        this.constantPool = constantPool;
        this.methods = methods;
        this.constants = constantPool.getConstantPool();
        try (PrintWriter newPrintWriter = new PrintWriter(dir + className + "_cp.html", charset.name());){
            this.printWriter = newPrintWriter;
            this.constantRef = new String[this.constants.length];
            this.constantRef[0] = "&lt;unknown&gt;";
            this.printWriter.print("<HTML><head><meta charset=\"");
            this.printWriter.print(charset.name());
            this.printWriter.println("\"></head>");
            this.printWriter.println("<BODY BGCOLOR=\"#C0C0C0\"><TABLE BORDER=0>");
            for (int i = 1; i < this.constants.length; ++i) {
                if (i % 2 == 0) {
                    this.printWriter.print("<TR BGCOLOR=\"#C0C0C0\"><TD>");
                } else {
                    this.printWriter.print("<TR BGCOLOR=\"#A0A0A0\"><TD>");
                }
                if (this.constants[i] != null) {
                    this.writeConstant(i);
                }
                this.printWriter.print("</TD></TR>\n");
            }
            this.printWriter.println("</TABLE></BODY></HTML>");
        }
    }

    private int getMethodNumber(String str) {
        for (int i = 0; i < this.methods.length; ++i) {
            String cmp = this.methods[i].getName() + this.methods[i].getSignature();
            if (!cmp.equals(str)) continue;
            return i;
        }
        return -1;
    }

    String referenceConstant(int index) {
        return this.constantRef[index];
    }

    private void writeConstant(int index) {
        byte tag = this.constants[index].getTag();
        this.printWriter.println("<H4> <A NAME=cp" + index + ">" + index + "</A> " + Const.getConstantName(tag) + "</H4>");
        switch (tag) {
            case 10: 
            case 11: {
                int nameIndex;
                int classIndex;
                if (tag == 10) {
                    ConstantMethodref c = this.constantPool.getConstant(index, (byte)10, ConstantMethodref.class);
                    classIndex = c.getClassIndex();
                    nameIndex = c.getNameAndTypeIndex();
                } else {
                    ConstantInterfaceMethodref c1 = this.constantPool.getConstant(index, (byte)11, ConstantInterfaceMethodref.class);
                    classIndex = c1.getClassIndex();
                    nameIndex = c1.getNameAndTypeIndex();
                }
                String methodName = this.constantPool.constantToString(nameIndex, (byte)12);
                String htmlMethodName = Class2HTML.toHTML(methodName);
                String methodClass = this.constantPool.constantToString(classIndex, (byte)7);
                String shortMethodClass = Utility.compactClassName(methodClass);
                shortMethodClass = Utility.compactClassName(shortMethodClass, this.classPackage + ".", true);
                ConstantNameAndType c2 = this.constantPool.getConstant(nameIndex, (byte)12, ConstantNameAndType.class);
                String signature = this.constantPool.constantToString(c2.getSignatureIndex(), (byte)1);
                String[] args = Utility.methodSignatureArgumentTypes(signature, false);
                String type = Utility.methodSignatureReturnType(signature, false);
                String retType = Class2HTML.referenceType(type);
                StringBuilder buf = new StringBuilder("(");
                for (int i = 0; i < args.length; ++i) {
                    buf.append(Class2HTML.referenceType(args[i]));
                    if (i >= args.length - 1) continue;
                    buf.append(",&nbsp;");
                }
                buf.append(")");
                String argTypes = buf.toString();
                String ref = methodClass.equals(this.className) ? "<A HREF=\"" + this.className + "_code.html#method" + this.getMethodNumber(methodName + signature) + "\" TARGET=Code>" + htmlMethodName + "</A>" : "<A HREF=\"" + methodClass + ".html\" TARGET=_top>" + shortMethodClass + "</A>." + htmlMethodName;
                this.constantRef[index] = retType + "&nbsp;<A HREF=\"" + this.className + "_cp.html#cp" + classIndex + "\" TARGET=Constants>" + shortMethodClass + "</A>.<A HREF=\"" + this.className + "_cp.html#cp" + index + "\" TARGET=ConstantPool>" + htmlMethodName + "</A>&nbsp;" + argTypes;
                this.printWriter.println("<P><TT>" + retType + "&nbsp;" + ref + argTypes + "&nbsp;</TT>\n<UL><LI><A HREF=\"#cp" + classIndex + "\">Class index(" + classIndex + ")</A>\n<LI><A HREF=\"#cp" + nameIndex + "\">NameAndType index(" + nameIndex + ")</A></UL>");
                break;
            }
            case 9: {
                ConstantFieldref c3 = this.constantPool.getConstant(index, (byte)9, ConstantFieldref.class);
                int classIndex = c3.getClassIndex();
                int nameIndex = c3.getNameAndTypeIndex();
                String fieldClass = this.constantPool.constantToString(classIndex, (byte)7);
                String shortFieldClass = Utility.compactClassName(fieldClass);
                shortFieldClass = Utility.compactClassName(shortFieldClass, this.classPackage + ".", true);
                String fieldName = this.constantPool.constantToString(nameIndex, (byte)12);
                String ref = fieldClass.equals(this.className) ? "<A HREF=\"" + fieldClass + "_methods.html#field" + fieldName + "\" TARGET=Methods>" + fieldName + "</A>" : "<A HREF=\"" + fieldClass + ".html\" TARGET=_top>" + shortFieldClass + "</A>." + fieldName + "\n";
                this.constantRef[index] = "<A HREF=\"" + this.className + "_cp.html#cp" + classIndex + "\" TARGET=Constants>" + shortFieldClass + "</A>.<A HREF=\"" + this.className + "_cp.html#cp" + index + "\" TARGET=ConstantPool>" + fieldName + "</A>";
                this.printWriter.println("<P><TT>" + ref + "</TT><BR>\n<UL><LI><A HREF=\"#cp" + classIndex + "\">Class(" + classIndex + ")</A><BR>\n<LI><A HREF=\"#cp" + nameIndex + "\">NameAndType(" + nameIndex + ")</A></UL>");
                break;
            }
            case 7: {
                ConstantClass c4 = this.constantPool.getConstant(index, (byte)7, ConstantClass.class);
                int nameIndex = c4.getNameIndex();
                String className2 = this.constantPool.constantToString(index, tag);
                String shortClassName = Utility.compactClassName(className2);
                shortClassName = Utility.compactClassName(shortClassName, this.classPackage + ".", true);
                String ref = "<A HREF=\"" + className2 + ".html\" TARGET=_top>" + shortClassName + "</A>";
                this.constantRef[index] = "<A HREF=\"" + this.className + "_cp.html#cp" + index + "\" TARGET=ConstantPool>" + shortClassName + "</A>";
                this.printWriter.println("<P><TT>" + ref + "</TT><UL><LI><A HREF=\"#cp" + nameIndex + "\">Name index(" + nameIndex + ")</A></UL>\n");
                break;
            }
            case 8: {
                ConstantString c5 = this.constantPool.getConstant(index, (byte)8, ConstantString.class);
                int nameIndex = c5.getStringIndex();
                String str = Class2HTML.toHTML(this.constantPool.constantToString(index, tag));
                this.printWriter.println("<P><TT>" + str + "</TT><UL><LI><A HREF=\"#cp" + nameIndex + "\">Name index(" + nameIndex + ")</A></UL>\n");
                break;
            }
            case 12: {
                ConstantNameAndType c6 = this.constantPool.getConstant(index, (byte)12, ConstantNameAndType.class);
                int nameIndex = c6.getNameIndex();
                int signatureIndex = c6.getSignatureIndex();
                this.printWriter.println("<P><TT>" + Class2HTML.toHTML(this.constantPool.constantToString(index, tag)) + "</TT><UL><LI><A HREF=\"#cp" + nameIndex + "\">Name index(" + nameIndex + ")</A>\n<LI><A HREF=\"#cp" + signatureIndex + "\">Signature index(" + signatureIndex + ")</A></UL>\n");
                break;
            }
            default: {
                this.printWriter.println("<P><TT>" + Class2HTML.toHTML(this.constantPool.constantToString(index, tag)) + "</TT>\n");
            }
        }
    }
}

