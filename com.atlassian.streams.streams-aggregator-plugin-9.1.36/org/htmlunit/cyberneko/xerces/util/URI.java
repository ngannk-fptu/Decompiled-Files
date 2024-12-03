/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.util;

import java.io.IOException;
import java.util.Locale;

public class URI {
    private static final byte[] fgLookupTable = new byte[128];
    private static final int RESERVED_CHARACTERS = 1;
    private static final int MARK_CHARACTERS = 2;
    private static final int SCHEME_CHARACTERS = 4;
    private static final int USERINFO_CHARACTERS = 8;
    private static final int ASCII_ALPHA_CHARACTERS = 16;
    private static final int ASCII_DIGIT_CHARACTERS = 32;
    private static final int ASCII_HEX_CHARACTERS = 64;
    private static final int PATH_CHARACTERS = 128;
    private static final int MASK_ALPHA_NUMERIC = 48;
    private static final int MASK_UNRESERVED_MASK = 50;
    private static final int MASK_URI_CHARACTER = 51;
    private static final int MASK_SCHEME_CHARACTER = 52;
    private static final int MASK_USERINFO_CHARACTER = 58;
    private static final int MASK_PATH_CHARACTER = 178;
    private String scheme_ = null;
    private String userinfo_ = null;
    private String host_ = null;
    private int port_ = -1;
    private String regAuthority_ = null;
    private String path_ = null;
    private String queryString_ = null;
    private String fragment_ = null;

    public URI(String uriSpec) throws MalformedURIException {
        this(null, uriSpec);
    }

    public URI(String uriSpec, boolean allowNonAbsoluteURI) throws MalformedURIException {
        this(null, uriSpec, allowNonAbsoluteURI);
    }

    public URI(URI base, String uriSpec) throws MalformedURIException {
        this.initialize(base, uriSpec);
    }

    public URI(URI base, String uriSpec, boolean allowNonAbsoluteURI) throws MalformedURIException {
        this.initialize(base, uriSpec, allowNonAbsoluteURI);
    }

    public URI(String scheme, String host, String path, String queryString, String fragment) throws MalformedURIException {
        this(scheme, null, host, -1, path, queryString, fragment);
    }

    public URI(String scheme, String userinfo, String host, int port, String path, String queryString, String fragment) throws MalformedURIException {
        if (scheme == null || scheme.trim().length() == 0) {
            throw new MalformedURIException("Scheme is required!");
        }
        if (host == null) {
            if (userinfo != null) {
                throw new MalformedURIException("Userinfo may not be specified if host is not specified!");
            }
            if (port != -1) {
                throw new MalformedURIException("Port may not be specified if host is not specified!");
            }
        }
        if (path != null) {
            if (path.indexOf(63) != -1 && queryString != null) {
                throw new MalformedURIException("Query string cannot be specified in path and query string!");
            }
            if (path.indexOf(35) != -1 && fragment != null) {
                throw new MalformedURIException("Fragment cannot be specified in both the path and fragment!");
            }
        }
        this.setScheme(scheme);
        this.setHost(host);
        this.setPort(port);
        this.setUserinfo(userinfo);
        this.setPath(path);
        this.setQueryString(queryString);
        this.setFragment(fragment);
    }

    private void initialize(URI other) {
        this.scheme_ = other.getScheme();
        this.userinfo_ = other.getUserinfo();
        this.host_ = other.getHost();
        this.port_ = other.getPort();
        this.regAuthority_ = other.getRegBasedAuthority();
        this.path_ = other.getPath();
        this.queryString_ = other.getQueryString();
        this.fragment_ = other.getFragment();
    }

