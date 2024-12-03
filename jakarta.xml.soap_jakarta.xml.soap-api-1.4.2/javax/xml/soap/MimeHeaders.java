/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import java.util.Iterator;
import java.util.Vector;
import javax.xml.soap.MimeHeader;

public class MimeHeaders {
    private Vector<MimeHeader> headers = new Vector();

    public String[] getHeader(String name) {
        Vector<String> values = new Vector<String>();
        for (int i = 0; i < this.headers.size(); ++i) {
            MimeHeader hdr = this.headers.elementAt(i);
            if (!hdr.getName().equalsIgnoreCase(name) || hdr.getValue() == null) continue;
            values.addElement(hdr.getValue());
        }
        if (values.size() == 0) {
            return null;
        }
        Object[] r = new String[values.size()];
        values.copyInto(r);
        return r;
    }

    public void setHeader(String name, String value) {
        boolean found = false;
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Illegal MimeHeader name");
        }
        for (int i = 0; i < this.headers.size(); ++i) {
            MimeHeader hdr = this.headers.elementAt(i);
            if (!hdr.getName().equalsIgnoreCase(name)) continue;
            if (!found) {
                this.headers.setElementAt(new MimeHeader(hdr.getName(), value), i);
                found = true;
                continue;
            }
            this.headers.removeElementAt(i--);
        }
        if (!found) {
            this.addHeader(name, value);
        }
    }

    public void addHeader(String name, String value) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Illegal MimeHeader name");
        }
        int pos = this.headers.size();
        for (int i = pos - 1; i >= 0; --i) {
            MimeHeader hdr = this.headers.elementAt(i);
            if (!hdr.getName().equalsIgnoreCase(name)) continue;
            this.headers.insertElementAt(new MimeHeader(name, value), i + 1);
            return;
        }
        this.headers.addElement(new MimeHeader(name, value));
    }

    public void removeHeader(String name) {
        for (int i = 0; i < this.headers.size(); ++i) {
            MimeHeader hdr = this.headers.elementAt(i);
            if (!hdr.getName().equalsIgnoreCase(name)) continue;
            this.headers.removeElementAt(i--);
        }
    }

    public void removeAllHeaders() {
        this.headers.removeAllElements();
    }

    public Iterator<MimeHeader> getAllHeaders() {
        return this.headers.iterator();
    }

    public Iterator<MimeHeader> getMatchingHeaders(String[] names) {
        return new MatchingIterator(names, true, this.headers.iterator());
    }

    public Iterator<MimeHeader> getNonMatchingHeaders(String[] names) {
        return new MatchingIterator(names, false, this.headers.iterator());
    }

    static class MatchingIterator
    implements Iterator<MimeHeader> {
        private final boolean match;
        private final Iterator<MimeHeader> iterator;
        private final String[] names;
        private MimeHeader nextHeader;

        MatchingIterator(String[] names, boolean match, Iterator<MimeHeader> i) {
            this.match = match;
            this.names = names;
            this.iterator = i;
        }

        private MimeHeader nextMatch() {
            block0: while (this.iterator.hasNext()) {
                MimeHeader hdr = this.iterator.next();
                if (this.names == null) {
                    return this.match ? null : hdr;
                }
                for (int i = 0; i < this.names.length; ++i) {
                    if (!hdr.getName().equalsIgnoreCase(this.names[i])) continue;
                    if (!this.match) continue block0;
                    return hdr;
                }
                if (this.match) continue;
                return hdr;
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            if (this.nextHeader == null) {
                this.nextHeader = this.nextMatch();
            }
            return this.nextHeader != null;
        }

        @Override
        public MimeHeader next() {
            if (this.nextHeader != null) {
                MimeHeader ret = this.nextHeader;
                this.nextHeader = null;
                return ret;
            }
            if (this.hasNext()) {
                return this.nextHeader;
            }
            return null;
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }
    }
}

