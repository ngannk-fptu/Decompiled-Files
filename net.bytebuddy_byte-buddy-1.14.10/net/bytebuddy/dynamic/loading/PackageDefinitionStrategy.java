/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.dynamic.loading;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface PackageDefinitionStrategy {
    public Definition define(ClassLoader var1, String var2, String var3);

    @HashCodeAndEqualsPlugin.Enhance
    public static class ManifestReading
    implements PackageDefinitionStrategy {
        @AlwaysNull
        private static final URL NOT_SEALED = null;
        private static final Attributes.Name[] ATTRIBUTE_NAMES = new Attributes.Name[]{Attributes.Name.SPECIFICATION_TITLE, Attributes.Name.SPECIFICATION_VERSION, Attributes.Name.SPECIFICATION_VENDOR, Attributes.Name.IMPLEMENTATION_TITLE, Attributes.Name.IMPLEMENTATION_VERSION, Attributes.Name.IMPLEMENTATION_VENDOR, Attributes.Name.SEALED};
        private final SealBaseLocator sealBaseLocator;

        public ManifestReading() {
            this(new SealBaseLocator.ForTypeResourceUrl());
        }

        public ManifestReading(SealBaseLocator sealBaseLocator) {
            this.sealBaseLocator = sealBaseLocator;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public Definition define(ClassLoader classLoader, String packageName, String typeName) {
            InputStream inputStream = classLoader.getResourceAsStream("META-INF/MANIFEST.MF");
            if (inputStream == null) {
                return Definition.Trivial.INSTANCE;
            }
            try {
                Definition.Simple simple;
                try {
                    Attributes attributes;
                    Manifest manifest = new Manifest(inputStream);
                    HashMap<Attributes.Name, String> values = new HashMap<Attributes.Name, String>();
                    Attributes mainAttributes = manifest.getMainAttributes();
                    if (mainAttributes != null) {
                        for (Attributes.Name attributeName : ATTRIBUTE_NAMES) {
                            values.put(attributeName, mainAttributes.getValue(attributeName));
                        }
                    }
                    if ((attributes = manifest.getAttributes(packageName.replace('.', '/').concat("/"))) != null) {
                        for (Attributes.Name attributeName : ATTRIBUTE_NAMES) {
                            String value = attributes.getValue(attributeName);
                            if (value == null) continue;
                            values.put(attributeName, value);
                        }
                    }
                    simple = new Definition.Simple((String)values.get(Attributes.Name.SPECIFICATION_TITLE), (String)values.get(Attributes.Name.SPECIFICATION_VERSION), (String)values.get(Attributes.Name.SPECIFICATION_VENDOR), (String)values.get(Attributes.Name.IMPLEMENTATION_TITLE), (String)values.get(Attributes.Name.IMPLEMENTATION_VERSION), (String)values.get(Attributes.Name.IMPLEMENTATION_VENDOR), Boolean.parseBoolean((String)values.get(Attributes.Name.SEALED)) ? this.sealBaseLocator.findSealBase(classLoader, typeName) : NOT_SEALED);
                    Object var15_18 = null;
                }
                catch (Throwable throwable) {
                    Object var15_19 = null;
                    inputStream.close();
                    throw throwable;
                }
                inputStream.close();
                return simple;
            }
            catch (IOException exception) {
                throw new IllegalStateException("Error while reading manifest file", exception);
            }
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return this.sealBaseLocator.equals(((ManifestReading)object).sealBaseLocator);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.sealBaseLocator.hashCode();
        }

        public static interface SealBaseLocator {
            @MaybeNull
            public URL findSealBase(ClassLoader var1, String var2);

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForTypeResourceUrl
            implements SealBaseLocator {
                private static final int EXCLUDE_INITIAL_SLASH = 1;
                private static final String CLASS_FILE_EXTENSION = ".class";
                private static final String JAR_FILE = "jar";
                private static final String FILE_SYSTEM = "file";
                private static final String RUNTIME_IMAGE = "jrt";
                private final SealBaseLocator fallback;

                public ForTypeResourceUrl() {
                    this(NonSealing.INSTANCE);
                }

                public ForTypeResourceUrl(SealBaseLocator fallback) {
                    this.fallback = fallback;
                }

                @MaybeNull
                public URL findSealBase(ClassLoader classLoader, String typeName) {
                    URL url = classLoader.getResource(typeName.replace('.', '/') + CLASS_FILE_EXTENSION);
                    if (url != null) {
                        try {
                            if (url.getProtocol().equals(JAR_FILE)) {
                                return URI.create(url.getPath().substring(0, url.getPath().indexOf(33))).toURL();
                            }
                            if (url.getProtocol().equals(FILE_SYSTEM)) {
                                return url;
                            }
                            if (url.getProtocol().equals(RUNTIME_IMAGE)) {
                                String path = url.getPath();
                                int modulePathIndex = path.indexOf(47, 1);
                                return modulePathIndex == -1 ? url : URI.create("jrt:" + path.substring(0, modulePathIndex)).toURL();
                            }
                        }
                        catch (MalformedURLException exception) {
                            throw new IllegalStateException("Unexpected URL: " + url, exception);
                        }
                    }
                    return this.fallback.findSealBase(classLoader, typeName);
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.fallback.equals(((ForTypeResourceUrl)object).fallback);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.fallback.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForFixedValue
            implements SealBaseLocator {
                @MaybeNull
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                private final URL sealBase;

                public ForFixedValue(@MaybeNull URL sealBase) {
                    this.sealBase = sealBase;
                }

                @MaybeNull
                public URL findSealBase(ClassLoader classLoader, String typeName) {
                    return this.sealBase;
                }

                @SuppressFBWarnings(value={"DMI_BLOCKING_METHODS_ON_URL"}, justification="Package sealing relies on URL equality.")
                public int hashCode() {
                    return this.sealBase == null ? 17 : this.sealBase.hashCode();
                }

                @SuppressFBWarnings(value={"DMI_BLOCKING_METHODS_ON_URL"}, justification="Package sealing relies on URL equality.")
                public boolean equals(@MaybeNull Object other) {
                    if (this == other) {
                        return true;
                    }
                    if (other == null || this.getClass() != other.getClass()) {
                        return false;
                    }
                    ForFixedValue forFixedValue = (ForFixedValue)other;
                    return this.sealBase == null ? forFixedValue.sealBase == null : this.sealBase.equals(forFixedValue.sealBase);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum NonSealing implements SealBaseLocator
            {
                INSTANCE;


                @Override
                @MaybeNull
                public URL findSealBase(ClassLoader classLoader, String typeName) {
                    return NOT_SEALED;
                }
            }
        }
    }

    public static interface Definition {
        public boolean isDefined();

        @MaybeNull
        public String getSpecificationTitle();

        @MaybeNull
        public String getSpecificationVersion();

        @MaybeNull
        public String getSpecificationVendor();

        @MaybeNull
        public String getImplementationTitle();

        @MaybeNull
        public String getImplementationVersion();

        @MaybeNull
        public String getImplementationVendor();

        @MaybeNull
        public URL getSealBase();

        public boolean isCompatibleTo(Package var1);

        public static class Simple
        implements Definition {
            @MaybeNull
            protected final URL sealBase;
            @MaybeNull
            private final String specificationTitle;
            @MaybeNull
            private final String specificationVersion;
            @MaybeNull
            private final String specificationVendor;
            @MaybeNull
            private final String implementationTitle;
            @MaybeNull
            private final String implementationVersion;
            @MaybeNull
            private final String implementationVendor;

            public Simple(@MaybeNull String specificationTitle, @MaybeNull String specificationVersion, @MaybeNull String specificationVendor, @MaybeNull String implementationTitle, @MaybeNull String implementationVersion, @MaybeNull String implementationVendor, @MaybeNull URL sealBase) {
                this.specificationTitle = specificationTitle;
                this.specificationVersion = specificationVersion;
                this.specificationVendor = specificationVendor;
                this.implementationTitle = implementationTitle;
                this.implementationVersion = implementationVersion;
                this.implementationVendor = implementationVendor;
                this.sealBase = sealBase;
            }

            public boolean isDefined() {
                return true;
            }

            @MaybeNull
            public String getSpecificationTitle() {
                return this.specificationTitle;
            }

            @MaybeNull
            public String getSpecificationVersion() {
                return this.specificationVersion;
            }

            @MaybeNull
            public String getSpecificationVendor() {
                return this.specificationVendor;
            }

            @MaybeNull
            public String getImplementationTitle() {
                return this.implementationTitle;
            }

            @MaybeNull
            public String getImplementationVersion() {
                return this.implementationVersion;
            }

            @MaybeNull
            public String getImplementationVendor() {
                return this.implementationVendor;
            }

            @MaybeNull
            public URL getSealBase() {
                return this.sealBase;
            }

            public boolean isCompatibleTo(Package definedPackage) {
                if (this.sealBase == null) {
                    return !definedPackage.isSealed();
                }
                return definedPackage.isSealed(this.sealBase);
            }

            @SuppressFBWarnings(value={"DMI_BLOCKING_METHODS_ON_URL"}, justification="Package sealing relies on URL equality.")
            public int hashCode() {
                int result = this.specificationTitle != null ? this.specificationTitle.hashCode() : 0;
                result = 31 * result + (this.specificationVersion != null ? this.specificationVersion.hashCode() : 0);
                result = 31 * result + (this.specificationVendor != null ? this.specificationVendor.hashCode() : 0);
                result = 31 * result + (this.implementationTitle != null ? this.implementationTitle.hashCode() : 0);
                result = 31 * result + (this.implementationVersion != null ? this.implementationVersion.hashCode() : 0);
                result = 31 * result + (this.implementationVendor != null ? this.implementationVendor.hashCode() : 0);
                result = 31 * result + (this.sealBase != null ? this.sealBase.hashCode() : 0);
                return result;
            }

            @SuppressFBWarnings(value={"DMI_BLOCKING_METHODS_ON_URL"}, justification="Package sealing relies on URL equality.")
            public boolean equals(@MaybeNull Object other) {
                if (this == other) {
                    return true;
                }
                if (other == null || this.getClass() != other.getClass()) {
                    return false;
                }
                Simple simple = (Simple)other;
                return !((this.specificationTitle == null ? simple.specificationTitle != null : !this.specificationTitle.equals(simple.specificationTitle)) || (this.specificationVersion == null ? simple.specificationVersion != null : !this.specificationVersion.equals(simple.specificationVersion)) || (this.specificationVendor == null ? simple.specificationVendor != null : !this.specificationVendor.equals(simple.specificationVendor)) || (this.implementationTitle == null ? simple.implementationTitle != null : !this.implementationTitle.equals(simple.implementationTitle)) || (this.implementationVersion == null ? simple.implementationVersion != null : !this.implementationVersion.equals(simple.implementationVersion)) || (this.implementationVendor == null ? simple.implementationVendor != null : !this.implementationVendor.equals(simple.implementationVendor)) || (this.sealBase == null ? simple.sealBase != null : !this.sealBase.equals(simple.sealBase)));
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Trivial implements Definition
        {
            INSTANCE;

            @AlwaysNull
            private static final String NO_VALUE;
            @AlwaysNull
            private static final URL NOT_SEALED;

            @Override
            public boolean isDefined() {
                return true;
            }

            @Override
            @MaybeNull
            public String getSpecificationTitle() {
                return NO_VALUE;
            }

            @Override
            @MaybeNull
            public String getSpecificationVersion() {
                return NO_VALUE;
            }

            @Override
            @MaybeNull
            public String getSpecificationVendor() {
                return NO_VALUE;
            }

            @Override
            @MaybeNull
            public String getImplementationTitle() {
                return NO_VALUE;
            }

            @Override
            @MaybeNull
            public String getImplementationVersion() {
                return NO_VALUE;
            }

            @Override
            public String getImplementationVendor() {
                return NO_VALUE;
            }

            @Override
            @MaybeNull
            public URL getSealBase() {
                return NOT_SEALED;
            }

            @Override
            public boolean isCompatibleTo(Package definedPackage) {
                return true;
            }

            static {
                NO_VALUE = null;
                NOT_SEALED = null;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Undefined implements Definition
        {
            INSTANCE;


            @Override
            public boolean isDefined() {
                return false;
            }

            @Override
            public String getSpecificationTitle() {
                throw new IllegalStateException("Cannot read property of undefined package");
            }

            @Override
            public String getSpecificationVersion() {
                throw new IllegalStateException("Cannot read property of undefined package");
            }

            @Override
            public String getSpecificationVendor() {
                throw new IllegalStateException("Cannot read property of undefined package");
            }

            @Override
            public String getImplementationTitle() {
                throw new IllegalStateException("Cannot read property of undefined package");
            }

            @Override
            public String getImplementationVersion() {
                throw new IllegalStateException("Cannot read property of undefined package");
            }

            @Override
            public String getImplementationVendor() {
                throw new IllegalStateException("Cannot read property of undefined package");
            }

            @Override
            public URL getSealBase() {
                throw new IllegalStateException("Cannot read property of undefined package");
            }

            @Override
            public boolean isCompatibleTo(Package definedPackage) {
                throw new IllegalStateException("Cannot check compatibility to undefined package");
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Trivial implements PackageDefinitionStrategy
    {
        INSTANCE;


        @Override
        public Definition define(ClassLoader classLoader, String packageName, String typeName) {
            return Definition.Trivial.INSTANCE;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum NoOp implements PackageDefinitionStrategy
    {
        INSTANCE;


        @Override
        public Definition define(ClassLoader classLoader, String packageName, String typeName) {
            return Definition.Undefined.INSTANCE;
        }
    }
}

