/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.templates.variables;

import com.atlassian.confluence.pages.templates.variables.ListVariable;
import com.atlassian.confluence.pages.templates.variables.StringVariable;
import com.atlassian.confluence.pages.templates.variables.TextAreaVariable;
import com.atlassian.confluence.pages.templates.variables.Variable;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class VariableFactory {
    private static final Pattern TEXTAREA_VARIABLE = Pattern.compile("[^|]+\\|textarea\\((\\d*),(\\d*)\\)");
    private static final Pattern LIST_VARIABLE = Pattern.compile("[^|]+\\|list\\(([^\\)]+)\\)");

    public static Variable wikiNameToVariable(String wikiName) {
        Matcher listMatcher;
        if (StringUtils.isEmpty((CharSequence)wikiName)) {
            return null;
        }
        String name = StringUtils.substringBefore((String)wikiName, (String)"|");
        Matcher textAreaMatcher = TEXTAREA_VARIABLE.matcher(wikiName);
        if (textAreaMatcher.matches()) {
            try {
                int rows = Integer.parseInt(textAreaMatcher.group(1));
                int columns = Integer.parseInt(textAreaMatcher.group(2));
                return new TextAreaVariable(name, rows, columns);
            }
            catch (NumberFormatException rows) {
                // empty catch block
            }
        }
        if ((listMatcher = LIST_VARIABLE.matcher(wikiName)).matches()) {
            String listItems = listMatcher.group(1);
            String[] items = StringUtils.split((String)listItems, (char)',');
            return new ListVariable(name, Arrays.asList(items));
        }
        return new StringVariable(name);
    }
}

