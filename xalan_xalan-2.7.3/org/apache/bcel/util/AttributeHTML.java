/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantValue;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.InnerClass;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.util.Class2HTML;
import org.apache.bcel.util.ConstantHTML;

final class AttributeHTML
implements Closeable {
    private final String className;
    private final PrintWriter printWriter;
    private int attrCount;
    private final ConstantHTML constantHtml;
    private final ConstantPool constantPool;

    AttributeHTML(String dir, String className, ConstantPool constantPool, ConstantHTML constantHtml, Charset charset) throws FileNotFoundException, UnsupportedEncodingException {
        this.className = className;
        this.constantPool = constantPool;
        this.constantHtml = constantHtml;
        this.printWriter = new PrintWriter(dir + className + "_attributes.html", charset.name());
        this.printWriter.print("<HTML><head><meta charset=\"");
        this.printWriter.print(charset.name());
        this.printWriter.println("\"></head>");
        this.printWriter.println("<BODY BGCOLOR=\"#C0C0C0\"><TABLE BORDER=0>");
    }

    @Override
    public void close() {
        this.printWriter.println("</TABLE></BODY></HTML>");
        this.printWriter.close();
    }

    private String codeLink(int link, int methodNumber) {
        return "<A HREF=\"" + this.className + "_code.html#code" + methodNumber + "@" + link + "\" TARGET=Code>" + link + "</A>";
    }

    void writeAttribute(Attribute attribute, String anchor) {
        this.writeAttribute(attribute, anchor, 0);
    }

    void writeAttribute(Attribute attribute, String anchor, int methodNumber) {
        byte tag = attribute.getTag();
        if (tag == -1) {
            return;
        }
        ++this.attrCount;
        if (this.attrCount % 2 == 0) {
            this.printWriter.print("<TR BGCOLOR=\"#C0C0C0\"><TD>");
        } else {
            this.printWriter.print("<TR BGCOLOR=\"#A0A0A0\"><TD>");
        }
        this.printWriter.println("<H4><A NAME=\"" + anchor + "\">" + this.attrCount + " " + Const.getAttributeName(tag) + "</A></H4>");
        switch (tag) {
            case 2: {
                Code c = (Code)attribute;
                this.printWriter.print("<UL><LI>Maximum stack size = " + c.getMaxStack() + "</LI>\n<LI>Number of local variables = " + c.getMaxLocals() + "</LI>\n<LI><A HREF=\"" + this.className + "_code.html#method" + methodNumber + "\" TARGET=Code>Byte code</A></LI></UL>\n");
                CodeException[] ce = c.getExceptionTable();
                int len = ce.length;
                if (len <= 0) break;
                this.printWriter.print("<P><B>Exceptions handled</B><UL>");
                for (CodeException cex : ce) {
                    int catchType = cex.getCatchType();
                    this.printWriter.print("<LI>");
                    if (catchType != 0) {
                        this.printWriter.print(this.constantHtml.referenceConstant(catchType));
                    } else {
                        this.printWriter.print("Any Exception");
                    }
                    this.printWriter.print("<BR>(Ranging from lines " + this.codeLink(cex.getStartPC(), methodNumber) + " to " + this.codeLink(cex.getEndPC(), methodNumber) + ", handled at line " + this.codeLink(cex.getHandlerPC(), methodNumber) + ")</LI>");
                }
                this.printWriter.print("</UL>");
                break;
            }
            case 1: {
                int index = ((ConstantValue)attribute).getConstantValueIndex();
                this.printWriter.print("<UL><LI><A HREF=\"" + this.className + "_cp.html#cp" + index + "\" TARGET=\"ConstantPool\">Constant value index(" + index + ")</A></UL>\n");
                break;
            }
            case 0: {
                int index = ((SourceFile)attribute).getSourceFileIndex();
                this.printWriter.print("<UL><LI><A HREF=\"" + this.className + "_cp.html#cp" + index + "\" TARGET=\"ConstantPool\">Source file index(" + index + ")</A></UL>\n");
                break;
            }
            case 3: {
                int[] indices = ((ExceptionTable)attribute).getExceptionIndexTable();
                this.printWriter.print("<UL>");
                for (int indice : indices) {
                    this.printWriter.print("<LI><A HREF=\"" + this.className + "_cp.html#cp" + indice + "\" TARGET=\"ConstantPool\">Exception class index(" + indice + ")</A>\n");
                }
                this.printWriter.print("</UL>\n");
                break;
            }
            case 4: {
                LineNumber[] lineNumbers = ((LineNumberTable)attribute).getLineNumberTable();
                this.printWriter.print("<P>");
                for (int i = 0; i < lineNumbers.length; ++i) {
                    this.printWriter.print("(" + lineNumbers[i].getStartPC() + ",&nbsp;" + lineNumbers[i].getLineNumber() + ")");
                    if (i >= lineNumbers.length - 1) continue;
                    this.printWriter.print(", ");
                }
                break;
            }
            case 5: {
                this.printWriter.print("<UL>");
                ((LocalVariableTable)attribute).forEach(var -> {
                    int sigIdx = var.getSignatureIndex();
                    String signature = this.constantPool.getConstantUtf8(sigIdx).getBytes();
                    signature = Utility.signatureToString(signature, false);
                    int start = var.getStartPC();
                    int end = start + var.getLength();
                    this.printWriter.println("<LI>" + Class2HTML.referenceType(signature) + "&nbsp;<B>" + var.getName() + "</B> in slot %" + var.getIndex() + "<BR>Valid from lines <A HREF=\"" + this.className + "_code.html#code" + methodNumber + "@" + start + "\" TARGET=Code>" + start + "</A> to <A HREF=\"" + this.className + "_code.html#code" + methodNumber + "@" + end + "\" TARGET=Code>" + end + "</A></LI>");
                });
                this.printWriter.print("</UL>\n");
                break;
            }
            case 6: {
                this.printWriter.print("<UL>");
                for (InnerClass clazz : ((InnerClasses)attribute).getInnerClasses()) {
                    int index = clazz.getInnerNameIndex();
                    String name = index > 0 ? this.constantPool.getConstantUtf8(index).getBytes() : "&lt;anonymous&gt;";
                    String access = Utility.accessToString(clazz.getInnerAccessFlags());
                    this.printWriter.print("<LI><FONT COLOR=\"#FF0000\">" + access + "</FONT> " + this.constantHtml.referenceConstant(clazz.getInnerClassIndex()) + " in&nbsp;class " + this.constantHtml.referenceConstant(clazz.getOuterClassIndex()) + " named " + name + "</LI>\n");
                }
                this.printWriter.print("</UL>\n");
                break;
            }
            default: {
                this.printWriter.print("<P>" + attribute);
            }
        }
        this.printWriter.println("</TD></TR>");
        this.printWriter.flush();
    }
}

