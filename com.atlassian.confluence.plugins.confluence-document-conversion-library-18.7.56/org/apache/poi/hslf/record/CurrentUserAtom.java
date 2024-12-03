/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hslf.exceptions.CorruptPowerPointFileException;
import org.apache.poi.hslf.exceptions.OldPowerPointFormatException;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

public class CurrentUserAtom {
    private static final Logger LOG = LogManager.getLogger(CurrentUserAtom.class);
    private static final byte[] atomHeader = new byte[]{0, 0, -10, 15};
    private static final byte[] headerToken = new byte[]{95, -64, -111, -29};
    private static final byte[] encHeaderToken = new byte[]{-33, -60, -47, -13};
    private int docFinalVersion;
    private byte docMajorNo;
    private byte docMinorNo;
    private long currentEditOffset;
    private String lastEditUser;
    private long releaseVersion;
    private byte[] _contents;
    private boolean isEncrypted;

    public int getDocFinalVersion() {
        return this.docFinalVersion;
    }

    public byte getDocMajorNo() {
        return this.docMajorNo;
    }

    public byte getDocMinorNo() {
        return this.docMinorNo;
    }

    public long getReleaseVersion() {
        return this.releaseVersion;
    }

    public void setReleaseVersion(long rv) {
        this.releaseVersion = rv;
    }

    public long getCurrentEditOffset() {
        return this.currentEditOffset;
    }

    public void setCurrentEditOffset(long id) {
        this.currentEditOffset = id;
    }

    public String getLastEditUsername() {
        return this.lastEditUser;
    }

    public void setLastEditUsername(String u) {
        this.lastEditUser = u;
    }

    public boolean isEncrypted() {
        return this.isEncrypted;
    }

    public void setEncrypted(boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public CurrentUserAtom() {
        this._contents = new byte[0];
        this.docFinalVersion = 1012;
        this.docMajorNo = (byte)3;
        this.docMinorNo = 0;
        this.releaseVersion = 8L;
        this.currentEditOffset = 0L;
        this.lastEditUser = "Apache POI";
        this.isEncrypted = false;
    }

    public CurrentUserAtom(DirectoryNode dir) throws IOException {
        DocumentEntry docProps = (DocumentEntry)dir.getEntry("Current User");
        if (docProps.getSize() > 131072) {
            throw new CorruptPowerPointFileException("The Current User stream is implausably long. It's normally 28-200 bytes long, but was " + docProps.getSize() + " bytes");
        }
        try (DocumentInputStream in = dir.createDocumentInputStream("Current User");){
            this._contents = IOUtils.toByteArray(in, docProps.getSize(), RecordAtom.getMaxRecordLength());
        }
        if (this._contents.length < 28) {
            boolean isPP95 = dir.hasEntry("PP40");
            if (!isPP95 && this._contents.length >= 4) {
                int size = LittleEndian.getInt(this._contents);
                boolean bl = isPP95 = size + 4 == this._contents.length;
            }
            if (isPP95) {
                throw new OldPowerPointFormatException("Based on the Current User stream, you seem to have supplied a PowerPoint95 file, which isn't supported");
            }
            throw new CorruptPowerPointFileException("The Current User stream must be at least 28 bytes long, but was only " + this._contents.length);
        }
        this.init();
    }

    private void init() {
        this.isEncrypted = LittleEndian.getInt(encHeaderToken) == LittleEndian.getInt(this._contents, 12);
        this.currentEditOffset = LittleEndian.getUInt(this._contents, 16);
        this.docFinalVersion = LittleEndian.getUShort(this._contents, 22);
        this.docMajorNo = this._contents[24];
        this.docMinorNo = this._contents[25];
        long usernameLen = LittleEndian.getUShort(this._contents, 20);
        if (usernameLen > 512L) {
            LOG.atWarn().log("Invalid username length {} found, treating as if there was no username set", (Object)Unbox.box(usernameLen));
            usernameLen = 0L;
        }
        this.releaseVersion = this._contents.length >= 28 + (int)usernameLen + 4 ? LittleEndian.getUInt(this._contents, 28 + (int)usernameLen) : 0L;
        int start = 28 + (int)usernameLen + 4;
        this.lastEditUser = (long)this._contents.length >= (long)start + 2L * usernameLen ? StringUtil.getFromUnicodeLE(this._contents, start, (int)usernameLen) : StringUtil.getFromCompressedUnicode(this._contents, 28, (int)usernameLen);
    }

    public void writeOut(OutputStream out) throws IOException {
        int size = 32 + 3 * this.lastEditUser.length();
        this._contents = IOUtils.safelyAllocate(size, RecordAtom.getMaxRecordLength());
        System.arraycopy(atomHeader, 0, this._contents, 0, 4);
        int atomSize = 24 + this.lastEditUser.length();
        LittleEndian.putInt(this._contents, 4, atomSize);
        LittleEndian.putInt(this._contents, 8, 20);
        System.arraycopy(this.isEncrypted ? encHeaderToken : headerToken, 0, this._contents, 12, 4);
        LittleEndian.putInt(this._contents, 16, (int)this.currentEditOffset);
        byte[] asciiUN = IOUtils.safelyAllocate(this.lastEditUser.length(), RecordAtom.getMaxRecordLength());
        StringUtil.putCompressedUnicode(this.lastEditUser, asciiUN, 0);
        LittleEndian.putShort(this._contents, 20, (short)asciiUN.length);
        LittleEndian.putShort(this._contents, 22, (short)this.docFinalVersion);
        this._contents[24] = this.docMajorNo;
        this._contents[25] = this.docMinorNo;
        this._contents[26] = 0;
        this._contents[27] = 0;
        System.arraycopy(asciiUN, 0, this._contents, 28, asciiUN.length);
        LittleEndian.putInt(this._contents, 28 + asciiUN.length, (int)this.releaseVersion);
        byte[] ucUN = IOUtils.safelyAllocate((long)this.lastEditUser.length() * 2L, RecordAtom.getMaxRecordLength());
        StringUtil.putUnicodeLE(this.lastEditUser, ucUN, 0);
        System.arraycopy(ucUN, 0, this._contents, 28 + asciiUN.length + 4, ucUN.length);
        out.write(this._contents);
    }

    public void writeToFS(POIFSFileSystem fs) throws IOException {
        try (UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream();){
            this.writeOut((OutputStream)baos);
            try (InputStream is = baos.toInputStream();){
                fs.createOrUpdateDocument(is, "Current User");
            }
        }
    }
}

