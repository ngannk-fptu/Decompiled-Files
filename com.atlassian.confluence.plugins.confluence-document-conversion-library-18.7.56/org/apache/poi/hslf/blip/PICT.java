/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.blip;

import java.awt.Dimension;
import java.io.IOException;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.hslf.blip.Metafile;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.sl.image.ImageHeaderPICT;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Removal;
import org.apache.poi.util.Units;

public final class PICT
extends Metafile {
    private static final Logger LOG = LogManager.getLogger(PICT.class);

    @Deprecated
    @Removal(version="5.3")
    public PICT() {
        this(new EscherContainerRecord(), new EscherBSERecord());
    }

    @Internal
    public PICT(EscherContainerRecord recordContainer, EscherBSERecord bse) {
        super(recordContainer, bse);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public byte[] getData() {
        byte[] rawdata = this.getRawData();
        try (UnsynchronizedByteArrayOutputStream out = new UnsynchronizedByteArrayOutputStream();){
            byte[] macheader = new byte[512];
            out.write(macheader);
            int pos = 16 * this.getUIDInstanceCount();
            byte[] pict = this.read(rawdata, pos);
            out.write(pict);
            byte[] byArray = out.toByteArray();
            return byArray;
        }
        catch (IOException e) {
            throw new HSLFException(e);
        }
    }

    /*
     * Exception decompiling
     */
    private byte[] read(byte[] data, int pos) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    protected byte[] formatImageForSlideshow(byte[] data) {
        int nOffset = 512;
        ImageHeaderPICT nHeader = new ImageHeaderPICT(data, 512);
        Metafile.Header header = new Metafile.Header();
        int wmfSize = data.length - 512;
        header.setWmfSize(wmfSize);
        byte[] compressed = PICT.compress(data, 512, wmfSize);
        header.setZipSize(compressed.length);
        header.setBounds(nHeader.getBounds());
        Dimension nDim = nHeader.getSize();
        header.setDimension(new Dimension(Units.toEMU(nDim.getWidth()), Units.toEMU(nDim.getHeight())));
        byte[] checksum = PICT.getChecksum(data);
        byte[] rawData = new byte[checksum.length * this.getUIDInstanceCount() + header.getSize() + compressed.length];
        int offset = 0;
        System.arraycopy(checksum, 0, rawData, offset, checksum.length);
        offset += checksum.length;
        if (this.getUIDInstanceCount() == 2) {
            System.arraycopy(checksum, 0, rawData, offset, checksum.length);
            offset += checksum.length;
        }
        header.write(rawData, offset);
        System.arraycopy(compressed, 0, rawData, offset += header.getSize(), compressed.length);
        return rawData;
    }

    @Override
    public PictureData.PictureType getType() {
        return PictureData.PictureType.PICT;
    }

    @Override
    public int getSignature() {
        return this.getUIDInstanceCount() == 1 ? 21536 : 21552;
    }

    @Override
    public void setSignature(int signature) {
        switch (signature) {
            case 21536: {
                this.setUIDInstanceCount(1);
                break;
            }
            case 21552: {
                this.setUIDInstanceCount(2);
                break;
            }
            default: {
                throw new IllegalArgumentException(signature + " is not a valid instance/signature value for PICT");
            }
        }
    }
}

