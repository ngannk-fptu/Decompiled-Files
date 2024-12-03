/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.depend.ClassFile;
import org.apache.tools.ant.taskdefs.optional.depend.ClassFileIterator;

public class JarFileIterator
implements ClassFileIterator {
    private ZipInputStream jarStream;

    public JarFileIterator(InputStream stream) throws IOException {
        this.jarStream = new ZipInputStream(stream);
    }

    @Override
    public ClassFile getNextClassFile() {
        ClassFile nextElement = null;
        try {
            ZipEntry jarEntry = this.jarStream.getNextEntry();
            while (nextElement == null && jarEntry != null) {
                String entryName = jarEntry.getName();
                if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
                    ClassFile javaClass = new ClassFile();
                    javaClass.read(this.jarStream);
                    nextElement = javaClass;
                    continue;
                }
                jarEntry = this.jarStream.getNextEntry();
            }
        }
        catch (IOException e) {
            String message = e.getMessage();
            String text = e.getClass().getName();
            if (message != null) {
                text = text + ": " + message;
            }
            throw new BuildException("Problem reading JAR file: " + text);
        }
        return nextElement;
    }
}

