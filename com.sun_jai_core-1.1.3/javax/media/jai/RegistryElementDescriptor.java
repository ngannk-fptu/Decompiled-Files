/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.PropertyGenerator;

public interface RegistryElementDescriptor {
    public String getName();

    public String[] getSupportedModes();

    public boolean isModeSupported(String var1);

    public boolean arePropertiesSupported();

    public PropertyGenerator[] getPropertyGenerators(String var1);

    public ParameterListDescriptor getParameterListDescriptor(String var1);
}

