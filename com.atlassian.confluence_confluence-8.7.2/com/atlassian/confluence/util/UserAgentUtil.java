/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  javax.servlet.http.HttpServletRequest
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.util;

import com.atlassian.core.filters.ServletContextThreadLocal;
import javax.servlet.http.HttpServletRequest;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class UserAgentUtil {
    private static final String USER_AGENT_HEADER_NAME = "USER-AGENT";
    private static final UserAgent UNKNOWN_USER_AGENT = new UserAgent(new Browser(BrowserFamily.UKNOWN, BrowserMajorVersion.UNKNOWN, "0"));

    public static boolean isBrowserFamily(@NonNull BrowserFamily browserFamily) {
        return UserAgentUtil.getCurrentUserAgent().getBrowser().getBrowserFamily().equals((Object)browserFamily);
    }

    public static boolean isBrowserMajorVersion(@NonNull BrowserMajorVersion browserMajorVersion) {
        return UserAgentUtil.getCurrentUserAgent().getBrowser().getBrowserMajorVersion().equals((Object)browserMajorVersion);
    }

    public static UserAgent getCurrentUserAgent() {
        String userAgent = UserAgentUtil.getUserAgent(ServletContextThreadLocal.getRequest());
        if (userAgent == null) {
            return UNKNOWN_USER_AGENT;
        }
        return UserAgentUtil.getUserAgentInfo(userAgent);
    }

    public static UserAgent getUserAgentInfo(String userAgent) {
        return new UserAgent(UserAgentUtil.getBrowser(userAgent));
    }

    public static @Nullable String getUserAgent(HttpServletRequest request) {
        return request == null ? null : request.getHeader(USER_AGENT_HEADER_NAME);
    }

    private static String getVersionNumber(String useragent, int pos) {
        if (pos < 0) {
            return "";
        }
        if (useragent == null) {
            return "";
        }
        StringBuilder res = new StringBuilder();
        int status = 0;
        while (pos < useragent.length()) {
            char c = useragent.charAt(pos);
            switch (status) {
                case 0: {
                    if (c == ' ' || c == '/') break;
                    if (c == ';' || c == ')') {
                        return "";
                    }
                    status = 1;
                }
                case 1: {
                    if (c == ';' || c == '/' || c == ')' || c == '(' || c == '[') {
                        return res.toString().trim();
                    }
                    if (c == ' ') {
                        status = 2;
                    }
                    res.append(c);
                    break;
                }
                case 2: {
                    if (Character.isLetter(c) && Character.isLowerCase(c) || Character.isDigit(c)) {
                        res.append(c);
                        status = 1;
                        break;
                    }
                    return res.toString().trim();
                }
            }
            ++pos;
        }
        return res.toString().trim();
    }

    private static Browser getBrowser(String userAgent) {
        Browser actualBrowser;
        if (userAgent == null) {
            return new Browser(BrowserFamily.UKNOWN, BrowserMajorVersion.UNKNOWN, "0");
        }
        if (userAgent.contains(BrowserMajorVersion.MSIE7.getUserAgentString()) && (actualBrowser = UserAgentUtil.getRealBrowserForIE7(userAgent)) != null) {
            return actualBrowser;
        }
        for (BrowserFamily browserFamily : BrowserFamily.values()) {
            if (!userAgent.contains(browserFamily.getUserAgentString())) continue;
            for (BrowserMajorVersion majorVersion : BrowserMajorVersion.values()) {
                int pos;
                if (!majorVersion.getBrowserFamily().equals((Object)browserFamily) || (pos = userAgent.indexOf(majorVersion.getUserAgentString())) <= -1) continue;
                return new Browser(browserFamily, majorVersion, majorVersion.getMinorVersionPrefix() + UserAgentUtil.getVersionNumber(userAgent, pos + majorVersion.getVersionPos()));
            }
            int pos = userAgent.indexOf(browserFamily.getUserAgentString());
            return new Browser(browserFamily, BrowserMajorVersion.UNKNOWN, browserFamily.getUserAgentString() + UserAgentUtil.getVersionNumber(userAgent, pos + browserFamily.getUserAgentString().length()));
        }
        return new Browser(BrowserFamily.UKNOWN, BrowserMajorVersion.UNKNOWN, "0");
    }

    private static Browser getRealBrowserForIE7(String userAgent) {
        if (userAgent.contains("Trident")) {
            if (userAgent.contains("Trident/3.1")) {
                return null;
            }
            if (userAgent.contains("Trident/4.0")) {
                return new Browser(BrowserFamily.MSIE, BrowserMajorVersion.MSIE8, "MSIE8.0");
            }
            if (userAgent.contains("Trident/5.0")) {
                return new Browser(BrowserFamily.MSIE, BrowserMajorVersion.MSIE9, "MSIE9.0");
            }
            if (userAgent.contains("Trident/6.0")) {
                return new Browser(BrowserFamily.MSIE, BrowserMajorVersion.MSIE10, "MSIE10.0");
            }
            return null;
        }
        return null;
    }

    public static class UserAgent {
        private final Browser browser;

        public UserAgent(Browser browser) {
            this.browser = browser;
        }

        public Browser getBrowser() {
            return this.browser;
        }

        public String toString() {
            return "UserAgent{browser=" + this.browser + "}";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            UserAgent userAgent = (UserAgent)o;
            return this.browser.equals(userAgent.browser);
        }

        public int hashCode() {
            int result = this.browser.hashCode();
            return result;
        }
    }

    public static class Browser {
        private final BrowserFamily browserFamily;
        private final BrowserMajorVersion browserMajorVersion;
        private final String browserMinorVersion;

        public Browser(BrowserFamily browserFamily, BrowserMajorVersion browserMajorVersion, String browserMinorVersion) {
            this.browserFamily = browserFamily;
            this.browserMajorVersion = browserMajorVersion;
            this.browserMinorVersion = browserMinorVersion;
        }

        public BrowserFamily getBrowserFamily() {
            return this.browserFamily;
        }

        public BrowserMajorVersion getBrowserMajorVersion() {
            return this.browserMajorVersion;
        }

        public String getBrowserMinorVersion() {
            return this.browserMinorVersion;
        }

        public String toString() {
            return "Browser{browserFamily=" + this.browserFamily + ", browserMajorVersion=" + this.browserMajorVersion + ", browserMinorVersion='" + this.browserMinorVersion + "'}";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Browser browser = (Browser)o;
            if (this.browserFamily != browser.browserFamily) {
                return false;
            }
            if (this.browserMajorVersion != browser.browserMajorVersion) {
                return false;
            }
            return !(this.browserMinorVersion != null ? !this.browserMinorVersion.equals(browser.browserMinorVersion) : browser.browserMinorVersion != null);
        }

        public int hashCode() {
            int result = this.browserFamily.hashCode();
            result = 31 * result + (this.browserMajorVersion != null ? this.browserMajorVersion.hashCode() : 0);
            result = 31 * result + (this.browserMinorVersion != null ? this.browserMinorVersion.hashCode() : 0);
            return result;
        }
    }

    public static enum BrowserMajorVersion {
        GOOGLE(BrowserFamily.GOOGLE, "google/", "Google", 7),
        GOOGLE_BOT(BrowserFamily.GOOGLE_BOT, "Googlebot/", "Google", 10),
        MSNBOT(BrowserFamily.MSNBOT, "msnbot", "MSNBot", 7),
        WEB_CRAWLER(BrowserFamily.WEB_CRAWLER, "webcrawler/", "WebCrawler", 11),
        MSIE4(BrowserFamily.MSIE, "MSIE 4", "MSIE", 4),
        MSIE5(BrowserFamily.MSIE, "MSIE 5.0", "MSIE", 4),
        MSIE55(BrowserFamily.MSIE, "MSIE 5.5", "MSIE", 4),
        MSIE5x(BrowserFamily.MSIE, "MSIE 5.", "MSIE", 4),
        MSIE6(BrowserFamily.MSIE, "MSIE 6.", "MSIE", 4),
        MSIE7(BrowserFamily.MSIE, "MSIE 7", "MSIE", 4),
        MSIE8(BrowserFamily.MSIE, "MSIE 8", "MSIE", 4),
        MSIE9(BrowserFamily.MSIE, "MSIE 9", "MSIE", 4),
        MSIE10(BrowserFamily.MSIE, "MSIE 10", "MSIE", 4),
        MSIE_UNKNOWN(BrowserFamily.MSIE, "MSIE", "MSIE", 4),
        FIREFOX15(BrowserFamily.FIREFOX, "Firefox/1.5", "Firefox", 8),
        FIREFOX2(BrowserFamily.FIREFOX, "Firefox/2", "Firefox", 8),
        FIREFOX3(BrowserFamily.FIREFOX, "Firefox/3.0", "Firefox", 8),
        FIREFOX31(BrowserFamily.FIREFOX, "Firefox/3.1", "Firefox", 8),
        FIREFOX35(BrowserFamily.FIREFOX, "Firefox/3.5", "Firefox", 8),
        FIREFOX36(BrowserFamily.FIREFOX, "Firefox/3.6", "Firefox", 8),
        FIREFOX4(BrowserFamily.FIREFOX, "Firefox/4.0", "Firefox", 8),
        FIREFOX_UNKNOWN(BrowserFamily.FIREFOX, "Firefox/", "Firefox", 8),
        SAFARI3(BrowserFamily.SAFARI, "Version/3.0", "Safari", 8),
        SAFARI35(BrowserFamily.SAFARI, "Version/3.5", "Safari", 8),
        SAFARI4(BrowserFamily.SAFARI, "Version/4.0", "Safari", 8),
        SAFARI_UNKNOWN(BrowserFamily.SAFARI, "Version/", "Safari", 8),
        CHROME1(BrowserFamily.CHROME, "Chrome/1.0", "Chrome", 7),
        CHROME2(BrowserFamily.CHROME, "Chrome/2.0", "Chrome", 7),
        CHROME3(BrowserFamily.CHROME, "Chrome/3.0", "Chrome", 7),
        CHROME4(BrowserFamily.CHROME, "Chrome/4.0", "Chrome", 7),
        CHROME_UNKNOWN(BrowserFamily.CHROME, "Chrome/", "Chrome", 7),
        LOTUS_NOTES(BrowserFamily.LOTUS_NOTES, "Lotus-Notes/", "LotusNotes", 12),
        OPERA6(BrowserFamily.OPERA, "Opera/6", "Opera", 6),
        OPERA7(BrowserFamily.OPERA, "Opera/7", "Opera", 6),
        OPERA8(BrowserFamily.OPERA, "Opera/8", "Opera", 6),
        OPERA9(BrowserFamily.OPERA, "Opera/9", "Opera", 6),
        OPERA10(BrowserFamily.OPERA, "Opera/10", "Opera", 6),
        KONQUEROR1(BrowserFamily.KONQUEROR, "Konqueror/1", "Konqueror", 10),
        KONQUEROR2(BrowserFamily.KONQUEROR, "Konqueror/2", "Konqueror", 10),
        KONQUEROR3(BrowserFamily.KONQUEROR, "Konqueror/3", "Konqueror", 10),
        KONQUEROR4(BrowserFamily.KONQUEROR, "Konqueror/4", "Konqueror", 10),
        CAMINO(BrowserFamily.GECKO, "Camino/", "Camino", 7),
        CHIMERA(BrowserFamily.GECKO, "Chimera/", "Chimera", 8),
        FIREBIRD(BrowserFamily.GECKO, "Firebird/", "Firebird", 9),
        PHEONIX(BrowserFamily.GECKO, "Phoenix/", "Phoenix", 8),
        GALEON(BrowserFamily.GECKO, "Galeon", "Galeon", 7),
        NETSCAPE4(BrowserFamily.NETSCAPE, "Netscape/4", "Netscape", 9),
        NETSCAPE6(BrowserFamily.GECKO, "Netscape/6", "Netscape", 9),
        NETSCAPE7(BrowserFamily.GECKO, "Netscape/7", "Netscape", 9),
        NETSCAPE_UNKNOWN(BrowserFamily.GECKO, "Netscape/", "Netscape", 9),
        GECKO_UNKNOWN(BrowserFamily.GECKO, "Gecko/", "Gecko", 6),
        KHTML_UNKNOWN(BrowserFamily.KHTML, "KHTML", "KHTML", 5),
        UNKNOWN(BrowserFamily.UKNOWN, "UNKNOWN", "UNKNOWN", -1);

        private final BrowserFamily browserFamily;
        private final String userAgentString;
        private final String minorVersionPrefix;
        private final int versionPos;

        private BrowserMajorVersion(BrowserFamily browserFamily, String userAgentString, String minorVersionPrefix, int versionPos) {
            this.browserFamily = browserFamily;
            this.userAgentString = userAgentString;
            this.minorVersionPrefix = minorVersionPrefix;
            this.versionPos = versionPos;
        }

        public String getUserAgentString() {
            return this.userAgentString;
        }

        public BrowserFamily getBrowserFamily() {
            return this.browserFamily;
        }

        public int getVersionPos() {
            return this.versionPos;
        }

        public String getMinorVersionPrefix() {
            return this.minorVersionPrefix;
        }
    }

    public static enum BrowserFamily {
        ATLASSIAN_MOBILE("AtlassianMobileApp"),
        CONFLUENCE_MOBILE_APP("Confluence/"),
        GOOGLE("google/"),
        GOOGLE_BOT("Googlebot/"),
        MSNBOT("msnbot"),
        WEB_CRAWLER("webcrawler/"),
        MSIE("MSIE"),
        FIREFOX("Firefox"),
        CHROME("Chrome/"),
        SAFARI("Safari/"),
        OPERA("Opera/"),
        GECKO("Gecko/"),
        LOTUS_NOTES("Lotus-Notes/"),
        NETSCAPE("Netscape/"),
        KONQUEROR("Konqueror/"),
        KHTML("KHTML"),
        UKNOWN("UNKNOWN");

        private final String userAgentString;

        private BrowserFamily(String userAgentString) {
            this.userAgentString = userAgentString;
        }

        public String getUserAgentString() {
            return this.userAgentString;
        }
    }
}

