/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.tools.ant.taskdefs.optional.depend.ClassFile;

public interface ClassFileIterator
extends Iterable<ClassFile> {
    public ClassFile getNextClassFile();

    @Override
    default public Iterator<ClassFile> iterator() {
        return new Iterator<ClassFile>(){
            ClassFile next;
            {
                this.next = ClassFileIterator.this.getNextClassFile();
            }

            @Override
            public boolean hasNext() {
                return this.next != null;
            }

            @Override
            public ClassFile next() {
                if (this.next == null) {
                    throw new NoSuchElementException();
                }
                try {
                    ClassFile classFile = this.next;
                    return classFile;
                }
                finally {
                    this.next = ClassFileIterator.this.getNextClassFile();
                }
            }
        };
    }
}

