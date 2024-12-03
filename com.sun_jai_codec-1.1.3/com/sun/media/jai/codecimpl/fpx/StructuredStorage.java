/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl.fpx;

import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.SegmentedSeekableStream;
import com.sun.media.jai.codecimpl.fpx.FPXUtils;
import com.sun.media.jai.codecimpl.fpx.PropertySet;
import com.sun.media.jai.codecimpl.fpx.SSDirectoryEntry;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;

public class StructuredStorage {
    private static final long FAT_ENDOFCHAIN = 0xFFFFFFFEL;
    private static final long FAT_FREESECT = 0xFFFFFFFFL;
    SeekableStream file;
    private int sectorShift;
    private int miniSectorShift;
    private long csectFat;
    private long sectDirStart;
    private long miniSectorCutoff;
    private long sectMiniFatStart;
    private long csectMiniFat;
    private long sectDifStart;
    private long csectDif;
    private long[] sectFat;
    private long[] MINIFAT;
    private SSDirectoryEntry[] DIR;
    private SeekableStream miniStream;
    private SeekableStream FATStream;
    long cwdIndex = -1L;

    public StructuredStorage(SeekableStream file) throws IOException {
        this.file = file;
        this.getHeader();
        this.getFat();
        this.getMiniFat();
        this.getDirectory();
        this.getMiniStream();
    }

    private void getHeader() throws IOException {
        this.file.seek(30L);
        this.sectorShift = this.file.readUnsignedShortLE();
        this.file.seek(32L);
        this.miniSectorShift = this.file.readUnsignedShortLE();
        this.file.seek(44L);
        this.csectFat = this.file.readUnsignedIntLE();
        this.file.seek(48L);
        this.sectDirStart = this.file.readUnsignedIntLE();
        this.file.seek(56L);
        this.miniSectorCutoff = this.file.readUnsignedIntLE();
        this.file.seek(60L);
        this.sectMiniFatStart = this.file.readUnsignedIntLE();
        this.file.seek(64L);
        this.csectMiniFat = this.file.readUnsignedIntLE();
        this.file.seek(68L);
        this.sectDifStart = this.file.readUnsignedIntLE();
        this.file.seek(72L);
        this.csectDif = this.file.readUnsignedIntLE();
        this.sectFat = new long[109];
        this.file.seek(76L);
        for (int i = 0; i < 109; ++i) {
            this.sectFat[i] = this.file.readUnsignedIntLE();
        }
    }

    private void getFat() throws IOException {
        long sector;
        int size = this.getSectorSize();
        int sectsPerFat = size / 4;
        int fatsPerDif = size / 4 - 1;
        int numFATSectors = (int)(this.csectFat + this.csectDif * (long)fatsPerDif);
        long[] FATSectors = new long[numFATSectors];
        int count = 0;
        for (int i = 0; i < 109 && (sector = this.sectFat[i]) != 0xFFFFFFFFL; ++i) {
            FATSectors[count++] = this.getOffsetOfSector(this.sectFat[i]);
        }
        if (this.csectDif > 0L) {
            long dif = this.sectDifStart;
            byte[] difBuf = new byte[size];
            int i = 0;
            while ((long)i < this.csectDif) {
                this.readSector(dif, difBuf, 0);
                for (int j = 0; j < fatsPerDif; ++j) {
                    int sec = FPXUtils.getIntLE(difBuf, 4 * j);
                    FATSectors[count++] = this.getOffsetOfSector(sec);
                }
                dif = FPXUtils.getIntLE(difBuf, size - 4);
                ++i;
            }
        }
        this.FATStream = new SegmentedSeekableStream(this.file, FATSectors, size, numFATSectors * size, true);
    }

    private void getMiniFat() throws IOException {
        int size = this.getSectorSize();
        int sectsPerFat = size / 4;
        int index = 0;
        this.MINIFAT = new long[(int)(this.csectMiniFat * (long)sectsPerFat)];
        long sector = this.sectMiniFatStart;
        byte[] buf = new byte[size];
        while (sector != 0xFFFFFFFEL) {
            this.readSector(sector, buf, 0);
            for (int j = 0; j < sectsPerFat; ++j) {
                this.MINIFAT[index++] = FPXUtils.getIntLE(buf, 4 * j);
            }
            sector = this.getFATSector(sector);
        }
    }

