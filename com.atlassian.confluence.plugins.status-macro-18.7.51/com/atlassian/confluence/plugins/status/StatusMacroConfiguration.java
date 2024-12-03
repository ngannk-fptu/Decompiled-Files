/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.LazyReference
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.BooleanUtils
 */
package com.atlassian.confluence.plugins.status;

import com.atlassian.confluence.plugins.status.StatusColour;
import io.atlassian.util.concurrent.LazyReference;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;

public class StatusMacroConfiguration {
    private static final String PARAM_COLOUR = "colour";
    private static final String PARAM_COLOR = "color";
    private static final String PARAM_TITLE = "title";
    private static final String PARAM_SUBTLE = "subtle";
    private Map<String, ?> params;
    private Supplier<StatusColour> colour = new LazyReference<StatusColour>(){

        protected StatusColour create() throws Exception {
            Optional<String> colour = StatusMacroConfiguration.this.param(StatusMacroConfiguration.PARAM_COLOUR);
            if (!colour.isPresent()) {
                colour = StatusMacroConfiguration.this.param(StatusMacroConfiguration.PARAM_COLOR);
            }
            return colour.map(StatusColour::fromString).orElse(StatusColour.DEFAULT);
        }
    };
    private Supplier<String> title = new LazyReference<String>(){

        protected String create() {
            return this.getTitle().toUpperCase();
        }

        private String getTitle() {
            Optional<String> title = StatusMacroConfiguration.this.param(StatusMacroConfiguration.PARAM_TITLE);
            return title.orElseGet(() -> StatusMacroConfiguration.this.getColour().name());
        }
    };
    private Supplier<Boolean> subtle = new LazyReference<Boolean>(){

        protected Boolean create() {
            Optional<String> subtle = StatusMacroConfiguration.this.param(StatusMacroConfiguration.PARAM_SUBTLE);
            return subtle.filter(BooleanUtils::toBoolean).isPresent();
        }
    };

    public static StatusMacroConfiguration createFor(Map<String, ?> params) {
        return new StatusMacroConfiguration(params);
    }

    private StatusMacroConfiguration(Map<String, ?> parameters) {
        this.params = parameters;
    }

    public StatusColour getColour() {
        return this.colour.get();
    }

    public boolean isSubtle() {
        return this.subtle.get();
    }

    public String getTitle() {
        return this.title.get();
    }

    private <T> Optional<T> param(String key) {
        Object[] valueArray;
        Object value = this.params.get(key);
        if (value instanceof Iterable) {
            return StreamSupport.stream(((Iterable)value).spliterator(), false).findFirst();
        }
        if (value instanceof Object[] && !ArrayUtils.isEmpty((Object[])(valueArray = (Object[])value))) {
            return Optional.of(valueArray[0]);
        }
        return Optional.ofNullable(value);
    }
}

