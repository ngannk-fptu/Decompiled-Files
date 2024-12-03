/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Integers;

public class SkeinParameters
implements CipherParameters {
    public static final int PARAM_TYPE_KEY = 0;
    public static final int PARAM_TYPE_CONFIG = 4;
    public static final int PARAM_TYPE_PERSONALISATION = 8;
    public static final int PARAM_TYPE_PUBLIC_KEY = 12;
    public static final int PARAM_TYPE_KEY_IDENTIFIER = 16;
    public static final int PARAM_TYPE_NONCE = 20;
    public static final int PARAM_TYPE_MESSAGE = 48;
    public static final int PARAM_TYPE_OUTPUT = 63;
    private Hashtable parameters;

    public SkeinParameters() {
        this(new Hashtable());
    }

    private SkeinParameters(Hashtable parameters) {
        this.parameters = parameters;
    }

    public Hashtable getParameters() {
        return this.parameters;
    }

    public byte[] getKey() {
        return (byte[])this.parameters.get(Integers.valueOf(0));
    }

    public byte[] getPersonalisation() {
        return (byte[])this.parameters.get(Integers.valueOf(8));
    }

    public byte[] getPublicKey() {
        return (byte[])this.parameters.get(Integers.valueOf(12));
    }

    public byte[] getKeyIdentifier() {
        return (byte[])this.parameters.get(Integers.valueOf(16));
    }

    public byte[] getNonce() {
        return (byte[])this.parameters.get(Integers.valueOf(20));
    }

    public static class Builder {
        private Hashtable parameters = new Hashtable();

        public Builder() {
        }

        public Builder(Hashtable paramsMap) {
            Enumeration keys = paramsMap.keys();
            while (keys.hasMoreElements()) {
                Integer key = (Integer)keys.nextElement();
                this.parameters.put(key, paramsMap.get(key));
            }
        }

        public Builder(SkeinParameters params) {
            Enumeration keys = params.parameters.keys();
            while (keys.hasMoreElements()) {
                Integer key = (Integer)keys.nextElement();
                this.parameters.put(key, params.parameters.get(key));
            }
        }

        public Builder set(int type, byte[] value) {
            if (value == null) {
                throw new IllegalArgumentException("Parameter value must not be null.");
            }
            if (type != 0 && (type < 4 || type >= 63 || type == 48)) {
                throw new IllegalArgumentException("Parameter types must be in the range 0,5..47,49..62.");
            }
            if (type == 4) {
                throw new IllegalArgumentException("Parameter type 4 is reserved for internal use.");
            }
            this.parameters.put(Integers.valueOf(type), value);
            return this;
        }

        public Builder setKey(byte[] key) {
            return this.set(0, key);
        }

        public Builder setPersonalisation(byte[] personalisation) {
            return this.set(8, personalisation);
        }

        public Builder setPersonalisation(Date date, String emailAddress, String distinguisher) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                OutputStreamWriter out = new OutputStreamWriter((OutputStream)bout, "UTF-8");
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                out.write(format.format(date));
                out.write(" ");
                out.write(emailAddress);
                out.write(" ");
                out.write(distinguisher);
                out.close();
                return this.set(8, bout.toByteArray());
            }
            catch (IOException e) {
                throw new IllegalStateException("Byte I/O failed: " + e);
            }
        }

        public Builder setPersonalisation(Date date, Locale dateLocale, String emailAddress, String distinguisher) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                OutputStreamWriter out = new OutputStreamWriter((OutputStream)bout, "UTF-8");
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", dateLocale);
                out.write(format.format(date));
                out.write(" ");
                out.write(emailAddress);
                out.write(" ");
                out.write(distinguisher);
                out.close();
                return this.set(8, bout.toByteArray());
            }
            catch (IOException e) {
                throw new IllegalStateException("Byte I/O failed: " + e);
            }
        }

        public Builder setPublicKey(byte[] publicKey) {
            return this.set(12, publicKey);
        }

        public Builder setKeyIdentifier(byte[] keyIdentifier) {
            return this.set(16, keyIdentifier);
        }

        public Builder setNonce(byte[] nonce) {
            return this.set(20, nonce);
        }

        public SkeinParameters build() {
            return new SkeinParameters(this.parameters);
        }
    }
}

