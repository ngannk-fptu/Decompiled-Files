/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.springframework.data.convert;

import javax.annotation.Nullable;
import org.springframework.data.mapping.Alias;
import org.springframework.data.util.TypeInformation;

public interface TypeInformationMapper {
    @Nullable
    public TypeInformation<?> resolveTypeFrom(Alias var1);

    public Alias createAliasFor(TypeInformation<?> var1);
}

