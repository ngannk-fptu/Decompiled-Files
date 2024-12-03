/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.jcr.lock;

import javax.jcr.RepositoryException;
import javax.jcr.lock.Lock;
import org.apache.jackrabbit.util.Text;

public class LockTokenMapper {
    private static final String OL = "opaquelocktoken:";
    private static final String SESSIONSCOPED = "4403ef44-4124-11e1-b965-00059a3c7a00";
    private static final String OPENSCOPED = "dccce564-412e-11e1-b969-00059a3c7a00";
    private static final String SESSPREFIX = "opaquelocktoken:4403ef44-4124-11e1-b965-00059a3c7a00:";
    private static final String OPENPREFIX = "opaquelocktoken:dccce564-412e-11e1-b969-00059a3c7a00:";

    public static String getDavLocktoken(Lock lock) throws RepositoryException {
        String jcrLockToken = lock.getLockToken();
        if (jcrLockToken == null) {
            return SESSPREFIX + Text.escape(lock.getNode().getIdentifier());
        }
        return OPENPREFIX + Text.escape(jcrLockToken);
    }

    public static String getJcrLockToken(String token) throws RepositoryException {
        if (token.startsWith(OPENPREFIX)) {
            return Text.unescape(token.substring(OPENPREFIX.length()));
        }
        throw new RepositoryException("not a token for an open-scoped JCR lock: " + token);
    }

    public static boolean isForSessionScopedLock(String token) {
        return token.startsWith(SESSPREFIX);
    }
}

