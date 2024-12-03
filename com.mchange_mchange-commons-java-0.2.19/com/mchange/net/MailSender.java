/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.net;

import com.mchange.net.ProtocolException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface MailSender {
    public void sendMail(String var1, String[] var2, String[] var3, String[] var4, String var5, String var6, String var7) throws IOException, ProtocolException, UnsupportedEncodingException;

    public void sendMail(String var1, String[] var2, String[] var3, String[] var4, String var5, String var6) throws IOException, ProtocolException;
}

