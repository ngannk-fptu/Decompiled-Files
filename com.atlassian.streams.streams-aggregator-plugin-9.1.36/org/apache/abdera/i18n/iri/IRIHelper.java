/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.iri;

import java.util.regex.Pattern;
import org.apache.abdera.i18n.iri.IRI;

public class IRIHelper {
    private static final Pattern javascript = Pattern.compile("\\s*j\\s*a\\s*v\\s*a\\s*s\\s*c\\s*r\\s*i\\s*p\\s*t\\s*:.*", 2);
    private static final Pattern mailto = Pattern.compile("\\s*m\\s*a\\s*i\\s*l\\s*t\\s*o\\s*:.*", 2);

    public static boolean isJavascriptUri(IRI uri) {
        if (uri == null) {
            return false;
        }
        return javascript.matcher(uri.toString()).matches();
    }

    public static boolean isMailtoUri(IRI uri) {
        if (uri == null) {
            return false;
        }
        return mailto.matcher(uri.toString()).matches();
    }
}

