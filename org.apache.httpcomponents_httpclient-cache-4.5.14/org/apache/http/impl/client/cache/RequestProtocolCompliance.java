/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HeaderElement
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpEntityEnclosingRequest
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.HttpVersion
 *  org.apache.http.ProtocolVersion
 *  org.apache.http.StatusLine
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.client.ClientProtocolException
 *  org.apache.http.client.methods.HttpRequestWrapper
 *  org.apache.http.entity.ContentType
 *  org.apache.http.entity.HttpEntityWrapper
 *  org.apache.http.message.BasicHeader
 *  org.apache.http.message.BasicHttpResponse
 *  org.apache.http.message.BasicStatusLine
 */
package org.apache.http.impl.client.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.cache.RequestProtocolError;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
class RequestProtocolCompliance {
    private final boolean weakETagOnPutDeleteAllowed;
    private static final List<String> disallowedWithNoCache = Arrays.asList("min-fresh", "max-stale", "max-age");

    public RequestProtocolCompliance() {
        this.weakETagOnPutDeleteAllowed = false;
    }

    public RequestProtocolCompliance(boolean weakETagOnPutDeleteAllowed) {
        this.weakETagOnPutDeleteAllowed = weakETagOnPutDeleteAllowed;
    }

    public List<RequestProtocolError> requestIsFatallyNonCompliant(HttpRequest request) {
        ArrayList<RequestProtocolError> theErrors = new ArrayList<RequestProtocolError>();
        RequestProtocolError anError = this.requestHasWeakETagAndRange(request);
        if (anError != null) {
            theErrors.add(anError);
        }
        if (!this.weakETagOnPutDeleteAllowed && (anError = this.requestHasWeekETagForPUTOrDELETEIfMatch(request)) != null) {
            theErrors.add(anError);
        }
        if ((anError = this.requestContainsNoCacheDirectiveWithFieldName(request)) != null) {
            theErrors.add(anError);
        }
        return theErrors;
    }

    public void makeRequestCompliant(HttpRequestWrapper request) throws ClientProtocolException {
        if (this.requestMustNotHaveEntity((HttpRequest)request)) {
            ((HttpEntityEnclosingRequest)request).setEntity(null);
        }
        this.verifyRequestWithExpectContinueFlagHas100continueHeader((HttpRequest)request);
        this.verifyOPTIONSRequestWithBodyHasContentType((HttpRequest)request);
        this.decrementOPTIONSMaxForwardsIfGreaterThen0((HttpRequest)request);
        this.stripOtherFreshnessDirectivesWithNoCache((HttpRequest)request);
        if (this.requestVersionIsTooLow((HttpRequest)request) || this.requestMinorVersionIsTooHighMajorVersionsMatch((HttpRequest)request)) {
            request.setProtocolVersion((ProtocolVersion)HttpVersion.HTTP_1_1);
        }
    }

    private void stripOtherFreshnessDirectivesWithNoCache(HttpRequest request) {
        ArrayList<HeaderElement> outElts = new ArrayList<HeaderElement>();
        boolean shouldStrip = false;
        for (Header h : request.getHeaders("Cache-Control")) {
            for (HeaderElement elt : h.getElements()) {
                if (!disallowedWithNoCache.contains(elt.getName())) {
                    outElts.add(elt);
                }
                if (!"no-cache".equals(elt.getName())) continue;
                shouldStrip = true;
            }
        }
        if (!shouldStrip) {
            return;
        }
        request.removeHeaders("Cache-Control");
        request.setHeader("Cache-Control", this.buildHeaderFromElements(outElts));
    }

    private String buildHeaderFromElements(List<HeaderElement> outElts) {
        StringBuilder newHdr = new StringBuilder("");
        boolean first = true;
        for (HeaderElement elt : outElts) {
            if (!first) {
                newHdr.append(",");
            } else {
                first = false;
            }
            newHdr.append(elt.toString());
        }
        return newHdr.toString();
    }

    private boolean requestMustNotHaveEntity(HttpRequest request) {
        return "TRACE".equals(request.getRequestLine().getMethod()) && request instanceof HttpEntityEnclosingRequest;
    }

    private void decrementOPTIONSMaxForwardsIfGreaterThen0(HttpRequest request) {
        if (!"OPTIONS".equals(request.getRequestLine().getMethod())) {
            return;
        }
        Header maxForwards = request.getFirstHeader("Max-Forwards");
        if (maxForwards == null) {
            return;
        }
        request.removeHeaders("Max-Forwards");
        int currentMaxForwards = Integer.parseInt(maxForwards.getValue());
        request.setHeader("Max-Forwards", Integer.toString(currentMaxForwards - 1));
    }

    private void verifyOPTIONSRequestWithBodyHasContentType(HttpRequest request) {
        if (!"OPTIONS".equals(request.getRequestLine().getMethod())) {
            return;
        }
        if (!(request instanceof HttpEntityEnclosingRequest)) {
            return;
        }
        this.addContentTypeHeaderIfMissing((HttpEntityEnclosingRequest)request);
    }

    private void addContentTypeHeaderIfMissing(HttpEntityEnclosingRequest request) {
        HttpEntity entity = request.getEntity();
        if (entity != null && entity.getContentType() == null) {
            HttpEntityWrapper entityWrapper = new HttpEntityWrapper(entity){

                public Header getContentType() {
                    return new BasicHeader("Content-Type", ContentType.APPLICATION_OCTET_STREAM.getMimeType());
                }
            };
            request.setEntity((HttpEntity)entityWrapper);
        }
    }

