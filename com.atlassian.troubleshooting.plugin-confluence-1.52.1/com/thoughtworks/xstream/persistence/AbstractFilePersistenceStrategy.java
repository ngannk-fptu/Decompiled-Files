/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.persistence;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.persistence.PersistenceStrategy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractFilePersistenceStrategy
implements PersistenceStrategy {
    private final FilenameFilter filter;
    private final File baseDirectory;
    private final String encoding;
    private final transient XStream xstream;

    public AbstractFilePersistenceStrategy(File baseDirectory, XStream xstream, String encoding) {
        this.baseDirectory = baseDirectory;
        this.xstream = xstream;
        this.encoding = encoding;
        this.filter = new ValidFilenameFilter();
    }

    protected ConverterLookup getConverterLookup() {
        return this.xstream.getConverterLookup();
    }

    protected Mapper getMapper() {
        return this.xstream.getMapper();
    }

    protected boolean isValid(File dir, String name) {
        return name.endsWith(".xml");
    }

    protected abstract Object extractKey(String var1);

    protected abstract String getName(Object var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeFile(File file, Object value) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            OutputStreamWriter writer = this.encoding != null ? new OutputStreamWriter((OutputStream)out, this.encoding) : new OutputStreamWriter(out);
            try {
                this.xstream.toXML(value, writer);
            }
            finally {
                ((Writer)writer).close();
            }
        }
        catch (IOException e) {
            throw new StreamException(e);
        }
    }

    private File getFile(String filename) {
        return new File(this.baseDirectory, filename);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object readFile(File file) {
        Object object;
        FileInputStream in = new FileInputStream(file);
        InputStreamReader reader = this.encoding != null ? new InputStreamReader((InputStream)in, this.encoding) : new InputStreamReader(in);
        try {
            object = this.xstream.fromXML(reader);
        }
        catch (Throwable throwable) {
            try {
                ((Reader)reader).close();
                throw throwable;
            }
            catch (FileNotFoundException e) {
                return null;
            }
            catch (IOException e) {
                throw new StreamException(e);
            }
        }
        ((Reader)reader).close();
        return object;
    }

    public Object put(Object key, Object value) {
        Object oldValue = this.get(key);
        String filename = this.getName(key);
        this.writeFile(new File(this.baseDirectory, filename), value);
        return oldValue;
    }

    public Iterator iterator() {
        return new XmlMapEntriesIterator();
    }

    public int size() {
        return this.baseDirectory.list(this.filter).length;
    }

    public boolean containsKey(Object key) {
        File file = this.getFile(this.getName(key));
        return file.isFile();
    }

    public Object get(Object key) {
        return this.readFile(this.getFile(this.getName(key)));
    }

    public Object remove(Object key) {
        File file = this.getFile(this.getName(key));
        Object value = null;
        if (file.isFile()) {
            value = this.readFile(file);
            file.delete();
        }
        return value;
    }

    protected class XmlMapEntriesIterator
    implements Iterator {
        private final File[] files;
        private int position;
        private File current;

        protected XmlMapEntriesIterator() {
            this.files = AbstractFilePersistenceStrategy.this.baseDirectory.listFiles(AbstractFilePersistenceStrategy.this.filter);
            this.position = -1;
            this.current = null;
        }

        public boolean hasNext() {
            return this.position + 1 < this.files.length;
        }

        public void remove() {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            this.current.delete();
        }

        public Object next() {
            return new Map.Entry(){
                private final File file;
                private final Object key;
                {
                    this.file = XmlMapEntriesIterator.this.current = XmlMapEntriesIterator.this.files[++XmlMapEntriesIterator.this.position];
                    this.key = AbstractFilePersistenceStrategy.this.extractKey(this.file.getName());
                }

                public Object getKey() {
                    return this.key;
                }

                public Object getValue() {
                    return AbstractFilePersistenceStrategy.this.readFile(this.file);
                }

                public Object setValue(Object value) {
                    return AbstractFilePersistenceStrategy.this.put(this.key, value);
                }

                public boolean equals(Object obj) {
                    if (!(obj instanceof Map.Entry)) {
                        return false;
                    }
                    Object value = this.getValue();
                    Map.Entry e2 = (Map.Entry)obj;
                    Object key2 = e2.getKey();
                    Object value2 = e2.getValue();
                    return (this.key == null ? key2 == null : this.key.equals(key2)) && (value == null ? value2 == null : this.getValue().equals(e2.getValue()));
                }
            };
        }
    }

    protected class ValidFilenameFilter
    implements FilenameFilter {
        protected ValidFilenameFilter() {
        }

        public boolean accept(File dir, String name) {
            return new File(dir, name).isFile() && AbstractFilePersistenceStrategy.this.isValid(dir, name);
        }
    }
}

