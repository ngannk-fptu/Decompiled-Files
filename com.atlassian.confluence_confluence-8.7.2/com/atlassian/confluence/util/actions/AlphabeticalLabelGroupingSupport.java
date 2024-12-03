/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.MultiMap
 *  org.apache.commons.collections.map.MultiValueMap
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util.actions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.StringUtils;

public class AlphabeticalLabelGroupingSupport {
    private final Collection data;
    private MultiMap alphabetBuckets;
    private static final String[] keys = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0-9"};
    public static final String NUMERIC = "0-9";
    private static final int MIN_GROUP_SIZE = 10;
    private Collator collator = Collator.getInstance();

    public AlphabeticalLabelGroupingSupport(Collection c) {
        this.data = c;
        this.fillBuckets();
        this.mergeBuckets();
    }

    public boolean hasContent(String s) {
        return this.alphabetBuckets.containsKey((Object)s);
    }

    public List getContents(String s) {
        return (List)this.alphabetBuckets.get((Object)s);
    }

    public List getContents() {
        return new ArrayList(this.data);
    }

    public Collection getKeys() {
        TreeSet keys = new TreeSet((o1, o2) -> {
            if (o1.equals(o2)) {
                return 0;
            }
            if (NUMERIC.equals(o1)) {
                return 1;
            }
            if (NUMERIC.equals(o2)) {
                return -1;
            }
            return this.collator.compare(o1, o2);
        });
        keys.addAll(this.alphabetBuckets.keySet());
        return keys;
    }

    private void fillBuckets() {
        if (this.alphabetBuckets == null) {
            this.alphabetBuckets = new MultiValueMap();
            for (Object obj : this.data) {
                char firstCharacter = this.extractFirstCharacter(obj);
                if (Character.isLetter(firstCharacter)) {
                    this.alphabetBuckets.put((Object)("" + Character.toUpperCase(firstCharacter)), obj);
                    continue;
                }
                if (!Character.isDigit(firstCharacter)) continue;
                this.alphabetBuckets.put((Object)NUMERIC, obj);
            }
        }
    }

    private void mergeBuckets() {
        int minGroupSize = this.getContents().size() / keys.length + 10;
        String lastBucket = null;
        for (int i = 0; i < keys.length - 1; ++i) {
            List lastBucketContents;
            String key = keys[i];
            ArrayList currentBucketContents = (ArrayList)this.alphabetBuckets.remove((Object)key);
            if (currentBucketContents == null) {
                currentBucketContents = new ArrayList();
            }
            if (lastBucket == null) {
                lastBucket = key;
                lastBucketContents = currentBucketContents;
            } else {
                lastBucketContents = (List)this.alphabetBuckets.remove((Object)lastBucket);
                if (lastBucketContents == null) {
                    lastBucketContents = new ArrayList();
                }
                if (lastBucketContents.size() + currentBucketContents.size() < minGroupSize) {
                    lastBucketContents.addAll(currentBucketContents);
                    lastBucket = this.updateBucketKey(lastBucket, key);
                } else {
                    this.addAllToBucket(lastBucket, lastBucketContents);
                    lastBucket = key;
                    lastBucketContents = currentBucketContents;
                }
            }
            this.addAllToBucket(lastBucket, lastBucketContents);
        }
    }

    private void addAllToBucket(String lastBucket, List lastBucketContents) {
        for (Object lastBucketContent : lastBucketContents) {
            this.alphabetBuckets.put((Object)lastBucket, lastBucketContent);
        }
    }

    private String updateBucketKey(String lastBucket, String key) {
        if (lastBucket == null || lastBucket.length() == 0) {
            return key;
        }
        return lastBucket.substring(0, 1) + "-" + key;
    }

    private char extractFirstCharacter(Object obj) {
        String name;
        char firstCharacter = '\u0000';
        if (obj instanceof ContentEntityObject) {
            ContentEntityObject entity = (ContentEntityObject)obj;
            name = entity.getTitle();
        } else if (obj instanceof Label) {
            Label label = (Label)obj;
            name = label.getName();
        } else {
            name = obj.toString();
        }
        if (StringUtils.isNotEmpty((CharSequence)name)) {
            firstCharacter = name.toLowerCase().charAt(0);
        }
        return firstCharacter;
    }
}

