/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class ParameterizedTypeImpl
implements ParameterizedType {
    private final Type[] actualTypeArguments;
    private final Class<?> rawType;
    private final Type ownerType;
    public static final Type MAP_OF_UNKNOWN_TYPE = new ParameterizedTypeImpl(Map.class, new Type[]{Object.class, Object.class}, null);
    public static final Type LIST_OF_UNKNOWN_TYPE = new ParameterizedTypeImpl(List.class, new Type[]{Object.class}, null);

    ParameterizedTypeImpl(Class<?> rawType, Type[] actualTypeArguments, Type ownerType) {
        this.actualTypeArguments = actualTypeArguments;
        this.rawType = rawType;
        Class<?> clazz = this.ownerType = ownerType != null ? ownerType : rawType.getDeclaringClass();
        if (rawType.getTypeParameters().length != actualTypeArguments.length) {
            throw new IllegalArgumentException("Argument length mismatch");
        }
    }

    @Override
    public Type[] getActualTypeArguments() {
        return (Type[])this.actualTypeArguments.clone();
    }

    @Override
    public Class<?> getRawType() {
        return this.rawType;
    }

    @Override
    public Type getOwnerType() {
        return this.ownerType;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType other = (ParameterizedType)obj;
        return Objects.equals(this.ownerType, other.getOwnerType()) && Objects.equals(this.rawType, other.getRawType()) && Arrays.equals(this.actualTypeArguments, other.getActualTypeArguments());
    }

    public int hashCode() {
        return Arrays.hashCode(this.actualTypeArguments) ^ Objects.hashCode(this.ownerType) ^ Objects.hashCode(this.rawType);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.ownerType == null) {
            buf.append(this.rawType.getName());
        } else {
            if (this.ownerType instanceof Class) {
                buf.append(((Class)this.ownerType).getName());
            } else {
                buf.append(this.ownerType);
            }
            buf.append('$');
            if (this.ownerType instanceof ParameterizedTypeImpl) {
                String simpleName = this.rawType.getName().replace(((ParameterizedTypeImpl)this.ownerType).rawType.getName() + "$", "");
                buf.append(simpleName);
            } else {
                buf.append(this.rawType.getSimpleName());
            }
        }
        if (this.actualTypeArguments != null && this.actualTypeArguments.length > 0) {
            buf.append('<');
            boolean first = true;
            for (Type t : this.actualTypeArguments) {
                if (first) {
                    first = false;
                } else {
                    buf.append(", ");
                }
                buf.append(t.toString());
            }
            buf.append('>');
        }
        return buf.toString();
    }
}

