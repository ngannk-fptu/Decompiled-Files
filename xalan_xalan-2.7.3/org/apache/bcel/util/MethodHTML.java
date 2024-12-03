/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.util.AttributeHTML;
import org.apache.bcel.util.Class2HTML;
import org.apache.bcel.util.ConstantHTML;

final class MethodHTML {
    private final String className;
    private final PrintWriter printWriter;
    private final ConstantHTML constantHtml;
    private final AttributeHTML attributeHtml;

    MethodHTML(String dir, String className, Method[] methods, Field[] fields, ConstantHTML constantHtml, AttributeHTML attributeHtml, Charset charset) throws FileNotFoundException, UnsupportedEncodingException {
        this.className = className;
        this.attributeHtml = attributeHtml;
        this.constantHtml = constantHtml;
        try (PrintWriter newPrintWriter = new PrintWriter(dir + className + "_methods.html", charset.name());){
            this.printWriter = newPrintWriter;
            this.printWriter.print("<HTML><head><meta charset=\"");
            this.printWriter.print(charset.name());
            this.printWriter.println("\"></head>");
            this.printWriter.println("<BODY BGCOLOR=\"#C0C0C0\"><TABLE BORDER=0>");
            this.printWriter.println("<TR><TH ALIGN=LEFT>Access&nbsp;flags</TH><TH ALIGN=LEFT>Type</TH><TH ALIGN=LEFT>Field&nbsp;name</TH></TR>");
            for (Field field : fields) {
                this.writeField(field);
            }
            this.printWriter.println("</TABLE>");
            this.printWriter.println("<TABLE BORDER=0><TR><TH ALIGN=LEFT>Access&nbsp;flags</TH><TH ALIGN=LEFT>Return&nbsp;type</TH><TH ALIGN=LEFT>Method&nbsp;name</TH><TH ALIGN=LEFT>Arguments</TH></TR>");
            for (int i = 0; i < methods.length; ++i) {
                this.writeMethod(methods[i], i);
            }
            this.printWriter.println("</TABLE></BODY></HTML>");
        }
    }

    private void writeField(Field field) {
        int i;
        String type = Utility.signatureToString(field.getSignature());
        String name = field.getName();
        String access = Utility.accessToString(field.getAccessFlags());
        access = Utility.replace(access, " ", "&nbsp;");
        this.printWriter.print("<TR><TD><FONT COLOR=\"#FF0000\">" + access + "</FONT></TD>\n<TD>" + Class2HTML.referenceType(type) + "</TD><TD><A NAME=\"field" + name + "\">" + name + "</A></TD>");
        Attribute[] attributes = field.getAttributes();
        for (i = 0; i < attributes.length; ++i) {
            this.attributeHtml.writeAttribute(attributes[i], name + "@" + i);
        }
        for (i = 0; i < attributes.length; ++i) {
            if (attributes[i].getTag() != 1) continue;
            String str = attributes[i].toString();
            this.printWriter.print("<TD>= <A HREF=\"" + this.className + "_attributes.html#" + name + "@" + i + "\" TARGET=\"Attributes\">" + str + "</TD>\n");
            break;
        }
        this.printWriter.println("</TR>");
    }

    private void writeMethod(Method method, int methodNumber) {
        int i;
        String signature = method.getSignature();
        String[] args = Utility.methodSignatureArgumentTypes(signature, false);
        String type = Utility.methodSignatureReturnType(signature, false);
        String name = method.getName();
        String access = Utility.accessToString(method.getAccessFlags());
        Attribute[] attributes = method.getAttributes();
        access = Utility.replace(access, " ", "&nbsp;");
        String htmlName = Class2HTML.toHTML(name);
        this.printWriter.print("<TR VALIGN=TOP><TD><FONT COLOR=\"#FF0000\"><A NAME=method" + methodNumber + ">" + access + "</A></FONT></TD>");
        this.printWriter.print("<TD>" + Class2HTML.referenceType(type) + "</TD><TD><A HREF=" + this.className + "_code.html#method" + methodNumber + " TARGET=Code>" + htmlName + "</A></TD>\n<TD>(");
        for (i = 0; i < args.length; ++i) {
            this.printWriter.print(Class2HTML.referenceType(args[i]));
            if (i >= args.length - 1) continue;
            this.printWriter.print(", ");
        }
        this.printWriter.print(")</TD></TR>");
        for (i = 0; i < attributes.length; ++i) {
            int j;
            this.attributeHtml.writeAttribute(attributes[i], "method" + methodNumber + "@" + i, methodNumber);
            byte tag = attributes[i].getTag();
            if (tag == 3) {
                this.printWriter.print("<TR VALIGN=TOP><TD COLSPAN=2></TD><TH ALIGN=LEFT>throws</TH><TD>");
                int[] exceptions = ((ExceptionTable)attributes[i]).getExceptionIndexTable();
                for (j = 0; j < exceptions.length; ++j) {
                    this.printWriter.print(this.constantHtml.referenceConstant(exceptions[j]));
                    if (j >= exceptions.length - 1) continue;
                    this.printWriter.print(", ");
                }
                this.printWriter.println("</TD></TR>");
                continue;
            }
            if (tag != 2) continue;
            Attribute[] attributeArray = ((Code)attributes[i]).getAttributes();
            for (j = 0; j < attributeArray.length; ++j) {
                this.attributeHtml.writeAttribute(attributeArray[j], "method" + methodNumber + "@" + i + "@" + j, methodNumber);
            }
        }
    }
}

