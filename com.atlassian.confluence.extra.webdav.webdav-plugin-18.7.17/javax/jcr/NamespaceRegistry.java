/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.AccessDeniedException;
import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;

public interface NamespaceRegistry {
    public static final String PREFIX_JCR = "jcr";
    public static final String PREFIX_NT = "nt";
    public static final String PREFIX_MIX = "mix";
    public static final String PREFIX_XML = "xml";
    public static final String PREFIX_EMPTY = "";
    public static final String NAMESPACE_JCR = "http://www.jcp.org/jcr/1.0";
    public static final String NAMESPACE_NT = "http://www.jcp.org/jcr/nt/1.0";
    public static final String NAMESPACE_MIX = "http://www.jcp.org/jcr/mix/1.0";
    public static final String NAMESPACE_XML = "http://www.w3.org/XML/1998/namespace";
    public static final String NAMESPACE_EMPTY = "";

    public void registerNamespace(String var1, String var2) throws NamespaceException, UnsupportedRepositoryOperationException, AccessDeniedException, RepositoryException;

    public void unregisterNamespace(String var1) throws NamespaceException, UnsupportedRepositoryOperationException, AccessDeniedException, RepositoryException;

    public String[] getPrefixes() throws RepositoryException;

    public String[] getURIs() throws RepositoryException;

    public String getURI(String var1) throws NamespaceException, RepositoryException;

    public String getPrefix(String var1) throws NamespaceException, RepositoryException;
}

