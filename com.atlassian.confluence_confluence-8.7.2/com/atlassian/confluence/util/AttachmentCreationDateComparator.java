/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.pages.Attachment;
import java.util.Comparator;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentCreationDateComparator
implements Comparator {
    private static final Logger log = LoggerFactory.getLogger(AttachmentCreationDateComparator.class);

    public int compare(Object o1, Object o2) {
        int retval;
        Attachment a1 = (Attachment)o1;
        Attachment a2 = (Attachment)o2;
        Date d1 = a1.getCreationDate();
        Date d2 = a2.getCreationDate();
        if (d1 == null) {
            log.error("Null creationdate on attachment " + a1.getFileName());
            d1 = new java.sql.Date(0L);
        }
        if (d2 == null) {
            log.error("Null creationdate on attachment " + a2.getFileName());
            d2 = new java.sql.Date(0L);
        }
        if ((retval = new java.sql.Date(d1.getTime()).compareTo(new java.sql.Date(d2.getTime()))) == 0) {
            return a1.getId() < a2.getId() ? -1 : 1;
        }
        return retval;
    }
}

