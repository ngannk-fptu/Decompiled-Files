/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.header;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.jackrabbit.webdav.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IfHeader
implements Header {
    private static final Logger log = LoggerFactory.getLogger(IfHeader.class);
    private final String headerValue;
    private final IfHeaderInterface ifHeader;
    private List<String> allTokens = new ArrayList<String>();
    private List<String> allNotTokens = new ArrayList<String>();

    public IfHeader(String[] tokens) {
        this.allTokens.addAll(Arrays.asList(tokens));
        StringBuffer b = new StringBuffer();
        for (String token : tokens) {
            b.append("(").append("<");
            b.append(token);
            b.append(">").append(")");
        }
        this.headerValue = b.toString();
        this.ifHeader = this.parse();
    }

    public IfHeader(HttpServletRequest req) {
        this.headerValue = req.getHeader("If");
        this.ifHeader = this.parse();
    }

    @Override
    public String getHeaderName() {
        return "If";
    }

    @Override
    public String getHeaderValue() {
        return this.headerValue;
    }

    public boolean hasValue() {
        return this.ifHeader != null;
    }

    public boolean matches(String tag, String token, String etag) {
        if (this.ifHeader == null) {
            log.debug("matches: No If header, assume match");
            return true;
        }
        return this.ifHeader.matches(tag, token, etag);
    }

    public Iterator<String> getAllTokens() {
        return this.allTokens.iterator();
    }

    public Iterator<String> getAllNotTokens() {
        return this.allNotTokens.iterator();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private IfHeaderInterface parse() {
        Cloneable ifHeader;
        if (this.headerValue != null && this.headerValue.length() > 0) {
            int firstChar = 0;
            try (StringReader reader = null;){
                reader = new StringReader(this.headerValue);
                try {
                    reader.mark(1);
                    firstChar = this.readWhiteSpace(reader);
                    reader.reset();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                if (firstChar == 40) {
                    ifHeader = this.parseUntagged(reader);
                }
                if (firstChar == 60) {
                    ifHeader = this.parseTagged(reader);
                }
                this.logIllegalState("If", firstChar, "(<", null);
                ifHeader = null;
            }
        } else {
            log.debug("IfHeader: No If header in request");
            ifHeader = null;
        }
        return ifHeader;
    }

    private IfHeaderMap parseTagged(StringReader reader) {
        IfHeaderMap map = new IfHeaderMap();
        try {
            int c;
            while ((c = this.readWhiteSpace(reader)) >= 0) {
                if (c == 60) {
                    String resource = this.readWord(reader, '>');
                    if (resource != null) {
                        map.put(resource, this.parseUntagged(reader));
                        continue;
                    }
                    break;
                }
                this.logIllegalState("Tagged", c, "<", reader);
            }
        }
        catch (IOException ioe) {
            log.error("parseTagged: Problem parsing If header: " + ioe.toString());
        }
        return map;
    }

    private IfHeaderList parseUntagged(StringReader reader) {
        IfHeaderList list = new IfHeaderList();
        try {
            while (true) {
                reader.mark(1);
                int c = this.readWhiteSpace(reader);
                if (c < 0) break;
                if (c == 40) {
                    list.add(this.parseIfList(reader));
                    continue;
                }
                if (c == 60) {
                    reader.reset();
                    break;
                }
                this.logIllegalState("Untagged", c, "(", reader);
            }
        }
        catch (IOException ioe) {
            log.error("parseUntagged: Problem parsing If header: " + ioe.toString());
        }
        return list;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private IfList parseIfList(StringReader reader) throws IOException {
        IfList res = new IfList();
        boolean positive = true;
        block6: while (true) {
            int nextChar = this.readWhiteSpace(reader);
            switch (nextChar) {
                case 78: 
                case 110: {
                    int not = reader.read();
                    if (not != 111 && not != 79) {
                        this.logIllegalState("IfList-Not", not, "o", null);
                        continue block6;
                    }
                    not = reader.read();
                    if (not != 116 && not != 84) {
                        this.logIllegalState("IfList-Not", not, "t", null);
                        continue block6;
                    }
                    positive = false;
                    continue block6;
                }
                case 60: {
                    String word = this.readWord(reader, '>');
                    if (word == null) continue block6;
                    res.add(new IfListEntryToken(word, positive));
                    if (positive) {
                        this.allTokens.add(word);
                    } else {
                        this.allNotTokens.add(word);
                    }
                    positive = true;
                    continue block6;
                }
                case 91: {
                    String word = this.readWord(reader, ']');
                    if (word == null) continue block6;
                    res.add(new IfListEntryEtag(word, positive));
                    positive = true;
                    continue block6;
                }
                case 41: {
                    log.debug("parseIfList: End of If list, terminating loop");
                    return res;
                }
                default: {
                    this.logIllegalState("IfList", nextChar, "nN<[)", reader);
                    if (nextChar < 0) return res;
                    continue block6;
                }
            }
            break;
        }
    }

    private int readWhiteSpace(Reader reader) throws IOException {
        int c = reader.read();
        while (c >= 0 && Character.isWhitespace((char)c)) {
            c = reader.read();
        }
        return c;
    }

    private String readWord(Reader reader, char end) throws IOException {
        StringBuffer buf = new StringBuffer();
        int c = reader.read();
        while (c >= 0 && c != end) {
            buf.append((char)c);
            c = reader.read();
        }
        if (c < 0) {
            log.error("readWord: Unexpected end of input reading word");
            return null;
        }
        return buf.toString();
    }

    private void logIllegalState(String state, int effChar, String expChar, StringReader reader) {
        String effString = effChar < 0 ? "<EOF>" : String.valueOf((char)effChar);
        log.error("logIllegalState: Unexpected character '" + effString + "' in state " + state + ", expected any of " + expChar);
        if (reader != null && effChar >= 0) {
            try {
                log.debug("logIllegalState: Catch up to any of " + expChar);
                do {
                    reader.mark(1);
                } while ((effChar = reader.read()) >= 0 && expChar.indexOf(effChar) < 0);
                if (effChar >= 0) {
                    reader.reset();
                }
            }
            catch (IOException ioe) {
                log.error("logIllegalState: IO Problem catching up to any of " + expChar);
            }
        }
    }

    private static class IfHeaderMap
    extends HashMap<String, IfHeaderList>
    implements IfHeaderInterface {
        private IfHeaderMap() {
        }

        @Override
        public boolean matches(String resource, String token, String etag) {
            log.debug("matches: Trying to match resource=" + resource + ", token=" + token + "," + etag);
            IfHeaderList list = (IfHeaderList)this.get(resource);
            if (list == null) {
                log.debug("matches: No entry for tag " + resource + ", assuming match");
                return true;
            }
            return list.matches(resource, token, etag);
        }
    }

    private static class IfHeaderList
    extends ArrayList<IfList>
    implements IfHeaderInterface {
        private IfHeaderList() {
        }

        @Override
        public boolean matches(String resource, String token, String etag) {
            log.debug("matches: Trying to match token=" + token + ", etag=" + etag);
            for (IfList il : this) {
                if (!il.match(token, etag)) continue;
                log.debug("matches: Found match with " + il);
                return true;
            }
            return false;
        }
    }

    private static interface IfHeaderInterface {
        public boolean matches(String var1, String var2, String var3);
    }

    private static class IfList
    extends ArrayList<IfListEntry> {
        private IfList() {
        }

        @Override
        public boolean add(IfListEntry entry) {
            return super.add(entry);
        }

        @Override
        public void add(int index, IfListEntry entry) {
            super.add(index, entry);
        }

        public boolean match(String token, String etag) {
            log.debug("match: Trying to match token=" + token + ", etag=" + etag);
            for (int i = 0; i < this.size(); ++i) {
                IfListEntry ile = (IfListEntry)this.get(i);
                if (ile.match(token, etag)) continue;
                log.debug("match: Entry " + i + "-" + ile + " does not match");
                return false;
            }
            return true;
        }
    }

    private static class IfListEntryEtag
    extends IfListEntry {
        IfListEntryEtag(String etag, boolean positive) {
            super(etag, positive);
        }

        @Override
        public boolean match(String token, String etag) {
            return super.match(etag);
        }

        @Override
        protected String getType() {
            return "ETag";
        }
    }

    private static class IfListEntryToken
    extends IfListEntry {
        IfListEntryToken(String token, boolean positive) {
            super(token, positive);
        }

        @Override
        public boolean match(String token, String etag) {
            return super.match(token);
        }

        @Override
        protected String getType() {
            return "Token";
        }
    }

    private static abstract class IfListEntry {
        protected final String value;
        protected final boolean positive;
        protected String stringValue;

        protected IfListEntry(String value, boolean positive) {
            this.value = value;
            this.positive = positive;
        }

        protected boolean match(String value) {
            return this.positive == this.value.equals(value);
        }

        public abstract boolean match(String var1, String var2);

        protected abstract String getType();

        protected String getValue() {
            return this.value;
        }

        public String toString() {
            if (this.stringValue == null) {
                this.stringValue = this.getType() + ": " + (this.positive ? "" : "!") + this.value;
            }
            return this.stringValue;
        }
    }
}

