/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.util;

import java.io.IOException;
import java.io.Serializable;

public class JaxmURI
implements Serializable {
    private static final String RESERVED_CHARACTERS = ";/?:@&=+$,";
    private static final String MARK_CHARACTERS = "-_.!~*'() ";
    private static final String SCHEME_CHARACTERS = "+-.";
    private static final String USERINFO_CHARACTERS = ";:&=+$,";
    private String m_scheme = null;
    private String m_userinfo = null;
    private String m_host = null;
    private int m_port = -1;
    private String m_path = null;
    private String m_queryString = null;
    private String m_fragment = null;

    public JaxmURI() {
    }

    public JaxmURI(JaxmURI p_other) {
        this.initialize(p_other);
    }

    public JaxmURI(String p_uriSpec) throws MalformedURIException {
        this((JaxmURI)null, p_uriSpec);
    }

    public JaxmURI(JaxmURI p_base, String p_uriSpec) throws MalformedURIException {
        this.initialize(p_base, p_uriSpec);
    }

    public JaxmURI(String p_scheme, String p_schemeSpecificPart) throws MalformedURIException {
        if (p_scheme == null || p_scheme.trim().length() == 0) {
            throw new MalformedURIException("Cannot construct URI with null/empty scheme!");
        }
        if (p_schemeSpecificPart == null || p_schemeSpecificPart.trim().length() == 0) {
            throw new MalformedURIException("Cannot construct URI with null/empty scheme-specific part!");
        }
        this.setScheme(p_scheme);
        this.setPath(p_schemeSpecificPart);
    }

    public JaxmURI(String p_scheme, String p_host, String p_path, String p_queryString, String p_fragment) throws MalformedURIException {
        this(p_scheme, null, p_host, -1, p_path, p_queryString, p_fragment);
    }

    public JaxmURI(String p_scheme, String p_userinfo, String p_host, int p_port, String p_path, String p_queryString, String p_fragment) throws MalformedURIException {
        if (p_scheme == null || p_scheme.trim().length() == 0) {
            throw new MalformedURIException("Scheme is required!");
        }
        if (p_host == null) {
            if (p_userinfo != null) {
                throw new MalformedURIException("Userinfo may not be specified if host is not specified!");
            }
            if (p_port != -1) {
                throw new MalformedURIException("Port may not be specified if host is not specified!");
            }
        }
        if (p_path != null) {
            if (p_path.indexOf(63) != -1 && p_queryString != null) {
                throw new MalformedURIException("Query string cannot be specified in path and query string!");
            }
            if (p_path.indexOf(35) != -1 && p_fragment != null) {
                throw new MalformedURIException("Fragment cannot be specified in both the path and fragment!");
            }
        }
        this.setScheme(p_scheme);
        this.setHost(p_host);
        this.setPort(p_port);
        this.setUserinfo(p_userinfo);
        this.setPath(p_path);
        this.setQueryString(p_queryString);
        this.setFragment(p_fragment);
    }

    private void initialize(JaxmURI p_other) {
        this.m_scheme = p_other.getScheme();
        this.m_userinfo = p_other.getUserinfo();
        this.m_host = p_other.getHost();
        this.m_port = p_other.getPort();
        this.m_path = p_other.getPath();
        this.m_queryString = p_other.getQueryString();
        this.m_fragment = p_other.getFragment();
    }

    private void initialize(JaxmURI p_base, String p_uriSpec) throws MalformedURIException {
        if (p_base == null && (p_uriSpec == null || p_uriSpec.trim().length() == 0)) {
            throw new MalformedURIException("Cannot initialize URI with empty parameters.");
        }
        if (p_uriSpec == null || p_uriSpec.trim().length() == 0) {
            this.initialize(p_base);
            return;
        }
        String uriSpec = p_uriSpec.trim();
        int uriSpecLen = uriSpec.length();
        int index = 0;
        int colonIdx = uriSpec.indexOf(58);
        int slashIdx = uriSpec.indexOf(47);
        if (colonIdx < 2 || colonIdx > slashIdx && slashIdx != -1) {
            int fragmentIdx = uriSpec.indexOf(35);
            if (p_base == null && fragmentIdx != 0) {
                throw new MalformedURIException("No scheme found in URI.");
            }
        } else {
            this.initializeScheme(uriSpec);
            index = this.m_scheme.length() + 1;
        }
        if (index + 1 < uriSpecLen && uriSpec.substring(index).startsWith("//")) {
            int startPos = index += 2;
            char testChar = '\u0000';
            while (index < uriSpecLen && (testChar = uriSpec.charAt(index)) != '/' && testChar != '?' && testChar != '#') {
                ++index;
            }
            if (index > startPos) {
                this.initializeAuthority(uriSpec.substring(startPos, index));
            } else {
                this.m_host = "";
            }
        }
        this.initializePath(uriSpec.substring(index));
        if (p_base != null) {
            int lastSlash;
            if (this.m_path.length() == 0 && this.m_scheme == null && this.m_host == null) {
                this.m_scheme = p_base.getScheme();
                this.m_userinfo = p_base.getUserinfo();
                this.m_host = p_base.getHost();
                this.m_port = p_base.getPort();
                this.m_path = p_base.getPath();
                if (this.m_queryString == null) {
                    this.m_queryString = p_base.getQueryString();
                }
                return;
            }
            if (this.m_scheme != null) {
                return;
            }
            this.m_scheme = p_base.getScheme();
            if (this.m_host != null) {
                return;
            }
            this.m_userinfo = p_base.getUserinfo();
            this.m_host = p_base.getHost();
            this.m_port = p_base.getPort();
            if (this.m_path.length() > 0 && this.m_path.startsWith("/")) {
                return;
            }
            String path = "";
            String basePath = p_base.getPath();
            if (basePath != null && (lastSlash = basePath.lastIndexOf(47)) != -1) {
                path = basePath.substring(0, lastSlash + 1);
            }
            path = path.concat(this.m_path);
            index = -1;
            while ((index = path.indexOf("/./")) != -1) {
                path = path.substring(0, index + 1).concat(path.substring(index + 3));
            }
            if (path.endsWith("/.")) {
                path = path.substring(0, path.length() - 1);
            }
            index = 1;
            int segIndex = -1;
            String tempString = null;
            while ((index = path.indexOf("/../", index)) > 0) {
                tempString = path.substring(0, path.indexOf("/../"));
                segIndex = tempString.lastIndexOf(47);
                if (segIndex != -1) {
                    if (!tempString.substring(segIndex++).equals("src/main")) {
                        path = path.substring(0, segIndex).concat(path.substring(index + 4));
                        continue;
                    }
                    index += 4;
                    continue;
                }
                index += 4;
            }
            if (path.endsWith("/src/main") && (segIndex = (tempString = path.substring(0, path.length() - 3)).lastIndexOf(47)) != -1) {
                path = path.substring(0, segIndex + 1);
            }
            this.m_path = path;
        }
    }

    private void initializeScheme(String p_uriSpec) throws MalformedURIException {
        int index;
        int uriSpecLen = p_uriSpec.length();
        String scheme = null;
        char testChar = '\u0000';
        for (index = 0; index < uriSpecLen && (testChar = p_uriSpec.charAt(index)) != ':' && testChar != '/' && testChar != '?' && testChar != '#'; ++index) {
        }
        scheme = p_uriSpec.substring(0, index);
        if (scheme.length() == 0) {
            throw new MalformedURIException("No scheme found in URI.");
        }
        this.setScheme(scheme);
    }

    private void initializeAuthority(String p_uriSpec) throws MalformedURIException {
        int index;
        int start = 0;
        int end = p_uriSpec.length();
        char testChar = '\u0000';
        String userinfo = null;
        if (p_uriSpec.indexOf(64, start) != -1) {
            for (index = 0; index < end && (testChar = p_uriSpec.charAt(index)) != '@'; ++index) {
            }
            userinfo = p_uriSpec.substring(start, index);
            ++index;
        }
        String host = null;
        start = index;
        while (index < end && (testChar = p_uriSpec.charAt(index)) != ':') {
            ++index;
        }
        host = p_uriSpec.substring(start, index);
        int port = -1;
        if (host.length() > 0 && testChar == ':') {
            start = ++index;
            while (index < end) {
                ++index;
            }
            String portStr = p_uriSpec.substring(start, index);
            if (portStr.length() > 0) {
                for (int i = 0; i < portStr.length(); ++i) {
                    if (JaxmURI.isDigit(portStr.charAt(i))) continue;
                    throw new MalformedURIException(portStr + " is invalid. Port should only contain digits!");
                }
                try {
                    port = Integer.parseInt(portStr);
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
        }
        this.setHost(host);
        this.setPort(port);
        this.setUserinfo(userinfo);
    }

    private void initializePath(String p_uriSpec) throws MalformedURIException {
        int index;
        if (p_uriSpec == null) {
            throw new MalformedURIException("Cannot initialize path from null string!");
        }
        int start = 0;
        int end = p_uriSpec.length();
        char testChar = '\u0000';
        for (index = 0; index < end && (testChar = p_uriSpec.charAt(index)) != '?' && testChar != '#'; ++index) {
            if (testChar == '%') {
                if (index + 2 < end && JaxmURI.isHex(p_uriSpec.charAt(index + 1)) && JaxmURI.isHex(p_uriSpec.charAt(index + 2))) continue;
                throw new MalformedURIException("Path contains invalid escape sequence!");
            }
            if (JaxmURI.isReservedCharacter(testChar) || JaxmURI.isUnreservedCharacter(testChar)) continue;
            throw new MalformedURIException("Path contains invalid character: " + testChar);
        }
        this.m_path = p_uriSpec.substring(start, index);
        if (testChar == '?') {
            start = ++index;
            while (index < end && (testChar = p_uriSpec.charAt(index)) != '#') {
                if (testChar == '%') {
                    if (index + 2 >= end || !JaxmURI.isHex(p_uriSpec.charAt(index + 1)) || !JaxmURI.isHex(p_uriSpec.charAt(index + 2))) {
                        throw new MalformedURIException("Query string contains invalid escape sequence!");
                    }
                } else if (!JaxmURI.isReservedCharacter(testChar) && !JaxmURI.isUnreservedCharacter(testChar)) {
                    throw new MalformedURIException("Query string contains invalid character:" + testChar);
                }
                ++index;
            }
            this.m_queryString = p_uriSpec.substring(start, index);
        }
        if (testChar == '#') {
            start = ++index;
            while (index < end) {
                testChar = p_uriSpec.charAt(index);
                if (testChar == '%') {
                    if (index + 2 >= end || !JaxmURI.isHex(p_uriSpec.charAt(index + 1)) || !JaxmURI.isHex(p_uriSpec.charAt(index + 2))) {
                        throw new MalformedURIException("Fragment contains invalid escape sequence!");
                    }
                } else if (!JaxmURI.isReservedCharacter(testChar) && !JaxmURI.isUnreservedCharacter(testChar)) {
                    throw new MalformedURIException("Fragment contains invalid character:" + testChar);
                }
                ++index;
            }
            this.m_fragment = p_uriSpec.substring(start, index);
        }
    }

    public String getScheme() {
        return this.m_scheme;
    }

    public String getSchemeSpecificPart() {
        StringBuilder schemespec = new StringBuilder();
        if (this.m_userinfo != null || this.m_host != null || this.m_port != -1) {
            schemespec.append("//");
        }
        if (this.m_userinfo != null) {
            schemespec.append(this.m_userinfo);
            schemespec.append('@');
        }
        if (this.m_host != null) {
            schemespec.append(this.m_host);
        }
        if (this.m_port != -1) {
            schemespec.append(':');
            schemespec.append(this.m_port);
        }
        if (this.m_path != null) {
            schemespec.append(this.m_path);
        }
        if (this.m_queryString != null) {
            schemespec.append('?');
            schemespec.append(this.m_queryString);
        }
        if (this.m_fragment != null) {
            schemespec.append('#');
            schemespec.append(this.m_fragment);
        }
        return schemespec.toString();
    }

    public String getUserinfo() {
        return this.m_userinfo;
    }

    public String getHost() {
        return this.m_host;
    }

    public int getPort() {
        return this.m_port;
    }

    public String getPath(boolean p_includeQueryString, boolean p_includeFragment) {
        StringBuilder pathString = new StringBuilder(this.m_path);
        if (p_includeQueryString && this.m_queryString != null) {
            pathString.append('?');
            pathString.append(this.m_queryString);
        }
        if (p_includeFragment && this.m_fragment != null) {
            pathString.append('#');
            pathString.append(this.m_fragment);
        }
        return pathString.toString();
    }

    public String getPath() {
        return this.m_path;
    }

    public String getQueryString() {
        return this.m_queryString;
    }

    public String getFragment() {
        return this.m_fragment;
    }

    public void setScheme(String p_scheme) throws MalformedURIException {
        if (p_scheme == null) {
            throw new MalformedURIException("Cannot set scheme from null string!");
        }
        if (!JaxmURI.isConformantSchemeName(p_scheme)) {
            throw new MalformedURIException("The scheme is not conformant.");
        }
        this.m_scheme = p_scheme.toLowerCase();
    }

    public void setUserinfo(String p_userinfo) throws MalformedURIException {
        if (p_userinfo == null) {
            this.m_userinfo = null;
        } else {
            if (this.m_host == null) {
                throw new MalformedURIException("Userinfo cannot be set when host is null!");
            }
            int end = p_userinfo.length();
            char testChar = '\u0000';
            for (int index = 0; index < end; ++index) {
                testChar = p_userinfo.charAt(index);
                if (testChar == '%') {
                    if (index + 2 < end && JaxmURI.isHex(p_userinfo.charAt(index + 1)) && JaxmURI.isHex(p_userinfo.charAt(index + 2))) continue;
                    throw new MalformedURIException("Userinfo contains invalid escape sequence!");
                }
                if (JaxmURI.isUnreservedCharacter(testChar) || USERINFO_CHARACTERS.indexOf(testChar) != -1) continue;
                throw new MalformedURIException("Userinfo contains invalid character:" + testChar);
            }
        }
        this.m_userinfo = p_userinfo;
    }

    public void setHost(String p_host) throws MalformedURIException {
        if (p_host == null || p_host.trim().length() == 0) {
            this.m_host = p_host;
            this.m_userinfo = null;
            this.m_port = -1;
        } else if (!JaxmURI.isWellFormedAddress(p_host)) {
            throw new MalformedURIException("Host is not a well formed address!");
        }
        this.m_host = p_host;
    }

    public void setPort(int p_port) throws MalformedURIException {
        if (p_port >= 0 && p_port <= 65535) {
            if (this.m_host == null) {
                throw new MalformedURIException("Port cannot be set when host is null!");
            }
        } else if (p_port != -1) {
            throw new MalformedURIException("Invalid port number!");
        }
        this.m_port = p_port;
    }

    public void setPath(String p_path) throws MalformedURIException {
        if (p_path == null) {
            this.m_path = null;
            this.m_queryString = null;
            this.m_fragment = null;
        } else {
            this.initializePath(p_path);
        }
    }

    public void appendPath(String p_addToPath) throws MalformedURIException {
        if (p_addToPath == null || p_addToPath.trim().length() == 0) {
            return;
        }
        if (!JaxmURI.isURIString(p_addToPath)) {
            throw new MalformedURIException("Path contains invalid character!");
        }
        this.m_path = this.m_path == null || this.m_path.trim().length() == 0 ? (p_addToPath.startsWith("/") ? p_addToPath : "/" + p_addToPath) : (this.m_path.endsWith("/") ? (p_addToPath.startsWith("/") ? this.m_path.concat(p_addToPath.substring(1)) : this.m_path.concat(p_addToPath)) : (p_addToPath.startsWith("/") ? this.m_path.concat(p_addToPath) : this.m_path.concat("/" + p_addToPath)));
    }

    public void setQueryString(String p_queryString) throws MalformedURIException {
        if (p_queryString == null) {
            this.m_queryString = null;
        } else {
            if (!this.isGenericURI()) {
                throw new MalformedURIException("Query string can only be set for a generic URI!");
            }
            if (this.getPath() == null) {
                throw new MalformedURIException("Query string cannot be set when path is null!");
            }
            if (!JaxmURI.isURIString(p_queryString)) {
                throw new MalformedURIException("Query string contains invalid character!");
            }
            this.m_queryString = p_queryString;
        }
    }

    public void setFragment(String p_fragment) throws MalformedURIException {
        if (p_fragment == null) {
            this.m_fragment = null;
        } else {
            if (!this.isGenericURI()) {
                throw new MalformedURIException("Fragment can only be set for a generic URI!");
            }
            if (this.getPath() == null) {
                throw new MalformedURIException("Fragment cannot be set when path is null!");
            }
            if (!JaxmURI.isURIString(p_fragment)) {
                throw new MalformedURIException("Fragment contains invalid character!");
            }
            this.m_fragment = p_fragment;
        }
    }

    public boolean equals(Object p_test) {
        if (p_test instanceof JaxmURI) {
            JaxmURI testURI = (JaxmURI)p_test;
            if ((this.m_scheme == null && testURI.m_scheme == null || this.m_scheme != null && testURI.m_scheme != null && this.m_scheme.equals(testURI.m_scheme)) && (this.m_userinfo == null && testURI.m_userinfo == null || this.m_userinfo != null && testURI.m_userinfo != null && this.m_userinfo.equals(testURI.m_userinfo)) && (this.m_host == null && testURI.m_host == null || this.m_host != null && testURI.m_host != null && this.m_host.equals(testURI.m_host)) && this.m_port == testURI.m_port && (this.m_path == null && testURI.m_path == null || this.m_path != null && testURI.m_path != null && this.m_path.equals(testURI.m_path)) && (this.m_queryString == null && testURI.m_queryString == null || this.m_queryString != null && testURI.m_queryString != null && this.m_queryString.equals(testURI.m_queryString)) && (this.m_fragment == null && testURI.m_fragment == null || this.m_fragment != null && testURI.m_fragment != null && this.m_fragment.equals(testURI.m_fragment))) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return 153214;
    }

    public String toString() {
        StringBuilder uriSpecString = new StringBuilder();
        if (this.m_scheme != null) {
            uriSpecString.append(this.m_scheme);
            uriSpecString.append(':');
        }
        uriSpecString.append(this.getSchemeSpecificPart());
        return uriSpecString.toString();
    }

    public boolean isGenericURI() {
        return this.m_host != null;
    }

    public static boolean isConformantSchemeName(String p_scheme) {
        if (p_scheme == null || p_scheme.trim().length() == 0) {
            return false;
        }
        if (!JaxmURI.isAlpha(p_scheme.charAt(0))) {
            return false;
        }
        for (int i = 1; i < p_scheme.length(); ++i) {
            char testChar = p_scheme.charAt(i);
            if (JaxmURI.isAlphanum(testChar) || SCHEME_CHARACTERS.indexOf(testChar) != -1) continue;
            return false;
        }
        return true;
    }

    public static boolean isWellFormedAddress(String p_address) {
        if (p_address == null) {
            return false;
        }
        String address = p_address.trim();
        int addrLength = address.length();
        if (addrLength == 0 || addrLength > 255) {
            return false;
        }
        if (address.startsWith(".") || address.startsWith("-")) {
            return false;
        }
        int index = address.lastIndexOf(46);
        if (address.endsWith(".")) {
            index = address.substring(0, index).lastIndexOf(46);
        }
        if (index + 1 < addrLength && JaxmURI.isDigit(p_address.charAt(index + 1))) {
            int numDots = 0;
            for (int i = 0; i < addrLength; ++i) {
                char testChar = address.charAt(i);
                if (testChar == '.') {
                    if (!JaxmURI.isDigit(address.charAt(i - 1)) || i + 1 < addrLength && !JaxmURI.isDigit(address.charAt(i + 1))) {
                        return false;
                    }
                    ++numDots;
                    continue;
                }
                if (JaxmURI.isDigit(testChar)) continue;
                return false;
            }
            if (numDots != 3) {
                return false;
            }
        } else {
            for (int i = 0; i < addrLength; ++i) {
                char testChar = address.charAt(i);
                if (testChar == '.') {
                    if (!JaxmURI.isAlphanum(address.charAt(i - 1))) {
                        return false;
                    }
                    if (i + 1 >= addrLength || JaxmURI.isAlphanum(address.charAt(i + 1))) continue;
                    return false;
                }
                if (JaxmURI.isAlphanum(testChar) || testChar == '-') continue;
                return false;
            }
        }
        return true;
    }

    private static boolean isDigit(char p_char) {
        return p_char >= '0' && p_char <= '9';
    }

    private static boolean isHex(char p_char) {
        return JaxmURI.isDigit(p_char) || p_char >= 'a' && p_char <= 'f' || p_char >= 'A' && p_char <= 'F';
    }

    private static boolean isAlpha(char p_char) {
        return p_char >= 'a' && p_char <= 'z' || p_char >= 'A' && p_char <= 'Z';
    }

    private static boolean isAlphanum(char p_char) {
        return JaxmURI.isAlpha(p_char) || JaxmURI.isDigit(p_char);
    }

    private static boolean isReservedCharacter(char p_char) {
        return RESERVED_CHARACTERS.indexOf(p_char) != -1;
    }

    private static boolean isUnreservedCharacter(char p_char) {
        return JaxmURI.isAlphanum(p_char) || MARK_CHARACTERS.indexOf(p_char) != -1;
    }

    private static boolean isURIString(String p_uric) {
        if (p_uric == null) {
            return false;
        }
        int end = p_uric.length();
        char testChar = '\u0000';
        for (int i = 0; i < end; ++i) {
            testChar = p_uric.charAt(i);
            if (testChar == '%') {
                if (i + 2 >= end || !JaxmURI.isHex(p_uric.charAt(i + 1)) || !JaxmURI.isHex(p_uric.charAt(i + 2))) {
                    return false;
                }
                i += 2;
                continue;
            }
            if (JaxmURI.isReservedCharacter(testChar) || JaxmURI.isUnreservedCharacter(testChar)) continue;
            return false;
        }
        return true;
    }

    public static class MalformedURIException
    extends IOException {
        public MalformedURIException() {
        }

        public MalformedURIException(String p_msg) {
            super(p_msg);
        }
    }
}

