/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.BitSet;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantInvokeDynamic;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.util.ByteSequence;
import org.apache.bcel.util.Class2HTML;
import org.apache.bcel.util.ConstantHTML;

final class CodeHTML {
    private static boolean wide;
    private final String className;
    private final PrintWriter printWriter;
    private BitSet gotoSet;
    private final ConstantPool constantPool;
    private final ConstantHTML constantHtml;

    CodeHTML(String dir, String className, Method[] methods, ConstantPool constantPool, ConstantHTML constantHtml, Charset charset) throws IOException {
        this.className = className;
        this.constantPool = constantPool;
        this.constantHtml = constantHtml;
        try (PrintWriter newPrintWriter = new PrintWriter(dir + className + "_code.html", charset.name());){
            this.printWriter = newPrintWriter;
            this.printWriter.print("<HTML><head><meta charset=\"");
            this.printWriter.print(charset.name());
            this.printWriter.println("\"></head>");
            this.printWriter.println("<BODY BGCOLOR=\"#C0C0C0\">");
            for (int i = 0; i < methods.length; ++i) {
                this.writeMethod(methods[i], i);
            }
            this.printWriter.println("</BODY></HTML>");
        }
    }

    private String codeToHTML(ByteSequence bytes, int methodNumber) throws IOException {
        int i;
        short opcode = (short)bytes.readUnsignedByte();
        int defaultOffset = 0;
        int noPadBytes = 0;
        StringBuilder buf = new StringBuilder(256);
        buf.append("<TT>").append(Const.getOpcodeName(opcode)).append("</TT></TD><TD>");
        if (opcode == 170 || opcode == 171) {
            int remainder = bytes.getIndex() % 4;
            noPadBytes = remainder == 0 ? 0 : 4 - remainder;
            for (i = 0; i < noPadBytes; ++i) {
                bytes.readByte();
            }
            defaultOffset = bytes.readInt();
        }
        switch (opcode) {
            case 170: {
                int low = bytes.readInt();
                int high = bytes.readInt();
                int offset = bytes.getIndex() - 12 - noPadBytes - 1;
                defaultOffset += offset;
                buf.append("<TABLE BORDER=1><TR>");
                int[] jumpTable = new int[high - low + 1];
                for (int i2 = 0; i2 < jumpTable.length; ++i2) {
                    jumpTable[i2] = offset + bytes.readInt();
                    buf.append("<TH>").append(low + i2).append("</TH>");
                }
                buf.append("<TH>default</TH></TR>\n<TR>");
                for (int element : jumpTable) {
                    buf.append("<TD><A HREF=\"#code").append(methodNumber).append("@").append(element).append("\">").append(element).append("</A></TD>");
                }
                buf.append("<TD><A HREF=\"#code").append(methodNumber).append("@").append(defaultOffset).append("\">").append(defaultOffset).append("</A></TD></TR>\n</TABLE>\n");
                break;
            }
            case 171: {
                int npairs = bytes.readInt();
                int offset = bytes.getIndex() - 8 - noPadBytes - 1;
                int[] jumpTable = new int[npairs];
                defaultOffset += offset;
                buf.append("<TABLE BORDER=1><TR>");
                for (i = 0; i < npairs; ++i) {
                    int match = bytes.readInt();
                    jumpTable[i] = offset + bytes.readInt();
                    buf.append("<TH>").append(match).append("</TH>");
                }
                buf.append("<TH>default</TH></TR>\n<TR>");
                for (i = 0; i < npairs; ++i) {
                    buf.append("<TD><A HREF=\"#code").append(methodNumber).append("@").append(jumpTable[i]).append("\">").append(jumpTable[i]).append("</A></TD>");
                }
                buf.append("<TD><A HREF=\"#code").append(methodNumber).append("@").append(defaultOffset).append("\">").append(defaultOffset).append("</A></TD></TR>\n</TABLE>\n");
                break;
            }
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 167: 
            case 168: 
            case 198: 
            case 199: {
                int index = bytes.getIndex() + bytes.readShort() - 1;
                buf.append("<A HREF=\"#code").append(methodNumber).append("@").append(index).append("\">").append(index).append("</A>");
                break;
            }
            case 200: 
            case 201: {
                int windex = bytes.getIndex() + bytes.readInt() - 1;
                buf.append("<A HREF=\"#code").append(methodNumber).append("@").append(windex).append("\">").append(windex).append("</A>");
                break;
            }
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 54: 
            case 55: 
            case 56: 
            case 57: 
            case 58: 
            case 169: {
                int vindex;
                if (wide) {
                    vindex = bytes.readShort();
                    wide = false;
                } else {
                    vindex = bytes.readUnsignedByte();
                }
                buf.append("%").append(vindex);
                break;
            }
            case 196: {
                wide = true;
                buf.append("(wide)");
                break;
            }
            case 188: {
                buf.append("<FONT COLOR=\"#00FF00\">").append(Const.getTypeName(bytes.readByte())).append("</FONT>");
                break;
            }
            case 178: 
            case 179: 
            case 180: 
            case 181: {
                int index = bytes.readShort();
                ConstantFieldref c1 = this.constantPool.getConstant(index, (byte)9, ConstantFieldref.class);
                int classIndex = c1.getClassIndex();
                String name = this.constantPool.getConstantString(classIndex, (byte)7);
                name = Utility.compactClassName(name, false);
                index = c1.getNameAndTypeIndex();
                String fieldName = this.constantPool.constantToString(index, (byte)12);
                if (name.equals(this.className)) {
                    buf.append("<A HREF=\"").append(this.className).append("_methods.html#field").append(fieldName).append("\" TARGET=Methods>").append(fieldName).append("</A>\n");
                    break;
                }
                buf.append(this.constantHtml.referenceConstant(classIndex)).append(".").append(fieldName);
                break;
            }
            case 187: 
            case 192: 
            case 193: {
                short index = bytes.readShort();
                buf.append(this.constantHtml.referenceConstant(index));
                break;
            }
            case 182: 
            case 183: 
            case 184: 
            case 185: 
            case 186: {
                String name;
                int index;
                int classIndex;
                ConstantCP c;
                short mIndex = bytes.readShort();
                if (opcode == 185) {
                    bytes.readUnsignedByte();
                    bytes.readUnsignedByte();
                    c = this.constantPool.getConstant(mIndex, (byte)11, ConstantInterfaceMethodref.class);
                    classIndex = c.getClassIndex();
                    index = c.getNameAndTypeIndex();
                    name = Class2HTML.referenceClass(classIndex);
                } else if (opcode == 186) {
                    bytes.readUnsignedByte();
                    bytes.readUnsignedByte();
                    c = this.constantPool.getConstant(mIndex, (byte)18, ConstantInvokeDynamic.class);
                    index = c.getNameAndTypeIndex();
                    name = "#" + ((ConstantInvokeDynamic)c).getBootstrapMethodAttrIndex();
                } else {
                    c = this.constantPool.getConstant(mIndex, (byte)10, ConstantMethodref.class);
                    classIndex = c.getClassIndex();
                    index = c.getNameAndTypeIndex();
                    name = Class2HTML.referenceClass(classIndex);
                }
                String str = Class2HTML.toHTML(this.constantPool.constantToString((Constant)this.constantPool.getConstant(index, (byte)12)));
                ConstantNameAndType c2 = this.constantPool.getConstant(index, (byte)12, ConstantNameAndType.class);
                String signature = this.constantPool.constantToString(c2.getSignatureIndex(), (byte)1);
                String[] args = Utility.methodSignatureArgumentTypes(signature, false);
                String type = Utility.methodSignatureReturnType(signature, false);
                buf.append(name).append(".<A HREF=\"").append(this.className).append("_cp.html#cp").append(mIndex).append("\" TARGET=ConstantPool>").append(str).append("</A>").append("(");
                for (int i3 = 0; i3 < args.length; ++i3) {
                    buf.append(Class2HTML.referenceType(args[i3]));
                    if (i3 >= args.length - 1) continue;
                    buf.append(", ");
                }
                buf.append("):").append(Class2HTML.referenceType(type));
                break;
            }
            case 19: 
            case 20: {
                short index = bytes.readShort();
                buf.append("<A HREF=\"").append(this.className).append("_cp.html#cp").append(index).append("\" TARGET=\"ConstantPool\">").append(Class2HTML.toHTML(this.constantPool.constantToString(index, ((Constant)this.constantPool.getConstant(index)).getTag()))).append("</a>");
                break;
            }
            case 18: {
                int index = bytes.readUnsignedByte();
                buf.append("<A HREF=\"").append(this.className).append("_cp.html#cp").append(index).append("\" TARGET=\"ConstantPool\">").append(Class2HTML.toHTML(this.constantPool.constantToString(index, ((Constant)this.constantPool.getConstant(index)).getTag()))).append("</a>");
                break;
            }
            case 189: {
                short index = bytes.readShort();
                buf.append(this.constantHtml.referenceConstant(index));
                break;
            }
            case 197: {
                short index = bytes.readShort();
                byte dimensions = bytes.readByte();
                buf.append(this.constantHtml.referenceConstant(index)).append(":").append(dimensions).append("-dimensional");
                break;
            }
            case 132: {
                short constant;
                int vindex;
                if (wide) {
                    vindex = bytes.readShort();
                    constant = bytes.readShort();
                    wide = false;
                } else {
                    vindex = bytes.readUnsignedByte();
                    constant = bytes.readByte();
                }
                buf.append("%").append(vindex).append(" ").append(constant);
                break;
            }
            default: {
                if (Const.getNoOfOperands(opcode) <= 0) break;
                int i4 = 0;
                while ((long)i4 < Const.getOperandTypeCount(opcode)) {
                    switch (Const.getOperandType(opcode, i4)) {
                        case 8: {
                            buf.append(bytes.readUnsignedByte());
                            break;
                        }
                        case 9: {
                            buf.append(bytes.readShort());
                            break;
                        }
                        case 10: {
                            buf.append(bytes.readInt());
                            break;
                        }
                        default: {
                            throw new IllegalStateException("Unreachable default case reached! " + Const.getOperandType(opcode, i4));
                        }
                    }
                    buf.append("&nbsp;");
                    ++i4;
                }
                break block0;
            }
        }
        buf.append("</TD>");
        return buf.toString();
    }

