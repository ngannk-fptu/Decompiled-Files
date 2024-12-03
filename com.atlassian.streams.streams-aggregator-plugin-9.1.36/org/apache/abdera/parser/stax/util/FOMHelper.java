/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Link;
import org.apache.abdera.parser.stax.util.FOMElementIterator;
import org.apache.abdera.parser.stax.util.FOMLinkIterator;
import org.apache.abdera.parser.stax.util.FOMList;
import org.apache.abdera.util.Constants;
import org.apache.axiom.util.UIDGenerator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMHelper
implements Constants {
    public static List<Category> getCategories(Element element, String scheme) {
        FOMElementIterator i = new FOMElementIterator(element, Category.class, SCHEME, scheme, null);
        return new FOMList<Category>(i);
    }

    public static List<Link> getLinks(Element element, String rel) {
        FOMLinkIterator i = new FOMLinkIterator(element, Link.class, REL, rel, "alternate");
        return new FOMList<Link>(i);
    }

    public static List<Link> getLinks(Element element, String ... rels) {
        ArrayList<Link> links = new ArrayList<Link>();
        for (String rel : rels) {
            List<Link> l = FOMHelper.getLinks(element, rel);
            links.addAll(l);
        }
        return links;
    }

    public static String generateUuid() {
        return UIDGenerator.generateURNString();
    }
}

