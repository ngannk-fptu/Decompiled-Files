/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.core.bean.EntityObject;
import com.google.common.collect.Lists;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContentUtils {
    public static List<ConfluenceEntityObject> mergeContentObjects(Iterator it1, Iterator it2, int maxSize) {
        ConfluenceEntityObject c1 = null;
        ConfluenceEntityObject c2 = null;
        ArrayList results = Lists.newArrayListWithExpectedSize((int)maxSize);
        while (results.size() < maxSize && (it1.hasNext() || it2.hasNext())) {
            ConfluenceEntityObject latest;
            if (c1 == null && it1.hasNext()) {
                c1 = (ConfluenceEntityObject)it1.next();
            }
            if (c2 == null && it2.hasNext()) {
                c2 = (ConfluenceEntityObject)it2.next();
            }
            if ((latest = ContentUtils.getLatest(c1, c2)) == null) break;
            if (latest == c1) {
                c1 = null;
            } else {
                c2 = null;
            }
            results.add(latest);
        }
        return results;
    }

    public static ConfluenceEntityObject getLatest(ConfluenceEntityObject c1, ConfluenceEntityObject c2) {
        if (c1 == null) {
            return c2;
        }
        if (c2 == null) {
            return c1;
        }
        if (new Timestamp(c1.getLastModificationDate().getTime()).compareTo(new Timestamp(c2.getLastModificationDate().getTime())) > 0) {
            return c1;
        }
        return c2;
    }

    public static List<Long> getEntityObjectsIds(List entityObjectList) {
        ArrayList<Long> spaceIds = new ArrayList<Long>();
        for (Object anEntityObjectList : entityObjectList) {
            EntityObject entity = (EntityObject)anEntityObjectList;
            spaceIds.add(entity.getId());
        }
        return spaceIds;
    }

    public static String getSpaceKeyFromCeo(ContentEntityObject ceo) {
        if (ceo instanceof SpaceContentEntityObject) {
            return ((SpaceContentEntityObject)ceo).getSpaceKey();
        }
        if (ceo instanceof Comment) {
            return ((Comment)ceo).getSpaceKey();
        }
        if (ceo instanceof Draft) {
            return ((Draft)ceo).getDraftSpaceKey();
        }
        return null;
    }
}

