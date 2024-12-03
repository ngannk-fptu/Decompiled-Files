/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.c14n.implementations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;

class XmlAttrStack {
    private static final Logger LOG = LoggerFactory.getLogger(XmlAttrStack.class);
    private int currentLevel = 0;
    private int lastlevel = 0;
    private XmlsStackElement cur;
    private final List<XmlsStackElement> levels = new ArrayList<XmlsStackElement>();
    private final boolean c14n11;

    public XmlAttrStack(boolean c14n11) {
        this.c14n11 = c14n11;
    }

    void push(int level) {
        this.currentLevel = level;
        if (this.currentLevel == -1) {
            return;
        }
        this.cur = null;
        while (this.lastlevel >= this.currentLevel) {
            this.levels.remove(this.levels.size() - 1);
            int newSize = this.levels.size();
            if (newSize == 0) {
                this.lastlevel = 0;
                return;
            }
            this.lastlevel = this.levels.get((int)(newSize - 1)).level;
        }
    }

    void addXmlnsAttr(Attr n) {
        if (this.cur == null) {
            this.cur = new XmlsStackElement();
            this.cur.level = this.currentLevel;
            this.levels.add(this.cur);
            this.lastlevel = this.currentLevel;
        }
        this.cur.nodes.add(n);
    }

    void getXmlnsAttr(Collection<Attr> col) {
        HashMap<String, Attr> loa;
        block17: {
            XmlsStackElement e;
            int size;
            block16: {
                Iterator<Attr> it;
                size = this.levels.size() - 1;
                if (this.cur == null) {
                    this.cur = new XmlsStackElement();
                    this.cur.level = this.currentLevel;
                    this.lastlevel = this.currentLevel;
                    this.levels.add(this.cur);
                }
                boolean parentRendered = false;
                e = null;
                if (size == -1) {
                    parentRendered = true;
                } else {
                    e = this.levels.get(size);
                    if (e.rendered && e.level + 1 == this.currentLevel) {
                        parentRendered = true;
                    }
                }
                if (parentRendered) {
                    col.addAll(this.cur.nodes);
                    this.cur.rendered = true;
                    return;
                }
                loa = new HashMap<String, Attr>();
                if (!this.c14n11) break block16;
                ArrayList<Attr> baseAttrs = new ArrayList<Attr>();
                boolean successiveOmitted = true;
                while (size >= 0) {
                    e = this.levels.get(size);
                    if (e.rendered) {
                        successiveOmitted = false;
                    }
                    it = e.nodes.iterator();
                    while (it.hasNext() && successiveOmitted) {
                        Attr n = it.next();
                        if ("base".equals(n.getLocalName()) && !e.rendered) {
                            baseAttrs.add(n);
                            continue;
                        }
                        if (loa.containsKey(n.getName())) continue;
                        loa.put(n.getName(), n);
                    }
                    --size;
                }
                if (baseAttrs.isEmpty()) break block17;
                it = col.iterator();
                String base = null;
                Attr baseAttr = null;
                while (it.hasNext()) {
                    Attr n = it.next();
                    if (!"base".equals(n.getLocalName())) continue;
                    base = n.getValue();
                    baseAttr = n;
                    break;
                }
                for (Attr n : baseAttrs) {
                    if (base == null) {
                        base = n.getValue();
                        baseAttr = n;
                        continue;
                    }
                    try {
                        base = XmlAttrStack.joinURI(n.getValue(), base);
                    }
                    catch (URISyntaxException ue) {
                        LOG.debug(ue.getMessage(), (Throwable)ue);
                    }
                }
                if (base == null || base.length() == 0) break block17;
                baseAttr.setValue(base);
                col.add(baseAttr);
                break block17;
            }
            while (size >= 0) {
                e = this.levels.get(size);
                for (Attr n : e.nodes) {
                    if (loa.containsKey(n.getName())) continue;
                    loa.put(n.getName(), n);
                }
                --size;
            }
        }
        this.cur.rendered = true;
        col.addAll(loa.values());
    }

