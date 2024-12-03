/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;

public abstract class ConfigurationSource {
    protected ConfigurationSource() {
    }

    public static ConfigurationSource getConfigurationSource(File file) {
        return new FileNameSource(file);
    }

    public static ConfigurationSource getConfigurationSource(URL configFileURL) {
        return new URLConfigurationSource(configFileURL);
    }

    public static ConfigurationSource getConfigurationSource(InputStream configFileStream) {
        return new InputStreamConfigurationSource(configFileStream);
    }

    public static ConfigurationSource getConfigurationSource() {
        return DefaultConfigurationSource.INSTANCE;
    }

    public abstract Configuration createConfiguration();

    private static class DefaultConfigurationSource
    extends ConfigurationSource {
        public static final DefaultConfigurationSource INSTANCE = new DefaultConfigurationSource();

        @Override
        public Configuration createConfiguration() {
            return ConfigurationFactory.parseConfiguration();
        }

        public String toString() {
            return "DefaultConfigurationSource [ ehcache.xml or ehcache-failsafe.xml ]";
        }
    }

    private static class InputStreamConfigurationSource
    extends ConfigurationSource {
        private final InputStream stream;

        public InputStreamConfigurationSource(InputStream stream) {
            this.stream = stream;
            stream.mark(Integer.MAX_VALUE);
        }

        @Override
        public Configuration createConfiguration() {
            try {
                this.stream.reset();
                return ConfigurationFactory.parseConfiguration(this.stream);
            }
            catch (IOException e) {
                throw new CacheException(e);
            }
        }

        public String toString() {
            return "InputStreamConfigurationSource [stream=" + this.stream + "]";
        }
    }

    private static class URLConfigurationSource
    extends ConfigurationSource {
        private final URL url;

        public URLConfigurationSource(URL url) {
            this.url = url;
        }

        @Override
        public Configuration createConfiguration() {
            return ConfigurationFactory.parseConfiguration(this.url);
        }

        public String toString() {
            return "URLConfigurationSource [url=" + this.url + "]";
        }
    }

    private static class FileNameSource
    extends ConfigurationSource {
        private final File file;

        public FileNameSource(File file) {
            this.file = file;
        }

        @Override
        public Configuration createConfiguration() {
            return ConfigurationFactory.parseConfiguration(this.file);
        }

        public String toString() {
            return "FileNameSource [file=" + this.file + "]";
        }
    }
}

