/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.owasp.validator.html.model.Tag;

public class Attribute {
    private final String name;
    private final String description;
    private final String onInvalid;
    private final List<String> allowedValues;
    private final Pattern[] allowedRegExps;
    private final Set<String> allowedValuesLower;

    public Attribute(String name, List<Pattern> allowedRegexps, List<String> allowedValues, String onInvalidStr, String description) {
        this.name = name;
        this.allowedRegExps = allowedRegexps.toArray(new Pattern[allowedRegexps.size()]);
        this.allowedValues = Collections.unmodifiableList(allowedValues);
        HashSet<String> allowedValuesLower = new HashSet<String>();
        for (String allowedValue : allowedValues) {
            allowedValuesLower.add(allowedValue.toLowerCase());
        }
        this.allowedValuesLower = allowedValuesLower;
        this.onInvalid = onInvalidStr;
        this.description = description;
    }

    public boolean matchesAllowedExpression(String value) {
        String input = value.toLowerCase();
        for (Pattern pattern : this.allowedRegExps) {
            if (pattern == null || !pattern.matcher(input).matches()) continue;
            return true;
        }
        return false;
    }

    public boolean containsAllowedValue(String valueInLowerCase) {
        return this.allowedValuesLower.contains(valueInLowerCase);
    }

    public String getName() {
        return this.name;
    }

    public String getOnInvalid() {
        return this.onInvalid;
    }

    public Attribute mutate(String onInvalid, String description) {
        return new Attribute(this.name, Arrays.asList(this.allowedRegExps), this.allowedValues, onInvalid != null && onInvalid.length() != 0 ? onInvalid : this.onInvalid, description != null && description.length() != 0 ? description : this.description);
    }

    public String matcherRegEx(boolean hasNext) {
        boolean hasRegExps;
        StringBuilder regExp = new StringBuilder();
        regExp.append(this.getName()).append("(\\s)*").append("=").append("(\\s)*").append("\"").append("(");
        boolean bl = hasRegExps = this.allowedRegExps.length > 0;
        if (this.allowedRegExps.length + this.allowedValues.size() > 0) {
            Iterator<String> allowedValues = this.allowedValues.iterator();
            while (allowedValues.hasNext()) {
                String allowedValue = allowedValues.next();
                regExp.append(Tag.escapeRegularExpressionCharacters(allowedValue));
                if (!allowedValues.hasNext() && !hasRegExps) continue;
                regExp.append("|");
            }
            Iterator<Pattern> allowedRegExps = Arrays.asList(this.allowedRegExps).iterator();
            while (allowedRegExps.hasNext()) {
                Pattern allowedRegExp = allowedRegExps.next();
                regExp.append(allowedRegExp.pattern());
                if (!allowedRegExps.hasNext()) continue;
                regExp.append("|");
            }
            if (this.allowedRegExps.length + this.allowedValues.size() > 0) {
                regExp.append(")");
            }
            regExp.append("\"(\\s)*");
            if (hasNext) {
                regExp.append("|");
            }
        }
        return regExp.toString();
    }

    public static String mergeRelValuesInAnchor(boolean addNofollow, boolean addNoopenerAndNoreferrer, String currentRelValue) {
        String newRelValue = "";
        if (currentRelValue == null || currentRelValue.isEmpty()) {
            if (addNofollow) {
                newRelValue = "nofollow";
            }
            if (addNoopenerAndNoreferrer) {
                newRelValue = newRelValue + " noopener noreferrer";
            }
        } else {
            ArrayList<String> relTokens = new ArrayList<String>();
            newRelValue = currentRelValue;
            for (String value : currentRelValue.split(" ")) {
                relTokens.add(value.toLowerCase());
            }
            if (addNofollow && !relTokens.contains("nofollow")) {
                newRelValue = newRelValue + " nofollow";
            }
            if (addNoopenerAndNoreferrer) {
                if (!relTokens.contains("noopener")) {
                    newRelValue = newRelValue + " noopener";
                }
                if (!relTokens.contains("noreferrer")) {
                    newRelValue = newRelValue + " noreferrer";
                }
            }
        }
        return newRelValue.trim();
    }
}

