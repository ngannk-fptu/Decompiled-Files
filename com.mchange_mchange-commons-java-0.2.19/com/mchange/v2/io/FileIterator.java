/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.io;

import com.mchange.v1.util.UIterator;
import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

public interface FileIterator
extends UIterator {
    public static final FileIterator EMPTY_FILE_ITERATOR = new FileIterator(){

        @Override
        public File nextFile() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new IllegalStateException();
        }

        @Override
        public void close() {
        }
    };

    public File nextFile() throws IOException;

    @Override
    public boolean hasNext() throws IOException;

    @Override
    public Object next() throws IOException;

    @Override
    public void remove() throws IOException;

    @Override
    public void close() throws IOException;
}