    private void getDirectory() throws IOException {
        int size = this.getSectorSize();
        long sector = this.sectDirStart;
        int numDirectorySectors = 0;
        while (sector != 0xFFFFFFFEL) {
            sector = this.getFATSector(sector);
            ++numDirectorySectors;
        }
        int directoryEntries = 4 * numDirectorySectors;
        this.DIR = new SSDirectoryEntry[directoryEntries];
        sector = this.sectDirStart;
        byte[] buf = new byte[size];
        int index = 0;
        while (sector != 0xFFFFFFFEL) {
            this.readSector(sector, buf, 0);
            int offset = 0;
            for (int i = 0; i < 4; ++i) {
                short length = FPXUtils.getShortLE(buf, offset + 64);
                String name = FPXUtils.getString(buf, offset + 0, length);
                long SIDLeftSibling = FPXUtils.getUnsignedIntLE(buf, offset + 68);
                long SIDRightSibling = FPXUtils.getUnsignedIntLE(buf, offset + 72);
                long SIDChild = FPXUtils.getUnsignedIntLE(buf, offset + 76);
                long startSector = FPXUtils.getUnsignedIntLE(buf, offset + 116);
                long streamSize = FPXUtils.getUnsignedIntLE(buf, offset + 120);
                this.DIR[index] = new SSDirectoryEntry(index, name, streamSize, startSector, SIDLeftSibling, SIDRightSibling, SIDChild);
                ++index;
                offset += 128;
            }
            sector = this.getFATSector(sector);
        }
    }

    private void getMiniStream() throws IOException {
        int length = this.getLength(0L);
        int sectorSize = this.getSectorSize();
        int sectors = (length + sectorSize - 1) / sectorSize;
        long[] segmentPositions = new long[sectors];
        long sector = this.getStartSector(0L);
        for (int i = 0; i < sectors - 1; ++i) {
            segmentPositions[i] = this.getOffsetOfSector(sector);
            if ((sector = this.getFATSector(sector)) == 0xFFFFFFFEL) break;
        }
        segmentPositions[sectors - 1] = this.getOffsetOfSector(sector);
        this.miniStream = new SegmentedSeekableStream(this.file, segmentPositions, sectorSize, length, true);
    }

    private int getSectorSize() {
        return 1 << this.sectorShift;
    }

    private long getOffsetOfSector(long sector) {
        return sector * (long)this.getSectorSize() + 512L;
    }

    private int getMiniSectorSize() {
        return 1 << this.miniSectorShift;
    }

    private long getOffsetOfMiniSector(long sector) {
        return sector * (long)this.getMiniSectorSize();
    }

    private void readMiniSector(long sector, byte[] buf, int offset, int length) throws IOException {
        this.miniStream.seek(this.getOffsetOfMiniSector(sector));
        this.miniStream.read(buf, offset, length);
    }

    private void readMiniSector(long sector, byte[] buf, int offset) throws IOException {
        this.readMiniSector(sector, buf, offset, this.getMiniSectorSize());
    }

    private void readSector(long sector, byte[] buf, int offset, int length) throws IOException {
        this.file.seek(this.getOffsetOfSector(sector));
        this.file.read(buf, offset, length);
    }

    private void readSector(long sector, byte[] buf, int offset) throws IOException {
        this.readSector(sector, buf, offset, this.getSectorSize());
    }

    private SSDirectoryEntry getDirectoryEntry(long index) {
        return this.DIR[(int)index];
    }

    private long getStartSector(long index) {
        return this.DIR[(int)index].getStartSector();
    }

    private int getLength(long index) {
        return (int)this.DIR[(int)index].getSize();
    }

    private long getFATSector(long sector) throws IOException {
        this.FATStream.seek(4L * sector);
        return this.FATStream.readUnsignedIntLE();
    }

    private long getMiniFATSector(long sector) {
        return this.MINIFAT[(int)sector];
    }

    private int getCurrentIndex() {
        return -1;
    }

    private int getIndex(String name, int index) {
        return -1;
    }

    private long searchDirectory(String name, long index) {
        if (index == 0xFFFFFFFFL) {
            return -1L;
        }
        SSDirectoryEntry dirent = this.getDirectoryEntry(index);
        if (name.equals(dirent.getName())) {
            return index;
        }
        long lindex = this.searchDirectory(name, dirent.getSIDLeftSibling());
        if (lindex != -1L) {
            return lindex;
        }
        long rindex = this.searchDirectory(name, dirent.getSIDRightSibling());
        if (rindex != -1L) {
            return rindex;
        }
        return -1L;
    }

    public void changeDirectoryToRoot() {
        this.cwdIndex = this.getDirectoryEntry(0L).getSIDChild();
    }

