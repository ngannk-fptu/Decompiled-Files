/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.felix.framework.util.SecureAction;

public class WeakZipFileFactory {
    private static final int WEAKLY_CLOSED = 0;
    private static final int OPEN = 1;
    private static final int CLOSED = 2;
    private static final SecureAction m_secureAction = new SecureAction();
    private final List<WeakZipFile> m_zipFiles = new ArrayList<WeakZipFile>();
    private final List<WeakZipFile> m_openFiles = new ArrayList<WeakZipFile>();
    private final Lock m_globalMutex = new ReentrantLock();
    private final int m_limit;

    public WeakZipFileFactory(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Limit must be non-negative.");
        }
        this.m_limit = limit;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public WeakZipFile create(File file) throws IOException {
        WeakZipFile wzf = new WeakZipFile(file);
        if (this.m_limit > 0) {
            this.m_globalMutex.lock();
            try {
                this.m_zipFiles.add(wzf);
                this.m_openFiles.add(wzf);
                if (this.m_openFiles.size() > this.m_limit) {
                    WeakZipFile candidate = this.m_openFiles.get(0);
                    for (WeakZipFile tmp : this.m_openFiles) {
                        if (candidate.m_timestamp <= tmp.m_timestamp) continue;
                        candidate = tmp;
                    }
                    candidate._closeWeakly();
                }
            }
            finally {
                this.m_globalMutex.unlock();
            }
        }
        return wzf;
    }

    List<WeakZipFile> getZipZiles() {
        this.m_globalMutex.lock();
        try {
            List<WeakZipFile> list = this.m_zipFiles;
            return list;
        }
        finally {
            this.m_globalMutex.unlock();
        }
    }

    List<WeakZipFile> getOpenZipZiles() {
        this.m_globalMutex.lock();
        try {
            List<WeakZipFile> list = this.m_openFiles;
            return list;
        }
        finally {
            this.m_globalMutex.unlock();
        }
    }

    public class WeakZipFile {
        private final File m_file;
        private final Lock m_localMutex = new ReentrantLock(false);
        private volatile ZipFile m_zipFile;
        private volatile int m_status = 1;
        private volatile long m_timestamp;
        private volatile SoftReference<LinkedHashMap<String, ZipEntry>> m_entries;

