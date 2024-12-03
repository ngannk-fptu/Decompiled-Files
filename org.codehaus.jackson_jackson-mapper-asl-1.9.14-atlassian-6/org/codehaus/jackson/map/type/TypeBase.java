/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.type;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializableWithType;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TypeBase
extends JavaType
implements JsonSerializableWithType {
    volatile String _canonicalName;

    @Deprecated
    protected TypeBase(Class<?> raw, int hash) {
        super(raw, hash);
    }

    protected TypeBase(Class<?> raw, int hash, Object valueHandler, Object typeHandler) {
        super(raw, hash);
        this._valueHandler = valueHandler;
        this._typeHandler = typeHandler;
    }

    public String toCanonical() {
        String str = this._canonicalName;
        if (str == null) {
            str = this.buildCanonicalName();
        }
        return str;
    }

    protected abstract String buildCanonicalName();

    public abstract StringBuilder getGenericSignature(StringBuilder var1);

    public abstract StringBuilder getErasedSignature(StringBuilder var1);

    public <T> T getValueHandler() {
        return (T)this._valueHandler;
    }

    public <T> T getTypeHandler() {
        return (T)this._typeHandler;
    }

    @Override
    public void serializeWithType(JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
        typeSer.writeTypePrefixForScalar(this, jgen);
        this.serialize(jgen, provider);
        typeSer.writeTypeSuffixForScalar(this, jgen);
    }

    @Override
    public void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeString(this.toCanonical());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static StringBuilder _classSignature(Class<?> cls, StringBuilder sb, boolean trailingSemicolon) {
        if (cls.isPrimitive()) {
            if (cls == Boolean.TYPE) {
                sb.append('Z');
                return sb;
            } else if (cls == Byte.TYPE) {
                sb.append('B');
                return sb;
            } else if (cls == Short.TYPE) {
                sb.append('S');
                return sb;
            } else if (cls == Character.TYPE) {
                sb.append('C');
                return sb;
            } else if (cls == Integer.TYPE) {
                sb.append('I');
                return sb;
            } else if (cls == Long.TYPE) {
                sb.append('J');
                return sb;
            } else if (cls == Float.TYPE) {
                sb.append('F');
                return sb;
            } else if (cls == Double.TYPE) {
                sb.append('D');
                return sb;
            } else {
                if (cls != Void.TYPE) throw new IllegalStateException("Unrecognized primitive type: " + cls.getName());
                sb.append('V');
            }
            return sb;
        } else {
            sb.append('L');
            String name = cls.getName();
            int len = name.length();
            for (int i = 0; i < len; ++i) {
                char c = name.charAt(i);
                if (c == '.') {
                    c = '/';
                }
                sb.append(c);
            }
            if (!trailingSemicolon) return sb;
            sb.append(';');
        }
        return sb;
    }
}