    public boolean changeDirectory(String name) {
        long index = this.searchDirectory(name, this.cwdIndex);
        if (index != -1L) {
            this.cwdIndex = this.getDirectoryEntry(index).getSIDChild();
            return true;
        }
        return false;
    }

    private long getStreamIndex(String name) {
        long index = this.cwdIndex;
        StringTokenizer st = new StringTokenizer(name, "/");
        boolean firstTime = true;
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if (!firstTime) {
                index = this.getDirectoryEntry(index).getSIDChild();
            } else {
                firstTime = false;
            }
            index = this.searchDirectory(tok, index);
        }
        return index;
    }

    public byte[] getStreamAsBytes(String name) throws IOException {
        long index = this.getStreamIndex(name);
        if (index == -1L) {
            return null;
        }
        int length = this.getLength(index);
        byte[] buf = new byte[length];
        if ((long)length > this.miniSectorCutoff) {
            int sectorSize = this.getSectorSize();
            int sectors = (length + sectorSize - 1) / sectorSize;
            long sector = this.getStartSector(index);
            int offset = 0;
            for (int i = 0; i < sectors - 1; ++i) {
                this.readSector(sector, buf, offset, sectorSize);
                offset += sectorSize;
                sector = this.getFATSector(sector);
                if (sector == 0xFFFFFFFEL) break;
            }
            this.readSector(sector, buf, offset, length - offset);
        } else {
            int sectorSize = this.getMiniSectorSize();
            int sectors = (length + sectorSize - 1) / sectorSize;
            long sector = this.getStartSector(index);
            int offset = 0;
            for (int i = 0; i < sectors - 1; ++i) {
                long miniSectorOffset = this.getOffsetOfMiniSector(sector);
                this.readMiniSector(sector, buf, offset, sectorSize);
                offset += sectorSize;
                sector = this.getMiniFATSector(sector);
            }
            this.readMiniSector(sector, buf, offset, length - offset);
        }
        return buf;
    }

    public SeekableStream getStream(String name) throws IOException {
        long index = this.getStreamIndex(name);
        if (index == -1L) {
            return null;
        }
        int length = this.getLength(index);
        if ((long)length > this.miniSectorCutoff) {
            int sectorSize = this.getSectorSize();
            int sectors = (length + sectorSize - 1) / sectorSize;
            long[] segmentPositions = new long[sectors];
            long sector = this.getStartSector(index);
            for (int i = 0; i < sectors - 1; ++i) {
                segmentPositions[i] = this.getOffsetOfSector(sector);
                if ((sector = this.getFATSector(sector)) == 0xFFFFFFFEL) break;
            }
            segmentPositions[sectors - 1] = this.getOffsetOfSector(sector);
            return new SegmentedSeekableStream(this.file, segmentPositions, sectorSize, length, true);
        }
        int sectorSize = this.getMiniSectorSize();
        int sectors = (length + sectorSize - 1) / sectorSize;
        long[] segmentPositions = new long[sectors];
        long sector = this.getStartSector(index);
        for (int i = 0; i < sectors - 1; ++i) {
            segmentPositions[i] = this.getOffsetOfMiniSector(sector);
            sector = this.getMiniFATSector(sector);
        }
        segmentPositions[sectors - 1] = this.getOffsetOfMiniSector(sector);
        return new SegmentedSeekableStream(this.miniStream, segmentPositions, sectorSize, length, true);
    }

    public static void main(String[] args) {
        try {
            RandomAccessFile f = new RandomAccessFile(args[0], "r");
            FileSeekableStream sis = new FileSeekableStream(f);
            StructuredStorage ss = new StructuredStorage(sis);
            ss.changeDirectoryToRoot();
            byte[] s = ss.getStreamAsBytes("\u0005SummaryInformation");
            PropertySet ps = new PropertySet(new ByteArraySeekableStream(s));
            byte[] thumb = ps.getBlob(17);
            System.out.print("BM");
            int fs = thumb.length - 8 + 14 + 40;
            System.out.print((char)(fs & 0xFF));
            System.out.print((char)(fs >> 8 & 0xFF));
            System.out.print((char)(fs >> 16 & 0xFF));
            System.out.print((char)(fs >> 24 & 0xFF));
            System.out.print('\u0000');
            System.out.print('\u0000');
            System.out.print('\u0000');
            System.out.print('\u0000');
            System.out.print('6');
            System.out.print('\u0000');
            System.out.print('\u0000');
            System.out.print('\u0000');
            for (int i = 8; i < thumb.length; ++i) {
                System.out.print((char)(thumb[i] & 0xFF));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

