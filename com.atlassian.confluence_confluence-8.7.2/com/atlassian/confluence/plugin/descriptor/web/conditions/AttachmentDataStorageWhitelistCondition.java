/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.pages.AttachmentDataStorageType;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentDataStorageWhitelistCondition
implements Condition {
    private static final Logger log = LoggerFactory.getLogger(AttachmentDataStorageWhitelistCondition.class);
    private static final String TYPE_PARAMETER_KEY = "type";
    private static final char TYPE_PARAMETER_SEPARATOR = ',';
    private static final Map<String, AttachmentDataStorageType> TYPE_MAPPING = ImmutableMap.of((Object)"filesystem", (Object)((Object)AttachmentDataStorageType.FILE_SYSTEM), (Object)"database", (Object)((Object)AttachmentDataStorageType.DATABASE));
    private AttachmentManager attachmentManager;
    private Iterable<AttachmentDataStorageType> whitelist;

    public void init(Map<String, String> parameters) throws PluginParseException {
        ImmutableList.Builder storageTypeBuilder;
        if (this.attachmentManager == null) {
            throw new IllegalStateException("The attachmentManager has not been set yet.");
        }
        String typeParameterValue = parameters.get(TYPE_PARAMETER_KEY);
        if (StringUtils.isNotBlank((CharSequence)typeParameterValue)) {
            String[] serialisedStorageTypes;
            storageTypeBuilder = ImmutableList.builder();
            for (String serialisedStorageType : serialisedStorageTypes = StringUtils.split((String)typeParameterValue, (char)',')) {
                if (this.mapToType((ImmutableList.Builder<AttachmentDataStorageType>)storageTypeBuilder, serialisedStorageType)) continue;
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("Unknown storage type [%s]", serialisedStorageType));
                if (!typeParameterValue.trim().equals(serialisedStorageType)) {
                    sb.append(String.format(" as part of value [%s]", typeParameterValue));
                }
                sb.append(String.format(" for parameter [%s].", TYPE_PARAMETER_KEY));
                sb.append(" ");
                sb.append(AttachmentDataStorageWhitelistCondition.createParametrizationHelpMessage());
                throw new IllegalArgumentException(sb.toString());
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Expected parameter [%s] to be configured in order to derive a whitelist of [%s] instances.", TYPE_PARAMETER_KEY, AttachmentDataStorageType.class.getName()));
            sb.append(" ");
            if (!parameters.isEmpty()) {
                sb.append(String.format("The parameters [%s] have been set but are not expected.", ToStringBuilder.reflectionToString((Object)parameters.keySet().toArray(), (ToStringStyle)ToStringStyle.SIMPLE_STYLE)));
                sb.append(" ");
            }
            sb.append(AttachmentDataStorageWhitelistCondition.createParametrizationHelpMessage());
            throw new IllegalArgumentException(sb.toString());
        }
        this.whitelist = storageTypeBuilder.build();
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        if (this.whitelist == null) {
            throw new IllegalStateException("The whitelist has not been configured yet, most likely due to init not being called yet.");
        }
        AttachmentDataStorageType backingStorageType = this.attachmentManager.getBackingStorageType();
        for (AttachmentDataStorageType whitelistedStorageType : this.whitelist) {
            if (whitelistedStorageType != backingStorageType) continue;
            return true;
        }
        if (log.isDebugEnabled()) {
            log.debug("Active type [{}] did not match any type in whitelist [{}].", (Object)backingStorageType, (Object)ToStringBuilder.reflectionToString(this.whitelist));
        }
        return false;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    private boolean mapToType(ImmutableList.Builder<AttachmentDataStorageType> storageTypeBuilder, String serialisedStorageType) {
        for (Map.Entry<String, AttachmentDataStorageType> typeMappingEntry : TYPE_MAPPING.entrySet()) {
            if (!typeMappingEntry.getKey().equalsIgnoreCase(serialisedStorageType)) continue;
            storageTypeBuilder.add((Object)typeMappingEntry.getValue());
            return true;
        }
        return false;
    }

    private static String createParametrizationHelpMessage() {
        return String.format("Parameter value separator is [%s] and the known type values are [%s].", Character.valueOf(','), ToStringBuilder.reflectionToString((Object)TYPE_MAPPING.keySet().toArray(), (ToStringStyle)ToStringStyle.SIMPLE_STYLE));
    }
}

