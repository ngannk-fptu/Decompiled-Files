/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.velocity.runtime.log.Log
 */
package org.apache.velocity.tools.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.tools.ConversionUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.InvalidScope;
import org.apache.velocity.tools.generic.FormatConfig;

@DefaultKey(value="browser")
@InvalidScope(value={"application"})
public class BrowserTool
extends FormatConfig
implements Serializable {
    private static final long serialVersionUID = 1734529350532353339L;
    protected Log LOG;
    private String userAgent = null;
    private String version = null;
    private int majorVersion = -1;
    private int minorVersion = -1;
    private String geckoVersion = null;
    private int geckoMajorVersion = -1;
    private int geckoMinorVersion = -1;
    private static Pattern genericVersion = Pattern.compile("/([A-Za-z]*( [\\d]* )\\.( [\\d]* )[^\\s]*)", 4);
    private static Pattern firefoxVersion = Pattern.compile("/(( [\\d]* )\\.( [\\d]* )[^\\s]*)", 4);
    private static Pattern ieVersion = Pattern.compile("compatible;\\s*\\w*[\\s|/]([A-Za-z]*( [\\d]* )\\.( [\\d]* )[^\\s]*)", 4);
    private static Pattern safariVersion = Pattern.compile("safari/(( [\\d]* )(?:\\. [\\d]* )?)", 4);
    private static Pattern mozillaVersion = Pattern.compile("netscape/(( [\\d]* )\\.( [\\d]* )[^\\s]*)", 4);
    private static Pattern fallbackVersion = Pattern.compile("[\\w]+/( [\\d]+ );", 4);
    private String acceptLanguage = null;
    private SortedMap<Float, List<String>> languageRangesByQuality = null;
    private String starLanguageRange = null;
    private List<String> languagesFilter = null;
    private String preferredLanguage = null;
    private static Pattern quality = Pattern.compile("^q\\s*=\\s*(\\d(?:0(?:.\\d{0,3})?|1(?:.0{0,3}))?)$");

    public void setRequest(HttpServletRequest request) {
        if (request != null) {
            this.setUserAgent(request.getHeader("User-Agent"));
            this.setAcceptLanguage(request.getHeader("Accept-Language"));
        } else {
            this.setUserAgent(null);
            this.setAcceptLanguage(null);
        }
    }

    public void setLog(Log log) {
        if (log == null) {
            throw new NullPointerException("log should not be set to null");
        }
        this.LOG = log;
    }

    public void setUserAgent(String ua) {
        this.userAgent = ua == null ? "" : ua.toLowerCase();
    }

    public void setAcceptLanguage(String al) {
        this.acceptLanguage = al == null ? "" : al.toLowerCase();
    }

    public void setLanguagesFilter(String filter) {
        this.languagesFilter = filter == null || filter.length() == 0 ? null : Arrays.asList(filter.split(","));
        this.preferredLanguage = null;
    }

    public String getLanguagesFilter() {
        return this.languagesFilter.toString();
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[ua=" + this.userAgent + "]";
    }

    public boolean get(String key) {
        return this.test(key);
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public String getAcceptLanguage() {
        return this.acceptLanguage;
    }

    public String getVersion() {
        this.parseVersion();
        return this.version;
    }

    public int getMajorVersion() {
        this.parseVersion();
        return this.majorVersion;
    }

    public int getMinorVersion() {
        this.parseVersion();
        return this.minorVersion;
    }

    public String getGeckoVersion() {
        this.parseVersion();
        return this.geckoVersion;
    }

    public int getGeckoMajorVersion() {
        this.parseVersion();
        return this.geckoMajorVersion;
    }

    public int getGeckoMinorVersion() {
        this.parseVersion();
        return this.geckoMinorVersion;
    }

    public boolean getGecko() {
        return this.test("gecko") && !this.test("like gecko");
    }

    public boolean getFirefox() {
        return this.test("firefox") || this.test("firebird") || this.test("phoenix") || this.test("iceweasel");
    }

    public boolean getIceweasel() {
        return this.test("iceweasel");
    }

    public boolean getGaleon() {
        return this.test("galeon");
    }

    public boolean getKmeleon() {
        return this.test("k-meleon");
    }

    public boolean getEpiphany() {
        return this.test("epiphany");
    }

    public boolean getSafari() {
        return (this.test("safari") || this.test("applewebkit")) && !this.test("chrome");
    }

    public boolean getChrome() {
        return this.test("chrome");
    }

    public boolean getDillo() {
        return this.test("dillo");
    }

    public boolean getNetscape() {
        return this.test("netscape") || !this.getFirefox() && !this.getSafari() && this.test("mozilla") && !this.test("spoofer") && !this.test("compatible") && !this.test("opera") && !this.test("webtv") && !this.test("hotjava");
    }

    public boolean getNav2() {
        return this.getNetscape() && this.getMajorVersion() == 2;
    }

    public boolean getNav3() {
        return this.getNetscape() && this.getMajorVersion() == 3;
    }

    public boolean getNav4() {
        return this.getNetscape() && this.getMajorVersion() == 4;
    }

    public boolean getNav4up() {
        return this.getNetscape() && this.getMajorVersion() >= 4;
    }

    public boolean getNav45() {
        return this.getNetscape() && this.getMajorVersion() == 4 && this.getMinorVersion() == 5;
    }

    public boolean getNav45up() {
        return this.getNetscape() && this.getMajorVersion() >= 5 || this.getNav4() && this.getMinorVersion() >= 5;
    }

    public boolean getNavgold() {
        return this.test("gold");
    }

    public boolean getNav6() {
        return this.getNetscape() && this.getMajorVersion() == 5;
    }

    public boolean getNav6up() {
        return this.getNetscape() && this.getMajorVersion() >= 5;
    }

    public boolean getMozilla() {
        return this.getNetscape() && this.getGecko();
    }

    public boolean getIe() {
        return this.test("msie") && !this.test("opera") || this.test("microsoft internet explorer");
    }

    public boolean getIe3() {
        return this.getIe() && this.getMajorVersion() < 4;
    }

    public boolean getIe4() {
        return this.getIe() && this.getMajorVersion() == 4;
    }

    public boolean getIe4up() {
        return this.getIe() && this.getMajorVersion() >= 4;
    }

    public boolean getIe5() {
        return this.getIe() && this.getMajorVersion() == 5;
    }

    public boolean getIe5up() {
        return this.getIe() && this.getMajorVersion() >= 5;
    }

    public boolean getIe55() {
        return this.getIe() && this.getMajorVersion() == 5 && this.getMinorVersion() >= 5;
    }

    public boolean getIe55up() {
        return this.getIe5() && this.getMinorVersion() >= 5 || this.getIe() && this.getMajorVersion() >= 6;
    }

    public boolean getIe6() {
        return this.getIe() && this.getMajorVersion() == 6;
    }

    public boolean getIe6up() {
        return this.getIe() && this.getMajorVersion() >= 6;
    }

    public boolean getIe7() {
        return this.getIe() && this.getMajorVersion() == 7;
    }

    public boolean getIe7up() {
        return this.getIe() && this.getMajorVersion() >= 7;
    }

    public boolean getIe8() {
        return this.getIe() && this.getMajorVersion() == 8;
    }

    public boolean getIe8up() {
        return this.getIe() && this.getMajorVersion() >= 8;
    }

    public boolean getNeoplanet() {
        return this.test("neoplanet");
    }

    public boolean getNeoplanet2() {
        return this.getNeoplanet() && this.test("2.");
    }

    public boolean getAol() {
        return this.test("aol");
    }

    public boolean getAol3() {
        return this.test("aol 3.0") || this.getAol() && this.getIe3();
    }

    public boolean getAol4() {
        return this.test("aol 4.0") || this.getAol() && this.getIe4();
    }

    public boolean getAol5() {
        return this.test("aol 5.0");
    }

    public boolean getAol6() {
        return this.test("aol 6.0");
    }

    public boolean getAolTV() {
        return this.test("navio") || this.test("navio_aoltv");
    }

    public boolean getOpera() {
        return this.test("opera");
    }

    public boolean getOpera3() {
        return this.test("opera 3") || this.test("opera/3");
    }

    public boolean getOpera4() {
        return this.test("opera 4") || this.test("opera/4");
    }

    public boolean getOpera5() {
        return this.test("opera 5") || this.test("opera/5");
    }

    public boolean getOpera6() {
        return this.test("opera 6") || this.test("opera/6");
    }

    public boolean getOpera7() {
        return this.test("opera 7") || this.test("opera/7");
    }

    public boolean getOpera8() {
        return this.test("opera 8") || this.test("opera/8");
    }

    public boolean getOpera9() {
        return this.test("opera/9");
    }

    public boolean getHotjava() {
        return this.test("hotjava");
    }

    public boolean getHotjava3() {
        return this.getHotjava() && this.getMajorVersion() == 3;
    }

    public boolean getHotjava3up() {
        return this.getHotjava() && this.getMajorVersion() >= 3;
    }

    public boolean getLobo() {
        return this.test("lobo");
    }

    public boolean getHttpclient() {
        return this.test("httpclient");
    }

    public boolean getAmaya() {
        return this.test("amaya");
    }

    public boolean getCurl() {
        return this.test("libcurl");
    }

    public boolean getStaroffice() {
        return this.test("staroffice");
    }

    public boolean getIcab() {
        return this.test("icab");
    }

    public boolean getLotusnotes() {
        return this.test("lotus-notes");
    }

    public boolean getKonqueror() {
        return this.test("konqueror");
    }

    public boolean getLynx() {
        return this.test("lynx");
    }

    public boolean getLinks() {
        return this.test("links");
    }

    public boolean getW3m() {
        return this.test("w3m");
    }

    public boolean getWebTV() {
        return this.test("webtv");
    }

    public boolean getMosaic() {
        return this.test("mosaic");
    }

    public boolean getWget() {
        return this.test("wget");
    }

    public boolean getGetright() {
        return this.test("getright");
    }

    public boolean getLwp() {
        return this.test("libwww-perl") || this.test("lwp-");
    }

    public boolean getYahoo() {
        return this.test("yahoo");
    }

    public boolean getGoogle() {
        return this.test("google");
    }

    public boolean getJava() {
        return this.test("java") || this.test("jdk") || this.test("httpunit") || this.test("httpclient") || this.test("lobo");
    }

    public boolean getAltavista() {
        return this.test("altavista");
    }

    public boolean getScooter() {
        return this.test("scooter");
    }

    public boolean getLycos() {
        return this.test("lycos");
    }

    public boolean getInfoseek() {
        return this.test("infoseek");
    }

    public boolean getWebcrawler() {
        return this.test("webcrawler");
    }

    public boolean getLinkexchange() {
        return this.test("lecodechecker");
    }

    public boolean getSlurp() {
        return this.test("slurp");
    }

    public boolean getRobot() {
        return this.getWget() || this.getGetright() || this.getLwp() || this.getYahoo() || this.getGoogle() || this.getAltavista() || this.getScooter() || this.getLycos() || this.getInfoseek() || this.getWebcrawler() || this.getLinkexchange() || this.test("bot") || this.test("spider") || this.test("crawl") || this.test("agent") || this.test("seek") || this.test("search") || this.test("reap") || this.test("worm") || this.test("find") || this.test("index") || this.test("copy") || this.test("fetch") || this.test("ia_archive") || this.test("zyborg");
    }

    public boolean getBlackberry() {
        return this.test("blackberry");
    }

    public boolean getAudrey() {
        return this.test("audrey");
    }

    public boolean getIopener() {
        return this.test("i-opener");
    }

    public boolean getAvantgo() {
        return this.test("avantgo");
    }

    public boolean getPalm() {
        return this.getAvantgo() || this.test("palmos");
    }

    public boolean getWap() {
        return this.test("up.browser") || this.test("nokia") || this.test("alcatel") || this.test("ericsson") || this.userAgent.indexOf("sie-") == 0 || this.test("wmlib") || this.test(" wap") || this.test("wap ") || this.test("wap/") || this.test("-wap") || this.test("wap-") || this.userAgent.indexOf("wap") == 0 || this.test("wapper") || this.test("zetor");
    }

    public boolean getWin16() {
        return this.test("win16") || this.test("16bit") || this.test("windows 3") || this.test("windows 16-bit");
    }

    public boolean getWin3x() {
        return this.test("win16") || this.test("windows 3") || this.test("windows 16-bit");
    }

    public boolean getWin31() {
        return this.test("win16") || this.test("windows 3.1") || this.test("windows 16-bit");
    }

    public boolean getWin95() {
        return this.test("win95") || this.test("windows 95");
    }

    public boolean getWin98() {
        return this.test("win98") || this.test("windows 98");
    }

    public boolean getWinnt() {
        return this.test("winnt") || this.test("windows nt") || this.test("nt4") || this.test("nt3");
    }

    public boolean getWin2k() {
        return this.test("nt 5.0") || this.test("nt5");
    }

    public boolean getWinxp() {
        return this.test("nt 5.1");
    }

    public boolean getVista() {
        return this.test("nt 6.0");
    }

    public boolean getDotnet() {
        return this.test(".net clr");
    }

    public boolean getWinme() {
        return this.test("win 9x 4.90");
    }

    public boolean getWin32() {
        return this.getWin95() || this.getWin98() || this.getWinnt() || this.getWin2k() || this.getWinxp() || this.getWinme() || this.test("win32");
    }

    public boolean getWindows() {
        return this.getWin16() || this.getWin31() || this.getWin95() || this.getWin98() || this.getWinnt() || this.getWin32() || this.getWin2k() || this.getWinme() || this.test("win");
    }

    public boolean getMac() {
        return this.test("macintosh") || this.test("mac_");
    }

    public boolean getMacosx() {
        return this.test("macintosh") || this.test("mac os x");
    }

    public boolean getMac68k() {
        return this.getMac() && (this.test("68k") || this.test("68000"));
    }

    public boolean getMacppc() {
        return this.getMac() && (this.test("ppc") || this.test("powerpc"));
    }

    public boolean getAmiga() {
        return this.test("amiga");
    }

    public boolean getEmacs() {
        return this.test("emacs");
    }

    public boolean getOs2() {
        return this.test("os/2");
    }

    public boolean getSun() {
        return this.test("sun");
    }

    public boolean getSun4() {
        return this.test("sunos 4");
    }

    public boolean getSun5() {
        return this.test("sunos 5");
    }

    public boolean getSuni86() {
        return this.getSun() && this.test("i86");
    }

    public boolean getIrix() {
        return this.test("irix");
    }

    public boolean getIrix5() {
        return this.test("irix5");
    }

    public boolean getIrix6() {
        return this.test("irix6");
    }

    public boolean getHpux() {
        return this.test("hp-ux");
    }

    public boolean getHpux9() {
        return this.getHpux() && this.test("09.");
    }

    public boolean getHpux10() {
        return this.getHpux() && this.test("10.");
    }

    public boolean getAix() {
        return this.test("aix");
    }

    public boolean getAix1() {
        return this.test("aix 1");
    }

    public boolean getAix2() {
        return this.test("aix 2");
    }

    public boolean getAix3() {
        return this.test("aix 3");
    }

    public boolean getAix4() {
        return this.test("aix 4");
    }

    public boolean getLinux() {
        return this.test("linux");
    }

    public boolean getSco() {
        return this.test("sco") || this.test("unix_sv");
    }

    public boolean getUnixware() {
        return this.test("unix_system_v");
    }

    public boolean getMpras() {
        return this.test("ncr");
    }

    public boolean getReliant() {
        return this.test("reliantunix");
    }

    public boolean getDec() {
        return this.test("dec") || this.test("osf1") || this.test("delalpha") || this.test("alphaserver") || this.test("ultrix") || this.test("alphastation");
    }

    public boolean getSinix() {
        return this.test("sinix");
    }

    public boolean getFreebsd() {
        return this.test("freebsd");
    }

    public boolean getBsd() {
        return this.test("bsd");
    }

    public boolean getX11() {
        return this.test("x11");
    }

    public boolean getUnix() {
        return this.getX11() || this.getSun() || this.getIrix() || this.getHpux() || this.getSco() || this.getUnixware() || this.getMpras() || this.getReliant() || this.getDec() || this.getLinux() || this.getBsd() || this.test("unix");
    }

    public boolean getVMS() {
        return this.test("vax") || this.test("openvms");
    }

    public boolean getCss() {
        return this.getIe() && this.getMajorVersion() >= 4 || this.getNetscape() && this.getMajorVersion() >= 4 || this.getGecko() || this.getKonqueror() || this.getOpera() && this.getMajorVersion() >= 3 || this.getSafari() || this.getChrome() || this.getLinks();
    }

    public boolean getCss1() {
        return this.getCss();
    }

    public boolean getCss2() {
        return this.getIe() && this.getMac() && this.getMajorVersion() >= 5 || this.getWin32() && this.getMajorVersion() >= 6 || this.getGecko() || this.getOpera() && this.getMajorVersion() >= 4 || this.getSafari() && this.getMajorVersion() >= 2 || this.getKonqueror() && this.getMajorVersion() >= 2 || this.getChrome();
    }

    public boolean getDom0() {
        return this.getIe() && this.getMajorVersion() >= 3 || this.getNetscape() && this.getMajorVersion() >= 2 || this.getOpera() && this.getMajorVersion() >= 3 || this.getGecko() || this.getSafari() || this.getChrome() || this.getKonqueror();
    }

    public boolean getDom1() {
        return this.getIe() && this.getMajorVersion() >= 5 || this.getGecko() || this.getSafari() && this.getMajorVersion() >= 2 || this.getOpera() && this.getMajorVersion() >= 4 || this.getKonqueror() && this.getMajorVersion() >= 2 || this.getChrome();
    }

    public boolean getDom2() {
        return this.getIe() && this.getMajorVersion() >= 6 || this.getMozilla() && (double)this.getMajorVersion() >= 5.0 || this.getOpera() && this.getMajorVersion() >= 7 || this.getFirefox() || this.getChrome();
    }

    public boolean getJavascript() {
        return this.getDom0();
    }

    public String getPreferredLanguage() {
        if (this.preferredLanguage != null) {
            return this.preferredLanguage;
        }
        this.parseAcceptLanguage();
        if (this.languageRangesByQuality.size() == 0) {
            this.preferredLanguage = this.starLanguageRange;
        } else {
            ArrayList<List<String>> lists = new ArrayList<List<String>>(this.languageRangesByQuality.values());
            Collections.reverse(lists);
            for (List list : lists) {
                for (String l : list) {
                    this.preferredLanguage = this.filterLanguageTag(l);
                    if (this.preferredLanguage == null) continue;
                    break;
                }
                if (this.preferredLanguage == null) continue;
                break;
            }
        }
        if (this.preferredLanguage == null) {
            this.preferredLanguage = this.filterLanguageTag(this.languagesFilter == null ? this.getLocale().getDisplayName() : this.languagesFilter.get(0));
        }
        assert (this.preferredLanguage != null);
        return this.preferredLanguage;
    }

    public Locale getPreferredLocale() {
        return ConversionUtils.toLocale(this.getPreferredLanguage());
    }

    private boolean test(String key) {
        return this.userAgent.indexOf(key) != -1;
    }

    private void parseVersion() {
        block34: {
            try {
                Matcher g;
                Matcher mv;
                block33: {
                    Matcher netscape;
                    if (this.version != null) {
                        return;
                    }
                    Matcher v = genericVersion.matcher(this.userAgent);
                    if (v.find()) {
                        this.version = v.group(1);
                        if (this.version.endsWith(";")) {
                            this.version = this.version.substring(0, this.version.length() - 1);
                        }
                        try {
                            this.majorVersion = Integer.valueOf(v.group(2));
                            String minor = v.group(3);
                            this.minorVersion = minor.startsWith("0") ? 0 : Integer.valueOf(minor);
                        }
                        catch (NumberFormatException nfe) {
                            this.LOG.error((Object)("BrowserTool: Could not parse browser version for User-Agent: " + this.userAgent), (Throwable)nfe);
                        }
                    }
                    if (this.test("firefox")) {
                        Matcher fx = firefoxVersion.matcher(this.userAgent);
                        if (fx.find()) {
                            this.version = fx.group(1);
                            try {
                                this.majorVersion = Integer.valueOf(fx.group(2));
                                String minor = fx.group(3);
                                if (minor.startsWith("0")) {
                                    this.minorVersion = 0;
                                    break block33;
                                }
                                this.minorVersion = Integer.valueOf(minor);
                            }
                            catch (NumberFormatException nfe) {
                                this.LOG.error((Object)("BrowserTool: Could not parse browser version for User-Agent: " + this.userAgent), (Throwable)nfe);
                            }
                        }
                    } else if (this.test("compatible")) {
                        Matcher ie = ieVersion.matcher(this.userAgent);
                        if (ie.find()) {
                            this.version = ie.group(1);
                            try {
                                this.majorVersion = Integer.valueOf(ie.group(2));
                                String minor = ie.group(3);
                                if (minor.startsWith("0")) {
                                    this.minorVersion = 0;
                                    break block33;
                                }
                                this.minorVersion = Integer.valueOf(minor);
                            }
                            catch (NumberFormatException nfe) {
                                this.LOG.error((Object)("BrowserTool: Could not parse browser version for User-Agent: " + this.userAgent), (Throwable)nfe);
                            }
                        }
                    } else if (this.getSafari()) {
                        Matcher safari = safariVersion.matcher(this.userAgent);
                        if (safari.find()) {
                            this.version = safari.group(1);
                            try {
                                int sv = Integer.valueOf(safari.group(2));
                                this.majorVersion = sv / 100;
                                this.minorVersion = sv % 100;
                            }
                            catch (NumberFormatException nfe) {
                                this.LOG.error((Object)("BrowserTool: Could not parse browser version for User-Agent: " + this.userAgent), (Throwable)nfe);
                            }
                        }
                    } else if (this.getGecko() && this.getNetscape() && this.test("netscape") && (netscape = mozillaVersion.matcher(this.userAgent)).find()) {
                        this.version = netscape.group(1);
                        try {
                            this.majorVersion = Integer.valueOf(netscape.group(2));
                            String minor = netscape.group(3);
                            this.minorVersion = minor.startsWith("0") ? 0 : Integer.valueOf(minor);
                        }
                        catch (NumberFormatException nfe) {
                            this.LOG.error((Object)("BrowserTool: Could not parse browser version for User-Agent: " + this.userAgent), (Throwable)nfe);
                        }
                    }
                }
                if (this.version == null && (mv = fallbackVersion.matcher(this.userAgent)).find()) {
                    this.version = mv.group(1);
                    try {
                        this.majorVersion = Integer.valueOf(this.version);
                        this.minorVersion = 0;
                    }
                    catch (NumberFormatException nfe) {
                        this.LOG.error((Object)("BrowserTool: Could not parse browser version for User-Agent: " + this.userAgent), (Throwable)nfe);
                    }
                }
                if (!this.getGecko() || !(g = Pattern.compile("\\([^)]*rv:(([\\d]*)\\.([\\d]*).*?)\\)").matcher(this.userAgent)).find()) break block34;
                this.geckoVersion = g.group(1);
                try {
                    this.geckoMajorVersion = Integer.valueOf(g.group(2));
                    String minor = g.group(3);
                    if (minor.startsWith("0")) {
                        this.geckoMinorVersion = 0;
                        break block34;
                    }
                    this.geckoMinorVersion = Integer.valueOf(minor);
                }
                catch (NumberFormatException nfe) {
                    this.LOG.error((Object)("BrowserTool: Could not parse browser version for User-Agent: " + this.userAgent), (Throwable)nfe);
                }
            }
            catch (PatternSyntaxException pse) {
                this.LOG.error((Object)("BrowserTool: Could not parse browser version for User-Agent: " + this.userAgent), (Throwable)pse);
            }
        }
    }

    private void parseAcceptLanguage() {
        if (this.languageRangesByQuality != null) {
            return;
        }
        this.languageRangesByQuality = new TreeMap<Float, List<String>>();
        StringTokenizer languageTokenizer = new StringTokenizer(this.acceptLanguage, ",");
        while (languageTokenizer.hasMoreTokens()) {
            String language = languageTokenizer.nextToken().trim();
            int qValueIndex = language.indexOf(59);
            if (qValueIndex == -1) {
                language = language.replace('-', '_');
                ArrayList<String> l = (ArrayList<String>)this.languageRangesByQuality.get(Float.valueOf(1.0f));
                if (l == null) {
                    l = new ArrayList<String>();
                    this.languageRangesByQuality.put(Float.valueOf(1.0f), l);
                }
                l.add(language);
                continue;
            }
            String code = language.substring(0, qValueIndex).trim().replace('-', '_');
            String qval = language.substring(qValueIndex + 1).trim();
            if ("*".equals(qval)) {
                this.starLanguageRange = code;
                continue;
            }
            Matcher m = quality.matcher(qval);
            if (m.matches()) {
                Float q = Float.valueOf(m.group(1));
                ArrayList<String> al = (ArrayList<String>)this.languageRangesByQuality.get(q);
                if (al == null) {
                    al = new ArrayList<String>();
                    this.languageRangesByQuality.put(q, al);
                }
                al.add(code);
                continue;
            }
            this.LOG.error((Object)("BrowserTool: could not parse language quality value: " + language));
        }
    }

    private String filterLanguageTag(String languageTag) {
        String[] parts;
        languageTag = languageTag.replace('-', '_');
        if (this.languagesFilter == null) {
            return languageTag;
        }
        if (this.languagesFilter.contains(languageTag)) {
            return languageTag;
        }
        if (languageTag.contains("_") && this.languagesFilter.contains((parts = languageTag.split("_"))[0])) {
            return parts[0];
        }
        return null;
    }
}

