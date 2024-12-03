/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.NaturalStringComparator
 */
package com.atlassian.confluence.extra.masterdetail;

import com.atlassian.confluence.extra.masterdetail.ExtractedDetails;
import com.atlassian.confluence.pages.NaturalStringComparator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractedDetailsComparator
implements Comparator<ExtractedDetails> {
    private final String key;
    private final boolean reverseSort;
    private final Comparator comparator;
    private final DateFormat dateFormatter;
    private static final String DATE_PATTERN = "(.*)\"(\\d\\d\\d\\d-\\d\\d-\\d\\d)\"(.*)";
    private static Pattern regex = Pattern.compile("(.*)\"(\\d\\d\\d\\d-\\d\\d-\\d\\d)\"(.*)", 32);

    public ExtractedDetailsComparator(String key, boolean reverseSort) {
        this.key = key;
        this.reverseSort = reverseSort;
        this.comparator = new NaturalStringComparator();
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public int compare(ExtractedDetails first, ExtractedDetails second) {
        boolean titleKey = "title".equalsIgnoreCase(this.key);
        String firstValue = this.getValue(first, titleKey);
        String secondValue = this.getValue(second, titleKey);
        try {
            Matcher firstDateMatcher = regex.matcher(firstValue);
            Matcher secondDateMatcher = regex.matcher(secondValue);
            if (firstDateMatcher.find() && secondDateMatcher.find()) {
                Date firstDate = this.dateFormatter.parse(firstDateMatcher.group(2));
                Date secondDate = this.dateFormatter.parse(secondDateMatcher.group(2));
                return !this.reverseSort ? firstDate.compareTo(secondDate) : secondDate.compareTo(firstDate);
            }
        }
        catch (ParseException firstDateMatcher) {
            // empty catch block
        }
        int comparison = this.comparator.compare(firstValue, secondValue);
        return this.reverseSort ? -comparison : comparison;
    }

    private String getValue(ExtractedDetails first, boolean titleKey) {
        String firstValue;
        if (first == null) {
            return "";
        }
        String string = firstValue = titleKey ? first.getTitle() : first.getDetailStorageFormat(this.key);
        if (firstValue == null) {
            return "";
        }
        return firstValue;
    }
}

