/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JsonMappingException
extends JsonProcessingException {
    private static final long serialVersionUID = 1L;
    static final int MAX_REFS_TO_LIST = 1000;
    protected LinkedList<Reference> _path;

    public JsonMappingException(String msg) {
        super(msg);
    }

    public JsonMappingException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }

    public JsonMappingException(String msg, JsonLocation loc) {
        super(msg, loc);
    }

    public JsonMappingException(String msg, JsonLocation loc, Throwable rootCause) {
        super(msg, loc, rootCause);
    }

    public static JsonMappingException from(JsonParser jp, String msg) {
        return new JsonMappingException(msg, jp.getTokenLocation());
    }

    public static JsonMappingException from(JsonParser jp, String msg, Throwable problem) {
        return new JsonMappingException(msg, jp.getTokenLocation(), problem);
    }

    public static JsonMappingException wrapWithPath(Throwable src, Object refFrom, String refFieldName) {
        return JsonMappingException.wrapWithPath(src, new Reference(refFrom, refFieldName));
    }

    public static JsonMappingException wrapWithPath(Throwable src, Object refFrom, int index) {
        return JsonMappingException.wrapWithPath(src, new Reference(refFrom, index));
    }

    public static JsonMappingException wrapWithPath(Throwable src, Reference ref) {
        JsonMappingException jme;
        if (src instanceof JsonMappingException) {
            jme = (JsonMappingException)src;
        } else {
            String msg = src.getMessage();
            if (msg == null || msg.length() == 0) {
                msg = "(was " + src.getClass().getName() + ")";
            }
            jme = new JsonMappingException(msg, null, src);
        }
        jme.prependPath(ref);
        return jme;
    }

    public List<Reference> getPath() {
        if (this._path == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this._path);
    }

    public void prependPath(Object referrer, String fieldName) {
        Reference ref = new Reference(referrer, fieldName);
        this.prependPath(ref);
    }

    public void prependPath(Object referrer, int index) {
        Reference ref = new Reference(referrer, index);
        this.prependPath(ref);
    }

    public void prependPath(Reference r) {
        if (this._path == null) {
            this._path = new LinkedList();
        }
        if (this._path.size() < 1000) {
            this._path.addFirst(r);
        }
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (this._path == null) {
            return msg;
        }
        StringBuilder sb = msg == null ? new StringBuilder() : new StringBuilder(msg);
        sb.append(" (through reference chain: ");
        this._appendPathDesc(sb);
        sb.append(')');
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + ": " + this.getMessage();
    }

    protected void _appendPathDesc(StringBuilder sb) {
        Iterator it = this._path.iterator();
        while (it.hasNext()) {
            sb.append(((Reference)it.next()).toString());
            if (!it.hasNext()) continue;
            sb.append("->");
        }
    }

    public static class Reference
    implements Serializable {
        private static final long serialVersionUID = 1L;
        protected Object _from;
        protected String _fieldName;
        protected int _index = -1;

        protected Reference() {
        }

        public Reference(Object from) {
            this._from = from;
        }

        public Reference(Object from, String fieldName) {
            this._from = from;
            if (fieldName == null) {
                throw new NullPointerException("Can not pass null fieldName");
            }
            this._fieldName = fieldName;
        }

        public Reference(Object from, int index) {
            this._from = from;
            this._index = index;
        }

        public void setFrom(Object o) {
            this._from = o;
        }

        public void setFieldName(String n) {
            this._fieldName = n;
        }

        public void setIndex(int ix) {
            this._index = ix;
        }

        public Object getFrom() {
            return this._from;
        }

        public String getFieldName() {
            return this._fieldName;
        }

        public int getIndex() {
            return this._index;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            Class<?> cls = this._from instanceof Class ? (Class<?>)this._from : this._from.getClass();
            Package pkg = cls.getPackage();
            if (pkg != null) {
                sb.append(pkg.getName());
                sb.append('.');
            }
            sb.append(cls.getSimpleName());
            sb.append('[');
            if (this._fieldName != null) {
                sb.append('\"');
                sb.append(this._fieldName);
                sb.append('\"');
            } else if (this._index >= 0) {
                sb.append(this._index);
            } else {
                sb.append('?');
            }
            sb.append(']');
            return sb.toString();
        }
    }
}

