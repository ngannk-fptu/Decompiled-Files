/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ESTException
extends IOException {
    private Throwable cause;
    private InputStream body;
    private int statusCode;
    private static final long MAX_ERROR_BODY = 8192L;

    public ESTException(String msg) {
        this(msg, null);
    }

    public ESTException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
        this.body = null;
        this.statusCode = 0;
    }

    public ESTException(String message, Throwable cause, int statusCode, InputStream body) {
        super(message);
        this.cause = cause;
        this.statusCode = statusCode;
        if (body != null) {
            byte[] b = new byte[8192];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                int i = body.read(b);
                while (i >= 0) {
                    if ((long)(bos.size() + i) > 8192L) {
                        i = 8192 - bos.size();
                        bos.write(b, 0, i);
                        break;
                    }
                    bos.write(b, 0, i);
                    i = body.read(b);
                }
                bos.flush();
                bos.close();
                this.body = new ByteArrayInputStream(bos.toByteArray());
                body.close();
            }
            catch (Exception exception) {}
        } else {
            this.body = null;
        }
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " HTTP Status Code: " + this.statusCode;
    }

    public InputStream getBody() {
        if (this.body == null) {
            return new InputStream(){

                @Override
                public int read() throws IOException {
                    return -1;
                }
            };
        }
        return this.body;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}