        private WeakZipFile(File file) throws IOException {
            this.m_file = file;
            this.m_zipFile = m_secureAction.openZipFile(this.m_file);
            this.m_timestamp = System.currentTimeMillis();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public ZipEntry getEntry(String name) {
            this.ensureZipFileIsOpen();
            try {
                ZipEntry dirEntry;
                ZipEntry ze;
                LinkedHashMap<String, ZipEntry> entries = this.getEntries(false);
                if (entries != null) {
                    ze = entries.get(name);
                    if (ze == null) {
                        ze = entries.get(name + "/");
                    }
                } else {
                    ze = this.m_zipFile.getEntry(name);
                }
                if (ze != null && ze.getSize() == 0L && !ze.isDirectory() && (dirEntry = this.m_zipFile.getEntry(name + '/')) != null) {
                    ze = dirEntry;
                }
                ZipEntry zipEntry = ze;
                return zipEntry;
            }
            finally {
                if (WeakZipFileFactory.this.m_limit > 0) {
                    this.m_localMutex.unlock();
                }
            }
        }

        public Enumeration<ZipEntry> entries() {
            this.ensureZipFileIsOpen();
            try {
                LinkedHashMap<String, ZipEntry> entries = this.getEntries(true);
                Enumeration<ZipEntry> enumeration = Collections.enumeration(entries.values());
                return enumeration;
            }
            finally {
                if (WeakZipFileFactory.this.m_limit > 0) {
                    this.m_localMutex.unlock();
                }
            }
        }

        public Enumeration<String> names() {
            this.ensureZipFileIsOpen();
            try {
                LinkedHashMap<String, ZipEntry> entries = this.getEntries(true);
                Enumeration<String> enumeration = Collections.enumeration(entries.keySet());
                return enumeration;
            }
            finally {
                if (WeakZipFileFactory.this.m_limit > 0) {
                    this.m_localMutex.unlock();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private LinkedHashMap<String, ZipEntry> getEntries(boolean create) {
            LinkedHashMap<String, ZipEntry> entries = null;
            if (this.m_entries != null) {
                entries = this.m_entries.get();
            }
            if (entries == null && create) {
                ZipFile zipFile = this.m_zipFile;
                synchronized (zipFile) {
                    if (this.m_entries != null) {
                        entries = this.m_entries.get();
                    }
                    if (entries == null) {
                        Enumeration<? extends ZipEntry> e = this.m_zipFile.entries();
                        entries = new LinkedHashMap();
                        while (e.hasMoreElements()) {
                            ZipEntry entry = e.nextElement();
                            entries.put(entry.getName(), entry);
                        }
                        this.m_entries = new SoftReference<LinkedHashMap<String, ZipEntry>>(entries);
                    }
                }
            }
            return entries;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public InputStream getInputStream(ZipEntry ze) throws IOException {
            this.ensureZipFileIsOpen();
            try {
                InputStream is = this.m_zipFile.getInputStream(ze);
                InputStream inputStream = WeakZipFileFactory.this.m_limit == 0 ? is : new WeakZipInputStream(ze.getName(), is);
                return inputStream;
            }
            finally {
                if (WeakZipFileFactory.this.m_limit > 0) {
                    this.m_localMutex.unlock();
                }
            }
        }

        void closeWeakly() {
            WeakZipFileFactory.this.m_globalMutex.lock();
            try {
                this._closeWeakly();
            }
            finally {
                WeakZipFileFactory.this.m_globalMutex.unlock();
            }
        }

        private void _closeWeakly() {
            this.m_localMutex.lock();
            try {
                if (this.m_status == 1) {
                    try {
                        this.m_status = 0;
                        if (this.m_zipFile != null) {
                            this.m_zipFile.close();
                            this.m_zipFile = null;
                        }
                        WeakZipFileFactory.this.m_openFiles.remove(this);
                    }
                    catch (IOException ex) {
                        this.__close();
                    }
                }
            }
            finally {
                this.m_localMutex.unlock();
            }
        }

        public void close() throws IOException {
            if (WeakZipFileFactory.this.m_limit > 0) {
                WeakZipFileFactory.this.m_globalMutex.lock();
                this.m_localMutex.lock();
            }
            try {
                ZipFile tmp = this.m_zipFile;
                this.__close();
                if (tmp != null) {
                    tmp.close();
                }
            }
            finally {
                if (WeakZipFileFactory.this.m_limit > 0) {
                    this.m_localMutex.unlock();
                    WeakZipFileFactory.this.m_globalMutex.unlock();
                }
            }
        }

        private void __close() {
            this.m_status = 2;
            this.m_zipFile = null;
            WeakZipFileFactory.this.m_zipFiles.remove(this);
            WeakZipFileFactory.this.m_openFiles.remove(this);
        }

        private void ensureZipFileIsOpen() {
            if (WeakZipFileFactory.this.m_limit == 0) {
                return;
            }
            this.m_localMutex.lock();
            if (this.m_status == 2) {
                this.m_localMutex.unlock();
                throw new IllegalStateException("Zip file is closed: " + this.m_file);
            }
            IOException cause = null;
            if (this.m_status == 0) {
                this.m_localMutex.unlock();
                WeakZipFileFactory.this.m_globalMutex.lock();
                this.m_localMutex.lock();
                if (this.m_status == 2) {
                    this.m_localMutex.unlock();
                    WeakZipFileFactory.this.m_globalMutex.unlock();
                    throw new IllegalStateException("Zip file is closed: " + this.m_file);
                }
                if (this.m_status == 0) {
                    try {
                        this.__reopenZipFile();
                    }
                    catch (IOException ex) {
                        cause = ex;
                    }
                }
                WeakZipFileFactory.this.m_globalMutex.unlock();
            }
            if (this.m_zipFile == null) {
                this.m_localMutex.unlock();
                IllegalStateException ise = new IllegalStateException("Zip file is closed: " + this.m_file);
                if (cause != null) {
                    ise.initCause(cause);
                }
                throw ise;
            }
        }

        private void __reopenZipFile() throws IOException {
            if (this.m_status == 0) {
                try {
                    this.m_zipFile = m_secureAction.openZipFile(this.m_file);
                    this.m_status = 1;
                    this.m_timestamp = System.currentTimeMillis();
                }
                catch (IOException ex) {
                    this.__close();
                    throw ex;
                }
                if (this.m_zipFile != null) {
                    WeakZipFileFactory.this.m_openFiles.add(this);
                    if (WeakZipFileFactory.this.m_openFiles.size() > WeakZipFileFactory.this.m_limit) {
                        WeakZipFile candidate = (WeakZipFile)WeakZipFileFactory.this.m_openFiles.get(0);
                        for (WeakZipFile tmp : WeakZipFileFactory.this.m_openFiles) {
                            if (candidate.m_timestamp <= tmp.m_timestamp) continue;
                            candidate = tmp;
                        }
                        candidate._closeWeakly();
                    }
                }
            }
        }

        class WeakZipInputStream
        extends InputStream {
            private final String m_entryName;
            private volatile InputStream m_is;
            private volatile int m_currentPos = 0;
            private volatile ZipFile m_zipFileSnapshot;

            WeakZipInputStream(String entryName, InputStream is) {
                this.m_entryName = entryName;
                this.m_is = is;
                this.m_zipFileSnapshot = WeakZipFile.this.m_zipFile;
            }

            private void ensureInputStreamIsValid() throws IOException {
                if (WeakZipFileFactory.this.m_limit == 0) {
                    return;
                }
                WeakZipFile.this.ensureZipFileIsOpen();
                if (this.m_zipFileSnapshot != WeakZipFile.this.m_zipFile) {
                    this.m_zipFileSnapshot = WeakZipFile.this.m_zipFile;
                    if (this.m_is != null) {
                        try {
                            this.m_is.close();
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                    try {
                        this.m_is = WeakZipFile.this.m_zipFile.getInputStream(WeakZipFile.this.m_zipFile.getEntry(this.m_entryName));
                        this.m_is.skip(this.m_currentPos);
                    }
                    catch (IOException ex) {
                        if (WeakZipFileFactory.this.m_limit > 0) {
                            WeakZipFile.this.m_localMutex.unlock();
                        }
                        throw ex;
                    }
                }
            }

            @Override
            public int available() throws IOException {
                this.ensureInputStreamIsValid();
                try {
                    int n = this.m_is.available();
                    return n;
                }
                finally {
                    if (WeakZipFileFactory.this.m_limit > 0) {
                        WeakZipFile.this.m_localMutex.unlock();
                    }
                }
            }

            @Override
            public void close() throws IOException {
                this.ensureInputStreamIsValid();
                try {
                    InputStream is = this.m_is;
                    this.m_is = null;
                    if (is != null) {
                        is.close();
                    }
                }
                finally {
                    if (WeakZipFileFactory.this.m_limit > 0) {
                        WeakZipFile.this.m_localMutex.unlock();
                    }
                }
            }

            @Override
            public int read() throws IOException {
                this.ensureInputStreamIsValid();
                try {
                    int len = this.m_is.read();
                    if (len > 0) {
                        ++this.m_currentPos;
                    }
                    int n = len;
                    return n;
                }
                finally {
                    if (WeakZipFileFactory.this.m_limit > 0) {
                        WeakZipFile.this.m_localMutex.unlock();
                    }
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public int read(byte[] bytes) throws IOException {
                this.ensureInputStreamIsValid();
                try {
                    int len = this.m_is.read(bytes);
                    if (len > 0) {
                        this.m_currentPos += len;
                    }
                    int n = len;
                    return n;
                }
                finally {
                    if (WeakZipFileFactory.this.m_limit > 0) {
                        WeakZipFile.this.m_localMutex.unlock();
                    }
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public int read(byte[] bytes, int i, int i1) throws IOException {
                this.ensureInputStreamIsValid();
                try {
                    int len = this.m_is.read(bytes, i, i1);
                    if (len > 0) {
                        this.m_currentPos += len;
                    }
                    int n = len;
                    return n;
                }
                finally {
                    if (WeakZipFileFactory.this.m_limit > 0) {
                        WeakZipFile.this.m_localMutex.unlock();
                    }
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public long skip(long l) throws IOException {
                this.ensureInputStreamIsValid();
                try {
                    long len = this.m_is.skip(l);
                    if (len > 0L) {
                        this.m_currentPos = (int)((long)this.m_currentPos + len);
                    }
                    long l2 = len;
                    return l2;
                }
                finally {
                    if (WeakZipFileFactory.this.m_limit > 0) {
                        WeakZipFile.this.m_localMutex.unlock();
                    }
                }
            }
        }
    }
}

