/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package org.owasp.validator.html.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;
import java.util.StringTokenizer;

public class URIUtils {
    private static final String FILE_PROTOCOL_PREFIX = "file:///";
    private static final char HREF_PATH_SEP = '/';
    private static final String URL_PATH_SEP_STR = "/";
    private static final String CURRENT_DIR_OP = ".";
    private static final String PARENT_DIR_OP = "..";

    @SuppressFBWarnings(value={"SECURITY"}, justification="The 2x Path Traversal warnings related to the use of new File(href) are not vulnerabilities as no data is read or written.")
    public static String resolveAsString(String href, String documentBase) {
        try {
            new URL(href);
            return href;
        }
        catch (MalformedURLException malformedURLException) {
            int idx;
            String absolute = null;
            absolute = documentBase != null && documentBase.length() > 0 ? ((idx = documentBase.lastIndexOf(47)) == documentBase.length() - 1 ? documentBase + href : documentBase + '/' + href) : href;
            try {
                if (absolute.indexOf("./") >= 0) {
                    absolute = URIUtils.normalize(absolute);
                }
                new URL(absolute);
                return absolute;
            }
            catch (MalformedURLException muex) {
                int idx2 = absolute.indexOf(58);
                if (idx2 >= 0) {
                    String scheme = absolute.substring(0, idx2);
                    String error = "unknown protocol: " + scheme;
                    if (error.equals(muex.getMessage())) {
                        return absolute;
                    }
                }
                String fileURL = absolute;
                File iFile = new File(href);
                boolean exists = iFile.exists();
                fileURL = URIUtils.createFileURL(iFile.getAbsolutePath());
                if (!(iFile.isAbsolute() || !(iFile = new File(absolute)).exists() && exists)) {
                    fileURL = URIUtils.createFileURL(iFile.getAbsolutePath());
                }
                try {
                    new URL(fileURL);
                    return fileURL;
                }
                catch (MalformedURLException malformedURLException2) {
                    return absolute;
                }
            }
        }
    }

    public static String normalize(String absoluteURL) throws MalformedURLException {
        if (absoluteURL == null) {
            return absoluteURL;
        }
        if (absoluteURL.indexOf(46) < 0) {
            return absoluteURL;
        }
        Stack<String> tokens = new Stack<String>();
        StringTokenizer st = new StringTokenizer(absoluteURL, URL_PATH_SEP_STR, true);
        String last = null;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (URL_PATH_SEP_STR.equals(token)) {
                if (URL_PATH_SEP_STR.equals(last)) {
                    tokens.push("");
                }
            } else if (PARENT_DIR_OP.equals(token)) {
                if (tokens.empty()) {
                    throw new MalformedURLException("invalid absolute URL: " + absoluteURL);
                }
                tokens.pop();
            } else if (!CURRENT_DIR_OP.equals(token)) {
                tokens.push(token);
            }
            last = token;
        }
        StringBuffer buffer = new StringBuffer(absoluteURL.length());
        for (int i = 0; i < tokens.size(); ++i) {
            if (i > 0) {
                buffer.append('/');
            }
            buffer.append((String)tokens.elementAt(i));
        }
        return buffer.toString();
    }

    private static String createFileURL(String filename) {
        if (filename == null) {
            return FILE_PROTOCOL_PREFIX;
        }
        StringBuffer sb = new StringBuffer(filename.length() + FILE_PROTOCOL_PREFIX.length());
        sb.append(FILE_PROTOCOL_PREFIX);
        for (char ch : filename.toCharArray()) {
            if (ch == '\\') {
                sb.append('/');
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }
}

