/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.mime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;

public class MediaTypeRegistry
implements Serializable {
    private static final long serialVersionUID = 4710974869988895410L;
    private final Map<MediaType, MediaType> registry = new ConcurrentHashMap<MediaType, MediaType>();
    private final Map<MediaType, MediaType> inheritance = new HashMap<MediaType, MediaType>();

    public static MediaTypeRegistry getDefaultRegistry() {
        return MimeTypes.getDefaultMimeTypes().getMediaTypeRegistry();
    }

    public SortedSet<MediaType> getTypes() {
        return new TreeSet<MediaType>(this.registry.values());
    }

    public SortedSet<MediaType> getAliases(MediaType type) {
        TreeSet<MediaType> aliases = new TreeSet<MediaType>();
        for (Map.Entry<MediaType, MediaType> entry : this.registry.entrySet()) {
            if (!entry.getValue().equals(type) || entry.getKey().equals(type)) continue;
            aliases.add(entry.getKey());
        }
        return aliases;
    }

    public SortedSet<MediaType> getChildTypes(MediaType type) {
        TreeSet<MediaType> children = new TreeSet<MediaType>();
        for (Map.Entry<MediaType, MediaType> entry : this.inheritance.entrySet()) {
            if (!entry.getValue().equals(type)) continue;
            children.add(entry.getKey());
        }
        return children;
    }

    public void addType(MediaType type) {
        this.registry.put(type, type);
    }

    public void addAlias(MediaType type, MediaType alias) {
        this.registry.put(alias, type);
    }

    public void addSuperType(MediaType type, MediaType supertype) {
        this.inheritance.put(type, supertype);
    }

    public MediaType normalize(MediaType type) {
        if (type == null) {
            return null;
        }
        MediaType canonical = this.registry.get(type.getBaseType());
        if (canonical == null) {
            return type;
        }
        if (type.hasParameters()) {
            return new MediaType(canonical, type.getParameters());
        }
        return canonical;
    }

    public boolean isSpecializationOf(MediaType a, MediaType b) {
        return this.isInstanceOf(this.getSupertype(a), b);
    }

    public boolean isInstanceOf(MediaType a, MediaType b) {
        return a != null && (a.equals(b) || this.isSpecializationOf(a, b));
    }

    public boolean isInstanceOf(String a, MediaType b) {
        return this.isInstanceOf(this.normalize(MediaType.parse(a)), b);
    }

    public MediaType getSupertype(MediaType type) {
        if (type == null) {
            return null;
        }
        if (this.inheritance.containsKey(type)) {
            return this.inheritance.get(type);
        }
        if (type.hasParameters()) {
            return type.getBaseType();
        }
        if (type.getSubtype().endsWith("+xml")) {
            return MediaType.APPLICATION_XML;
        }
        if (type.getSubtype().endsWith("+zip")) {
            return MediaType.APPLICATION_ZIP;
        }
        if ("text".equals(type.getType()) && !MediaType.TEXT_PLAIN.equals(type)) {
            return MediaType.TEXT_PLAIN;
        }
        if (type.getType().contains("empty") && !MediaType.EMPTY.equals(type)) {
            return MediaType.EMPTY;
        }
        if (!MediaType.OCTET_STREAM.equals(type)) {
            return MediaType.OCTET_STREAM;
        }
        return null;
    }
}

