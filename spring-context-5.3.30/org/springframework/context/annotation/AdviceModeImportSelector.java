/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.GenericTypeResolver
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.type.AnnotatedTypeMetadata
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AdviceModeImportSelector<A extends Annotation>
implements ImportSelector {
    public static final String DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME = "mode";

    protected String getAdviceModeAttributeName() {
        return DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME;
    }

    @Override
    public final String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Class annType = GenericTypeResolver.resolveTypeArgument(this.getClass(), AdviceModeImportSelector.class);
        Assert.state((annType != null ? 1 : 0) != 0, (String)"Unresolvable type argument for AdviceModeImportSelector");
        AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor((AnnotatedTypeMetadata)importingClassMetadata, annType);
        if (attributes == null) {
            throw new IllegalArgumentException(String.format("@%s is not present on importing class '%s' as expected", annType.getSimpleName(), importingClassMetadata.getClassName()));
        }
        AdviceMode adviceMode = (AdviceMode)attributes.getEnum(this.getAdviceModeAttributeName());
        String[] imports = this.selectImports(adviceMode);
        if (imports == null) {
            throw new IllegalArgumentException("Unknown AdviceMode: " + (Object)((Object)adviceMode));
        }
        return imports;
    }

    @Nullable
    protected abstract String[] selectImports(AdviceMode var1);
}

