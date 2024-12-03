/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Properties
 */
package org.bouncycastle.est.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.est.ESTClient;
import org.bouncycastle.est.ESTClientSourceProvider;
import org.bouncycastle.est.ESTException;
import org.bouncycastle.est.ESTRequest;
import org.bouncycastle.est.ESTRequestBuilder;
import org.bouncycastle.est.ESTResponse;
import org.bouncycastle.est.Source;
import org.bouncycastle.util.Properties;

class DefaultESTClient
implements ESTClient {
    private static final Charset utf8 = Charset.forName("UTF-8");
    private static byte[] CRLF = new byte[]{13, 10};
    private final ESTClientSourceProvider sslSocketProvider;

    public DefaultESTClient(ESTClientSourceProvider sslSocketProvider) {
        this.sslSocketProvider = sslSocketProvider;
    }

    private static void writeLine(OutputStream os, String s) throws IOException {
        os.write(s.getBytes());
        os.write(CRLF);
    }

    @Override
    public ESTResponse doRequest(ESTRequest req) throws IOException {
        ESTResponse resp = null;
        ESTRequest r = req;
        int rcCount = 15;
        while ((r = this.redirectURL(resp = this.performRequest(r))) != null && --rcCount > 0) {
        }
        if (rcCount == 0) {
            throw new ESTException("Too many redirects..");
        }
        return resp;
    }

    protected ESTRequest redirectURL(ESTResponse response) throws IOException {
        ESTRequest redirectingRequest = null;
        if (response.getStatusCode() >= 300 && response.getStatusCode() <= 399) {
            switch (response.getStatusCode()) {
                case 301: 
                case 302: 
                case 303: 
                case 306: 
                case 307: {
                    String loc = response.getHeader("Location");
                    if ("".equals(loc)) {
                        throw new ESTException("Redirect status type: " + response.getStatusCode() + " but no location header");
                    }
                    ESTRequestBuilder requestBuilder = new ESTRequestBuilder(response.getOriginalRequest());
                    if (loc.startsWith("http")) {
                        redirectingRequest = requestBuilder.withURL(new URL(loc)).build();
                        break;
                    }
                    URL u = response.getOriginalRequest().getURL();
                    redirectingRequest = requestBuilder.withURL(new URL(u.getProtocol(), u.getHost(), u.getPort(), loc)).build();
                    break;
                }
                default: {
                    throw new ESTException("Client does not handle http status code: " + response.getStatusCode());
                }
            }
        }
        if (redirectingRequest != null) {
            response.close();
        }
        return redirectingRequest;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ESTResponse performRequest(ESTRequest c) throws IOException {
        ESTResponse res = null;
        Source socketSource = null;
        try {
            ESTResponse eSTResponse;
            URL u;
            socketSource = this.sslSocketProvider.makeSource(c.getURL().getHost(), c.getURL().getPort());
            if (c.getListener() != null) {
                c = c.getListener().onConnection(socketSource, c);
            }
            OutputStream os = null;
            Set opts = Properties.asKeySet((String)"org.bouncycastle.debug.est");
            os = opts.contains("output") || opts.contains("all") ? new PrintingOutputStream(socketSource.getOutputStream()) : socketSource.getOutputStream();
            String req = c.getURL().getPath() + (c.getURL().getQuery() != null ? c.getURL().getQuery() : "");
            ESTRequestBuilder rb = new ESTRequestBuilder(c);
            Map<String, String[]> headers = c.getHeaders();
            if (!headers.containsKey("Connection")) {
                rb.addHeader("Connection", "close");
            }
            if ((u = c.getURL()).getPort() > -1) {
                rb.setHeader("Host", String.format("%s:%d", u.getHost(), u.getPort()));
            } else {
                rb.setHeader("Host", u.getHost());
            }
            ESTRequest rc = rb.build();
            DefaultESTClient.writeLine(os, rc.getMethod() + " " + req + " HTTP/1.1");
            for (Map.Entry<String, String[]> ent : rc.getHeaders().entrySet()) {
                String[] vs = ent.getValue();
                for (int i = 0; i != vs.length; ++i) {
                    DefaultESTClient.writeLine(os, ent.getKey() + ": " + vs[i]);
                }
            }
            os.write(CRLF);
            os.flush();
            rc.writeData(os);
            os.flush();
            if (rc.getHijacker() != null) {
                eSTResponse = res = rc.getHijacker().hijack(rc, socketSource);
                return eSTResponse;
            }
            eSTResponse = res = new ESTResponse(rc, socketSource);
            return eSTResponse;
        }
        finally {
            if (socketSource != null && res == null) {
                socketSource.close();
            }
        }
    }

    private static class PrintingOutputStream
    extends OutputStream {
        private final OutputStream tgt;

        public PrintingOutputStream(OutputStream tgt) {
            this.tgt = tgt;
        }

        @Override
        public void write(int b) throws IOException {
            System.out.print(String.valueOf((char)b));
            this.tgt.write(b);
        }
    }
}

