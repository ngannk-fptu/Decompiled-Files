/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

public class OSInfo {
    private static HashMap<String, String> archMapping = new HashMap();
    public static final String X86 = "x86";
    public static final String X86_64 = "x86_64";
    public static final String IA64_32 = "ia64_32";
    public static final String IA64 = "ia64";
    public static final String PPC = "ppc";
    public static final String PPC64 = "ppc64";
    public static final String IBMZ = "s390";
    public static final String IBMZ_64 = "s390x";
    public static final String AARCH_64 = "aarch64";
    public static final String RISCV_64 = "riscv64";

    public static void main(String[] stringArray) {
        if (stringArray.length >= 1) {
            if ("--os".equals(stringArray[0])) {
                System.out.print(OSInfo.getOSName());
                return;
            }
            if ("--arch".equals(stringArray[0])) {
                System.out.print(OSInfo.getArchName());
                return;
            }
        }
        System.out.print(OSInfo.getNativeLibFolderPathForCurrentOS());
    }

    public static String getNativeLibFolderPathForCurrentOS() {
        return OSInfo.getOSName() + "/" + OSInfo.getArchName();
    }

    public static String getOSName() {
        return OSInfo.translateOSNameToFolderName(System.getProperty("os.name"));
    }

    public static boolean isAndroid() {
        return System.getProperty("java.runtime.name", "").toLowerCase().contains("android");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String getHardwareName() {
        String string;
        block7: {
            Process process = Runtime.getRuntime().exec("uname -m");
            process.waitFor();
            InputStream inputStream = process.getInputStream();
            try {
                int n = 0;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] byArray = new byte[32];
                while ((n = inputStream.read(byArray, 0, byArray.length)) >= 0) {
                    byteArrayOutputStream.write(byArray, 0, n);
                }
                string = byteArrayOutputStream.toString();
                if (inputStream == null) break block7;
            }
            catch (Throwable throwable) {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    throw throwable;
                }
                catch (Throwable throwable2) {
                    System.err.println("Error while running uname -m: " + throwable2.getMessage());
                    return "unknown";
                }
            }
            inputStream.close();
        }
        return string;
    }

    static String resolveArmArchType() {
        if (System.getProperty("os.name").contains("Linux")) {
            String string = OSInfo.getHardwareName();
            if (string.startsWith("armv6")) {
                return "armv6";
            }
            if (string.startsWith("armv7")) {
                return "armv7";
            }
            String string2 = System.getProperty("sun.arch.abi");
            if (string2 != null && string2.startsWith("gnueabihf")) {
                return "armv7";
            }
            try {
                int n = Runtime.getRuntime().exec("which readelf").waitFor();
                if (n == 0) {
                    String string3 = System.getProperty("java.home");
                    String[] stringArray = new String[]{"/bin/sh", "-c", "find '" + string3 + "' -name 'libjvm.so' | head -1 | xargs readelf -A | grep 'Tag_ABI_VFP_args: VFP registers'"};
                    n = Runtime.getRuntime().exec(stringArray).waitFor();
                    if (n == 0) {
                        return "armv7";
                    }
                } else {
                    System.err.println("WARNING! readelf not found. Cannot check if running on an armhf system, armel architecture will be presumed.");
                }
            }
            catch (IOException iOException) {
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
        return "arm";
    }

    public static String getArchName() {
        String string = System.getProperty("os.arch");
        if (OSInfo.isAndroid()) {
            return "android-arm";
        }
        if (string.startsWith("arm")) {
            string = OSInfo.resolveArmArchType();
        } else {
            String string2 = string.toLowerCase(Locale.US);
            if (archMapping.containsKey(string2)) {
                return archMapping.get(string2);
            }
        }
        return OSInfo.translateArchNameToFolderName(string);
    }

    static String translateOSNameToFolderName(String string) {
        if (string.contains("Windows")) {
            return "Windows";
        }
        if (string.contains("Mac")) {
            return "Mac";
        }
        if (string.contains("Linux")) {
            return "Linux";
        }
        if (string.contains("AIX")) {
            return "AIX";
        }
        return string.replaceAll("\\W", "");
    }

    static String translateArchNameToFolderName(String string) {
        return string.replaceAll("\\W", "");
    }

    static {
        archMapping.put(X86, X86);
        archMapping.put("i386", X86);
        archMapping.put("i486", X86);
        archMapping.put("i586", X86);
        archMapping.put("i686", X86);
        archMapping.put("pentium", X86);
        archMapping.put(X86_64, X86_64);
        archMapping.put("amd64", X86_64);
        archMapping.put("em64t", X86_64);
        archMapping.put("universal", X86_64);
        archMapping.put(IA64, IA64);
        archMapping.put("ia64w", IA64);
        archMapping.put(IA64_32, IA64_32);
        archMapping.put("ia64n", IA64_32);
        archMapping.put(PPC, PPC);
        archMapping.put("power", PPC);
        archMapping.put("powerpc", PPC);
        archMapping.put("power_pc", PPC);
        archMapping.put("power_rs", PPC);
        archMapping.put(PPC64, PPC64);
        archMapping.put("power64", PPC64);
        archMapping.put("powerpc64", PPC64);
        archMapping.put("power_pc64", PPC64);
        archMapping.put("power_rs64", PPC64);
        archMapping.put(IBMZ, IBMZ);
        archMapping.put(IBMZ_64, IBMZ_64);
        archMapping.put(AARCH_64, AARCH_64);
        archMapping.put(RISCV_64, RISCV_64);
    }
}

