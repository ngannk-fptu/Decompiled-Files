/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.openxml4j.opc.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.internal.ContentType;
import org.apache.poi.openxml4j.opc.internal.MemoryPackagePartOutputStream;
import org.apache.poi.openxml4j.opc.internal.marshallers.ZipPartMarshaller;
import org.apache.poi.util.IOUtils;

public final class MemoryPackagePart
extends PackagePart {
    protected byte[] data;

    public MemoryPackagePart(OPCPackage pack, PackagePartName partName, String contentType) throws InvalidFormatException {
        this(pack, partName, contentType, true);
    }

    public MemoryPackagePart(OPCPackage pack, PackagePartName partName, String contentType, boolean loadRelationships) throws InvalidFormatException {
        super(pack, partName, new ContentType(contentType), loadRelationships);
    }

    @Override
    protected InputStream getInputStreamImpl() {
        if (this.data == null) {
            this.data = new byte[0];
        }
        return new UnsynchronizedByteArrayInputStream(this.data);
    }

    @Override
    protected OutputStream getOutputStreamImpl() {
        return new MemoryPackagePartOutputStream(this);
    }

    @Override
    public long getSize() {
        return this.data == null ? 0L : (long)this.data.length;
    }

    @Override
    public void clear() {
        this.data = null;
    }

    @Override
    public boolean save(OutputStream os) throws OpenXML4JException {
        return new ZipPartMarshaller().marshall(this, os);
    }

    @Override
    public boolean load(InputStream is) throws InvalidFormatException {
        try (UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream();){
            IOUtils.copy(is, (OutputStream)baos);
            this.data = baos.toByteArray();
        }
        catch (IOException e) {
            throw new InvalidFormatException(e.getMessage());
        }
        return true;
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }
}

