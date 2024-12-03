/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.toJava;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.axis.Version;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaWriter;
import org.apache.axis.wsdl.toJava.Namespaces;
import org.apache.axis.wsdl.toJava.Utils;

public abstract class JavaClassWriter
extends JavaWriter {
    protected Namespaces namespaces;
    protected String className;
    protected String packageName;

    protected JavaClassWriter(Emitter emitter, String fullClassName, String type) {
        super(emitter, type);
        this.namespaces = emitter.getNamespaces();
        this.packageName = Utils.getJavaPackageName(fullClassName);
        this.className = Utils.getJavaLocalName(fullClassName);
    }

    protected String getFileName() {
        return this.namespaces.toDir(this.packageName) + this.className + ".java";
    }

    protected void registerFile(String file) {
        String pkg = this.getPackage();
        String fqClass = pkg != null && pkg.length() > 0 ? pkg + '.' + this.getClassName() : this.getClassName();
        this.emitter.getGeneratedFileInfo().add(file, fqClass, this.type);
    }

    protected void writeFileHeader(PrintWriter pw) throws IOException {
        this.writeHeaderComments(pw);
        this.writePackage(pw);
        pw.println(this.getClassModifiers() + this.getClassText() + this.getClassName() + ' ' + this.getExtendsText() + this.getImplementsText() + "{");
    }

    protected void writeHeaderComments(PrintWriter pw) throws IOException {
        String localFile = this.getFileName();
        int lastSepChar = localFile.lastIndexOf(File.separatorChar);
        if (lastSepChar >= 0) {
            localFile = localFile.substring(lastSepChar + 1);
        }
        pw.println("/**");
        pw.println(" * " + localFile);
        pw.println(" *");
        pw.println(" * " + Messages.getMessage("wsdlGenLine00"));
        pw.println(" * " + Messages.getMessage("wsdlGenLine01", Version.getVersionText()));
        pw.println(" */");
        pw.println();
    }

    protected void writePackage(PrintWriter pw) throws IOException {
        String pkg = this.getPackage();
        if (pkg != null && pkg.length() > 0) {
            pw.println("package " + pkg + ";");
            pw.println();
        }
    }

    protected String getClassModifiers() {
        return "public ";
    }

    protected String getClassText() {
        return "class ";
    }

    protected String getExtendsText() {
        return "";
    }

    protected String getImplementsText() {
        return "";
    }

    protected String getPackage() {
        return this.packageName;
    }

    protected String getClassName() {
        return this.className;
    }

    protected void writeFileFooter(PrintWriter pw) throws IOException {
        super.writeFileFooter(pw);
        pw.println('}');
    }
}

