/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.Base64;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.transport.http.NonBlockingBufferedInputStream;
import org.apache.axis.transport.http.SimpleAxisServer;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.NetworkUtils;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;

public class SimpleAxisWorker
implements Runnable {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$http$SimpleAxisWorker == null ? (class$org$apache$axis$transport$http$SimpleAxisWorker = SimpleAxisWorker.class$("org.apache.axis.transport.http.SimpleAxisWorker")) : class$org$apache$axis$transport$http$SimpleAxisWorker).getName());
    private SimpleAxisServer server;
    private Socket socket;
    private static String transportName = "SimpleHTTP";
    private static byte[] OK = ("200 " + Messages.getMessage("ok00")).getBytes();
    private static byte[] NOCONTENT = ("202 " + Messages.getMessage("ok00") + "\n\n").getBytes();
    private static byte[] UNAUTH = ("401 " + Messages.getMessage("unauth00")).getBytes();
    private static byte[] SENDER = "400".getBytes();
    private static byte[] ISE = ("500 " + Messages.getMessage("internalError01")).getBytes();
    private static byte[] HTTP = "HTTP/1.0 ".getBytes();
    private static byte[] XML_MIME_STUFF = "\r\nContent-Type: text/xml; charset=utf-8\r\nContent-Length: ".getBytes();
    private static byte[] HTML_MIME_STUFF = "\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: ".getBytes();
    private static byte[] SEPARATOR = "\r\n\r\n".getBytes();
    private static final byte[] toLower = new byte[256];
    private static final int BUFSIZ = 4096;
    private static final byte[] lenHeader;
    private static final int lenLen;
    private static final byte[] typeHeader;
    private static final int typeLen;
    private static final byte[] locationHeader;
    private static final int locationLen;
    private static final byte[] actionHeader;
    private static final int actionLen;
    private static final byte[] cookieHeader;
    private static final int cookieLen;
    private static final byte[] cookie2Header;
    private static final int cookie2Len;
    private static final byte[] authHeader;
    private static final int authLen;
    private static final byte[] getHeader;
    private static final byte[] postHeader;
    private static final byte[] headerEnder;
    private static final byte[] basicAuth;
    static /* synthetic */ Class class$org$apache$axis$transport$http$SimpleAxisWorker;

    public SimpleAxisWorker(SimpleAxisServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        try {
            this.execute();
        }
        finally {
            SimpleAxisServer.getPool().workerDone(this, false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void execute() {
        block59: {
            block58: {
                block57: {
                    block56: {
                        block55: {
                            buf = new byte[4096];
                            engine = this.server.getAxisServer();
                            msgContext = new MessageContext(engine);
                            requestMsg = null;
                            is = new NonBlockingBufferedInputStream();
                            soapAction = new StringBuffer();
                            httpRequest = new StringBuffer();
                            fileName = new StringBuffer();
                            cookie = new StringBuffer();
                            cookie2 = new StringBuffer();
                            authInfo = new StringBuffer();
                            contentType = new StringBuffer();
                            contentLocation = new StringBuffer();
                            responseMsg = null;
                            msgContext.setTransportName(SimpleAxisWorker.transportName);
                            responseMsg = null;
                            try {
                                try {
                                    block54: {
                                        status = SimpleAxisWorker.OK;
                                        doWsdl = false;
                                        cooky = null;
                                        methodName = null;
                                        try {
                                            if (this.server.isSessionUsed()) {
                                                cookie.delete(0, cookie.length());
                                                cookie2.delete(0, cookie2.length());
                                            }
                                            authInfo.delete(0, authInfo.length());
                                            is.setInputStream(this.socket.getInputStream());
                                            requestHeaders = new MimeHeaders();
                                            contentLength = this.parseHeaders(is, buf, contentType, contentLocation, soapAction, httpRequest, fileName, cookie, cookie2, authInfo, requestHeaders);
                                            is.setContentLength(contentLength);
                                            paramIdx = fileName.toString().indexOf(63);
                                            if (paramIdx != -1) {
                                                params = fileName.substring(paramIdx + 1);
                                                fileName.setLength(paramIdx);
                                                SimpleAxisWorker.log.debug((Object)Messages.getMessage("filename00", fileName.toString()));
                                                SimpleAxisWorker.log.debug((Object)Messages.getMessage("params00", params));
                                                if ("wsdl".equalsIgnoreCase(params)) {
                                                    doWsdl = true;
                                                }
                                                if (params.startsWith("method=")) {
                                                    methodName = params.substring(7);
                                                }
                                            }
                                            msgContext.setProperty("realpath", fileName.toString());
                                            msgContext.setProperty("path", fileName.toString());
                                            msgContext.setProperty("jws.classDir", "jwsClasses");
                                            msgContext.setProperty("home.dir", ".");
                                            url = "http://" + SimpleAxisWorker.getLocalHost() + ":" + this.server.getServerSocket().getLocalPort() + "/" + fileName.toString();
                                            msgContext.setProperty("transport.url", url);
                                            filePart = fileName.toString();
                                            if (filePart.startsWith("axis/services/")) {
                                                servicePart = filePart.substring(14);
                                                separator = servicePart.indexOf(47);
                                                if (separator > -1) {
                                                    msgContext.setProperty("objectID", servicePart.substring(separator + 1));
                                                    servicePart = servicePart.substring(0, separator);
                                                }
                                                msgContext.setTargetService(servicePart);
                                            }
                                            if (authInfo.length() > 0) {
                                                decoded = Base64.decode(authInfo.toString());
                                                userBuf = new StringBuffer();
                                                pwBuf = new StringBuffer();
                                                authBuf = userBuf;
                                                for (i = 0; i < decoded.length; ++i) {
                                                    if ((char)(decoded[i] & 127) == ':') {
                                                        authBuf = pwBuf;
                                                        continue;
                                                    }
                                                    authBuf.append((char)(decoded[i] & 127));
                                                }
                                                if (SimpleAxisWorker.log.isDebugEnabled()) {
                                                    SimpleAxisWorker.log.debug((Object)Messages.getMessage("user00", userBuf.toString()));
                                                }
                                                msgContext.setUsername(userBuf.toString());
                                                msgContext.setPassword(pwBuf.toString());
                                            }
                                            if (!httpRequest.toString().equals("GET")) ** GOTO lbl-1000
                                            out = this.socket.getOutputStream();
                                            out.write(SimpleAxisWorker.HTTP);
                                            if (fileName.length() == 0) {
                                                out.write("301 Redirect\nLocation: /axis/\n\n".getBytes());
                                                out.flush();
                                            }
                                            ** GOTO lbl-1000
                                        }
                                        catch (Exception e) {
                                            if (e instanceof AxisFault) {
                                                af = (AxisFault)e;
                                                SimpleAxisWorker.log.debug((Object)Messages.getMessage("serverFault00"), (Throwable)af);
                                                faultCode = af.getFaultCode();
                                                status = Constants.FAULT_SOAP12_SENDER.equals(faultCode) ? SimpleAxisWorker.SENDER : ("Server.Unauthorized".equals(af.getFaultCode().getLocalPart()) ? SimpleAxisWorker.UNAUTH : SimpleAxisWorker.ISE);
                                            } else {
                                                status = SimpleAxisWorker.ISE;
                                                af = AxisFault.makeFault(e);
                                            }
                                            responseMsg = msgContext.getResponseMessage();
                                            if (responseMsg == null) {
                                                responseMsg = new Message(af);
                                                responseMsg.setMessageContext(msgContext);
                                                break block54;
                                            } else {
                                                try {
                                                    env = responseMsg.getSOAPEnvelope();
                                                    env.clearBody();
                                                    env.addBodyElement(new SOAPFault((AxisFault)e));
                                                }
                                                catch (AxisFault fault) {
                                                    // empty catch block
                                                }
                                            }
                                            break block54;
                                        }
                                        var32_45 = null;
                                        break block55;
lbl-1000:
                                        // 1 sources

                                        {
                                            block60: {
                                                out.write(status);
                                                if (methodName == null) break block60;
                                                body = "<" + methodName + ">" + "</" + methodName + ">";
                                                msgtxt = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\"><SOAP-ENV:Body>" + body + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>";
                                                istream = new ByteArrayInputStream(msgtxt.getBytes());
                                                requestMsg = new Message(istream);
                                                ** GOTO lbl175
                                            }
                                            if (!doWsdl) ** GOTO lbl-1000
                                            engine.generateWSDL(msgContext);
                                            doc = (Document)msgContext.getProperty("WSDL");
                                            if (doc == null) ** GOTO lbl175
                                            XMLUtils.normalize(doc.getDocumentElement());
                                            response = XMLUtils.PrettyDocumentToString(doc);
                                            respBytes = response.getBytes();
                                            out.write(SimpleAxisWorker.XML_MIME_STUFF);
                                            this.putInt(buf, out, respBytes.length);
                                            out.write(SimpleAxisWorker.SEPARATOR);
                                            out.write(respBytes);
                                            out.flush();
                                        }
                                        break block56;
lbl-1000:
                                        // 1 sources

                                        {
                                            sb = new StringBuffer();
                                            sb.append("<h2>And now... Some Services</h2>\n");
                                            i = engine.getConfig().getDeployedServices();
                                            sb.append("<ul>\n");
                                            while (i.hasNext()) {
                                                sd = (ServiceDesc)i.next();
                                                sb.append("<li>\n");
                                                sb.append(sd.getName());
                                                sb.append(" <a href=\"services/");
                                                sb.append(sd.getName());
                                                sb.append("?wsdl\"><i>(wsdl)</i></a></li>\n");
                                                operations = sd.getOperations();
                                                if (operations.isEmpty()) continue;
                                                sb.append("<ul>\n");
                                                it = operations.iterator();
                                                while (it.hasNext()) {
                                                    desc = (OperationDesc)it.next();
                                                    sb.append("<li>" + desc.getName());
                                                }
                                                sb.append("</ul>\n");
                                            }
                                            sb.append("</ul>\n");
                                            bytes = sb.toString().getBytes();
                                            out.write(SimpleAxisWorker.HTML_MIME_STUFF);
                                            this.putInt(buf, out, bytes.length);
                                            out.write(SimpleAxisWorker.SEPARATOR);
                                            out.write(bytes);
                                            out.flush();
                                        }
                                        break block57;
lbl-1000:
                                        // 1 sources

                                        {
                                            soapActionString = soapAction.toString();
                                            if (soapActionString != null) {
                                                msgContext.setUseSOAPAction(true);
                                                msgContext.setSOAPActionURI(soapActionString);
                                            }
                                            requestMsg = new Message(is, false, contentType.toString(), contentLocation.toString());
lbl175:
                                            // 3 sources

                                            requestMimeHeaders = requestMsg.getMimeHeaders();
                                            i = requestHeaders.getAllHeaders();
                                            while (i.hasNext()) {
                                                requestHeader = (MimeHeader)i.next();
                                                requestMimeHeaders.addHeader(requestHeader.getName(), requestHeader.getValue());
                                            }
                                            msgContext.setRequestMessage(requestMsg);
                                            requestEncoding = (String)requestMsg.getProperty("javax.xml.soap.character-set-encoding");
                                            if (requestEncoding != null) {
                                                msgContext.setProperty("javax.xml.soap.character-set-encoding", requestEncoding);
                                            }
                                            if (this.server.isSessionUsed()) {
                                                if (cookie.length() > 0) {
                                                    cooky = cookie.toString().trim();
                                                } else if (cookie2.length() > 0) {
                                                    cooky = cookie2.toString().trim();
                                                }
                                                if (cooky == null) {
                                                    i = SimpleAxisServer.sessionIndex++;
                                                    cooky = "" + i;
                                                }
                                                msgContext.setSession(this.server.createSession(cooky));
                                            }
                                            engine.invoke(msgContext);
                                            responseMsg = msgContext.getResponseMessage();
                                            if (responseMsg != null) break block54;
                                            status = SimpleAxisWorker.NOCONTENT;
                                        }
                                    }
                                    responseEncoding = (String)msgContext.getProperty("javax.xml.soap.character-set-encoding");
                                    if (responseEncoding != null && responseMsg != null) {
                                        responseMsg.setProperty("javax.xml.soap.character-set-encoding", responseEncoding);
                                    }
                                    out = this.socket.getOutputStream();
                                    out.write(SimpleAxisWorker.HTTP);
                                    out.write(status);
                                    if (responseMsg != null) {
                                        if (this.server.isSessionUsed() && null != cooky && 0 != cooky.trim().length()) {
                                            cookieOut = new StringBuffer();
                                            cookieOut.append("\r\nSet-Cookie: ").append(cooky).append("\r\nSet-Cookie2: ").append(cooky);
                                            out.write(cookieOut.toString().getBytes());
                                        }
                                        out.write(("\r\nContent-Type: " + responseMsg.getContentType(msgContext.getSOAPConstants())).getBytes());
                                        i = responseMsg.getMimeHeaders().getAllHeaders();
                                        while (i.hasNext()) {
                                            responseHeader = (MimeHeader)i.next();
                                            out.write(13);
                                            out.write(10);
                                            out.write(responseHeader.getName().getBytes());
                                            out.write(SimpleAxisWorker.headerEnder);
                                            out.write(responseHeader.getValue().getBytes());
                                        }
                                        out.write(SimpleAxisWorker.SEPARATOR);
                                        responseMsg.writeTo(out);
                                    }
                                    out.flush();
                                    break block58;
                                }
                                catch (Exception e) {
                                    SimpleAxisWorker.log.info((Object)Messages.getMessage("exception00"), (Throwable)e);
                                    var32_49 = null;
                                    try {
                                        if (this.socket != null) {
                                            this.socket.close();
                                        }
                                        break block59;
                                    }
                                    catch (Exception e) {}
                                    break block59;
                                }
                            }
                            catch (Throwable var31_59) {
                                var32_50 = null;
                                ** try [egrp 7[TRYBLOCK] [15 : 1912->1929)] { 
lbl241:
                                // 1 sources

                                if (this.socket == null) throw var31_59;
                                this.socket.close();
                                throw var31_59;
lbl244:
                                // 1 sources

                                catch (Exception e) {
                                    // empty catch block
                                }
                                throw var31_59;
                            }
                        }
                        try {}
                        catch (Exception e) {
                            // empty catch block
                            return;
                        }
                        if (this.socket == null) return;
                        this.socket.close();
                        return;
                    }
                    var32_46 = null;
                    try {}
                    catch (Exception e) {
                        // empty catch block
                        return;
                    }
                    if (this.socket == null) return;
                    this.socket.close();
                    return;
                }
                var32_47 = null;
                try {}
                catch (Exception e) {
                    // empty catch block
                    return;
                }
                if (this.socket == null) return;
                this.socket.close();
                return;
            }
            var32_48 = null;
            try {}
            catch (Exception e) {}
            if (this.socket != null) {
                this.socket.close();
            }
        }
        if (msgContext.getProperty("quit.requested") == null) return;
        try {
            this.server.stop();
            return;
        }
        catch (Exception e) {
            // empty catch block
        }
    }

    protected void invokeMethodFromGet(String methodName, String args) throws Exception {
    }

    private int parseHeaders(NonBlockingBufferedInputStream is, byte[] buf, StringBuffer contentType, StringBuffer contentLocation, StringBuffer soapAction, StringBuffer httpRequest, StringBuffer fileName, StringBuffer cookie, StringBuffer cookie2, StringBuffer authInfo, MimeHeaders headers) throws IOException {
        int len = 0;
        int n = this.readLine(is, buf, 0, buf.length);
        if (n < 0) {
            throw new IOException(Messages.getMessage("unexpectedEOS00"));
        }
        httpRequest.delete(0, httpRequest.length());
        fileName.delete(0, fileName.length());
        contentType.delete(0, contentType.length());
        contentLocation.delete(0, contentLocation.length());
        if (buf[0] == getHeader[0]) {
            char c;
            httpRequest.append("GET");
            for (int i = 0; i < n - 5 && (c = (char)(buf[i + 5] & 0x7F)) != ' '; ++i) {
                fileName.append(c);
            }
            log.debug((Object)Messages.getMessage("filename01", "SimpleAxisServer", fileName.toString()));
            return 0;
        }
        if (buf[0] == postHeader[0]) {
            char c;
            httpRequest.append("POST");
            for (int i = 0; i < n - 6 && (c = (char)(buf[i + 6] & 0x7F)) != ' '; ++i) {
                fileName.append(c);
            }
            log.debug((Object)Messages.getMessage("filename01", "SimpleAxisServer", fileName.toString()));
        } else {
            throw new IOException(Messages.getMessage("badRequest00"));
        }
        while ((n = this.readLine(is, buf, 0, buf.length)) > 0 && (n > 2 || buf[0] != 10 && buf[0] != 13 || len <= 0)) {
            int endHeaderIndex;
            for (endHeaderIndex = 0; endHeaderIndex < n && toLower[buf[endHeaderIndex]] != headerEnder[0]; ++endHeaderIndex) {
            }
            int i = (endHeaderIndex += 2) - 1;
            if (endHeaderIndex == lenLen && this.matches(buf, lenHeader)) {
                while (++i < n && buf[i] >= 48 && buf[i] <= 57) {
                    len = len * 10 + (buf[i] - 48);
                }
                headers.addHeader("Content-Length", String.valueOf(len));
                continue;
            }
            if (endHeaderIndex == actionLen && this.matches(buf, actionHeader)) {
                soapAction.delete(0, soapAction.length());
                ++i;
                while (++i < n && buf[i] != 34) {
                    soapAction.append((char)(buf[i] & 0x7F));
                }
                headers.addHeader("SOAPAction", "\"" + soapAction.toString() + "\"");
                continue;
            }
            if (this.server.isSessionUsed() && endHeaderIndex == cookieLen && this.matches(buf, cookieHeader)) {
                while (++i < n && buf[i] != 59 && buf[i] != 13 && buf[i] != 10) {
                    cookie.append((char)(buf[i] & 0x7F));
                }
                headers.addHeader("Set-Cookie", cookie.toString());
                continue;
            }
            if (this.server.isSessionUsed() && endHeaderIndex == cookie2Len && this.matches(buf, cookie2Header)) {
                while (++i < n && buf[i] != 59 && buf[i] != 13 && buf[i] != 10) {
                    cookie2.append((char)(buf[i] & 0x7F));
                }
                headers.addHeader("Set-Cookie2", cookie.toString());
                continue;
            }
            if (endHeaderIndex == authLen && this.matches(buf, authHeader)) {
                if (this.matches(buf, endHeaderIndex, basicAuth)) {
                    i += basicAuth.length;
                    while (++i < n && buf[i] != 13 && buf[i] != 10) {
                        if (buf[i] == 32) continue;
                        authInfo.append((char)(buf[i] & 0x7F));
                    }
                    headers.addHeader("Authorization", new String(basicAuth) + authInfo.toString());
                    continue;
                }
                throw new IOException(Messages.getMessage("badAuth00"));
            }
            if (endHeaderIndex == locationLen && this.matches(buf, locationHeader)) {
                while (++i < n && buf[i] != 13 && buf[i] != 10) {
                    if (buf[i] == 32) continue;
                    contentLocation.append((char)(buf[i] & 0x7F));
                }
                headers.addHeader("Content-Location", contentLocation.toString());
                continue;
            }
            if (endHeaderIndex == typeLen && this.matches(buf, typeHeader)) {
                while (++i < n && buf[i] != 13 && buf[i] != 10) {
                    if (buf[i] == 32) continue;
                    contentType.append((char)(buf[i] & 0x7F));
                }
                headers.addHeader("Content-Type", contentLocation.toString());
                continue;
            }
            String customHeaderName = new String(buf, 0, endHeaderIndex - 2);
            StringBuffer customHeaderValue = new StringBuffer();
            while (++i < n && buf[i] != 13 && buf[i] != 10) {
                if (buf[i] == 32) continue;
                customHeaderValue.append((char)(buf[i] & 0x7F));
            }
            headers.addHeader(customHeaderName, customHeaderValue.toString());
        }
        return len;
    }

    public boolean matches(byte[] buf, byte[] target) {
        for (int i = 0; i < target.length; ++i) {
            if (toLower[buf[i]] == target[i]) continue;
            return false;
        }
        return true;
    }

    public boolean matches(byte[] buf, int bufIdx, byte[] target) {
        for (int i = 0; i < target.length; ++i) {
            if (toLower[buf[bufIdx + i]] == target[i]) continue;
            return false;
        }
        return true;
    }

    private void putInt(byte[] buf, OutputStream out, int value) throws IOException {
        int len = 0;
        int offset = buf.length;
        if (value < 0) {
            buf[--offset] = 45;
            value = -value;
            ++len;
        }
        if (value == 0) {
            buf[--offset] = 48;
            ++len;
        }
        while (value > 0) {
            buf[--offset] = (byte)(value % 10 + 48);
            value /= 10;
            ++len;
        }
        out.write(buf, offset, len);
    }

    private int readLine(NonBlockingBufferedInputStream is, byte[] b, int off, int len) throws IOException {
        int c;
        int count = 0;
        while ((c = is.read()) != -1) {
            int peek;
            if (c != 10 && c != 13) {
                b[off++] = (byte)c;
                ++count;
            }
            if (count != len && (10 != c || (peek = is.peek()) == 32 || peek == 9)) continue;
            break;
        }
        return count > 0 ? count : -1;
    }

    public static String getLocalHost() {
        return NetworkUtils.getLocalHostname();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        for (int i = 0; i < 256; ++i) {
            SimpleAxisWorker.toLower[i] = (byte)i;
        }
        for (int lc = 97; lc <= 122; ++lc) {
            SimpleAxisWorker.toLower[lc + 65 - 97] = (byte)lc;
        }
        lenHeader = "content-length: ".getBytes();
        lenLen = lenHeader.length;
        typeHeader = ("Content-Type".toLowerCase() + ": ").getBytes();
        typeLen = typeHeader.length;
        locationHeader = ("Content-Location".toLowerCase() + ": ").getBytes();
        locationLen = locationHeader.length;
        actionHeader = "soapaction: ".getBytes();
        actionLen = actionHeader.length;
        cookieHeader = "cookie: ".getBytes();
        cookieLen = cookieHeader.length;
        cookie2Header = "cookie2: ".getBytes();
        cookie2Len = cookie2Header.length;
        authHeader = "authorization: ".getBytes();
        authLen = authHeader.length;
        getHeader = "GET".getBytes();
        postHeader = "POST".getBytes();
        headerEnder = ": ".getBytes();
        basicAuth = "basic ".getBytes();
    }
}

