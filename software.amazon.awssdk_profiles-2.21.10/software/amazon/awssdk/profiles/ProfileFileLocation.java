/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.UserHomeDirectoryUtils
 */
package software.amazon.awssdk.profiles;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.utils.UserHomeDirectoryUtils;

@SdkPublicApi
public final class ProfileFileLocation {
    private static final Pattern HOME_DIRECTORY_PATTERN = Pattern.compile("^~(/|" + Pattern.quote(FileSystems.getDefault().getSeparator()) + ").*$");

    private ProfileFileLocation() {
    }

    public static Path configurationFilePath() {
        return ProfileFileLocation.resolveProfileFilePath(ProfileFileSystemSetting.AWS_CONFIG_FILE.getStringValue().orElseGet(() -> Paths.get(UserHomeDirectoryUtils.userHomeDirectory(), ".aws", "config").toString()));
    }

    public static Path credentialsFilePath() {
        return ProfileFileLocation.resolveProfileFilePath(ProfileFileSystemSetting.AWS_SHARED_CREDENTIALS_FILE.getStringValue().orElseGet(() -> Paths.get(UserHomeDirectoryUtils.userHomeDirectory(), ".aws", "credentials").toString()));
    }

    public static Optional<Path> configurationFileLocation() {
        return ProfileFileLocation.resolveIfExists(ProfileFileLocation.configurationFilePath());
    }

    public static Optional<Path> credentialsFileLocation() {
        return ProfileFileLocation.resolveIfExists(ProfileFileLocation.credentialsFilePath());
    }

    private static Path resolveProfileFilePath(String path) {
        if (HOME_DIRECTORY_PATTERN.matcher(path).matches()) {
            path = UserHomeDirectoryUtils.userHomeDirectory() + path.substring(1);
        }
        return Paths.get(path, new String[0]);
    }

    private static Optional<Path> resolveIfExists(Path path) {
        return Optional.ofNullable(path).filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).filter(Files::isReadable);
    }
}

