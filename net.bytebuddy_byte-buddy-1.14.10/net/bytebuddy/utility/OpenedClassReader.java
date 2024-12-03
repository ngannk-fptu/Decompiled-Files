/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility;

import java.security.AccessController;
import java.security.PrivilegedAction;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.utility.privilege.GetSystemPropertyAction;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OpenedClassReader {
    public static final String EXPERIMENTAL_PROPERTY = "net.bytebuddy.experimental";
    public static final boolean EXPERIMENTAL;
    public static final int ASM_API;
    private static final boolean ACCESS_CONTROLLER;

    private OpenedClassReader() {
        throw new UnsupportedOperationException("This class is a utility class and not supposed to be instantiated");
    }

    @AccessControllerPlugin.Enhance
    private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
        PrivilegedAction<T> action;
        if (ACCESS_CONTROLLER) {
            return AccessController.doPrivileged(privilegedAction);
        }
        return action.run();
    }

    public static ClassReader of(byte[] binaryRepresentation) {
        ClassFileVersion latest;
        ClassFileVersion classFileVersion = ClassFileVersion.ofClassFile(binaryRepresentation);
        if (classFileVersion.isGreaterThan(latest = ClassFileVersion.latest())) {
            if (EXPERIMENTAL) {
                binaryRepresentation[4] = (byte)(latest.getMinorVersion() >>> 8);
                binaryRepresentation[5] = (byte)latest.getMinorVersion();
                binaryRepresentation[6] = (byte)(latest.getMajorVersion() >>> 8);
                binaryRepresentation[7] = (byte)latest.getMajorVersion();
                ClassReader classReader = new ClassReader(binaryRepresentation);
                binaryRepresentation[4] = (byte)(classFileVersion.getMinorVersion() >>> 8);
                binaryRepresentation[5] = (byte)classFileVersion.getMinorVersion();
                binaryRepresentation[6] = (byte)(classFileVersion.getMajorVersion() >>> 8);
                binaryRepresentation[7] = (byte)classFileVersion.getMajorVersion();
                return classReader;
            }
            throw new IllegalArgumentException(classFileVersion + " is not supported by the current version of Byte Buddy which officially supports " + latest + " - update Byte Buddy or set " + EXPERIMENTAL_PROPERTY + " as a VM property");
        }
        return new ClassReader(binaryRepresentation);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    static {
        boolean experimental;
        try {
            Class.forName("java.security.AccessController", false, null);
            ACCESS_CONTROLLER = Boolean.parseBoolean(System.getProperty("net.bytebuddy.securitymanager", "true"));
        }
        catch (ClassNotFoundException classNotFoundException) {
            ACCESS_CONTROLLER = false;
        }
        catch (SecurityException securityException) {
            ACCESS_CONTROLLER = true;
        }
        try {
            experimental = Boolean.parseBoolean(OpenedClassReader.doPrivileged(new GetSystemPropertyAction(EXPERIMENTAL_PROPERTY)));
        }
        catch (Exception ignored) {
            experimental = false;
        }
        EXPERIMENTAL = experimental;
        ASM_API = 589824;
    }
}

