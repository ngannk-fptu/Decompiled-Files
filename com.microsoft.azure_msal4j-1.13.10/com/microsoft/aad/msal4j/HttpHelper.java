/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.HttpEvent;
import com.microsoft.aad.msal4j.HttpRequest;
import com.microsoft.aad.msal4j.HttpUtils;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAcquireTokenParameters;
import com.microsoft.aad.msal4j.IHttpClient;
import com.microsoft.aad.msal4j.IHttpResponse;
import com.microsoft.aad.msal4j.LogHelper;
import com.microsoft.aad.msal4j.MsalClientException;
import com.microsoft.aad.msal4j.MsalThrottlingException;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.RequestContext;
import com.microsoft.aad.msal4j.ServiceBundle;
import com.microsoft.aad.msal4j.SilentParameters;
import com.microsoft.aad.msal4j.StringHelper;
import com.microsoft.aad.msal4j.TelemetryHelper;
import com.microsoft.aad.msal4j.ThrottlingCache;
import com.microsoft.aad.msal4j.XmsClientTelemetryInfo;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HttpHelper {
    private static final Logger log = LoggerFactory.getLogger(HttpHelper.class);
    public static final String RETRY_AFTER_HEADER = "Retry-After";
    public static final int RETRY_NUM = 2;
    public static final int RETRY_DELAY_MS = 1000;
    public static final int HTTP_STATUS_200 = 200;
    public static final int HTTP_STATUS_400 = 400;
    public static final int HTTP_STATUS_429 = 429;
    public static final int HTTP_STATUS_500 = 500;

    private HttpHelper() {
    }

    static IHttpResponse executeHttpRequest(HttpRequest httpRequest, RequestContext requestContext, ServiceBundle serviceBundle) {
        IHttpResponse httpResponse;
        HttpHelper.checkForThrottling(requestContext);
        HttpEvent httpEvent = new HttpEvent();
        try (TelemetryHelper telemetryHelper = serviceBundle.getTelemetryManager().createTelemetryHelper(requestContext.telemetryRequestId(), requestContext.clientId(), httpEvent, false);){
            HttpHelper.addRequestInfoToTelemetry(httpRequest, httpEvent);
            try {
                IHttpClient httpClient = serviceBundle.getHttpClient();
                httpResponse = HttpHelper.executeHttpRequestWithRetries(httpRequest, httpClient);
            }
            catch (Exception e) {
                httpEvent.setOauthErrorCode("unknown");
                throw new MsalClientException(e);
            }
            HttpHelper.addResponseInfoToTelemetry(httpResponse, httpEvent);
            if (httpResponse.headers() != null) {
                HttpHelper.verifyReturnedCorrelationId(httpRequest, httpResponse);
            }
        }
        HttpHelper.processThrottlingInstructions(httpResponse, requestContext);
        return httpResponse;
    }

    private static String getRequestThumbprint(RequestContext requestContext) {
        IAccount account;
        StringBuilder sb = new StringBuilder();
        sb.append(requestContext.clientId() + ".");
        sb.append(requestContext.authority() + ".");
        IAcquireTokenParameters apiParameters = requestContext.apiParameters();
        if (apiParameters instanceof SilentParameters && (account = ((SilentParameters)apiParameters).account()) != null) {
            sb.append(account.homeAccountId() + ".");
        }
        TreeSet<String> sortedScopes = new TreeSet<String>(apiParameters.scopes());
        sb.append(String.join((CharSequence)" ", sortedScopes));
        return StringHelper.createSha256Hash(sb.toString());
    }

    private static boolean isRetryable(IHttpResponse httpResponse) {
        return httpResponse.statusCode() >= 500 && HttpHelper.getRetryAfterHeader(httpResponse) == null;
    }

    private static IHttpResponse executeHttpRequestWithRetries(HttpRequest httpRequest, IHttpClient httpClient) throws Exception {
        IHttpResponse httpResponse = null;
        for (int i = 0; i < 2 && HttpHelper.isRetryable(httpResponse = httpClient.send(httpRequest)); ++i) {
            Thread.sleep(1000L);
        }
        return httpResponse;
    }

    private static void checkForThrottling(RequestContext requestContext) {
        String requestThumbprint;
        long retryInMs;
        if (requestContext.clientApplication() instanceof PublicClientApplication && requestContext.apiParameters() != null && (retryInMs = ThrottlingCache.retryInMs(requestThumbprint = HttpHelper.getRequestThumbprint(requestContext))) > 0L) {
            throw new MsalThrottlingException(retryInMs);
        }
    }

    private static void processThrottlingInstructions(IHttpResponse httpResponse, RequestContext requestContext) {
        if (requestContext.clientApplication() instanceof PublicClientApplication) {
            Long expirationTimestamp = null;
            Integer retryAfterHeaderVal = HttpHelper.getRetryAfterHeader(httpResponse);
            if (retryAfterHeaderVal != null) {
                expirationTimestamp = System.currentTimeMillis() + (long)(retryAfterHeaderVal * 1000);
            } else if (httpResponse.statusCode() == 429 || httpResponse.statusCode() >= 500) {
                expirationTimestamp = System.currentTimeMillis() + (long)(ThrottlingCache.DEFAULT_THROTTLING_TIME_SEC * 1000);
            }
            if (expirationTimestamp != null) {
                ThrottlingCache.set(HttpHelper.getRequestThumbprint(requestContext), expirationTimestamp);
            }
        }
    }

    private static Integer getRetryAfterHeader(IHttpResponse httpResponse) {
        if (httpResponse.headers() != null) {
            TreeMap<String, List<String>> headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
            headers.putAll(httpResponse.headers());
            if (headers.containsKey(RETRY_AFTER_HEADER) && ((List)headers.get(RETRY_AFTER_HEADER)).size() == 1) {
                try {
                    int headerValue = Integer.parseInt((String)((List)headers.get(RETRY_AFTER_HEADER)).get(0));
                    if (headerValue > 0 && headerValue <= 3600) {
                        return headerValue;
                    }
                }
                catch (NumberFormatException ex) {
                    log.warn("Failed to parse value of Retry-After header - NumberFormatException");
                }
            }
        }
        return null;
    }

    private static void addRequestInfoToTelemetry(HttpRequest httpRequest, HttpEvent httpEvent) {
        try {
            httpEvent.setHttpPath(httpRequest.url().toURI());
            httpEvent.setHttpMethod(httpRequest.httpMethod().toString());
            if (!StringHelper.isBlank(httpRequest.url().getQuery())) {
                httpEvent.setQueryParameters(httpRequest.url().getQuery());
            }
        }
        catch (Exception ex) {
            String correlationId = httpRequest.headerValue("client-request-id");
            log.warn(LogHelper.createMessage("Setting URL telemetry fields failed: " + LogHelper.getPiiScrubbedDetails(ex), correlationId != null ? correlationId : ""));
        }
    }

    private static void addResponseInfoToTelemetry(IHttpResponse httpResponse, HttpEvent httpEvent) {
        XmsClientTelemetryInfo xmsClientTelemetryInfo;
        String xMsClientTelemetry;
        String xMsRequestId;
        httpEvent.setHttpResponseStatus(httpResponse.statusCode());
        Map<String, List<String>> headers = httpResponse.headers();
        String userAgent = HttpUtils.headerValue(headers, "User-Agent");
        if (!StringHelper.isBlank(userAgent)) {
            httpEvent.setUserAgent(userAgent);
        }
        if (!StringHelper.isBlank(xMsRequestId = HttpUtils.headerValue(headers, "x-ms-request-id"))) {
            httpEvent.setRequestIdHeader(xMsRequestId);
        }
        if ((xMsClientTelemetry = HttpUtils.headerValue(headers, "x-ms-clitelem")) != null && (xmsClientTelemetryInfo = XmsClientTelemetryInfo.parseXmsTelemetryInfo(xMsClientTelemetry)) != null) {
            httpEvent.setXmsClientTelemetryInfo(xmsClientTelemetryInfo);
        }
    }

    private static void verifyReturnedCorrelationId(HttpRequest httpRequest, IHttpResponse httpResponse) {
        String sentCorrelationId = httpRequest.headerValue("client-request-id");
        String returnedCorrelationId = HttpUtils.headerValue(httpResponse.headers(), "client-request-id");
        if (StringHelper.isBlank(returnedCorrelationId) || !returnedCorrelationId.equals(sentCorrelationId)) {
            String msg = LogHelper.createMessage(String.format("Sent (%s) Correlation Id is not same as received (%s).", sentCorrelationId, returnedCorrelationId), sentCorrelationId);
            log.info(msg);
        }
    }
}