    private void findGotos(ByteSequence bytes, Code code) throws IOException {
        this.gotoSet = new BitSet(bytes.available());
        if (code != null) {
            Attribute[] attributes;
            CodeException[] ce;
            for (CodeException cex : ce = code.getExceptionTable()) {
                this.gotoSet.set(cex.getStartPC());
                this.gotoSet.set(cex.getEndPC());
                this.gotoSet.set(cex.getHandlerPC());
            }
            for (Attribute attribute : attributes = code.getAttributes()) {
                if (attribute.getTag() != 5) continue;
                ((LocalVariableTable)attribute).forEach(var -> {
                    int start = var.getStartPC();
                    this.gotoSet.set(start);
                    this.gotoSet.set(start + var.getLength());
                });
                break;
            }
        }
        block7: while (bytes.available() > 0) {
            int opcode = bytes.readUnsignedByte();
            switch (opcode) {
                case 170: 
                case 171: {
                    int index;
                    int offset;
                    int remainder = bytes.getIndex() % 4;
                    int noPadBytes = remainder == 0 ? 0 : 4 - remainder;
                    for (int j = 0; j < noPadBytes; ++j) {
                        bytes.readByte();
                    }
                    int defaultOffset = bytes.readInt();
                    if (opcode == 170) {
                        int low = bytes.readInt();
                        int high = bytes.readInt();
                        offset = bytes.getIndex() - 12 - noPadBytes - 1;
                        this.gotoSet.set(defaultOffset += offset);
                        for (int j = 0; j < high - low + 1; ++j) {
                            index = offset + bytes.readInt();
                            this.gotoSet.set(index);
                        }
                        continue block7;
                    }
                    int npairs = bytes.readInt();
                    offset = bytes.getIndex() - 8 - noPadBytes - 1;
                    this.gotoSet.set(defaultOffset += offset);
                    for (int j = 0; j < npairs; ++j) {
                        bytes.readInt();
                        index = offset + bytes.readInt();
                        this.gotoSet.set(index);
                    }
                    continue block7;
                }
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: 
                case 167: 
                case 168: 
                case 198: 
                case 199: {
                    int index = bytes.getIndex() + bytes.readShort() - 1;
                    this.gotoSet.set(index);
                    continue block7;
                }
                case 200: 
                case 201: {
                    int index = bytes.getIndex() + bytes.readInt() - 1;
                    this.gotoSet.set(index);
                    continue block7;
                }
            }
            bytes.unreadByte();
            this.codeToHTML(bytes, 0);
        }
    }

