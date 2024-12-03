/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.taskdefs.optional.depend.ClassFile;
import org.apache.tools.ant.taskdefs.optional.depend.ClassFileIterator;

public class DirectoryIterator
implements ClassFileIterator {
    private Deque<Iterator<File>> enumStack = new ArrayDeque<Iterator<File>>();
    private Iterator<File> currentIterator;

    public DirectoryIterator(File rootDirectory, boolean changeInto) throws IOException {
        this.currentIterator = this.getDirectoryEntries(rootDirectory).iterator();
    }

    private List<File> getDirectoryEntries(File directory) {
        File[] filesInDir = directory.listFiles();
        if (filesInDir == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(filesInDir);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public ClassFile getNextClassFile() {
        ClassFile nextElement = null;
        try {
            while (nextElement == null) {
                if (this.currentIterator.hasNext()) {
                    File element = this.currentIterator.next();
                    if (element.isDirectory()) {
                        this.enumStack.push(this.currentIterator);
                        List<File> files = this.getDirectoryEntries(element);
                        this.currentIterator = files.iterator();
                        continue;
                    }
                    InputStream inFileStream = Files.newInputStream(element.toPath(), new OpenOption[0]);
                    try {
                        if (!element.getName().endsWith(".class")) continue;
                        ClassFile javaClass = new ClassFile();
                        javaClass.read(inFileStream);
                        nextElement = javaClass;
                        continue;
                    }
                    finally {
                        if (inFileStream == null) continue;
                        inFileStream.close();
                        continue;
                    }
                }
                if (this.enumStack.isEmpty()) {
                    return nextElement;
                }
                this.currentIterator = this.enumStack.pop();
            }
            return nextElement;
        }
        catch (IOException e) {
            return null;
        }
    }
}

