/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.mapping;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.Streamable;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class PropertyPath
implements Streamable<PropertyPath> {
    private static final String PARSE_DEPTH_EXCEEDED = "Trying to parse a path with depth greater than 1000! This has been disabled for security reasons to prevent parsing overflows.";
    private static final String DELIMITERS = "_\\.";
    private static final Pattern SPLITTER = Pattern.compile("(?:[%s]?([%s]*?[^%s]+))".replaceAll("%s", "_\\."));
    private static final Pattern SPLITTER_FOR_QUOTED = Pattern.compile("(?:[%s]?([%s]*?[^%s]+))".replaceAll("%s", "\\."));
    private static final Pattern NESTED_PROPERTY_PATTERN = Pattern.compile("\\p{Lu}[\\p{Ll}\\p{Nd}]*$");
    private static final Map<Key, PropertyPath> cache = new ConcurrentReferenceHashMap();
    private final TypeInformation<?> owningType;
    private final String name;
    private final TypeInformation<?> typeInformation;
    private final TypeInformation<?> actualTypeInformation;
    private final boolean isCollection;
    @Nullable
    private PropertyPath next;

    PropertyPath(String name, Class<?> owningType) {
        this(name, ClassTypeInformation.from(owningType), Collections.emptyList());
    }

    PropertyPath(String name, TypeInformation<?> owningType, List<PropertyPath> base) {
        Assert.hasText((String)name, (String)"Name must not be null or empty!");
        Assert.notNull(owningType, (String)"Owning type must not be null!");
        Assert.notNull(base, (String)"Previously found properties must not be null!");
        String propertyName = Introspector.decapitalize(name);
        TypeInformation<?> propertyType = owningType.getProperty(propertyName);
        if (propertyType == null) {
            throw new PropertyReferenceException(propertyName, owningType, base);
        }
        this.owningType = owningType;
        this.typeInformation = propertyType;
        this.isCollection = propertyType.isCollectionLike();
        this.name = propertyName;
        this.actualTypeInformation = propertyType.getActualType() == null ? propertyType : propertyType.getRequiredActualType();
    }

    public TypeInformation<?> getOwningType() {
        return this.owningType;
    }

    public String getSegment() {
        return this.name;
    }

    public PropertyPath getLeafProperty() {
        PropertyPath result = this;
        while (result.hasNext()) {
            result = result.requiredNext();
        }
        return result;
    }

    public Class<?> getLeafType() {
        return this.getLeafProperty().getType();
    }

    public Class<?> getType() {
        return this.actualTypeInformation.getType();
    }

    public TypeInformation<?> getTypeInformation() {
        return this.typeInformation;
    }

    @Nullable
    public PropertyPath next() {
        return this.next;
    }

    public boolean hasNext() {
        return this.next != null;
    }

    public String toDotPath() {
        if (this.hasNext()) {
            return this.getSegment() + "." + this.requiredNext().toDotPath();
        }
        return this.getSegment();
    }

    public boolean isCollection() {
        return this.isCollection;
    }

    public PropertyPath nested(String path) {
        Assert.hasText((String)path, (String)"Path must not be null or empty!");
        String lookup = this.toDotPath().concat(".").concat(path);
        return PropertyPath.from(lookup, this.owningType);
    }

    @Override
    public Iterator<PropertyPath> iterator() {
        return new Iterator<PropertyPath>(){
            @Nullable
            private PropertyPath current;
            {
                this.current = PropertyPath.this;
            }

            @Override
            public boolean hasNext() {
                return this.current != null;
            }

            @Override
            @Nullable
            public PropertyPath next() {
                PropertyPath result = this.current;
                if (result == null) {
                    return null;
                }
                this.current = result.next();
                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertyPath)) {
            return false;
        }
        PropertyPath that = (PropertyPath)o;
        if (this.isCollection != that.isCollection) {
            return false;
        }
        if (!ObjectUtils.nullSafeEquals(this.owningType, that.owningType)) {
            return false;
        }
        if (!ObjectUtils.nullSafeEquals((Object)this.name, (Object)that.name)) {
            return false;
        }
        if (!ObjectUtils.nullSafeEquals(this.typeInformation, that.typeInformation)) {
            return false;
        }
        if (!ObjectUtils.nullSafeEquals(this.actualTypeInformation, that.actualTypeInformation)) {
            return false;
        }
        return ObjectUtils.nullSafeEquals((Object)this.next, (Object)that.next);
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.owningType);
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.name);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.typeInformation);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.actualTypeInformation);
        result = 31 * result + (this.isCollection ? 1 : 0);
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.next);
        return result;
    }

    private PropertyPath requiredNext() {
        PropertyPath result = this.next;
        if (result == null) {
            throw new IllegalStateException("No next path available! Clients should call hasNext() before invoking this method!");
        }
        return result;
    }

    public static PropertyPath from(String source, Class<?> type) {
        return PropertyPath.from(source, ClassTypeInformation.from(type));
    }

    public static PropertyPath from(String source, TypeInformation<?> type) {
        Assert.hasText((String)source, (String)"Source must not be null or empty!");
        Assert.notNull(type, (String)"TypeInformation must not be null or empty!");
        return cache.computeIfAbsent(Key.of(type, source), it -> {
            Matcher matcher;
            ArrayList<String> iteratorSource = new ArrayList<String>();
            Matcher matcher2 = matcher = PropertyPath.isQuoted(((Key)it).path) ? SPLITTER_FOR_QUOTED.matcher(((Key)it).path.replace("\\Q", "").replace("\\E", "")) : SPLITTER.matcher("_" + ((Key)it).path);
            while (matcher.find()) {
                iteratorSource.add(matcher.group(1));
            }
            Iterator parts = iteratorSource.iterator();
            PropertyPath result = null;
            Stack<PropertyPath> current = new Stack<PropertyPath>();
            while (parts.hasNext()) {
                if (result == null) {
                    result = PropertyPath.create((String)parts.next(), ((Key)it).type, current);
                    current.push(result);
                    continue;
                }
                current.push(PropertyPath.create((String)parts.next(), current));
            }
            if (result == null) {
                throw new IllegalStateException(String.format("Expected parsing to yield a PropertyPath from %s but got null!", source));
            }
            return result;
        });
    }

    private static boolean isQuoted(String source) {
        return source.matches("^\\\\Q.*\\\\E$");
    }

    private static PropertyPath create(String source, Stack<PropertyPath> base) {
        PropertyPath propertyPath;
        PropertyPath previous = base.peek();
        previous.next = propertyPath = PropertyPath.create(source, previous.typeInformation.getRequiredActualType(), base);
        return propertyPath;
    }

    private static PropertyPath create(String source, TypeInformation<?> type, List<PropertyPath> base) {
        return PropertyPath.create(source, type, "", base);
    }

    private static PropertyPath create(String source, TypeInformation<?> type, String addTail, List<PropertyPath> base) {
        if (base.size() > 1000) {
            throw new IllegalArgumentException(PARSE_DEPTH_EXCEEDED);
        }
        PropertyReferenceException exception = null;
        PropertyPath current = null;
        try {
            current = new PropertyPath(source, type, base);
            if (!base.isEmpty()) {
                base.get((int)(base.size() - 1)).next = current;
            }
            ArrayList<PropertyPath> newBase = new ArrayList<PropertyPath>(base);
            newBase.add(current);
            if (StringUtils.hasText((String)addTail)) {
                current.next = PropertyPath.create(addTail, current.actualTypeInformation, newBase);
            }
            return current;
        }
        catch (PropertyReferenceException e) {
            if (current != null) {
                throw e;
            }
            exception = e;
            Matcher matcher = NESTED_PROPERTY_PATTERN.matcher(source);
            if (matcher.find() && matcher.start() != 0) {
                int position = matcher.start();
                String head = source.substring(0, position);
                String tail = source.substring(position);
                try {
                    return PropertyPath.create(head, type, tail + addTail, base);
                }
                catch (PropertyReferenceException e2) {
                    throw e2.hasDeeperResolutionDepthThan(exception) ? e2 : exception;
                }
            }
            throw exception;
        }
    }

    public String toString() {
        return String.format("%s.%s", this.owningType.getType().getSimpleName(), this.toDotPath());
    }

    private static final class Key {
        private final TypeInformation<?> type;
        private final String path;

        private Key(TypeInformation<?> type, String path) {
            this.type = type;
            this.path = path;
        }

        public static Key of(TypeInformation<?> type, String path) {
            return new Key(type, path);
        }

        public TypeInformation<?> getType() {
            return this.type;
        }

        public String getPath() {
            return this.path;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Key)) {
                return false;
            }
            Key key = (Key)o;
            if (!ObjectUtils.nullSafeEquals(this.type, key.type)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals((Object)this.path, (Object)key.path);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode(this.type);
            result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.path);
            return result;
        }

        public String toString() {
            return "PropertyPath.Key(type=" + this.getType() + ", path=" + this.getPath() + ")";
        }
    }
}