    private static String joinURI(String baseURI, String relativeURI) throws URISyntaxException {
        String tquery;
        String tpath;
        String tauthority;
        String tscheme;
        String bscheme = null;
        String bauthority = null;
        String bpath = "";
        String bquery = null;
        if (baseURI != null) {
            if (baseURI.endsWith("..")) {
                baseURI = baseURI + "/";
            }
            URI base = new URI(baseURI);
            bscheme = base.getScheme();
            bauthority = base.getAuthority();
            bpath = base.getPath();
            bquery = base.getQuery();
        }
        URI r = new URI(relativeURI);
        String rscheme = r.getScheme();
        String rauthority = r.getAuthority();
        String rpath = r.getPath();
        String rquery = r.getQuery();
        if (rscheme != null && rscheme.equals(bscheme)) {
            rscheme = null;
        }
        if (rscheme != null) {
            tscheme = rscheme;
            tauthority = rauthority;
            tpath = XmlAttrStack.removeDotSegments(rpath);
            tquery = rquery;
        } else {
            if (rauthority != null) {
                tauthority = rauthority;
                tpath = XmlAttrStack.removeDotSegments(rpath);
                tquery = rquery;
            } else {
                if (rpath.length() == 0) {
                    tpath = bpath;
                    tquery = rquery != null ? rquery : bquery;
                } else {
                    if (rpath.charAt(0) == '/') {
                        tpath = XmlAttrStack.removeDotSegments(rpath);
                    } else {
                        int last;
                        tpath = bauthority != null && bpath.length() == 0 ? "/" + rpath : ((last = bpath.lastIndexOf(47)) == -1 ? rpath : bpath.substring(0, last + 1) + rpath);
                        tpath = XmlAttrStack.removeDotSegments(tpath);
                    }
                    tquery = rquery;
                }
                tauthority = bauthority;
            }
            tscheme = bscheme;
        }
        return new URI(tscheme, tauthority, tpath, tquery, null).toString();
    }

    private static String removeDotSegments(String path) {
        LOG.debug("STEP OUTPUT BUFFER\t\tINPUT BUFFER");
        String input = path;
        while (input.indexOf("//") > -1) {
            input = input.replaceAll("//", "/");
        }
        StringBuilder output = new StringBuilder();
        if (input.charAt(0) == '/') {
            output.append('/');
            input = input.substring(1);
        }
        XmlAttrStack.printStep("1 ", output.toString(), input);
        while (input.length() != 0) {
            String segment;
            int index;
            if (input.startsWith("./")) {
                input = input.substring(2);
                XmlAttrStack.printStep("2A", output.toString(), input);
                continue;
            }
            if (input.startsWith("../")) {
                input = input.substring(3);
                if (!"/".equals(output.toString())) {
                    output.append("../");
                }
                XmlAttrStack.printStep("2A", output.toString(), input);
                continue;
            }
            if (input.startsWith("/./")) {
                input = input.substring(2);
                XmlAttrStack.printStep("2B", output.toString(), input);
                continue;
            }
            if ("/.".equals(input)) {
                input = input.replaceFirst("/.", "/");
                XmlAttrStack.printStep("2B", output.toString(), input);
                continue;
            }
            if (input.startsWith("/../")) {
                input = input.substring(3);
                if (output.length() == 0) {
                    output.append('/');
                } else if (output.toString().endsWith("../")) {
                    output.append("..");
                } else if (output.toString().endsWith("..")) {
                    output.append("/..");
                } else {
                    index = output.lastIndexOf("/");
                    if (index == -1) {
                        output = new StringBuilder();
                        if (input.charAt(0) == '/') {
                            input = input.substring(1);
                        }
                    } else {
                        output = output.delete(index, output.length());
                    }
                }
                XmlAttrStack.printStep("2C", output.toString(), input);
                continue;
            }
            if ("/..".equals(input)) {
                input = input.replaceFirst("/..", "/");
                if (output.length() == 0) {
                    output.append('/');
                } else if (output.toString().endsWith("../")) {
                    output.append("..");
                } else if (output.toString().endsWith("..")) {
                    output.append("/..");
                } else {
                    index = output.lastIndexOf("/");
                    if (index == -1) {
                        output = new StringBuilder();
                        if (input.charAt(0) == '/') {
                            input = input.substring(1);
                        }
                    } else {
                        output = output.delete(index, output.length());
                    }
                }
                XmlAttrStack.printStep("2C", output.toString(), input);
                continue;
            }
            if (".".equals(input)) {
                input = "";
                XmlAttrStack.printStep("2D", output.toString(), input);
                continue;
            }
            if ("..".equals(input)) {
                if (!"/".equals(output.toString())) {
                    output.append("..");
                }
                input = "";
                XmlAttrStack.printStep("2D", output.toString(), input);
                continue;
            }
            int end = -1;
            int begin = input.indexOf(47);
            if (begin == 0) {
                end = input.indexOf(47, 1);
            } else {
                end = begin;
                begin = 0;
            }
            if (end == -1) {
                segment = input.substring(begin);
                input = "";
            } else {
                segment = input.substring(begin, end);
                input = input.substring(end);
            }
            output.append(segment);
            XmlAttrStack.printStep("2E", output.toString(), input);
        }
        if (output.toString().endsWith("..")) {
            output.append('/');
            XmlAttrStack.printStep("3 ", output.toString(), input);
        }
        return output.toString();
    }

    private static void printStep(String step, String output, String input) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(" " + step + ":   " + output);
            if (output.length() == 0) {
                LOG.debug("\t\t\t\t" + input);
            } else {
                LOG.debug("\t\t\t" + input);
            }
        }
    }

    private static class XmlsStackElement {
        int level;
        boolean rendered = false;
        final List<Attr> nodes = new ArrayList<Attr>();

        private XmlsStackElement() {
        }
    }
}

