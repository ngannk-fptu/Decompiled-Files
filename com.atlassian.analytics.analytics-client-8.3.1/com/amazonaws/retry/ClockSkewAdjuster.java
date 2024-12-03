/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.Header
 *  org.apache.http.HttpResponse
 */
package com.amazonaws.retry;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.Request;
import com.amazonaws.SdkBaseException;
import com.amazonaws.annotation.NotThreadSafe;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.annotation.SdkTestInternalApi;
import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.retry.RetryUtils;
import com.amazonaws.util.DateUtils;
import com.amazonaws.util.ValidationUtils;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

@ThreadSafe
@SdkInternalApi
public final class ClockSkewAdjuster {
    private static final Log log = LogFactory.getLog(ClockSkewAdjuster.class);
    private static final Set<Integer> AUTHENTICATION_ERROR_STATUS_CODES;
    private static final int CLOCK_SKEW_ADJUST_THRESHOLD_IN_SECONDS = 240;
    private volatile Integer estimatedSkew;

    public Integer getEstimatedSkew() {
        return this.estimatedSkew;
    }

    public void updateEstimatedSkew(AdjustmentRequest adjustmentRequest) {
        try {
            Date serverDate = this.getServerDate(adjustmentRequest);
            if (serverDate != null) {
                this.estimatedSkew = this.timeSkewInSeconds(this.getCurrentDate(adjustmentRequest), serverDate);
            }
        }
        catch (RuntimeException exception) {
            log.debug((Object)"Unable to update estimated skew.", (Throwable)exception);
        }
    }

    public ClockSkewAdjustment getAdjustment(AdjustmentRequest adjustmentRequest) {
        ValidationUtils.assertNotNull(adjustmentRequest, "adjustmentRequest");
        ValidationUtils.assertNotNull(adjustmentRequest.exception, "adjustmentRequest.exception");
        ValidationUtils.assertNotNull(adjustmentRequest.clientRequest, "adjustmentRequest.clientRequest");
        ValidationUtils.assertNotNull(adjustmentRequest.serviceResponse, "adjustmentRequest.serviceResponse");
        int timeSkewInSeconds = 0;
        boolean isAdjustmentRecommended = false;
        try {
            Date serverDate;
            if (this.isAdjustmentRecommended(adjustmentRequest) && (serverDate = this.getServerDate(adjustmentRequest)) != null) {
                timeSkewInSeconds = this.timeSkewInSeconds(this.getCurrentDate(adjustmentRequest), serverDate);
                isAdjustmentRecommended = true;
            }
        }
        catch (RuntimeException e) {
            log.warn((Object)"Unable to correct for clock skew.", (Throwable)e);
        }
        return new ClockSkewAdjustment(isAdjustmentRecommended, timeSkewInSeconds);
    }

    private boolean isAdjustmentRecommended(AdjustmentRequest adjustmentRequest) {
        if (!(adjustmentRequest.exception instanceof AmazonServiceException)) {
            return false;
        }
        AmazonServiceException exception = (AmazonServiceException)adjustmentRequest.exception;
        return this.isDefinitelyClockSkewError(exception) || this.mayBeClockSkewError(exception) && this.clientRequestWasSkewed(adjustmentRequest);
    }

    private boolean isDefinitelyClockSkewError(AmazonServiceException exception) {
        return RetryUtils.isClockSkewError(exception);
    }

    private boolean mayBeClockSkewError(AmazonServiceException exception) {
        return AUTHENTICATION_ERROR_STATUS_CODES.contains(exception.getStatusCode());
    }

    private boolean clientRequestWasSkewed(AdjustmentRequest adjustmentRequest) {
        Date serverDate = this.getServerDate(adjustmentRequest);
        if (serverDate == null) {
            return false;
        }
        int requestClockSkew = this.timeSkewInSeconds(this.getClientDate(adjustmentRequest), serverDate);
        return Math.abs(requestClockSkew) > 240;
    }

