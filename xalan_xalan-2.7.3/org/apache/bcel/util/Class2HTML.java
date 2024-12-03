/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.util.AttributeHTML;
import org.apache.bcel.util.CodeHTML;
import org.apache.bcel.util.ConstantHTML;
import org.apache.bcel.util.MethodHTML;

public class Class2HTML
implements Constants {
    private static String classPackage;
    private static String className;
    private static ConstantPool constantPool;
    private static final Set<String> basicTypes;
    private final JavaClass javaClass;
    private final String dir;

    public static void main(String[] argv) throws IOException {
        int i;
        String[] fileName = new String[argv.length];
        int files = 0;
        ClassParser parser = null;
        JavaClass javaClass = null;
        String zipFile = null;
        char sep = File.separatorChar;
        String dir = "." + sep;
        for (i = 0; i < argv.length; ++i) {
            if (argv[i].charAt(0) == '-') {
                if (argv[i].equals("-d")) {
                    boolean created;
                    File store;
                    if (!(dir = argv[++i]).endsWith("" + sep)) {
                        dir = dir + sep;
                    }
                    if ((store = new File(dir)).isDirectory() || (created = store.mkdirs()) || store.isDirectory()) continue;
                    System.out.println("Tried to create the directory " + dir + " but failed");
                    continue;
                }
                if (argv[i].equals("-zip")) {
                    zipFile = argv[++i];
                    continue;
                }
                System.out.println("Unknown option " + argv[i]);
                continue;
            }
            fileName[files++] = argv[i];
        }
        if (files == 0) {
            System.err.println("Class2HTML: No input files specified.");
        } else {
            for (i = 0; i < files; ++i) {
                System.out.print("Processing " + fileName[i] + "...");
                parser = zipFile == null ? new ClassParser(fileName[i]) : new ClassParser(zipFile, fileName[i]);
                javaClass = parser.parse();
                new Class2HTML(javaClass, dir);
                System.out.println("Done.");
            }
        }
    }

    static String referenceClass(int index) {
        String str = constantPool.getConstantString(index, (byte)7);
        str = Utility.compactClassName(str);
        str = Utility.compactClassName(str, classPackage + ".", true);
        return "<A HREF=\"" + className + "_cp.html#cp" + index + "\" TARGET=ConstantPool>" + str + "</A>";
    }

    static String referenceType(String type) {
        String shortType = Utility.compactClassName(type);
        shortType = Utility.compactClassName(shortType, classPackage + ".", true);
        int index = type.indexOf(91);
        String baseType = type;
        if (index > -1) {
            baseType = type.substring(0, index);
        }
        if (basicTypes.contains(baseType)) {
            return "<FONT COLOR=\"#00FF00\">" + type + "</FONT>";
        }
        return "<A HREF=\"" + baseType + ".html\" TARGET=_top>" + shortType + "</A>";
    }

    static String toHTML(String str) {
        StringBuilder buf = new StringBuilder();
        block6: for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            switch (ch) {
                case '<': {
                    buf.append("&lt;");
                    continue block6;
                }
                case '>': {
                    buf.append("&gt;");
                    continue block6;
                }
                case '\n': {
                    buf.append("\\n");
                    continue block6;
                }
                case '\r': {
                    buf.append("\\r");
                    continue block6;
                }
                default: {
                    buf.append(ch);
                }
            }
        }
        return buf.toString();
    }

    public Class2HTML(JavaClass javaClass, String dir) throws IOException {
        this(javaClass, dir, StandardCharsets.UTF_8);
    }

    private Class2HTML(JavaClass javaClass, String dir, Charset charset) throws IOException {
        Method[] methods = javaClass.getMethods();
        this.javaClass = javaClass;
        this.dir = dir;
        className = javaClass.getClassName();
        constantPool = javaClass.getConstantPool();
        int index = className.lastIndexOf(46);
        classPackage = index > -1 ? className.substring(0, index) : "";
        ConstantHTML constantHtml = new ConstantHTML(dir, className, classPackage, methods, constantPool, charset);
        try (AttributeHTML attributeHtml = new AttributeHTML(dir, className, constantPool, constantHtml, charset);){
            new MethodHTML(dir, className, methods, javaClass.getFields(), constantHtml, attributeHtml, charset);
            this.writeMainHTML(attributeHtml, charset);
            new CodeHTML(dir, className, methods, constantPool, constantHtml, charset);
        }
    }

    private void writeMainHTML(AttributeHTML attributeHtml, Charset charset) throws FileNotFoundException, UnsupportedEncodingException {
        try (PrintWriter file = new PrintWriter(this.dir + className + ".html", charset.name());){
            file.println("<HTML>\n<HEAD><TITLE>Documentation for " + className + "</TITLE></HEAD>\n<FRAMESET BORDER=1 cols=\"30%,*\">\n<FRAMESET BORDER=1 rows=\"80%,*\">\n<FRAME NAME=\"ConstantPool\" SRC=\"" + className + "_cp.html\"\n MARGINWIDTH=\"0\" MARGINHEIGHT=\"0\" FRAMEBORDER=\"1\" SCROLLING=\"AUTO\">\n<FRAME NAME=\"Attributes\" SRC=\"" + className + "_attributes.html\"\n MARGINWIDTH=\"0\" MARGINHEIGHT=\"0\" FRAMEBORDER=\"1\" SCROLLING=\"AUTO\">\n</FRAMESET>\n<FRAMESET BORDER=1 rows=\"80%,*\">\n<FRAME NAME=\"Code\" SRC=\"" + className + "_code.html\"\n MARGINWIDTH=0 MARGINHEIGHT=0 FRAMEBORDER=1 SCROLLING=\"AUTO\">\n<FRAME NAME=\"Methods\" SRC=\"" + className + "_methods.html\"\n MARGINWIDTH=0 MARGINHEIGHT=0 FRAMEBORDER=1 SCROLLING=\"AUTO\">\n</FRAMESET></FRAMESET></HTML>");
        }
        Attribute[] attributes = this.javaClass.getAttributes();
        for (int i = 0; i < attributes.length; ++i) {
            attributeHtml.writeAttribute(attributes[i], "class" + i);
        }
    }

    static {
        basicTypes = new HashSet<String>();
        basicTypes.add("int");
        basicTypes.add("short");
        basicTypes.add("boolean");
        basicTypes.add("void");
        basicTypes.add("char");
        basicTypes.add("byte");
        basicTypes.add("long");
        basicTypes.add("double");
        basicTypes.add("float");
    }
}

