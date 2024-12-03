/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ElementIdCreator;
import com.google.common.base.CharMatcher;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class HtmlElementIdCreator
implements ElementIdCreator {
    private static final int MAX_LENGTH = 255;
    private static final String BLANK_ID = "blank";
    private static final String AUTO_GEN_BASIS = "aid";
    private static final Pattern VALID_FIRST_CHAR = Pattern.compile("[a-zA-Z]");
    private static final String ID_PREFIX = "id-";
    private HashMap<String, Integer> idCountMap = new HashMap();

    public HtmlElementIdCreator() {
        this.idCountMap.put(AUTO_GEN_BASIS, 0);
    }

    @Override
    public String generateId() {
        return this.generateId(AUTO_GEN_BASIS);
    }

    @Override
    public String generateId(String basis) {
        Object id = HtmlElementIdCreator.convertToIdHtml5(basis);
        if (!this.idCountMap.containsKey(id)) {
            this.idCountMap.put((String)id, 1);
        } else {
            int extension = this.idCountMap.get(id);
            this.idCountMap.put((String)id, extension + 1);
            id = (String)id + "." + extension;
        }
        return id;
    }

    public static String convertToIdHtml5(String original) {
        if (StringUtils.isBlank((CharSequence)original)) {
            return BLANK_ID;
        }
        Object str = HtmlElementIdCreator.removeWhitespace(original);
        if (StringUtils.isNotBlank((CharSequence)str)) {
            Matcher firstCharCheck = VALID_FIRST_CHAR.matcher((CharSequence)str);
            boolean validFirstChar = firstCharCheck.find();
            if (!validFirstChar || firstCharCheck.start() != 0) {
                str = ID_PREFIX + (String)str;
            }
            if (((String)str).length() > 255) {
                str = ((String)str).substring(0, 255);
            }
        } else {
            str = AUTO_GEN_BASIS;
        }
        return str;
    }

    private static String removeWhitespace(String original) {
        return CharMatcher.whitespace().removeFrom((CharSequence)original);
    }
}

