/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFNum;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.NumberingDocument;

public class XWPFNumbering
extends POIXMLDocumentPart {
    protected List<XWPFAbstractNum> abstractNums = new ArrayList<XWPFAbstractNum>();
    protected List<XWPFNum> nums = new ArrayList<XWPFNum>();
    boolean isNew;
    private CTNumbering ctNumbering;

    public XWPFNumbering(PackagePart part) {
        super(part);
        this.isNew = true;
    }

    public XWPFNumbering() {
        this.abstractNums = new ArrayList<XWPFAbstractNum>();
        this.nums = new ArrayList<XWPFNum>();
        this.isNew = true;
    }

    @Override
    protected void onDocumentRead() throws IOException {
        try (InputStream is = this.getPackagePart().getInputStream();){
            NumberingDocument numberingDoc = (NumberingDocument)NumberingDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.ctNumbering = numberingDoc.getNumbering();
            for (CTNum cTNum : this.ctNumbering.getNumArray()) {
                this.nums.add(new XWPFNum(cTNum, this));
            }
            for (XmlObject xmlObject : this.ctNumbering.getAbstractNumArray()) {
                this.abstractNums.add(new XWPFAbstractNum((CTAbstractNum)xmlObject, this));
            }
            this.isNew = false;
        }
    }

    @Override
    protected void commit() throws IOException {
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTNumbering.type.getName().getNamespaceURI(), "numbering"));
        PackagePart part = this.getPackagePart();
        try (OutputStream out = part.getOutputStream();){
            this.ctNumbering.save(out, xmlOptions);
        }
    }

    public void setNumbering(CTNumbering numbering) {
        this.ctNumbering = numbering;
    }

    public boolean numExist(BigInteger numID) {
        for (XWPFNum num : this.nums) {
            if (!num.getCTNum().getNumId().equals(numID)) continue;
            return true;
        }
        return false;
    }

    public BigInteger addNum(XWPFNum num) {
        this.ctNumbering.addNewNum();
        int pos = this.ctNumbering.sizeOfNumArray() - 1;
        this.ctNumbering.setNumArray(pos, num.getCTNum());
        this.nums.add(num);
        return num.getCTNum().getNumId();
    }

    public BigInteger addNum(BigInteger abstractNumID) {
        CTNum ctNum = this.ctNumbering.addNewNum();
        ctNum.addNewAbstractNumId();
        ctNum.getAbstractNumId().setVal(abstractNumID);
        ctNum.setNumId(BigInteger.valueOf((long)this.nums.size() + 1L));
        XWPFNum num = new XWPFNum(ctNum, this);
        this.nums.add(num);
        return ctNum.getNumId();
    }

    public void addNum(BigInteger abstractNumID, BigInteger numID) {
        CTNum ctNum = this.ctNumbering.addNewNum();
        ctNum.addNewAbstractNumId();
        ctNum.getAbstractNumId().setVal(abstractNumID);
        ctNum.setNumId(numID);
        XWPFNum num = new XWPFNum(ctNum, this);
        this.nums.add(num);
    }

    public XWPFNum getNum(BigInteger numID) {
        for (XWPFNum num : this.nums) {
            if (!num.getCTNum().getNumId().equals(numID)) continue;
            return num;
        }
        return null;
    }

    public XWPFAbstractNum getAbstractNum(BigInteger abstractNumID) {
        for (XWPFAbstractNum abstractNum : this.abstractNums) {
            if (!abstractNum.getAbstractNum().getAbstractNumId().equals(abstractNumID)) continue;
            return abstractNum;
        }
        return null;
    }

    public BigInteger getIdOfAbstractNum(XWPFAbstractNum abstractNum) {
        CTAbstractNum copy = (CTAbstractNum)abstractNum.getCTAbstractNum().copy();
        XWPFAbstractNum newAbstractNum = new XWPFAbstractNum(copy, this);
        for (int i = 0; i < this.abstractNums.size(); ++i) {
            newAbstractNum.getCTAbstractNum().setAbstractNumId(BigInteger.valueOf(i));
            newAbstractNum.setNumbering(this);
            if (!newAbstractNum.getCTAbstractNum().valueEquals(this.abstractNums.get(i).getCTAbstractNum())) continue;
            return newAbstractNum.getCTAbstractNum().getAbstractNumId();
        }
        return null;
    }

    public BigInteger addAbstractNum(XWPFAbstractNum abstractNum) {
        int pos = this.abstractNums.size();
        if (abstractNum.getAbstractNum() != null) {
            CTAbstractNum ctAbstractNum = this.ctNumbering.addNewAbstractNum();
            ctAbstractNum.set(abstractNum.getAbstractNum());
            abstractNum.setCtAbstractNum(ctAbstractNum);
        } else {
            abstractNum.setCtAbstractNum(this.ctNumbering.addNewAbstractNum());
            BigInteger id = this.findNextAbstractNumberingId();
            abstractNum.getAbstractNum().setAbstractNumId(id);
            this.ctNumbering.setAbstractNumArray(pos, abstractNum.getAbstractNum());
            abstractNum.setCtAbstractNum(this.ctNumbering.getAbstractNumArray(pos));
        }
        this.abstractNums.add(abstractNum);
        return abstractNum.getCTAbstractNum().getAbstractNumId();
    }

    private BigInteger findNextAbstractNumberingId() {
        long maxId = 0L;
        for (XWPFAbstractNum num : this.abstractNums) {
            maxId = Math.max(maxId, num.getAbstractNum().getAbstractNumId().longValue());
        }
        return BigInteger.valueOf(maxId + 1L);
    }

    public boolean removeAbstractNum(BigInteger abstractNumID) {
        BigInteger foundNumId;
        for (XWPFAbstractNum abstractNum : this.abstractNums) {
            foundNumId = abstractNum.getAbstractNum().getAbstractNumId();
            if (!abstractNumID.equals(foundNumId)) continue;
            this.abstractNums.remove(abstractNum);
            break;
        }
        for (int i = 0; i < this.ctNumbering.sizeOfAbstractNumArray(); ++i) {
            CTAbstractNum ctAbstractNum = this.ctNumbering.getAbstractNumArray(i);
            foundNumId = ctAbstractNum.getAbstractNumId();
            if (!abstractNumID.equals(foundNumId)) continue;
            this.ctNumbering.removeAbstractNum(i);
            return true;
        }
        return false;
    }

    public BigInteger getAbstractNumID(BigInteger numID) {
        XWPFNum num = this.getNum(numID);
        if (num == null) {
            return null;
        }
        if (num.getCTNum() == null) {
            return null;
        }
        if (num.getCTNum().getAbstractNumId() == null) {
            return null;
        }
        return num.getCTNum().getAbstractNumId().getVal();
    }

    public List<XWPFAbstractNum> getAbstractNums() {
        return Collections.unmodifiableList(this.abstractNums);
    }

    public List<XWPFNum> getNums() {
        return Collections.unmodifiableList(this.nums);
    }
}

