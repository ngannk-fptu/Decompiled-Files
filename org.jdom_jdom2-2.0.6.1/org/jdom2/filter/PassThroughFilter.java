/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import org.jdom2.filter.AbstractFilter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class PassThroughFilter
extends AbstractFilter<Object> {
    private static final long serialVersionUID = 200L;

    PassThroughFilter() {
    }

    @Override
    public Object filter(Object content) {
        return content;
    }

    @Override
    public List<Object> filter(List<?> content) {
        if (content == null || content.isEmpty()) {
            return Collections.emptyList();
        }
        if (content instanceof RandomAccess) {
            return Collections.unmodifiableList(content);
        }
        ArrayList ret = new ArrayList();
        Iterator<?> it = content.iterator();
        while (it.hasNext()) {
            ret.add(it.next());
        }
        return Collections.unmodifiableList(ret);
    }
}

