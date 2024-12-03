/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.transaction;

import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.xml.Namespace;

public interface TransactionConstants {
    public static final Namespace NAMESPACE = Namespace.getNamespace("dcr", "http://www.day.com/jcr/webdav/1.0");
    public static final String HEADER_TRANSACTIONID = "TransactionId";
    public static final String XML_TRANSACTION = "transaction";
    public static final String XML_GLOBAL = "global";
    public static final String XML_LOCAL = "local";
    public static final String XML_TRANSACTIONINFO = "transactioninfo";
    public static final String XML_TRANSACTIONSTATUS = "transactionstatus";
    public static final String XML_COMMIT = "commit";
    public static final String XML_ROLLBACK = "rollback";
    public static final Type TRANSACTION = Type.create("transaction", NAMESPACE);
    public static final Scope LOCAL = Scope.create("local", NAMESPACE);
    public static final Scope GLOBAL = Scope.create("global", NAMESPACE);
}