    private int timeSkewInSeconds(Date clientTime, Date serverTime) {
        ValidationUtils.assertNotNull(clientTime, "clientTime");
        ValidationUtils.assertNotNull(serverTime, "serverTime");
        long value = (clientTime.getTime() - serverTime.getTime()) / 1000L;
        if ((long)((int)value) != value) {
            throw new IllegalStateException("Time is too skewed to adjust: (clientTime: " + clientTime.getTime() + ", serverTime: " + serverTime.getTime() + ")");
        }
        return (int)value;
    }

    private Date getCurrentDate(AdjustmentRequest adjustmentRequest) {
        return new Date(adjustmentRequest.currentTime);
    }

    private Date getClientDate(AdjustmentRequest adjustmentRequest) {
        return new Date(adjustmentRequest.currentTime - (long)(adjustmentRequest.clientRequest.getTimeOffset() * 1000));
    }

    private Date getServerDate(AdjustmentRequest adjustmentRequest) {
        String serverDateStr = null;
        try {
            Header[] responseDateHeader = adjustmentRequest.serviceResponse.getHeaders("Date");
            if (responseDateHeader.length > 0) {
                serverDateStr = responseDateHeader[0].getValue();
                log.debug((Object)("Reported server date (from 'Date' header): " + serverDateStr));
                return DateUtils.parseRFC822Date(serverDateStr);
            }
            if (adjustmentRequest.exception == null) {
                return null;
            }
            String exceptionMessage = adjustmentRequest.exception.getMessage();
            serverDateStr = this.getServerDateFromException(exceptionMessage);
            if (serverDateStr != null) {
                log.debug((Object)("Reported server date (from exception message): " + serverDateStr));
                return DateUtils.parseCompressedISO8601Date(serverDateStr);
            }
            log.debug((Object)"Server did not return a date, so clock skew adjustments will not be applied.");
            return null;
        }
        catch (RuntimeException e) {
            log.warn((Object)("Unable to parse clock skew offset from response: " + serverDateStr), (Throwable)e);
            return null;
        }
    }

    private String getServerDateFromException(String body) {
        int startPos = body.indexOf("(");
        int endPos = body.indexOf(" + ");
        if (endPos == -1) {
            endPos = body.indexOf(" - ");
        }
        return endPos == -1 ? null : body.substring(startPos + 1, endPos);
    }

    static {
        HashSet<Integer> statusCodes = new HashSet<Integer>();
        statusCodes.add(401);
        statusCodes.add(403);
        AUTHENTICATION_ERROR_STATUS_CODES = Collections.unmodifiableSet(statusCodes);
    }

    @ThreadSafe
    public static final class ClockSkewAdjustment {
        private final boolean shouldAdjustForSkew;
        private final int adjustmentInSeconds;

        private ClockSkewAdjustment(boolean shouldAdjust, int adjustmentInSeconds) {
            this.shouldAdjustForSkew = shouldAdjust;
            this.adjustmentInSeconds = adjustmentInSeconds;
        }

        public boolean shouldAdjustForSkew() {
            return this.shouldAdjustForSkew;
        }

        public int inSeconds() {
            if (!this.shouldAdjustForSkew) {
                throw new IllegalStateException("An adjustment is not recommended.");
            }
            return this.adjustmentInSeconds;
        }
    }

    @NotThreadSafe
    public static final class AdjustmentRequest {
        private Request<?> clientRequest;
        private HttpResponse serviceResponse;
        private SdkBaseException exception;
        private long currentTime = System.currentTimeMillis();

        public AdjustmentRequest clientRequest(Request<?> clientRequest) {
            this.clientRequest = clientRequest;
            return this;
        }

        public AdjustmentRequest serviceResponse(HttpResponse serviceResponse) {
            this.serviceResponse = serviceResponse;
            return this;
        }

        public AdjustmentRequest exception(SdkBaseException exception) {
            this.exception = exception;
            return this;
        }

        @SdkTestInternalApi
        public AdjustmentRequest currentTime(long currentTime) {
            this.currentTime = currentTime;
            return this;
        }
    }
}

