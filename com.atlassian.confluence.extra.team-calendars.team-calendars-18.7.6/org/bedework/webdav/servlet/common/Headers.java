/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.xml.ws.Holder
 */
package org.bedework.webdav.servlet.common;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Holder;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;

public class Headers {
    public static final int depthInfinity = Integer.MAX_VALUE;
    public static final int depthNone = Integer.MIN_VALUE;

    public static int depth(HttpServletRequest req) throws WebdavException {
        return Headers.depth(req, Integer.MIN_VALUE);
    }

    public static int depth(HttpServletRequest req, int def) throws WebdavException {
        String depthStr = req.getHeader("Depth");
        if (depthStr == null) {
            return def;
        }
        if (depthStr.equals("infinity")) {
            return Integer.MAX_VALUE;
        }
        if (depthStr.equals("0")) {
            return 0;
        }
        if (depthStr.equals("1")) {
            return 1;
        }
        throw new WebdavBadRequest();
    }

    public static boolean brief(HttpServletRequest req) {
        String[] bels;
        String b = req.getHeader("Brief");
        if (b != null) {
            return b.equalsIgnoreCase("T");
        }
        b = req.getHeader("Prefer");
        if (b == null) {
            return false;
        }
        for (String bel : bels = b.split(",")) {
            String tr = bel.trim();
            if ("return-minimal".equalsIgnoreCase(tr)) {
                return true;
            }
            if (!Headers.preferKeyEquals(tr, "return", "minimal")) continue;
            return true;
        }
        return false;
    }

    public static boolean returnRepresentation(HttpServletRequest req) {
        String[] bels;
        String b = req.getHeader("Prefer");
        if (b == null) {
            return false;
        }
        for (String bel : bels = b.split(",")) {
            String tr = bel.trim();
            if ("return-representation".equalsIgnoreCase(tr)) {
                return true;
            }
            if (!Headers.preferKeyEquals(tr, "return", "representation")) continue;
            return true;
        }
        return false;
    }

    private static boolean preferKeyEquals(String unparsed, String key, String val) {
        if (unparsed.indexOf("=") < 0) {
            return false;
        }
        String[] sp = unparsed.split("=");
        if (sp.length != 2 || sp[0] == null || sp[1] == null) {
            return false;
        }
        if (!key.equalsIgnoreCase(sp[0].trim())) {
            return false;
        }
        return val.equals(sp[1].trim());
    }

    public static void makeLocation(HttpServletResponse resp, String url) {
        resp.setHeader("Location", url);
    }

    public static IfHeader testIfHeader(HttpServletRequest req) throws WebdavException {
        String ntt;
        String hdrStr = req.getHeader("If");
        if (hdrStr == null) {
            return null;
        }
        String h = hdrStr.trim();
        IfHeader ih = new IfHeader();
        if (h.startsWith("<")) {
            int pos = h.indexOf(">");
            if (pos < 0) {
                throw new WebdavException("Invalid If header: " + hdrStr);
            }
            ih.resourceTag = h.substring(1, pos);
            h = h.substring(pos + 1).trim();
        }
        if (!h.startsWith("(") || !h.endsWith(")")) {
            throw new WebdavException("Invalid If header: " + hdrStr);
        }
        h = h.substring(1, h.length() - 1);
        Holder hpos = new Holder();
        hpos.value = new Integer(0);
        while ((ntt = Headers.nextTagOrToken(hdrStr, h, (Holder<Integer>)hpos)) != null) {
            ih.addTagOrToken(ntt);
        }
        return ih;
    }

    private static String nextTagOrToken(String hdrStr, String h, Holder<Integer> hpos) throws WebdavException {
        String endDelim;
        int pos = (Integer)hpos.value;
        if (pos >= h.length()) {
            return null;
        }
        String res = null;
        char delim = h.charAt(pos);
        if (delim == '<') {
            endDelim = ">";
        } else if (delim == '[') {
            endDelim = "]";
        } else {
            throw new WebdavException("Invalid If header: " + hdrStr);
        }
        pos = h.indexOf(endDelim, pos);
        if (pos < 0) {
            throw new WebdavException("Invalid If header: " + hdrStr);
        }
        res = h.substring(0, pos + 1);
        ++pos;
        while (pos < h.length() && Character.isSpaceChar(h.charAt(pos))) {
            ++pos;
        }
        hpos.value = new Integer(pos);
        return res;
    }

    public static boolean ifNoneMatchAny(HttpServletRequest req) throws WebdavException {
        String hdrStr = req.getHeader("If-None-Match");
        return "*".equals(hdrStr);
    }

    public static String ifNoneMatch(HttpServletRequest req) throws WebdavException {
        return req.getHeader("If-None-Match");
    }

    public static String ifMatch(HttpServletRequest req) throws WebdavException {
        return req.getHeader("If-Match");
    }

    public static String ifScheduleTagMatch(HttpServletRequest req) throws WebdavException {
        return req.getHeader("If-Schedule-Tag-Match");
    }

    public static IfHeaders processIfHeaders(HttpServletRequest req) throws WebdavException {
        IfHeaders ih = new IfHeaders();
        ih.create = Headers.ifNoneMatchAny(req);
        ih.ifEtag = Headers.ifMatch(req);
        ih.ifHeader = Headers.testIfHeader(req);
        return ih;
    }

    public static class IfHeaders {
        public boolean create;
        public String ifEtag;
        public IfHeader ifHeader;
    }

    public static class IfHeader {
        public String resourceTag;
        public List<TagOrToken> tagsAndTokens = new ArrayList<TagOrToken>();

        public void addTagOrToken(String tagOrToken) throws WebdavException {
            boolean entityTag;
            if (tagOrToken.length() < 3) {
                throw new WebdavException("Invalid tag or token for If header: " + tagOrToken);
            }
            if (tagOrToken.startsWith("[")) {
                entityTag = true;
            } else if (tagOrToken.startsWith("<")) {
                entityTag = false;
            } else {
                throw new WebdavException("Invalid tag or token for If header: " + tagOrToken);
            }
            this.tagsAndTokens.add(new TagOrToken(entityTag, tagOrToken.substring(1, tagOrToken.length() - 1)));
        }

        public static class TagOrToken {
            public boolean entityTag;
            public String value;

            public TagOrToken(boolean entityTag, String value) {
                this.entityTag = entityTag;
                this.value = value;
            }
        }
    }
}

