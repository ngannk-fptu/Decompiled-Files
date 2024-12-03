/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERPrintableString
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.est.CsrAttrs
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.util.Selector
 *  org.bouncycastle.util.Store
 *  org.bouncycastle.util.encoders.Base64
 */
package org.bouncycastle.est;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.est.CsrAttrs;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cmc.CMCException;
import org.bouncycastle.cmc.SimplePKIResponse;
import org.bouncycastle.est.CACertsResponse;
import org.bouncycastle.est.CSRAttributesResponse;
import org.bouncycastle.est.CSRRequestResponse;
import org.bouncycastle.est.ESTAuth;
import org.bouncycastle.est.ESTClient;
import org.bouncycastle.est.ESTClientProvider;
import org.bouncycastle.est.ESTException;
import org.bouncycastle.est.ESTRequest;
import org.bouncycastle.est.ESTRequestBuilder;
import org.bouncycastle.est.ESTResponse;
import org.bouncycastle.est.ESTSourceConnectionListener;
import org.bouncycastle.est.EnrollmentResponse;
import org.bouncycastle.est.Source;
import org.bouncycastle.est.TLSUniqueProvider;
import org.bouncycastle.mime.BasicMimeParser;
import org.bouncycastle.mime.ConstantMimeContext;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeContext;
import org.bouncycastle.mime.MimeParserContext;
import org.bouncycastle.mime.MimeParserListener;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;

public class ESTService {
    protected static final String CACERTS = "/cacerts";
    protected static final String SIMPLE_ENROLL = "/simpleenroll";
    protected static final String SIMPLE_REENROLL = "/simplereenroll";
    protected static final String FULLCMC = "/fullcmc";
    protected static final String SERVERGEN = "/serverkeygen";
    protected static final String CSRATTRS = "/csrattrs";
    protected static final Set<String> illegalParts = new HashSet<String>();
    private final String server;
    private final ESTClientProvider clientProvider;
    private static final Pattern pathInValid;

    ESTService(String serverAuthority, String label, ESTClientProvider clientProvider) {
        serverAuthority = this.verifyServer(serverAuthority);
        if (label != null) {
            label = this.verifyLabel(label);
            this.server = "https://" + serverAuthority + "/.well-known/est/" + label;
        } else {
            this.server = "https://" + serverAuthority + "/.well-known/est";
        }
        this.clientProvider = clientProvider;
    }

    public static X509CertificateHolder[] storeToArray(Store<X509CertificateHolder> store) {
        return ESTService.storeToArray(store, null);
    }

    public static X509CertificateHolder[] storeToArray(Store<X509CertificateHolder> store, Selector<X509CertificateHolder> selector) {
        Collection c = store.getMatches(selector);
        return c.toArray(new X509CertificateHolder[c.size()]);
    }

    public CACertsResponse getCACerts() throws ESTException {
        ESTResponse resp = null;
        Exception finalThrowable = null;
        CACertsResponse caCertsResponse = null;
        URL url = null;
        boolean failedBeforeClose = false;
        try {
            url = new URL(this.server + CACERTS);
            ESTClient client = this.clientProvider.makeClient();
            ESTRequest req = new ESTRequestBuilder("GET", url).withClient(client).build();
            resp = client.doRequest(req);
            Store<X509CertificateHolder> caCerts = null;
            Store<X509CRLHolder> crlHolderStore = null;
            if (resp.getStatusCode() == 200) {
                String contentType = resp.getHeaders().getFirstValue("Content-Type");
                if (contentType == null || !contentType.startsWith("application/pkcs7-mime")) {
                    String j = contentType != null ? " got " + contentType : " but was not present.";
                    throw new ESTException("Response : " + url.toString() + "Expecting application/pkcs7-mime " + j, null, resp.getStatusCode(), resp.getInputStream());
                }
                try {
                    ASN1InputStream ain = this.getASN1InputStream(resp.getInputStream(), resp.getContentLength());
                    SimplePKIResponse spkr = new SimplePKIResponse(ContentInfo.getInstance((Object)ain.readObject()));
                    caCerts = spkr.getCertificates();
                    crlHolderStore = spkr.getCRLs();
                }
                catch (Throwable ex) {
                    throw new ESTException("Decoding CACerts: " + url.toString() + " " + ex.getMessage(), ex, resp.getStatusCode(), resp.getInputStream());
                }
            } else if (resp.getStatusCode() != 204) {
                throw new ESTException("Get CACerts: " + url.toString(), null, resp.getStatusCode(), resp.getInputStream());
            }
            caCertsResponse = new CACertsResponse(caCerts, crlHolderStore, req, resp.getSource(), this.clientProvider.isTrusted());
        }
        catch (Throwable t) {
            failedBeforeClose = true;
            if (t instanceof ESTException) {
                throw (ESTException)t;
            }
            throw new ESTException(t.getMessage(), t);
        }
        finally {
            if (resp != null) {
                try {
                    resp.close();
                }
                catch (Exception t) {
                    finalThrowable = t;
                }
            }
        }
        if (finalThrowable != null) {
            if (finalThrowable instanceof ESTException) {
                throw (ESTException)finalThrowable;
            }
            throw new ESTException("Get CACerts: " + url.toString(), (Throwable)finalThrowable, resp.getStatusCode(), null);
        }
        return caCertsResponse;
    }

