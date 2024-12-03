/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.session;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.catalina.valves.CrawlerSessionManagerValve;

public class Constants {
    public static final Set<String> excludedAttributeNames;

    static {
        HashSet<String> names = new HashSet<String>();
        names.add("javax.security.auth.subject");
        names.add(CrawlerSessionManagerValve.class.getName());
        excludedAttributeNames = Collections.unmodifiableSet(names);
    }
}

