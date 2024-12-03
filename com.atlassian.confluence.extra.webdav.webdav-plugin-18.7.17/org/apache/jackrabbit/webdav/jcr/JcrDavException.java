/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.MergeException;
import javax.jcr.NamespaceException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.PathNotFoundException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.version.VersionException;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JcrDavException
extends DavException {
    private static Logger log = LoggerFactory.getLogger(JcrDavException.class);
    private static Map<Class<? extends Throwable>, Integer> codeMap = new LinkedHashMap<Class<? extends Throwable>, Integer>(20);
    private Class<? extends Throwable> exceptionClass;

    private static int lookupErrorCode(Class<? extends Throwable> exceptionClass) {
        Integer code = codeMap.get(exceptionClass);
        if (code == null) {
            for (Class<? extends Throwable> jcrExceptionClass : codeMap.keySet()) {
                if (!jcrExceptionClass.isAssignableFrom(exceptionClass)) continue;
                code = codeMap.get(jcrExceptionClass);
                break;
            }
            if (code == null) {
                code = 403;
            }
        }
        return code;
    }

    public JcrDavException(Throwable cause, int errorCode) {
        super(errorCode, cause.getMessage(), cause, null);
        this.exceptionClass = cause.getClass();
        if (log.isDebugEnabled()) {
            log.debug("Handling exception with error code " + errorCode, cause);
        }
    }

    public JcrDavException(RepositoryException cause) {
        this(cause, JcrDavException.lookupErrorCode(cause.getClass()));
    }

    @Override
    public boolean hasErrorCondition() {
        return true;
    }

    @Override
    public Element toXml(Document document) {
        Element error = DomUtil.createElement(document, "error", DavConstants.NAMESPACE);
        Element excep = DomUtil.createElement(document, "exception", ItemResourceConstants.NAMESPACE);
        DomUtil.addChildElement(excep, "class", ItemResourceConstants.NAMESPACE, this.exceptionClass.getName());
        DomUtil.addChildElement(excep, "message", ItemResourceConstants.NAMESPACE, this.getMessage());
        error.appendChild(excep);
        return error;
    }

    static {
        codeMap.put(AccessDeniedException.class, 403);
        codeMap.put(ConstraintViolationException.class, 409);
        codeMap.put(InvalidItemStateException.class, 409);
        codeMap.put(InvalidSerializedDataException.class, 400);
        codeMap.put(InvalidQueryException.class, 400);
        codeMap.put(ItemExistsException.class, 409);
        codeMap.put(ItemNotFoundException.class, 403);
        codeMap.put(LockException.class, 423);
        codeMap.put(MergeException.class, 409);
        codeMap.put(NamespaceException.class, 409);
        codeMap.put(NoSuchNodeTypeException.class, 409);
        codeMap.put(NoSuchWorkspaceException.class, 409);
        codeMap.put(PathNotFoundException.class, 409);
        codeMap.put(ReferentialIntegrityException.class, 409);
        codeMap.put(LoginException.class, 401);
        codeMap.put(UnsupportedRepositoryOperationException.class, 501);
        codeMap.put(ValueFormatException.class, 409);
        codeMap.put(VersionException.class, 409);
        codeMap.put(RepositoryException.class, 403);
    }
}

