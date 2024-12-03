/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hemf.usermodel;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.imageio.ImageIO;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hemf.record.emf.HemfComment;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emfplus.HemfPlusImage;
import org.apache.poi.hemf.record.emfplus.HemfPlusObject;
import org.apache.poi.hemf.usermodel.HemfPicture;
import org.apache.poi.hwmf.record.HwmfBitmapDib;
import org.apache.poi.hwmf.record.HwmfFill;
import org.apache.poi.hwmf.usermodel.HwmfEmbedded;
import org.apache.poi.hwmf.usermodel.HwmfEmbeddedType;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.util.IOUtils;

public class HemfEmbeddedIterator
implements Iterator<HwmfEmbedded> {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 100000000;
    private static int MAX_RECORD_LENGTH = 100000000;
    private final Deque<Iterator<?>> iterStack = new ArrayDeque();
    private Object current;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public HemfEmbeddedIterator(HemfPicture emf) {
        this(emf.getRecords().iterator());
    }

    public HemfEmbeddedIterator(Iterator<HemfRecord> recordIterator) {
        this.iterStack.add(recordIterator);
    }

    @Override
    public boolean hasNext() {
        return this.moveNext();
    }

    private boolean moveNext() {
        if (this.iterStack.isEmpty()) {
            return false;
        }
        if (this.current != null) {
            return true;
        }
        do {
            Iterator<Object> iter = this.iterStack.peek();
            while (iter.hasNext()) {
                HwmfBitmapDib bitmap;
                Object obj = iter.next();
                if (obj instanceof HemfComment.EmfComment) {
                    Iterator<GenericRecord> iter2;
                    HemfComment.EmfCommentData cd = ((HemfComment.EmfComment)obj).getCommentData();
                    if (cd instanceof HemfComment.EmfCommentDataWMF || cd instanceof HemfComment.EmfCommentDataGeneric) {
                        this.current = obj;
                        return true;
                    }
                    if (cd instanceof HemfComment.EmfCommentDataMultiformats && (iter2 = ((HemfComment.EmfCommentDataMultiformats)cd).getFormats().iterator()).hasNext()) {
                        this.iterStack.push(iter2);
                        continue;
                    }
                    if (cd instanceof HemfComment.EmfCommentDataPlus && (iter2 = ((HemfComment.EmfCommentDataPlus)cd).getRecords().iterator()).hasNext()) {
                        iter = iter2;
                        this.iterStack.push(iter2);
                        continue;
                    }
                }
                if (obj instanceof HemfComment.EmfCommentDataFormat) {
                    this.current = obj;
                    return true;
                }
                if (obj instanceof HemfPlusObject.EmfPlusObject && ((HemfPlusObject.EmfPlusObject)obj).getObjectType() == HemfPlusObject.EmfPlusObjectType.IMAGE) {
                    this.current = obj;
                    return true;
                }
                if (!(obj instanceof HwmfFill.WmfStretchDib) || !(bitmap = ((HwmfFill.WmfStretchDib)obj).getBitmap()).isValid()) continue;
                this.current = obj;
                return true;
            }
            this.iterStack.pop();
        } while (!this.iterStack.isEmpty());
        return false;
    }

    @Override
    public HwmfEmbedded next() {
        HwmfEmbedded emb = this.checkEmfCommentDataWMF();
        if (emb != null) {
            return emb;
        }
        emb = this.checkEmfCommentDataGeneric();
        if (emb != null) {
            return emb;
        }
        emb = this.checkEmfCommentDataFormat();
        if (emb != null) {
            return emb;
        }
        emb = this.checkEmfPlusObject();
        if (emb != null) {
            return emb;
        }
        emb = this.checkWmfStretchDib();
        if (emb != null) {
            return emb;
        }
        throw new NoSuchElementException("no further embedded wmf records found.");
    }

    private HwmfEmbedded checkEmfCommentDataWMF() {
        if (!(this.current instanceof HemfComment.EmfComment) || !(((HemfComment.EmfComment)this.current).getCommentData() instanceof HemfComment.EmfCommentDataWMF)) {
            return null;
        }
        HemfComment.EmfCommentDataWMF wmf = (HemfComment.EmfCommentDataWMF)((HemfComment.EmfComment)this.current).getCommentData();
        HwmfEmbedded emb = new HwmfEmbedded();
        emb.setEmbeddedType(HwmfEmbeddedType.WMF);
        emb.setData(wmf.getWMFData());
        this.current = null;
        return emb;
    }

    private HwmfEmbedded checkEmfCommentDataGeneric() {
        if (!(this.current instanceof HemfComment.EmfComment) || !(((HemfComment.EmfComment)this.current).getCommentData() instanceof HemfComment.EmfCommentDataGeneric)) {
            return null;
        }
        HemfComment.EmfCommentDataGeneric cdg = (HemfComment.EmfCommentDataGeneric)((HemfComment.EmfComment)this.current).getCommentData();
        HwmfEmbedded emb = new HwmfEmbedded();
        emb.setEmbeddedType(HwmfEmbeddedType.UNKNOWN);
        emb.setData(cdg.getPrivateData());
        this.current = null;
        return emb;
    }

    private HwmfEmbedded checkEmfCommentDataFormat() {
        if (!(this.current instanceof HemfComment.EmfCommentDataFormat)) {
            return null;
        }
        HemfComment.EmfCommentDataFormat cdf = (HemfComment.EmfCommentDataFormat)this.current;
        HwmfEmbedded emb = new HwmfEmbedded();
        boolean isEmf = cdf.getSignature() == HemfComment.EmfFormatSignature.ENHMETA_SIGNATURE;
        emb.setEmbeddedType(isEmf ? HwmfEmbeddedType.EMF : HwmfEmbeddedType.EPS);
        emb.setData(cdf.getRawData());
        this.current = null;
        return emb;
    }

    private HwmfEmbedded checkWmfStretchDib() {
        if (!(this.current instanceof HwmfFill.WmfStretchDib)) {
            return null;
        }
        HwmfEmbedded emb = new HwmfEmbedded();
        emb.setData(((HwmfFill.WmfStretchDib)this.current).getBitmap().getBMPData());
        emb.setEmbeddedType(HwmfEmbeddedType.BMP);
        this.current = null;
        return emb;
    }

    private HwmfEmbedded checkEmfPlusObject() {
        HwmfEmbeddedType et;
        if (!(this.current instanceof HemfPlusObject.EmfPlusObject)) {
            return null;
        }
        HemfPlusObject.EmfPlusObject epo = (HemfPlusObject.EmfPlusObject)this.current;
        assert (epo.getObjectType() == HemfPlusObject.EmfPlusObjectType.IMAGE);
        HemfPlusImage.EmfPlusImage img = (HemfPlusImage.EmfPlusImage)epo.getObjectData();
        assert (img.getImageDataType() != null);
        HwmfEmbedded emb = this.getEmfPlusImageData();
        if (emb == null) {
            return null;
        }
        block0 : switch (img.getImageDataType()) {
            case BITMAP: {
                if (img.getBitmapType() == HemfPlusImage.EmfPlusBitmapDataType.COMPRESSED) {
                    switch (FileMagic.valueOf(emb.getRawData())) {
                        case JPEG: {
                            et = HwmfEmbeddedType.JPEG;
                            break block0;
                        }
                        case GIF: {
                            et = HwmfEmbeddedType.GIF;
                            break block0;
                        }
                        case PNG: {
                            et = HwmfEmbeddedType.PNG;
                            break block0;
                        }
                        case TIFF: {
                            et = HwmfEmbeddedType.TIFF;
                            break block0;
                        }
                    }
                    et = HwmfEmbeddedType.BITMAP;
                    break;
                }
                et = HwmfEmbeddedType.PNG;
                this.compressGDIBitmap(img, emb, et);
                break;
            }
            case METAFILE: {
                assert (img.getMetafileType() != null);
                switch (img.getMetafileType()) {
                    case Wmf: 
                    case WmfPlaceable: {
                        et = HwmfEmbeddedType.WMF;
                        break block0;
                    }
                    case Emf: 
                    case EmfPlusDual: 
                    case EmfPlusOnly: {
                        et = HwmfEmbeddedType.EMF;
                        break block0;
                    }
                }
                et = HwmfEmbeddedType.UNKNOWN;
                break;
            }
            default: {
                et = HwmfEmbeddedType.UNKNOWN;
            }
        }
        emb.setEmbeddedType(et);
        return emb;
    }

    private void compressGDIBitmap(HemfPlusImage.EmfPlusImage img, HwmfEmbedded emb, HwmfEmbeddedType et) {
        BufferedImage bi = img.readGDIImage(emb.getRawData());
        try {
            UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();
            ImageIO.write((RenderedImage)bi, et.toString(), (OutputStream)bos);
            emb.setData(bos.toByteArray());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private HwmfEmbedded getEmfPlusImageData() {
        HemfPlusObject.EmfPlusObject epo = (HemfPlusObject.EmfPlusObject)this.current;
        assert (epo.getObjectType() == HemfPlusObject.EmfPlusObjectType.IMAGE);
        int objectId = epo.getObjectId();
        HwmfEmbedded emb = new HwmfEmbedded();
        int totalSize = epo.getTotalObjectSize() == 0 ? ((HemfPlusImage.EmfPlusImage)epo.getObjectData()).getImageData().length : epo.getTotalObjectSize();
        IOUtils.safelyAllocateCheck(totalSize, MAX_RECORD_LENGTH);
        try (UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream(totalSize);){
            boolean hasNext = false;
            do {
                HemfPlusImage.EmfPlusImage img = (HemfPlusImage.EmfPlusImage)epo.getObjectData();
                assert (img.getImageDataType() != null);
                assert (!hasNext || img.getImageDataType() == HemfPlusImage.EmfPlusImageDataType.CONTINUED);
                bos.write(img.getImageData());
                this.current = null;
            } while (hasNext = this.moveNext() && this.current instanceof HemfPlusObject.EmfPlusObject && (epo = (HemfPlusObject.EmfPlusObject)this.current).getObjectId() == objectId && bos.size() < totalSize - 16);
            emb.setData(bos.toByteArray());
            HwmfEmbedded hwmfEmbedded = emb;
            return hwmfEmbedded;
        }
        catch (IOException ignored) {
            return null;
        }
    }
}

