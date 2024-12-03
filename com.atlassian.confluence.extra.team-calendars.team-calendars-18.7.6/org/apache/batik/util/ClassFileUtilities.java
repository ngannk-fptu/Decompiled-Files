/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class ClassFileUtilities {
    public static final byte CONSTANT_UTF8_INFO = 1;
    public static final byte CONSTANT_INTEGER_INFO = 3;
    public static final byte CONSTANT_FLOAT_INFO = 4;
    public static final byte CONSTANT_LONG_INFO = 5;
    public static final byte CONSTANT_DOUBLE_INFO = 6;
    public static final byte CONSTANT_CLASS_INFO = 7;
    public static final byte CONSTANT_STRING_INFO = 8;
    public static final byte CONSTANT_FIELDREF_INFO = 9;
    public static final byte CONSTANT_METHODREF_INFO = 10;
    public static final byte CONSTANT_INTERFACEMETHODREF_INFO = 11;
    public static final byte CONSTANT_NAMEANDTYPE_INFO = 12;

    protected ClassFileUtilities() {
    }

    public static void main(String[] args) {
        String[] cwdFiles;
        boolean showFiles = false;
        if (args.length == 1 && args[0].equals("-f")) {
            showFiles = true;
        } else if (args.length != 0) {
            System.err.println("usage: org.apache.batik.util.ClassFileUtilities [-f]");
            System.err.println();
            System.err.println("  -f    list files that cause each jar file dependency");
            System.exit(1);
        }
        File cwd = new File(".");
        File buildDir = null;
        for (String cwdFile : cwdFiles = cwd.list()) {
            if (!cwdFile.startsWith("batik-")) continue;
            buildDir = new File(cwdFile);
            if (buildDir.isDirectory()) break;
            buildDir = null;
        }
        if (buildDir == null || !buildDir.isDirectory()) {
            System.out.println("Directory 'batik-xxx' not found in current directory!");
            return;
        }
        try {
            HashMap cs = new HashMap();
            HashMap js = new HashMap();
            ClassFileUtilities.collectJars(buildDir, js, cs);
            HashSet<JarFile> classpath = new HashSet<JarFile>();
            Iterator<Object> i = js.values().iterator();
            while (i.hasNext()) {
                classpath.add(((Jar)i.next()).jarFile);
            }
            for (ClassFile fromFile : cs.values()) {
                Set result = ClassFileUtilities.getClassDependencies(fromFile.getInputStream(), classpath, false);
                Iterator<Object> iterator = result.iterator();
                while (iterator.hasNext()) {
                    Object aResult = iterator.next();
                    ClassFile toFile = (ClassFile)cs.get(aResult);
                    if (fromFile == toFile || toFile == null) continue;
                    fromFile.deps.add(toFile);
                }
            }
            for (ClassFile fromFile : cs.values()) {
                for (Object dep : fromFile.deps) {
                    ClassFile toFile = (ClassFile)dep;
                    Jar fromJar = fromFile.jar;
                    Jar toJar = toFile.jar;
                    if (fromFile.name.equals(toFile.name) || toJar == fromJar || fromJar.files.contains(toFile.name)) continue;
                    Integer n = (Integer)fromJar.deps.get(toJar);
                    if (n == null) {
                        fromJar.deps.put(toJar, 1);
                        continue;
                    }
                    fromJar.deps.put(toJar, n + 1);
                }
            }
            ArrayList<Triple> triples = new ArrayList<Triple>(10);
            for (Jar fromJar : js.values()) {
                for (Object o : fromJar.deps.keySet()) {
                    Jar toJar = (Jar)o;
                    Triple t = new Triple();
                    t.from = fromJar;
                    t.to = toJar;
                    t.count = (Integer)fromJar.deps.get(toJar);
                    triples.add(t);
                }
            }
            Collections.sort(triples);
            for (Triple t : triples) {
                System.out.println(t.count + "," + t.from.name + "," + t.to.name);
                if (!showFiles) continue;
                for (Object file : t.from.files) {
                    ClassFile fromFile = (ClassFile)file;
                    for (Object dep : fromFile.deps) {
                        ClassFile toFile = (ClassFile)dep;
                        if (toFile.jar != t.to || t.from.files.contains(toFile.name)) continue;
                        System.out.println("\t" + fromFile.name + " --> " + toFile.name);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void collectJars(File dir, Map jars, Map classFiles) throws IOException {
        File[] files;
        for (File file : files = dir.listFiles()) {
            String n = file.getName();
            if (n.endsWith(".jar") && file.isFile()) {
                Jar j = new Jar();
                j.name = file.getPath();
                j.file = file;
                j.jarFile = new JarFile(file);
                jars.put(j.name, j);
                Enumeration<JarEntry> entries = j.jarFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry ze = entries.nextElement();
                    String name = ze.getName();
                    if (!name.endsWith(".class")) continue;
                    ClassFile cf = new ClassFile();
                    cf.name = name;
                    cf.jar = j;
                    classFiles.put(j.name + '!' + cf.name, cf);
                    j.files.add(cf);
                }
                continue;
            }
            if (!file.isDirectory()) continue;
            ClassFileUtilities.collectJars(file, jars, classFiles);
        }
    }

    public static Set getClassDependencies(String path, Set classpath, boolean rec) throws IOException {
        return ClassFileUtilities.getClassDependencies(new FileInputStream(path), classpath, rec);
    }

    public static Set getClassDependencies(InputStream is, Set classpath, boolean rec) throws IOException {
        HashSet result = new HashSet();
        HashSet done = new HashSet();
        ClassFileUtilities.computeClassDependencies(is, classpath, done, result, rec);
        return result;
    }

    private static void computeClassDependencies(InputStream is, Set classpath, Set done, Set result, boolean rec) throws IOException {
        for (Object o : ClassFileUtilities.getClassDependencies(is)) {
            String s = (String)o;
            if (done.contains(s)) continue;
            done.add(s);
            for (Object aClasspath : classpath) {
                InputStream depis = null;
                String path = null;
                Object cpEntry = aClasspath;
                if (cpEntry instanceof JarFile) {
                    JarFile jarFile = (JarFile)cpEntry;
                    String classFileName = s + ".class";
                    ZipEntry ze = jarFile.getEntry(classFileName);
                    if (ze != null) {
                        path = jarFile.getName() + '!' + classFileName;
                        depis = jarFile.getInputStream(ze);
                    }
                } else {
                    path = (String)cpEntry + '/' + s + ".class";
                    File f = new File(path);
                    if (f.isFile()) {
                        depis = new FileInputStream(f);
                    }
                }
                if (depis == null) continue;
                result.add(path);
                if (!rec) continue;
                ClassFileUtilities.computeClassDependencies(depis, classpath, done, result, rec);
            }
        }
    }

    public static Set getClassDependencies(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        if (dis.readInt() != -889275714) {
            throw new IOException("Invalid classfile");
        }
        dis.readInt();
        int len = dis.readShort();
        String[] strs = new String[len];
        HashSet<Integer> classes = new HashSet<Integer>();
        HashSet<Integer> desc = new HashSet<Integer>();
        block8: for (int i = 1; i < len; ++i) {
            int constCode = dis.readByte() & 0xFF;
            switch (constCode) {
                case 5: 
                case 6: {
                    dis.readLong();
                    ++i;
                    continue block8;
                }
                case 3: 
                case 4: 
                case 9: 
                case 10: 
                case 11: {
                    dis.readInt();
                    continue block8;
                }
                case 7: {
                    classes.add(dis.readShort() & 0xFFFF);
                    continue block8;
                }
                case 8: {
                    dis.readShort();
                    continue block8;
                }
                case 12: {
                    dis.readShort();
                    desc.add(dis.readShort() & 0xFFFF);
                    continue block8;
                }
                case 1: {
                    strs[i] = dis.readUTF();
                    continue block8;
                }
                default: {
                    throw new RuntimeException("unexpected data in constant-pool:" + constCode);
                }
            }
        }
        HashSet<String> result = new HashSet<String>();
        Iterator it = classes.iterator();
        while (it.hasNext()) {
            result.add(strs[(Integer)it.next()]);
        }
        it = desc.iterator();
        while (it.hasNext()) {
            result.addAll(ClassFileUtilities.getDescriptorClasses(strs[(Integer)it.next()]));
        }
        return result;
    }

    protected static Set getDescriptorClasses(String desc) {
        HashSet<String> result = new HashSet<String>();
        int i = 0;
        char c = desc.charAt(i);
        switch (c) {
            case '(': {
                StringBuffer sb;
                block14: while (true) {
                    c = desc.charAt(++i);
                    switch (c) {
                        case '[': {
                            while ((c = desc.charAt(++i)) == '[') {
                            }
                            if (c != 'L') continue block14;
                        }
                        case 'L': {
                            c = desc.charAt(++i);
                            sb = new StringBuffer();
                            while (c != ';') {
                                sb.append(c);
                                c = desc.charAt(++i);
                            }
                            result.add(sb.toString());
                            continue block14;
                        }
                        default: {
                            continue block14;
                        }
                        case ')': 
                    }
                    break;
                }
                c = desc.charAt(++i);
                switch (c) {
                    case '[': {
                        while ((c = desc.charAt(++i)) == '[') {
                        }
                        if (c != 'L') break;
                    }
                    case 'L': {
                        c = desc.charAt(++i);
                        sb = new StringBuffer();
                        while (c != ';') {
                            sb.append(c);
                            c = desc.charAt(++i);
                        }
                        result.add(sb.toString());
                        break;
                    }
                }
                break;
            }
            case '[': {
                while ((c = desc.charAt(++i)) == '[') {
                }
                if (c != 'L') break;
            }
            case 'L': {
                c = desc.charAt(++i);
                StringBuffer sb = new StringBuffer();
                while (c != ';') {
                    sb.append(c);
                    c = desc.charAt(++i);
                }
                result.add(sb.toString());
                break;
            }
        }
        return result;
    }

    protected static class Triple
    implements Comparable {
        public Jar from;
        public Jar to;
        public int count;

        protected Triple() {
        }

        public int compareTo(Object o) {
            return ((Triple)o).count - this.count;
        }
    }

    protected static class Jar {
        public String name;
        public File file;
        public JarFile jarFile;
        public Map deps = new HashMap();
        public Set files = new HashSet();

        protected Jar() {
        }
    }

    protected static class ClassFile {
        public String name;
        public List deps = new ArrayList(10);
        public Jar jar;

        protected ClassFile() {
        }

        public InputStream getInputStream() throws IOException {
            return this.jar.jarFile.getInputStream(this.jar.jarFile.getEntry(this.name));
        }
    }
}

