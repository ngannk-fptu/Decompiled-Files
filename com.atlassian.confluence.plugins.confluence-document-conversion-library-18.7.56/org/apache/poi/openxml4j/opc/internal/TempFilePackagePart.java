/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.openxml4j.opc.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.internal.ContentType;
import org.apache.poi.openxml4j.opc.internal.marshallers.ZipPartMarshaller;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.TempFile;

public final class TempFilePackagePart
extends PackagePart {
    private static final Logger LOG = LogManager.getLogger(TempFilePackagePart.class);
    private File tempFile = TempFile.createTempFile("poi-package-part", ".tmp");

    public TempFilePackagePart(OPCPackage pack, PackagePartName partName, String contentType) throws InvalidFormatException, IOException {
        this(pack, partName, contentType, true);
    }

    public TempFilePackagePart(OPCPackage pack, PackagePartName partName, String contentType, boolean loadRelationships) throws InvalidFormatException, IOException {
        super(pack, partName, new ContentType(contentType), loadRelationships);
    }

    @Override
    protected InputStream getInputStreamImpl() throws IOException {
        return new FileInputStream(this.tempFile);
    }

    @Override
    protected OutputStream getOutputStreamImpl() throws IOException {
        return new FileOutputStream(this.tempFile);
    }

    @Override
    public long getSize() {
        return this.tempFile.length();
    }

    @Override
    public void clear() {
        try (OutputStream os = this.getOutputStreamImpl();){
            os.write(new byte[0]);
        }
        catch (IOException e) {
            LOG.atWarn().log("Failed to clear data in temp file", (Object)e);
        }
    }

    @Override
    public boolean save(OutputStream os) throws OpenXML4JException {
        return new ZipPartMarshaller().marshall(this, os);
    }

    @Override
    public boolean load(InputStream is) throws InvalidFormatException {
        try (OutputStream os = this.getOutputStreamImpl();){
            IOUtils.copy(is, os);
        }
        catch (IOException e) {
            throw new InvalidFormatException(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public void close() {
        if (!this.tempFile.delete()) {
            LOG.atInfo().log("Failed to delete temp file; may already have been closed and deleted");
        }
    }

    @Override
    public void flush() {
    }
}

