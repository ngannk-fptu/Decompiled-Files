/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.MessageFormat;

enum Encoding {
    UNICODE("UTF-16LE", true, false),
    UTF8("UTF-8", true, false),
    CP437("Cp437", false, false),
    CP850("Cp850", false, false),
    CP874("MS874", true, true),
    CP932("MS932", true, false),
    CP936("MS936", true, false),
    CP949("MS949", true, false),
    CP950("MS950", true, false),
    CP1250("Cp1250", true, true),
    CP1251("Cp1251", true, true),
    CP1252("Cp1252", true, true),
    CP1253("Cp1253", true, true),
    CP1254("Cp1254", true, true),
    CP1255("Cp1255", true, true),
    CP1256("Cp1256", true, true),
    CP1257("Cp1257", true, true),
    CP1258("Cp1258", true, true);

    private final String charsetName;
    private final boolean supportsAsciiConversion;
    private final boolean hasAsciiCompatibleSBCS;
    private boolean jvmSupportConfirmed = false;
    private Charset charset;

    private Encoding(String charsetName, boolean supportsAsciiConversion, boolean hasAsciiCompatibleSBCS) {
        this.charsetName = charsetName;
        this.supportsAsciiConversion = supportsAsciiConversion;
        this.hasAsciiCompatibleSBCS = hasAsciiCompatibleSBCS;
    }

    final Encoding checkSupported() throws UnsupportedEncodingException {
        if (!this.jvmSupportConfirmed) {
            if (!Charset.isSupported(this.charsetName)) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_codePageNotSupported"));
                Object[] msgArgs = new Object[]{this.charsetName};
                throw new UnsupportedEncodingException(form.format(msgArgs));
            }
            this.jvmSupportConfirmed = true;
        }
        return this;
    }

    final Charset charset() throws SQLServerException {
        try {
            this.checkSupported();
            if (this.charset == null) {
                this.charset = Charset.forName(this.charsetName);
            }
        }
        catch (UnsupportedEncodingException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_codePageNotSupported"));
            Object[] msgArgs = new Object[]{this.charsetName};
            throw new SQLServerException(form.format(msgArgs), e);
        }
        return this.charset;
    }

    String getCharsetName() {
        return this.charsetName;
    }

    boolean supportsAsciiConversion() {
        return this.supportsAsciiConversion;
    }

    boolean hasAsciiCompatibleSBCS() {
        return this.hasAsciiCompatibleSBCS;
    }
}

