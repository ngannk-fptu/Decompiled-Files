/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 */
package org.apache.abdera.ext.thread;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.activation.MimeType;
import org.apache.abdera.ext.thread.InReplyTo;
import org.apache.abdera.ext.thread.ThreadConstants;
import org.apache.abdera.ext.thread.Total;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Source;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ThreadHelper {
    ThreadHelper() {
    }

    public static int getCount(Link link) {
        String val = link.getAttributeValue(ThreadConstants.THRCOUNT);
        return val != null ? Integer.parseInt(val) : 0;
    }

    public static AtomDate getUpdated(Link link) {
        String val = link.getAttributeValue(ThreadConstants.THRUPDATED);
        if (val == null) {
            val = link.getAttributeValue(ThreadConstants.THRWHEN);
        }
        return val != null ? AtomDate.valueOf(val) : null;
    }

    public static void setCount(Link link, int count) {
        link.setAttributeValue(ThreadConstants.THRCOUNT, String.valueOf(count).trim());
    }

    public static void setUpdated(Link link, Date when) {
        link.setAttributeValue(ThreadConstants.THRUPDATED, AtomDate.valueOf(when).getValue());
    }

    public static void setUpdated(Link link, Calendar when) {
        link.setAttributeValue(ThreadConstants.THRUPDATED, AtomDate.valueOf(when).getValue());
    }

    public static void setUpdated(Link link, long when) {
        link.setAttributeValue(ThreadConstants.THRUPDATED, AtomDate.valueOf(when).getValue());
    }

    public static void setUpdated(Link link, String when) {
        link.setAttributeValue(ThreadConstants.THRUPDATED, AtomDate.valueOf(when).getValue());
    }

    public static Total addTotal(Entry entry, int total) {
        Factory factory = entry.getFactory();
        Total totalelement = (Total)factory.newExtensionElement(ThreadConstants.THRTOTAL, entry);
        totalelement.setValue(total);
        return totalelement;
    }

    public static Total getTotal(Entry entry) {
        return (Total)entry.getFirstChild(ThreadConstants.THRTOTAL);
    }

    public static void addInReplyTo(Entry entry, InReplyTo replyTo) {
        entry.addExtension(replyTo);
    }

    public static InReplyTo addInReplyTo(Entry entry) {
        return (InReplyTo)entry.addExtension(ThreadConstants.IN_REPLY_TO);
    }

    public static InReplyTo addInReplyTo(Entry entry, Entry ref) {
        if (ref.equals(entry)) {
            return null;
        }
        InReplyTo irt = ThreadHelper.addInReplyTo(entry);
        try {
            Link selflink;
            Source src;
            irt.setRef(ref.getId());
            Link altlink = ref.getAlternateLink();
            if (altlink != null) {
                irt.setHref(altlink.getResolvedHref());
                if (altlink.getMimeType() != null) {
                    irt.setMimeType(altlink.getMimeType());
                }
            }
            if ((src = ref.getSource()) != null && (selflink = src.getSelfLink()) != null) {
                irt.setSource(selflink.getResolvedHref());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return irt;
    }

    public static InReplyTo addInReplyTo(Entry entry, IRI ref) {
        try {
            if (entry.getId() != null && entry.getId().equals(ref)) {
                return null;
            }
        }
        catch (Exception e) {
            // empty catch block
        }
        InReplyTo irt = ThreadHelper.addInReplyTo(entry);
        irt.setRef(ref);
        return irt;
    }

    public static InReplyTo addInReplyTo(Entry entry, String ref) {
        return ThreadHelper.addInReplyTo(entry, new IRI(ref));
    }

    public static InReplyTo addInReplyTo(Entry entry, IRI ref, IRI source, IRI href, MimeType type) {
        InReplyTo irt = ThreadHelper.addInReplyTo(entry, ref);
        if (irt != null) {
            if (source != null) {
                irt.setSource(source);
            }
            if (href != null) {
                irt.setHref(href);
            }
            if (type != null) {
                irt.setMimeType(type);
            }
        }
        return irt;
    }

    public static InReplyTo addInReplyTo(Entry entry, String ref, String source, String href, String type) {
        InReplyTo irt = ThreadHelper.addInReplyTo(entry, ref);
        if (irt != null) {
            if (source != null) {
                irt.setSource(source);
            }
            if (href != null) {
                irt.setHref(href);
            }
            if (type != null) {
                irt.setMimeType(type);
            }
        }
        return irt;
    }

    public static InReplyTo getInReplyTo(Entry entry) {
        return (InReplyTo)entry.getFirstChild(ThreadConstants.IN_REPLY_TO);
    }

    public static List<InReplyTo> getInReplyTos(Entry entry) {
        List<InReplyTo> list = entry.getExtensions(ThreadConstants.IN_REPLY_TO);
        return list;
    }

    public static InReplyTo newInReplyTo(Factory factory) {
        return new InReplyTo(factory);
    }

    public static Total newTotal(Factory factory) {
        return new Total(factory);
    }
}

