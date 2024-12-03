/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.utility.OpenedClassReader;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ClassFileVersion
implements Comparable<ClassFileVersion>,
Serializable {
    private static final long serialVersionUID = 1L;
    protected static final int BASE_VERSION = 44;
    public static final ClassFileVersion JAVA_V1;
    public static final ClassFileVersion JAVA_V2;
    public static final ClassFileVersion JAVA_V3;
    public static final ClassFileVersion JAVA_V4;
    public static final ClassFileVersion JAVA_V5;
    public static final ClassFileVersion JAVA_V6;
    public static final ClassFileVersion JAVA_V7;
    public static final ClassFileVersion JAVA_V8;
    public static final ClassFileVersion JAVA_V9;
    public static final ClassFileVersion JAVA_V10;
    public static final ClassFileVersion JAVA_V11;
    public static final ClassFileVersion JAVA_V12;
    public static final ClassFileVersion JAVA_V13;
    public static final ClassFileVersion JAVA_V14;
    public static final ClassFileVersion JAVA_V15;
    public static final ClassFileVersion JAVA_V16;
    public static final ClassFileVersion JAVA_V17;
    public static final ClassFileVersion JAVA_V18;
    public static final ClassFileVersion JAVA_V19;
    public static final ClassFileVersion JAVA_V20;
    public static final ClassFileVersion JAVA_V21;
    public static final ClassFileVersion JAVA_V22;
    private static final VersionLocator VERSION_LOCATOR;
    private final int versionNumber;
    private static final boolean ACCESS_CONTROLLER;

    protected ClassFileVersion(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    @AccessControllerPlugin.Enhance
    private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
        PrivilegedAction<T> action;
        if (ACCESS_CONTROLLER) {
            return AccessController.doPrivileged(privilegedAction);
        }
        return action.run();
    }

    public static ClassFileVersion ofMinorMajor(int versionNumber) {
        ClassFileVersion classFileVersion = new ClassFileVersion(versionNumber);
        if (classFileVersion.getMajorVersion() > 0 && classFileVersion.getMajorVersion() <= 44) {
            throw new IllegalArgumentException("Class version " + versionNumber + " is not valid");
        }
        return classFileVersion;
    }

    public static ClassFileVersion ofJavaVersionString(String javaVersionString) {
        if (javaVersionString.equals("1.1")) {
            return JAVA_V1;
        }
        if (javaVersionString.equals("1.2")) {
            return JAVA_V2;
        }
        if (javaVersionString.equals("1.3")) {
            return JAVA_V3;
        }
        if (javaVersionString.equals("1.4")) {
            return JAVA_V4;
        }
        if (javaVersionString.equals("1.5") || javaVersionString.equals("5")) {
            return JAVA_V5;
        }
        if (javaVersionString.equals("1.6") || javaVersionString.equals("6")) {
            return JAVA_V6;
        }
        if (javaVersionString.equals("1.7") || javaVersionString.equals("7")) {
            return JAVA_V7;
        }
        if (javaVersionString.equals("1.8") || javaVersionString.equals("8")) {
            return JAVA_V8;
        }
        if (javaVersionString.equals("1.9") || javaVersionString.equals("9")) {
            return JAVA_V9;
        }
        if (javaVersionString.equals("1.10") || javaVersionString.equals("10")) {
            return JAVA_V10;
        }
        if (javaVersionString.equals("1.11") || javaVersionString.equals("11")) {
            return JAVA_V11;
        }
        if (javaVersionString.equals("1.12") || javaVersionString.equals("12")) {
            return JAVA_V12;
        }
        if (javaVersionString.equals("1.13") || javaVersionString.equals("13")) {
            return JAVA_V13;
        }
        if (javaVersionString.equals("1.14") || javaVersionString.equals("14")) {
            return JAVA_V14;
        }
        if (javaVersionString.equals("1.15") || javaVersionString.equals("15")) {
            return JAVA_V15;
        }
        if (javaVersionString.equals("1.16") || javaVersionString.equals("16")) {
            return JAVA_V16;
        }
        if (javaVersionString.equals("1.17") || javaVersionString.equals("17")) {
            return JAVA_V17;
        }
        if (javaVersionString.equals("1.18") || javaVersionString.equals("18")) {
            return JAVA_V18;
        }
        if (javaVersionString.equals("1.19") || javaVersionString.equals("19")) {
            return JAVA_V19;
        }
        if (javaVersionString.equals("1.20") || javaVersionString.equals("20")) {
            return JAVA_V20;
        }
        if (javaVersionString.equals("1.21") || javaVersionString.equals("21")) {
            return JAVA_V21;
        }
        if (javaVersionString.equals("1.22") || javaVersionString.equals("22")) {
            return JAVA_V22;
        }
        if (OpenedClassReader.EXPERIMENTAL) {
            try {
                int version = Integer.parseInt(javaVersionString.startsWith("1.") ? javaVersionString.substring(2) : javaVersionString);
                if (version > 0) {
                    return new ClassFileVersion(44 + version);
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        throw new IllegalArgumentException("Unknown Java version string: " + javaVersionString);
    }

    public static ClassFileVersion ofJavaVersion(int javaVersion) {
        switch (javaVersion) {
            case 1: {
                return JAVA_V1;
            }
            case 2: {
                return JAVA_V2;
            }
            case 3: {
                return JAVA_V3;
            }
            case 4: {
                return JAVA_V4;
            }
            case 5: {
                return JAVA_V5;
            }
            case 6: {
                return JAVA_V6;
            }
            case 7: {
                return JAVA_V7;
            }
            case 8: {
                return JAVA_V8;
            }
            case 9: {
                return JAVA_V9;
            }
            case 10: {
                return JAVA_V10;
            }
            case 11: {
                return JAVA_V11;
            }
            case 12: {
                return JAVA_V12;
            }
            case 13: {
                return JAVA_V13;
            }
            case 14: {
                return JAVA_V14;
            }
            case 15: {
                return JAVA_V15;
            }
            case 16: {
                return JAVA_V16;
            }
            case 17: {
                return JAVA_V17;
            }
            case 18: {
                return JAVA_V18;
            }
            case 19: {
                return JAVA_V19;
            }
            case 20: {
                return JAVA_V20;
            }
            case 21: {
                return JAVA_V21;
            }
            case 22: {
                return JAVA_V22;
            }
        }
        if (OpenedClassReader.EXPERIMENTAL && javaVersion > 0) {
            return new ClassFileVersion(44 + javaVersion);
        }
        throw new IllegalArgumentException("Unknown Java version: " + javaVersion);
    }

    public static ClassFileVersion latest() {
        return JAVA_V22;
    }

    public static ClassFileVersion ofThisVm() {
        return VERSION_LOCATOR.resolve();
    }

    @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
    public static ClassFileVersion ofThisVm(ClassFileVersion fallback) {
        try {
            return ClassFileVersion.ofThisVm();
        }
        catch (Exception ignored) {
            return fallback;
        }
    }

    public static ClassFileVersion of(Class<?> type) throws IOException {
        return ClassFileVersion.of(type, ClassFileLocator.ForClassLoader.of(type.getClassLoader()));
    }

    public static ClassFileVersion of(Class<?> type, ClassFileLocator classFileLocator) throws IOException {
        return ClassFileVersion.of(TypeDescription.ForLoadedType.of(type), classFileLocator);
    }

    public static ClassFileVersion of(TypeDescription typeDescription, ClassFileLocator classFileLocator) throws IOException {
        return ClassFileVersion.ofClassFile(classFileLocator.locate(typeDescription.getName()).resolve());
    }

    public static ClassFileVersion ofClassFile(byte[] binaryRepresentation) {
        if (binaryRepresentation.length < 7) {
            throw new IllegalArgumentException("Supplied byte array is too short to be a class file with " + binaryRepresentation.length + " byte");
        }
        return ClassFileVersion.ofMinorMajor(binaryRepresentation[4] << 24 | binaryRepresentation[5] << 16 | binaryRepresentation[6] << 8 | binaryRepresentation[7]);
    }

    public int getMinorMajorVersion() {
        return this.versionNumber;
    }

    public short getMajorVersion() {
        return (short)(this.versionNumber & 0xFFFF);
    }

    public short getMinorVersion() {
        return (short)(this.versionNumber >>> 16);
    }

    public int getJavaVersion() {
        return this.getMajorVersion() - 44;
    }

    public boolean isAtLeast(ClassFileVersion classFileVersion) {
        return this.compareTo(classFileVersion) > -1;
    }

    public boolean isGreaterThan(ClassFileVersion classFileVersion) {
        return this.compareTo(classFileVersion) > 0;
    }

    public boolean isAtMost(ClassFileVersion classFileVersion) {
        return this.compareTo(classFileVersion) < 1;
    }

    public boolean isLessThan(ClassFileVersion classFileVersion) {
        return this.compareTo(classFileVersion) < 0;
    }

    public ClassFileVersion asPreviewVersion() {
        return new ClassFileVersion(this.versionNumber | 0xFFFF0000);
    }

    public boolean isPreviewVersion() {
        return (this.versionNumber & 0xFFFF0000) == -65536;
    }

    @Override
    public int compareTo(ClassFileVersion other) {
        return Integer.signum(this.getMajorVersion() == other.getMajorVersion() ? this.getMinorVersion() - other.getMinorVersion() : this.getMajorVersion() - other.getMajorVersion());
    }

    public int hashCode() {
        return this.versionNumber;
    }

    public boolean equals(@MaybeNull Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        return this.versionNumber == ((ClassFileVersion)other).versionNumber;
    }

    public String toString() {
        return "Java " + this.getJavaVersion() + " (" + this.getMinorMajorVersion() + ")";
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    static {
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
        JAVA_V1 = new ClassFileVersion(196653);
        JAVA_V2 = new ClassFileVersion(46);
        JAVA_V3 = new ClassFileVersion(47);
        JAVA_V4 = new ClassFileVersion(48);
        JAVA_V5 = new ClassFileVersion(49);
        JAVA_V6 = new ClassFileVersion(50);
        JAVA_V7 = new ClassFileVersion(51);
        JAVA_V8 = new ClassFileVersion(52);
        JAVA_V9 = new ClassFileVersion(53);
        JAVA_V10 = new ClassFileVersion(54);
        JAVA_V11 = new ClassFileVersion(55);
        JAVA_V12 = new ClassFileVersion(56);
        JAVA_V13 = new ClassFileVersion(57);
        JAVA_V14 = new ClassFileVersion(58);
        JAVA_V15 = new ClassFileVersion(59);
        JAVA_V16 = new ClassFileVersion(60);
        JAVA_V17 = new ClassFileVersion(61);
        JAVA_V18 = new ClassFileVersion(62);
        JAVA_V19 = new ClassFileVersion(63);
        JAVA_V20 = new ClassFileVersion(64);
        JAVA_V21 = new ClassFileVersion(65);
        JAVA_V22 = new ClassFileVersion(66);
        VERSION_LOCATOR = ClassFileVersion.doPrivileged(VersionLocator.Resolver.INSTANCE);
    }

    protected static interface VersionLocator {
        public static final String EARLY_ACCESS = "-ea";
        public static final String JAVA_VERSION = "java.version";

        public ClassFileVersion resolve();

        @HashCodeAndEqualsPlugin.Enhance
        public static class Unresolved
        implements VersionLocator {
            private final String message;

            protected Unresolved(String message) {
                this.message = message;
            }

            public ClassFileVersion resolve() {
                throw new IllegalStateException("Failed to resolve the class file version of the current VM: " + this.message);
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
                return this.message.equals(((Unresolved)object).message);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.message.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class Resolved
        implements VersionLocator {
            private final ClassFileVersion classFileVersion;

            protected Resolved(ClassFileVersion classFileVersion) {
                this.classFileVersion = classFileVersion;
            }

            public ClassFileVersion resolve() {
                return this.classFileVersion;
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
                return this.classFileVersion.equals(((Resolved)object).classFileVersion);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.classFileVersion.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Resolver implements PrivilegedAction<VersionLocator>
        {
            INSTANCE;


            @Override
            @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
            public VersionLocator run() {
                try {
                    Method method;
                    Class<?> type = Class.forName(Runtime.class.getName() + "$Version");
                    try {
                        method = type.getMethod("feature", new Class[0]);
                    }
                    catch (NoSuchMethodException ignored) {
                        method = type.getMethod("major", new Class[0]);
                    }
                    return new Resolved(ClassFileVersion.ofJavaVersion((Integer)method.invoke(Runtime.class.getMethod("version", new Class[0]).invoke(null, new Object[0]), new Object[0])));
                }
                catch (Throwable ignored) {
                    try {
                        String versionString = System.getProperty(VersionLocator.JAVA_VERSION);
                        if (versionString == null) {
                            throw new IllegalStateException("Java version property is not set");
                        }
                        if (versionString.equals("0")) {
                            return new Resolved(JAVA_V6);
                        }
                        if (versionString.endsWith(VersionLocator.EARLY_ACCESS)) {
                            versionString = versionString.substring(0, versionString.length() - VersionLocator.EARLY_ACCESS.length());
                        }
                        int[] versionIndex = new int[]{-1, 0, 0};
                        for (int index = 1; index < 3; ++index) {
                            versionIndex[index] = versionString.indexOf(46, versionIndex[index - 1] + 1);
                            if (versionIndex[index] != -1) continue;
                            throw new IllegalStateException("This JVM's version string does not seem to be valid: " + versionString);
                        }
                        return new Resolved(ClassFileVersion.ofJavaVersion(Integer.parseInt(versionString.substring(versionIndex[1] + 1, versionIndex[2]))));
                    }
                    catch (Throwable throwable) {
                        return new Unresolved(throwable.getMessage());
                    }
                }
            }
        }
    }
}

