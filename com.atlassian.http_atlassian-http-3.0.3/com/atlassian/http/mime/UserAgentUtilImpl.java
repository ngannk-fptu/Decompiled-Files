/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.http.mime;

import com.atlassian.http.mime.UserAgentUtil;

public class UserAgentUtilImpl
implements UserAgentUtil {
    @Override
    public UserAgentUtil.UserAgent getUserAgentInfo(String userAgent) {
        return new UserAgentUtil.UserAgent(this.getBrowser(userAgent), this.getOS(userAgent));
    }

    @Override
    public UserAgentUtil.BrowserFamily getBrowserFamily(String userAgent) {
        if (userAgent == null) {
            return UserAgentUtil.BrowserFamily.UKNOWN;
        }
        for (UserAgentUtil.BrowserFamily browserFamily : UserAgentUtil.BrowserFamily.values()) {
            if (!userAgent.contains(browserFamily.getUserAgentString())) continue;
            return browserFamily;
        }
        return UserAgentUtil.BrowserFamily.UKNOWN;
    }

    private String getVersionNumber(String useragent, int pos) {
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

    private UserAgentUtil.OperatingSystem getOS(String userAgent) {
        if (userAgent == null) {
            return new UserAgentUtil.OperatingSystem(UserAgentUtil.OperatingSystem.OperatingSystemFamily.UNKNOWN);
        }
        for (UserAgentUtil.OperatingSystem.OperatingSystemFamily osFamily : UserAgentUtil.OperatingSystem.OperatingSystemFamily.values()) {
            if (!userAgent.contains(osFamily.getUserAgentString())) continue;
            return new UserAgentUtil.OperatingSystem(osFamily);
        }
        return new UserAgentUtil.OperatingSystem(UserAgentUtil.OperatingSystem.OperatingSystemFamily.UNKNOWN);
    }

    private UserAgentUtil.Browser getBrowser(String userAgent) {
        if (userAgent == null) {
            return new UserAgentUtil.Browser(UserAgentUtil.BrowserFamily.UKNOWN, UserAgentUtil.BrowserMajorVersion.UNKNOWN, "0");
        }
        for (UserAgentUtil.BrowserFamily browserFamily : UserAgentUtil.BrowserFamily.values()) {
            if (!userAgent.contains(browserFamily.getUserAgentString())) continue;
            for (UserAgentUtil.BrowserMajorVersion majorVersion : UserAgentUtil.BrowserMajorVersion.values()) {
                int pos;
                if (!majorVersion.getBrowserFamily().equals((Object)browserFamily) || (pos = userAgent.indexOf(majorVersion.getUserAgentString())) <= -1) continue;
                return new UserAgentUtil.Browser(browserFamily, majorVersion, majorVersion.getMinorVersionPrefix() + this.getVersionNumber(userAgent, pos + majorVersion.getVersionPos()));
            }
            int pos = userAgent.indexOf(browserFamily.getUserAgentString());
            return new UserAgentUtil.Browser(browserFamily, UserAgentUtil.BrowserMajorVersion.UNKNOWN, browserFamily.getUserAgentString() + this.getVersionNumber(userAgent, pos + browserFamily.getUserAgentString().length()));
        }
        return new UserAgentUtil.Browser(UserAgentUtil.BrowserFamily.UKNOWN, UserAgentUtil.BrowserMajorVersion.UNKNOWN, "0");
    }
}

