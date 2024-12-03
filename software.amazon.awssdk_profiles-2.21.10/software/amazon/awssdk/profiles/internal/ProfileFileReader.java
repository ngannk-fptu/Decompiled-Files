/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Pair
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.profiles.internal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.internal.ProfileSection;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Pair;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class ProfileFileReader {
    private static final Logger log = Logger.loggerFor(ProfileFileReader.class);
    private static final Pattern EMPTY_LINE = Pattern.compile("^[\t ]*$");
    private static final Pattern VALID_IDENTIFIER = Pattern.compile("^[A-Za-z0-9_\\-/.%@:\\+]*$");

    private ProfileFileReader() {
    }

    public static Map<String, Map<String, Map<String, String>>> parseFile(InputStream profileStream, ProfileFile.Type fileType) {
        ParserState state = new ParserState(fileType);
        BufferedReader profileReader = new BufferedReader(new InputStreamReader(profileStream, StandardCharsets.UTF_8));
        profileReader.lines().forEach(line -> ProfileFileReader.parseLine(state, line));
        return state.profiles;
    }

    private static void parseLine(ParserState state, String line) {
        ++state.currentLineNumber;
        if (ProfileFileReader.isEmptyLine(line) || ProfileFileReader.isCommentLine(line)) {
            return;
        }
        Optional<String> sectionDefined = ProfileFileReader.sectionDefinitionLine(line);
        if (sectionDefined.isPresent()) {
            state.sectionReadInProgress = sectionDefined.get();
            ProfileFileReader.readSectionProfileDefinitionLine(state, line);
        } else if (ProfileFileReader.isProfileDefinitionLine(line)) {
            state.sectionReadInProgress = "profiles";
            ProfileFileReader.readProfileDefinitionLine(state, line);
        } else if (ProfileFileReader.isPropertyContinuationLine(line)) {
            ProfileFileReader.readPropertyContinuationLine(state, line);
        } else {
            ProfileFileReader.readPropertyDefinitionLine(state, line);
        }
    }

    private static void readProfileDefinitionLine(ParserState state, String line) {
        String lineWithoutComments = ProfileFileReader.removeTrailingComments(line, "#", ";");
        String lineWithoutWhitespace = StringUtils.trim((String)lineWithoutComments);
        Validate.isTrue((boolean)lineWithoutWhitespace.endsWith("]"), (String)("Profile definition must end with ']' on line " + state.currentLineNumber), (Object[])new Object[0]);
        Optional<String> profileName = ProfileFileReader.parseProfileDefinition(state, lineWithoutWhitespace);
        ProfileFileReader.updateStateBasedOnProfileName(state, profileName);
    }

    private static void readPropertyDefinitionLine(ParserState state, String line) {
        if (state.ignoringCurrentProfile) {
            return;
        }
        Validate.isTrue((state.currentProfileBeingRead != null ? 1 : 0) != 0, (String)("Expected a profile definition on line " + state.currentLineNumber), (Object[])new Object[0]);
        String lineWithoutComments = ProfileFileReader.removeTrailingComments(line, " #", " ;", "\t#", "\t;");
        String lineWithoutWhitespace = StringUtils.trim((String)lineWithoutComments);
        Optional<Pair<String, String>> propertyDefinition = ProfileFileReader.parsePropertyDefinition(state, lineWithoutWhitespace);
        if (!propertyDefinition.isPresent()) {
            state.ignoringCurrentProperty = true;
            return;
        }
        Pair<String, String> property = propertyDefinition.get();
        if (((Map)((Map)state.profiles.get(state.sectionReadInProgress)).get(state.currentProfileBeingRead)).containsKey(property.left())) {
            log.warn(() -> "Warning: Duplicate property '" + (String)property.left() + "' detected on line " + state.currentLineNumber + ". The later one in the file will be used.");
        }
        state.currentPropertyBeingRead = (String)property.left();
        state.ignoringCurrentProperty = false;
        state.validatingContinuationsAsSubProperties = ((String)property.right()).equals("");
        ((Map)((Map)state.profiles.get(state.sectionReadInProgress)).get(state.currentProfileBeingRead)).put(property.left(), property.right());
    }

    private static void readPropertyContinuationLine(ParserState state, String line) {
        if (state.ignoringCurrentProfile || state.ignoringCurrentProperty) {
            return;
        }
        Validate.isTrue((state.currentProfileBeingRead != null && state.currentPropertyBeingRead != null ? 1 : 0) != 0, (String)("Expected a profile or property definition on line " + state.currentLineNumber), (Object[])new Object[0]);
        line = StringUtils.trim((String)line);
        Map profileProperties = (Map)((Map)state.profiles.get(state.sectionReadInProgress)).get(state.currentProfileBeingRead);
        String currentPropertyValue = (String)profileProperties.get(state.currentPropertyBeingRead);
        String newPropertyValue = currentPropertyValue + "\n" + line;
        if (state.validatingContinuationsAsSubProperties) {
            ProfileFileReader.parsePropertyDefinition(state, line);
        }
        profileProperties.put(state.currentPropertyBeingRead, newPropertyValue);
    }

    /*
     * Enabled aggressive block sorting
     */
    private static Optional<String> parseProfileDefinition(ParserState state, String lineWithoutWhitespace) {
        String standardizedProfileName;
        boolean hasProfilePrefix;
        String lineWithoutBrackets = lineWithoutWhitespace.substring(1, lineWithoutWhitespace.length() - 1);
        String rawProfileName = StringUtils.trim((String)lineWithoutBrackets);
        boolean bl = hasProfilePrefix = rawProfileName.startsWith("profile ") || rawProfileName.startsWith("profile\t");
        if (state.fileType == ProfileFile.Type.CONFIGURATION) {
            if (hasProfilePrefix) {
                standardizedProfileName = StringUtils.trim((String)rawProfileName.substring("profiles".length()));
            } else {
                if (!rawProfileName.equals("default")) {
                    log.warn(() -> "Ignoring profile '" + rawProfileName + "' on line " + state.currentLineNumber + " because it did not start with 'profile ' and it was not 'default'.");
                    return Optional.empty();
                }
                standardizedProfileName = "default";
            }
        } else {
            if (state.fileType != ProfileFile.Type.CREDENTIALS) {
                throw new IllegalStateException("Unknown profile file type: " + (Object)((Object)state.fileType));
            }
            standardizedProfileName = rawProfileName;
        }
        String profileName = StringUtils.trim((String)standardizedProfileName);
        if (!ProfileFileReader.isValidIdentifier(profileName)) {
            log.warn(() -> "Ignoring profile '" + standardizedProfileName + "' on line " + state.currentLineNumber + " because it was not alphanumeric with only these special characters: - / . % @ _ : +");
            return Optional.empty();
        }
        boolean isDefaultProfile = profileName.equals("default");
        boolean seenProfileBefore = ((Map)state.profiles.get("profiles")).containsKey(profileName);
        if (state.fileType == ProfileFile.Type.CONFIGURATION && isDefaultProfile && seenProfileBefore) {
            if (!hasProfilePrefix && state.seenDefaultProfileWithProfilePrefix) {
                log.warn(() -> "Ignoring profile '[default]' on line " + state.currentLineNumber + ", because '[profile default]' was already seen in the same file.");
                return Optional.empty();
            }
            if (hasProfilePrefix && !state.seenDefaultProfileWithProfilePrefix) {
                log.warn(() -> "Ignoring earlier-seen '[default]', because '[profile default]' was found on line " + state.currentLineNumber);
                ((Map)state.profiles.get("profiles")).remove("default");
            }
        }
        if (isDefaultProfile && hasProfilePrefix) {
            state.seenDefaultProfileWithProfilePrefix = true;
        }
        return Optional.of(profileName);
    }

    private static Optional<Pair<String, String>> parsePropertyDefinition(ParserState state, String line) {
        int firstEqualsLocation = line.indexOf(61);
        Validate.isTrue((firstEqualsLocation != -1 ? 1 : 0) != 0, (String)("Expected an '=' sign defining a property on line " + state.currentLineNumber), (Object[])new Object[0]);
        String propertyKey = StringUtils.trim((String)line.substring(0, firstEqualsLocation));
        String propertyValue = StringUtils.trim((String)line.substring(firstEqualsLocation + 1));
        Validate.isTrue((!propertyKey.isEmpty() ? 1 : 0) != 0, (String)("Property did not have a name on line " + state.currentLineNumber), (Object[])new Object[0]);
        if (!ProfileFileReader.isValidIdentifier(propertyKey)) {
            log.warn(() -> "Ignoring property '" + propertyKey + "' on line " + state.currentLineNumber + " because its name was not alphanumeric with only these special characters: - / . % @ _ : +");
            return Optional.empty();
        }
        return Optional.of(Pair.of((Object)propertyKey, (Object)propertyValue));
    }

    private static String removeTrailingComments(String line, String ... commentPatterns) {
        return line.substring(0, ProfileFileReader.findEarliestMatch(line, commentPatterns));
    }

    private static int findEarliestMatch(String line, String ... searchPatterns) {
        return Stream.of(searchPatterns).mapToInt(line::indexOf).filter(location -> location >= 0).min().orElseGet(line::length);
    }

    private static boolean isEmptyLine(String line) {
        return EMPTY_LINE.matcher(line).matches();
    }

    private static boolean isCommentLine(String line) {
        return line.startsWith("#") || line.startsWith(";");
    }

    private static boolean isProfileDefinitionLine(String line) {
        return line.startsWith("[");
    }

    private static boolean isPropertyContinuationLine(String line) {
        return line.startsWith(" ") || line.startsWith("\t");
    }

    private static boolean isValidIdentifier(String value) {
        return VALID_IDENTIFIER.matcher(value).matches();
    }

    private static Optional<String> sectionDefinitionLine(String line) {
        if (line.startsWith("[")) {
            String lineWithoutBrackets = line.substring(1, line.length() - 1);
            String rawProfileName = StringUtils.trim((String)lineWithoutBrackets);
            return Arrays.stream(ProfileSection.values()).filter(x -> !"profiles".equals(x.getSectionTitle())).map(title -> title.getSectionTitle()).filter(reservedTitle -> rawProfileName.startsWith(String.format("%s ", reservedTitle)) || rawProfileName.startsWith(String.format("%s\t", reservedTitle))).findFirst();
        }
        return Optional.empty();
    }

    private static void readSectionProfileDefinitionLine(ParserState state, String line) {
        String lineWithoutComments = ProfileFileReader.removeTrailingComments(line, "#", ";");
        String lineWithoutWhitespace = StringUtils.trim((String)lineWithoutComments);
        Validate.isTrue((boolean)lineWithoutWhitespace.endsWith("]"), (String)("Section definition must end with ']' on line " + state.currentLineNumber), (Object[])new Object[0]);
        Optional<String> profileName = ProfileFileReader.parseSpecialProfileDefinition(state, lineWithoutWhitespace);
        ProfileFileReader.updateStateBasedOnProfileName(state, profileName);
    }

    private static void updateStateBasedOnProfileName(ParserState state, Optional<String> profileName) {
        if (!profileName.isPresent()) {
            state.ignoringCurrentProfile = true;
            return;
        }
        state.currentProfileBeingRead = profileName.get();
        state.currentPropertyBeingRead = null;
        state.ignoringCurrentProfile = false;
        state.ignoringCurrentProperty = false;
        ((Map)state.profiles.get(state.sectionReadInProgress)).computeIfAbsent(profileName.get(), i -> new LinkedHashMap());
    }

    private static Optional<String> parseSpecialProfileDefinition(ParserState state, String lineWithoutWhitespace) {
        String lineWithoutBrackets = lineWithoutWhitespace.substring(1, lineWithoutWhitespace.length() - 1);
        String rawProfileName = StringUtils.trim((String)lineWithoutBrackets);
        String profilePrefix = Arrays.stream(ProfileSection.values()).filter(x -> !x.getSectionTitle().equals("profiles")).map(x -> x.getSectionTitle()).filter(title -> rawProfileName.startsWith(String.format("%s ", title)) || rawProfileName.startsWith(String.format("%s\t", title))).findFirst().orElse(null);
        if (state.fileType != ProfileFile.Type.CONFIGURATION || profilePrefix == null) {
            return Optional.empty();
        }
        String standardizedProfileName = StringUtils.trim((String)rawProfileName.substring(profilePrefix.length()));
        String profilePrefixName = StringUtils.trim((String)standardizedProfileName);
        if (!ProfileFileReader.isValidIdentifier(profilePrefixName)) {
            log.warn(() -> "Ignoring " + standardizedProfileName + "' on line " + state.currentLineNumber + " because it was not alphanumeric with only these special characters: - / . % @ _ : +");
            return Optional.empty();
        }
        return Optional.of(profilePrefixName);
    }

    private static final class ParserState {
        private final ProfileFile.Type fileType;
        private int currentLineNumber = 0;
        private String currentProfileBeingRead = null;
        private String currentPropertyBeingRead = null;
        private boolean ignoringCurrentProfile = false;
        private boolean ignoringCurrentProperty = false;
        private boolean validatingContinuationsAsSubProperties = false;
        private boolean seenDefaultProfileWithProfilePrefix = false;
        private String sectionReadInProgress;
        private Map<String, Map<String, Map<String, String>>> profiles = new LinkedHashMap<String, Map<String, Map<String, String>>>();

        private ParserState(ProfileFile.Type fileType) {
            this.profiles.put("profiles", new LinkedHashMap());
            Arrays.stream(ProfileSection.values()).forEach(profileSection -> {
                Map cfr_ignored_0 = this.profiles.put(profileSection.getSectionTitle(), new LinkedHashMap());
            });
            this.fileType = fileType;
        }
    }
}

