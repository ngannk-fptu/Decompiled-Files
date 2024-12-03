/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.profiles;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFileLocation;
import software.amazon.awssdk.profiles.internal.ProfileFileReader;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.SdkBuilder;

@SdkPublicApi
public final class ProfileFile {
    public static final String PROFILES_SECTION_TITLE = "profiles";
    private final Map<String, Map<String, Profile>> profilesAndSectionsMap;

    private ProfileFile(Map<String, Map<String, Map<String, String>>> profilesSectionMap) {
        Validate.paramNotNull(profilesSectionMap, "profilesSectionMap");
        this.profilesAndSectionsMap = this.convertToProfilesSectionsMap(profilesSectionMap);
    }

    public Optional<Profile> getSection(String sectionName, String sectionTitle) {
        Map<String, Profile> sectionMap = this.profilesAndSectionsMap.get(sectionName);
        if (sectionMap != null) {
            return Optional.ofNullable(sectionMap.get(sectionTitle));
        }
        return Optional.empty();
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static Aggregator aggregator() {
        return new Aggregator();
    }

    public static ProfileFile defaultProfileFile() {
        return ProfileFile.aggregator().applyMutation(ProfileFile::addCredentialsFile).applyMutation(ProfileFile::addConfigFile).build();
    }

    public Optional<Profile> profile(String profileName) {
        Map<String, Profile> profileMap = this.profilesAndSectionsMap.get(PROFILES_SECTION_TITLE);
        return profileMap != null ? Optional.ofNullable(profileMap.get(profileName)) : Optional.empty();
    }

    public Map<String, Profile> profiles() {
        Map<String, Profile> profileMap = this.profilesAndSectionsMap.get(PROFILES_SECTION_TITLE);
        return profileMap != null ? Collections.unmodifiableMap(profileMap) : Collections.emptyMap();
    }

    public String toString() {
        return ToString.builder("ProfileFile").add("profilesAndSectionsMap", this.profilesAndSectionsMap.values()).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ProfileFile that = (ProfileFile)o;
        return Objects.equals(this.profilesAndSectionsMap, that.profilesAndSectionsMap);
    }

    public int hashCode() {
        return Objects.hashCode(this.profilesAndSectionsMap);
    }

    private static void addCredentialsFile(Aggregator builder) {
        ProfileFileLocation.credentialsFileLocation().ifPresent(l -> builder.addFile(ProfileFile.builder().content((Path)l).type(Type.CREDENTIALS).build()));
    }

    private static void addConfigFile(Aggregator builder) {
        ProfileFileLocation.configurationFileLocation().ifPresent(l -> builder.addFile(ProfileFile.builder().content((Path)l).type(Type.CONFIGURATION).build()));
    }

    private Map<String, Map<String, Profile>> convertToProfilesSectionsMap(Map<String, Map<String, Map<String, String>>> sortedProfilesSectionMap) {
        LinkedHashMap<String, Map<String, Profile>> result = new LinkedHashMap<String, Map<String, Profile>>();
        sortedProfilesSectionMap.entrySet().forEach(sections -> {
            result.put((String)sections.getKey(), new LinkedHashMap());
            Map stringProfileMap = (Map)result.get(sections.getKey());
            ((Map)sections.getValue()).entrySet().forEach(section -> {
                Profile profile2 = Profile.builder().name((String)section.getKey()).properties((Map)section.getValue()).build();
                stringProfileMap.put(section.getKey(), profile2);
            });
        });
        return result;
    }

    public static final class Aggregator
    implements SdkBuilder<Aggregator, ProfileFile> {
        private List<ProfileFile> files = new ArrayList<ProfileFile>();

        public Aggregator addFile(ProfileFile file) {
            this.files.add(file);
            return this;
        }

        @Override
        public ProfileFile build() {
            LinkedHashMap aggregateRawProfiles = new LinkedHashMap();
            for (int i = this.files.size() - 1; i >= 0; --i) {
                this.files.get(i).profilesAndSectionsMap.entrySet().forEach(sectionKeyValue -> this.addToAggregate(aggregateRawProfiles, (Map)sectionKeyValue.getValue(), (String)sectionKeyValue.getKey()));
            }
            return new ProfileFile(aggregateRawProfiles);
        }

        private void addToAggregate(Map<String, Map<String, Map<String, String>>> aggregateRawProfiles, Map<String, Profile> profiles, String sectionName) {
            aggregateRawProfiles.putIfAbsent(sectionName, new LinkedHashMap());
            Map<String, Map<String, String>> profileMap = aggregateRawProfiles.get(sectionName);
            for (Map.Entry<String, Profile> profile2 : profiles.entrySet()) {
                profileMap.compute(profile2.getKey(), (k, current) -> {
                    if (current == null) {
                        return new HashMap<String, String>(((Profile)profile2.getValue()).properties());
                    }
                    current.putAll(((Profile)profile2.getValue()).properties());
                    return current;
                });
            }
        }
    }

    private static final class BuilderImpl
    implements Builder {
        private InputStream content;
        private Path contentLocation;
        private Type type;

        private BuilderImpl() {
        }

        @Override
        public Builder content(InputStream contentStream) {
            this.contentLocation = null;
            this.content = contentStream;
            return this;
        }

        public void setContent(InputStream contentStream) {
            this.content(contentStream);
        }

        @Override
        public Builder content(Path contentLocation) {
            Validate.paramNotNull(contentLocation, "profileLocation");
            Validate.validState(Files.exists(contentLocation, new LinkOption[0]), "Profile file '%s' does not exist.", contentLocation);
            this.content = null;
            this.contentLocation = contentLocation;
            return this;
        }

        public void setContentLocation(Path contentLocation) {
            this.content(contentLocation);
        }

        @Override
        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public void setType(Type type) {
            this.type(type);
        }

        @Override
        public ProfileFile build() {
            InputStream stream = this.content != null ? this.content : FunctionalUtils.invokeSafely(() -> Files.newInputStream(this.contentLocation, new OpenOption[0]));
            Validate.paramNotNull(this.type, "type");
            Validate.paramNotNull(stream, "content");
            try {
                ProfileFile profileFile = new ProfileFile(ProfileFileReader.parseFile(stream, this.type));
                return profileFile;
            }
            finally {
                IoUtils.closeQuietly(stream, null);
            }
        }
    }

    public static interface Builder
    extends SdkBuilder<Builder, ProfileFile> {
        public Builder content(InputStream var1);

        public Builder content(Path var1);

        public Builder type(Type var1);

        @Override
        public ProfileFile build();
    }

    public static enum Type {
        CONFIGURATION,
        CREDENTIALS;

    }
}

