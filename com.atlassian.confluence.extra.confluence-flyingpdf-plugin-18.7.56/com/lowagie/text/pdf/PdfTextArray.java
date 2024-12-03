/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfNumber;
import java.util.ArrayList;
import java.util.List;

public class PdfTextArray {
    private List<Object> arrayList = new ArrayList<Object>();
    private String lastStr;
    private Float lastNum;

    public PdfTextArray(String str) {
        this.add(str);
    }

    public PdfTextArray() {
    }

    public void add(PdfNumber number) {
        this.add((float)number.doubleValue());
    }

    public void add(float number) {
        if (number != 0.0f) {
            if (this.lastNum != null) {
                this.lastNum = Float.valueOf(number + this.lastNum.floatValue());
                if (this.lastNum.floatValue() != 0.0f) {
                    this.replaceLast(this.lastNum);
                } else {
                    this.arrayList.remove(this.arrayList.size() - 1);
                }
            } else {
                this.lastNum = Float.valueOf(number);
                this.arrayList.add(this.lastNum);
            }
            this.lastStr = null;
        }
    }

    public void add(String str) {
        if (str.length() > 0) {
            if (this.lastStr != null) {
                this.lastStr = this.lastStr + str;
                this.replaceLast(this.lastStr);
            } else {
                this.lastStr = str;
                this.arrayList.add(this.lastStr);
            }
            this.lastNum = null;
        }
    }

    List getArrayList() {
        return this.arrayList;
    }

    private void replaceLast(Object obj) {
        this.arrayList.set(this.arrayList.size() - 1, obj);
    }
}

