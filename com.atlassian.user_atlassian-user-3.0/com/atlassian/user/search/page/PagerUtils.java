/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 *  org.apache.log4j.Category
 */
package com.atlassian.user.search.page;

import com.atlassian.user.Entity;
import com.atlassian.user.search.page.Pager;
import com.opensymphony.util.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Category;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PagerUtils {
    public static final Category log = Category.getInstance(PagerUtils.class);
    public static final Pager EMPTY_PAGER = Pager.EMPTY_PAGER;
    public static final String ESCAPEDCOMMA = "ESCAPEDCOMMA";

    public static <T> List<T> toList(Pager<T> pager) {
        ArrayList<T> list = new ArrayList<T>();
        for (T obj : pager) {
            list.add(obj);
        }
        return list;
    }

    public static int count(Pager pager) {
        Iterator iter = pager.iterator();
        int count = 0;
        while (iter.hasNext()) {
            iter.next();
            ++count;
        }
        return count;
    }

    public static List toListOfEntityNames(Pager pager) {
        ArrayList<String> list = new ArrayList<String>();
        for (Object aPager : pager) {
            Entity e = (Entity)aPager;
            if (e == null) {
                log.error((Object)"null entity in pager");
                continue;
            }
            list.add(e.getName());
        }
        return list;
    }

    public static String extractSearchResultName(String distinguishedName) {
        String[] firstPhrase;
        String result = distinguishedName;
        if (!TextUtils.stringSet((String)distinguishedName)) {
            return result;
        }
        distinguishedName = distinguishedName.replaceAll("\\\\,", ESCAPEDCOMMA);
        String[] rdns = distinguishedName.split(",");
        boolean invalidDN = true;
        if (rdns.length >= 1 && (firstPhrase = rdns[0].split("=")).length >= 2) {
            result = firstPhrase[1].replaceAll(ESCAPEDCOMMA, "\\\\,");
            invalidDN = false;
        }
        if (log.isDebugEnabled() && invalidDN) {
            log.debug((Object)("Could not extract name from search result: " + distinguishedName));
        }
        return result;
    }
}

