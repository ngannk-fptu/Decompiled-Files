/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.util.I18nHelper
 *  com.atlassian.crowd.validator.DirectoryValidationContext
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.crowd.embedded.validator.impl;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.validator.DirectoryValidatorFactory;
import com.atlassian.crowd.embedded.validator.Validator;
import com.atlassian.crowd.embedded.validator.impl.AzureADConnectionValidator;
import com.atlassian.crowd.embedded.validator.impl.AzureADConnectorValidator;
import com.atlassian.crowd.embedded.validator.impl.CompoundValidator;
import com.atlassian.crowd.embedded.validator.impl.CustomDirectoryValidator;
import com.atlassian.crowd.embedded.validator.impl.DelegatedDirectoryConnectionValidator;
import com.atlassian.crowd.embedded.validator.impl.InternalDirectoryValidator;
import com.atlassian.crowd.embedded.validator.impl.LDAPConnectionValidator;
import com.atlassian.crowd.embedded.validator.impl.LDAPConnectorValidator;
import com.atlassian.crowd.embedded.validator.impl.LDAPGroupConfigValidator;
import com.atlassian.crowd.embedded.validator.impl.LDAPGroupSearchConfigValidator;
import com.atlassian.crowd.embedded.validator.impl.LDAPUserConfigValidator;
import com.atlassian.crowd.embedded.validator.impl.LDAPUserSearchConfigValidator;
import com.atlassian.crowd.embedded.validator.impl.RemoteCrowdConnectionValidator;
import com.atlassian.crowd.embedded.validator.impl.RemoteCrowdConnectorValidator;
import com.atlassian.crowd.embedded.validator.impl.SynchronisationSchedulingConfigValidator;
import com.atlassian.crowd.util.I18nHelper;
import com.atlassian.crowd.validator.DirectoryValidationContext;
import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class DirectoryValidatorFactoryImpl
implements DirectoryValidatorFactory {
    private final I18nHelper i18nHelper;

    public DirectoryValidatorFactoryImpl(I18nHelper i18nHelper) {
        this.i18nHelper = i18nHelper;
    }

    private Validator<Directory> getCrowdDirectoryValidator(DirectoryValidationContext context) {
        switch (context) {
            case DEFAULT: {
                return new CompoundValidator<Directory>((List<Validator<Directory>>)ImmutableList.of((Object)new RemoteCrowdConnectionValidator(this.i18nHelper), (Object)new RemoteCrowdConnectorValidator(this.i18nHelper), (Object)new SynchronisationSchedulingConfigValidator(this.i18nHelper)));
            }
            case CONNECTION: {
                return new RemoteCrowdConnectionValidator(this.i18nHelper);
            }
            case CONNECTOR_ATTRIBUTES: {
                return new RemoteCrowdConnectorValidator(this.i18nHelper);
            }
            case SYNCHRONISATION_SCHEDULING: {
                return new SynchronisationSchedulingConfigValidator(this.i18nHelper);
            }
        }
        throw new UnsupportedOperationException(String.format("No validator is available for type: %s, and context: %s", DirectoryType.CROWD, context.name()));
    }

    private Validator<Directory> getCustomDirectoryValidator(DirectoryValidationContext context) {
        switch (context) {
            case DEFAULT: {
                return new CompoundValidator<Directory>((List<Validator<Directory>>)ImmutableList.of((Object)new CustomDirectoryValidator(this.i18nHelper)));
            }
        }
        throw new UnsupportedOperationException(String.format("No validator is available for type: %s, and context: %s", DirectoryType.CUSTOM, context.name()));
    }

    private Validator<Directory> getAzureADValidator(DirectoryValidationContext context) {
        switch (context) {
            case DEFAULT: {
                return new CompoundValidator<Directory>((List<Validator<Directory>>)ImmutableList.of((Object)new AzureADConnectionValidator(this.i18nHelper), (Object)new AzureADConnectorValidator(this.i18nHelper), (Object)new SynchronisationSchedulingConfigValidator(this.i18nHelper)));
            }
            case CONNECTION: {
                return new AzureADConnectionValidator(this.i18nHelper);
            }
            case CONNECTOR_ATTRIBUTES: {
                return new AzureADConnectorValidator(this.i18nHelper);
            }
            case SYNCHRONISATION_SCHEDULING: {
                return new SynchronisationSchedulingConfigValidator(this.i18nHelper);
            }
        }
        throw new UnsupportedOperationException(String.format("No validator is available for type: %s, and context: %s", DirectoryType.AZURE_AD, context.name()));
    }

    private Validator<Directory> getInternalDirectoryValidator(DirectoryValidationContext context) {
        switch (context) {
            case DEFAULT: {
                return new CompoundValidator<Directory>((List<Validator<Directory>>)ImmutableList.of((Object)new InternalDirectoryValidator(this.i18nHelper)));
            }
        }
        throw new UnsupportedOperationException(String.format("No validator is available for type: %s, and context: %s", DirectoryType.INTERNAL, context.name()));
    }

    private Validator<Directory> getDelegatedDirectoryValidator(DirectoryValidationContext context) {
        switch (context) {
            case DEFAULT: {
                return new CompoundValidator<Directory>((List<Validator<Directory>>)ImmutableList.of((Object)new DelegatedDirectoryConnectionValidator(this.i18nHelper), (Object)new LDAPConnectorValidator(this.i18nHelper), (Object)new LDAPUserConfigValidator(this.i18nHelper), (Object)new LDAPGroupConfigValidator(this.i18nHelper)));
            }
            case CONNECTION: {
                return new DelegatedDirectoryConnectionValidator(this.i18nHelper);
            }
            case CONNECTOR_ATTRIBUTES: {
                return new LDAPConnectorValidator(this.i18nHelper);
            }
            case USER_CONFIGURATION: {
                return new LDAPUserConfigValidator(this.i18nHelper);
            }
            case GROUP_CONFIGURATION: {
                return new LDAPGroupConfigValidator(this.i18nHelper);
            }
            case USER_SEARCH: {
                return new LDAPUserSearchConfigValidator(this.i18nHelper);
            }
            case GROUP_SEARCH: {
                return new LDAPGroupSearchConfigValidator(this.i18nHelper);
            }
        }
        throw new UnsupportedOperationException(String.format("No validator is available for type: %s, and context: %s", DirectoryType.DELEGATING, context.name()));
    }

    private Validator<Directory> getConnectorValidator(DirectoryValidationContext context) {
        switch (context) {
            case DEFAULT: {
                return new CompoundValidator<Directory>((List<Validator<Directory>>)ImmutableList.of((Object)new LDAPConnectionValidator(this.i18nHelper), (Object)new LDAPConnectorValidator(this.i18nHelper), (Object)new SynchronisationSchedulingConfigValidator(this.i18nHelper), (Object)new LDAPUserConfigValidator(this.i18nHelper), (Object)new LDAPGroupConfigValidator(this.i18nHelper)));
            }
            case CONNECTION: {
                return new LDAPConnectionValidator(this.i18nHelper);
            }
            case CONNECTOR_ATTRIBUTES: {
                return new LDAPConnectorValidator(this.i18nHelper);
            }
            case USER_CONFIGURATION: {
                return new LDAPUserConfigValidator(this.i18nHelper);
            }
            case GROUP_CONFIGURATION: {
                return new LDAPGroupConfigValidator(this.i18nHelper);
            }
            case USER_SEARCH: {
                return new LDAPUserSearchConfigValidator(this.i18nHelper);
            }
            case GROUP_SEARCH: {
                return new LDAPGroupSearchConfigValidator(this.i18nHelper);
            }
            case SYNCHRONISATION_SCHEDULING: {
                return new SynchronisationSchedulingConfigValidator(this.i18nHelper);
            }
        }
        throw new UnsupportedOperationException(String.format("No validator is available for type: %s, and context: %s", DirectoryType.CONNECTOR, context.name()));
    }

    private Validator<Directory> getContextualValidator(DirectoryType directoryType, DirectoryValidationContext validationContext) {
        if (directoryType == DirectoryType.INTERNAL) {
            return this.getInternalDirectoryValidator(validationContext);
        }
        if (directoryType == DirectoryType.CONNECTOR) {
            return this.getConnectorValidator(validationContext);
        }
        if (directoryType == DirectoryType.DELEGATING) {
            return this.getDelegatedDirectoryValidator(validationContext);
        }
        if (directoryType == DirectoryType.CROWD) {
            return this.getCrowdDirectoryValidator(validationContext);
        }
        if (directoryType == DirectoryType.CUSTOM) {
            return this.getCustomDirectoryValidator(validationContext);
        }
        if (directoryType == DirectoryType.AZURE_AD) {
            return this.getAzureADValidator(validationContext);
        }
        throw new UnsupportedOperationException(String.format("No validator is available for type: %s, and context: %s", directoryType.name(), validationContext.name()));
    }

    @Override
    public Validator<Directory> getValidator(DirectoryType directoryType, EnumSet<DirectoryValidationContext> directoryValidationContexts) {
        if (directoryValidationContexts.contains(DirectoryValidationContext.DEFAULT)) {
            return this.getContextualValidator(directoryType, DirectoryValidationContext.DEFAULT);
        }
        List directoryValidators = directoryValidationContexts.stream().filter(context -> context != DirectoryValidationContext.DEFAULT).map(context -> this.getContextualValidator(directoryType, (DirectoryValidationContext)context)).collect(Collectors.toList());
        return new CompoundValidator<Directory>(directoryValidators);
    }
}

