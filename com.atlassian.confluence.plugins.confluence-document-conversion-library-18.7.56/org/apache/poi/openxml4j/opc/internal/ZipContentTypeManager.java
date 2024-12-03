/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.archivers.ArchiveEntry
 *  org.apache.commons.compress.archivers.zip.ZipArchiveEntry
 *  org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
 */
package org.apache.poi.openxml4j.opc.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.StreamHelper;
import org.apache.poi.openxml4j.opc.internal.ContentTypeManager;
import org.w3c.dom.Document;

public class ZipContentTypeManager
extends ContentTypeManager {
    private static final Logger LOG = LogManager.getLogger(ZipContentTypeManager.class);

    public ZipContentTypeManager(InputStream in, OPCPackage pkg) throws InvalidFormatException {
        super(in, pkg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean saveImpl(Document content, OutputStream out) {
        boolean bl;
        ZipArchiveOutputStream zos = out instanceof ZipArchiveOutputStream ? (ZipArchiveOutputStream)out : new ZipArchiveOutputStream(out);
        ZipArchiveEntry partEntry = new ZipArchiveEntry("[Content_Types].xml");
        zos.putArchiveEntry((ArchiveEntry)partEntry);
        try {
            bl = StreamHelper.saveXmlInStream(content, (OutputStream)zos);
        }
        catch (Throwable throwable) {
            try {
                zos.closeArchiveEntry();
                throw throwable;
            }
            catch (IOException ioe) {
                LOG.atError().withThrowable(ioe).log("Cannot write: [Content_Types].xml in Zip !");
                return false;
            }
        }
        zos.closeArchiveEntry();
        return bl;
    }
}

