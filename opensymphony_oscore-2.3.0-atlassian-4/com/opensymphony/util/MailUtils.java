/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.MessagingException
 *  javax.mail.internet.AddressException
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeUtility
 */
package com.opensymphony.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

public class MailUtils {
    public static final byte[] decodeBytes(String str) throws IOException {
        try {
            int read;
            ByteArrayInputStream encodedStringStream = new ByteArrayInputStream(str.getBytes());
            InputStream decoder = MimeUtility.decode((InputStream)encodedStringStream, (String)"base64");
            ByteArrayOutputStream decodedByteStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[256];
            while ((read = decoder.read(buffer)) != -1) {
                decodedByteStream.write(buffer, 0, read);
            }
            decodedByteStream.flush();
            return decodedByteStream.toByteArray();
        }
        catch (MessagingException me) {
            throw new IOException("Cannot decode data.");
        }
    }

    public static final String encodeBytes(byte[] data) throws IOException {
        try {
            ByteArrayOutputStream encodedByteStream = new ByteArrayOutputStream();
            OutputStream encoder = MimeUtility.encode((OutputStream)encodedByteStream, (String)"base64");
            encoder.write(data);
            encoder.flush();
            return new String(encodedByteStream.toByteArray());
        }
        catch (MessagingException me) {
            throw new IOException("Cannot encode data.");
        }
    }

    public static final boolean verifyEmail(String email) {
        if (email == null) {
            return false;
        }
        if (email.indexOf(64) < 1) {
            return false;
        }
        try {
            new InternetAddress(email);
            return true;
        }
        catch (AddressException e) {
            return false;
        }
    }
}

