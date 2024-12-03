/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.BaseWatchable;
import com.sun.pdfview.ImageInfo;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFParser;
import com.sun.pdfview.PDFRenderer;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Cache {
    private Map<Integer, SoftReference> pages = Collections.synchronizedMap(new HashMap());

    public void addPage(Integer pageNumber, PDFPage page) {
        this.addPageRecord(pageNumber, page, null);
    }

    public void addPage(Integer pageNumber, PDFPage page, PDFParser parser) {
        this.addPageRecord(pageNumber, page, parser);
    }

    public void addImage(PDFPage page, ImageInfo info, BufferedImage image) {
        this.addImageRecord(page, info, image, null);
    }

    public void addImage(PDFPage page, ImageInfo info, BufferedImage image, PDFRenderer renderer) {
        this.addImageRecord(page, info, image, renderer);
    }

    public PDFPage getPage(Integer pageNumber) {
        PageRecord rec = this.getPageRecord(pageNumber);
        if (rec != null) {
            return (PDFPage)rec.value;
        }
        return null;
    }

    public PDFParser getPageParser(Integer pageNumber) {
        PageRecord rec = this.getPageRecord(pageNumber);
        if (rec != null) {
            return (PDFParser)rec.generator;
        }
        return null;
    }

    public BufferedImage getImage(PDFPage page, ImageInfo info) {
        Record rec = this.getImageRecord(page, info);
        if (rec != null) {
            return (BufferedImage)rec.value;
        }
        return null;
    }

    public PDFRenderer getImageRenderer(PDFPage page, ImageInfo info) {
        Record rec = this.getImageRecord(page, info);
        if (rec != null) {
            return (PDFRenderer)rec.generator;
        }
        return null;
    }

    public void removePage(Integer pageNumber) {
        this.removePageRecord(pageNumber);
    }

    public void removeImage(PDFPage page, ImageInfo info) {
        this.removeImageRecord(page, info);
    }

    PageRecord addPageRecord(Integer pageNumber, PDFPage page, PDFParser parser) {
        PageRecord rec = new PageRecord();
        rec.value = page;
        rec.generator = parser;
        this.pages.put(pageNumber, new SoftReference<PageRecord>(rec));
        return rec;
    }

    PageRecord getPageRecord(Integer pageNumber) {
        SoftReference ref = this.pages.get(pageNumber);
        if (ref != null) {
            String val = ref.get() == null ? " not in " : " in ";
            return (PageRecord)ref.get();
        }
        return null;
    }

    PageRecord removePageRecord(Integer pageNumber) {
        SoftReference ref = this.pages.remove(pageNumber);
        if (ref != null) {
            return (PageRecord)ref.get();
        }
        return null;
    }

    Record addImageRecord(PDFPage page, ImageInfo info, BufferedImage image, PDFRenderer renderer) {
        Integer pageNumber = new Integer(page.getPageNumber());
        PageRecord pageRec = this.getPageRecord(pageNumber);
        if (pageRec == null) {
            pageRec = this.addPageRecord(pageNumber, page, null);
        }
        Record rec = new Record();
        rec.value = image;
        rec.generator = renderer;
        pageRec.images.put(info, new SoftReference<Record>(rec));
        return rec;
    }

    Record getImageRecord(PDFPage page, ImageInfo info) {
        SoftReference<Record> ref;
        Integer pageNumber = new Integer(page.getPageNumber());
        PageRecord pageRec = this.getPageRecord(pageNumber);
        if (pageRec != null && (ref = pageRec.images.get(info)) != null) {
            String val = ref.get() == null ? " not in " : " in ";
            return ref.get();
        }
        return null;
    }

    Record removeImageRecord(PDFPage page, ImageInfo info) {
        SoftReference<Record> ref;
        Integer pageNumber = new Integer(page.getPageNumber());
        PageRecord pageRec = this.getPageRecord(pageNumber);
        if (pageRec != null && (ref = pageRec.images.remove(info)) != null) {
            return ref.get();
        }
        return null;
    }

    class PageRecord
    extends Record {
        Map<ImageInfo, SoftReference<Record>> images;

        public PageRecord() {
            this.images = Collections.synchronizedMap(new HashMap());
        }
    }

    class Record {
        Object value;
        BaseWatchable generator;

        Record() {
        }
    }
}

