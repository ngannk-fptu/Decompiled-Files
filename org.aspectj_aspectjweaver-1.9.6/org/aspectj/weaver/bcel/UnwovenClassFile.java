/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.util.FileUtil;
import org.aspectj.weaver.IUnwovenClassFile;
import org.aspectj.weaver.bcel.Utility;

public class UnwovenClassFile
implements IUnwovenClassFile {
    protected String filename;
    protected char[] charfilename;
    protected byte[] bytes;
    protected List<ChildClass> writtenChildClasses = Collections.emptyList();
    protected String className = null;
    protected boolean isModule = false;

    public UnwovenClassFile(String filename, byte[] bytes) {
        this.filename = filename;
        this.isModule = filename.toLowerCase().endsWith("module-info.java");
        this.bytes = bytes;
    }

    public UnwovenClassFile(String filename, String classname, byte[] bytes) {
        this.filename = filename;
        this.isModule = filename.toLowerCase().endsWith("module-info.class");
        this.className = classname;
        this.bytes = bytes;
    }

    public boolean shouldBeWoven() {
        return !this.isModule;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    public String makeInnerFileName(String innerName) {
        String prefix = this.filename.substring(0, this.filename.length() - 6);
        return prefix + "$" + innerName + ".class";
    }

    @Override
    public byte[] getBytes() {
        return this.bytes;
    }

    public JavaClass getJavaClass() {
        if (this.getBytes() == null) {
            System.out.println("no bytes for: " + this.getFilename());
            Thread.dumpStack();
        }
        return Utility.makeJavaClass(this.filename, this.getBytes());
    }

    public void writeUnchangedBytes() throws IOException {
        this.writeWovenBytes(this.getBytes(), Collections.emptyList());
    }

    public void writeWovenBytes(byte[] bytes, List<ChildClass> childClasses) throws IOException {
        this.writeChildClasses(childClasses);
        BufferedOutputStream os = FileUtil.makeOutputStream(new File(this.filename));
        os.write(bytes);
        os.close();
    }

    private void writeChildClasses(List<ChildClass> childClasses) throws IOException {
        this.deleteAllChildClasses();
        childClasses.removeAll(this.writtenChildClasses);
        for (ChildClass childClass : childClasses) {
            this.writeChildClassFile(childClass.name, childClass.bytes);
        }
        this.writtenChildClasses = childClasses;
    }

    private void writeChildClassFile(String innerName, byte[] bytes) throws IOException {
        BufferedOutputStream os = FileUtil.makeOutputStream(new File(this.makeInnerFileName(innerName)));
        os.write(bytes);
        os.close();
    }

    protected void deleteAllChildClasses() {
        for (ChildClass childClass : this.writtenChildClasses) {
            this.deleteChildClassFile(childClass.name);
        }
    }

    protected void deleteChildClassFile(String innerName) {
        File childClassFile = new File(this.makeInnerFileName(innerName));
        childClassFile.delete();
    }

    static boolean unchanged(byte[] b1, byte[] b2) {
        int len = b1.length;
        if (b2.length != len) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (b1[i] == b2[i]) continue;
            return false;
        }
        return true;
    }

    @Override
    public char[] getClassNameAsChars() {
        if (this.charfilename == null) {
            this.charfilename = this.getClassName().replace('.', '/').toCharArray();
        }
        return this.charfilename;
    }

    @Override
    public String getClassName() {
        if (this.className == null) {
            this.className = this.getJavaClass().getClassName();
        }
        return this.className;
    }

    public String toString() {
        return "UnwovenClassFile(" + this.filename + ", " + this.getClassName() + ")";
    }

    public void setClassNameAsChars(char[] classNameAsChars) {
        this.charfilename = classNameAsChars;
    }

    public static class ChildClass {
        public final String name;
        public final byte[] bytes;

        ChildClass(String name, byte[] bytes) {
            this.name = name;
            this.bytes = bytes;
        }

        public boolean equals(Object other) {
            if (!(other instanceof ChildClass)) {
                return false;
            }
            ChildClass o = (ChildClass)other;
            return o.name.equals(this.name) && UnwovenClassFile.unchanged(o.bytes, this.bytes);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String toString() {
            return "(ChildClass " + this.name + ")";
        }
    }
}

