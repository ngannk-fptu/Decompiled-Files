/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.axis.utils.ClassUtils;

public class JWSClassLoader
extends ClassLoader {
    private String classFile = null;
    private String name = null;

    public JWSClassLoader(String name, ClassLoader cl, String classFile) throws FileNotFoundException, IOException {
        super(cl);
        this.name = name + ".class";
        this.classFile = classFile;
        FileInputStream fis = new FileInputStream(classFile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int i = 0;
        while ((i = fis.read(buf)) != -1) {
            baos.write(buf, 0, i);
        }
        fis.close();
        baos.close();
        byte[] data = baos.toByteArray();
        this.defineClass(name, data, 0, data.length);
        ClassUtils.setClassLoader(name, this);
    }

    public InputStream getResourceAsStream(String resourceName) {
        try {
            if (resourceName.equals(this.name)) {
                return new FileInputStream(this.classFile);
            }
        }
        catch (FileNotFoundException fileNotFoundException) {
            // empty catch block
        }
        return null;
    }
}

