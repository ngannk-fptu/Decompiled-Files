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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.StringUtils;

public class AlphabeticalGroupingSupport {
    private final Collection data;
    private MultiMap alphabetBuckets;
    public static final String NUMERIC = "numeric";

    public AlphabeticalGroupingSupport(Collection c) {
        this.data = c;
    }

    public boolean hasContent(char c) {
        if (Character.isDigit(c)) {
            return this.hasContent(NUMERIC);
        }
        return this.hasContent(Character.toString(c));
    }

    public boolean hasContent(String s) {
        if (s != null && ((String)s).length() > 1 && !((String)s).equals(NUMERIC)) {
            s = "" + ((String)s).charAt(0);
        }
        return this.getAlphabetBuckets().containsKey(s);
    }

    public List getContents(char c) {
        if (Character.isDigit(c)) {
            return this.getContents(NUMERIC);
        }
        return this.getContents(Character.toString(c));
    }

    public List getContents(String s) {
        if (s == null || ((String)s).equals("")) {
            return this.getContents();
        }
        if (((String)s).length() > 1 && !((String)s).equals(NUMERIC)) {
            s = "" + ((String)s).charAt(0);
        }
        return (List)this.getAlphabetBuckets().get(s);
    }

    public List getContents() {
        return new ArrayList(this.data);
    }

    private MultiMap getAlphabetBuckets() {
        if (this.alphabetBuckets == null) {
            this.alphabetBuckets = new MultiValueMap();
            for (Object obj : this.data) {
                char firstCharacter = this.extractFirstCharacter(obj);
                if (Character.isLetter(firstCharacter)) {
                    this.alphabetBuckets.put((Object)("" + firstCharacter), obj);
                    continue;
                }
                if (!Character.isDigit(firstCharacter)) continue;
                this.alphabetBuckets.put((Object)NUMERIC, obj);
            }
        }
        return this.alphabetBuckets;
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

