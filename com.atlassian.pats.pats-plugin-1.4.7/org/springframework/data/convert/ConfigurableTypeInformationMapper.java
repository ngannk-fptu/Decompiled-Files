/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.convert;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.springframework.data.convert.TypeInformationMapper;
import org.springframework.data.mapping.Alias;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;

public class ConfigurableTypeInformationMapper
implements TypeInformationMapper {
    private final Map<ClassTypeInformation<?>, Alias> typeToAlias;
    private final Map<Alias, ClassTypeInformation<?>> aliasToType;

    public ConfigurableTypeInformationMapper(Map<? extends Class<?>, String> sourceTypeMap) {
        Assert.notNull(sourceTypeMap, (String)"SourceTypeMap must not be null!");
        this.typeToAlias = new HashMap(sourceTypeMap.size());
        this.aliasToType = new HashMap(sourceTypeMap.size());
        for (Map.Entry<Class<?>, String> entry : sourceTypeMap.entrySet()) {
            ClassTypeInformation<?> type = ClassTypeInformation.from(entry.getKey());
            Alias alias = Alias.of(entry.getValue());
            if (this.typeToAlias.containsValue(alias)) {
                throw new IllegalArgumentException(String.format("Detected mapping ambiguity! String %s cannot be mapped to more than one type!", alias));
            }
            this.typeToAlias.put(type, alias);
            this.aliasToType.put(alias, type);
        }
    }

    @Override
    public Alias createAliasFor(TypeInformation<?> type) {
        return this.typeToAlias.getOrDefault(type, Alias.NONE);
    }

    @Override
    @Nullable
    public TypeInformation<?> resolveTypeFrom(Alias alias) {
        return this.aliasToType.get(alias);
    }
}

