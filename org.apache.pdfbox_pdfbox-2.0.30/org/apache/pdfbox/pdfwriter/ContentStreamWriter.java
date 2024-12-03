/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdfwriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.util.Charsets;

public class ContentStreamWriter {
    private final OutputStream output;
    public static final byte[] SPACE = new byte[]{32};
    public static final byte[] EOL = new byte[]{10};

    public ContentStreamWriter(OutputStream out) {
        this.output = out;
    }

    public void writeToken(COSBase base) throws IOException {
        this.writeObject(base);
    }

    public void writeToken(Operator op) throws IOException {
        this.writeObject(op);
    }

    public void writeTokens(Object ... tokens) throws IOException {
        for (Object token : tokens) {
            this.writeObject(token);
        }
        this.output.write("\n".getBytes(Charsets.US_ASCII));
    }

    public void writeTokens(List<?> tokens) throws IOException {
        for (Object token : tokens) {
            this.writeObject(token);
        }
    }

    private void writeObject(Object o) throws IOException {
        if (o instanceof COSBase) {
            this.writeObject((COSBase)o);
        } else if (o instanceof Operator) {
            this.writeObject((Operator)o);
        } else {
            throw new IOException("Error:Unknown type in content stream:" + o);
        }
    }

    private void writeObject(Operator op) throws IOException {
        if (op.getName().equals("BI")) {
            this.output.write("BI".getBytes(Charsets.ISO_8859_1));
            this.output.write(EOL);
            COSDictionary dic = op.getImageParameters();
            for (COSName key : dic.keySet()) {
                COSBase value = dic.getDictionaryObject(key);
                key.writePDF(this.output);
                this.output.write(SPACE);
                this.writeObject(value);
                this.output.write(EOL);
            }
            this.output.write("ID".getBytes(Charsets.ISO_8859_1));
            this.output.write(EOL);
            this.output.write(op.getImageData());
            this.output.write(EOL);
            this.output.write("EI".getBytes(Charsets.ISO_8859_1));
            this.output.write(EOL);
        } else {
            this.output.write(op.getName().getBytes(Charsets.ISO_8859_1));
            this.output.write(EOL);
        }
    }

    private void writeObject(COSBase o) throws IOException {
        if (o instanceof COSString) {
            COSWriter.writeString((COSString)o, this.output);
            this.output.write(SPACE);
        } else if (o instanceof COSFloat) {
            ((COSFloat)o).writePDF(this.output);
            this.output.write(SPACE);
        } else if (o instanceof COSInteger) {
            ((COSInteger)o).writePDF(this.output);
            this.output.write(SPACE);
        } else if (o instanceof COSBoolean) {
            ((COSBoolean)o).writePDF(this.output);
            this.output.write(SPACE);
        } else if (o instanceof COSName) {
            ((COSName)o).writePDF(this.output);
            this.output.write(SPACE);
        } else if (o instanceof COSArray) {
            COSArray array = (COSArray)o;
            this.output.write(COSWriter.ARRAY_OPEN);
            for (int i = 0; i < array.size(); ++i) {
                this.writeObject(array.get(i));
            }
            this.output.write(COSWriter.ARRAY_CLOSE);
            this.output.write(SPACE);
        } else if (o instanceof COSDictionary) {
            COSDictionary obj = (COSDictionary)o;
            this.output.write(COSWriter.DICT_OPEN);
            for (Map.Entry<COSName, COSBase> entry : obj.entrySet()) {
                if (entry.getValue() == null) continue;
                this.writeObject(entry.getKey());
                this.writeObject(entry.getValue());
            }
            this.output.write(COSWriter.DICT_CLOSE);
            this.output.write(SPACE);
        } else if (o instanceof COSNull) {
            this.output.write("null".getBytes(Charsets.ISO_8859_1));
            this.output.write(SPACE);
        } else {
            throw new IOException("Error:Unknown type in content stream:" + o);
        }
    }
}

