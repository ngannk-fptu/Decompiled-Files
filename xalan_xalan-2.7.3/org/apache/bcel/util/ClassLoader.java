/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Hashtable;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.util.ClassLoaderRepository;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;

@Deprecated
public class ClassLoader
extends java.lang.ClassLoader {
    private static final String BCEL_TOKEN = "$$BCEL$$";
    public static final String[] DEFAULT_IGNORED_PACKAGES = new String[]{"java.", "javax.", "sun."};
    private final Hashtable<String, Class<?>> classes = new Hashtable();
    private final String[] ignoredPackages;
    private Repository repository = SyntheticRepository.getInstance();

    public ClassLoader() {
        this(DEFAULT_IGNORED_PACKAGES);
    }

    public ClassLoader(java.lang.ClassLoader deferTo) {
        super(deferTo);
        this.ignoredPackages = DEFAULT_IGNORED_PACKAGES;
        this.repository = new ClassLoaderRepository(deferTo);
    }

    public ClassLoader(java.lang.ClassLoader deferTo, String[] ignoredPackages) {
        this(ignoredPackages);
        this.repository = new ClassLoaderRepository(deferTo);
    }

    public ClassLoader(String[] ignoredPackages) {
        this.ignoredPackages = ignoredPackages;
    }

    protected JavaClass createClass(String className) {
        int index = className.indexOf(BCEL_TOKEN);
        String realName = className.substring(index + BCEL_TOKEN.length());
        JavaClass clazz = null;
        try {
            byte[] bytes = Utility.decode(realName, true);
            ClassParser parser = new ClassParser(new ByteArrayInputStream(bytes), "foo");
            clazz = parser.parse();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ConstantPool cp = clazz.getConstantPool();
        ConstantClass cl = cp.getConstant(clazz.getClassNameIndex(), (byte)7, ConstantClass.class);
        ConstantUtf8 name = cp.getConstantUtf8(cl.getNameIndex());
        name.setBytes(Utility.packageToPath(className));
        return clazz;
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        Class<?> cl = null;
        cl = this.classes.get(className);
        if (cl == null) {
            for (String ignoredPackage : this.ignoredPackages) {
                if (!className.startsWith(ignoredPackage)) continue;
                cl = this.getParent().loadClass(className);
                break;
            }
            if (cl == null) {
                JavaClass clazz = null;
                if (className.contains(BCEL_TOKEN)) {
                    clazz = this.createClass(className);
                } else {
                    clazz = this.repository.loadClass(className);
                    if (clazz == null) {
                        throw new ClassNotFoundException(className);
                    }
                    clazz = this.modifyClass(clazz);
                }
                if (clazz != null) {
                    byte[] bytes = clazz.getBytes();
                    cl = this.defineClass(className, bytes, 0, bytes.length);
                } else {
                    cl = Class.forName(className);
                }
            }
            if (resolve) {
                this.resolveClass(cl);
            }
        }
        this.classes.put(className, cl);
        return cl;
    }

    protected JavaClass modifyClass(JavaClass clazz) {
        return clazz;
    }
}

