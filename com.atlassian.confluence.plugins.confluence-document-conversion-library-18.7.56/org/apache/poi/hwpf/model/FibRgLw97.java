/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.FibRgLw;
import org.apache.poi.hwpf.model.SubdocumentType;
import org.apache.poi.hwpf.model.types.FibRgLw97AbstractType;
import org.apache.poi.util.Internal;

@Internal
public class FibRgLw97
extends FibRgLw97AbstractType
implements FibRgLw {
    public FibRgLw97() {
    }

    public FibRgLw97(byte[] std, int offset) {
        this.fillFields(std, offset);
    }

    @Override
    public int getSubdocumentTextStreamLength(SubdocumentType subdocumentType) {
        switch (subdocumentType) {
            case MAIN: {
                return this.getCcpText();
            }
            case FOOTNOTE: {
                return this.getCcpFtn();
            }
            case HEADER: {
                return this.getCcpHdd();
            }
            case MACRO: {
                return this.field_7_reserved3;
            }
            case ANNOTATION: {
                return this.getCcpAtn();
            }
            case ENDNOTE: {
                return this.getCcpEdn();
            }
            case TEXTBOX: {
                return this.getCcpTxbx();
            }
            case HEADER_TEXTBOX: {
                return this.getCcpHdrTxbx();
            }
        }
        throw new UnsupportedOperationException("Unsupported: " + (Object)((Object)subdocumentType));
    }

    @Override
    public void setSubdocumentTextStreamLength(SubdocumentType subdocumentType, int newLength) {
        switch (subdocumentType) {
            case MAIN: {
                this.setCcpText(newLength);
                return;
            }
            case FOOTNOTE: {
                this.setCcpFtn(newLength);
                return;
            }
            case HEADER: {
                this.setCcpHdd(newLength);
                return;
            }
            case MACRO: {
                this.field_7_reserved3 = newLength;
                return;
            }
            case ANNOTATION: {
                this.setCcpAtn(newLength);
                return;
            }
            case ENDNOTE: {
                this.setCcpEdn(newLength);
                return;
            }
            case TEXTBOX: {
                this.setCcpTxbx(newLength);
                return;
            }
            case HEADER_TEXTBOX: {
                this.setCcpHdrTxbx(newLength);
                return;
            }
        }
        throw new UnsupportedOperationException("Unsupported: " + (Object)((Object)subdocumentType));
    }
}