    private void initialize(URI base, String uriSpec, boolean allowNonAbsoluteURI) throws MalformedURIException {
        int uriSpecLen;
        int n = uriSpecLen = uriSpec != null ? uriSpec.length() : 0;
        if (base == null && uriSpecLen == 0) {
            if (allowNonAbsoluteURI) {
                this.path_ = "";
                return;
            }
            throw new MalformedURIException("Cannot initialize URI with empty parameters.");
        }
        if (uriSpecLen == 0) {
            this.initialize(base);
            return;
        }
        int index = 0;
        int colonIdx = uriSpec.indexOf(58);
        if (colonIdx != -1) {
            int searchFrom = colonIdx - 1;
            int slashIdx = uriSpec.lastIndexOf(47, searchFrom);
            int queryIdx = uriSpec.lastIndexOf(63, searchFrom);
            int fragmentIdx = uriSpec.lastIndexOf(35, searchFrom);
            if (colonIdx == 0 || slashIdx != -1 || queryIdx != -1 || fragmentIdx != -1) {
                if (colonIdx == 0 || base == null && fragmentIdx != 0 && !allowNonAbsoluteURI) {
                    throw new MalformedURIException("No scheme found in URI.");
                }
            } else {
                this.initializeScheme(uriSpec);
                index = this.scheme_.length() + 1;
                if (colonIdx == uriSpecLen - 1 || uriSpec.charAt(colonIdx + 1) == '#') {
                    throw new MalformedURIException("Scheme specific part cannot be empty.");
                }
            }
        } else if (base == null && uriSpec.indexOf(35) != 0 && !allowNonAbsoluteURI) {
            throw new MalformedURIException("No scheme found in URI.");
        }
        if (index + 1 < uriSpecLen && uriSpec.charAt(index) == '/' && uriSpec.charAt(index + 1) == '/') {
            char testChar;
            int startPos = index += 2;
            while (index < uriSpecLen && (testChar = uriSpec.charAt(index)) != '/' && testChar != '?' && testChar != '#') {
                ++index;
            }
            if (index > startPos) {
                if (!this.initializeAuthority(uriSpec.substring(startPos, index))) {
                    index = startPos - 2;
                }
            } else {
                this.host_ = "";
            }
        }
        this.initializePath(uriSpec, index);
        if (base != null) {
            this.absolutize(base);
        }
    }

    private void initialize(URI base, String uriSpec) throws MalformedURIException {
        int uriSpecLen;
        int n = uriSpecLen = uriSpec != null ? uriSpec.length() : 0;
        if (base == null && uriSpecLen == 0) {
            throw new MalformedURIException("Cannot initialize URI with empty parameters.");
        }
        if (uriSpecLen == 0) {
            this.initialize(base);
            return;
        }
        int index = 0;
        int colonIdx = uriSpec.indexOf(58);
        if (colonIdx != -1) {
            int searchFrom = colonIdx - 1;
            int slashIdx = uriSpec.lastIndexOf(47, searchFrom);
            int queryIdx = uriSpec.lastIndexOf(63, searchFrom);
            int fragmentIdx = uriSpec.lastIndexOf(35, searchFrom);
            if (colonIdx == 0 || slashIdx != -1 || queryIdx != -1 || fragmentIdx != -1) {
                if (colonIdx == 0 || base == null && fragmentIdx != 0) {
                    throw new MalformedURIException("No scheme found in URI.");
                }
            } else {
                this.initializeScheme(uriSpec);
                index = this.scheme_.length() + 1;
                if (colonIdx == uriSpecLen - 1 || uriSpec.charAt(colonIdx + 1) == '#') {
                    throw new MalformedURIException("Scheme specific part cannot be empty.");
                }
            }
        } else if (base == null && uriSpec.indexOf(35) != 0) {
            throw new MalformedURIException("No scheme found in URI.");
        }
        if (index + 1 < uriSpecLen && uriSpec.charAt(index) == '/' && uriSpec.charAt(index + 1) == '/') {
            char testChar;
            int startPos = index += 2;
            while (index < uriSpecLen && (testChar = uriSpec.charAt(index)) != '/' && testChar != '?' && testChar != '#') {
                ++index;
            }
            if (index > startPos) {
                if (!this.initializeAuthority(uriSpec.substring(startPos, index))) {
                    index = startPos - 2;
                }
            } else {
                this.host_ = "";
            }
        }
        this.initializePath(uriSpec, index);
        if (base != null) {
            this.absolutize(base);
        }
    }

