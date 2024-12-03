/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.header;

import com.sun.jersey.core.header.HttpDateFormat;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class ContentDisposition {
    private String type;
    private Map<String, String> parameters;
    private String fileName;
    private Date creationDate;
    private Date modificationDate;
    private Date readDate;
    private long size;

    protected ContentDisposition(String type, String fileName, Date creationDate, Date modificationDate, Date readDate, long size) {
        this.type = type;
        this.fileName = fileName;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.readDate = readDate;
        this.size = size;
    }

    public ContentDisposition(String header) throws ParseException {
        this(header, false);
    }

    public ContentDisposition(String header, boolean fileNameFix) throws ParseException {
        this(HttpHeaderReader.newInstance(header), fileNameFix);
    }

    public ContentDisposition(HttpHeaderReader reader) throws ParseException {
        this(reader, false);
    }

    public ContentDisposition(HttpHeaderReader reader, boolean fileNameFix) throws ParseException {
        reader.hasNext();
        this.type = reader.nextToken();
        if (reader.hasNext()) {
            this.parameters = HttpHeaderReader.readParameters(reader, fileNameFix);
        }
        this.parameters = this.parameters == null ? Collections.emptyMap() : Collections.unmodifiableMap(this.parameters);
        this.createParameters();
    }

    public String getType() {
        return this.type;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public String getFileName() {
        return this.fileName;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public Date getModificationDate() {
        return this.modificationDate;
    }

    public Date getReadDate() {
        return this.readDate;
    }

    public long getSize() {
        return this.size;
    }

    public String toString() {
        return this.toStringBuffer().toString();
    }

    protected StringBuilder toStringBuffer() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.type);
        this.addStringParameter(sb, "filename", this.fileName);
        this.addDateParameter(sb, "creation-date", this.creationDate);
        this.addDateParameter(sb, "modification-date", this.modificationDate);
        this.addDateParameter(sb, "read-date", this.readDate);
        this.addLongParameter(sb, "size", this.size);
        return sb;
    }

    protected void addStringParameter(StringBuilder sb, String name, String p) {
        if (p != null) {
            sb.append("; ").append(name).append("=\"").append(p).append("\"");
        }
    }

    protected void addDateParameter(StringBuilder sb, String name, Date p) {
        if (p != null) {
            sb.append("; ").append(name).append("=\"").append(HttpDateFormat.getPreferedDateFormat().format(p)).append("\"");
        }
    }

    protected void addLongParameter(StringBuilder sb, String name, Long p) {
        if (p != -1L) {
            sb.append("; ").append(name).append('=').append(Long.toString(p));
        }
    }

    private void createParameters() throws ParseException {
        this.fileName = this.parameters.get("filename");
        this.creationDate = this.createDate("creation-date");
        this.modificationDate = this.createDate("modification-date");
        this.readDate = this.createDate("read-date");
        this.size = this.createLong("size");
    }

    private Date createDate(String name) throws ParseException {
        String value = this.parameters.get(name);
        if (value == null) {
            return null;
        }
        return HttpDateFormat.getPreferedDateFormat().parse(value);
    }

    private long createLong(String name) throws ParseException {
        String value = this.parameters.get(name);
        if (value == null) {
            return -1L;
        }
        try {
            return Long.valueOf(value);
        }
        catch (NumberFormatException e) {
            throw new ParseException("Error parsing size parameter of value, " + value, 0);
        }
    }

    public static ContentDispositionBuilder type(String type) {
        return new ContentDispositionBuilder(type);
    }

    public static class ContentDispositionBuilder<T extends ContentDispositionBuilder<T, V>, V extends ContentDisposition> {
        protected String type;
        protected String fileName;
        protected Date creationDate;
        protected Date modificationDate;
        protected Date readDate;
        protected long size = -1L;

        ContentDispositionBuilder(String type) {
            this.type = type;
        }

        public T fileName(String fileName) {
            this.fileName = fileName;
            return (T)this;
        }

        public T creationDate(Date creationDate) {
            this.creationDate = creationDate;
            return (T)this;
        }

        public T modificationDate(Date modificationDate) {
            this.modificationDate = modificationDate;
            return (T)this;
        }

        public T readDate(Date readDate) {
            this.readDate = readDate;
            return (T)this;
        }

        public T size(long size) {
            this.size = size;
            return (T)this;
        }

        public V build() {
            ContentDisposition cd = new ContentDisposition(this.type, this.fileName, this.creationDate, this.modificationDate, this.readDate, this.size);
            return (V)cd;
        }
    }
}

