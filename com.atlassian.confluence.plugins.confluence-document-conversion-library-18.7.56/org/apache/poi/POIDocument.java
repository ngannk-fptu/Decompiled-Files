/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.WritingNotSupportedException;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIDecryptor;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptor;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;

public abstract class POIDocument
implements Closeable {
    private SummaryInformation sInf;
    private DocumentSummaryInformation dsInf;
    private DirectoryNode directory;
    private static final Logger LOG = LogManager.getLogger(POIDocument.class);
    private boolean initialized;

    protected POIDocument(DirectoryNode dir) {
        this.directory = dir;
    }

    protected POIDocument(POIFSFileSystem fs) {
        this(fs.getRoot());
    }

    public DocumentSummaryInformation getDocumentSummaryInformation() {
        if (!this.initialized) {
            this.readProperties();
        }
        return this.dsInf;
    }

    public SummaryInformation getSummaryInformation() {
        if (!this.initialized) {
            this.readProperties();
        }
        return this.sInf;
    }

    public void createInformationProperties() {
        if (!this.initialized) {
            this.readProperties();
        }
        if (this.sInf == null) {
            this.sInf = PropertySetFactory.newSummaryInformation();
        }
        if (this.dsInf == null) {
            this.dsInf = PropertySetFactory.newDocumentSummaryInformation();
        }
    }

    @Internal
    public void readProperties() {
        SummaryInformation si;
        if (this.initialized) {
            return;
        }
        DocumentSummaryInformation dsi = this.readPropertySet(DocumentSummaryInformation.class, "\u0005DocumentSummaryInformation");
        if (dsi != null) {
            this.dsInf = dsi;
        }
        if ((si = this.readPropertySet(SummaryInformation.class, "\u0005SummaryInformation")) != null) {
            this.sInf = si;
        }
        this.initialized = true;
    }

    private <T> T readPropertySet(Class<T> clazz, String name) {
        String localName = clazz.getName().substring(clazz.getName().lastIndexOf(46) + 1);
        try {
            PropertySet ps = this.getPropertySet(name);
            if (clazz.isInstance(ps)) {
                return (T)ps;
            }
            if (ps != null) {
                LOG.atWarn().log("{} property set came back with wrong class - {}", (Object)localName, (Object)ps.getClass().getName());
            } else {
                LOG.atWarn().log("{} property set came back as null", (Object)localName);
            }
        }
        catch (IOException e) {
            LOG.atError().withThrowable(e).log("can't retrieve property set");
        }
        return null;
    }

    protected PropertySet getPropertySet(String setName) throws IOException {
        return this.getPropertySet(setName, this.getEncryptionInfo());
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected PropertySet getPropertySet(String setName, EncryptionInfo encryptionInfo) throws IOException {
        dirNode = this.directory;
        encPoifs = null;
        step = "getting";
        if (encryptionInfo != null && encryptionInfo.isDocPropsEncrypted()) {
            step = "getting encrypted";
            encryptedStream = this.getEncryptedPropertyStreamName();
            if (!dirNode.hasEntry(encryptedStream)) {
                throw new EncryptedDocumentException("can't find encrypted property stream '" + encryptedStream + "'");
            }
            dec = (CryptoAPIDecryptor)encryptionInfo.getDecryptor();
            encPoifs = dec.getSummaryEntries(dirNode, encryptedStream);
            dirNode = encPoifs.getRoot();
        }
        if (dirNode == null || !dirNode.hasEntry(setName)) {
            encryptedStream = null;
        }
        ** GOTO lbl26
        {
            catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                throw new IOException("Error " + step + " property set with name " + setName, e);
            }
            catch (Throwable var12_15) {
                IOUtils.closeQuietly(encPoifs);
                throw var12_15;
            }
            IOUtils.closeQuietly(encPoifs);
            return encryptedStream;
lbl26:
            // 1 sources

            step = "getting";
            dis = dirNode.createDocumentInputStream(dirNode.getEntry(setName));
            var7_9 = null;
            try {
                step = "creating";
                var8_10 = PropertySetFactory.create(dis);
                IOUtils.closeQuietly(encPoifs);
                return var8_10;
            }
            catch (Throwable var8_11) {}
            {
                var7_9 = var8_11;
                throw var8_11;
            }
        }
        finally {
            if (dis != null) {
                if (var7_9 != null) {
                    try {
                        dis.close();
                    }
                    catch (Throwable var9_12) {
                        var7_9.addSuppressed(var9_12);
                    }
                } else {
                    dis.close();
                }
            }
        }
    }

    protected void writeProperties() throws IOException {
        this.validateInPlaceWritePossible();
        this.writeProperties(this.directory.getFileSystem(), null);
    }

    @Internal
    public void writeProperties(POIFSFileSystem outFS) throws IOException {
        this.writeProperties(outFS, null);
    }

    protected void writeProperties(POIFSFileSystem outFS, List<String> writtenEntries) throws IOException {
        EncryptionInfo ei = this.getEncryptionInfo();
        Encryptor encGen = ei == null ? null : ei.getEncryptor();
        boolean encryptProps = ei != null && ei.isDocPropsEncrypted() && encGen instanceof CryptoAPIEncryptor;
        try (POIFSFileSystem tmpFS = new POIFSFileSystem();){
            POIFSFileSystem fs = encryptProps ? tmpFS : outFS;
            this.writePropertySet("\u0005SummaryInformation", this.getSummaryInformation(), fs, writtenEntries);
            this.writePropertySet("\u0005DocumentSummaryInformation", this.getDocumentSummaryInformation(), fs, writtenEntries);
            if (!encryptProps) {
                return;
            }
            this.writePropertySet("\u0005DocumentSummaryInformation", PropertySetFactory.newDocumentSummaryInformation(), outFS);
            if (outFS.getRoot().hasEntry("\u0005SummaryInformation")) {
                outFS.getRoot().getEntry("\u0005SummaryInformation").delete();
            }
            CryptoAPIEncryptor enc = (CryptoAPIEncryptor)encGen;
            try {
                enc.setSummaryEntries(outFS.getRoot(), this.getEncryptedPropertyStreamName(), fs);
            }
            catch (GeneralSecurityException e) {
                throw new IOException(e);
            }
        }
    }

    private void writePropertySet(String name, PropertySet ps, POIFSFileSystem outFS, List<String> writtenEntries) throws IOException {
        if (ps == null) {
            return;
        }
        this.writePropertySet(name, ps, outFS);
        if (writtenEntries != null) {
            writtenEntries.add(name);
        }
    }

    private void writePropertySet(String name, PropertySet set, POIFSFileSystem outFS) throws IOException {
        try (UnsynchronizedByteArrayOutputStream bOut = new UnsynchronizedByteArrayOutputStream();){
            PropertySet mSet = new PropertySet(set);
            mSet.write((OutputStream)bOut);
            try (InputStream bIn = bOut.toInputStream();){
                outFS.createOrUpdateDocument(bIn, name);
            }
            LOG.atInfo().log("Wrote property set {} of size {}", (Object)name, (Object)Unbox.box(bOut.size()));
        }
        catch (WritingNotSupportedException ignored) {
            LOG.atError().log("Couldn't write property set with name {} as not supported by HPSF yet", (Object)name);
        }
    }

    protected void validateInPlaceWritePossible() throws IllegalStateException {
        if (this.directory == null) {
            throw new IllegalStateException("Newly created Document, cannot save in-place");
        }
        if (this.directory.getParent() != null) {
            throw new IllegalStateException("This is not the root Document, cannot save embedded resource in-place");
        }
        if (this.directory.getFileSystem() == null || !this.directory.getFileSystem().isInPlaceWriteable()) {
            throw new IllegalStateException("Opened read-only or via an InputStream, a Writeable File is required");
        }
    }

    public abstract void write() throws IOException;

    public abstract void write(File var1) throws IOException;

    public abstract void write(OutputStream var1) throws IOException;

    @Override
    public void close() throws IOException {
        if (this.directory != null && this.directory.getFileSystem() != null) {
            this.directory.getFileSystem().close();
            this.clearDirectory();
        }
    }

    @Internal
    public DirectoryNode getDirectory() {
        return this.directory;
    }

    @Internal
    protected void clearDirectory() {
        this.directory = null;
    }

    @Internal
    protected boolean initDirectory() {
        if (this.directory == null) {
            this.directory = new POIFSFileSystem().getRoot();
            return true;
        }
        return false;
    }

    @Internal
    protected void replaceDirectory(DirectoryNode newDirectory) throws IOException {
        if (newDirectory == this.directory || newDirectory != null && this.directory != null && newDirectory.getFileSystem() == this.directory.getFileSystem()) {
            return;
        }
        if (this.directory != null && this.directory.getFileSystem() != null) {
            this.directory.getFileSystem().close();
        }
        this.directory = newDirectory;
    }

    protected String getEncryptedPropertyStreamName() {
        return "encryption";
    }

    public EncryptionInfo getEncryptionInfo() throws IOException {
        return null;
    }
}

