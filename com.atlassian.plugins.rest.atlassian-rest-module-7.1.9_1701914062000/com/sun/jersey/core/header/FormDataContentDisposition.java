/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.header;

import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.text.ParseException;
import java.util.Date;

public class FormDataContentDisposition
extends ContentDisposition {
    private String name;

    protected FormDataContentDisposition(String type, String name, String fileName, Date creationDate, Date modificationDate, Date readDate, long size) {
        super(type, fileName, creationDate, modificationDate, readDate, size);
        this.name = name;
        if (!this.getType().equalsIgnoreCase("form-data")) {
            throw new IllegalArgumentException("The content dispostion type is not equal to form-data");
        }
        if (name == null) {
            throw new IllegalArgumentException("The name parameter is not present");
        }
    }

    public FormDataContentDisposition(String header) throws ParseException {
        this(header, false);
    }

    public FormDataContentDisposition(String header, boolean fileNameFix) throws ParseException {
        this(HttpHeaderReader.newInstance(header), fileNameFix);
    }

    public FormDataContentDisposition(HttpHeaderReader reader) throws ParseException {
        this(reader, false);
    }

    public FormDataContentDisposition(HttpHeaderReader reader, boolean fileNameFix) throws ParseException {
        super(reader, fileNameFix);
        if (!this.getType().equalsIgnoreCase("form-data")) {
            throw new IllegalArgumentException("The content dispostion type is not equal to form-data");
        }
        this.name = this.getParameters().get("name");
        if (this.name == null) {
            throw new IllegalArgumentException("The name parameter is not present");
        }
    }

    public String getName() {
        return this.name;
    }

    @Override
    protected StringBuilder toStringBuffer() {
        StringBuilder sb = super.toStringBuffer();
        this.addStringParameter(sb, "name", this.name);
        return sb;
    }

    public static FormDataContentDispositionBuilder name(String name) {
        return new FormDataContentDispositionBuilder(name);
    }

    public static class FormDataContentDispositionBuilder
    extends ContentDisposition.ContentDispositionBuilder<FormDataContentDispositionBuilder, FormDataContentDisposition> {
        private String name;

        FormDataContentDispositionBuilder(String name) {
            super("form-data");
            this.name = name;
        }

        @Override
        public FormDataContentDisposition build() {
            FormDataContentDisposition cd = new FormDataContentDisposition(this.type, this.name, this.fileName, this.creationDate, this.modificationDate, this.readDate, this.size);
            return cd;
        }
    }
}

