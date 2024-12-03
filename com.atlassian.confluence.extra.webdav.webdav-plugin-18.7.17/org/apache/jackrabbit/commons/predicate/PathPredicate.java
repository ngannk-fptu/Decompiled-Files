/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.predicate;

import java.util.regex.Pattern;
import javax.jcr.Item;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.predicate.Predicate;

public class PathPredicate
implements Predicate {
    protected final Pattern regex;

    public PathPredicate(String pattern) {
        String suffix = "";
        String prefix = "";
        if (pattern.endsWith("/**")) {
            suffix = "/.*";
            pattern = pattern.substring(0, pattern.length() - 3);
        } else if (pattern.endsWith("*")) {
            suffix = "[^/]*$";
            pattern = pattern.substring(0, pattern.length() - 1);
        }
        if (pattern.charAt(0) != '/') {
            prefix = "^.*/";
        }
        pattern = prefix + pattern.replaceAll("\\.", "\\\\.") + suffix;
        this.regex = Pattern.compile(pattern);
    }

    @Override
    public boolean evaluate(Object item) {
        if (item instanceof Item) {
            try {
                return this.regex.matcher(((Item)item).getPath()).matches();
            }
            catch (RepositoryException re) {
                return false;
            }
        }
        return false;
    }
}

