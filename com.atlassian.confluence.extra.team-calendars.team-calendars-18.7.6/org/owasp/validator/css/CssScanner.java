/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.InputSource
 */
package org.owasp.validator.css;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.batik.css.parser.Parser;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;
import org.owasp.validator.css.CssHandler;
import org.owasp.validator.css.CssParser;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import org.w3c.css.sac.InputSource;

public class CssScanner {
    protected static final Timeout DEFAULT_TIMEOUT = Timeout.ofMilliseconds(1000L);
    private static final String CDATA = "^\\s*<!\\[CDATA\\[(.*)\\]\\]>\\s*$";
    private final Parser parser = new CssParser();
    private final InternalPolicy policy;
    private final ResourceBundle messages;
    private final boolean shouldParseImportedStyles;
    private static final Pattern cdataMatchPattern = Pattern.compile("^\\s*<!\\[CDATA\\[(.*)\\]\\]>\\s*$", 32);

    public CssScanner(InternalPolicy policy, ResourceBundle messages) {
        this(policy, messages, false);
    }

    public CssScanner(InternalPolicy policy, ResourceBundle messages, boolean shouldParseImportedStyles) {
        this.policy = policy;
        this.messages = messages;
        this.shouldParseImportedStyles = shouldParseImportedStyles;
    }

    public CleanResults scanStyleSheet(String taintedCss, int sizeLimit) throws ScanException {
        long startOfScan = System.currentTimeMillis();
        ArrayList<String> errorMessages = new ArrayList<String>();
        Matcher m = cdataMatchPattern.matcher(taintedCss);
        boolean isCdata = m.matches();
        if (isCdata) {
            taintedCss = m.group(1);
        }
        CssHandler handler = new CssHandler(this.policy, errorMessages, this.messages);
        this.parser.setDocumentHandler(handler);
        try {
            this.parser.parseStyleSheet(new InputSource((Reader)new StringReader(taintedCss)));
        }
        catch (IOException | org.apache.batik.css.parser.ParseException e) {
            throw new ScanException(e);
        }
        String cleaned = this.getCleanStylesheetWithImports(sizeLimit, errorMessages, handler);
        if (isCdata && !this.policy.isUseXhtml()) {
            cleaned = "<![CDATA[[" + cleaned + "]]>";
        }
        return new CleanResults(startOfScan, cleaned, null, errorMessages);
    }

    public CleanResults scanInlineStyle(String taintedCss, String tagName, int sizeLimit) throws ScanException {
        long startOfScan = System.currentTimeMillis();
        ArrayList<String> errorMessages = new ArrayList<String>();
        CssHandler handler = new CssHandler((Policy)this.policy, errorMessages, this.messages, tagName);
        this.parser.setDocumentHandler(handler);
        try {
            this.parser.parseStyleDeclaration(taintedCss);
        }
        catch (IOException ioe) {
            throw new ScanException(ioe);
        }
        String cleaned = this.getCleanStylesheetWithImports(sizeLimit, errorMessages, handler);
        return new CleanResults(startOfScan, cleaned, null, errorMessages);
    }

    private String getCleanStylesheetWithImports(int sizeLimit, List<String> errorMessages, CssHandler handler) throws ScanException {
        String cleaned = handler.getCleanStylesheet();
        if (this.shouldParseImportedStyles) {
            handler.emptyStyleSheet();
            this.parseImportedStylesheets(handler.getImportedStylesheetsURIList(), errorMessages, sizeLimit);
            cleaned = handler.getCleanStylesheet() + cleaned;
        }
        return cleaned;
    }

    private void parseImportedStylesheets(LinkedList<URI> stylesheets, List<String> errorMessages, int sizeLimit) throws ScanException {
        if (!stylesheets.isEmpty()) {
            int importedStylesheets = 0;
            Timeout timeout = DEFAULT_TIMEOUT;
            try {
                timeout = Timeout.ofMilliseconds(Long.parseLong(this.policy.getDirective("connectionTimeout")));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setResponseTimeout(timeout).setConnectionRequestTimeout(timeout).build();
            CloseableHttpClient httpClient = HttpClientBuilder.create().disableAutomaticRetries().disableConnectionState().disableCookieManagement().setDefaultRequestConfig(requestConfig).build();
            int allowedImports = 1;
            try {
                allowedImports = Integer.parseInt(this.policy.getDirective("maxStyleSheetImports"));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            while (!stylesheets.isEmpty()) {
                URI stylesheetUri = stylesheets.removeFirst();
                if (++importedStylesheets > allowedImports) {
                    errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.import.exceeded", new Object[]{HTMLEntityEncoder.htmlEntityEncode(stylesheetUri.toString()), String.valueOf(allowedImports)}));
                    continue;
                }
                HttpClientResponseHandler<String> responseHandler = new HttpClientResponseHandler<String>(){

                    @Override
                    public String handleResponse(ClassicHttpResponse response) throws IOException {
                        int status = response.getCode();
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            try {
                                return entity != null ? EntityUtils.toString(entity) : null;
                            }
                            catch (org.apache.batik.css.parser.ParseException | ParseException ex) {
                                throw new ClientProtocolException(ex);
                            }
                        }
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                };
                byte[] stylesheet = null;
                try {
                    String responseBody = httpClient.execute((ClassicHttpRequest)new HttpGet(stylesheetUri), responseHandler);
                    stylesheet = responseBody.getBytes(Charset.forName("UTF8"));
                    if (stylesheet != null && stylesheet.length > sizeLimit) {
                        errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.import.toolarge", new Object[]{HTMLEntityEncoder.htmlEntityEncode(stylesheetUri.toString()), String.valueOf(this.policy.getMaxInputSize())}));
                        stylesheet = null;
                    }
                }
                catch (IOException ioe) {
                    errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.import.failure", new Object[]{HTMLEntityEncoder.htmlEntityEncode(stylesheetUri.toString())}));
                }
                if (stylesheet == null) continue;
                sizeLimit -= stylesheet.length;
                try {
                    InputSource nextStyleSheet = new InputSource((Reader)new InputStreamReader((InputStream)new ByteArrayInputStream(stylesheet), Charset.forName("UTF8")));
                    this.parser.parseStyleSheet(nextStyleSheet);
                }
                catch (IOException ioe) {
                    throw new ScanException(ioe);
                }
            }
        }
    }
}

