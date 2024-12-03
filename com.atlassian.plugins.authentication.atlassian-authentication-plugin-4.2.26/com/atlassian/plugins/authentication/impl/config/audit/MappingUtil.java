/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.ChangedValue
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.config.audit;

import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.plugins.authentication.impl.config.audit.KeyMapping;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.codehaus.jackson.map.ObjectMapper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MappingUtil {
    public static final String OLD_SANITIZED_VALUE = "OLD_SANITIZED_VALUE";
    public static final String NEW_SANITIZED_VALUE = "NEW_SANITIZED_VALUE";
    public static final String SANITIZED_VALUE = "SANITIZED_VALUE";
    public static final String KEY_PREFIX = "com.atlassian.plugins.authentication.audit.change.";
    private static final Logger log = LoggerFactory.getLogger(MappingUtil.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MappingUtil() {
    }

    public static <T, U> Optional<ChangedValue> mapChange(KeyMapping<U> keyMapping, @Nullable T oldObject, @Nullable T newObject, Class<U> targetClass) {
        String from = MappingUtil.extractNullSafely(oldObject, keyMapping.getPropertyExtractor(), targetClass);
        String to = MappingUtil.extractNullSafely(newObject, keyMapping.getPropertyExtractor(), targetClass);
        if (keyMapping.isSanitize()) {
            if (!Objects.equals(from, to)) {
                from = from.isEmpty() ? from : OLD_SANITIZED_VALUE;
                to = to.isEmpty() ? to : NEW_SANITIZED_VALUE;
            } else {
                from = from.isEmpty() ? from : SANITIZED_VALUE;
                String string = to = to.isEmpty() ? to : SANITIZED_VALUE;
            }
        }
        if (from.equals(to)) {
            return Optional.empty();
        }
        return Optional.of(ChangedValue.fromI18nKeys((String)keyMapping.getKey()).from(from).to(to).build());
    }

    public static <T> Function<T, String> toJson(Function<T, ?> before) {
        return before.andThen(o -> {
            try {
                return OBJECT_MAPPER.writeValueAsString(o);
            }
            catch (IOException e) {
                log.error("Could not map object to json", (Throwable)e);
                return "error serializing data";
            }
        });
    }

    private static <T, U> String extractNullSafely(@Nullable T object, Function<U, String> propertyExtractor, Class<U> targetClass) {
        return Optional.ofNullable(object).map(config -> targetClass.isInstance(config) ? targetClass.cast(config) : null).map(propertyExtractor).orElse("");
    }
}

