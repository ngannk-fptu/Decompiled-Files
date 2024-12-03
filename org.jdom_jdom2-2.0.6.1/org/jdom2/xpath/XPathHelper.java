/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.xpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.NamespaceAware;
import org.jdom2.Parent;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.filter.Filters;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class XPathHelper {
    private XPathHelper() {
    }

    private static StringBuilder getPositionPath(Object node, List<?> siblings, String pathToken, StringBuilder buffer) {
        buffer.append(pathToken);
        if (siblings != null) {
            int position = 0;
            Iterator<?> i = siblings.iterator();
            while (i.hasNext()) {
                ++position;
                if (i.next() != node) continue;
            }
            if (position > 1 || i.hasNext()) {
                buffer.append('[').append(position).append(']');
            }
        }
        return buffer;
    }

    private static final StringBuilder getSingleStep(NamespaceAware nsa, StringBuilder buffer) {
        if (nsa instanceof Content) {
            Content content = (Content)nsa;
            Parent pnt = content.getParent();
            if (content instanceof Text) {
                List<Text> sibs = pnt == null ? null : pnt.getContent(Filters.text());
                return XPathHelper.getPositionPath(content, sibs, "text()", buffer);
            }
            if (content instanceof Comment) {
                List<Comment> sibs = pnt == null ? null : pnt.getContent(Filters.comment());
                return XPathHelper.getPositionPath(content, sibs, "comment()", buffer);
            }
            if (content instanceof ProcessingInstruction) {
                List<ProcessingInstruction> sibs = pnt == null ? null : pnt.getContent(Filters.processinginstruction());
                return XPathHelper.getPositionPath(content, sibs, "processing-instruction()", buffer);
            }
            if (content instanceof Element && ((Element)content).getNamespace() == Namespace.NO_NAMESPACE) {
                String ename = ((Element)content).getName();
                List<Element> sibs = pnt instanceof Element ? ((Element)pnt).getChildren(ename) : null;
                return XPathHelper.getPositionPath(content, sibs, ename, buffer);
            }
            if (content instanceof Element) {
                Element emt = (Element)content;
                List<Element> sibs = pnt instanceof Element ? ((Element)pnt).getChildren(emt.getName(), emt.getNamespace()) : null;
                String xps = "*[local-name() = '" + emt.getName() + "' and namespace-uri() = '" + emt.getNamespaceURI() + "']";
                return XPathHelper.getPositionPath(content, sibs, xps, buffer);
            }
            List<NamespaceAware> sibs = pnt == null ? Collections.singletonList(nsa) : pnt.getContent();
            return XPathHelper.getPositionPath(content, sibs, "node()", buffer);
        }
        if (nsa instanceof Attribute) {
            Attribute att = (Attribute)nsa;
            if (att.getNamespace() == Namespace.NO_NAMESPACE) {
                buffer.append("@").append(att.getName());
            } else {
                buffer.append("@*[local-name() = '").append(att.getName());
                buffer.append("' and namespace-uri() = '");
                buffer.append(att.getNamespaceURI()).append("']");
            }
        }
        return buffer;
    }

    private static StringBuilder getRelativeElementPath(Element from, Parent to, StringBuilder sb) {
        Parent p;
        if (from == to) {
            sb.append(".");
            return sb;
        }
        ArrayList<Parent> tostack = new ArrayList<Parent>();
        for (p = to; p != null && p != from; p = p.getParent()) {
            tostack.add(p);
        }
        int pos = tostack.size();
        if (p != from) {
            Parent f;
            int fcnt = 0;
            for (f = from; f != null && (pos = XPathHelper.locate(f, tostack)) < 0; f = f.getParent()) {
                ++fcnt;
            }
            if (f == null) {
                throw new IllegalArgumentException("The 'from' and 'to' Element have no common ancestor.");
            }
            while (--fcnt >= 0) {
                sb.append("../");
            }
        }
        while (--pos >= 0) {
            XPathHelper.getSingleStep((NamespaceAware)tostack.get(pos), sb);
            sb.append("/");
        }
        sb.setLength(sb.length() - 1);
        return sb;
    }

    private static int locate(Parent f, List<Parent> tostack) {
        int ret = tostack.size();
        while (--ret >= 0) {
            if (f != tostack.get(ret)) continue;
            return ret;
        }
        return -1;
    }

    public static String getRelativePath(Content from, Content to) {
        Element efrom;
        if (from == null) {
            throw new NullPointerException("Cannot create a path from a null target");
        }
        if (to == null) {
            throw new NullPointerException("Cannot create a path to a null target");
        }
        StringBuilder sb = new StringBuilder();
        if (from == to) {
            return ".";
        }
        Element element = efrom = from instanceof Element ? (Element)from : from.getParentElement();
        if (from != efrom) {
            sb.append("../");
        }
        if (to instanceof Element) {
            XPathHelper.getRelativeElementPath(efrom, (Element)to, sb);
        } else {
            Parent telement = to.getParent();
            if (telement == null) {
                throw new IllegalArgumentException("Cannot get a relative XPath to detached content.");
            }
            XPathHelper.getRelativeElementPath(efrom, telement, sb);
            sb.append("/");
            XPathHelper.getSingleStep(to, sb);
        }
        return sb.toString();
    }

    public static String getRelativePath(Content from, Attribute to) {
        if (from == null) {
            throw new NullPointerException("Cannot create a path from a null Content");
        }
        if (to == null) {
            throw new NullPointerException("Cannot create a path to a null Attribute");
        }
        Element t = to.getParent();
        if (t == null) {
            throw new IllegalArgumentException("Cannot create a path to detached Attribute");
        }
        StringBuilder sb = new StringBuilder(XPathHelper.getRelativePath(from, (Content)t));
        sb.append("/");
        XPathHelper.getSingleStep(to, sb);
        return sb.toString();
    }

    public static String getRelativePath(Attribute from, Attribute to) {
        if (from == null) {
            throw new NullPointerException("Cannot create a path from a null 'from'");
        }
        if (to == null) {
            throw new NullPointerException("Cannot create a path to a null target");
        }
        if (from == to) {
            return ".";
        }
        Element f = from.getParent();
        if (f == null) {
            throw new IllegalArgumentException("Cannot create a path from a detached attrbibute");
        }
        return "../" + XPathHelper.getRelativePath((Content)f, to);
    }

    public static String getRelativePath(Attribute from, Content to) {
        if (from == null) {
            throw new NullPointerException("Cannot create a path from a null 'from'");
        }
        if (to == null) {
            throw new NullPointerException("Cannot create a path to a null target");
        }
        Element f = from.getParent();
        if (f == null) {
            throw new IllegalArgumentException("Cannot create a path from a detached attrbibute");
        }
        if (f == to) {
            return "..";
        }
        return "../" + XPathHelper.getRelativePath((Content)f, to);
    }

    public static String getAbsolutePath(Content to) {
        Element t;
        if (to == null) {
            throw new NullPointerException("Cannot create a path to a null target");
        }
        StringBuilder sb = new StringBuilder();
        Element element = t = to instanceof Element ? (Element)to : to.getParentElement();
        if (t == null) {
            if (to.getParent() == null) {
                throw new IllegalArgumentException("Cannot create a path to detached target");
            }
            sb.append("/");
            XPathHelper.getSingleStep(to, sb);
            return sb.toString();
        }
        Element r = t;
        while (r.getParentElement() != null) {
            r = r.getParentElement();
        }
        sb.append("/");
        XPathHelper.getSingleStep(r, sb);
        if (r != t) {
            sb.append("/");
            XPathHelper.getRelativeElementPath(r, t, sb);
        }
        if (t != to) {
            sb.append("/");
            XPathHelper.getSingleStep(to, sb);
        }
        return sb.toString();
    }

    public static String getAbsolutePath(Attribute to) {
        if (to == null) {
            throw new NullPointerException("Cannot create a path to a null target");
        }
        Element t = to.getParent();
        if (t == null) {
            throw new IllegalArgumentException("Cannot create a path to detached target");
        }
        Element r = t;
        while (r.getParentElement() != null) {
            r = r.getParentElement();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("/");
        XPathHelper.getSingleStep(r, sb);
        if (t != r) {
            sb.append("/");
            XPathHelper.getRelativeElementPath(r, t, sb);
        }
        sb.append("/");
        XPathHelper.getSingleStep(to, sb);
        return sb.toString();
    }
}

