/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.Map;

public class DynaProperty
implements Serializable {
    private static final int BOOLEAN_TYPE = 1;
    private static final int BYTE_TYPE = 2;
    private static final int CHAR_TYPE = 3;
    private static final int DOUBLE_TYPE = 4;
    private static final int FLOAT_TYPE = 5;
    private static final int INT_TYPE = 6;
    private static final int LONG_TYPE = 7;
    private static final int SHORT_TYPE = 8;
    protected String name = null;
    protected transient Class<?> type = null;
    protected transient Class<?> contentType;

    public DynaProperty(String name) {
        this(name, Object.class);
    }

    public DynaProperty(String name, Class<?> type) {
        this.name = name;
        this.type = type;
        if (type != null && type.isArray()) {
            this.contentType = type.getComponentType();
        }
    }

    public DynaProperty(String name, Class<?> type, Class<?> contentType) {
        this.name = name;
        this.type = type;
        this.contentType = contentType;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getType() {
        return this.type;
    }

    public Class<?> getContentType() {
        return this.contentType;
    }

    public boolean isIndexed() {
        if (this.type == null) {
            return false;
        }
        if (this.type.isArray()) {
            return true;
        }
        return List.class.isAssignableFrom(this.type);
    }

    public boolean isMapped() {
        if (this.type == null) {
            return false;
        }
        return Map.class.isAssignableFrom(this.type);
    }

    public boolean equals(Object obj) {
        boolean result = false;
        boolean bl = result = obj == this;
        if (!result && obj instanceof DynaProperty) {
            DynaProperty that = (DynaProperty)obj;
            result = (this.name == null ? that.name == null : this.name.equals(that.name)) && (this.type == null ? that.type == null : this.type.equals(that.type)) && (this.contentType == null ? that.contentType == null : this.contentType.equals(that.contentType));
        }
        return result;
    }

    public int hashCode() {
        int result = 1;
        result = result * 31 + (this.name == null ? 0 : this.name.hashCode());
        result = result * 31 + (this.type == null ? 0 : this.type.hashCode());
        result = result * 31 + (this.contentType == null ? 0 : this.contentType.hashCode());
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("DynaProperty[name=");
        sb.append(this.name);
        sb.append(",type=");
        sb.append(this.type);
        if (this.isMapped() || this.isIndexed()) {
            sb.append(" <").append(this.contentType).append(">");
        }
        sb.append("]");
        return sb.toString();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        this.writeAnyClass(this.type, out);
        if (this.isMapped() || this.isIndexed()) {
            this.writeAnyClass(this.contentType, out);
        }
        out.defaultWriteObject();
    }

    private void writeAnyClass(Class<?> clazz, ObjectOutputStream out) throws IOException {
        int primitiveType = 0;
        if (Boolean.TYPE.equals(clazz)) {
            primitiveType = 1;
        } else if (Byte.TYPE.equals(clazz)) {
            primitiveType = 2;
        } else if (Character.TYPE.equals(clazz)) {
            primitiveType = 3;
        } else if (Double.TYPE.equals(clazz)) {
            primitiveType = 4;
        } else if (Float.TYPE.equals(clazz)) {
            primitiveType = 5;
        } else if (Integer.TYPE.equals(clazz)) {
            primitiveType = 6;
        } else if (Long.TYPE.equals(clazz)) {
            primitiveType = 7;
        } else if (Short.TYPE.equals(clazz)) {
            primitiveType = 8;
        }
        if (primitiveType == 0) {
            out.writeBoolean(false);
            out.writeObject(clazz);
        } else {
            out.writeBoolean(true);
            out.writeInt(primitiveType);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.type = this.readAnyClass(in);
        if (this.isMapped() || this.isIndexed()) {
            this.contentType = this.readAnyClass(in);
        }
        in.defaultReadObject();
    }

    private Class<?> readAnyClass(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if (in.readBoolean()) {
            switch (in.readInt()) {
                case 1: {
                    return Boolean.TYPE;
                }
                case 2: {
                    return Byte.TYPE;
                }
                case 3: {
                    return Character.TYPE;
                }
                case 4: {
                    return Double.TYPE;
                }
                case 5: {
                    return Float.TYPE;
                }
                case 6: {
                    return Integer.TYPE;
                }
                case 7: {
                    return Long.TYPE;
                }
                case 8: {
                    return Short.TYPE;
                }
            }
            throw new StreamCorruptedException("Invalid primitive type. Check version of beanutils used to serialize is compatible.");
        }
        return (Class)in.readObject();
    }
}

