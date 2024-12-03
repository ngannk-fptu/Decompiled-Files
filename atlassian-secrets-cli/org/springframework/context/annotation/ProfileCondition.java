/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.util.List;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

class ProfileCondition
implements Condition {
    ProfileCondition() {
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
        if (attrs != null) {
            for (Object value : (List)attrs.get("value")) {
                if (!context.getEnvironment().acceptsProfiles((String[])value)) continue;
                return true;
            }
            return false;
        }
        return true;
    }
}

