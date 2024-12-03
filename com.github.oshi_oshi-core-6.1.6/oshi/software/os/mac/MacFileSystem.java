/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.PointerType
 *  com.sun.jna.platform.mac.CoreFoundation
 *  com.sun.jna.platform.mac.CoreFoundation$CFDictionaryRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFMutableDictionaryRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFStringRef
 *  com.sun.jna.platform.mac.DiskArbitration
 *  com.sun.jna.platform.mac.DiskArbitration$DADiskRef
 *  com.sun.jna.platform.mac.DiskArbitration$DASessionRef
 *  com.sun.jna.platform.mac.IOKit$IOIterator
 *  com.sun.jna.platform.mac.IOKit$IORegistryEntry
 *  com.sun.jna.platform.mac.IOKitUtil
 *  com.sun.jna.platform.mac.SystemB
 *  com.sun.jna.platform.mac.SystemB$Statfs
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.software.os.mac;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.DiskArbitration;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.platform.mac.SystemB;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.mac.MacOSFileStore;
import oshi.util.FileSystemUtil;
import oshi.util.platform.mac.CFUtil;
import oshi.util.platform.mac.SysctlUtil;

@ThreadSafe
public class MacFileSystem
extends AbstractFileSystem {
    private static final Logger LOG = LoggerFactory.getLogger(MacFileSystem.class);
    public static final String OSHI_MAC_FS_PATH_EXCLUDES = "oshi.os.mac.filesystem.path.excludes";
    public static final String OSHI_MAC_FS_PATH_INCLUDES = "oshi.os.mac.filesystem.path.includes";
    public static final String OSHI_MAC_FS_VOLUME_EXCLUDES = "oshi.os.mac.filesystem.volume.excludes";
    public static final String OSHI_MAC_FS_VOLUME_INCLUDES = "oshi.os.mac.filesystem.volume.includes";
    private static final List<PathMatcher> FS_PATH_EXCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.mac.filesystem.path.excludes");
    private static final List<PathMatcher> FS_PATH_INCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.mac.filesystem.path.includes");
    private static final List<PathMatcher> FS_VOLUME_EXCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.mac.filesystem.volume.excludes");
    private static final List<PathMatcher> FS_VOLUME_INCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.mac.filesystem.volume.includes");
    private static final Pattern LOCAL_DISK = Pattern.compile("/dev/disk\\d");
    private static final int MNT_RDONLY = 1;
    private static final int MNT_SYNCHRONOUS = 2;
    private static final int MNT_NOEXEC = 4;
    private static final int MNT_NOSUID = 8;
    private static final int MNT_NODEV = 16;
    private static final int MNT_UNION = 32;
    private static final int MNT_ASYNC = 64;
    private static final int MNT_CPROTECT = 128;
    private static final int MNT_EXPORTED = 256;
    private static final int MNT_QUARANTINE = 1024;
    private static final int MNT_LOCAL = 4096;
    private static final int MNT_QUOTA = 8192;
    private static final int MNT_ROOTFS = 16384;
    private static final int MNT_DOVOLFS = 32768;
    private static final int MNT_DONTBROWSE = 0x100000;
    private static final int MNT_IGNORE_OWNERSHIP = 0x200000;
    private static final int MNT_AUTOMOUNTED = 0x400000;
    private static final int MNT_JOURNALED = 0x800000;
    private static final int MNT_NOUSERXATTR = 0x1000000;
    private static final int MNT_DEFWRITE = 0x2000000;
    private static final int MNT_MULTILABEL = 0x4000000;
    private static final int MNT_NOATIME = 0x10000000;
    private static final Map<Integer, String> OPTIONS_MAP = new HashMap<Integer, String>();

    @Override
    public List<OSFileStore> getFileStores(boolean localOnly) {
        return MacFileSystem.getFileStoreMatching(null, localOnly);
    }

    static List<OSFileStore> getFileStoreMatching(String nameToMatch) {
        return MacFileSystem.getFileStoreMatching(nameToMatch, false);
    }

    private static List<OSFileStore> getFileStoreMatching(String nameToMatch, boolean localOnly) {
        ArrayList<OSFileStore> fsList = new ArrayList<OSFileStore>();
        int numfs = SystemB.INSTANCE.getfsstat64(null, 0, 0);
        if (numfs > 0) {
            DiskArbitration.DASessionRef session = DiskArbitration.INSTANCE.DASessionCreate(CoreFoundation.INSTANCE.CFAllocatorGetDefault());
            if (session == null) {
                LOG.error("Unable to open session to DiskArbitration framework.");
            } else {
                CoreFoundation.CFStringRef daVolumeNameKey = CoreFoundation.CFStringRef.createCFString((String)"DAVolumeName");
                SystemB.Statfs[] fs = new SystemB.Statfs[numfs];
                numfs = SystemB.INSTANCE.getfsstat64(fs, numfs * new SystemB.Statfs().size(), 16);
                for (int f = 0; f < numfs; ++f) {
                    String volume = Native.toString((byte[])fs[f].f_mntfromname, (Charset)StandardCharsets.UTF_8);
                    String path = Native.toString((byte[])fs[f].f_mntonname, (Charset)StandardCharsets.UTF_8);
                    String type = Native.toString((byte[])fs[f].f_fstypename, (Charset)StandardCharsets.UTF_8);
                    int flags = fs[f].f_flags;
                    if (localOnly && (flags & 0x1000) == 0 || !path.equals("/") && (PSEUDO_FS_TYPES.contains(type) || FileSystemUtil.isFileStoreExcluded(path, volume, FS_PATH_INCLUDES, FS_PATH_EXCLUDES, FS_VOLUME_INCLUDES, FS_VOLUME_EXCLUDES))) continue;
                    String description = "Volume";
                    if (LOCAL_DISK.matcher(volume).matches()) {
                        description = "Local Disk";
                    } else if (volume.startsWith("localhost:") || volume.startsWith("//") || volume.startsWith("smb://") || NETWORK_FS_TYPES.contains(type)) {
                        description = "Network Drive";
                    }
                    File file = new File(path);
                    String name = file.getName();
                    if (name.isEmpty()) {
                        name = file.getPath();
                    }
                    if (nameToMatch != null && !nameToMatch.equals(name)) continue;
                    StringBuilder options = new StringBuilder((1 & flags) == 0 ? "rw" : "ro");
                    String moreOptions = OPTIONS_MAP.entrySet().stream().filter(e -> ((Integer)e.getKey() & flags) > 0).map(Map.Entry::getValue).collect(Collectors.joining(","));
                    if (!moreOptions.isEmpty()) {
                        options.append(',').append(moreOptions);
                    }
                    String uuid = "";
                    String bsdName = volume.replace("/dev/disk", "disk");
                    if (bsdName.startsWith("disk")) {
                        IOKit.IOIterator fsIter;
                        CoreFoundation.CFMutableDictionaryRef matchingDict;
                        DiskArbitration.DADiskRef disk = DiskArbitration.INSTANCE.DADiskCreateFromBSDName(CoreFoundation.INSTANCE.CFAllocatorGetDefault(), session, volume);
                        if (disk != null) {
                            CoreFoundation.CFDictionaryRef diskInfo = DiskArbitration.INSTANCE.DADiskCopyDescription(disk);
                            if (diskInfo != null) {
                                Pointer result = diskInfo.getValue((PointerType)daVolumeNameKey);
                                name = CFUtil.cfPointerToString(result);
                                diskInfo.release();
                            }
                            disk.release();
                        }
                        if ((matchingDict = IOKitUtil.getBSDNameMatchingDict((String)bsdName)) != null && (fsIter = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)matchingDict)) != null) {
                            IOKit.IORegistryEntry fsEntry = fsIter.next();
                            if (fsEntry != null && fsEntry.conformsTo("IOMedia")) {
                                uuid = fsEntry.getStringProperty("UUID");
                                if (uuid != null) {
                                    uuid = uuid.toLowerCase();
                                }
                                fsEntry.release();
                            }
                            fsIter.release();
                        }
                    }
                    fsList.add(new MacOSFileStore(name, volume, name, path, options.toString(), uuid == null ? "" : uuid, "", description, type, file.getFreeSpace(), file.getUsableSpace(), file.getTotalSpace(), fs[f].f_ffree, fs[f].f_files));
                }
                daVolumeNameKey.release();
                session.release();
            }
        }
        return fsList;
    }

    @Override
    public long getOpenFileDescriptors() {
        return SysctlUtil.sysctl("kern.num_files", 0);
    }

    @Override
    public long getMaxFileDescriptors() {
        return SysctlUtil.sysctl("kern.maxfiles", 0);
    }

    static {
        OPTIONS_MAP.put(2, "synchronous");
        OPTIONS_MAP.put(4, "noexec");
        OPTIONS_MAP.put(8, "nosuid");
        OPTIONS_MAP.put(16, "nodev");
        OPTIONS_MAP.put(32, "union");
        OPTIONS_MAP.put(64, "asynchronous");
        OPTIONS_MAP.put(128, "content-protection");
        OPTIONS_MAP.put(256, "exported");
        OPTIONS_MAP.put(1024, "quarantined");
        OPTIONS_MAP.put(4096, "local");
        OPTIONS_MAP.put(8192, "quotas");
        OPTIONS_MAP.put(16384, "rootfs");
        OPTIONS_MAP.put(32768, "volfs");
        OPTIONS_MAP.put(0x100000, "nobrowse");
        OPTIONS_MAP.put(0x200000, "noowners");
        OPTIONS_MAP.put(0x400000, "automounted");
        OPTIONS_MAP.put(0x800000, "journaled");
        OPTIONS_MAP.put(0x1000000, "nouserxattr");
        OPTIONS_MAP.put(0x2000000, "defwrite");
        OPTIONS_MAP.put(0x4000000, "multilabel");
        OPTIONS_MAP.put(0x10000000, "noatime");
    }
}

