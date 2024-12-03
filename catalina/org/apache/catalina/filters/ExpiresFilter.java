/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.WriteListener
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  javax.servlet.http.MappingMatch
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.MappingMatch;
import org.apache.catalina.filters.FilterBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class ExpiresFilter
extends FilterBase {
    private static final Pattern commaSeparatedValuesPattern = Pattern.compile("\\s*,\\s*");
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String HEADER_EXPIRES = "Expires";
    private static final String HEADER_LAST_MODIFIED = "Last-Modified";
    private final Log log = LogFactory.getLog(ExpiresFilter.class);
    private static final String PARAMETER_EXPIRES_BY_TYPE = "ExpiresByType";
    private static final String PARAMETER_EXPIRES_DEFAULT = "ExpiresDefault";
    private static final String PARAMETER_EXPIRES_EXCLUDED_RESPONSE_STATUS_CODES = "ExpiresExcludedResponseStatusCodes";
    private ExpiresConfiguration defaultExpiresConfiguration;
    private int[] excludedResponseStatusCodes = new int[]{304};
    private Map<String, ExpiresConfiguration> expiresConfigurationByContentType = new LinkedHashMap<String, ExpiresConfiguration>();

    protected static int[] commaDelimitedListToIntArray(String commaDelimitedInts) {
        String[] intsAsStrings = ExpiresFilter.commaDelimitedListToStringArray(commaDelimitedInts);
        int[] ints = new int[intsAsStrings.length];
        for (int i = 0; i < intsAsStrings.length; ++i) {
            String intAsString = intsAsStrings[i];
            try {
                ints[i] = Integer.parseInt(intAsString);
                continue;
            }
            catch (NumberFormatException e) {
                throw new RuntimeException(sm.getString("expiresFilter.numberError", new Object[]{i, commaDelimitedInts}));
            }
        }
        return ints;
    }

    protected static String[] commaDelimitedListToStringArray(String commaDelimitedStrings) {
        return commaDelimitedStrings == null || commaDelimitedStrings.length() == 0 ? new String[]{} : commaSeparatedValuesPattern.split(commaDelimitedStrings);
    }

    protected static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.contains(searchStr);
    }

    protected static String intsToCommaDelimitedString(int[] ints) {
        if (ints == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < ints.length; ++i) {
            result.append(ints[i]);
            if (i >= ints.length - 1) continue;
            result.append(", ");
        }
        return result.toString();
    }

    protected static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    protected static boolean isNotEmpty(String str) {
        return !ExpiresFilter.isEmpty(str);
    }

    protected static boolean startsWithIgnoreCase(String string, String prefix) {
        if (string == null || prefix == null) {
            return string == null && prefix == null;
        }
        if (prefix.length() > string.length()) {
            return false;
        }
        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    protected static String substringBefore(String str, String separator) {
        if (str == null || str.isEmpty() || separator == null) {
            return null;
        }
        if (separator.isEmpty()) {
            return "";
        }
        int separatorIndex = str.indexOf(separator);
        if (separatorIndex == -1) {
            return str;
        }
        return str.substring(0, separatorIndex);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            if (response.isCommitted()) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)sm.getString("expiresFilter.responseAlreadyCommitted", new Object[]{httpRequest.getRequestURL()}));
                }
                chain.doFilter(request, response);
            } else {
                XHttpServletResponse xResponse = new XHttpServletResponse(httpRequest, httpResponse);
                chain.doFilter(request, (ServletResponse)xResponse);
                if (!xResponse.isWriteResponseBodyStarted()) {
                    this.onBeforeWriteResponseBody(httpRequest, xResponse);
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    public ExpiresConfiguration getDefaultExpiresConfiguration() {
        return this.defaultExpiresConfiguration;
    }

    public String getExcludedResponseStatusCodes() {
        return ExpiresFilter.intsToCommaDelimitedString(this.excludedResponseStatusCodes);
    }

    public int[] getExcludedResponseStatusCodesAsInts() {
        return this.excludedResponseStatusCodes;
    }

    @Deprecated
    protected Date getExpirationDate(XHttpServletResponse response) {
        return this.getExpirationDate((HttpServletRequest)null, response);
    }

    protected Date getExpirationDate(HttpServletRequest request, XHttpServletResponse response) {
        String majorType;
        Date result;
        String contentTypeWithoutCharset;
        ExpiresConfiguration configuration;
        int lastSlash;
        String servletPath;
        String contentType = response.getContentType();
        if (contentType == null && request != null && request.getHttpServletMapping().getMappingMatch() == MappingMatch.DEFAULT && response.getStatus() == 304 && (servletPath = request.getServletPath()) != null && (lastSlash = servletPath.lastIndexOf(47)) > -1) {
            String fileName = servletPath.substring(lastSlash + 1);
            contentType = request.getServletContext().getMimeType(fileName);
        }
        if (contentType != null) {
            contentType = contentType.toLowerCase(Locale.ENGLISH);
        }
        if ((configuration = this.expiresConfigurationByContentType.get(contentType)) != null) {
            Date result2 = this.getExpirationDate(configuration, response);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("expiresFilter.useMatchingConfiguration", new Object[]{configuration, contentType, contentType, result2}));
            }
            return result2;
        }
        if (ExpiresFilter.contains(contentType, ";") && (configuration = this.expiresConfigurationByContentType.get(contentTypeWithoutCharset = ExpiresFilter.substringBefore(contentType, ";").trim())) != null) {
            result = this.getExpirationDate(configuration, response);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("expiresFilter.useMatchingConfiguration", new Object[]{configuration, contentTypeWithoutCharset, contentType, result}));
            }
            return result;
        }
        if (ExpiresFilter.contains(contentType, "/") && (configuration = this.expiresConfigurationByContentType.get(majorType = ExpiresFilter.substringBefore(contentType, "/"))) != null) {
            result = this.getExpirationDate(configuration, response);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("expiresFilter.useMatchingConfiguration", new Object[]{configuration, majorType, contentType, result}));
            }
            return result;
        }
        if (this.defaultExpiresConfiguration != null) {
            Date result3 = this.getExpirationDate(this.defaultExpiresConfiguration, response);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("expiresFilter.useDefaultConfiguration", new Object[]{this.defaultExpiresConfiguration, contentType, result3}));
            }
            return result3;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("expiresFilter.noExpirationConfiguredForContentType", new Object[]{contentType}));
        }
        return null;
    }

    protected Date getExpirationDate(ExpiresConfiguration configuration, XHttpServletResponse response) {
        Calendar calendar;
        switch (configuration.getStartingPoint()) {
            case ACCESS_TIME: {
                calendar = Calendar.getInstance();
                break;
            }
            case LAST_MODIFICATION_TIME: {
                if (response.isLastModifiedHeaderSet()) {
                    try {
                        long lastModified = response.getLastModifiedHeader();
                        calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(lastModified);
                    }
                    catch (NumberFormatException e) {
                        calendar = Calendar.getInstance();
                    }
                    break;
                }
                calendar = Calendar.getInstance();
                break;
            }
            default: {
                throw new IllegalStateException(sm.getString("expiresFilter.unsupportedStartingPoint", new Object[]{configuration.getStartingPoint()}));
            }
        }
        for (Duration duration : configuration.getDurations()) {
            calendar.add(duration.getUnit().getCalendardField(), duration.getAmount());
        }
        return calendar.getTime();
    }

    public Map<String, ExpiresConfiguration> getExpiresConfigurationByContentType() {
        return this.expiresConfigurationByContentType;
    }

    @Override
    protected Log getLogger() {
        return this.log;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration names = filterConfig.getInitParameterNames();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            String value = filterConfig.getInitParameter(name);
            try {
                if (name.startsWith(PARAMETER_EXPIRES_BY_TYPE)) {
                    String contentType = name.substring(PARAMETER_EXPIRES_BY_TYPE.length()).trim().toLowerCase(Locale.ENGLISH);
                    ExpiresConfiguration expiresConfiguration = this.parseExpiresConfiguration(value);
                    this.expiresConfigurationByContentType.put(contentType, expiresConfiguration);
                    continue;
                }
                if (name.equalsIgnoreCase(PARAMETER_EXPIRES_DEFAULT)) {
                    ExpiresConfiguration expiresConfiguration;
                    this.defaultExpiresConfiguration = expiresConfiguration = this.parseExpiresConfiguration(value);
                    continue;
                }
                if (name.equalsIgnoreCase(PARAMETER_EXPIRES_EXCLUDED_RESPONSE_STATUS_CODES)) {
                    this.excludedResponseStatusCodes = ExpiresFilter.commaDelimitedListToIntArray(value);
                    continue;
                }
                this.log.warn((Object)sm.getString("expiresFilter.unknownParameterIgnored", new Object[]{name, value}));
            }
            catch (RuntimeException e) {
                throw new ServletException(sm.getString("expiresFilter.exceptionProcessingParameter", new Object[]{name, value}), (Throwable)e);
            }
        }
        this.log.debug((Object)sm.getString("expiresFilter.filterInitialized", new Object[]{this.toString()}));
    }

    protected boolean isEligibleToExpirationHeaderGeneration(HttpServletRequest request, XHttpServletResponse response) {
        boolean expirationHeaderHasBeenSet;
        boolean bl = expirationHeaderHasBeenSet = response.containsHeader(HEADER_EXPIRES) || ExpiresFilter.contains(response.getCacheControlHeader(), "max-age");
        if (expirationHeaderHasBeenSet) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("expiresFilter.expirationHeaderAlreadyDefined", new Object[]{request.getRequestURI(), response.getStatus(), response.getContentType()}));
            }
            return false;
        }
        for (int skippedStatusCode : this.excludedResponseStatusCodes) {
            if (response.getStatus() != skippedStatusCode) continue;
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("expiresFilter.skippedStatusCode", new Object[]{request.getRequestURI(), response.getStatus(), response.getContentType()}));
            }
            return false;
        }
        return true;
    }

    public void onBeforeWriteResponseBody(HttpServletRequest request, XHttpServletResponse response) {
        if (!this.isEligibleToExpirationHeaderGeneration(request, response)) {
            return;
        }
        Date expirationDate = this.getExpirationDate(request, response);
        if (expirationDate == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("expiresFilter.noExpirationConfigured", new Object[]{request.getRequestURI(), response.getStatus(), response.getContentType()}));
            }
        } else {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("expiresFilter.setExpirationDate", new Object[]{request.getRequestURI(), response.getStatus(), response.getContentType(), expirationDate}));
            }
            String maxAgeDirective = "max-age=" + (expirationDate.getTime() - System.currentTimeMillis()) / 1000L;
            String cacheControlHeader = response.getCacheControlHeader();
            String newCacheControlHeader = cacheControlHeader == null ? maxAgeDirective : cacheControlHeader + ", " + maxAgeDirective;
            response.setHeader(HEADER_CACHE_CONTROL, newCacheControlHeader);
            response.setDateHeader(HEADER_EXPIRES, expirationDate.getTime());
        }
    }

    protected ExpiresConfiguration parseExpiresConfiguration(String inputLine) {
        StartingPoint startingPoint;
        String currentToken;
        String line = inputLine.trim();
        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        try {
            currentToken = tokenizer.nextToken();
        }
        catch (NoSuchElementException e) {
            throw new IllegalStateException(sm.getString("expiresFilter.startingPointNotFound", new Object[]{line}));
        }
        if ("access".equalsIgnoreCase(currentToken) || "now".equalsIgnoreCase(currentToken)) {
            startingPoint = StartingPoint.ACCESS_TIME;
        } else if ("modification".equalsIgnoreCase(currentToken)) {
            startingPoint = StartingPoint.LAST_MODIFICATION_TIME;
        } else if (!tokenizer.hasMoreTokens() && ExpiresFilter.startsWithIgnoreCase(currentToken, "a")) {
            startingPoint = StartingPoint.ACCESS_TIME;
            tokenizer = new StringTokenizer(currentToken.substring(1) + " seconds", " ");
        } else if (!tokenizer.hasMoreTokens() && ExpiresFilter.startsWithIgnoreCase(currentToken, "m")) {
            startingPoint = StartingPoint.LAST_MODIFICATION_TIME;
            tokenizer = new StringTokenizer(currentToken.substring(1) + " seconds", " ");
        } else {
            throw new IllegalStateException(sm.getString("expiresFilter.startingPointInvalid", new Object[]{currentToken, line}));
        }
        try {
            currentToken = tokenizer.nextToken();
        }
        catch (NoSuchElementException e) {
            throw new IllegalStateException(sm.getString("expiresFilter.noDurationFound", new Object[]{line}));
        }
        if ("plus".equalsIgnoreCase(currentToken)) {
            try {
                currentToken = tokenizer.nextToken();
            }
            catch (NoSuchElementException e) {
                throw new IllegalStateException(sm.getString("expiresFilter.noDurationFound", new Object[]{line}));
            }
        }
        ArrayList<Duration> durations = new ArrayList<Duration>();
        while (currentToken != null) {
            DurationUnit durationUnit;
            int amount;
            try {
                amount = Integer.parseInt(currentToken);
            }
            catch (NumberFormatException e) {
                throw new IllegalStateException(sm.getString("expiresFilter.invalidDurationNumber", new Object[]{currentToken, line}));
            }
            try {
                currentToken = tokenizer.nextToken();
            }
            catch (NoSuchElementException e) {
                throw new IllegalStateException(sm.getString("expiresFilter.noDurationUnitAfterAmount", new Object[]{amount, line}));
            }
            if ("year".equalsIgnoreCase(currentToken) || "years".equalsIgnoreCase(currentToken)) {
                durationUnit = DurationUnit.YEAR;
            } else if ("month".equalsIgnoreCase(currentToken) || "months".equalsIgnoreCase(currentToken)) {
                durationUnit = DurationUnit.MONTH;
            } else if ("week".equalsIgnoreCase(currentToken) || "weeks".equalsIgnoreCase(currentToken)) {
                durationUnit = DurationUnit.WEEK;
            } else if ("day".equalsIgnoreCase(currentToken) || "days".equalsIgnoreCase(currentToken)) {
                durationUnit = DurationUnit.DAY;
            } else if ("hour".equalsIgnoreCase(currentToken) || "hours".equalsIgnoreCase(currentToken)) {
                durationUnit = DurationUnit.HOUR;
            } else if ("minute".equalsIgnoreCase(currentToken) || "minutes".equalsIgnoreCase(currentToken)) {
                durationUnit = DurationUnit.MINUTE;
            } else if ("second".equalsIgnoreCase(currentToken) || "seconds".equalsIgnoreCase(currentToken)) {
                durationUnit = DurationUnit.SECOND;
            } else {
                throw new IllegalStateException(sm.getString("expiresFilter.invalidDurationUnit", new Object[]{currentToken, line}));
            }
            Duration duration = new Duration(amount, durationUnit);
            durations.add(duration);
            if (tokenizer.hasMoreTokens()) {
                currentToken = tokenizer.nextToken();
                continue;
            }
            currentToken = null;
        }
        return new ExpiresConfiguration(startingPoint, durations);
    }

    public void setDefaultExpiresConfiguration(ExpiresConfiguration defaultExpiresConfiguration) {
        this.defaultExpiresConfiguration = defaultExpiresConfiguration;
    }

    public void setExcludedResponseStatusCodes(int[] excludedResponseStatusCodes) {
        this.excludedResponseStatusCodes = excludedResponseStatusCodes;
    }

    public void setExpiresConfigurationByContentType(Map<String, ExpiresConfiguration> expiresConfigurationByContentType) {
        this.expiresConfigurationByContentType = expiresConfigurationByContentType;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[excludedResponseStatusCode=[" + ExpiresFilter.intsToCommaDelimitedString(this.excludedResponseStatusCodes) + "], default=" + this.defaultExpiresConfiguration + ", byType=" + this.expiresConfigurationByContentType + "]";
    }

    public class XHttpServletResponse
    extends HttpServletResponseWrapper {
        private String cacheControlHeader;
        private long lastModifiedHeader;
        private boolean lastModifiedHeaderSet;
        private PrintWriter printWriter;
        private final HttpServletRequest request;
        private ServletOutputStream servletOutputStream;
        private boolean writeResponseBodyStarted;

        public XHttpServletResponse(HttpServletRequest request, HttpServletResponse response) {
            super(response);
            this.request = request;
        }

        public void addDateHeader(String name, long date) {
            super.addDateHeader(name, date);
            if (!this.lastModifiedHeaderSet) {
                this.lastModifiedHeader = date;
                this.lastModifiedHeaderSet = true;
            }
        }

        public void addHeader(String name, String value) {
            super.addHeader(name, value);
            if (ExpiresFilter.HEADER_CACHE_CONTROL.equalsIgnoreCase(name) && this.cacheControlHeader == null) {
                this.cacheControlHeader = value;
            }
        }

        public String getCacheControlHeader() {
            return this.cacheControlHeader;
        }

        public long getLastModifiedHeader() {
            return this.lastModifiedHeader;
        }

        public ServletOutputStream getOutputStream() throws IOException {
            if (this.servletOutputStream == null) {
                this.servletOutputStream = new XServletOutputStream(super.getOutputStream(), this.request, this);
            }
            return this.servletOutputStream;
        }

        public PrintWriter getWriter() throws IOException {
            if (this.printWriter == null) {
                this.printWriter = new XPrintWriter(super.getWriter(), this.request, this);
            }
            return this.printWriter;
        }

        public boolean isLastModifiedHeaderSet() {
            return this.lastModifiedHeaderSet;
        }

        public boolean isWriteResponseBodyStarted() {
            return this.writeResponseBodyStarted;
        }

        public void reset() {
            super.reset();
            this.lastModifiedHeader = 0L;
            this.lastModifiedHeaderSet = false;
            this.cacheControlHeader = null;
        }

        public void setDateHeader(String name, long date) {
            super.setDateHeader(name, date);
            if (ExpiresFilter.HEADER_LAST_MODIFIED.equalsIgnoreCase(name)) {
                this.lastModifiedHeader = date;
                this.lastModifiedHeaderSet = true;
            }
        }

        public void setHeader(String name, String value) {
            super.setHeader(name, value);
            if (ExpiresFilter.HEADER_CACHE_CONTROL.equalsIgnoreCase(name)) {
                this.cacheControlHeader = value;
            }
        }

        public void setWriteResponseBodyStarted(boolean writeResponseBodyStarted) {
            this.writeResponseBodyStarted = writeResponseBodyStarted;
        }
    }

    protected static class ExpiresConfiguration {
        private final List<Duration> durations;
        private final StartingPoint startingPoint;

        public ExpiresConfiguration(StartingPoint startingPoint, List<Duration> durations) {
            this.startingPoint = startingPoint;
            this.durations = durations;
        }

        public List<Duration> getDurations() {
            return this.durations;
        }

        public StartingPoint getStartingPoint() {
            return this.startingPoint;
        }

        public String toString() {
            return "ExpiresConfiguration[startingPoint=" + (Object)((Object)this.startingPoint) + ", duration=" + this.durations + "]";
        }
    }

    protected static enum StartingPoint {
        ACCESS_TIME,
        LAST_MODIFICATION_TIME;

    }

    protected static class Duration {
        protected final int amount;
        protected final DurationUnit unit;

        public Duration(int amount, DurationUnit unit) {
            this.amount = amount;
            this.unit = unit;
        }

        public int getAmount() {
            return this.amount;
        }

        public DurationUnit getUnit() {
            return this.unit;
        }

        public String toString() {
            return this.amount + " " + (Object)((Object)this.unit);
        }
    }

    protected static enum DurationUnit {
        DAY(6),
        HOUR(10),
        MINUTE(12),
        MONTH(2),
        SECOND(13),
        WEEK(3),
        YEAR(1);

        private final int calendarField;

        private DurationUnit(int calendarField) {
            this.calendarField = calendarField;
        }

        public int getCalendardField() {
            return this.calendarField;
        }
    }

    public class XServletOutputStream
    extends ServletOutputStream {
        private final HttpServletRequest request;
        private final XHttpServletResponse response;
        private final ServletOutputStream servletOutputStream;

        public XServletOutputStream(ServletOutputStream servletOutputStream, HttpServletRequest request, XHttpServletResponse response) {
            this.servletOutputStream = servletOutputStream;
            this.response = response;
            this.request = request;
        }

        public void close() throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.close();
        }

        private void fireOnBeforeWriteResponseBodyEvent() {
            if (!this.response.isWriteResponseBodyStarted()) {
                this.response.setWriteResponseBodyStarted(true);
                ExpiresFilter.this.onBeforeWriteResponseBody(this.request, this.response);
            }
        }

        public void flush() throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.flush();
        }

        public void print(boolean b) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(b);
        }

        public void print(char c) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(c);
        }

        public void print(double d) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(d);
        }

        public void print(float f) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(f);
        }

        public void print(int i) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(i);
        }

        public void print(long l) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(l);
        }

        public void print(String s) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(s);
        }

        public void println() throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println();
        }

        public void println(boolean b) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(b);
        }

        public void println(char c) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(c);
        }

        public void println(double d) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(d);
        }

        public void println(float f) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(f);
        }

        public void println(int i) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(i);
        }

        public void println(long l) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(l);
        }

        public void println(String s) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(s);
        }

        public void write(byte[] b) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.write(b);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.write(b, off, len);
        }

        public void write(int b) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.write(b);
        }

        public boolean isReady() {
            return false;
        }

        public void setWriteListener(WriteListener listener) {
        }
    }

    public class XPrintWriter
    extends PrintWriter {
        private final PrintWriter out;
        private final HttpServletRequest request;
        private final XHttpServletResponse response;

        public XPrintWriter(PrintWriter out, HttpServletRequest request, XHttpServletResponse response) {
            super(out);
            this.out = out;
            this.request = request;
            this.response = response;
        }

        @Override
        public PrintWriter append(char c) {
            this.fireBeforeWriteResponseBodyEvent();
            return this.out.append(c);
        }

        @Override
        public PrintWriter append(CharSequence csq) {
            this.fireBeforeWriteResponseBodyEvent();
            return this.out.append(csq);
        }

        @Override
        public PrintWriter append(CharSequence csq, int start, int end) {
            this.fireBeforeWriteResponseBodyEvent();
            return this.out.append(csq, start, end);
        }

        @Override
        public void close() {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.close();
        }

        private void fireBeforeWriteResponseBodyEvent() {
            if (!this.response.isWriteResponseBodyStarted()) {
                this.response.setWriteResponseBodyStarted(true);
                ExpiresFilter.this.onBeforeWriteResponseBody(this.request, this.response);
            }
        }

        @Override
        public void flush() {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.flush();
        }

        @Override
        public void print(boolean b) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(b);
        }

        @Override
        public void print(char c) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(c);
        }

        @Override
        public void print(char[] s) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(s);
        }

        @Override
        public void print(double d) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(d);
        }

        @Override
        public void print(float f) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(f);
        }

        @Override
        public void print(int i) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(i);
        }

        @Override
        public void print(long l) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(l);
        }

        @Override
        public void print(Object obj) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(obj);
        }

        @Override
        public void print(String s) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(s);
        }

        @Override
        public PrintWriter printf(Locale l, String format, Object ... args) {
            this.fireBeforeWriteResponseBodyEvent();
            return this.out.printf(l, format, args);
        }

        @Override
        public PrintWriter printf(String format, Object ... args) {
            this.fireBeforeWriteResponseBodyEvent();
            return this.out.printf(format, args);
        }

        @Override
        public void println() {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println();
        }

        @Override
        public void println(boolean x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override
        public void println(char x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override
        public void println(char[] x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override
        public void println(double x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override
        public void println(float x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override
        public void println(int x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override
        public void println(long x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override
        public void println(Object x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override
        public void println(String x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override
        public void write(char[] buf) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.write(buf);
        }

        @Override
        public void write(char[] buf, int off, int len) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.write(buf, off, len);
        }

        @Override
        public void write(int c) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.write(c);
        }

        @Override
        public void write(String s) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.write(s);
        }

        @Override
        public void write(String s, int off, int len) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.write(s, off, len);
        }
    }
}

