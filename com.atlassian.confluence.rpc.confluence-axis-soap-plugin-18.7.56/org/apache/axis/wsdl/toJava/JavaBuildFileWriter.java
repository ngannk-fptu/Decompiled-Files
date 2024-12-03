/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Definition
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import javax.wsdl.Definition;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaWriter;

public class JavaBuildFileWriter
extends JavaWriter {
    protected Definition definition;
    protected SymbolTable symbolTable;

    public JavaBuildFileWriter(Emitter emitter, Definition definition, SymbolTable symbolTable) {
        super(emitter, "build");
        this.definition = definition;
        this.symbolTable = symbolTable;
    }

    protected String getFileName() {
        String dir = this.emitter.getOutputDir();
        if (dir == null) {
            dir = ".";
        }
        return dir + "/build.xml";
    }

    protected void writeFileBody(PrintWriter out) throws IOException {
        out.write("<?xml version=\"1.0\"?>\n");
        out.write("<project basedir=\".\" default=\"jar\">\n");
        out.write("    <property name=\"src\" location=\".\"/>\n");
        out.write("    <property name=\"build.classes\" location=\"classes\"/>\n");
        out.write("    <path id=\"classpath\">\n");
        StringTokenizer tok = this.getClasspathComponets();
        while (tok.hasMoreTokens()) {
            out.write("        <pathelement location=\"" + tok.nextToken() + "\"/>\n");
        }
        out.write("    </path>\n");
        out.write("    <target name=\"compile\">\n");
        out.write("       <mkdir dir=\"${build.classes}\"/>\n");
        out.write("        <javac destdir=\"${build.classes}\" debug=\"on\">\n");
        out.write("            <classpath refid=\"classpath\" />\n");
        out.write("            <src path=\"${src}\"/>\n");
        out.write("        </javac>\n");
        out.write("    </target>\n");
        out.write("    <target name=\"jar\" depends=\"compile\">\n");
        out.write("        <copy todir=\"${build.classes}\">\n");
        out.write("            <fileset dir=\".\" casesensitive=\"yes\" >\n");
        out.write("                <include name=\"**/*.wsdd\"/>\n");
        out.write("            </fileset>\n");
        out.write("        </copy>\n");
        out.write("        <jar jarfile=\"" + this.getJarFileName(this.symbolTable.getWSDLURI()) + ".jar\" basedir=\"${build.classes}\" >\n");
        out.write("        <include name=\"**\" />\n");
        out.write("        <manifest>\n");
        out.write("            <section name=\"org/apache/ws4j2ee\">\n");
        out.write("            <attribute name=\"Implementation-Title\" value=\"Apache Axis\"/>\n");
        out.write("            <attribute name=\"Implementation-Vendor\" value=\"Apache Web Services\"/>\n");
        out.write("            </section>\n");
        out.write("        </manifest>\n");
        out.write("        </jar>\n");
        out.write("        <delete dir=\"${build.classes}\"/>\n");
        out.write("    </target>\n");
        out.write("</project>\n");
        out.close();
    }

    private StringTokenizer getClasspathComponets() {
        String classpath = System.getProperty("java.class.path");
        String spearator = ";";
        if (classpath.indexOf(59) < 0) {
            spearator = ":";
        }
        return new StringTokenizer(classpath, spearator);
    }

    private String getJarFileName(String wsdlFile) {
        int index = 0;
        index = wsdlFile.lastIndexOf("/");
        if (index > 0) {
            wsdlFile = wsdlFile.substring(index + 1);
        }
        if ((index = wsdlFile.lastIndexOf("?")) > 0) {
            wsdlFile = wsdlFile.substring(0, index);
        }
        if ((index = wsdlFile.indexOf(46)) != -1) {
            return wsdlFile.substring(0, index);
        }
        return wsdlFile;
    }

    public void generate() throws IOException {
        if (this.emitter.isBuildFileWanted()) {
            super.generate();
        }
    }
}

