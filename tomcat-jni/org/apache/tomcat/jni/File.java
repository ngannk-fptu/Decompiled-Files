/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import java.nio.ByteBuffer;
import org.apache.tomcat.jni.Error;
import org.apache.tomcat.jni.FileInfo;

@Deprecated
public class File {
    public static final int APR_FOPEN_READ = 1;
    public static final int APR_FOPEN_WRITE = 2;
    public static final int APR_FOPEN_CREATE = 4;
    public static final int APR_FOPEN_APPEND = 8;
    public static final int APR_FOPEN_TRUNCATE = 16;
    public static final int APR_FOPEN_BINARY = 32;
    public static final int APR_FOPEN_EXCL = 64;
    public static final int APR_FOPEN_BUFFERED = 128;
    public static final int APR_FOPEN_DELONCLOSE = 256;
    public static final int APR_FOPEN_XTHREAD = 512;
    public static final int APR_FOPEN_SHARELOCK = 1024;
    public static final int APR_FOPEN_NOCLEANUP = 2048;
    public static final int APR_FOPEN_SENDFILE_ENABLED = 4096;
    public static final int APR_FOPEN_LARGEFILE = 16384;
    public static final int APR_SET = 0;
    public static final int APR_CUR = 1;
    public static final int APR_END = 2;
    public static final int APR_FILE_ATTR_READONLY = 1;
    public static final int APR_FILE_ATTR_EXECUTABLE = 2;
    public static final int APR_FILE_ATTR_HIDDEN = 4;
    public static final int APR_FLOCK_SHARED = 1;
    public static final int APR_FLOCK_EXCLUSIVE = 2;
    public static final int APR_FLOCK_TYPEMASK = 15;
    public static final int APR_FLOCK_NONBLOCK = 16;
    public static final int APR_NOFILE = 0;
    public static final int APR_REG = 1;
    public static final int APR_DIR = 2;
    public static final int APR_CHR = 3;
    public static final int APR_BLK = 4;
    public static final int APR_PIPE = 5;
    public static final int APR_LNK = 6;
    public static final int APR_SOCK = 7;
    public static final int APR_UNKFILE = 127;
    public static final int APR_FPROT_USETID = 32768;
    public static final int APR_FPROT_UREAD = 1024;
    public static final int APR_FPROT_UWRITE = 512;
    public static final int APR_FPROT_UEXECUTE = 256;
    public static final int APR_FPROT_GSETID = 16384;
    public static final int APR_FPROT_GREAD = 64;
    public static final int APR_FPROT_GWRITE = 32;
    public static final int APR_FPROT_GEXECUTE = 16;
    public static final int APR_FPROT_WSTICKY = 8192;
    public static final int APR_FPROT_WREAD = 4;
    public static final int APR_FPROT_WWRITE = 2;
    public static final int APR_FPROT_WEXECUTE = 1;
    public static final int APR_FPROT_OS_DEFAULT = 4095;
    public static final int APR_FINFO_LINK = 1;
    public static final int APR_FINFO_MTIME = 16;
    public static final int APR_FINFO_CTIME = 32;
    public static final int APR_FINFO_ATIME = 64;
    public static final int APR_FINFO_SIZE = 256;
    public static final int APR_FINFO_CSIZE = 512;
    public static final int APR_FINFO_DEV = 4096;
    public static final int APR_FINFO_INODE = 8192;
    public static final int APR_FINFO_NLINK = 16384;
    public static final int APR_FINFO_TYPE = 32768;
    public static final int APR_FINFO_USER = 65536;
    public static final int APR_FINFO_GROUP = 131072;
    public static final int APR_FINFO_UPROT = 0x100000;
    public static final int APR_FINFO_GPROT = 0x200000;
    public static final int APR_FINFO_WPROT = 0x400000;
    public static final int APR_FINFO_ICASE = 0x1000000;
    public static final int APR_FINFO_NAME = 0x2000000;
    public static final int APR_FINFO_MIN = 33136;
    public static final int APR_FINFO_IDENT = 12288;
    public static final int APR_FINFO_OWNER = 196608;
    public static final int APR_FINFO_PROT = 0x700000;
    public static final int APR_FINFO_NORM = 7582064;
    public static final int APR_FINFO_DIRENT = 0x2000000;

    public static native long open(String var0, int var1, int var2, long var3) throws Error;

    public static native int close(long var0);

    public static native int flush(long var0);

    public static native long mktemp(String var0, int var1, long var2) throws Error;

    public static native int remove(String var0, long var1);

    public static native int rename(String var0, String var1, long var2);

    public static native int copy(String var0, String var1, int var2, long var3);

    public static native int append(String var0, String var1, int var2, long var3);

    public static native int puts(byte[] var0, long var1);

    public static native long seek(long var0, int var2, long var3) throws Error;

    public static native int putc(byte var0, long var1);

    public static native int ungetc(byte var0, long var1);

    public static native int write(long var0, byte[] var2, int var3, int var4);

    public static native int writeb(long var0, ByteBuffer var2, int var3, int var4);

    public static native int writeFull(long var0, byte[] var2, int var3, int var4);

    public static native int writeFullb(long var0, ByteBuffer var2, int var3, int var4);

    public static native int writev(long var0, byte[][] var2);

    public static native int writevFull(long var0, byte[][] var2);

    public static native int read(long var0, byte[] var2, int var3, int var4);

    public static native int readb(long var0, ByteBuffer var2, int var3, int var4);

    public static native int readFull(long var0, byte[] var2, int var3, int var4);

    public static native int readFullb(long var0, ByteBuffer var2, int var3, int var4);

    public static native int gets(byte[] var0, int var1, long var2);

    public static native int getc(long var0) throws Error;

    public static native int eof(long var0);

    public static native String nameGet(long var0);

    public static native int permsSet(String var0, int var1);

    public static native int attrsSet(String var0, int var1, int var2, long var3);

    public static native int mtimeSet(String var0, long var1, long var3);

    public static native int lock(long var0, int var2);

    public static native int unlock(long var0);

    public static native int flagsGet(long var0);

    public static native int trunc(long var0, long var2);

    public static native int pipeCreate(long[] var0, long var1);

    public static native long pipeTimeoutGet(long var0) throws Error;

    public static native int pipeTimeoutSet(long var0, long var2);

    public static native long dup(long var0, long var2, long var4) throws Error;

    public static native int dup2(long var0, long var2, long var4);

    public static native int stat(FileInfo var0, String var1, int var2, long var3);

    public static native FileInfo getStat(String var0, int var1, long var2);

    public static native int infoGet(FileInfo var0, int var1, long var2);

    public static native FileInfo getInfo(int var0, long var1);
}

