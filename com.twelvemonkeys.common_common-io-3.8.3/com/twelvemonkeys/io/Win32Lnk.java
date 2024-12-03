/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.LittleEndianDataInputStream;
import com.twelvemonkeys.io.Win32File;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

final class Win32Lnk
extends File {
    private static final byte[] LNK_MAGIC = new byte[]{76, 0, 0, 0};
    private static final byte[] LNK_GUID = new byte[]{1, 20, 2, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 70};
    private final File target;
    private static final int FLAG_ITEM_ID_LIST = 1;
    private static final int FLAG_FILE_LOC_INFO = 2;
    private static final int FLAG_DESC_STRING = 4;
    private static final int FLAG_REL_PATH_STRING = 8;
    private static final int FLAG_WORKING_DIRECTORY = 16;
    private static final int FLAG_COMMAND_LINE_ARGS = 32;
    private static final int FLAG_ICON_FILENAME = 64;
    private static final int FLAG_ADDITIONAL_INFO = 128;

    private Win32Lnk(String string) throws IOException {
        super(string);
        File file = Win32Lnk.parse(this);
        if (file == this) {
            file = new File(string);
        }
        this.target = file;
    }

    Win32Lnk(File file) throws IOException {
        this(file.getPath());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static File parse(File file) throws IOException {
        if (!file.getName().endsWith(".lnk")) {
            return file;
        }
        File file2 = file;
        try (LittleEndianDataInputStream littleEndianDataInputStream = new LittleEndianDataInputStream(new BufferedInputStream(new FileInputStream(file)));){
            int n;
            byte[] byArray = new byte[4];
            littleEndianDataInputStream.readFully(byArray);
            byte[] byArray2 = new byte[16];
            littleEndianDataInputStream.readFully(byArray2);
            if (!Arrays.equals(LNK_MAGIC, byArray) || !Arrays.equals(LNK_GUID, byArray2)) {
                File file3 = file;
                return file3;
            }
            int n2 = littleEndianDataInputStream.readInt();
            littleEndianDataInputStream.readInt();
            littleEndianDataInputStream.skipBytes(48);
            if ((n2 & 1) != 0) {
                n = littleEndianDataInputStream.readShort();
                littleEndianDataInputStream.skipBytes(n);
            }
            if ((n2 & 2) != 0) {
                n = littleEndianDataInputStream.readInt();
                littleEndianDataInputStream.readInt();
                int n3 = littleEndianDataInputStream.readInt();
                if ((n3 & 1) != 0) {
                    // empty if block
                }
                if ((n3 & 2) != 0) {
                    // empty if block
                }
                littleEndianDataInputStream.skipBytes(4);
                int n4 = littleEndianDataInputStream.readInt();
                littleEndianDataInputStream.skipBytes(n4 - 20);
                byte[] byArray3 = new byte[n - n4 - 1];
                littleEndianDataInputStream.readFully(byArray3, 0, byArray3.length);
                String string = new String(byArray3, 0, byArray3.length - 1);
                try {
                    file2 = Win32Lnk.parse(new File(string));
                }
                catch (StackOverflowError stackOverflowError) {
                    throw new IOException("Cannot resolve cyclic link: " + stackOverflowError.getMessage());
                }
            }
            if ((n2 & 4) != 0) {
                n = littleEndianDataInputStream.readShort();
                byte[] byArray4 = new byte[n];
                littleEndianDataInputStream.readFully(byArray4, 0, n);
            }
            if ((n2 & 8) != 0) {
                n = littleEndianDataInputStream.readShort();
                byte[] byArray5 = new byte[n];
                littleEndianDataInputStream.readFully(byArray5, 0, n);
                String string = new String(byArray5, 0, n);
                if (file2 == file) {
                    try {
                        file2 = Win32Lnk.parse(new File(file.getParentFile(), string));
                    }
                    catch (StackOverflowError stackOverflowError) {
                        throw new IOException("Cannot resolve cyclic link: " + stackOverflowError.getMessage());
                    }
                }
            }
            if ((n2 & 0x10) != 0) {
                // empty if block
            }
            if ((n2 & 0x20) != 0) {
                file2 = file;
            }
            if ((n2 & 0x40) != 0) {
                // empty if block
            }
            if ((n2 & 0x80) != 0) {
                // empty if block
            }
        }
        return file2;
    }

    public File getTarget() {
        return this.target;
    }

    @Override
    public boolean isDirectory() {
        return this.target.isDirectory();
    }

    @Override
    public boolean canRead() {
        return this.target.canRead();
    }

    @Override
    public boolean canWrite() {
        return this.target.canWrite();
    }

    @Override
    public boolean exists() {
        return this.target.exists();
    }

    @Override
    public File getCanonicalFile() throws IOException {
        return this.target.getCanonicalFile();
    }

    @Override
    public String getCanonicalPath() throws IOException {
        return this.target.getCanonicalPath();
    }

    @Override
    public boolean isFile() {
        return this.target.isFile();
    }

    @Override
    public boolean isHidden() {
        return this.target.isHidden();
    }

    @Override
    public long lastModified() {
        return this.target.lastModified();
    }

    @Override
    public long length() {
        return this.target.length();
    }

    @Override
    public String[] list() {
        return this.target.list();
    }

    @Override
    public String[] list(FilenameFilter filenameFilter) {
        return this.target.list(filenameFilter);
    }

    @Override
    public File[] listFiles() {
        return Win32File.wrap(this.target.listFiles());
    }

    @Override
    public File[] listFiles(FileFilter fileFilter) {
        return Win32File.wrap(this.target.listFiles(fileFilter));
    }

    @Override
    public File[] listFiles(FilenameFilter filenameFilter) {
        return Win32File.wrap(this.target.listFiles(filenameFilter));
    }

    @Override
    public boolean setLastModified(long l) {
        return this.target.setLastModified(l);
    }

    @Override
    public boolean setReadOnly() {
        return this.target.setReadOnly();
    }

    @Override
    public String toString() {
        if (this.target.equals(this)) {
            return super.toString();
        }
        return super.toString() + " -> " + this.target.toString();
    }
}