    private ASN1InputStream getASN1InputStream(InputStream respStream, Long contentLength) {
        if (contentLength == null) {
            return new ASN1InputStream(respStream);
        }
        if ((long)contentLength.intValue() == contentLength) {
            return new ASN1InputStream(respStream, contentLength.intValue());
        }
        return new ASN1InputStream(respStream);
    }

    public EnrollmentResponse simpleEnroll(EnrollmentResponse priorResponse) throws Exception {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        try (ESTResponse resp = null;){
            ESTClient client = this.clientProvider.makeClient();
            resp = client.doRequest(new ESTRequestBuilder(priorResponse.getRequestToRetry()).withClient(client).build());
            EnrollmentResponse enrollmentResponse = this.handleEnrollResponse(resp);
            return enrollmentResponse;
        }
    }

    protected EnrollmentResponse enroll(boolean reenroll, PKCS10CertificationRequest certificationRequest, ESTAuth auth, boolean certGen) throws IOException {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        try (ESTResponse resp = null;){
            byte[] data = this.annotateRequest(certificationRequest.getEncoded()).getBytes();
            URL url = new URL(this.server + (certGen ? SERVERGEN : (reenroll ? SIMPLE_REENROLL : SIMPLE_ENROLL)));
            ESTClient client = this.clientProvider.makeClient();
            ESTRequestBuilder req = new ESTRequestBuilder("POST", url).withData(data).withClient(client);
            req.addHeader("Content-Type", "application/pkcs10");
            req.addHeader("Content-Length", "" + data.length);
            req.addHeader("Content-Transfer-Encoding", "base64");
            if (auth != null) {
                auth.applyAuth(req);
            }
            resp = client.doRequest(req.build());
            EnrollmentResponse enrollmentResponse = this.handleEnrollResponse(resp);
            return enrollmentResponse;
        }
    }

    public EnrollmentResponse simpleEnroll(boolean reenroll, PKCS10CertificationRequest certificationRequest, ESTAuth auth) throws IOException {
        return this.enroll(reenroll, certificationRequest, auth, false);
    }

    public EnrollmentResponse simpleEnrollWithServersideCreation(PKCS10CertificationRequest certificationRequest, ESTAuth auth) throws IOException {
        return this.enroll(false, certificationRequest, auth, true);
    }