    public void absolutize(URI base) {
        int index;
        if (this.path_.length() == 0 && this.scheme_ == null && this.host_ == null && this.regAuthority_ == null) {
            this.scheme_ = base.getScheme();
            this.userinfo_ = base.getUserinfo();
            this.host_ = base.getHost();
            this.port_ = base.getPort();
            this.regAuthority_ = base.getRegBasedAuthority();
            this.path_ = base.getPath();
            if (this.queryString_ == null) {
                this.queryString_ = base.getQueryString();
                if (this.fragment_ == null) {
                    this.fragment_ = base.getFragment();
                }
            }
            return;
        }
        if (this.scheme_ != null) {
            return;
        }
        this.scheme_ = base.getScheme();
        if (this.host_ != null || this.regAuthority_ != null) {
            return;
        }
        this.userinfo_ = base.getUserinfo();
        this.host_ = base.getHost();
        this.port_ = base.getPort();
        this.regAuthority_ = base.getRegBasedAuthority();
        if (this.path_.length() > 0 && this.path_.startsWith("/")) {
            return;
        }
        String path = "";
        String basePath = base.getPath();
        if (basePath != null && basePath.length() > 0) {
            int lastSlash = basePath.lastIndexOf(47);
            if (lastSlash != -1) {
                path = basePath.substring(0, lastSlash + 1);
            }
        } else if (this.path_.length() > 0) {
            path = "/";
        }
        path = path.concat(this.path_);
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
                if (!"..".equals(tempString.substring(segIndex))) {
                    path = path.substring(0, segIndex + 1).concat(path.substring(index + 4));
                    index = segIndex;
                    continue;
                }
                index += 4;
                continue;
            }
            index += 4;
        }
        if (path.endsWith("/..") && (segIndex = (tempString = path.substring(0, path.length() - 3)).lastIndexOf(47)) != -1) {
            path = path.substring(0, segIndex + 1);
        }
        this.path_ = path;
    }

    private void initializeScheme(String uriSpec) throws MalformedURIException {
        char testChar;
        int index;
        int uriSpecLen = uriSpec.length();
        String scheme = null;
        for (index = 0; index < uriSpecLen && (testChar = uriSpec.charAt(index)) != ':' && testChar != '/' && testChar != '?' && testChar != '#'; ++index) {
        }
        scheme = uriSpec.substring(0, index);
        if (scheme.length() == 0) {
            throw new MalformedURIException("No scheme found in URI.");
        }
        this.setScheme(scheme);
    }

    private boolean initializeAuthority(String uriSpec) {
        int index;
        int start = 0;
        int end = uriSpec.length();
        String userinfo = null;
        if (uriSpec.indexOf(64, start) != -1) {
            char testChar;
            for (index = 0; index < end && (testChar = uriSpec.charAt(index)) != '@'; ++index) {
            }
            userinfo = uriSpec.substring(start, index);
            ++index;
        }
        String host = null;
        start = index;
        boolean hasPort = false;
        if (index < end) {
            if (uriSpec.charAt(start) == '[') {
                int bracketIndex = uriSpec.indexOf(93, start);
                int n = index = bracketIndex != -1 ? bracketIndex : end;
                if (index + 1 < end && uriSpec.charAt(index + 1) == ':') {
                    ++index;
                    hasPort = true;
                } else {
                    index = end;
                }
            } else {
                int colonIndex = uriSpec.lastIndexOf(58, end);
                index = colonIndex > start ? colonIndex : end;
                hasPort = index != end;
            }
        }
        host = uriSpec.substring(start, index);
        int port = -1;
        if (host.length() > 0 && hasPort) {
            start = ++index;
            while (index < end) {
                ++index;
            }
            String portStr = uriSpec.substring(start, index);
            if (portStr.length() > 0) {
                try {
                    port = Integer.parseInt(portStr);
                    if (port == -1) {
                        --port;
                    }
                }
                catch (NumberFormatException nfe) {
                    port = -2;
                }
            }
        }
        if (this.isValidServerBasedAuthority(host, port, userinfo)) {
            this.host_ = host;
            this.port_ = port;
            this.userinfo_ = userinfo;
            return true;
        }
        if (this.isValidRegistryBasedAuthority(uriSpec)) {
            this.regAuthority_ = uriSpec;
            return true;
        }
        return false;
    }

    private boolean isValidServerBasedAuthority(String host, int port, String userinfo) {
        if (!URI.isWellFormedAddress(host) || port < -1 || port > 65535) {
            return false;
        }
        if (userinfo != null) {
            int end = userinfo.length();
            for (int index = 0; index < end; ++index) {
                char testChar = userinfo.charAt(index);
                if (testChar == '%') {
                    if (index + 2 >= end || !URI.isHex(userinfo.charAt(index + 1)) || !URI.isHex(userinfo.charAt(index + 2))) {
                        return false;
                    }
                    index += 2;
                    continue;
                }
                if (URI.isUserinfoCharacter(testChar)) continue;
                return false;
            }
        }
        return true;
    }

    private boolean isValidRegistryBasedAuthority(String authority) {
        int end = authority.length();
        for (int index = 0; index < end; ++index) {
            char testChar = authority.charAt(index);
            if (testChar == '%') {
                if (index + 2 >= end || !URI.isHex(authority.charAt(index + 1)) || !URI.isHex(authority.charAt(index + 2))) {
                    return false;
                }
                index += 2;
                continue;
            }
            if (URI.isPathCharacter(testChar)) continue;
            return false;
        }
        return true;
    }

    private void initializePath(String uriSpec, int nStartIndex) throws MalformedURIException {
        int index;
        if (uriSpec == null) {
            throw new MalformedURIException("Cannot initialize path from null string!");
        }
        int start = nStartIndex;
        int end = uriSpec.length();
        char testChar = '\u0000';
        if (start < end) {
            if (this.getScheme() == null || uriSpec.charAt(start) == '/') {
                for (index = nStartIndex; index < end; ++index) {
                    testChar = uriSpec.charAt(index);
                    if (testChar == '%') {
                        if (index + 2 >= end || !URI.isHex(uriSpec.charAt(index + 1)) || !URI.isHex(uriSpec.charAt(index + 2))) {
                            throw new MalformedURIException("Path contains invalid escape sequence!");
                        }
                        index += 2;
                        continue;
                    }
                    if (URI.isPathCharacter(testChar)) continue;
                    if (testChar != '?' && testChar != '#') {
                        throw new MalformedURIException("Path contains invalid character: " + testChar);
                    }
                    break;
                }
            } else {
                while (index < end && (testChar = (char)uriSpec.charAt(index)) != '?' && testChar != '#') {
                    if (testChar == '%') {
                        if (index + 2 >= end || !URI.isHex(uriSpec.charAt(index + 1)) || !URI.isHex(uriSpec.charAt(index + 2))) {
                            throw new MalformedURIException("Opaque part contains invalid escape sequence!");
                        }
                        index += 2;
                    } else if (!URI.isURICharacter(testChar)) {
                        throw new MalformedURIException("Opaque part contains invalid character: " + testChar);
                    }
                    ++index;
                }
            }
        }
        this.path_ = uriSpec.substring(start, index);
        if (testChar == '?') {
            start = ++index;
            while (index < end && (testChar = uriSpec.charAt(index)) != '#') {
                if (testChar == '%') {
                    if (index + 2 >= end || !URI.isHex(uriSpec.charAt(index + 1)) || !URI.isHex(uriSpec.charAt(index + 2))) {
                        throw new MalformedURIException("Query string contains invalid escape sequence!");
                    }
                    index += 2;
                } else if (!URI.isURICharacter(testChar)) {
                    throw new MalformedURIException("Query string contains invalid character: " + testChar);
                }
                ++index;
            }
            this.queryString_ = uriSpec.substring(start, index);
        }
        if (testChar == '#') {
            start = ++index;
            while (index < end) {
                testChar = uriSpec.charAt(index);
                if (testChar == '%') {
                    if (index + 2 >= end || !URI.isHex(uriSpec.charAt(index + 1)) || !URI.isHex(uriSpec.charAt(index + 2))) {
                        throw new MalformedURIException("Fragment contains invalid escape sequence!");
                    }
                    index += 2;
                } else if (!URI.isURICharacter(testChar)) {
                    throw new MalformedURIException("Fragment contains invalid character: " + testChar);
                }
                ++index;
            }
            this.fragment_ = uriSpec.substring(start, index);
        }
    }

    public String getScheme() {
        return this.scheme_;
    }

    public String getSchemeSpecificPart() {
        StringBuilder schemespec = new StringBuilder();
        if (this.host_ != null || this.regAuthority_ != null) {
            schemespec.append("//");
            if (this.host_ != null) {
                if (this.userinfo_ != null) {
                    schemespec.append(this.userinfo_);
                    schemespec.append('@');
                }
                schemespec.append(this.host_);
                if (this.port_ != -1) {
                    schemespec.append(':');
                    schemespec.append(this.port_);
                }
            } else {
                schemespec.append(this.regAuthority_);
            }
        }
        if (this.path_ != null) {
            schemespec.append(this.path_);
        }
        if (this.queryString_ != null) {
            schemespec.append('?');
            schemespec.append(this.queryString_);
        }
        if (this.fragment_ != null) {
            schemespec.append('#');
            schemespec.append(this.fragment_);
        }
        return schemespec.toString();
    }

    public String getUserinfo() {
        return this.userinfo_;
    }

    public String getHost() {
        return this.host_;
    }

    public int getPort() {
        return this.port_;
    }

    public String getRegBasedAuthority() {
        return this.regAuthority_;
    }

    public String getPath() {
        return this.path_;
    }

    public String getQueryString() {
        return this.queryString_;
    }

    public String getFragment() {
        return this.fragment_;
    }

    public void setScheme(String scheme) throws MalformedURIException {
        if (scheme == null) {
            throw new MalformedURIException("Cannot set scheme from null string!");
        }
        if (!URI.isConformantSchemeName(scheme)) {
            throw new MalformedURIException("The scheme is not conformant.");
        }
        this.scheme_ = scheme.toLowerCase(Locale.ROOT);
    }

    public void setUserinfo(String userinfo) throws MalformedURIException {
        if (userinfo == null) {
            this.userinfo_ = null;
            return;
        }
        if (this.host_ == null) {
            throw new MalformedURIException("Userinfo cannot be set when host is null!");
        }
        int end = userinfo.length();
        for (int index = 0; index < end; ++index) {
            char testChar = userinfo.charAt(index);
            if (testChar == '%') {
                if (index + 2 < end && URI.isHex(userinfo.charAt(index + 1)) && URI.isHex(userinfo.charAt(index + 2))) continue;
                throw new MalformedURIException("Userinfo contains invalid escape sequence!");
            }
            if (URI.isUserinfoCharacter(testChar)) continue;
            throw new MalformedURIException("Userinfo contains invalid character:" + testChar);
        }
        this.userinfo_ = userinfo;
    }

    public void setHost(String host) throws MalformedURIException {
        if (host == null || host.length() == 0) {
            if (host != null) {
                this.regAuthority_ = null;
            }
            this.host_ = host;
            this.userinfo_ = null;
            this.port_ = -1;
            return;
        }
        if (!URI.isWellFormedAddress(host)) {
            throw new MalformedURIException("Host is not a well formed address!");
        }
        this.host_ = host;
        this.regAuthority_ = null;
    }

    public void setPort(int port) throws MalformedURIException {
        if (port >= 0 && port <= 65535) {
            if (this.host_ == null) {
                throw new MalformedURIException("Port cannot be set when host is null!");
            }
        } else if (port != -1) {
            throw new MalformedURIException("Invalid port number!");
        }
        this.port_ = port;
    }

    public void setPath(String path) throws MalformedURIException {
        if (path == null) {
            this.path_ = null;
            this.queryString_ = null;
            this.fragment_ = null;
        } else {
            this.initializePath(path, 0);
        }
    }

    public void setQueryString(String queryString) throws MalformedURIException {
        if (queryString == null) {
            this.queryString_ = null;
        } else {
            if (!this.isGenericURI()) {
                throw new MalformedURIException("Query string can only be set for a generic URI!");
            }
            if (this.getPath() == null) {
                throw new MalformedURIException("Query string cannot be set when path is null!");
            }
            if (!URI.isURIString(queryString)) {
                throw new MalformedURIException("Query string contains invalid character!");
            }
            this.queryString_ = queryString;
        }
    }

    public void setFragment(String fragment) throws MalformedURIException {
        if (fragment == null) {
            this.fragment_ = null;
        } else {
            if (!this.isGenericURI()) {
                throw new MalformedURIException("Fragment can only be set for a generic URI!");
            }
            if (this.getPath() == null) {
                throw new MalformedURIException("Fragment cannot be set when path is null!");
            }
            if (!URI.isURIString(fragment)) {
                throw new MalformedURIException("Fragment contains invalid character!");
            }
            this.fragment_ = fragment;
        }
    }

    public boolean equals(Object test) {
        if (test instanceof URI) {
            URI testURI = (URI)test;
            if ((this.scheme_ == null && testURI.scheme_ == null || this.scheme_ != null && testURI.scheme_ != null && this.scheme_.equals(testURI.scheme_)) && (this.userinfo_ == null && testURI.userinfo_ == null || this.userinfo_ != null && testURI.userinfo_ != null && this.userinfo_.equals(testURI.userinfo_)) && (this.host_ == null && testURI.host_ == null || this.host_ != null && testURI.host_ != null && this.host_.equals(testURI.host_)) && this.port_ == testURI.port_ && (this.path_ == null && testURI.path_ == null || this.path_ != null && testURI.path_ != null && this.path_.equals(testURI.path_)) && (this.queryString_ == null && testURI.queryString_ == null || this.queryString_ != null && testURI.queryString_ != null && this.queryString_.equals(testURI.queryString_)) && (this.fragment_ == null && testURI.fragment_ == null || this.fragment_ != null && testURI.fragment_ != null && this.fragment_.equals(testURI.fragment_))) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder uriSpecString = new StringBuilder();
        if (this.scheme_ != null) {
            uriSpecString.append(this.scheme_);
            uriSpecString.append(':');
        }
        uriSpecString.append(this.getSchemeSpecificPart());
        return uriSpecString.toString();
    }

    public boolean isGenericURI() {
        return this.host_ != null;
    }

    public boolean isAbsoluteURI() {
        return this.scheme_ != null;
    }

    private static boolean isConformantSchemeName(String scheme) {
        if (scheme == null || scheme.trim().length() == 0 || !URI.isAlpha(scheme.charAt(0))) {
            return false;
        }
        int schemeLength = scheme.length();
        for (int i = 1; i < schemeLength; ++i) {
            char testChar = scheme.charAt(i);
            if (URI.isSchemeCharacter(testChar)) continue;
            return false;
        }
        return true;
    }

    public static boolean isWellFormedAddress(String address) {
        if (address == null) {
            return false;
        }
        int addrLength = address.length();
        if (addrLength == 0) {
            return false;
        }
        if (address.startsWith("[")) {
            return URI.isWellFormedIPv6Reference(address);
        }
        if (address.startsWith(".") || address.startsWith("-") || address.endsWith("-")) {
            return false;
        }
        int index = address.lastIndexOf(46);
        if (address.endsWith(".")) {
            index = address.substring(0, index).lastIndexOf(46);
        }
        if (index + 1 < addrLength && URI.isDigit(address.charAt(index + 1))) {
            return URI.isWellFormedIPv4Address(address);
        }
        if (addrLength > 255) {
            return false;
        }
        int labelCharCount = 0;
        for (int i = 0; i < addrLength; ++i) {
            char testChar = address.charAt(i);
            if (testChar == '.') {
                if (!URI.isAlphanum(address.charAt(i - 1)) || i + 1 < addrLength && !URI.isAlphanum(address.charAt(i + 1))) {
                    return false;
                }
                labelCharCount = 0;
                continue;
            }
            if (!URI.isAlphanum(testChar) && testChar != '-') {
                return false;
            }
            if (++labelCharCount <= 63) continue;
            return false;
        }
        return true;
    }

    public static boolean isWellFormedIPv4Address(String address) {
        int addrLength = address.length();
        int numDots = 0;
        int numDigits = 0;
        for (int i = 0; i < addrLength; ++i) {
            char testChar = address.charAt(i);
            if (testChar == '.') {
                if (i > 0 && !URI.isDigit(address.charAt(i - 1)) || i + 1 < addrLength && !URI.isDigit(address.charAt(i + 1))) {
                    return false;
                }
                numDigits = 0;
                if (++numDots <= 3) continue;
                return false;
            }
            if (!URI.isDigit(testChar)) {
                return false;
            }
            if (++numDigits > 3) {
                return false;
            }
            if (numDigits != 3) continue;
            char first = address.charAt(i - 2);
            char second = address.charAt(i - 1);
            if (first < '2' || first == '2' && (second < '5' || second == '5' && testChar <= '5')) continue;
            return false;
        }
        return numDots == 3;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean isWellFormedIPv6Reference(String address) {
        int addrLength = address.length();
        int index = 1;
        int end = addrLength - 1;
        if (addrLength <= 2) return false;
        if (address.charAt(0) != '[') return false;
        if (address.charAt(end) != ']') {
            return false;
        }
        int[] counter = new int[1];
        if ((index = URI.scanHexSequence(address, index, end, counter)) == -1) {
            return false;
        }
        if (index == end) {
            if (counter[0] != 8) return false;
            return true;
        }
        if (index + 1 >= end) return false;
        if (address.charAt(index) != ':') return false;
        if (address.charAt(index + 1) == ':') {
            counter[0] = counter[0] + 1;
            if (counter[0] > 8) {
                return false;
            }
            if ((index += 2) == end) {
                return true;
            }
        } else {
            if (counter[0] != 6) return false;
            if (!URI.isWellFormedIPv4Address(address.substring(index + 1, end))) return false;
            return true;
        }
        int prevCount = counter[0];
        if ((index = URI.scanHexSequence(address, index, end, counter)) == end) return true;
        if (index == -1) return false;
        if (!URI.isWellFormedIPv4Address(address.substring(counter[0] > prevCount ? index + 1 : index, end))) return false;
        return true;
    }

    private static int scanHexSequence(String address, int index, int end, int[] counter) {
        int numDigits = 0;
        int start = index;
        while (index < end) {
            char testChar = address.charAt(index);
            if (testChar == ':') {
                if (numDigits > 0 && (counter[0] = counter[0] + 1) > 8) {
                    return -1;
                }
                if (numDigits == 0 || index + 1 < end && address.charAt(index + 1) == ':') {
                    return index;
                }
                numDigits = 0;
            } else {
                if (!URI.isHex(testChar)) {
                    if (testChar == '.' && numDigits < 4 && numDigits > 0 && counter[0] <= 6) {
                        int back = index - numDigits - 1;
                        return back >= start ? back : back + 1;
                    }
                    return -1;
                }
                if (++numDigits > 4) {
                    return -1;
                }
            }
            ++index;
        }
        return numDigits > 0 && (counter[0] = counter[0] + 1) <= 8 ? end : -1;
    }

    private static boolean isDigit(char chr) {
        return chr >= '0' && chr <= '9';
    }

    private static boolean isHex(char ch) {
        return ch <= 'f' && (fgLookupTable[ch] & 0x40) != 0;
    }

    private static boolean isAlpha(char ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
    }

    private static boolean isAlphanum(char ch) {
        return ch <= 'z' && (fgLookupTable[ch] & 0x30) != 0;
    }

    private static boolean isURICharacter(char ch) {
        return ch <= '~' && (fgLookupTable[ch] & 0x33) != 0;
    }

    private static boolean isSchemeCharacter(char ch) {
        return ch <= 'z' && (fgLookupTable[ch] & 0x34) != 0;
    }

    private static boolean isUserinfoCharacter(char ch) {
        return ch <= 'z' && (fgLookupTable[ch] & 0x3A) != 0;
    }

    private static boolean isPathCharacter(char ch) {
        return ch <= '~' && (fgLookupTable[ch] & 0xB2) != 0;
    }

    private static boolean isURIString(String uric) {
        if (uric == null) {
            return false;
        }
        int end = uric.length();
        for (int i = 0; i < end; ++i) {
            char testChar = uric.charAt(i);
            if (testChar == '%') {
                if (i + 2 >= end || !URI.isHex(uric.charAt(i + 1)) || !URI.isHex(uric.charAt(i + 2))) {
                    return false;
                }
                i += 2;
                continue;
            }
            if (URI.isURICharacter(testChar)) continue;
            return false;
        }
        return true;
    }

    static {
        int i = 48;
        while (i <= 57) {
            int n = i++;
            fgLookupTable[n] = (byte)(fgLookupTable[n] | 0x60);
        }
        for (i = 65; i <= 70; ++i) {
            int n = i;
            fgLookupTable[n] = (byte)(fgLookupTable[n] | 0x50);
            int n2 = i + 32;
            fgLookupTable[n2] = (byte)(fgLookupTable[n2] | 0x50);
        }
        for (i = 71; i <= 90; ++i) {
            int n = i;
            fgLookupTable[n] = (byte)(fgLookupTable[n] | 0x10);
            int n3 = i + 32;
            fgLookupTable[n3] = (byte)(fgLookupTable[n3] | 0x10);
        }
        fgLookupTable[59] = (byte)(fgLookupTable[59] | 1);
        fgLookupTable[47] = (byte)(fgLookupTable[47] | 1);
        fgLookupTable[63] = (byte)(fgLookupTable[63] | 1);
        fgLookupTable[58] = (byte)(fgLookupTable[58] | 1);
        fgLookupTable[64] = (byte)(fgLookupTable[64] | 1);
        fgLookupTable[38] = (byte)(fgLookupTable[38] | 1);
        fgLookupTable[61] = (byte)(fgLookupTable[61] | 1);
        fgLookupTable[43] = (byte)(fgLookupTable[43] | 1);
        fgLookupTable[36] = (byte)(fgLookupTable[36] | 1);
        fgLookupTable[44] = (byte)(fgLookupTable[44] | 1);
        fgLookupTable[91] = (byte)(fgLookupTable[91] | 1);
        fgLookupTable[93] = (byte)(fgLookupTable[93] | 1);
        fgLookupTable[45] = (byte)(fgLookupTable[45] | 2);
        fgLookupTable[95] = (byte)(fgLookupTable[95] | 2);
        fgLookupTable[46] = (byte)(fgLookupTable[46] | 2);
        fgLookupTable[33] = (byte)(fgLookupTable[33] | 2);
        fgLookupTable[126] = (byte)(fgLookupTable[126] | 2);
        fgLookupTable[42] = (byte)(fgLookupTable[42] | 2);
        fgLookupTable[39] = (byte)(fgLookupTable[39] | 2);
        fgLookupTable[40] = (byte)(fgLookupTable[40] | 2);
        fgLookupTable[41] = (byte)(fgLookupTable[41] | 2);
        fgLookupTable[43] = (byte)(fgLookupTable[43] | 4);
        fgLookupTable[45] = (byte)(fgLookupTable[45] | 4);
        fgLookupTable[46] = (byte)(fgLookupTable[46] | 4);
        fgLookupTable[59] = (byte)(fgLookupTable[59] | 8);
        fgLookupTable[58] = (byte)(fgLookupTable[58] | 8);
        fgLookupTable[38] = (byte)(fgLookupTable[38] | 8);
        fgLookupTable[61] = (byte)(fgLookupTable[61] | 8);
        fgLookupTable[43] = (byte)(fgLookupTable[43] | 8);
        fgLookupTable[36] = (byte)(fgLookupTable[36] | 8);
        fgLookupTable[44] = (byte)(fgLookupTable[44] | 8);
        fgLookupTable[59] = (byte)(fgLookupTable[59] | 0x80);
        fgLookupTable[47] = (byte)(fgLookupTable[47] | 0x80);
        fgLookupTable[58] = (byte)(fgLookupTable[58] | 0x80);
        fgLookupTable[64] = (byte)(fgLookupTable[64] | 0x80);
        fgLookupTable[38] = (byte)(fgLookupTable[38] | 0x80);
        fgLookupTable[61] = (byte)(fgLookupTable[61] | 0x80);
        fgLookupTable[43] = (byte)(fgLookupTable[43] | 0x80);
        fgLookupTable[36] = (byte)(fgLookupTable[36] | 0x80);
        fgLookupTable[44] = (byte)(fgLookupTable[44] | 0x80);
    }

    public static class MalformedURIException
    extends IOException {
        private static final long serialVersionUID = -8343545858797571098L;

        public MalformedURIException(String msg) {
            super(msg);
        }
    }
}

