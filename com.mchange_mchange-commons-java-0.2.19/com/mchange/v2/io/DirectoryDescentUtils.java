/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.io;

import com.mchange.v2.io.FileIterator;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public final class DirectoryDescentUtils {
    public static FileIterator depthFirstEagerDescent(File file) throws IOException {
        return DirectoryDescentUtils.depthFirstEagerDescent(file, null, false);
    }

    public static FileIterator depthFirstEagerDescent(File file, FileFilter fileFilter, boolean bl) throws IOException {
        LinkedList linkedList = new LinkedList();
        HashSet hashSet = new HashSet();
        DirectoryDescentUtils.depthFirstEagerDescend(file, fileFilter, bl, linkedList, hashSet);
        return new IteratorFileIterator(linkedList.iterator());
    }

    public static void addSubtree(File file, FileFilter fileFilter, boolean bl, Collection collection) throws IOException {
        HashSet hashSet = new HashSet();
        DirectoryDescentUtils.depthFirstEagerDescend(file, fileFilter, bl, collection, hashSet);
    }

    private static void depthFirstEagerDescend(File file, FileFilter fileFilter, boolean bl, Collection collection, Set set) throws IOException {
        String string = file.getCanonicalPath();
        if (!set.contains(string)) {
            if (fileFilter == null || fileFilter.accept(file)) {
                collection.add(bl ? new File(string) : file);
            }
            set.add(string);
            String[] stringArray = file.list();
            int n = stringArray.length;
            for (int i = 0; i < n; ++i) {
                File file2 = new File(file, stringArray[i]);
                if (file2.isDirectory()) {
                    DirectoryDescentUtils.depthFirstEagerDescend(file2, fileFilter, bl, collection, set);
                    continue;
                }
                if (fileFilter != null && !fileFilter.accept(file2)) continue;
                collection.add(bl ? file2.getCanonicalFile() : file2);
            }
        }
    }

    private DirectoryDescentUtils() {
    }

    public static void main(String[] stringArray) {
        try {
            FileIterator fileIterator = DirectoryDescentUtils.depthFirstEagerDescent(new File(stringArray[0]));
            while (fileIterator.hasNext()) {
                System.err.println(fileIterator.nextFile().getPath());
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static class IteratorFileIterator
    implements FileIterator {
        Iterator ii;
        Object last;

        IteratorFileIterator(Iterator iterator) {
            this.ii = iterator;
        }

        @Override
        public File nextFile() throws IOException {
            return (File)this.next();
        }

        @Override
        public boolean hasNext() throws IOException {
            return this.ii.hasNext();
        }

        @Override
        public Object next() throws IOException {
            this.last = this.ii.next();
            return this.last;
        }

        @Override
        public void remove() throws IOException {
            if (this.last == null) {
                throw new IllegalStateException();
            }
            ((File)this.last).delete();
            this.last = null;
        }

        @Override
        public void close() throws IOException {
        }
    }
}

