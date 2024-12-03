/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.fileiterator;

import java.io.File;
import java.util.Iterator;

public class FileIterator
implements Iterator<File> {
    File dir;
    int n = 0;
    FileIterator next;

    public FileIterator(File nxt) {
        assert (nxt.isDirectory());
        this.dir = nxt;
    }

    @Override
    public boolean hasNext() {
        if (this.next != null) {
            return this.next.hasNext();
        }
        return this.n < this.dir.list().length;
    }

    @Override
    public File next() {
        if (this.next != null) {
            File answer = this.next.next();
            if (!this.next.hasNext()) {
                this.next = null;
            }
            return answer;
        }
        File nxt = this.dir.listFiles()[this.n++];
        if (nxt.isDirectory()) {
            this.next = new FileIterator(nxt);
            return nxt;
        }
        if (nxt.isFile()) {
            return nxt;
        }
        throw new IllegalStateException("File disappeared");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove from a file iterator");
    }
}

