/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html.scan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.model.Attribute;
import org.owasp.validator.html.model.Tag;

public class Constants {
    public static final String DEFAULT_ENCODING_ALGORITHM = "UTF-8";
    public static final Tag BASIC_PARAM_TAG_RULE;
    public static final List<String> defaultAllowedEmptyTags;
    public static final List<String> defaultRequireClosingTags;
    private static final String[] allowedEmptyTags;
    private static final String[] requiresClosingTags;
    public static final String DEFAULT_LOCALE_LANG = "en";
    public static final String DEFAULT_LOCALE_LOC = "US";
    public static final String big5CharsToEncode = "<>\"'&";
    public static final Set<Integer> big5CharsToEncodeSet;

    static {
        allowedEmptyTags = new String[]{"br", "hr", "a", "img", "link", "iframe", "script", "object", "applet", "frame", "base", "param", "meta", "input", "textarea", "embed", "basefont", "col"};
        requiresClosingTags = new String[]{"iframe", "script", "link"};
        Attribute paramNameAttr = new Attribute("name", Arrays.asList(Policy.ANYTHING_REGEXP), Collections.emptyList(), null, null);
        Attribute paramValueAttr = new Attribute("value", Arrays.asList(Policy.ANYTHING_REGEXP), Collections.emptyList(), null, null);
        HashMap<String, Attribute> attrs = new HashMap<String, Attribute>();
        attrs.put(paramNameAttr.getName().toLowerCase(), paramNameAttr);
        attrs.put(paramValueAttr.getName().toLowerCase(), paramValueAttr);
        BASIC_PARAM_TAG_RULE = new Tag("param", attrs, "validate");
        ArrayList<String> allowedEmptyTagsList = new ArrayList<String>(Arrays.asList(allowedEmptyTags));
        defaultAllowedEmptyTags = Collections.unmodifiableList(allowedEmptyTagsList);
        ArrayList<String> requiresClosingTagsList = new ArrayList<String>(Arrays.asList(requiresClosingTags));
        defaultRequireClosingTags = Collections.unmodifiableList(requiresClosingTagsList);
        big5CharsToEncodeSet = new HashSet<Integer>(){
            {
                for (int i = 0; i < Constants.big5CharsToEncode.length(); ++i) {
                    this.add(Integer.valueOf(Constants.big5CharsToEncode.charAt(i)));
                }
            }
        };
    }
}

