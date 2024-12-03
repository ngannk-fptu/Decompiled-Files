/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfContentParser {
    public static final int COMMAND_TYPE = 200;
    private PRTokeniser tokeniser;

    public PdfContentParser(PRTokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public List<PdfObject> parse(List<PdfObject> ls) throws IOException {
        PdfObject ob;
        if (ls == null) {
            ls = new ArrayList<PdfObject>();
        } else {
            ls.clear();
        }
        while ((ob = this.readPRObject()) != null) {
            ls.add(ob);
            if (ob.type() != 200) continue;
            break;
        }
        return ls;
    }

    public PRTokeniser getTokeniser() {
        return this.tokeniser;
    }

    public void setTokeniser(PRTokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public PdfDictionary readDictionary() throws IOException {
        PdfDictionary dic = new PdfDictionary();
        while (true) {
            if (!this.nextValidToken()) {
                throw new IOException(MessageLocalization.getComposedMessage("unexpected.end.of.file"));
            }
            if (this.tokeniser.getTokenType() == 8) break;
            if (this.tokeniser.getTokenType() != 3) {
                throw new IOException(MessageLocalization.getComposedMessage("dictionary.key.is.not.a.name"));
            }
            PdfName name = new PdfName(this.tokeniser.getStringValue(), false);
            PdfObject obj = this.readPRObject();
            int type = obj.type();
            if (-type == 8) {
                throw new IOException(MessageLocalization.getComposedMessage("unexpected.gt.gt"));
            }
            if (-type == 6) {
                throw new IOException(MessageLocalization.getComposedMessage("unexpected.close.bracket"));
            }
            dic.put(name, obj);
        }
        return dic;
    }

    public PdfArray readArray() throws IOException {
        PdfObject obj;
        int type;
        PdfArray array = new PdfArray();
        while (-(type = (obj = this.readPRObject()).type()) != 6) {
            if (-type == 8) {
                throw new IOException(MessageLocalization.getComposedMessage("unexpected.gt.gt"));
            }
            array.add(obj);
        }
        return array;
    }

    public PdfObject readPRObject() throws IOException {
        if (!this.nextValidToken()) {
            return null;
        }
        int type = this.tokeniser.getTokenType();
        switch (type) {
            case 7: {
                PdfDictionary dic = this.readDictionary();
                return dic;
            }
            case 5: {
                return this.readArray();
            }
            case 2: {
                PdfString str = new PdfString(this.tokeniser.getStringValue(), null).setHexWriting(this.tokeniser.isHexString());
                return str;
            }
            case 3: {
                return new PdfName(this.tokeniser.getStringValue(), false);
            }
            case 1: {
                return new PdfNumber(this.tokeniser.getStringValue());
            }
            case 10: {
                return new PdfLiteral(200, this.tokeniser.getStringValue());
            }
        }
        return new PdfLiteral(-type, this.tokeniser.getStringValue());
    }

    public boolean nextValidToken() throws IOException {
        while (this.tokeniser.nextToken()) {
            if (this.tokeniser.getTokenType() == 4) continue;
            return true;
        }
        return false;
    }
}

