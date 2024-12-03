/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.http.mime;

import com.google.common.base.Preconditions;

public interface UserAgentUtil {
    public UserAgent getUserAgentInfo(String var1);

    public BrowserFamily getBrowserFamily(String var1);

    public static class UserAgent {
        private final Browser browser;
        private final OperatingSystem operatingSystem;

        public UserAgent(Browser browser, OperatingSystem operatingSystem) {
            this.browser = browser;
            this.operatingSystem = operatingSystem;
        }

        public Browser getBrowser() {
            return this.browser;
        }

        public OperatingSystem getOperatingSystem() {
            return this.operatingSystem;
        }

        public String toString() {
            return "UserAgent{browser=" + this.browser + ", operatingSystem=" + this.operatingSystem + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            UserAgent userAgent = (UserAgent)o;
            if (!this.browser.equals(userAgent.browser)) {
                return false;
            }
            return this.operatingSystem.equals(userAgent.operatingSystem);
        }

        public int hashCode() {
            int result = this.browser.hashCode();
            result = 31 * result + this.operatingSystem.hashCode();
            return result;
        }
    }

    public static class OperatingSystem {
        private final OperatingSystemFamily operatingSystemFamily;

        public OperatingSystem(OperatingSystemFamily operatingSystemFamily) {
            this.operatingSystemFamily = operatingSystemFamily;
        }

        public OperatingSystemFamily getOperatingSystemFamily() {
            return this.operatingSystemFamily;
        }

        public String toString() {
            return "OperatingSystem{operatingSystemFamily=" + (Object)((Object)this.operatingSystemFamily) + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            OperatingSystem that = (OperatingSystem)o;
            return this.operatingSystemFamily == that.operatingSystemFamily;
        }

        public int hashCode() {
            return this.operatingSystemFamily.hashCode();
        }

        public static enum OperatingSystemFamily {
            GOOGLE("google/"),
            GOOGLE_BOT("Googlebot/"),
            MSNBOT("msnbot"),
            WEB_CRAWLER("webcrawler/"),
            WINDOWS("Win"),
            LINUX("Linux"),
            MAC("Mac"),
            BSD("BSD"),
            SUN_OS("SunOS"),
            UNIX("IRIX"),
            SONY("SonyEricsson"),
            NOKIA("Nokia"),
            BLACKBERRY("BlackBerry"),
            SYMBIAN("SymbianOS"),
            BEOS("BeOS"),
            AMIGA("Amiga"),
            NINTENDO_WII("Nintendo Wii"),
            UNKNOWN("UNKNOWN");

            private final String userAgentString;

            private OperatingSystemFamily(String userAgentString) {
                this.userAgentString = userAgentString;
            }

            public String getUserAgentString() {
                return this.userAgentString;
            }
        }
    }

    public static class Browser {
        private final BrowserFamily browserFamily;
        private final BrowserMajorVersion browserMajorVersion;
        private final String browserMinorVersion;

        public Browser(BrowserFamily browserFamily, BrowserMajorVersion browserMajorVersion, String browserMinorVersion) {
            this.browserFamily = (BrowserFamily)((Object)Preconditions.checkNotNull((Object)((Object)browserFamily), (Object)"browserFamily"));
            this.browserMajorVersion = (BrowserMajorVersion)((Object)Preconditions.checkNotNull((Object)((Object)browserMajorVersion), (Object)"browserMajorVersion"));
            this.browserMinorVersion = (String)Preconditions.checkNotNull((Object)browserMinorVersion, (Object)"browserMinorVersion");
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
            return "Browser{browserFamily=" + (Object)((Object)this.browserFamily) + ", browserMajorVersion=" + (Object)((Object)this.browserMajorVersion) + ", browserMinorVersion='" + this.browserMinorVersion + '\'' + '}';
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
        MSIE10(BrowserFamily.MSIE, "MSIE 10.0", "MSIE", 4),
        MSIE11(BrowserFamily.MSIE_TRIDENT, "rv:11.0", "MSIE 11", 5),
        MSIE_UNKNOWN(BrowserFamily.MSIE, "MSIE", "MSIE", 4),
        MS_EDGE_12(BrowserFamily.MS_EDGE, "Edge/12", "Edge/12.", 8),
        MS_EDGE_UNKNOWN(BrowserFamily.MS_EDGE, "Edge/", "Edge/", 5),
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
        GOOGLE("google/"),
        GOOGLE_BOT("Googlebot/"),
        MSNBOT("msnbot"),
        WEB_CRAWLER("webcrawler/"),
        MSIE("MSIE"),
        MSIE_TRIDENT("Trident/"),
        MS_EDGE("Edge/"),
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

