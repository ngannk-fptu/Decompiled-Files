/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.RegexValidator;

public class UrlValidator
implements Serializable {
    private static final long serialVersionUID = 7557161713937335013L;
    public static final long ALLOW_ALL_SCHEMES = 1L;
    public static final long ALLOW_2_SLASHES = 2L;
    public static final long NO_FRAGMENTS = 4L;
    public static final long ALLOW_LOCAL_URLS = 8L;
    private static final String URL_REGEX = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?";
    private static final Pattern URL_PATTERN = Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
    private static final int PARSE_URL_SCHEME = 2;
    private static final int PARSE_URL_AUTHORITY = 4;
    private static final int PARSE_URL_PATH = 5;
    private static final int PARSE_URL_QUERY = 7;
    private static final int PARSE_URL_FRAGMENT = 9;
    private static final String SCHEME_REGEX = "^\\p{Alpha}[\\p{Alnum}\\+\\-\\.]*";
    private static final Pattern SCHEME_PATTERN = Pattern.compile("^\\p{Alpha}[\\p{Alnum}\\+\\-\\.]*");
    private static final String AUTHORITY_CHARS_REGEX = "\\p{Alnum}\\-\\.";
    private static final String IPV6_REGEX = "[0-9a-fA-F:]+";
    private static final String USERINFO_CHARS_REGEX = "[a-zA-Z0-9%-._~!$&'()*+,;=]";
    private static final String USERINFO_FIELD_REGEX = "[a-zA-Z0-9%-._~!$&'()*+,;=]+:[a-zA-Z0-9%-._~!$&'()*+,;=]*@";
    private static final String AUTHORITY_REGEX = "(?:\\[([0-9a-fA-F:]+)\\]|(?:(?:[a-zA-Z0-9%-._~!$&'()*+,;=]+:[a-zA-Z0-9%-._~!$&'()*+,;=]*@)?([\\p{Alnum}\\-\\.]*)))(:\\d*)?(.*)?";
    private static final Pattern AUTHORITY_PATTERN = Pattern.compile("(?:\\[([0-9a-fA-F:]+)\\]|(?:(?:[a-zA-Z0-9%-._~!$&'()*+,;=]+:[a-zA-Z0-9%-._~!$&'()*+,;=]*@)?([\\p{Alnum}\\-\\.]*)))(:\\d*)?(.*)?");
    private static final int PARSE_AUTHORITY_IPV6 = 1;
    private static final int PARSE_AUTHORITY_HOST_IP = 2;
    private static final int PARSE_AUTHORITY_EXTRA = 4;
    private static final String PATH_REGEX = "^(/[-\\w:@&?=+,.!/~*'%$_;\\(\\)]*)?$";
    private static final Pattern PATH_PATTERN = Pattern.compile("^(/[-\\w:@&?=+,.!/~*'%$_;\\(\\)]*)?$");
    private static final String QUERY_REGEX = "^(.*)$";
    private static final Pattern QUERY_PATTERN = Pattern.compile("^(.*)$");
    private final long options;
    private final Set<String> allowedSchemes;
    private final RegexValidator authorityValidator;
    private static final String[] DEFAULT_SCHEMES = new String[]{"http", "https", "ftp"};
    private static final UrlValidator DEFAULT_URL_VALIDATOR = new UrlValidator();

    public static UrlValidator getInstance() {
        return DEFAULT_URL_VALIDATOR;
    }

    public UrlValidator() {
        this(null);
    }

    public UrlValidator(String[] schemes) {
        this(schemes, 0L);
    }

    public UrlValidator(long options) {
        this(null, null, options);
    }

    public UrlValidator(String[] schemes, long options) {
        this(schemes, null, options);
    }

    public UrlValidator(RegexValidator authorityValidator, long options) {
        this(null, authorityValidator, options);
    }

    public UrlValidator(String[] schemes, RegexValidator authorityValidator, long options) {
        this.options = options;
        if (this.isOn(1L)) {
            this.allowedSchemes = Collections.emptySet();
        } else {
            if (schemes == null) {
                schemes = DEFAULT_SCHEMES;
            }
            this.allowedSchemes = new HashSet<String>(schemes.length);
            for (int i = 0; i < schemes.length; ++i) {
                this.allowedSchemes.add(schemes[i].toLowerCase(Locale.ENGLISH));
            }
        }
        this.authorityValidator = authorityValidator;
    }

    public boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        Matcher urlMatcher = URL_PATTERN.matcher(value);
        if (!urlMatcher.matches()) {
            return false;
        }
        String scheme = urlMatcher.group(2);
        if (!this.isValidScheme(scheme)) {
            return false;
        }
        String authority = urlMatcher.group(4);
        if ("file".equals(scheme) ? !"".equals(authority) && authority.contains(":") : !this.isValidAuthority(authority)) {
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
        return !this.isOff(1L) || this.allowedSchemes.contains(scheme.toLowerCase(Locale.ENGLISH));
    }

    protected boolean isValidAuthority(String authority) {
        String extra;
        if (authority == null) {
            return false;
        }
        if (this.authorityValidator != null && this.authorityValidator.isValid(authority)) {
            return true;
        }
        String authorityASCII = DomainValidator.unicodeToASCII(authority);
        Matcher authorityMatcher = AUTHORITY_PATTERN.matcher(authorityASCII);
        if (!authorityMatcher.matches()) {
            return false;
        }
        String ipv6 = authorityMatcher.group(1);
        if (ipv6 != null) {
            InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();
            if (!inetAddressValidator.isValidInet6Address(ipv6)) {
                return false;
            }
        } else {
            InetAddressValidator inetAddressValidator;
            String hostLocation = authorityMatcher.group(2);
            DomainValidator domainValidator = DomainValidator.getInstance(this.isOn(8L));
            if (!domainValidator.isValid(hostLocation) && !(inetAddressValidator = InetAddressValidator.getInstance()).isValidInet4Address(hostLocation)) {
                return false;
            }
        }
        return (extra = authorityMatcher.group(4)) == null || extra.trim().length() <= 0;
    }

    protected boolean isValidPath(String path) {
        if (path == null) {
            return false;
        }
        if (!PATH_PATTERN.matcher(path).matches()) {
            return false;
        }
        try {
            URI uri = new URI(null, null, path, null);
            String norm = uri.normalize().getPath();
            if (norm.startsWith("/../") || norm.equals("/..")) {
                return false;
            }
        }
        catch (URISyntaxException e) {
            return false;
        }
        int slash2Count = this.countToken("//", path);
        return !this.isOff(2L) || slash2Count <= 0;
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
        return this.isOff(4L);
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

    private boolean isOn(long flag) {
        return (this.options & flag) > 0L;
    }

    private boolean isOff(long flag) {
        return (this.options & flag) == 0L;
    }

    Matcher matchURL(String value) {
        return URL_PATTERN.matcher(value);
    }
}

