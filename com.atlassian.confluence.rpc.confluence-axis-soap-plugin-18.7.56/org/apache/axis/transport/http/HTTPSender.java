/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.net.BooleanHolder;
import org.apache.axis.components.net.DefaultSocketFactory;
import org.apache.axis.components.net.SocketFactory;
import org.apache.axis.components.net.SocketFactoryFactory;
import org.apache.axis.encoding.Base64;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.transport.http.ChunkedInputStream;
import org.apache.axis.transport.http.ChunkedOutputStream;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.transport.http.SocketHolder;
import org.apache.axis.transport.http.SocketInputStream;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.TeeOutputStream;
import org.apache.commons.logging.Log;

public class HTTPSender
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$http$HTTPSender == null ? (class$org$apache$axis$transport$http$HTTPSender = HTTPSender.class$("org.apache.axis.transport.http.HTTPSender")) : class$org$apache$axis$transport$http$HTTPSender).getName());
    private static final String ACCEPT_HEADERS = "Accept: application/soap+xml, application/dime, multipart/related, text/*\r\nUser-Agent: " + Messages.getMessage("axisUserAgent") + "\r\n";
    private static final String CACHE_HEADERS = "Cache-Control: no-cache\r\nPragma: no-cache\r\n";
    private static final String CHUNKED_HEADER = HTTPConstants.HEADER_TRANSFER_ENCODING + ": " + HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED + "\r\n";
    private static final String HEADER_CONTENT_TYPE_LC = "Content-Type".toLowerCase();
    private static final String HEADER_LOCATION_LC = "Location".toLowerCase();
    private static final String HEADER_CONTENT_LOCATION_LC = "Content-Location".toLowerCase();
    private static final String HEADER_CONTENT_LENGTH_LC = "Content-Length".toLowerCase();
    private static final String HEADER_TRANSFER_ENCODING_LC = HTTPConstants.HEADER_TRANSFER_ENCODING.toLowerCase();
    URL targetURL;
    static /* synthetic */ Class class$org$apache$axis$transport$http$HTTPSender;

    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("enter00", "HTTPSender::invoke"));
        }
        SocketHolder socketHolder = new SocketHolder(null);
        try {
            BooleanHolder useFullURL = new BooleanHolder(false);
            StringBuffer otherHeaders = new StringBuffer();
            this.targetURL = new URL(msgContext.getStrProp("transport.url"));
            String host = this.targetURL.getHost();
            int port = this.targetURL.getPort();
            InputStream inp = this.writeToSocket(socketHolder, msgContext, this.targetURL, otherHeaders, host, port, msgContext.getTimeout(), useFullURL);
            Hashtable headers = new Hashtable();
            inp = this.readHeadersFromSocket(socketHolder, msgContext, inp, headers);
            this.readFromSocket(socketHolder, msgContext, inp, headers);
        }
        catch (Exception e) {
            log.debug((Object)e);
            try {
                if (socketHolder.getSocket() != null) {
                    socketHolder.getSocket().close();
                }
            }
            catch (IOException ie) {
                // empty catch block
            }
            throw AxisFault.makeFault(e);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("exit00", "HTTPDispatchHandler::invoke"));
        }
    }

    protected void getSocket(SocketHolder sockHolder, MessageContext msgContext, String protocol, String host, int port, int timeout, StringBuffer otherHeaders, BooleanHolder useFullURL) throws Exception {
        SocketFactory factory;
        Hashtable<String, String> options = this.getOptions();
        if (timeout > 0) {
            if (options == null) {
                options = new Hashtable<String, String>();
            }
            options.put(DefaultSocketFactory.CONNECT_TIMEOUT, Integer.toString(timeout));
        }
        if ((factory = SocketFactoryFactory.getFactory(protocol, options)) == null) {
            throw new IOException(Messages.getMessage("noSocketFactory", protocol));
        }
        Socket sock = factory.create(host, port, otherHeaders, useFullURL);
        if (timeout > 0) {
            sock.setSoTimeout(timeout);
        }
        sockHolder.setSocket(sock);
    }

    private InputStream writeToSocket(SocketHolder sockHolder, MessageContext msgContext, URL tmpURL, StringBuffer otherHeaders, String host, int port, int timeout, BooleanHolder useFullURL) throws Exception {
        Hashtable userHeaderTable;
        String action;
        String userID = msgContext.getUsername();
        String passwd = msgContext.getPassword();
        String string = action = msgContext.useSOAPAction() ? msgContext.getSOAPActionURI() : "";
        if (action == null) {
            action = "";
        }
        if (userID == null && tmpURL.getUserInfo() != null) {
            String info = tmpURL.getUserInfo();
            int sep = info.indexOf(58);
            if (sep >= 0 && sep + 1 < info.length()) {
                userID = info.substring(0, sep);
                passwd = info.substring(sep + 1);
            } else {
                userID = info;
            }
        }
        if (userID != null) {
            StringBuffer tmpBuf = new StringBuffer();
            tmpBuf.append(userID).append(":").append(passwd == null ? "" : passwd);
            otherHeaders.append("Authorization").append(": Basic ").append(Base64.encode(tmpBuf.toString().getBytes())).append("\r\n");
        }
        if (msgContext.getMaintainSession()) {
            this.fillHeaders(msgContext, "Cookie", otherHeaders);
            this.fillHeaders(msgContext, "Cookie2", otherHeaders);
        }
        StringBuffer header2 = new StringBuffer();
        String webMethod = null;
        boolean posting = true;
        Message reqMessage = msgContext.getRequestMessage();
        boolean http10 = true;
        boolean httpChunkStream = false;
        boolean httpContinueExpected = false;
        String httpConnection = null;
        String httpver = msgContext.getStrProp("axis.transport.version");
        if (null == httpver) {
            httpver = HTTPConstants.HEADER_PROTOCOL_V10;
        }
        if ((httpver = httpver.trim()).equals(HTTPConstants.HEADER_PROTOCOL_V11)) {
            http10 = false;
        }
        if ((userHeaderTable = (Hashtable)msgContext.getProperty("HTTP-Request-Headers")) != null) {
            if (null == otherHeaders) {
                otherHeaders = new StringBuffer(1024);
            }
            Iterator e = userHeaderTable.entrySet().iterator();
            while (e.hasNext()) {
                String val;
                Map.Entry me = e.next();
                Object keyObj = me.getKey();
                if (null == keyObj) continue;
                String key = keyObj.toString().trim();
                if (key.equalsIgnoreCase(HTTPConstants.HEADER_TRANSFER_ENCODING)) {
                    if (http10 || null == (val = me.getValue().toString()) || !val.trim().equalsIgnoreCase(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED)) continue;
                    httpChunkStream = true;
                    continue;
                }
                if (key.equalsIgnoreCase("Connection")) {
                    if (http10 || !(val = me.getValue().toString()).trim().equalsIgnoreCase(HTTPConstants.HEADER_CONNECTION_CLOSE)) continue;
                    httpConnection = HTTPConstants.HEADER_CONNECTION_CLOSE;
                    continue;
                }
                if (!http10 && key.equalsIgnoreCase("Expect") && null != (val = me.getValue().toString()) && val.trim().equalsIgnoreCase("100-continue")) {
                    httpContinueExpected = true;
                }
                otherHeaders.append(key).append(": ").append(me.getValue()).append("\r\n");
            }
        }
        if (!http10) {
            httpConnection = HTTPConstants.HEADER_CONNECTION_CLOSE;
        }
        header2.append(" ");
        header2.append(http10 ? "HTTP/1.0" : "HTTP/1.1").append("\r\n");
        MimeHeaders mimeHeaders = reqMessage.getMimeHeaders();
        if (posting) {
            String[] header = mimeHeaders.getHeader("Content-Type");
            String contentType = header != null && header.length > 0 ? mimeHeaders.getHeader("Content-Type")[0] : reqMessage.getContentType(msgContext.getSOAPConstants());
            if (contentType == null || contentType.equals("")) {
                throw new Exception(Messages.getMessage("missingContentType"));
            }
            header2.append("Content-Type").append(": ").append(contentType).append("\r\n");
        }
        header2.append(ACCEPT_HEADERS).append("Host").append(": ").append(host).append(port == -1 ? "" : ":" + port).append("\r\n").append(CACHE_HEADERS).append("SOAPAction").append(": \"").append(action).append("\"\r\n");
        if (posting) {
            if (!httpChunkStream) {
                header2.append("Content-Length").append(": ").append(reqMessage.getContentLength()).append("\r\n");
            } else {
                header2.append(CHUNKED_HEADER);
            }
        }
        if (mimeHeaders != null) {
            Iterator i = mimeHeaders.getAllHeaders();
            while (i.hasNext()) {
                MimeHeader mimeHeader = (MimeHeader)i.next();
                String headerName = mimeHeader.getName();
                if (headerName.equals("Content-Type") || headerName.equals("SOAPAction")) continue;
                header2.append(mimeHeader.getName()).append(": ").append(mimeHeader.getValue()).append("\r\n");
            }
        }
        if (null != httpConnection) {
            header2.append("Connection");
            header2.append(": ");
            header2.append(httpConnection);
            header2.append("\r\n");
        }
        this.getSocket(sockHolder, msgContext, this.targetURL.getProtocol(), host, port, timeout, otherHeaders, useFullURL);
        if (null != otherHeaders) {
            header2.append(otherHeaders.toString());
        }
        header2.append("\r\n");
        StringBuffer header = new StringBuffer();
        if (msgContext.getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS) {
            webMethod = msgContext.getStrProp("soap12.webmethod");
        }
        if (webMethod == null) {
            webMethod = "POST";
        } else {
            posting = webMethod.equals("POST");
        }
        header.append(webMethod).append(" ");
        if (useFullURL.value) {
            header.append(tmpURL.toExternalForm());
        } else {
            header.append(tmpURL.getFile() == null || tmpURL.getFile().equals("") ? "/" : tmpURL.getFile());
        }
        header.append(header2.toString());
        OutputStream out = sockHolder.getSocket().getOutputStream();
        if (!posting) {
            out.write(header.toString().getBytes("iso-8859-1"));
            out.flush();
            return null;
        }
        InputStream inp = null;
        if (httpChunkStream || httpContinueExpected) {
            out.write(header.toString().getBytes("iso-8859-1"));
        }
        if (httpContinueExpected) {
            out.flush();
            Hashtable cheaders = new Hashtable();
            inp = this.readHeadersFromSocket(sockHolder, msgContext, null, cheaders);
            int returnCode = -1;
            Integer Irc = (Integer)msgContext.getProperty(HTTPConstants.MC_HTTP_STATUS_CODE);
            if (null != Irc) {
                returnCode = Irc;
            }
            if (100 == returnCode) {
                msgContext.removeProperty(HTTPConstants.MC_HTTP_STATUS_CODE);
                msgContext.removeProperty(HTTPConstants.MC_HTTP_STATUS_MESSAGE);
            } else {
                String statusMessage = (String)msgContext.getProperty(HTTPConstants.MC_HTTP_STATUS_MESSAGE);
                AxisFault fault = new AxisFault("HTTP", "(" + returnCode + ")" + statusMessage, null, null);
                fault.setFaultDetailString(Messages.getMessage("return01", "" + returnCode, ""));
                throw fault;
            }
        }
        ByteArrayOutputStream baos = null;
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("xmlSent00"));
            log.debug((Object)"---------------------------------------------------");
            baos = new ByteArrayOutputStream();
        }
        if (httpChunkStream) {
            ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(out);
            out = new BufferedOutputStream(chunkedOutputStream, 8192);
            try {
                if (baos != null) {
                    out = new TeeOutputStream(out, baos);
                }
                reqMessage.writeTo(out);
            }
            catch (SOAPException e) {
                log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            }
            out.flush();
            chunkedOutputStream.eos();
        } else {
            out = new BufferedOutputStream(out, 8192);
            try {
                if (!httpContinueExpected) {
                    out.write(header.toString().getBytes("iso-8859-1"));
                }
                if (baos != null) {
                    out = new TeeOutputStream(out, baos);
                }
                reqMessage.writeTo(out);
            }
            catch (SOAPException e) {
                log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            }
            out.flush();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)(header + new String(baos.toByteArray())));
        }
        return inp;
    }

    private void fillHeaders(MessageContext msgContext, String header, StringBuffer otherHeaders) {
        Object ck1 = msgContext.getProperty(header);
        if (ck1 != null) {
            if (ck1 instanceof String[]) {
                String[] cookies = (String[])ck1;
                for (int i = 0; i < cookies.length; ++i) {
                    this.addCookie(otherHeaders, header, cookies[i]);
                }
            } else {
                this.addCookie(otherHeaders, header, (String)ck1);
            }
        }
    }

    private void addCookie(StringBuffer otherHeaders, String header, String cookie) {
        otherHeaders.append(header).append(": ").append(cookie).append("\r\n");
    }

    private InputStream readHeadersFromSocket(SocketHolder sockHolder, MessageContext msgContext, InputStream inp, Hashtable headers) throws IOException {
        int b = 0;
        int len = 0;
        int colonIndex = -1;
        int returnCode = 0;
        if (null == inp) {
            inp = new BufferedInputStream(sockHolder.getSocket().getInputStream());
        }
        if (headers == null) {
            headers = new Hashtable<String, String>();
        }
        boolean readTooMuch = false;
        ByteArrayOutputStream buf = new ByteArrayOutputStream(4097);
        while (true) {
            String value;
            String name;
            if (!readTooMuch) {
                b = (byte)inp.read();
            }
            if (b == -1) break;
            readTooMuch = false;
            if (b != 13 && b != 10) {
                if (b == 58 && colonIndex == -1) {
                    colonIndex = len;
                }
                ++len;
                buf.write(b);
                continue;
            }
            if (b == 13) continue;
            if (len == 0) break;
            b = (byte)inp.read();
            readTooMuch = true;
            if (b == 32 || b == 9) continue;
            buf.close();
            byte[] hdata = buf.toByteArray();
            buf.reset();
            if (colonIndex != -1) {
                name = new String(hdata, 0, colonIndex, "iso-8859-1");
                value = new String(hdata, colonIndex + 1, len - 1 - colonIndex, "iso-8859-1");
                colonIndex = -1;
            } else {
                name = new String(hdata, 0, len, "iso-8859-1");
                value = "";
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)(name + value));
            }
            if (msgContext.getProperty(HTTPConstants.MC_HTTP_STATUS_CODE) == null) {
                int start = name.indexOf(32) + 1;
                String tmp = name.substring(start).trim();
                int end = tmp.indexOf(32);
                if (end != -1) {
                    tmp = tmp.substring(0, end);
                }
                returnCode = Integer.parseInt(tmp);
                msgContext.setProperty(HTTPConstants.MC_HTTP_STATUS_CODE, new Integer(returnCode));
                msgContext.setProperty(HTTPConstants.MC_HTTP_STATUS_MESSAGE, name.substring(start + end + 1));
            } else if (msgContext.getMaintainSession()) {
                String nameLowerCase = name.toLowerCase();
                if (nameLowerCase.equalsIgnoreCase("Set-Cookie")) {
                    this.handleCookie("Cookie", null, value, msgContext);
                } else if (nameLowerCase.equalsIgnoreCase("Set-Cookie2")) {
                    this.handleCookie("Cookie2", null, value, msgContext);
                } else {
                    headers.put(name.toLowerCase(), value);
                }
            } else {
                headers.put(name.toLowerCase(), value);
            }
            len = 0;
        }
        return inp;
    }

    private InputStream readFromSocket(SocketHolder socketHolder, MessageContext msgContext, InputStream inp, Hashtable headers) throws IOException {
        String contentLocation;
        String contentType;
        Message outMsg = null;
        Integer rc = (Integer)msgContext.getProperty(HTTPConstants.MC_HTTP_STATUS_CODE);
        int returnCode = 0;
        if (rc != null) {
            returnCode = rc;
        }
        contentType = null == (contentType = (String)headers.get(HEADER_CONTENT_TYPE_LC)) ? null : contentType.trim();
        String location = (String)headers.get(HEADER_LOCATION_LC);
        String string = location = null == location ? null : location.trim();
        if (returnCode > 199 && returnCode < 300) {
            if (returnCode == 202) {
                return inp;
            }
        } else if (msgContext.getSOAPConstants() != SOAPConstants.SOAP12_CONSTANTS && (contentType == null || contentType.startsWith("text/html") || returnCode <= 499 || returnCode >= 600)) {
            byte b;
            if (location != null && (returnCode == 302 || returnCode == 307)) {
                inp.close();
                socketHolder.getSocket().close();
                msgContext.removeProperty(HTTPConstants.MC_HTTP_STATUS_CODE);
                msgContext.setProperty("transport.url", location);
                this.invoke(msgContext);
                return inp;
            }
            if (returnCode == 100) {
                msgContext.removeProperty(HTTPConstants.MC_HTTP_STATUS_CODE);
                msgContext.removeProperty(HTTPConstants.MC_HTTP_STATUS_MESSAGE);
                this.readHeadersFromSocket(socketHolder, msgContext, inp, headers);
                return this.readFromSocket(socketHolder, msgContext, inp, headers);
            }
            ByteArrayOutputStream buf = new ByteArrayOutputStream(4097);
            while (-1 != (b = (byte)inp.read())) {
                buf.write(b);
            }
            String statusMessage = msgContext.getStrProp(HTTPConstants.MC_HTTP_STATUS_MESSAGE);
            AxisFault fault = new AxisFault("HTTP", "(" + returnCode + ")" + statusMessage, null, null);
            fault.setFaultDetailString(Messages.getMessage("return01", "" + returnCode, buf.toString()));
            fault.addFaultDetail(Constants.QNAME_FAULTDETAIL_HTTPERRORCODE, Integer.toString(returnCode));
            throw fault;
        }
        contentLocation = null == (contentLocation = (String)headers.get(HEADER_CONTENT_LOCATION_LC)) ? null : contentLocation.trim();
        String contentLength = (String)headers.get(HEADER_CONTENT_LENGTH_LC);
        contentLength = null == contentLength ? null : contentLength.trim();
        String transferEncoding = (String)headers.get(HEADER_TRANSFER_ENCODING_LC);
        if (null != transferEncoding && (transferEncoding = transferEncoding.trim().toLowerCase()).equals(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED)) {
            inp = new ChunkedInputStream(inp);
        }
        outMsg = new Message(new SocketInputStream(inp, socketHolder.getSocket()), false, contentType, contentLocation);
        MimeHeaders mimeHeaders = outMsg.getMimeHeaders();
        Enumeration e = headers.keys();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            mimeHeaders.addHeader(key, ((String)headers.get(key)).trim());
        }
        outMsg.setMessageType("response");
        msgContext.setResponseMessage(outMsg);
        if (log.isDebugEnabled()) {
            if (null == contentLength) {
                log.debug((Object)("\n" + Messages.getMessage("no00", "Content-Length")));
            }
            log.debug((Object)("\n" + Messages.getMessage("xmlRecd00")));
            log.debug((Object)"-----------------------------------------------");
            log.debug((Object)outMsg.getSOAPEnvelope().toString());
        }
        return inp;
    }

    public void handleCookie(String cookieName, String setCookieName, String cookie, MessageContext msgContext) {
        int keyIndex = (cookie = this.cleanupCookie(cookie)).indexOf("=");
        String key = keyIndex != -1 ? cookie.substring(0, keyIndex) : cookie;
        ArrayList<String> cookies = new ArrayList<String>();
        Object oldCookies = msgContext.getProperty(cookieName);
        boolean alreadyExist = false;
        if (oldCookies != null) {
            if (oldCookies instanceof String[]) {
                String[] oldCookiesArray = (String[])oldCookies;
                for (int i = 0; i < oldCookiesArray.length; ++i) {
                    String anOldCookie = oldCookiesArray[i];
                    if (key != null && anOldCookie.indexOf(key) == 0) {
                        anOldCookie = cookie;
                        alreadyExist = true;
                    }
                    cookies.add(anOldCookie);
                }
            } else {
                String oldCookie = (String)oldCookies;
                if (key != null && oldCookie.indexOf(key) == 0) {
                    oldCookie = cookie;
                    alreadyExist = true;
                }
                cookies.add(oldCookie);
            }
        }
        if (!alreadyExist) {
            cookies.add(cookie);
        }
        if (cookies.size() == 1) {
            msgContext.setProperty(cookieName, cookies.get(0));
        } else if (cookies.size() > 1) {
            msgContext.setProperty(cookieName, cookies.toArray(new String[cookies.size()]));
        }
    }

    private String cleanupCookie(String cookie) {
        int index = (cookie = cookie.trim()).indexOf(59);
        if (index != -1) {
            cookie = cookie.substring(0, index);
        }
        return cookie;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

