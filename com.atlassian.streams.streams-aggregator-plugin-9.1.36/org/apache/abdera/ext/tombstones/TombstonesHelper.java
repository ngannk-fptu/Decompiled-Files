/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.ext.tombstones;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.abdera.ext.tombstones.Tombstone;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TombstonesHelper {
    public static final String TNS = "http://purl.org/atompub/tombstones/1.0";
    public static final QName DELETED_ENTRY = new QName("http://purl.org/atompub/tombstones/1.0", "deleted-entry");
    public static final QName BY = new QName("http://purl.org/atompub/tombstones/1.0", "by");
    public static final QName COMMENT = new QName("http://purl.org/atompub/tombstones/1.0", "comment");

    public static List<Tombstone> getTombstones(Feed source) {
        return source.getExtensions(DELETED_ENTRY);
    }

    public static void addTombstone(Feed source, Tombstone tombstone) {
        source.addExtension(tombstone);
    }

    public static Tombstone addTombstone(Feed source, Entry entry) {
        Tombstone tombstone = (Tombstone)source.getFactory().newExtensionElement(DELETED_ENTRY, source);
        tombstone.setRef(entry.getId());
        Object parent = entry.getParentElement();
        if (parent != null && parent.equals(source)) {
            entry.discard();
        }
        return tombstone;
    }

    public static Tombstone addTombstone(Feed source, Entry entry, Date when, String by, String comment) {
        Tombstone ts = TombstonesHelper.addTombstone(source, entry);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, Entry entry, String when, String by, String comment) {
        Tombstone ts = TombstonesHelper.addTombstone(source, entry);
        ts.setWhen(when);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, Entry entry, Calendar when, String by, String comment) {
        Tombstone ts = TombstonesHelper.addTombstone(source, entry);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, Entry entry, long when, String by, String comment) {
        Tombstone ts = TombstonesHelper.addTombstone(source, entry);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, Entry entry, AtomDate when, String by, String comment) {
        Tombstone ts = TombstonesHelper.addTombstone(source, entry);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }

    public static boolean hasTombstones(Feed source) {
        return source.getExtension(DELETED_ENTRY) != null;
    }

    public static Tombstone addTombstone(Feed source, String id) {
        Tombstone tombstone = (Tombstone)source.getFactory().newExtensionElement(DELETED_ENTRY);
        tombstone.setRef(id);
        return tombstone;
    }

    public static Tombstone addTombstone(Feed source, String id, Date when, String by, String comment) {
        Tombstone ts = TombstonesHelper.addTombstone(source, id);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, String id, String when, String by, String comment) {
        Tombstone ts = TombstonesHelper.addTombstone(source, id);
        ts.setWhen(when);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, String id, Calendar when, String by, String comment) {
        Tombstone ts = TombstonesHelper.addTombstone(source, id);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, String id, long when, String by, String comment) {
        Tombstone ts = TombstonesHelper.addTombstone(source, id);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, String id, AtomDate when, String by, String comment) {
        Tombstone ts = TombstonesHelper.addTombstone(source, id);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }
}

