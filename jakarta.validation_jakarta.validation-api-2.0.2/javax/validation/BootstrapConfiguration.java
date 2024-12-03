/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import java.util.Map;
import java.util.Set;
import javax.validation.executable.ExecutableType;

public interface BootstrapConfiguration {
    public String getDefaultProviderClassName();

    public String getConstraintValidatorFactoryClassName();

    public String getMessageInterpolatorClassName();

    public String getTraversableResolverClassName();

    public String getParameterNameProviderClassName();

    public String getClockProviderClassName();

    public Set<String> getValueExtractorClassNames();

    public Set<String> getConstraintMappingResourcePaths();

    public boolean isExecutableValidationEnabled();

    public Set<ExecutableType> getDefaultValidatedExecutableTypes();

    public Map<String, String> getProperties();
}

