/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.impl.values.NamespaceManager;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

public abstract class JavaBase64Holder
extends XmlObjectBase {
    protected byte[] _value;
    protected boolean _hashcached = false;
    protected int hashcode = 0;
    protected static final MessageDigest md5;

    @Override
    public SchemaType schemaType() {
        return BuiltinSchemaTypeSystem.ST_BASE_64_BINARY;
    }

    @Override
    protected String compute_text(NamespaceManager nsm) {
        return Base64.getEncoder().encodeToString(this._value);
    }

    @Override
    protected void set_text(String s) {
        this._hashcached = false;
        this._value = this._validateOnSet() ? JavaBase64Holder.validateLexical(s, this.schemaType(), _voorVc) : JavaBase64Holder.lex(s, _voorVc);
    }

    @Override
    protected void set_nil() {
        this._hashcached = false;
        this._value = null;
    }

    public static byte[] lex(String v, ValidationContext c) {
        try {
            return Base64.getMimeDecoder().decode(v);
        }
        catch (IllegalArgumentException e) {
            c.invalid("base64Binary", new Object[]{"not encoded properly"});
            return null;
        }
    }

    public static byte[] validateLexical(String v, SchemaType sType, ValidationContext context) {
        byte[] bytes = JavaBase64Holder.lex(v, context);
        if (bytes == null) {
            return null;
        }
        if (!sType.matchPatternFacet(v)) {
            context.invalid("cvc-datatype-valid.1.1b", new Object[]{"base 64", QNameHelper.readable(sType)});
            return null;
        }
        return bytes;
    }

    @Override
    public byte[] getByteArrayValue() {
        this.check_dated();
        if (this._value == null) {
            return null;
        }
        byte[] result = new byte[this._value.length];
        System.arraycopy(this._value, 0, result, 0, this._value.length);
        return result;
    }

    @Override
    protected void set_ByteArray(byte[] ba) {
        this._hashcached = false;
        this._value = new byte[ba.length];
        System.arraycopy(ba, 0, this._value, 0, ba.length);
    }

    @Override
    protected boolean equal_to(XmlObject i) {
        byte[] ival = ((XmlBase64Binary)i).getByteArrayValue();
        return Arrays.equals(this._value, ival);
    }

    @Override
    protected int value_hash_code() {
        if (this._hashcached) {
            return this.hashcode;
        }
        this._hashcached = true;
        if (this._value == null) {
            this.hashcode = 0;
            return 0;
        }
        byte[] res = md5.digest(this._value);
        this.hashcode = res[0] << 24 | res[1] << 16 | res[2] << 8 | res[3];
        return this.hashcode;
    }

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Cannot find MD5 hash Algorithm");
        }
    }
}

