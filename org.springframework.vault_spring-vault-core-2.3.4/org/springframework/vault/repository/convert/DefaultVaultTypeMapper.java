/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.convert.DefaultTypeMapper
 *  org.springframework.data.convert.SimpleTypeInformationMapper
 *  org.springframework.data.convert.TypeAliasAccessor
 *  org.springframework.data.convert.TypeInformationMapper
 *  org.springframework.data.mapping.Alias
 *  org.springframework.data.mapping.PersistentEntity
 *  org.springframework.data.mapping.context.MappingContext
 *  org.springframework.data.util.ClassTypeInformation
 *  org.springframework.data.util.TypeInformation
 *  org.springframework.lang.Nullable
 */
package org.springframework.vault.repository.convert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.data.convert.DefaultTypeMapper;
import org.springframework.data.convert.SimpleTypeInformationMapper;
import org.springframework.data.convert.TypeAliasAccessor;
import org.springframework.data.convert.TypeInformationMapper;
import org.springframework.data.mapping.Alias;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.vault.repository.convert.VaultTypeMapper;

public class DefaultVaultTypeMapper
extends DefaultTypeMapper<Map<String, Object>>
implements VaultTypeMapper {
    public static final String DEFAULT_TYPE_KEY = "_class";
    private static final TypeInformation<Map> MAP_TYPE_INFO = ClassTypeInformation.from(Map.class);
    @Nullable
    private final String typeKey;

    public DefaultVaultTypeMapper() {
        this(DEFAULT_TYPE_KEY);
    }

    public DefaultVaultTypeMapper(@Nullable String typeKey) {
        this(typeKey, Collections.singletonList(new SimpleTypeInformationMapper()));
    }

    public DefaultVaultTypeMapper(@Nullable String typeKey, MappingContext<? extends PersistentEntity<?, ?>, ?> mappingContext) {
        this(typeKey, new SecretDocumentTypeAliasAccessor(typeKey), mappingContext, Collections.singletonList(new SimpleTypeInformationMapper()));
    }

    public DefaultVaultTypeMapper(@Nullable String typeKey, List<? extends TypeInformationMapper> mappers) {
        this(typeKey, new SecretDocumentTypeAliasAccessor(typeKey), null, mappers);
    }

    private DefaultVaultTypeMapper(@Nullable String typeKey, TypeAliasAccessor<Map<String, Object>> accessor, MappingContext<? extends PersistentEntity<?, ?>, ?> mappingContext, List<? extends TypeInformationMapper> mappers) {
        super(accessor, mappingContext, mappers);
        this.typeKey = typeKey;
    }

    @Override
    public boolean isTypeKey(String key) {
        return this.typeKey != null && this.typeKey.equals(key);
    }

    protected TypeInformation<?> getFallbackTypeFor(Map<String, Object> source) {
        return MAP_TYPE_INFO;
    }

    static class SecretDocumentTypeAliasAccessor
    implements TypeAliasAccessor<Map<String, Object>> {
        @Nullable
        private final String typeKey;

        SecretDocumentTypeAliasAccessor(@Nullable String typeKey) {
            this.typeKey = typeKey;
        }

        public Alias readAliasFrom(Map<String, Object> source) {
            return this.typeKey == null ? Alias.NONE : Alias.ofNullable((Object)source.get(this.typeKey));
        }

        public void writeTypeTo(Map<String, Object> sink, Object alias) {
            if (this.typeKey != null) {
                sink.put(this.typeKey, alias);
            }
        }
    }
}

