/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.querylang.fields.FieldHandler
 *  com.atlassian.querylang.fields.UISupport
 *  com.atlassian.querylang.lib.fields.FieldRegistry
 *  com.atlassian.querylang.lib.fields.FieldRegistryProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.cql.rest;

import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.plugins.cql.rest.model.QueryField;
import com.atlassian.confluence.plugins.cql.rest.model.RestUiSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.querylang.fields.FieldHandler;
import com.atlassian.querylang.fields.UISupport;
import com.atlassian.querylang.lib.fields.FieldRegistry;
import com.atlassian.querylang.lib.fields.FieldRegistryProvider;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestUiSupportFactory {
    private final I18nResolver i18nResolver;
    private final FieldRegistryProvider cqlFieldRegistryProvider;

    @Autowired
    public RestUiSupportFactory(@ComponentImport I18nResolver i18nResolver, FieldRegistryProvider cqlFieldRegistryProvider) {
        this.i18nResolver = i18nResolver;
        this.cqlFieldRegistryProvider = cqlFieldRegistryProvider;
    }

    public RestUiSupport makeUiSupport(String fieldName, QueryField.FieldType type) {
        FieldHandler handler = this.getHandler(fieldName, type);
        return this.makeUiSupport(handler.getFieldMetaData().uiSupport(), type);
    }

    private FieldHandler getHandler(String fieldName, QueryField.FieldType type) {
        FieldRegistry fieldRegistry = this.cqlFieldRegistryProvider.getFieldRegistry();
        if (type.equals((Object)QueryField.FieldType.TEXT)) {
            return fieldRegistry.getTextFieldHandler(fieldName);
        }
        if (type.equals((Object)QueryField.FieldType.EQUALITY)) {
            return fieldRegistry.getEqualityFieldHandler(fieldName);
        }
        if (type.equals((Object)QueryField.FieldType.DATE)) {
            return fieldRegistry.getDateTimeFieldHandler(fieldName);
        }
        throw new UnsupportedOperationException("Cannot create RestUiSupport for field of type: " + type);
    }

    public RestUiSupport makeUiSupport(Optional<UISupport> uiSupportOption, QueryField.FieldType type) {
        if (!uiSupportOption.isPresent()) {
            return null;
        }
        UISupport uiSupport = uiSupportOption.get();
        if ("~".equals(uiSupport.getDefaultOperator()) != QueryField.FieldType.TEXT.equals((Object)type)) {
            return null;
        }
        return RestUiSupport.builder().label(this.makeMessage(Optional.of(uiSupport.getI18nKey()))).tooltip(this.makeMessage(uiSupport.tooltipI18nKey())).valueType(uiSupport.getValueType()).dataUri(uiSupport.dataUri().orElse(null)).build();
    }

    private Message makeMessage(Optional<String> keyOption) {
        if (!keyOption.isPresent()) {
            return null;
        }
        String key = keyOption.get();
        String translation = this.i18nResolver.getText(key);
        return SimpleMessage.builder().key(key).translation(translation).build();
    }
}

