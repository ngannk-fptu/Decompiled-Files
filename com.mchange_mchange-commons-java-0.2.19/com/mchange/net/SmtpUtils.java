/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.net;

import com.mchange.io.OutputStreamUtils;
import com.mchange.io.ReaderUtils;
import com.mchange.net.SmtpException;
import com.mchange.net.SocketUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;

public final class SmtpUtils {
    private static final String ENC = "8859_1";
    private static final String CRLF = "\r\n";
    private static final String CHARSET = "charset";
    private static final int CHARSET_LEN = "charset".length();
    public static final int DEFAULT_SMTP_PORT = 25;

    public static void sendMail(InetAddress inetAddress, int n, String string, String[] stringArray, Properties properties, byte[] byArray) throws IOException, SmtpException {
        Socket socket = null;
        DataOutputStream dataOutputStream = null;
        BufferedReader bufferedReader = null;
        try {
            socket = new Socket(inetAddress, n);
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), ENC));
            SmtpUtils.ensureResponse(bufferedReader, 200, 300);
            dataOutputStream.writeBytes("HELO " + socket.getLocalAddress().getHostName() + CRLF);
            dataOutputStream.flush();
            SmtpUtils.ensureResponse(bufferedReader, 200, 300);
            dataOutputStream.writeBytes("MAIL FROM: " + string + CRLF);
            dataOutputStream.flush();
            SmtpUtils.ensureResponse(bufferedReader, 200, 300);
            int n2 = stringArray.length;
            while (--n2 >= 0) {
                dataOutputStream.writeBytes("RCPT TO: " + stringArray[n2] + CRLF);
                dataOutputStream.flush();
                SmtpUtils.ensureResponse(bufferedReader, 200, 300);
            }
            dataOutputStream.writeBytes("DATA\r\n");
            dataOutputStream.flush();
            SmtpUtils.ensureResponse(bufferedReader, 300, 400);
            Enumeration<Object> enumeration = properties.keys();
            while (enumeration.hasMoreElements()) {
                String string2 = (String)enumeration.nextElement();
                String string3 = properties.getProperty(string2);
                dataOutputStream.writeBytes(string2 + ": " + string3 + CRLF);
            }
            dataOutputStream.writeBytes(CRLF);
            dataOutputStream.write(byArray);
            dataOutputStream.writeBytes("\r\n.\r\n");
            dataOutputStream.flush();
            SmtpUtils.ensureResponse(bufferedReader, 200, 300);
            dataOutputStream.writeBytes("QUIT\r\n");
            dataOutputStream.flush();
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            try {
                unsupportedEncodingException.printStackTrace();
                throw new InternalError("8859_1 not supported???");
            }
            catch (Throwable throwable) {
                OutputStreamUtils.attemptClose(dataOutputStream);
                ReaderUtils.attemptClose(bufferedReader);
                SocketUtils.attemptClose(socket);
                throw throwable;
            }
        }
        OutputStreamUtils.attemptClose(dataOutputStream);
        ReaderUtils.attemptClose(bufferedReader);
        SocketUtils.attemptClose(socket);
    }

    private static String encodingFromContentType(String string) {
        int n = string.indexOf(CHARSET);
        if (n >= 0) {
            String string2 = string.substring(n + CHARSET_LEN);
            if ((string2 = string2.trim()).charAt(0) != '=') {
                return SmtpUtils.encodingFromContentType(string2);
            }
            int n2 = (string2 = string2.substring(1).trim()).indexOf(59);
            if (n2 >= 0) {
                string2 = string2.substring(0, n2);
            }
            return string2;
        }
        return null;
    }

    private static byte[] bytesFromBodyString(String string, String string2) throws UnsupportedEncodingException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter((OutputStream)byteArrayOutputStream, string2));
        printWriter.print(string);
        printWriter.flush();
        return byteArrayOutputStream.toByteArray();
    }

    private static void ensureResponse(BufferedReader bufferedReader, int n, int n2) throws IOException, SmtpException {
        String string = bufferedReader.readLine();
        try {
            int n3 = Integer.parseInt(string.substring(0, 3));
            while (string.charAt(3) == '-') {
                string = bufferedReader.readLine();
            }
            if (n3 < n || n3 >= n2) {
                throw new SmtpException(n3, string);
            }
        }
        catch (NumberFormatException numberFormatException) {
            throw new SmtpException("Bad SMTP response while mailing document!");
        }
    }

    public static void main(String[] stringArray) {
        try {
            InetAddress inetAddress = InetAddress.getByName("mailhub.mchange.com");
            int n = 25;
            String string = "octavia@mchange.com";
            String[] stringArray2 = new String[]{"swaldman@mchange.com", "sw-lists@mchange.com"};
            Properties properties = new Properties();
            properties.put("From", "goolash@mchange.com");
            properties.put("To", "garbage@mchange.com");
            properties.put("Subject", "Test test test AGAIN...");
            byte[] byArray = "This is a test AGAIN! Imagine that!".getBytes(ENC);
            SmtpUtils.sendMail(inetAddress, n, string, stringArray2, properties, byArray);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private SmtpUtils() {
    }
}