    public EnrollmentResponse enrollPop(boolean reEnroll, final PKCS10CertificationRequestBuilder builder, final ContentSigner contentSigner, ESTAuth auth, boolean certGen) throws IOException {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        try (ESTResponse resp = null;){
            URL url = new URL(this.server + (reEnroll ? SIMPLE_REENROLL : SIMPLE_ENROLL));
            ESTClient client = this.clientProvider.makeClient();
            ESTRequestBuilder reqBldr = new ESTRequestBuilder("POST", url).withClient(client).withConnectionListener(new ESTSourceConnectionListener(){

                public ESTRequest onConnection(Source source, ESTRequest request) throws IOException {
                    if (source instanceof TLSUniqueProvider && ((TLSUniqueProvider)((Object)source)).isTLSUniqueAvailable()) {
                        PKCS10CertificationRequestBuilder localBuilder = new PKCS10CertificationRequestBuilder(builder);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] tlsUnique = ((TLSUniqueProvider)((Object)source)).getTLSUnique();
                        localBuilder.setAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, (ASN1Encodable)new DERPrintableString(Base64.toBase64String((byte[])tlsUnique)));
                        bos.write(ESTService.this.annotateRequest(localBuilder.build(contentSigner).getEncoded()).getBytes());
                        bos.flush();
                        ESTRequestBuilder reqBuilder = new ESTRequestBuilder(request).withData(bos.toByteArray());
                        reqBuilder.setHeader("Content-Type", "application/pkcs10");
                        reqBuilder.setHeader("Content-Transfer-Encoding", "base64");
                        reqBuilder.setHeader("Content-Length", Long.toString(bos.size()));
                        return reqBuilder.build();
                    }
                    throw new IOException("Source does not supply TLS unique.");
                }
            });
            if (auth != null) {
                auth.applyAuth(reqBldr);
            }
            resp = client.doRequest(reqBldr.build());
            EnrollmentResponse enrollmentResponse = this.handleEnrollResponse(resp);
            return enrollmentResponse;
        }
    }

    public EnrollmentResponse simpleEnrollPoP(boolean reEnroll, PKCS10CertificationRequestBuilder builder, ContentSigner contentSigner, ESTAuth auth) throws IOException {
        return this.enrollPop(reEnroll, builder, contentSigner, auth, false);
    }

    public EnrollmentResponse simpleEnrollPopWithServersideCreation(PKCS10CertificationRequestBuilder builder, ContentSigner contentSigner, ESTAuth auth) throws IOException {
        return this.enrollPop(false, builder, contentSigner, auth, true);
    }

    protected EnrollmentResponse handleEnrollResponse(ESTResponse resp) throws IOException {
        ESTRequest req = resp.getOriginalRequest();
        Store<X509CertificateHolder> enrolled = null;
        if (resp.getStatusCode() == 202) {
            String rt = resp.getHeader("Retry-After");
            if (rt == null) {
                throw new ESTException("Got Status 202 but not Retry-After header from: " + req.getURL().toString());
            }
            long notBefore = -1L;
            try {
                notBefore = System.currentTimeMillis() + Long.parseLong(rt) * 1000L;
            }
            catch (NumberFormatException nfe) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    notBefore = dateFormat.parse(rt).getTime();
                }
                catch (Exception ex) {
                    throw new ESTException("Unable to parse Retry-After header:" + req.getURL().toString() + " " + ex.getMessage(), null, resp.getStatusCode(), resp.getInputStream());
                }
            }
            return new EnrollmentResponse(null, notBefore, req, resp.getSource());
        }
        if (resp.getStatusCode() == 200 && resp.getHeaderOrEmpty("content-type").contains("multipart/mixed")) {
            Headers mimeHeaders = new Headers(resp.getHeaderOrEmpty("content-type"), "base64");
            BasicMimeParser mp = new BasicMimeParser(mimeHeaders, resp.getInputStream());
            final Object[] parts = new Object[2];
            mp.parse(new MimeParserListener(){

                @Override
                public MimeContext createContext(MimeParserContext parserContext, Headers headers) {
                    return ConstantMimeContext.Instance;
                }

                @Override
                public void object(MimeParserContext parserContext, Headers headers, InputStream inputStream) throws IOException {
                    if (headers.getContentType().contains("application/pkcs8")) {
                        ASN1InputStream asn1In = new ASN1InputStream(inputStream);
                        parts[0] = PrivateKeyInfo.getInstance((Object)asn1In.readObject());
                        if (asn1In.readObject() != null) {
                            throw new ESTException("Unexpected ASN1 object after private key info");
                        }
                    } else if (headers.getContentType().contains("application/pkcs7-mime")) {
                        ASN1InputStream asn1In = new ASN1InputStream(inputStream);
                        try {
                            parts[1] = new SimplePKIResponse(ContentInfo.getInstance((Object)asn1In.readObject()));
                        }
                        catch (CMCException e) {
                            throw new IOException(e.getMessage());
                        }
                        if (asn1In.readObject() != null) {
                            throw new ESTException("Unexpected ASN1 object after reading certificates");
                        }
                    }
                }
            });
            if (parts[0] == null || parts[1] == null) {
                throw new ESTException("received neither private key info and certificates");
            }
            enrolled = ((SimplePKIResponse)parts[1]).getCertificates();
            return new EnrollmentResponse(enrolled, -1L, null, resp.getSource(), PrivateKeyInfo.getInstance((Object)parts[0]));
        }
        if (resp.getStatusCode() == 200) {
            ASN1InputStream ain = new ASN1InputStream(resp.getInputStream());
            SimplePKIResponse spkr = null;
            try {
                spkr = new SimplePKIResponse(ContentInfo.getInstance((Object)ain.readObject()));
            }
            catch (CMCException e) {
                throw new ESTException(e.getMessage(), e.getCause());
            }
            enrolled = spkr.getCertificates();
            return new EnrollmentResponse(enrolled, -1L, null, resp.getSource());
        }
        throw new ESTException("Simple Enroll: " + req.getURL().toString(), null, resp.getStatusCode(), resp.getInputStream());
    }

    /*
     * Unable to fully structure code
     */
    public CSRRequestResponse getCSRAttributes() throws ESTException {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        resp = null;
        response = null;
        finalThrowable = null;
        url = null;
        try {
            url = new URL(this.server + "/csrattrs");
            client = this.clientProvider.makeClient();
            req = new ESTRequestBuilder("GET", url).withClient(client).build();
            resp = client.doRequest(req);
            switch (resp.getStatusCode()) {
                case 200: {
                    try {
                        ain = this.getASN1InputStream(resp.getInputStream(), resp.getContentLength());
                        seq = ASN1Sequence.getInstance((Object)ain.readObject());
                        response = new CSRAttributesResponse(CsrAttrs.getInstance((Object)seq));
                        ** break;
lbl19:
                        // 1 sources

                        break;
                    }
                    catch (Throwable ex) {
                        throw new ESTException("Decoding CACerts: " + url.toString() + " " + ex.getMessage(), ex, resp.getStatusCode(), resp.getInputStream());
                    }
                }
                case 204: {
                    response = null;
                    ** break;
lbl25:
                    // 1 sources

                    break;
                }
                case 404: {
                    response = null;
                    ** break;
lbl29:
                    // 1 sources

                    break;
                }
                default: {
                    throw new ESTException("CSR Attribute request: " + req.getURL().toString(), null, resp.getStatusCode(), resp.getInputStream());
                }
            }
        }
        catch (Throwable t) {
            if (t instanceof ESTException) {
                throw (ESTException)t;
            }
            throw new ESTException(t.getMessage(), t);
        }
        finally {
            if (resp != null) {
                try {
                    resp.close();
                }
                catch (Exception ex) {
                    finalThrowable = ex;
                }
            }
        }
        if (finalThrowable != null) {
            if (finalThrowable instanceof ESTException) {
                throw (ESTException)finalThrowable;
            }
            throw new ESTException(finalThrowable.getMessage(), (Throwable)finalThrowable, resp.getStatusCode(), null);
        }
        return new CSRRequestResponse(response, resp.getSource());
    }

    private String annotateRequest(byte[] data) {
        int i = 0;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        do {
            if (i + 48 < data.length) {
                pw.print(Base64.toBase64String((byte[])data, (int)i, (int)48));
                i += 48;
            } else {
                pw.print(Base64.toBase64String((byte[])data, (int)i, (int)(data.length - i)));
                i = data.length;
            }
            pw.print('\n');
        } while (i < data.length);
        pw.flush();
        return sw.toString();
    }

    private String verifyLabel(String label) {
        while (label.endsWith("/") && label.length() > 0) {
            label = label.substring(0, label.length() - 1);
        }
        while (label.startsWith("/") && label.length() > 0) {
            label = label.substring(1);
        }
        if (label.length() == 0) {
            throw new IllegalArgumentException("Label set but after trimming '/' is not zero length string.");
        }
        if (!pathInValid.matcher(label).matches()) {
            throw new IllegalArgumentException("Server path " + label + " contains invalid characters");
        }
        if (illegalParts.contains(label)) {
            throw new IllegalArgumentException("Label " + label + " is a reserved path segment.");
        }
        return label;
    }

    private String verifyServer(String server) {
        try {
            while (server.endsWith("/") && server.length() > 0) {
                server = server.substring(0, server.length() - 1);
            }
            if (server.contains("://")) {
                throw new IllegalArgumentException("Server contains scheme, must only be <dnsname/ipaddress>:port, https:// will be added arbitrarily.");
            }
            URL u = new URL("https://" + server);
            if (u.getPath().length() == 0 || u.getPath().equals("/")) {
                return server;
            }
            throw new IllegalArgumentException("Server contains path, must only be <dnsname/ipaddress>:port, a path of '/.well-known/est/<label>' will be added arbitrarily.");
        }
        catch (Exception ex) {
            if (ex instanceof IllegalArgumentException) {
                throw (IllegalArgumentException)ex;
            }
            throw new IllegalArgumentException("Scheme and host is invalid: " + ex.getMessage(), ex);
        }
    }

    static {
        illegalParts.add(CACERTS.substring(1));
        illegalParts.add(SIMPLE_ENROLL.substring(1));
        illegalParts.add(SIMPLE_REENROLL.substring(1));
        illegalParts.add(FULLCMC.substring(1));
        illegalParts.add(SERVERGEN.substring(1));
        illegalParts.add(CSRATTRS.substring(1));
        pathInValid = Pattern.compile("^[0-9a-zA-Z_\\-.~!$&'()*+,;:=]+");
    }
}

