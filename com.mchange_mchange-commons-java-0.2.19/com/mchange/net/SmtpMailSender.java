/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.net;

import com.mchange.net.MailSender;
import com.mchange.net.MimeUtils;
import com.mchange.net.ProtocolException;
import com.mchange.net.SmtpException;
import com.mchange.net.SmtpUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class SmtpMailSender
implements MailSender {
    InetAddress hostAddr;
    int port;

    public SmtpMailSender(InetAddress inetAddress, int n) {
        this.hostAddr = inetAddress;
        this.port = n;
    }

    public SmtpMailSender(InetAddress inetAddress) {
        this(inetAddress, 25);
    }

    public SmtpMailSender(String string, int n) throws UnknownHostException {
        this(InetAddress.getByName(string), n);
    }

    public SmtpMailSender(String string) throws UnknownHostException {
        this(string, 25);
    }

    @Override
    public void sendMail(String string, String[] stringArray, String[] stringArray2, String[] stringArray3, String string2, String string3, String string4) throws IOException, ProtocolException, UnsupportedEncodingException {
        String[] stringArray4;
        if (stringArray == null || stringArray.length < 1) {
            throw new SmtpException("You must specify at least one recipient in the \"to\" field.");
        }
        Properties properties = new Properties();
        properties.put("From", string);
        properties.put("To", SmtpMailSender.makeRecipientString(stringArray));
        properties.put("Subject", string2);
        properties.put("MIME-Version", "1.0");
        properties.put("Content-Type", "text/plain; charset=" + MimeUtils.normalEncoding(string4));
        properties.put("X-Generator", this.getClass().getName());
        if (stringArray2 != null || stringArray3 != null) {
            int n = stringArray.length + (stringArray2 != null ? stringArray2.length : 0) + (stringArray3 != null ? stringArray3.length : 0);
            stringArray4 = new String[n];
            int n2 = 0;
            System.arraycopy(stringArray, 0, stringArray4, n2, stringArray.length);
            n2 += stringArray.length;
            if (stringArray2 != null) {
                System.arraycopy(stringArray2, 0, stringArray4, n2, stringArray2.length);
                n2 += stringArray2.length;
                properties.put("CC", SmtpMailSender.makeRecipientString(stringArray2));
            }
            if (stringArray3 != null) {
                System.arraycopy(stringArray3, 0, stringArray4, n2, stringArray3.length);
            }
        } else {
            stringArray4 = stringArray;
        }
        SmtpUtils.sendMail(this.hostAddr, this.port, string, stringArray4, properties, string3.getBytes(string4));
    }

    @Override
    public void sendMail(String string, String[] stringArray, String[] stringArray2, String[] stringArray3, String string2, String string3) throws IOException, ProtocolException {
        try {
            this.sendMail(string, stringArray, stringArray2, stringArray3, string2, string3, System.getProperty("file.encoding"));
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new InternalError("Default encoding [" + System.getProperty("file.encoding") + "] not supported???");
        }
    }

    private static String makeRecipientString(String[] stringArray) {
        StringBuffer stringBuffer = new StringBuffer(256);
        int n = stringArray.length;
        for (int i = 0; i < n; ++i) {
            if (i != 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(stringArray[i]);
        }
        return stringBuffer.toString();
    }

    public static void main(String[] stringArray) {
        try {
            String[] stringArray2 = new String[]{"stevewaldman@uky.edu"};
            String[] stringArray3 = new String[]{};
            String[] stringArray4 = new String[]{"stevewaldman@mac.com"};
            String string = "swaldman@mchange.com";
            String string2 = "Test SmtpMailSender Again";
            String string3 = "Wheeeee!!!";
            SmtpMailSender smtpMailSender = new SmtpMailSender("localhost");
            smtpMailSender.sendMail(string, stringArray2, stringArray3, stringArray4, string2, string3);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

