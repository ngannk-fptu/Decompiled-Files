/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font.encoding;

import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.StandardEncoding;

public class DictionaryEncoding
extends Encoding {
    private final COSDictionary encoding;
    private final Encoding baseEncoding;
    private final Map<Integer, String> differences = new HashMap<Integer, String>();

    public DictionaryEncoding(COSName baseEncoding, COSArray differences) {
        this.encoding = new COSDictionary();
        this.encoding.setItem(COSName.NAME, (COSBase)COSName.ENCODING);
        this.encoding.setItem(COSName.DIFFERENCES, (COSBase)differences);
        if (baseEncoding != COSName.STANDARD_ENCODING) {
            this.encoding.setItem(COSName.BASE_ENCODING, (COSBase)baseEncoding);
            this.baseEncoding = Encoding.getInstance(baseEncoding);
        } else {
            this.baseEncoding = Encoding.getInstance(baseEncoding);
        }
        if (this.baseEncoding == null) {
            throw new IllegalArgumentException("Invalid encoding: " + baseEncoding);
        }
        this.codeToName.putAll(this.baseEncoding.codeToName);
        this.inverted.putAll(this.baseEncoding.inverted);
        this.applyDifferences();
    }

    public DictionaryEncoding(COSDictionary fontEncoding) {
        this.encoding = fontEncoding;
        this.baseEncoding = null;
        this.applyDifferences();
    }

    public DictionaryEncoding(COSDictionary fontEncoding, boolean isNonSymbolic, Encoding builtIn) {
        this.encoding = fontEncoding;
        Encoding base = null;
        boolean hasBaseEncoding = this.encoding.containsKey(COSName.BASE_ENCODING);
        if (hasBaseEncoding) {
            COSName name = this.encoding.getCOSName(COSName.BASE_ENCODING);
            base = Encoding.getInstance(name);
        }
        if (base == null) {
            if (isNonSymbolic) {
                base = StandardEncoding.INSTANCE;
            } else if (builtIn != null) {
                base = builtIn;
            } else {
                throw new IllegalArgumentException("Symbolic fonts must have a built-in encoding");
            }
        }
        this.baseEncoding = base;
        this.codeToName.putAll(this.baseEncoding.codeToName);
        this.inverted.putAll(this.baseEncoding.inverted);
        this.applyDifferences();
    }

    private void applyDifferences() {
        COSBase base = this.encoding.getDictionaryObject(COSName.DIFFERENCES);
        if (!(base instanceof COSArray)) {
            return;
        }
        COSArray diffArray = (COSArray)base;
        int currentIndex = -1;
        for (int i = 0; i < diffArray.size(); ++i) {
            COSBase next = diffArray.getObject(i);
            if (next instanceof COSNumber) {
                currentIndex = ((COSNumber)next).intValue();
                continue;
            }
            if (!(next instanceof COSName)) continue;
            COSName name = (COSName)next;
            this.overwrite(currentIndex, name.getName());
            this.differences.put(currentIndex, name.getName());
            ++currentIndex;
        }
    }

    public Encoding getBaseEncoding() {
        return this.baseEncoding;
    }

    public Map<Integer, String> getDifferences() {
        return this.differences;
    }

    @Override
    public COSBase getCOSObject() {
        return this.encoding;
    }

    @Override
    public String getEncodingName() {
        if (this.baseEncoding == null) {
            return "differences";
        }
        return this.baseEncoding.getEncodingName() + " with differences";
    }
}

