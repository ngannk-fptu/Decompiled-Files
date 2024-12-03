/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.properties.EncryptionProperties
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.crypto;

import com.atlassian.crowd.common.properties.EncryptionProperties;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionKeyFilePermissionChanger {
    private static final ImmutableSet<PosixFilePermission> POSIX_OWNER_PERMISSIONS = ImmutableSet.of((Object)((Object)PosixFilePermission.OWNER_READ), (Object)((Object)PosixFilePermission.OWNER_WRITE), (Object)((Object)PosixFilePermission.OWNER_EXECUTE));
    private static final Logger log = LoggerFactory.getLogger(EncryptionKeyFilePermissionChanger.class);

    public void makeFileReadableOnlyByOwner(String keyPath) {
        if (!((Boolean)EncryptionProperties.SET_ENCRYPTION_KEYS_OWNERSHIP_ATTRIBUTES.getValue()).booleanValue()) {
            return;
        }
        try {
            Path path = Paths.get(keyPath, new String[0]);
            PosixFileAttributeView posixFileAttributeView = Files.getFileAttributeView(path, PosixFileAttributeView.class, new LinkOption[0]);
            if (posixFileAttributeView != null) {
                this.removeNonOwnerPosixAttributes(posixFileAttributeView, path);
                return;
            }
            log.warn("Cannot set access attributes of keyFile {}", (Object)keyPath);
        }
        catch (IOException e) {
            log.error("Cannot set access attributes of keyFile {}", (Object)keyPath, (Object)e);
        }
    }

    private void removeNonOwnerPosixAttributes(PosixFileAttributeView posixFileAttributeView, Path path) throws IOException {
        Set<PosixFilePermission> permissions = posixFileAttributeView.readAttributes().permissions();
        if (!permissions.contains((Object)PosixFilePermission.OWNER_READ)) {
            log.warn("Will skip permissions update for {}, because file would be not readable by owner", (Object)path);
            return;
        }
        Set<PosixFilePermission> updatedPermissions = permissions.stream().filter(arg_0 -> POSIX_OWNER_PERMISSIONS.contains(arg_0)).collect(Collectors.toSet());
        posixFileAttributeView.setPermissions(updatedPermissions);
    }
}

