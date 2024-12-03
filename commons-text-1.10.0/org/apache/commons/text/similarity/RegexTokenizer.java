/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 */
package org.apache.commons.text.similarity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.similarity.Tokenizer;

final class RegexTokenizer
implements Tokenizer<CharSequence> {
    private static final Pattern PATTERN = Pattern.compile("(\\w)+");

    RegexTokenizer() {
    }

    public CharSequence[] tokenize(CharSequence text) {
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)text), (String)"Invalid text", (Object[])new Object[0]);
        Matcher matcher = PATTERN.matcher(text);
        ArrayList<String> tokens = new ArrayList<String>();
        while (matcher.find()) {
            tokens.add(matcher.group(0));
        }
        return tokens.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }
}

