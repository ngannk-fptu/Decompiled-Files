/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.confluence.search.v2.summary.Summarizer
 *  com.atlassian.confluence.search.v2.summary.Summary
 *  com.atlassian.confluence.search.v2.summary.Summary$Fragment
 *  com.atlassian.core.util.ImageInfo
 *  com.atlassian.core.util.XMLUtils
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.renderer.util.RendererUtil
 *  com.atlassian.renderer.util.UrlUtil
 *  com.atlassian.spring.container.ComponentNotFoundException
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  com.google.common.base.Strings
 *  javax.mail.internet.MailDateFormat
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.beanutils.BeanUtils
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.time.DurationFormatUtils
 *  org.apache.commons.text.WordUtils
 *  org.apache.struts2.ServletActionContext
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.owasp.validator.html.PolicyException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationContext
 */
package com.atlassian.confluence.util;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.content.render.xhtml.PolicyConfiguredCleaner;
import com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.TimeZone;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocalFilter;
import com.atlassian.confluence.event.events.analytics.HttpRequestStats;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.search.v2.summary.Summarizer;
import com.atlassian.confluence.search.v2.summary.Summary;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.FilesystemUtils;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.PlainTextToHtmlConverter;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.UrlUtils;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.confluence.web.context.StaticHttpContext;
import com.atlassian.core.util.ImageInfo;
import com.atlassian.core.util.XMLUtils;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.renderer.util.RendererUtil;
import com.atlassian.renderer.util.UrlUtil;
import com.atlassian.spring.container.ComponentNotFoundException;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.atlassian.util.profiling.UtilTimerStack;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.google.common.base.Strings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.MailDateFormat;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.text.WordUtils;
import org.apache.struts2.ServletActionContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.owasp.validator.html.PolicyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class SafeGeneralUtil {
    private static final Logger LOG = LoggerFactory.getLogger(SafeGeneralUtil.class);
    public static final SafeGeneralUtil INSTANCE = new SafeGeneralUtil();
    protected static Long systemStartupTime;
    private static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
    private static final String ELLIPSIS = "\u2026";
    private static final String EMAIL_PATTERN_STRING = "([\\w\\-%+.]+@[\\w\\-%.]+\\.\\p{Alpha}+)";
    private static final Pattern EMAIL_PATTERN;
    private static final Pattern UNMATCHED_PLACEHOLDER_PATTERN;
    private static final int HTTP_DEFAULT_PORT = 80;
    private static final int HTTPS_DEFAULT_PORT = 443;
    private static final String[] JAVASCRIPT_ESCAPE_FIND;
    private static final String[] JAVASCRIPT_ESCAPE_REPLACE;
    private static final String[] HTML_ATTR_ESCAPE_FIND;
    private static final String[] HTML_ATTR_ESCAPE_REPLACE;

    protected SafeGeneralUtil() {
    }

    public static String getStackTrace(Throwable t) {
        if (t == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static Date convertMailFormatDate(String date) throws ParseException {
        if (StringUtils.isBlank((CharSequence)date)) {
            return null;
        }
        return new MailDateFormat().parse(date);
    }

    @Deprecated
    public static Character convertToCharacter(Object obj) {
        if (obj instanceof Character) {
            return (Character)obj;
        }
        try {
            return Character.valueOf(obj.toString().charAt(0));
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Integer convertToInteger(Object obj) {
        if (obj instanceof Integer) {
            return (Integer)obj;
        }
        try {
            DecimalFormat parseFormat = new DecimalFormat("###############");
            return parseFormat.parse(obj.toString()).intValue();
        }
        catch (Exception e) {
            return null;
        }
    }

    @Deprecated
    public static @Nullable Boolean convertToBoolean(Object obj) {
        if (obj instanceof Boolean) {
            return (Boolean)obj;
        }
        try {
            return Boolean.valueOf(obj.toString());
        }
        catch (Exception e) {
            return null;
        }
    }

    @Deprecated
    public static boolean convertToBoolean(Object bool, boolean defaultValue) {
        Boolean boolObj = SafeGeneralUtil.convertToBoolean(bool);
        if (boolObj == null) {
            return defaultValue;
        }
        return boolObj;
    }

    @Deprecated
    public static String convertToString(Object obj) {
        try {
            String result = obj.toString();
            return Strings.emptyToNull((String)result);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String getOriginalUrl(HttpServletRequest request) {
        Object url = request.getRequestURI().substring(request.getContextPath().length());
        String queryString = request.getQueryString();
        if (queryString != null) {
            url = (String)url + "?" + queryString;
        }
        return url;
    }

    @Deprecated
    public static String completeUrlEncode(String url) {
        return HtmlUtil.completeUrlEncode(url);
    }

    @Deprecated
    public static String urlEncode(String url) {
        return HtmlUtil.urlEncode(url);
    }

    @Deprecated
    public static String urlEncode(String value, String encoding) {
        return HtmlUtil.urlEncode(value, encoding);
    }

    @Deprecated
    public static String urlDecode(String url) {
        return HtmlUtil.urlDecode(url);
    }

    @Deprecated
    public static boolean shouldUrlDecode(String str) {
        return HtmlUtil.shouldUrlDecode(str);
    }

    public static String getPageUrl(VersionHistorySummary summary) {
        return "/pages/viewpage.action?pageId=" + summary.getId();
    }

    @Deprecated
    public static @NonNull String getPageUrl(@Nullable AbstractPage page) {
        if (page == null) {
            return "";
        }
        return page.getUrlPath();
    }

    public static String getIdBasedPageUrl(AbstractPage page) {
        if (page == null) {
            return "";
        }
        return page.getIdBasedPageUrl();
    }

    public static String getEditPageUrl(AbstractPage page) {
        Objects.requireNonNull(page);
        UrlBuilder url = new UrlBuilder("/pages/resumedraft.action").add("draftId", page.getId());
        String pageShareId = page.getShareId();
        if (pageShareId != null) {
            url.add("draftShareId", pageShareId);
        }
        return url.toUrl();
    }

    public static String getAttachmentUrl(Attachment attachment) {
        ContentEntityObject container = Objects.requireNonNull(attachment.getContainer());
        String previewParamValue = "/" + container.getId() + "/" + ((Attachment)attachment.getLatestVersion()).getId() + "/" + attachment.getFileName();
        return new UrlBuilder(Objects.requireNonNull(SafeGeneralUtil.getParentPageOrBlog(attachment)).getUrlPath()).add("preview", previewParamValue).toUrl();
    }

    public static String getCommentUrl(Attachment attachment, Comment comment) {
        ContentEntityObject container = Objects.requireNonNull(attachment.getContainer());
        String previewParamValue = comment.getParent() == null ? "/" + container.getId() + "/" + ((Attachment)attachment.getLatestVersion()).getId() + "/" + attachment.getVersion() + "/" + comment.getId() + "/" + attachment.getFileName() : "/" + container.getId() + "/" + ((Attachment)attachment.getLatestVersion()).getId() + "/" + attachment.getVersion() + "/" + SafeGeneralUtil.getParentComment(comment).getId() + "/" + comment.getId() + "/" + attachment.getFileName();
        return new UrlBuilder(Objects.requireNonNull(SafeGeneralUtil.getParentPageOrBlog(attachment)).getUrlPath()).add("preview", previewParamValue).toUrl();
    }

    public static @Nullable AbstractPage getParentPageOrBlog(Contained content) {
        Object container = content.getContainer();
        if (container instanceof AbstractPage) {
            return (AbstractPage)container;
        }
        if (container instanceof Contained) {
            return SafeGeneralUtil.getParentPageOrBlog((Contained)container);
        }
        return null;
    }

    public static Comment getParentComment(Comment comment) {
        if (comment == null) {
            return null;
        }
        if (comment.getParent() == null) {
            return comment;
        }
        return SafeGeneralUtil.getParentComment(comment.getParent());
    }

    @Deprecated(forRemoval=true)
    public static boolean isSafeTitleForUrl(String title) {
        return UrlUtils.isSafeTitleForUrl(title);
    }

    private static boolean isAscii(char c) {
        return c < '\u0080';
    }

    @Deprecated(forRemoval=true)
    public static boolean isSafeTitleForFilesystem(String title) {
        return FilesystemUtils.isSafeTitleForFilesystem(title);
    }

    @HtmlSafe
    public static String customGetPageUrl(AbstractPage page) {
        String pageUrl = page.getUrlPath();
        return SafeGeneralUtil.appendAmpersandOrQuestionMark(pageUrl);
    }

    @HtmlSafe
    @Deprecated(forRemoval=true)
    public static String appendAmpersandOrQuestionMark(String str) {
        return UrlUtils.appendAmpersandOrQuestionMark(str);
    }

    public static String wordWrap(String str, int max) {
        if (StringUtils.isBlank((CharSequence)str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        int nonSpaceChars = 0;
        for (int i = 0; i < sb.length(); ++i) {
            nonSpaceChars = Character.isWhitespace(sb.charAt(i)) ? 0 : ++nonSpaceChars;
            if (nonSpaceChars <= max) continue;
            nonSpaceChars = 0;
            sb.insert(i, ' ');
            ++i;
        }
        return sb.toString().trim();
    }

    public static String highlight(String content, String searchWords) {
        if (StringUtils.isEmpty((CharSequence)content) || StringUtils.isEmpty((CharSequence)searchWords)) {
            return content;
        }
        StringTokenizer st = new StringTokenizer(searchWords, ", ");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.equalsIgnoreCase("span") || token.equalsIgnoreCase("class") || token.equalsIgnoreCase("search") || token.equalsIgnoreCase("highlight")) continue;
            content = Pattern.compile("(" + token + ")", 2).matcher(content).replaceAll("<span class=\"search-highlight\">$0</span>");
        }
        return content;
    }

    @Deprecated
    public static String doubleUrlEncode(String url) {
        return HtmlUtil.urlEncode(HtmlUtil.urlEncode(url));
    }

    public static boolean isAllAscii(String s) {
        char[] sChars;
        for (char sChar : sChars = s.toCharArray()) {
            if (SafeGeneralUtil.isAscii(sChar)) continue;
            return false;
        }
        return true;
    }

    public static boolean isAllLettersOrNumbers(String s) {
        char[] sChars;
        for (char sChar : sChars = s.toCharArray()) {
            if (Character.isLetterOrDigit(sChar)) continue;
            return false;
        }
        return true;
    }

    public static String getVersionNumber() {
        return BuildInformation.INSTANCE.getVersionNumber();
    }

    public static Date getBuildDate() {
        return BuildInformation.INSTANCE.getBuildDate();
    }

    public static String getBuildDateString() {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        format.setTimeZone(TimeZone.getDefault().getWrappedTimeZone());
        return format.format(BuildInformation.INSTANCE.getBuildDate());
    }

    public static String getBuildNumber() {
        return BuildInformation.INSTANCE.getBuildNumber();
    }

    public static Long getSystemStartupTime() {
        return systemStartupTime;
    }

    @Deprecated
    public static boolean isLicenseExpired() {
        ApplicationContext context = BootstrapUtils.getBootstrapContext();
        if (context == null) {
            return false;
        }
        LicenseService licenseService = (LicenseService)context.getBean("licenseService");
        ConfluenceLicense license = licenseService.retrieve();
        return license.isExpired();
    }

    @Deprecated
    public static boolean hasTooManyUsers() {
        return !GeneralUtil.getUserAccessor().isLicensedToAddMoreUsers();
    }

    @Deprecated
    public static boolean stringSet(String str) {
        return StringUtils.isNotEmpty((CharSequence)str);
    }

    public static String formatLongTime(long time) {
        if (time >= 3600000L) {
            return DurationFormatUtils.formatDuration((long)time, (String)"H:mm:ss.SSS", (boolean)true);
        }
        return DurationFormatUtils.formatDuration((long)time, (String)"mm:ss.SSS", (boolean)true);
    }

    public static String displayShortUrl(String url) {
        return SafeGeneralUtil.displayShortUrl(url, 32);
    }

    public static String displayShortUrl(String url, int length) {
        if (StringUtils.isEmpty((CharSequence)url)) {
            return "";
        }
        if (UrlUtil.startsWithUrl((String)url)) {
            if (url.startsWith("http://")) {
                url = url.substring(7);
            }
            boolean containsGoogle = url.contains(".google.");
            int indexOfQuery = url.indexOf("q=");
            if (containsGoogle && indexOfQuery >= 0) {
                url = SafeGeneralUtil.extractGoogleUrl(url, indexOfQuery);
            }
        }
        if (url.length() < length) {
            return url;
        }
        return url.substring(0, length - 1) + ELLIPSIS;
    }

    private static String extractGoogleUrl(String url, int indexOfQuery) {
        try {
            int indexOfAmpersand = ((String)url).indexOf("&", indexOfQuery);
            String googleQueryPhrase = indexOfAmpersand > -1 ? ((String)url).substring(indexOfQuery + 2, indexOfAmpersand) : ((String)url).substring(indexOfQuery + 2);
            url = "Google: " + URLDecoder.decode(googleQueryPhrase, DEFAULT_CHARACTER_ENCODING);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return url;
    }

    @Deprecated(forRemoval=true)
    public static Date toEndOfMonth(Calendar postDate, boolean isSqlServer) {
        Instant instant = postDate.getTime().toInstant().atZone(postDate.getTimeZone().toZoneId()).truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1).plusMonths(1L).toInstant().minusMillis(isSqlServer ? 3L : 1L);
        return Date.from(instant);
    }

    public static void copyDate(Calendar original, Calendar copy) {
        int[] calendarPeriods;
        for (int p : calendarPeriods = new int[]{1, 2, 5, 11, 12, 13, 14}) {
            copy.set(p, original.get(p));
        }
    }

    public static String getCharacterEncoding() {
        try {
            Settings globalSettings = SafeGeneralUtil.getGlobalSettings();
            if (globalSettings == null) {
                LOG.debug("Null instance of GlobalSettings. Will use {} as default encoding", (Object)DEFAULT_CHARACTER_ENCODING);
                return DEFAULT_CHARACTER_ENCODING;
            }
            return globalSettings.getDefaultEncoding();
        }
        catch (ComponentNotFoundException ex) {
            LOG.warn("Could not obtain an instance of GlobalSettings");
            return DEFAULT_CHARACTER_ENCODING;
        }
    }

    public static Charset getDefaultCharset() {
        try {
            return Charset.forName(SafeGeneralUtil.getCharacterEncoding());
        }
        catch (UnsupportedCharsetException unsupportedCharsetException) {
            LOG.warn("Will use OS default charset {}, Could not Charset for encoding {} with error : {}", new Object[]{Charset.defaultCharset(), SafeGeneralUtil.getCharacterEncoding(), unsupportedCharsetException.getMessage()});
            return Charset.defaultCharset();
        }
    }

    @HtmlSafe
    public static String escapeXml(String stringToEscape) {
        return XMLUtils.escape((String)stringToEscape);
    }

    @HtmlSafe
    @Deprecated(forRemoval=true)
    public static String cleanQuietly(String stringToClean) {
        if (ContainerManager.isContainerSetup()) {
            return ((RenderedContentCleaner)ContainerManager.getComponent((String)"renderedContentCleaner", RenderedContentCleaner.class)).cleanQuietly(stringToClean);
        }
        try {
            return PolicyConfiguredCleaner.createRenderedContentCleaner().cleanQuietly(stringToClean);
        }
        catch (IOException | PolicyException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] escapeXml(Object[] args) {
        String[] encodedArgs = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            encodedArgs[i] = SafeGeneralUtil.escapeXml(String.valueOf(args[i]));
        }
        return encodedArgs;
    }

    public static String escapeForJavascript(String s) {
        return StringUtils.replaceEach((String)s, (String[])JAVASCRIPT_ESCAPE_FIND, (String[])JAVASCRIPT_ESCAPE_REPLACE);
    }

    @Deprecated
    public static String escapeForHtmlAttribute(String s) {
        return StringUtils.replaceEach((String)s, (String[])HTML_ATTR_ESCAPE_FIND, (String[])HTML_ATTR_ESCAPE_REPLACE);
    }

    public static boolean isSetupComplete() {
        return BootstrapUtils.getBootstrapManager() != null && BootstrapUtils.getBootstrapManager().isSetupComplete();
    }

    public static String maskEmail(String emailAddress) {
        return SafeGeneralUtil.maskEmail(emailAddress, SafeGeneralUtil.getGlobalSettings());
    }

    @Deprecated
    public static String maskEmail(String emailAddress, Settings globalSettings, I18NBean i18NBean) {
        return SafeGeneralUtil.maskEmail(emailAddress, globalSettings);
    }

    public static String maskEmail(String emailAddress, Settings globalSettings) {
        if (emailAddress == null || "email.address.public".equals(globalSettings.getEmailAddressVisibility())) {
            return emailAddress;
        }
        if ("email.address.private".equals(globalSettings.getEmailAddressVisibility())) {
            return "";
        }
        return SafeGeneralUtil.alwaysMaskEmail(emailAddress);
    }

    public static String alwaysMaskEmail(String emailAddress) {
        StringBuilder buf = new StringBuilder(emailAddress.length() + 20);
        for (int i = 0; i < emailAddress.length(); ++i) {
            char c = emailAddress.charAt(i);
            if (c == '.') {
                buf.append(" dot ");
                continue;
            }
            if (c == '@') {
                buf.append(" at ");
                continue;
            }
            buf.append(c);
        }
        return buf.toString();
    }

    public static String findAndMaskEmail(String text, User currentUser) {
        if (currentUser != null && GeneralUtil.getUserAccessor().hasMembership("confluence-administrators", currentUser.getName())) {
            return text;
        }
        if (StringUtils.isEmpty((CharSequence)text) || "email.address.public".equals(SafeGeneralUtil.getGlobalSettings().getEmailAddressVisibility())) {
            return text;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder(text.length());
        while (matcher.find()) {
            matcher.appendReplacement(sb, SafeGeneralUtil.maskEmail(matcher.group(0)));
        }
        if (sb.length() == 0) {
            return text;
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String escapeCDATA(String s) {
        if (!s.contains("]]")) {
            return s;
        }
        return s.replaceAll("]]", "]] ");
    }

    public static String unescapeCDATA(String s) {
        if (!s.contains("]] ")) {
            return s;
        }
        return s.replaceAll("]] ", "]]");
    }

    public static String unescapeEntities(String str) {
        Pattern hexEntityPattern = Pattern.compile("&([a-fA-F0-9]+);");
        Pattern decimalEntityPattern = Pattern.compile("&#([0-9]+);");
        str = SafeGeneralUtil.replaceNumericEntities(str, hexEntityPattern, 16);
        return SafeGeneralUtil.replaceNumericEntities(str, decimalEntityPattern, 10);
    }

    private static String replaceNumericEntities(String str, Pattern pattern, int base) {
        Matcher matcher = pattern.matcher(str);
        StringBuilder buf = new StringBuilder(str.length());
        while (matcher.find()) {
            matcher.appendReplacement(buf, Character.toString((char)Integer.parseInt(matcher.group(1), base)));
        }
        matcher.appendTail(buf);
        return buf.toString();
    }

    public static String base64Decode(String s) {
        try {
            String s1 = s.replaceAll("_", "/");
            String s2 = s1.replaceAll("-", "+");
            byte[] sBytes = s2.getBytes();
            return new String(Base64.decodeBase64((byte[])sBytes), DEFAULT_CHARACTER_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            LOG.error("This Java installation doesn't support UTF-8. Call Mulder");
            return s;
        }
    }

    public static String base64Encode(String s) {
        byte[] sBytes = s.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeBase64URLSafeString((byte[])sBytes);
    }

    @Deprecated
    public static String hackSingleQuotes(String s) {
        if (StringUtils.isNotEmpty((CharSequence)s)) {
            return s.replaceAll("'", "' + '\\\\'' + '");
        }
        return s;
    }

    public static boolean isInLastDays(Date date, int maxDays) {
        if (date == null) {
            return false;
        }
        long tstamp = date.getTime();
        long t0 = System.currentTimeMillis();
        long dt = t0 - tstamp;
        long secs = dt / 1000L;
        long mins = secs / 60L;
        long hours = mins / 60L;
        long days = hours / 24L;
        return days < (long)maxDays;
    }

    public static String getRelativeTime(Date date) {
        if (date == null) {
            return "No timestamp.";
        }
        long tstamp = date.getTime();
        long t0 = System.currentTimeMillis();
        long dt = t0 - tstamp;
        long secs = dt / 1000L;
        long mins = secs / 60L;
        long hours = mins / 60L;
        long days = hours / 24L;
        if (days == 1L) {
            return ConfluenceActionSupport.getTextStatic("one.day.ago");
        }
        if (days > 1L) {
            return ConfluenceActionSupport.getTextStatic("x.days.ago", new String[]{String.valueOf(days)});
        }
        if (hours == 1L) {
            return ConfluenceActionSupport.getTextStatic("one.hour.ago");
        }
        if (hours > 1L) {
            return ConfluenceActionSupport.getTextStatic("x.hours.ago", new Object[]{"" + hours});
        }
        if (mins == 1L) {
            return ConfluenceActionSupport.getTextStatic("one.min.ago");
        }
        if (mins > 0L) {
            return ConfluenceActionSupport.getTextStatic("x.mins.ago", new Object[]{"" + mins});
        }
        return ConfluenceActionSupport.getTextStatic("less.than.one.min");
    }

    public static String getFormatDateSimple(Date date) {
        return new SimpleDateFormat("dd MMM").format(date);
    }

    public static String getCookieValue(HttpServletRequest request, String key) {
        LOG.debug("Looking for a cookie named : '{}'", (Object)key);
        Cookie[] cookies = request.getCookies();
        if (cookies == null || key == null || key.isEmpty()) {
            if (cookies == null) {
                LOG.debug("The Cookies array in the HTTP request is null");
            }
            return null;
        }
        for (Cookie cookie : cookies) {
            if (!cookie.getName().equals(key)) continue;
            return cookie.getValue();
        }
        LOG.debug("No cookie was found with name :{}", (Object)key);
        return null;
    }

    public static String getCookieValue(String key) {
        return SafeGeneralUtil.getCookieValue(ServletActionContext.getRequest(), key);
    }

    @Deprecated
    @HtmlSafe
    public static String htmlEncode(String text) {
        return HtmlUtil.htmlEncode(text);
    }

    @Deprecated
    @HtmlSafe
    public static String htmlEncodeAndReplaceSpaces(String text) {
        return HtmlUtil.htmlEncodeAndReplaceSpaces(text);
    }

    @HtmlSafe
    public static String plain2html(String text) {
        return PlainTextToHtmlConverter.toHtml(text);
    }

    public static ResourceBundle getDefaultResourceBundle() {
        ConfluenceActionSupport action = new ConfluenceActionSupport();
        if (ContainerManager.isContainerSetup()) {
            ContainerManager.autowireComponent((Object)action);
        }
        return ResourceBundle.getBundle(ConfluenceActionSupport.class.getName(), action.getLocale(), Thread.currentThread().getContextClassLoader());
    }

    public static I18NBean getI18n() {
        return (I18NBean)ContainerManager.getComponent((String)"i18NBean");
    }

    @Deprecated
    public static int arraySize(int[] array) {
        return array.length;
    }

    @Deprecated
    public static @Nullable String escapeXMLCharacters(@Nullable String input) {
        return StringUtils.replace((String)StringUtils.replace((String)StringUtils.replace((String)input, (String)"&", (String)"&amp;"), (String)"<", (String)"&lt;"), (String)">", (String)"&gt;");
    }

    public static String replaceInvalidXmlCharacters(String text) {
        if (text == null) {
            return null;
        }
        char[] chars = text.toCharArray();
        int len = text.length();
        for (int i = 0; i < len; ++i) {
            int ch = text.charAt(i);
            if (ch >= 55296 && ch <= 56319) {
                if (++i < len) {
                    char low = text.charAt(i);
                    if (low < '\udc00' || low > '\udfff') {
                        chars[i - 1] = 65533;
                        chars[i] = 65533;
                        continue;
                    }
                    ch = 65536 + (ch - 55296) * 1024 + (low - 56320);
                } else {
                    chars[i - 1] = 65533;
                    continue;
                }
            }
            if (SafeGeneralUtil.isXMLCharacter(ch)) continue;
            chars[i] = 65533;
        }
        return String.valueOf(chars);
    }

    private static boolean isXMLCharacter(int c) {
        if (c == 10 || c == 13 || c == 9) {
            return true;
        }
        if (c < 32) {
            return false;
        }
        if (c <= 55295) {
            return true;
        }
        if (c < 57344) {
            return false;
        }
        if (c <= 65533) {
            return true;
        }
        if (c < 65536) {
            return false;
        }
        return c <= 0x10FFFF;
    }

    @Deprecated
    @HtmlSafe
    public static String htmlEscapeQuotes(String input) {
        if (StringUtils.isEmpty((CharSequence)input)) {
            return "";
        }
        return PlainTextToHtmlConverter.encodeHtmlEntities(input.replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\""));
    }

    @Deprecated
    public static <T> List<T> filterNulls(Collection<T> in) {
        ArrayList<T> l = new ArrayList<T>(in.size());
        for (T o : in) {
            if (o == null) continue;
            l.add(o);
        }
        return l;
    }

    public static String shortenString(String str, int max) {
        if (StringUtils.isNotBlank((CharSequence)str) && str.length() > max) {
            return str.substring(0, max) + ELLIPSIS;
        }
        return str;
    }

    public static String specialToLowerCase(String str) {
        boolean lowerSupported;
        if (StringUtils.isEmpty((CharSequence)str)) {
            return str;
        }
        ApplicationConfiguration appConfig = SafeGeneralUtil.safeGetAppConfig();
        boolean propertyIsSet = appConfig != null && appConfig.getProperty((Object)"hibernate.database.lower_non_ascii_supported") != null;
        boolean bl = lowerSupported = appConfig != null && appConfig.getBooleanProperty((Object)"hibernate.database.lower_non_ascii_supported");
        if (propertyIsSet && !lowerSupported) {
            char[] chars = str.toCharArray();
            for (int i = 0; i < chars.length; ++i) {
                if (!SafeGeneralUtil.isAscii(chars[i])) continue;
                chars[i] = Character.toLowerCase(chars[i]);
            }
            return new String(chars);
        }
        return str.toLowerCase();
    }

    private static ApplicationConfiguration safeGetAppConfig() {
        AtlassianBootstrapManager bootstrapManager = BootstrapUtils.getBootstrapManager();
        if (bootstrapManager == null) {
            return null;
        }
        return bootstrapManager.getApplicationConfig();
    }

    @Deprecated
    public static String replaceConfluenceHomeConstant(String in, String confHome) {
        return StringUtils.replace((String)in, (String)"${confluenceHome}", (String)confHome);
    }

    public static String replaceConfluenceConstants(String in, File home, File localHome) {
        return StringUtils.replace((String)StringUtils.replace((String)in, (String)"${confluenceHome}", (String)home.getPath()), (String)"${localHome}", (String)localHome.getPath());
    }

    @Deprecated
    public static Collection<String> specialLowerCaseCollection(Collection<String> collection) {
        ArrayList<String> lowerCollection = new ArrayList<String>(collection.size());
        for (String string : collection) {
            lowerCollection.add(SafeGeneralUtil.specialToLowerCase(string));
        }
        return lowerCollection;
    }

    public static <T> List<T> safeSubList(List<T> list, int max) {
        if (list == null || list.size() < max) {
            return list;
        }
        return list.subList(0, max);
    }

    public static String summarise(String content) {
        return RendererUtil.summarise((String)content);
    }

    public static Summary makeSummary(String content) {
        return SafeGeneralUtil.makeSummary(content, null);
    }

    public static Summary makeSummary(String content, @Nullable String query) {
        Summary summary;
        block8: {
            summary = null;
            try {
                String strippedContent = RendererUtil.stripBasicMarkup((String)content);
                strippedContent = strippedContent.replaceAll("&nbsp;", "\u00a0");
                if (strippedContent.trim().isEmpty()) break block8;
                Summarizer s = (Summarizer)ContainerManager.getComponent((String)"summarizer");
                try (Ticker ignored = Timers.start((String)"Summarizer.getSummary");){
                    summary = s.getSummary(strippedContent, query);
                }
            }
            catch (IOException e) {
                LOG.error("Could not create summary of content '" + content + "' with query '" + query + "'", (Throwable)e);
            }
        }
        return summary;
    }

    public static String makeFlatSummary(String content) {
        return SafeGeneralUtil.makeFlatSummary(content, null);
    }

    public static String makeFlatSummary(String content, @Nullable String query) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        Summary summary = SafeGeneralUtil.makeSummary(content, query);
        if (summary == null) {
            return content;
        }
        StringBuilder excerpt = new StringBuilder();
        for (Summary.Fragment f : summary.getFragments()) {
            excerpt.append(f.getText());
        }
        return excerpt.toString();
    }

    public static ImageInfo getImageInfo(File pathToImage) {
        ImageInfo imageInfo;
        if (pathToImage == null) {
            return null;
        }
        FileInputStream fis = new FileInputStream(pathToImage);
        try {
            ImageInfo imageInfo2 = new ImageInfo();
            imageInfo2.setInput((InputStream)fis);
            imageInfo2.setDetermineImageNumber(true);
            imageInfo2.setCollectComments(true);
            imageInfo2.check();
            imageInfo = imageInfo2;
        }
        catch (Throwable throwable) {
            try {
                try {
                    fis.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                LOG.error("Could not load image " + pathToImage);
                return null;
            }
        }
        fis.close();
        return imageInfo;
    }

    public static String getNiceDuration(int minutes, int seconds) {
        Object result = "";
        if (minutes > 0) {
            result = (String)result + minutes + " minute" + (minutes > 1 ? "s" : "") + " ";
        }
        if (seconds > 0) {
            result = (String)result + seconds + " second" + (seconds > 1 ? "s" : "");
        }
        return result;
    }

    public static String getCompactDuration(long time) {
        return DurationFormatUtils.formatDuration((long)time, (String)"HH:mm:ss");
    }

    public static String lookupDomainName(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object port = "";
        if (!SafeGeneralUtil.isStandardPort(request.getScheme(), request.getServerPort())) {
            port = ":" + request.getServerPort();
        }
        return request.getScheme() + "://" + request.getServerName() + (String)port + request.getContextPath();
    }

    private static boolean isStandardPort(String scheme, int port) {
        return "http".equalsIgnoreCase(scheme) && port == 80 || "https".equalsIgnoreCase(scheme) && port == 443;
    }

    public static Settings getGlobalSettings() {
        if (ContainerManager.isContainerSetup()) {
            SettingsManager settingsManager = (SettingsManager)ContainerManager.getComponent((String)"settingsManager");
            return settingsManager.getGlobalSettings();
        }
        return null;
    }

    public static long getPercentage(long numerator, long denom) {
        return Math.round(100.0 * (double)numerator / (double)denom);
    }

    public static long getPercentage(String numeratorAsString, String denomAsString) {
        long denom;
        long numerator;
        try {
            numerator = Long.parseLong(numeratorAsString);
            denom = Long.parseLong(denomAsString);
        }
        catch (NumberFormatException e) {
            return 0L;
        }
        return SafeGeneralUtil.getPercentage(numerator, denom);
    }

    @Deprecated
    public static String getConfluenceTempDirectoryPath() {
        return GeneralUtil.getLocalTempDirectory().getPath();
    }

    public static String[] splitCommaDelimitedString(String escapedNames) {
        if (StringUtils.isEmpty((CharSequence)escapedNames)) {
            return new String[0];
        }
        ArrayList<String> result = new ArrayList<String>();
        StringBuilder currentName = new StringBuilder();
        int i = 0;
        while (i < escapedNames.length()) {
            char c;
            if ((c = escapedNames.charAt(i++)) == '\\') {
                currentName.append(escapedNames.charAt(i++));
                continue;
            }
            if (c != ',') {
                currentName.append(c);
                continue;
            }
            result.add(currentName.toString().trim());
            currentName = new StringBuilder();
        }
        if (StringUtils.isNotEmpty((CharSequence)currentName.toString())) {
            result.add(currentName.toString().trim());
        }
        return result.toArray(new String[0]);
    }

    @Deprecated
    public static List<String> escapeCommas(List<String> toEscape) {
        ArrayList<String> result = new ArrayList<String>(toEscape.size());
        for (String str : toEscape) {
            result.add(SafeGeneralUtil.escapeCommas(str));
        }
        return result;
    }

    public static String escapeCommas(String toEscape) {
        if (toEscape == null) {
            return null;
        }
        if (!toEscape.contains("\\") && !toEscape.contains(",")) {
            return toEscape;
        }
        StringBuilder sb = new StringBuilder(toEscape.length() + 5);
        for (int i = 0; i < toEscape.length(); ++i) {
            if (toEscape.charAt(i) == ',') {
                sb.append("\\,");
                continue;
            }
            if (toEscape.charAt(i) == '\\') {
                sb.append("\\\\");
                continue;
            }
            sb.append(toEscape.charAt(i));
        }
        return sb.toString();
    }

    @Deprecated
    public static String constrainLength(String s, int length) {
        if (s != null && s.length() > length) {
            return s.substring(0, length);
        }
        return s;
    }

    @Deprecated(forRemoval=true)
    public static Map<String, String> convertBeanToMap(Object bean) {
        if (bean == null) {
            return Collections.EMPTY_MAP;
        }
        try {
            Map beanMap = BeanUtils.describe((Object)bean);
            beanMap.remove("class");
            return beanMap;
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            ReflectiveOperationException exception = ex;
            LOG.warn("Exception while converting an information bean to a Map.", (Throwable)exception);
            return Collections.EMPTY_MAP;
        }
    }

    public static <K, V> Map<K, V> prefixAllMapKeys(String prefix, Map<K, V> map) {
        if (StringUtils.isBlank((CharSequence)prefix)) {
            return map;
        }
        if (map == null) {
            return new HashMap(0);
        }
        HashMap<K, V> prefixedMap = new HashMap<K, V>(map.size());
        String prefixPoint = prefix + ".";
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            if (!(key instanceof String)) {
                prefixedMap.put(key, value);
                continue;
            }
            String prefixedKeyStr = prefixPoint + key;
            prefixedMap.put(prefixedKeyStr, value);
        }
        return prefixedMap;
    }

    public static String rdfEncode(String s) {
        if (StringUtils.isBlank((CharSequence)s)) {
            return s;
        }
        return s.replaceAll("--", "&#45;&#45;");
    }

    public static String populateSimpleMessage(String template, List<String> values) {
        if (StringUtils.isBlank((CharSequence)template)) {
            return template;
        }
        String result = template;
        if (values != null) {
            for (int i = 0; i < values.size(); ++i) {
                String value = values.get(i);
                if (value == null) {
                    LOG.debug("Null substitution value supplied for template: " + template);
                    continue;
                }
                String regex = "\\{" + i + "}";
                result = result.replaceAll(regex, value);
            }
        }
        Matcher matcher = UNMATCHED_PLACEHOLDER_PATTERN.matcher(result);
        result = matcher.replaceAll("");
        return result;
    }

    public static <T> List<T> getRandomSubSet(List<T> list, int sizeOfSubset, Random random) {
        int l = list.size();
        if (sizeOfSubset > l) {
            sizeOfSubset = l;
        }
        for (int i = 0; i < sizeOfSubset; ++i) {
            Collections.swap(list, i, i + random.nextInt(l - i));
        }
        return list.subList(0, sizeOfSubset);
    }

    public static String trimDownStringToWord(String s) {
        if (s != null && s.length() > 60) {
            return WordUtils.abbreviate((String)s, (int)60, (int)72, (String)ELLIPSIS);
        }
        return s;
    }

    public static boolean isDateWithin24Hours(Date date) {
        return Instant.now().minus(1L, ChronoUnit.DAYS).isBefore(date.toInstant());
    }

    @HtmlSafe
    public static String refineOsDestination(String osDestination) {
        return osDestination != null ? XMLUtils.escape((String)osDestination).replaceAll("\\s", "%20") : "";
    }

    public static String removeEmailsFromString(String text) {
        return text != null ? text.replaceAll(EMAIL_PATTERN_STRING, "") : "";
    }

    public static Duration getServerRenderTime(ServletRequest servletRequest) {
        return SafeGeneralUtil.getDuration(servletRequest, Instant::now);
    }

    private static Duration getDuration(ServletRequest servletRequest, Supplier<Instant> now) {
        return RequestTimeThreadLocalFilter.getRequestStartTime(servletRequest).map(start -> Duration.between(start, (Temporal)now.get())).orElse(Duration.ZERO);
    }

    public static String getRequestCorrelationId() {
        return RequestCacheThreadLocal.getRequestCorrelationId();
    }

    public static void flushResponse() {
        StaticHttpContext httpContext = new StaticHttpContext();
        HttpServletResponse response = httpContext.getResponse();
        HttpServletRequest request = httpContext.getRequest();
        try {
            if (response != null && request != null && request.getAttribute("com.atlassian.confluence.util.profiling.ConfluenceSitemeshDecorator") != null) {
                response.getWriter().flush();
            }
        }
        catch (Exception e) {
            LOG.error("Error flushing response", (Throwable)e);
        }
    }

    public static void elapse(String tag) {
        HttpRequestStats.elapse(tag);
    }

    public static <T> T applyIfNonNull(T delegate, Function<T, T> wrapper) {
        return delegate != null ? (T)wrapper.apply(delegate) : null;
    }

    public static List<String> sortList(List<String> list) {
        ArrayList<String> newList = new ArrayList<String>(list);
        Collections.sort(newList);
        return newList;
    }

    @Deprecated
    public static void profilePush(String s) {
        UtilTimerStack.push((String)s);
    }

    @Deprecated
    public static void profilePop(String s) {
        UtilTimerStack.pop((String)s);
    }

    static {
        EMAIL_PATTERN = Pattern.compile(EMAIL_PATTERN_STRING);
        UNMATCHED_PLACEHOLDER_PATTERN = Pattern.compile("\\{\\d+}");
        JAVASCRIPT_ESCAPE_FIND = new String[]{"\\", "'", "\""};
        JAVASCRIPT_ESCAPE_REPLACE = new String[]{"\\\\", "\\'", "\\\""};
        HTML_ATTR_ESCAPE_FIND = new String[]{"\\", "\""};
        HTML_ATTR_ESCAPE_REPLACE = new String[]{"\\\\", "\\\""};
    }
}

