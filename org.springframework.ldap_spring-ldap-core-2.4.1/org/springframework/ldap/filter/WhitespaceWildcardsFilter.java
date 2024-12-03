/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.springframework.ldap.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.support.LdapEncoder;
import org.springframework.util.StringUtils;

public class WhitespaceWildcardsFilter
extends EqualsFilter {
    private static Pattern starReplacePattern = Pattern.compile("\\s+");

    public WhitespaceWildcardsFilter(String attribute, String value) {
        super(attribute, value);
    }

    @Override
    protected String encodeValue(String value) {
        if (!StringUtils.hasText((String)value)) {
            return "*";
        }
        String filterEncoded = LdapEncoder.filterEncode(value.trim());
        Matcher m = starReplacePattern.matcher(filterEncoded);
        StringBuffer buff = new StringBuffer(value.length() + 2);
        buff.append('*');
        while (m.find()) {
            m.appendReplacement(buff, "*");
        }
        m.appendTail(buff);
        buff.append('*');
        return buff.toString();
    }
}

