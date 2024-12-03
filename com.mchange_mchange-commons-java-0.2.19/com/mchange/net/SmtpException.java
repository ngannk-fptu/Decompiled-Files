/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.net;

import com.mchange.net.ProtocolException;

public class SmtpException
extends ProtocolException {
    int resp_num;

    public SmtpException() {
    }

    public SmtpException(String string) {
        super(string);
    }

    public SmtpException(int n, String string) {
        this(string);
        this.resp_num = n;
    }

    public int getSmtpResponseNumber() {
        return this.resp_num;
    }
}

