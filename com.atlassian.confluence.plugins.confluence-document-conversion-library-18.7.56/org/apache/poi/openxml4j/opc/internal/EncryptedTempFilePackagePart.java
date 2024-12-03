/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.openxml4j.opc.internal;

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
import org.apache.poi.poifs.crypt.temp.EncryptedTempData;
import org.apache.poi.util.IOUtils;

public final class EncryptedTempFilePackagePart
extends PackagePart {
    private static final Logger LOG = LogManager.getLogger(EncryptedTempFilePackagePart.class);
    private EncryptedTempData tempFile = new EncryptedTempData();

    public EncryptedTempFilePackagePart(OPCPackage pack, PackagePartName partName, String contentType) throws InvalidFormatException, IOException {
        this(pack, partName, contentType, true);
    }

    public EncryptedTempFilePackagePart(OPCPackage pack, PackagePartName partName, String contentType, boolean loadRelationships) throws InvalidFormatException, IOException {
        super(pack, partName, new ContentType(contentType), loadRelationships);
    }

    @Override
    protected InputStream getInputStreamImpl() throws IOException {
        return this.tempFile.getInputStream();
    }

    @Override
    protected OutputStream getOutputStreamImpl() throws IOException {
        return this.tempFile.getOutputStream();
    }

    @Override
    public long getSize() {
        return this.tempFile.getByteCount();
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
        this.tempFile.dispose();
    }

    @Override
    public void flush() {
    }
}

