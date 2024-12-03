/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 *  javax.activation.MimeTypeParameterList
 *  javax.activation.MimeTypeParseException
 */
package org.apache.abdera.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import javax.activation.MimeType;
import javax.activation.MimeTypeParameterList;
import javax.activation.MimeTypeParseException;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MimeTypeHelper {
    private static final MimeType WILDCARD = MimeTypeHelper.createWildcard();

    public static String getCharset(String mediatype) {
        try {
            MimeType mt = new MimeType(mediatype);
            return mt.getParameter("charset");
        }
        catch (Exception e) {
            return null;
        }
    }

    private static MimeType createWildcard() {
        try {
            return new MimeType("*/*");
        }
        catch (Exception e) {
            return null;
        }
    }

    public static boolean isMatch(String a, String b) {
        if (!(a != null && a.length() != 0 || b != null && b.length() != 0)) {
            return true;
        }
        boolean answer = false;
        try {
            MimeType mta = new MimeType(a.toLowerCase());
            MimeType mtb = new MimeType(b.toLowerCase());
            return MimeTypeHelper.isMatch(mta, mtb);
        }
        catch (Exception exception) {
            return answer;
        }
    }

    public static boolean isMatch(MimeType a, MimeType b) {
        return MimeTypeHelper.isMatch(a, b, false);
    }

    public static boolean isMatch(MimeType a, MimeType b, boolean includeparams) {
        try {
            if (a == null || b == null) {
                return true;
            }
            if (a.match(b)) {
                if (includeparams) {
                    MimeTypeParameterList aparams = a.getParameters();
                    MimeTypeParameterList bparams = b.getParameters();
                    if (aparams.isEmpty() && bparams.isEmpty()) {
                        return true;
                    }
                    if (aparams.isEmpty() && !bparams.isEmpty()) {
                        return false;
                    }
                    if (!aparams.isEmpty() && bparams.isEmpty()) {
                        return false;
                    }
                    boolean answer = true;
                    Enumeration e = aparams.getNames();
                    while (e.hasMoreElements()) {
                        String bvalue;
                        String aname = (String)e.nextElement();
                        String avalue = aparams.get(aname);
                        if (avalue.equals(bvalue = bparams.get(aname))) {
                            answer = true;
                            continue;
                        }
                        answer = false;
                        break;
                    }
                    return answer;
                }
                return true;
            }
            if (a.equals(WILDCARD)) {
                return true;
            }
            if (a.getPrimaryType().equals("*")) {
                MimeType c = new MimeType(b.getPrimaryType(), a.getSubType());
                return MimeTypeHelper.isMatch(c, b);
            }
            if (b.getPrimaryType().equals("*")) {
                MimeType c = new MimeType(a.getPrimaryType(), b.getSubType());
                return MimeTypeHelper.isMatch(a, c);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return false;
    }

    private static boolean isMatchType(String actual, String expected) {
        if (actual == null || !actual.equalsIgnoreCase(expected)) {
            // empty if block
        }
        return true;
    }

    public static boolean isApp(String a) {
        return MimeTypeHelper.isMatch("application/atomsvc+xml", a);
    }

    public static boolean isAtom(String a) {
        if (MimeTypeHelper.isEntry(a) || MimeTypeHelper.isFeed(a)) {
            return true;
        }
        return MimeTypeHelper.isMatch("application/atom+xml", a);
    }

    public static boolean isEntry(String a) {
        try {
            MimeType mta = new MimeType(a.toLowerCase());
            MimeType mtb = new MimeType("application/atom+xml");
            MimeType mtc = new MimeType("application/atom+xml;type=entry");
            return MimeTypeHelper.isMatch(mta, mtc) || MimeTypeHelper.isMatch(mta, mtb) && MimeTypeHelper.isMatchType(mta.getParameter("type"), "entry");
        }
        catch (Exception exception) {
            return false;
        }
    }

    public static boolean isFeed(String a) {
        try {
            MimeType mta = new MimeType(a.toLowerCase());
            MimeType mtb = new MimeType("application/atom+xml");
            MimeType mtc = new MimeType("application/atom+xml;type=feed");
            return MimeTypeHelper.isMatch(mta, mtc) || MimeTypeHelper.isMatch(mta, mtb) && MimeTypeHelper.isMatchType(mta.getParameter("type"), "feed");
        }
        catch (Exception exception) {
            return false;
        }
    }

    public static boolean isXml(String a) {
        boolean answer;
        boolean bl = answer = MimeTypeHelper.isMatch("application/xml", a) || MimeTypeHelper.isMatch("text/xml", a);
        if (!answer) {
            try {
                MimeType mta = new MimeType(a);
                answer = ("application".equalsIgnoreCase(mta.getPrimaryType()) || "text".equalsIgnoreCase(mta.getPrimaryType())) && mta.getSubType().equals("xml") || mta.getSubType().endsWith("+xml");
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return answer;
    }

    public static boolean isText(String a) {
        return MimeTypeHelper.isMatch("text/*", a);
    }

    public static boolean isMimeType(String a) {
        boolean answer = false;
        try {
            new MimeType(a);
            answer = true;
        }
        catch (MimeTypeParseException e) {
            answer = false;
        }
        return answer;
    }

    public static <T extends Base> String getMimeType(T base) {
        String type = null;
        if (base instanceof Document) {
            Document doc = (Document)base;
            MimeType mt = doc.getContentType();
            type = mt != null ? mt.toString() : MimeTypeHelper.getMimeType(doc.getRoot());
        } else if (base instanceof Element) {
            Element el = (Element)base;
            if (el.getDocument() != null) {
                MimeType mt = el.getDocument().getContentType();
                String string = type = mt != null ? mt.toString() : null;
            }
            if (type == null) {
                if (el instanceof Feed) {
                    type = "application/atom+xml;type=feed";
                } else if (el instanceof Entry) {
                    type = "application/atom+xml;type=entry";
                } else if (el instanceof Service) {
                    type = "application/atomsvc+xml";
                } else if (el instanceof Categories) {
                    type = "application/atomcat+xml";
                }
            }
        }
        if (type == null) {
            type = base.getFactory().getMimeType(base);
        }
        return type != null ? type : "application/xml";
    }

    public static String[] condense(String ... types) {
        if (types.length <= 1) {
            return types;
        }
        ArrayList<String> res = new ArrayList<String>();
        Arrays.sort(types, MimeTypeHelper.getComparator());
        for (String t : types) {
            if (MimeTypeHelper.contains(t, res, true)) continue;
            res.add(t);
        }
        for (int n = 0; n < res.size(); ++n) {
            String t = (String)res.get(n);
            if (!MimeTypeHelper.contains(t, res, false)) continue;
            res.remove(t);
        }
        return res.toArray(new String[res.size()]);
    }

    private static boolean contains(String t1, List<String> t, boolean self) {
        if (self && t.contains(t1)) {
            return true;
        }
        for (String t2 : t) {
            int c = MimeTypeHelper.compare(t1, t2);
            if (c != 1) continue;
            return true;
        }
        return false;
    }

    public static Comparator<String> getComparator() {
        return new Comparator<String>(){

            @Override
            public int compare(String o1, String o2) {
                return MimeTypeHelper.compare(o1, o2);
            }
        };
    }

    public static int compare(MimeType mt1, MimeType mt2) {
        String st1 = mt1.getSubType();
        String st2 = mt2.getSubType();
        if (MimeTypeHelper.isMatch(mt1, mt2)) {
            if (st1.equals("*")) {
                return -1;
            }
            if (st2.equals("*")) {
                return 1;
            }
        }
        return 0;
    }

    public static int compare(String t1, String t2) {
        try {
            MimeType mt1 = new MimeType(t1);
            MimeType mt2 = new MimeType(t2);
            return MimeTypeHelper.compare(mt1, mt2);
        }
        catch (Exception exception) {
            return 0;
        }
    }

    public static boolean isMultipart(String a) {
        return MimeTypeHelper.isMatch("Multipart/Related", a);
    }
}