    private void verifyRequestWithExpectContinueFlagHas100continueHeader(HttpRequest request) {
        if (request instanceof HttpEntityEnclosingRequest) {
            if (((HttpEntityEnclosingRequest)request).expectContinue() && ((HttpEntityEnclosingRequest)request).getEntity() != null) {
                this.add100ContinueHeaderIfMissing(request);
            } else {
                this.remove100ContinueHeaderIfExists(request);
            }
        } else {
            this.remove100ContinueHeaderIfExists(request);
        }
    }

    private void remove100ContinueHeaderIfExists(HttpRequest request) {
        boolean hasHeader = false;
        Header[] expectHeaders = request.getHeaders("Expect");
        ArrayList<HeaderElement> expectElementsThatAreNot100Continue = new ArrayList<HeaderElement>();
        for (Header h : expectHeaders) {
            for (HeaderElement elt : h.getElements()) {
                if (!"100-continue".equalsIgnoreCase(elt.getName())) {
                    expectElementsThatAreNot100Continue.add(elt);
                    continue;
                }
                hasHeader = true;
            }
            if (hasHeader) {
                request.removeHeader(h);
                for (HeaderElement elt : expectElementsThatAreNot100Continue) {
                    BasicHeader newHeader = new BasicHeader("Expect", elt.getName());
                    request.addHeader((Header)newHeader);
                }
                return;
            }
            expectElementsThatAreNot100Continue = new ArrayList();
        }
    }

    private void add100ContinueHeaderIfMissing(HttpRequest request) {
        boolean hasHeader = false;
        for (Header h : request.getHeaders("Expect")) {
            for (HeaderElement elt : h.getElements()) {
                if (!"100-continue".equalsIgnoreCase(elt.getName())) continue;
                hasHeader = true;
            }
        }
        if (!hasHeader) {
            request.addHeader("Expect", "100-continue");
        }
    }

    protected boolean requestMinorVersionIsTooHighMajorVersionsMatch(HttpRequest request) {
        ProtocolVersion requestProtocol = request.getProtocolVersion();
        if (requestProtocol.getMajor() != HttpVersion.HTTP_1_1.getMajor()) {
            return false;
        }
        return requestProtocol.getMinor() > HttpVersion.HTTP_1_1.getMinor();
    }

    protected boolean requestVersionIsTooLow(HttpRequest request) {
        return request.getProtocolVersion().compareToVersion((ProtocolVersion)HttpVersion.HTTP_1_1) < 0;
    }

    public HttpResponse getErrorForRequest(RequestProtocolError errorCheck) {
        switch (errorCheck) {
            case BODY_BUT_NO_LENGTH_ERROR: {
                return new BasicHttpResponse((StatusLine)new BasicStatusLine((ProtocolVersion)HttpVersion.HTTP_1_1, 411, ""));
            }
            case WEAK_ETAG_AND_RANGE_ERROR: {
                return new BasicHttpResponse((StatusLine)new BasicStatusLine((ProtocolVersion)HttpVersion.HTTP_1_1, 400, "Weak eTag not compatible with byte range"));
            }
            case WEAK_ETAG_ON_PUTDELETE_METHOD_ERROR: {
                return new BasicHttpResponse((StatusLine)new BasicStatusLine((ProtocolVersion)HttpVersion.HTTP_1_1, 400, "Weak eTag not compatible with PUT or DELETE requests"));
            }
            case NO_CACHE_DIRECTIVE_WITH_FIELD_NAME: {
                return new BasicHttpResponse((StatusLine)new BasicStatusLine((ProtocolVersion)HttpVersion.HTTP_1_1, 400, "No-Cache directive MUST NOT include a field name"));
            }
        }
        throw new IllegalStateException("The request was compliant, therefore no error can be generated for it.");
    }

    private RequestProtocolError requestHasWeakETagAndRange(HttpRequest request) {
        String method = request.getRequestLine().getMethod();
        if (!"GET".equals(method)) {
            return null;
        }
        Header range = request.getFirstHeader("Range");
        if (range == null) {
            return null;
        }
        Header ifRange = request.getFirstHeader("If-Range");
        if (ifRange == null) {
            return null;
        }
        String val = ifRange.getValue();
        if (val.startsWith("W/")) {
            return RequestProtocolError.WEAK_ETAG_AND_RANGE_ERROR;
        }
        return null;
    }

    private RequestProtocolError requestHasWeekETagForPUTOrDELETEIfMatch(HttpRequest request) {
        String method = request.getRequestLine().getMethod();
        if (!"PUT".equals(method) && !"DELETE".equals(method)) {
            return null;
        }
        Header ifMatch = request.getFirstHeader("If-Match");
        if (ifMatch != null) {
            String val = ifMatch.getValue();
            if (val.startsWith("W/")) {
                return RequestProtocolError.WEAK_ETAG_ON_PUTDELETE_METHOD_ERROR;
            }
        } else {
            Header ifNoneMatch = request.getFirstHeader("If-None-Match");
            if (ifNoneMatch == null) {
                return null;
            }
            String val2 = ifNoneMatch.getValue();
            if (val2.startsWith("W/")) {
                return RequestProtocolError.WEAK_ETAG_ON_PUTDELETE_METHOD_ERROR;
            }
        }
        return null;
    }

    private RequestProtocolError requestContainsNoCacheDirectiveWithFieldName(HttpRequest request) {
        for (Header h : request.getHeaders("Cache-Control")) {
            for (HeaderElement elt : h.getElements()) {
                if (!"no-cache".equalsIgnoreCase(elt.getName()) || elt.getValue() == null) continue;
                return RequestProtocolError.NO_CACHE_DIRECTIVE_WITH_FIELD_NAME;
            }
        }
        return null;
    }
}

