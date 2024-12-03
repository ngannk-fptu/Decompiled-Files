/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.predicate;

import javax.jcr.Item;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.predicate.Predicate;

public class NtFilePredicate
implements Predicate {
    public static final String NT_FILE = "nt:file";
    public static final String NT_HIERARCHYNODE = "nt:hierarchyNode";
    public static final String NT_RESOURCE = "nt:resource";
    public static final String JCR_CONTENT = "jcr:content";
    public static final String JCR_ENCODING = "jcr:encoding";
    public static final String JCR_MIMETYPE = "jcr:mimeType";
    public static final String JCR_PRIMARY_TYPE = "jcr:primaryType";
    protected final boolean ignoreEncoding;
    protected final boolean ignoreMimeType;

    public NtFilePredicate() {
        this(false, false);
    }

    public NtFilePredicate(boolean ignoreEncoding, boolean ignoreMimeType) {
        this.ignoreEncoding = ignoreEncoding;
        this.ignoreMimeType = ignoreMimeType;
    }

    public boolean isIgnoreEncoding() {
        return this.ignoreEncoding;
    }

    public boolean isIgnoreMimeType() {
        return this.ignoreMimeType;
    }

    @Override
    public boolean evaluate(Object item) {
        if (item instanceof Item && !((Item)item).isNode()) {
            try {
                Property prop = (Property)item;
                String dnt = prop.getDefinition().getDeclaringNodeType().getName();
                if (dnt.equals(NT_FILE) || dnt.equals(NT_HIERARCHYNODE)) {
                    return true;
                }
                if (this.ignoreEncoding && prop.getName().equals(JCR_ENCODING)) {
                    return false;
                }
                if (this.ignoreMimeType && prop.getName().equals(JCR_MIMETYPE)) {
                    return false;
                }
                if (prop.getParent().getName().equals(JCR_CONTENT) && dnt.equals(NT_RESOURCE)) {
                    return true;
                }
            }
            catch (RepositoryException re) {
                return false;
            }
        }
        return false;
    }
}