    private void writeMethod(Method method, int methodNumber) throws IOException {
        String signature = method.getSignature();
        String[] args = Utility.methodSignatureArgumentTypes(signature, false);
        String type = Utility.methodSignatureReturnType(signature, false);
        String name = method.getName();
        String htmlName = Class2HTML.toHTML(name);
        String access = Utility.accessToString(method.getAccessFlags());
        access = Utility.replace(access, " ", "&nbsp;");
        Attribute[] attributes = method.getAttributes();
        this.printWriter.print("<P><B><FONT COLOR=\"#FF0000\">" + access + "</FONT>&nbsp;<A NAME=method" + methodNumber + ">" + Class2HTML.referenceType(type) + "</A>&nbsp<A HREF=\"" + this.className + "_methods.html#method" + methodNumber + "\" TARGET=Methods>" + htmlName + "</A>(");
        for (int i = 0; i < args.length; ++i) {
            this.printWriter.print(Class2HTML.referenceType(args[i]));
            if (i >= args.length - 1) continue;
            this.printWriter.print(",&nbsp;");
        }
        this.printWriter.println(")</B></P>");
        Code c = null;
        byte[] code = null;
        if (attributes.length > 0) {
            this.printWriter.print("<H4>Attributes</H4><UL>\n");
            for (int i = 0; i < attributes.length; ++i) {
                byte tag = attributes[i].getTag();
                if (tag != -1) {
                    this.printWriter.print("<LI><A HREF=\"" + this.className + "_attributes.html#method" + methodNumber + "@" + i + "\" TARGET=Attributes>" + Const.getAttributeName(tag) + "</A></LI>\n");
                } else {
                    this.printWriter.print("<LI>" + attributes[i] + "</LI>");
                }
                if (tag != 2) continue;
                c = (Code)attributes[i];
                Attribute[] attributes2 = c.getAttributes();
                code = c.getCode();
                this.printWriter.print("<UL>");
                for (int j = 0; j < attributes2.length; ++j) {
                    tag = attributes2[j].getTag();
                    this.printWriter.print("<LI><A HREF=\"" + this.className + "_attributes.html#method" + methodNumber + "@" + i + "@" + j + "\" TARGET=Attributes>" + Const.getAttributeName(tag) + "</A></LI>\n");
                }
                this.printWriter.print("</UL>");
            }
            this.printWriter.println("</UL>");
        }
        if (code != null) {
            try (ByteSequence stream = new ByteSequence(code);){
                stream.mark(stream.available());
                this.findGotos(stream, c);
                stream.reset();
                this.printWriter.println("<TABLE BORDER=0><TR><TH ALIGN=LEFT>Byte<BR>offset</TH><TH ALIGN=LEFT>Instruction</TH><TH ALIGN=LEFT>Argument</TH>");
                while (stream.available() > 0) {
                    int offset = stream.getIndex();
                    String str = this.codeToHTML(stream, methodNumber);
                    String anchor = "";
                    if (this.gotoSet.get(offset)) {
                        anchor = "<A NAME=code" + methodNumber + "@" + offset + "></A>";
                    }
                    String anchor2 = stream.getIndex() == code.length ? "<A NAME=code" + methodNumber + "@" + code.length + ">" + offset + "</A>" : "" + offset;
                    this.printWriter.println("<TR VALIGN=TOP><TD>" + anchor2 + "</TD><TD>" + anchor + str + "</TR>");
                }
            }
            this.printWriter.println("<TR><TD> </A></TD></TR>");
            this.printWriter.println("</TABLE>");
        }
    }
}

