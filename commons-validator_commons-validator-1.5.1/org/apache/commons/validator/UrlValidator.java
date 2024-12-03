/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.util.Flags;

@Deprecated
public class UrlValidator
implements Serializable {
    private static final long serialVersionUID = 24137157400029593L;
    public static final int ALLOW_ALL_SCHEMES = 1;
    public static final int ALLOW_2_SLASHES = 2;
    public static final int NO_FRAGMENTS = 4;
    private static final String ALPHA_CHARS = "a-zA-Z";
    private static final String SPECIAL_CHARS = ";/@&=,.?:+$";
    private static final String VALID_CHARS = "[^\\s;/@&=,.?:+$]";
    private static final String AUTHORITY_CHARS_REGEX = "\\p{Alnum}\\-\\.";
    private static final String ATOM = "[^\\s;/@&=,.?:+$]+";
    private static final String URL_REGEX = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?";
    private static final Pattern URL_PATTERN = Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
    private static final int PARSE_URL_SCHEME = 2;
    private static final int PARSE_URL_AUTHORITY = 4;
    private static final int PARSE_URL_PATH = 5;
    private static final int PARSE_URL_QUERY = 7;
    private static final int PARSE_URL_FRAGMENT = 9;
    private static final Pattern SCHEME_PATTERN = Pattern.compile("^\\p{Alpha}[\\p{Alnum}\\+\\-\\.]*");
    private static final String AUTHORITY_REGEX = "^([\\p{Alnum}\\-\\.]*)(:\\d*)?(.*)?";
    private static final Pattern AUTHORITY_PATTERN = Pattern.compile("^([\\p{Alnum}\\-\\.]*)(:\\d*)?(.*)?");
    private static final int PARSE_AUTHORITY_HOST_IP = 1;
    private static final int PARSE_AUTHORITY_PORT = 2;
    private static final int PARSE_AUTHORITY_EXTRA = 3;
    private static final Pattern PATH_PATTERN = Pattern.compile("^(/[-\\w:@&?=+,.!/~*'%$_;]*)?$");
    private static final Pattern QUERY_PATTERN = Pattern.compile("^(.*)$");
    private static final Pattern LEGAL_ASCII_PATTERN = Pattern.compile("^\\p{ASCII}+$");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^[^\\s;/@&=,.?:+$]+(\\.[^\\s;/@&=,.?:+$]+)*$");
    private static final Pattern PORT_PATTERN = Pattern.compile("^:(\\d{1,5})$");
    private static final Pattern ATOM_PATTERN = Pattern.compile("^([^\\s;/@&=,.?:+$]+).*?$");
    private static final Pattern ALPHA_PATTERN = Pattern.compile("^[a-zA-Z]");
    private final Flags options;
    private final Set<String> allowedSchemes = new HashSet<String>();
    protected String[] defaultSchemes = new String[]{"http", "https", "ftp"};

    public UrlValidator() {
        this(null);
    }

    public UrlValidator(String[] schemes) {
        this(schemes, 0);
    }

    public UrlValidator(int options) {
        this(null, options);
    }

    public UrlValidator(String[] schemes, int options) {
        this.options = new Flags(options);
        if (this.options.isOn(1L)) {
            return;
        }
        if (schemes == null) {
            schemes = this.defaultSchemes;
        }
        this.allowedSchemes.addAll(Arrays.asList(schemes));
    }

    public boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        if (!LEGAL_ASCII_PATTERN.matcher(value).matches()) {
            return false;
        }
        Matcher urlMatcher = URL_PATTERN.matcher(value);
        if (!urlMatcher.matches()) {
            return false;
        }
        if (!this.isValidScheme(urlMatcher.group(2))) {
            return false;
        }
        if (!this.isValidAuthority(urlMatcher.group(4))) {
            return false;
        }
        if (!this.isValidPath(urlMatcher.group(5))) {
            return false;
        }
        if (!this.isValidQuery(urlMatcher.group(7))) {
            return false;
        }
        return this.isValidFragment(urlMatcher.group(9));
    }

    protected boolean isValidScheme(String scheme) {
        if (scheme == null) {
            return false;
        }
        if (!SCHEME_PATTERN.matcher(scheme).matches()) {
            return false;
        }
        return !this.options.isOff(1L) || this.allowedSchemes.contains(scheme);
    }

    protected boolean isValidAuthority(String authority) {
        if (authority == null) {
            return false;
        }
        InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();
        Matcher authorityMatcher = AUTHORITY_PATTERN.matcher(authority);
        if (!authorityMatcher.matches()) {
            return false;
        }
        boolean hostname = false;
        String hostIP = authorityMatcher.group(1);
        boolean ipV4Address = inetAddressValidator.isValid(hostIP);
        if (!ipV4Address) {
            hostname = DOMAIN_PATTERN.matcher(hostIP).matches();
        }
        if (hostname) {
            char[] chars = hostIP.toCharArray();
            int size = 1;
            for (int i = 0; i < chars.length; ++i) {
                if (chars[i] != '.') continue;
                ++size;
            }
            String[] domainSegment = new String[size];
            boolean match = true;
            int segmentCount = 0;
            int segmentLength = 0;
            while (match) {
                Matcher atomMatcher = ATOM_PATTERN.matcher(hostIP);
                match = atomMatcher.matches();
                if (!match) continue;
                domainSegment[segmentCount] = atomMatcher.group(1);
                segmentLength = domainSegment[segmentCount].length() + 1;
                hostIP = segmentLength >= hostIP.length() ? "" : hostIP.substring(segmentLength);
                ++segmentCount;
            }
            String topLevel = domainSegment[segmentCount - 1];
            if (topLevel.length() < 2 || topLevel.length() > 4) {
                return false;
            }
            if (!ALPHA_PATTERN.matcher(topLevel.substring(0, 1)).matches()) {
                return false;
            }
            if (segmentCount < 2) {
                return false;
            }
        }
        if (!hostname && !ipV4Address) {
            return false;
        }
        String port = authorityMatcher.group(2);
        if (port != null && !PORT_PATTERN.matcher(port).matches()) {
            return false;
        }
        String extra = authorityMatcher.group(3);
        return GenericValidator.isBlankOrNull(extra);
    }

    protected boolean isValidPath(String path) {
        if (path == null) {
            return false;
        }
        if (!PATH_PATTERN.matcher(path).matches()) {
            return false;
        }
        int slash2Count = this.countToken("//", path);
        if (this.options.isOff(2L) && slash2Count > 0) {
            return false;
        }
        int slashCount = this.countToken("/", path);
        int dot2Count = this.countToken("..", path);
        return dot2Count <= 0 || slashCount - slash2Count - 1 > dot2Count;
    }

    protected boolean isValidQuery(String query) {
        if (query == null) {
            return true;
        }
        return QUERY_PATTERN.matcher(query).matches();
    }

    protected boolean isValidFragment(String fragment) {
        if (fragment == null) {
            return true;
        }
        return this.options.isOff(4L);
    }

    protected int countToken(String token, String target) {
        int tokenIndex = 0;
        int count = 0;
        while (tokenIndex != -1) {
            if ((tokenIndex = target.indexOf(token, tokenIndex)) <= -1) continue;
            ++tokenIndex;
            ++count;
        }
        return count;
    }
}

