/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hslf.record.ExHyperlink;
import org.apache.poi.hslf.record.ExHyperlinkAtom;
import org.apache.poi.hslf.record.ExObjList;
import org.apache.poi.hslf.record.HSLFEscherClientDataRecord;
import org.apache.poi.hslf.record.InteractiveInfo;
import org.apache.poi.hslf.record.InteractiveInfoAtom;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.TxInteractiveInfoAtom;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSimpleShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.sl.usermodel.Hyperlink;
import org.apache.poi.sl.usermodel.Slide;

public final class HSLFHyperlink
implements Hyperlink<HSLFShape, HSLFTextParagraph> {
    private final ExHyperlink exHyper;
    private final InteractiveInfo info;
    private TxInteractiveInfoAtom txinfo;

    protected HSLFHyperlink(ExHyperlink exHyper, InteractiveInfo info) {
        this.info = info;
        this.exHyper = exHyper;
    }

    public ExHyperlink getExHyperlink() {
        return this.exHyper;
    }

    public InteractiveInfo getInfo() {
        return this.info;
    }

    public TxInteractiveInfoAtom getTextRunInfo() {
        return this.txinfo;
    }

    protected void setTextRunInfo(TxInteractiveInfoAtom txinfo) {
        this.txinfo = txinfo;
    }

    static HSLFHyperlink createHyperlink(HSLFSimpleShape shape) {
        ExHyperlink exHyper = new ExHyperlink();
        int linkId = shape.getSheet().getSlideShow().addToObjListAtom(exHyper);
        ExHyperlinkAtom obj = exHyper.getExHyperlinkAtom();
        obj.setNumber(linkId);
        InteractiveInfo info = new InteractiveInfo();
        info.getInteractiveInfoAtom().setHyperlinkID(linkId);
        HSLFEscherClientDataRecord cldata = shape.getClientData(true);
        cldata.addChild(info);
        HSLFHyperlink hyper = new HSLFHyperlink(exHyper, info);
        hyper.linkToNextSlide();
        shape.setHyperlink(hyper);
        return hyper;
    }

    static HSLFHyperlink createHyperlink(HSLFTextRun run) {
        ExHyperlink exHyper = new ExHyperlink();
        int linkId = run.getTextParagraph().getSheet().getSlideShow().addToObjListAtom(exHyper);
        ExHyperlinkAtom obj = exHyper.getExHyperlinkAtom();
        obj.setNumber(linkId);
        InteractiveInfo info = new InteractiveInfo();
        info.getInteractiveInfoAtom().setHyperlinkID(linkId);
        HSLFHyperlink hyper = new HSLFHyperlink(exHyper, info);
        hyper.linkToNextSlide();
        TxInteractiveInfoAtom txinfo = new TxInteractiveInfoAtom();
        int startIdx = run.getTextParagraph().getStartIdxOfTextRun(run);
        int endIdx = startIdx + run.getLength();
        txinfo.setStartIndex(startIdx);
        txinfo.setEndIndex(endIdx);
        hyper.setTextRunInfo(txinfo);
        run.setHyperlink(hyper);
        return hyper;
    }

    @Override
    public HyperlinkType getType() {
        switch (this.info.getInteractiveInfoAtom().getHyperlinkType()) {
            case 8: {
                return this.exHyper.getLinkURL().startsWith("mailto:") ? HyperlinkType.EMAIL : HyperlinkType.URL;
            }
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 7: {
                return HyperlinkType.DOCUMENT;
            }
            case 6: 
            case 9: 
            case 10: {
                return HyperlinkType.FILE;
            }
        }
        return HyperlinkType.NONE;
    }

    @Override
    public void linkToEmail(String emailAddress) {
        InteractiveInfoAtom iia = this.info.getInteractiveInfoAtom();
        iia.setAction((byte)4);
        iia.setJump((byte)0);
        iia.setHyperlinkType((byte)8);
        this.exHyper.setLinkURL("mailto:" + emailAddress);
        this.exHyper.setLinkTitle(emailAddress);
        this.exHyper.setLinkOptions(16);
    }

    @Override
    public void linkToUrl(String url) {
        InteractiveInfoAtom iia = this.info.getInteractiveInfoAtom();
        iia.setAction((byte)4);
        iia.setJump((byte)0);
        iia.setHyperlinkType((byte)8);
        this.exHyper.setLinkURL(url);
        this.exHyper.setLinkTitle(url);
        this.exHyper.setLinkOptions(16);
    }

    @Override
    public void linkToSlide(Slide<HSLFShape, HSLFTextParagraph> slide) {
        assert (slide instanceof HSLFSlide);
        HSLFSlide sl = (HSLFSlide)slide;
        int slideNum = slide.getSlideNumber();
        String alias = "Slide " + slideNum;
        InteractiveInfoAtom iia = this.info.getInteractiveInfoAtom();
        iia.setAction((byte)4);
        iia.setJump((byte)0);
        iia.setHyperlinkType((byte)7);
        this.linkToDocument(sl._getSheetNumber(), slideNum, alias, 48);
    }

    @Override
    public void linkToNextSlide() {
        InteractiveInfoAtom iia = this.info.getInteractiveInfoAtom();
        iia.setAction((byte)3);
        iia.setJump((byte)1);
        iia.setHyperlinkType((byte)0);
        this.linkToDocument(1, -1, "NEXT", 16);
    }

    @Override
    public void linkToPreviousSlide() {
        InteractiveInfoAtom iia = this.info.getInteractiveInfoAtom();
        iia.setAction((byte)3);
        iia.setJump((byte)2);
        iia.setHyperlinkType((byte)1);
        this.linkToDocument(1, -1, "PREV", 16);
    }

    @Override
    public void linkToFirstSlide() {
        InteractiveInfoAtom iia = this.info.getInteractiveInfoAtom();
        iia.setAction((byte)3);
        iia.setJump((byte)3);
        iia.setHyperlinkType((byte)2);
        this.linkToDocument(1, -1, "FIRST", 16);
    }

    @Override
    public void linkToLastSlide() {
        InteractiveInfoAtom iia = this.info.getInteractiveInfoAtom();
        iia.setAction((byte)3);
        iia.setJump((byte)4);
        iia.setHyperlinkType((byte)3);
        this.linkToDocument(1, -1, "LAST", 16);
    }

    private void linkToDocument(int sheetNumber, int slideNumber, String alias, int options) {
        this.exHyper.setLinkURL(sheetNumber + "," + slideNumber + "," + alias);
        this.exHyper.setLinkTitle(alias);
        this.exHyper.setLinkOptions(options);
    }

    @Override
    public String getAddress() {
        return this.exHyper.getLinkURL();
    }

    @Override
    public void setAddress(String str) {
        this.exHyper.setLinkURL(str);
    }

    public int getId() {
        return this.exHyper.getExHyperlinkAtom().getNumber();
    }

    @Override
    public String getLabel() {
        return this.exHyper.getLinkTitle();
    }

    @Override
    public void setLabel(String label) {
        this.exHyper.setLinkTitle(label);
    }

    public int getStartIndex() {
        return this.txinfo == null ? -1 : this.txinfo.getStartIndex();
    }

    public void setStartIndex(int startIndex) {
        if (this.txinfo != null) {
            this.txinfo.setStartIndex(startIndex);
        }
    }

    public int getEndIndex() {
        return this.txinfo == null ? -1 : this.txinfo.getEndIndex();
    }

    public void setEndIndex(int endIndex) {
        if (this.txinfo != null) {
            this.txinfo.setEndIndex(endIndex);
        }
    }

    public static List<HSLFHyperlink> find(HSLFTextShape shape) {
        return HSLFHyperlink.find(shape.getTextParagraphs());
    }

    protected static List<HSLFHyperlink> find(List<HSLFTextParagraph> paragraphs) {
        ArrayList<HSLFHyperlink> lst = new ArrayList<HSLFHyperlink>();
        if (paragraphs == null || paragraphs.isEmpty()) {
            return lst;
        }
        HSLFTextParagraph firstPara = paragraphs.get(0);
        HSLFSlideShow ppt = firstPara.getSheet().getSlideShow();
        ExObjList exobj = ppt.getDocumentRecord().getExObjList(false);
        if (exobj != null) {
            Record[] records = firstPara.getRecords();
            HSLFHyperlink.find(Arrays.asList(records), exobj, lst);
        }
        return lst;
    }

    protected static HSLFHyperlink find(HSLFShape shape) {
        HSLFSlideShow ppt = shape.getSheet().getSlideShow();
        ExObjList exobj = ppt.getDocumentRecord().getExObjList(false);
        HSLFEscherClientDataRecord cldata = shape.getClientData(false);
        if (exobj != null && cldata != null) {
            ArrayList<HSLFHyperlink> lst = new ArrayList<HSLFHyperlink>();
            HSLFHyperlink.find(cldata.getHSLFChildRecords(), exobj, lst);
            return lst.isEmpty() ? null : (HSLFHyperlink)lst.get(0);
        }
        return null;
    }

    private static void find(List<? extends Record> records, ExObjList exobj, List<HSLFHyperlink> out) {
        ListIterator<? extends Record> iter = records.listIterator();
        while (iter.hasNext()) {
            int id;
            ExHyperlink exHyper;
            InteractiveInfo hldr;
            InteractiveInfoAtom info;
            Record r = iter.next();
            if (!(r instanceof InteractiveInfo) || (info = (hldr = (InteractiveInfo)r).getInteractiveInfoAtom()) == null || (exHyper = exobj.get(id = info.getHyperlinkID())) == null) continue;
            HSLFHyperlink link = new HSLFHyperlink(exHyper, hldr);
            out.add(link);
            if (!iter.hasNext()) continue;
            r = iter.next();
            if (!(r instanceof TxInteractiveInfoAtom)) {
                iter.previous();
                continue;
            }
            link.setTextRunInfo((TxInteractiveInfoAtom)r);
        }
    }
}

