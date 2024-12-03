/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.ext.history;

import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Source;

public final class FeedPagingHelper {
    public static final String FH_PREFIX = "fh";
    public static final String FHNS = "http://purl.org/syndication/history/1.0";
    public static final QName COMPLETE = new QName("http://purl.org/syndication/history/1.0", "complete", "fh");
    public static final QName ARCHIVE = new QName("http://purl.org/syndication/history/1.0", "archive", "fh");

    FeedPagingHelper() {
    }

    public static boolean isComplete(Source feed) {
        return feed.getExtension(COMPLETE) != null;
    }

    public static void setComplete(Source feed, boolean complete) {
        if (complete) {
            if (!FeedPagingHelper.isComplete(feed)) {
                feed.addExtension(COMPLETE);
            }
        } else if (FeedPagingHelper.isComplete(feed)) {
            Object ext = feed.getExtension(COMPLETE);
            ext.discard();
        }
    }

    public static void setArchive(Source feed, boolean archive) {
        if (archive) {
            if (!FeedPagingHelper.isArchive(feed)) {
                feed.addExtension(ARCHIVE);
            }
        } else if (FeedPagingHelper.isArchive(feed)) {
            Object ext = feed.getExtension(ARCHIVE);
            ext.discard();
        }
    }

    public static boolean isArchive(Source feed) {
        return feed.getExtension(ARCHIVE) != null;
    }

    public static boolean isPaged(Source feed) {
        return feed.getLink("next") != null || feed.getLink("previous") != null || feed.getLink("first") != null || feed.getLink("last") != null;
    }

    public static Link setNext(Source feed, String iri) {
        Link link = feed.getLink("next");
        if (link != null) {
            link.setHref(iri);
        } else {
            link = feed.addLink(iri, "next");
        }
        return link;
    }

    public static Link setPrevious(Source feed, String iri) {
        Link link = feed.getLink("previous");
        if (link != null) {
            link.setHref(iri);
        } else {
            link = feed.addLink(iri, "previous");
        }
        return link;
    }

    public static Link setFirst(Source feed, String iri) {
        Link link = feed.getLink("first");
        if (link != null) {
            link.setHref(iri);
        } else {
            link = feed.addLink(iri, "first");
        }
        return link;
    }

    public static Link setLast(Source feed, String iri) {
        Link link = feed.getLink("last");
        if (link != null) {
            link.setHref(iri);
        } else {
            link = feed.addLink(iri, "last");
        }
        return link;
    }

    public static Link setNextArchive(Source feed, String iri) {
        Link link = feed.getLink("next-archive");
        if (link == null) {
            link = feed.getLink("http://www.iana.org/assignments/relation/next-archive");
        }
        if (link != null) {
            link.setHref(iri);
        } else {
            link = feed.addLink(iri, "next-archive");
        }
        return link;
    }

    public static Link setPreviousArchive(Source feed, String iri) {
        Link link = feed.getLink("prev-archive");
        if (link == null) {
            link = feed.getLink("http://www.iana.org/assignments/relation/prev-archive");
        }
        if (link != null) {
            link.setHref(iri);
        } else {
            link = feed.addLink(iri, "prev-archive");
        }
        return link;
    }

    public static Link setCurrent(Source feed, String iri) {
        Link link = feed.getLink("current");
        if (link == null) {
            link = feed.getLink("http://www.iana.org/assignments/relation/current");
        }
        if (link != null) {
            link.setHref(iri);
        } else {
            link = feed.addLink(iri, "current");
        }
        return link;
    }

    public static IRI getNext(Source feed) {
        Link link = feed.getLink("next");
        return link != null ? link.getResolvedHref() : null;
    }

    public static IRI getPrevious(Source feed) {
        Link link = feed.getLink("previous");
        return link != null ? link.getResolvedHref() : null;
    }

    public static IRI getFirst(Source feed) {
        Link link = feed.getLink("first");
        return link != null ? link.getResolvedHref() : null;
    }

    public static IRI getLast(Source feed) {
        Link link = feed.getLink("last");
        return link != null ? link.getResolvedHref() : null;
    }

    public static IRI getPreviousArchive(Source feed) {
        Link link = feed.getLink("prev-archive");
        if (link == null) {
            link = feed.getLink("http://www.iana.org/assignments/relation/prev-archive");
        }
        return link != null ? link.getResolvedHref() : null;
    }

    public static IRI getNextArchive(Source feed) {
        Link link = feed.getLink("next-archive");
        if (link == null) {
            link = feed.getLink("http://www.iana.org/assignments/relation/next-archive");
        }
        return link != null ? link.getResolvedHref() : null;
    }

    public static IRI getCurrent(Source feed) {
        Link link = feed.getLink("current");
        if (link == null) {
            link = feed.getLink("http://www.iana.org/assignments/relation/current");
        }
        return link != null ? link.getResolvedHref() : null;
    }
}

