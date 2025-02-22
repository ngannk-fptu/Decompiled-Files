/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.StandardSystemProperty;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ElementTypesAreNonnullByDefault;
import com.google.common.io.IgnoreJRERequirement;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
abstract class TempFileCreator {
    static final TempFileCreator INSTANCE = TempFileCreator.pickSecureCreator();

    abstract File createTempDir();

    abstract File createTempFile(String var1) throws IOException;

    private static TempFileCreator pickSecureCreator() {
        try {
            Class.forName("java.nio.file.Path");
            return new JavaNioCreator();
        }
        catch (ClassNotFoundException classNotFoundException) {
            try {
                int version = (Integer)Class.forName("android.os.Build$VERSION").getField("SDK_INT").get(null);
                int jellyBean = (Integer)Class.forName("android.os.Build$VERSION_CODES").getField("JELLY_BEAN").get(null);
                if (version < jellyBean) {
                    return new ThrowingCreator();
                }
            }
            catch (NoSuchFieldException e) {
                return new ThrowingCreator();
            }
            catch (ClassNotFoundException e) {
                return new ThrowingCreator();
            }
            catch (IllegalAccessException e) {
                return new ThrowingCreator();
            }
            return new JavaIoCreator();
        }
    }

    @IgnoreJRERequirement
    @VisibleForTesting
    static void testMakingUserPermissionsFromScratch() throws IOException {
        FileAttribute<?> unused = JavaNioCreator.userPermissions().get();
    }

    private TempFileCreator() {
    }

    private static final class ThrowingCreator
    extends TempFileCreator {
        private static final String MESSAGE = "Guava cannot securely create temporary files or directories under SDK versions before Jelly Bean. You can create one yourself, either in the insecure default directory or in a more secure directory, such as context.getCacheDir(). For more information, see the Javadoc for Files.createTempDir().";

        private ThrowingCreator() {
        }

        @Override
        File createTempDir() {
            throw new IllegalStateException(MESSAGE);
        }

        @Override
        File createTempFile(String prefix) throws IOException {
            throw new IOException(MESSAGE);
        }
    }

    private static final class JavaIoCreator
    extends TempFileCreator {
        private static final int TEMP_DIR_ATTEMPTS = 10000;

        private JavaIoCreator() {
        }

        @Override
        File createTempDir() {
            File baseDir = new File(StandardSystemProperty.JAVA_IO_TMPDIR.value());
            String baseName = System.currentTimeMillis() + "-";
            for (int counter = 0; counter < 10000; ++counter) {
                File tempDir = new File(baseDir, baseName + counter);
                if (!tempDir.mkdir()) continue;
                return tempDir;
            }
            throw new IllegalStateException("Failed to create directory within 10000 attempts (tried " + baseName + "0 to " + baseName + 9999 + ')');
        }

        @Override
        File createTempFile(String prefix) throws IOException {
            return File.createTempFile(prefix, null, null);
        }
    }

    @IgnoreJRERequirement
    private static final class JavaNioCreator
    extends TempFileCreator {
        private static final PermissionSupplier filePermissions;
        private static final PermissionSupplier directoryPermissions;

        private JavaNioCreator() {
        }

        @Override
        File createTempDir() {
            try {
                return Files.createTempDirectory(Paths.get(StandardSystemProperty.JAVA_IO_TMPDIR.value(), new String[0]), null, directoryPermissions.get()).toFile();
            }
            catch (IOException e) {
                throw new IllegalStateException("Failed to create directory", e);
            }
        }

        @Override
        File createTempFile(String prefix) throws IOException {
            return Files.createTempFile(Paths.get(StandardSystemProperty.JAVA_IO_TMPDIR.value(), new String[0]), prefix, null, filePermissions.get()).toFile();
        }

        private static PermissionSupplier userPermissions() {
            try {
                UserPrincipal user = FileSystems.getDefault().getUserPrincipalLookupService().lookupPrincipalByName(JavaNioCreator.getUsername());
                final ImmutableList<AclEntry> acl = ImmutableList.of(AclEntry.newBuilder().setType(AclEntryType.ALLOW).setPrincipal(user).setPermissions(EnumSet.allOf(AclEntryPermission.class)).setFlags(AclEntryFlag.DIRECTORY_INHERIT, AclEntryFlag.FILE_INHERIT).build());
                FileAttribute<ImmutableList<AclEntry>> attribute = new FileAttribute<ImmutableList<AclEntry>>(){

                    @Override
                    public String name() {
                        return "acl:acl";
                    }

                    @Override
                    public ImmutableList<AclEntry> value() {
                        return acl;
                    }
                };
                return () -> attribute;
            }
            catch (IOException e) {
                return () -> {
                    throw new IOException("Could not find user", e);
                };
            }
        }

        private static String getUsername() {
            String fromSystemProperty = Objects.requireNonNull(StandardSystemProperty.USER_NAME.value());
            try {
                Class<?> processHandleClass = Class.forName("java.lang.ProcessHandle");
                Class<?> processHandleInfoClass = Class.forName("java.lang.ProcessHandle$Info");
                Class<?> optionalClass = Class.forName("java.util.Optional");
                Method currentMethod = processHandleClass.getMethod("current", new Class[0]);
                Method infoMethod = processHandleClass.getMethod("info", new Class[0]);
                Method userMethod = processHandleInfoClass.getMethod("user", new Class[0]);
                Method orElseMethod = optionalClass.getMethod("orElse", Object.class);
                Object current = currentMethod.invoke(null, new Object[0]);
                Object info = infoMethod.invoke(current, new Object[0]);
                Object user = userMethod.invoke(info, new Object[0]);
                return (String)Objects.requireNonNull(orElseMethod.invoke(user, fromSystemProperty));
            }
            catch (ClassNotFoundException runningUnderAndroidOrJava8) {
                return fromSystemProperty;
            }
            catch (InvocationTargetException e) {
                Throwables.throwIfUnchecked(e.getCause());
                return fromSystemProperty;
            }
            catch (NoSuchMethodException shouldBeImpossible) {
                return fromSystemProperty;
            }
            catch (IllegalAccessException shouldBeImpossible) {
                return fromSystemProperty;
            }
        }

        static {
            Set<String> views = FileSystems.getDefault().supportedFileAttributeViews();
            if (views.contains("posix")) {
                filePermissions = () -> PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));
                directoryPermissions = () -> PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
            } else {
                filePermissions = views.contains("acl") ? (directoryPermissions = JavaNioCreator.userPermissions()) : (directoryPermissions = () -> {
                    throw new IOException("unrecognized FileSystem type " + FileSystems.getDefault());
                });
            }
        }

        @IgnoreJRERequirement
        private static interface PermissionSupplier {
            public FileAttribute<?> get() throws IOException;
        }
    }
}

