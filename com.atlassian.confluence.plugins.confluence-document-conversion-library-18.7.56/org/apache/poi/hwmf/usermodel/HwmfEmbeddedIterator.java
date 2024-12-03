/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hwmf.usermodel;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.hwmf.record.HwmfEscape;
import org.apache.poi.hwmf.record.HwmfFill;
import org.apache.poi.hwmf.record.HwmfRecord;
import org.apache.poi.hwmf.usermodel.HwmfEmbedded;
import org.apache.poi.hwmf.usermodel.HwmfEmbeddedType;
import org.apache.poi.hwmf.usermodel.HwmfPicture;

public class HwmfEmbeddedIterator
implements Iterator<HwmfEmbedded> {
    private final Deque<Iterator<?>> iterStack = new ArrayDeque();
    private Object current;

    public HwmfEmbeddedIterator(HwmfPicture wmf) {
        this(wmf.getRecords().iterator());
    }

    public HwmfEmbeddedIterator(Iterator<HwmfRecord> recordIterator) {
        this.iterStack.add(recordIterator);
    }

    @Override
    public boolean hasNext() {
        if (this.iterStack.isEmpty()) {
            return false;
        }
        if (this.current != null) {
            return true;
        }
        do {
            Iterator<?> iter = this.iterStack.peek();
            while (iter.hasNext()) {
                HwmfEscape.WmfEscapeEMF emfData;
                Object obj = iter.next();
                if (obj instanceof HwmfFill.HwmfImageRecord) {
                    this.current = obj;
                    return true;
                }
                if (!(obj instanceof HwmfEscape) || ((HwmfEscape)obj).getEscapeFunction() != HwmfEscape.EscapeFunction.META_ESCAPE_ENHANCED_METAFILE || !(emfData = (HwmfEscape.WmfEscapeEMF)((HwmfEscape)obj).getEscapeData()).isValid()) continue;
                this.current = obj;
                return true;
            }
            this.iterStack.pop();
        } while (!this.iterStack.isEmpty());
        return false;
    }

    @Override
    public HwmfEmbedded next() {
        HwmfEmbedded emb = this.checkHwmfImageRecord();
        if (emb != null) {
            return emb;
        }
        emb = this.checkHwmfEscapeRecord();
        if (emb != null) {
            return emb;
        }
        throw new NoSuchElementException("no further embedded emf records found.");
    }

    private HwmfEmbedded checkHwmfImageRecord() {
        if (!(this.current instanceof HwmfFill.HwmfImageRecord)) {
            return null;
        }
        HwmfFill.HwmfImageRecord hir = (HwmfFill.HwmfImageRecord)this.current;
        this.current = null;
        HwmfEmbedded emb = new HwmfEmbedded();
        emb.setEmbeddedType(HwmfEmbeddedType.BMP);
        emb.setData(hir.getBMPData());
        return emb;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private HwmfEmbedded checkHwmfEscapeRecord() {
        if (!(this.current instanceof HwmfEscape)) {
            return null;
        }
        HwmfEmbedded emb = new HwmfEmbedded();
        emb.setEmbeddedType(HwmfEmbeddedType.EMF);
        try (UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();){
            HwmfEscape.WmfEscapeEMF img;
            do {
                HwmfEscape esc = (HwmfEscape)this.current;
                assert (esc.getEscapeFunction() == HwmfEscape.EscapeFunction.META_ESCAPE_ENHANCED_METAFILE);
                img = (HwmfEscape.WmfEscapeEMF)esc.getEscapeData();
                assert (img.isValid());
                bos.write(img.getEmfData());
                this.current = null;
            } while (img.getRemainingBytes() > 0 && this.hasNext() && this.current instanceof HwmfEscape);
            emb.setData(bos.toByteArray());
            HwmfEmbedded hwmfEmbedded = emb;
            return hwmfEmbedded;
        }
        catch (IOException ignored) {
            return null;
        }
    }
}

