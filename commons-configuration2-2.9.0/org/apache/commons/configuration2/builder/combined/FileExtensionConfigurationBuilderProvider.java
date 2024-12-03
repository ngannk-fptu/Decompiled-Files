/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder.combined;

import java.util.Collection;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.combined.BaseConfigurationBuilderProvider;
import org.apache.commons.configuration2.builder.combined.ConfigurationDeclaration;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class FileExtensionConfigurationBuilderProvider
extends BaseConfigurationBuilderProvider {
    private static final char EXT_SEPARATOR = '.';
    private final String matchingConfigurationClass;
    private final String extension;

    public FileExtensionConfigurationBuilderProvider(String bldrCls, String reloadBldrCls, String matchingConfigCls, String defConfigClass, String ext, Collection<String> paramCls) {
        super(bldrCls, reloadBldrCls, defConfigClass, paramCls);
        if (matchingConfigCls == null) {
            throw new IllegalArgumentException("Matching configuration class must not be null!");
        }
        if (ext == null) {
            throw new IllegalArgumentException("File extension must not be null!");
        }
        this.matchingConfigurationClass = matchingConfigCls;
        this.extension = ext;
    }

    public String getMatchingConfigurationClass() {
        return this.matchingConfigurationClass;
    }

    public String getExtension() {
        return this.extension;
    }

    @Override
    protected String determineConfigurationClass(ConfigurationDeclaration decl, Collection<BuilderParameters> params) throws ConfigurationException {
        String currentExt = FileExtensionConfigurationBuilderProvider.extractExtension(FileExtensionConfigurationBuilderProvider.fetchCurrentFileName(params));
        return this.getExtension().equalsIgnoreCase(currentExt) ? this.getMatchingConfigurationClass() : this.getConfigurationClass();
    }

    private static String fetchCurrentFileName(Collection<BuilderParameters> params) {
        for (BuilderParameters p : params) {
            if (!(p instanceof FileBasedBuilderParametersImpl)) continue;
            FileBasedBuilderParametersImpl fp = (FileBasedBuilderParametersImpl)p;
            return fp.getFileHandler().getFileName();
        }
        return null;
    }

    private static String extractExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int pos = fileName.lastIndexOf(46);
        return pos < 0 ? null : fileName.substring(pos + 1);
    }
}

