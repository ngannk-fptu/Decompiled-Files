/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.PropertyUtils
 */
package org.apache.velocity.tools.generic;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.LocaleConfig;
import org.apache.velocity.tools.generic.ValueParser;

@DefaultKey(value="display")
public class DisplayTool
extends LocaleConfig {
    public static final String LIST_DELIM_KEY = "listDelim";
    public static final String LIST_FINAL_DELIM_KEY = "listFinalDelim";
    public static final String TRUNCATE_LENGTH_KEY = "truncateLength";
    public static final String TRUNCATE_SUFFIX_KEY = "truncateSuffix";
    public static final String TRUNCATE_AT_WORD_KEY = "truncateAtWord";
    public static final String CELL_LENGTH_KEY = "cellLength";
    public static final String CELL_SUFFIX_KEY = "cellSuffix";
    public static final String DEFAULT_ALTERNATE_KEY = "defaultAlternate";
    public static final String ALLOWED_TAGS_KEY = "allowedTags";
    private String defaultDelim = ", ";
    private String defaultFinalDelim = " and ";
    private int defaultTruncateLength = 30;
    private String defaultTruncateSuffix = "...";
    private boolean defaultTruncateAtWord = false;
    private int defaultCellLength = 30;
    private String defaultCellSuffix = "...";
    private String defaultAlternate = "null";
    private String[] defaultAllowedTags = null;

    @Override
    protected void configure(ValueParser values) {
        String[] allowedTags;
        String defaultAlternate;
        String cellSuffix;
        Integer cellLength;
        Boolean truncateAtWord;
        String truncateSuffix;
        Integer truncateLength;
        String listFinalDelim;
        super.configure(values);
        String listDelim = values.getString(LIST_DELIM_KEY);
        if (listDelim != null) {
            this.setListDelimiter(listDelim);
        }
        if ((listFinalDelim = values.getString(LIST_FINAL_DELIM_KEY)) != null) {
            this.setListFinalDelimiter(listFinalDelim);
        }
        if ((truncateLength = values.getInteger(TRUNCATE_LENGTH_KEY)) != null) {
            this.setTruncateLength(truncateLength);
        }
        if ((truncateSuffix = values.getString(TRUNCATE_SUFFIX_KEY)) != null) {
            this.setTruncateSuffix(truncateSuffix);
        }
        if ((truncateAtWord = values.getBoolean(TRUNCATE_AT_WORD_KEY)) != null) {
            this.setTruncateAtWord(truncateAtWord);
        }
        if ((cellLength = values.getInteger(CELL_LENGTH_KEY)) != null) {
            this.setCellLength(cellLength);
        }
        if ((cellSuffix = values.getString(CELL_SUFFIX_KEY)) != null) {
            this.setCellSuffix(cellSuffix);
        }
        if ((defaultAlternate = values.getString(DEFAULT_ALTERNATE_KEY)) != null) {
            this.setDefaultAlternate(defaultAlternate);
        }
        if ((allowedTags = values.getStrings(ALLOWED_TAGS_KEY)) != null) {
            this.setAllowedTags(allowedTags);
        }
    }

    public String getListDelimiter() {
        return this.defaultDelim;
    }

    protected void setListDelimiter(String delim) {
        this.defaultDelim = delim;
    }

    public String getListFinalDelimiter() {
        return this.defaultFinalDelim;
    }

    protected void setListFinalDelimiter(String finalDelim) {
        this.defaultFinalDelim = finalDelim;
    }

    public int getTruncateLength() {
        return this.defaultTruncateLength;
    }

    protected void setTruncateLength(int maxlen) {
        this.defaultTruncateLength = maxlen;
    }

    public String getTruncateSuffix() {
        return this.defaultTruncateSuffix;
    }

    protected void setTruncateSuffix(String suffix) {
        this.defaultTruncateSuffix = suffix;
    }

    public boolean getTruncateAtWord() {
        return this.defaultTruncateAtWord;
    }

    protected void setTruncateAtWord(boolean atWord) {
        this.defaultTruncateAtWord = atWord;
    }

    public String getCellSuffix() {
        return this.defaultCellSuffix;
    }

    protected void setCellSuffix(String suffix) {
        this.defaultCellSuffix = suffix;
    }

    public int getCellLength() {
        return this.defaultCellLength;
    }

    protected void setCellLength(int maxlen) {
        this.defaultCellLength = maxlen;
    }

    public String getDefaultAlternate() {
        return this.defaultAlternate;
    }

    protected void setDefaultAlternate(String dflt) {
        this.defaultAlternate = dflt;
    }

    public String[] getAllowedTags() {
        return this.defaultAllowedTags;
    }

    protected void setAllowedTags(String[] tags) {
        this.defaultAllowedTags = tags;
    }

    public String list(Object list) {
        return this.list(list, this.defaultDelim, this.defaultFinalDelim);
    }

    public String list(Object list, String delim) {
        return this.list(list, delim, delim);
    }

    public String list(Object list, String delim, String finaldelim) {
        return this.list(list, delim, finaldelim, null);
    }

    public String list(Object list, String delim, String finaldelim, String property) {
        List<Object> items;
        if (list == null) {
            return null;
        }
        if (list instanceof Collection) {
            return this.format((Collection)list, delim, finaldelim, property);
        }
        if (list.getClass().isArray()) {
            int size = Array.getLength(list);
            items = new ArrayList<Object>(size);
            for (int i = 0; i < size; ++i) {
                items.add(Array.get(list, i));
            }
        } else {
            items = Collections.singletonList(list);
        }
        return this.format(items, delim, finaldelim, property);
    }

    protected String format(Collection list, String delim, String finaldelim, String property) {
        StringBuilder sb = new StringBuilder();
        int size = list.size();
        Iterator iterator = list.iterator();
        for (int i = 0; i < size; ++i) {
            if (property != null && property.length() > 0) {
                sb.append(this.getProperty(iterator.next(), property));
            } else {
                sb.append(iterator.next());
            }
            if (i < size - 2) {
                sb.append(delim);
                continue;
            }
            if (i >= size - 1) continue;
            sb.append(finaldelim);
        }
        return sb.toString();
    }

    @Deprecated
    public String message(String format, Collection args) {
        return this.message(format, new Object[]{args});
    }

    @Deprecated
    public String message(String format, Object arg) {
        return this.message(format, new Object[]{arg});
    }

    @Deprecated
    public String message(String format, Object arg1, Object arg2) {
        return this.message(format, new Object[]{arg1, arg2});
    }

    public String message(String format, Object ... args) {
        if (format == null) {
            return null;
        }
        if (args == null || args.length == 0) {
            return format;
        }
        if (args.length == 1 && args[0] instanceof Collection) {
            Collection list = (Collection)args[0];
            if (list.isEmpty()) {
                return format;
            }
            args = list.toArray();
        }
        return MessageFormat.format(format, args);
    }

    public String printf(String format, Object ... args) {
        if (format == null) {
            return null;
        }
        if (args == null || args.length == 0) {
            return format;
        }
        if (args.length == 1 && args[0] instanceof Collection) {
            Collection list = (Collection)args[0];
            if (list.isEmpty()) {
                return format;
            }
            args = list.toArray();
        }
        return String.format(this.getLocale(), format, args);
    }

    public String truncate(Object truncateMe) {
        return this.truncate(truncateMe, this.defaultTruncateLength);
    }

    public String truncate(Object truncateMe, int maxLength) {
        return this.truncate(truncateMe, maxLength, this.defaultTruncateSuffix);
    }

    public String truncate(Object truncateMe, String suffix) {
        return this.truncate(truncateMe, this.defaultTruncateLength, suffix);
    }

    public String truncate(Object truncateMe, int maxLength, String suffix) {
        return this.truncate(truncateMe, maxLength, suffix, this.defaultTruncateAtWord);
    }

    public String truncate(Object truncateMe, int maxLength, String suffix, boolean defaultTruncateAtWord) {
        int lastSpace;
        if (truncateMe == null || maxLength <= 0) {
            return null;
        }
        String string = String.valueOf(truncateMe);
        if (string.length() <= maxLength) {
            return string;
        }
        if (suffix == null || maxLength - suffix.length() <= 0) {
            return string.substring(0, maxLength);
        }
        if (defaultTruncateAtWord && (lastSpace = string.substring(0, maxLength - suffix.length() + 1).lastIndexOf(" ")) > suffix.length()) {
            return string.substring(0, lastSpace) + suffix;
        }
        return string.substring(0, maxLength - suffix.length()) + suffix;
    }

    public String space(int length) {
        if (length < 0) {
            return null;
        }
        StringBuilder space = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            space.append(' ');
        }
        return space.toString();
    }

    public String cell(Object obj) {
        return this.cell(obj, this.defaultCellLength);
    }

    public String cell(Object obj, int cellsize) {
        return this.cell(obj, cellsize, this.defaultCellSuffix);
    }

    public String cell(Object obj, String suffix) {
        return this.cell(obj, this.defaultCellLength, suffix);
    }

    public String cell(Object obj, int cellsize, String suffix) {
        if (obj == null || cellsize <= 0) {
            return null;
        }
        String value = String.valueOf(obj);
        if (value.length() == cellsize) {
            return value;
        }
        if (value.length() > cellsize) {
            return this.truncate(value, cellsize, suffix);
        }
        return value + this.space(cellsize - value.length());
    }

    public String capitalize(Object capitalizeMe) {
        if (capitalizeMe == null) {
            return null;
        }
        String string = String.valueOf(capitalizeMe);
        switch (string.length()) {
            case 0: {
                return string;
            }
            case 1: {
                return string.toUpperCase();
            }
        }
        StringBuilder out = new StringBuilder(string.length());
        out.append(string.substring(0, 1).toUpperCase());
        out.append(string.substring(1, string.length()));
        return out.toString();
    }

    public String uncapitalize(Object uncapitalizeMe) {
        if (uncapitalizeMe == null) {
            return null;
        }
        String string = String.valueOf(uncapitalizeMe);
        switch (string.length()) {
            case 0: {
                return string;
            }
            case 1: {
                return string.toLowerCase();
            }
        }
        StringBuilder out = new StringBuilder(string.length());
        out.append(string.substring(0, 1).toLowerCase());
        out.append(string.substring(1, string.length()));
        return out.toString();
    }

    public Object alt(Object checkMe) {
        return this.alt(checkMe, this.defaultAlternate);
    }

    public Object alt(Object checkMe, Object alternate) {
        if (checkMe == null) {
            return alternate;
        }
        return checkMe;
    }

    public String br(Object obj) {
        if (obj == null) {
            return null;
        }
        return String.valueOf(obj).replaceAll("\n", "<br />\n");
    }

    public String stripTags(Object obj) {
        return this.stripTags(obj, this.defaultAllowedTags);
    }

    public String stripTags(Object obj, String ... allowedTags) {
        if (obj == null) {
            return null;
        }
        StringBuilder allowedTagList = new StringBuilder();
        if (allowedTags != null) {
            for (String tag : allowedTags) {
                if (tag == null || !tag.matches("[a-zA-Z0-9]+")) continue;
                if (allowedTagList.length() > 0) {
                    allowedTagList.append("|");
                }
                allowedTagList.append(tag);
            }
        }
        String tagRule = "<[^>]*?>";
        if (allowedTagList.length() > 0) {
            tagRule = "<(?!/?(" + allowedTagList.toString() + ")[\\s>/])[^>]*?>";
        }
        return Pattern.compile(tagRule, 2).matcher(String.valueOf(obj)).replaceAll("");
    }

    public String plural(int value, String singular) {
        return this.plural(value, singular, null);
    }

    public String plural(int value, String singular, String plural) {
        if (value == 1 || value == -1) {
            return singular;
        }
        if (plural != null) {
            return plural;
        }
        if (singular == null || singular.length() == 0) {
            return singular;
        }
        boolean isCapital = !singular.substring(singular.length() - 1).toLowerCase().equals(singular.substring(singular.length() - 1));
        String word = singular.toLowerCase();
        if (word.endsWith("x") || word.endsWith("sh") || word.endsWith("ch") || word.endsWith("s")) {
            return singular.concat(isCapital ? "ES" : "es");
        }
        if (word.length() > 1 && word.endsWith("y") && !word.substring(word.length() - 2, word.length() - 1).matches("[aeiou]")) {
            return singular.substring(0, singular.length() - 1).concat(isCapital ? "IES" : "ies");
        }
        return singular.concat(isCapital ? "S" : "s");
    }

    protected Object getProperty(Object object, String property) {
        try {
            return PropertyUtils.getProperty((Object)object, (String)property);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not retrieve '" + property + "' from " + object + ": " + e);
        }
    }

    public Measurements measure(Object measureMe) {
        if (measureMe == null) {
            return null;
        }
        return new Measurements(String.valueOf(measureMe));
    }

    public static class Measurements {
        private int height;
        private int width;

        public Measurements(String s) {
            String[] lines = s.split("\n");
            this.height = lines.length;
            for (String line : lines) {
                if (line.length() <= this.width) continue;
                this.width = line.length();
            }
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        public String toString() {
            StringBuilder out = new StringBuilder(28);
            out.append("{ height: ");
            out.append(this.height);
            out.append(", width: ");
            out.append(this.width);
            out.append(" }");
            return out.toString();
        }
    }
}

