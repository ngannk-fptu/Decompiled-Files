/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

import java.text.DateFormat;
import org.apache.jackrabbit.webdav.util.HttpDateFormat;
import org.apache.jackrabbit.webdav.xml.Namespace;

public interface DavConstants {
    public static final Namespace NAMESPACE = Namespace.getNamespace("D", "DAV:");
    public static final String HEADER_DAV = "DAV";
    public static final String HEADER_DESTINATION = "Destination";
    public static final String HEADER_IF = "If";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_CONTENT_LANGUAGE = "Content-Language";
    public static final String HEADER_ETAG = "ETag";
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";
    public static final String HEADER_LOCK_TOKEN = "Lock-Token";
    public static final String OPAQUE_LOCK_TOKEN_PREFIX = "opaquelocktoken:";
    public static final String HEADER_TIMEOUT = "Timeout";
    public static final String TIMEOUT_INFINITE = "Infinite";
    public static final long INFINITE_TIMEOUT = Integer.MAX_VALUE;
    public static final long UNDEFINED_TIMEOUT = Integer.MIN_VALUE;
    public static final String HEADER_OVERWRITE = "Overwrite";
    public static final String HEADER_DEPTH = "Depth";
    public static final String DEPTH_INFINITY_S = "infinity";
    public static final int DEPTH_INFINITY = Integer.MAX_VALUE;
    public static final int DEPTH_0 = 0;
    public static final int DEPTH_1 = 1;
    public static final String XML_ALLPROP = "allprop";
    public static final String XML_COLLECTION = "collection";
    public static final String XML_DST = "dst";
    public static final String XML_HREF = "href";
    public static final String XML_INCLUDE = "include";
    public static final String XML_KEEPALIVE = "keepalive";
    public static final String XML_LINK = "link";
    public static final String XML_MULTISTATUS = "multistatus";
    public static final String XML_OMIT = "omit";
    public static final String XML_PROP = "prop";
    public static final String XML_PROPERTYBEHAVIOR = "propertybehavior";
    public static final String XML_PROPERTYUPDATE = "propertyupdate";
    public static final String XML_PROPFIND = "propfind";
    public static final String XML_PROPNAME = "propname";
    public static final String XML_PROPSTAT = "propstat";
    public static final String XML_REMOVE = "remove";
    public static final String XML_RESPONSE = "response";
    public static final String XML_RESPONSEDESCRIPTION = "responsedescription";
    public static final String XML_SET = "set";
    public static final String XML_SOURCE = "source";
    public static final String XML_STATUS = "status";
    public static final String XML_ACTIVELOCK = "activelock";
    public static final String XML_DEPTH = "depth";
    public static final String XML_LOCKTOKEN = "locktoken";
    public static final String XML_TIMEOUT = "timeout";
    public static final String XML_LOCKSCOPE = "lockscope";
    public static final String XML_EXCLUSIVE = "exclusive";
    public static final String XML_SHARED = "shared";
    public static final String XML_LOCKENTRY = "lockentry";
    public static final String XML_LOCKINFO = "lockinfo";
    public static final String XML_LOCKTYPE = "locktype";
    public static final String XML_WRITE = "write";
    public static final String XML_OWNER = "owner";
    public static final String XML_LOCKROOT = "lockroot";
    public static final String PROPERTY_CREATIONDATE = "creationdate";
    public static final String PROPERTY_DISPLAYNAME = "displayname";
    public static final String PROPERTY_GETCONTENTLANGUAGE = "getcontentlanguage";
    public static final String PROPERTY_GETCONTENTLENGTH = "getcontentlength";
    public static final String PROPERTY_GETCONTENTTYPE = "getcontenttype";
    public static final String PROPERTY_GETETAG = "getetag";
    public static final String PROPERTY_GETLASTMODIFIED = "getlastmodified";
    public static final String PROPERTY_LOCKDISCOVERY = "lockdiscovery";
    public static final String PROPERTY_RESOURCETYPE = "resourcetype";
    public static final String PROPERTY_SOURCE = "source";
    public static final String PROPERTY_SUPPORTEDLOCK = "supportedlock";
    public static final int PROPFIND_BY_PROPERTY = 0;
    public static final int PROPFIND_ALL_PROP = 1;
    public static final int PROPFIND_PROPERTY_NAMES = 2;
    public static final int PROPFIND_ALL_PROP_INCLUDE = 3;
    public static final long UNDEFINED_TIME = -1L;
    public static final DateFormat modificationDateFormat = HttpDateFormat.modificationDateFormat();
    public static final DateFormat creationDateFormat = HttpDateFormat.creationDateFormat();
}

