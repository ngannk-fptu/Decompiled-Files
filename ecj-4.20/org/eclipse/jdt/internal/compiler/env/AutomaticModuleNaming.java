/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class AutomaticModuleNaming {
    private static final String AUTOMATIC_MODULE_NAME = "Automatic-Module-Name";

    public static char[] determineAutomaticModuleName(String jarFileName) {
        try {
            Throwable throwable = null;
            Object var2_3 = null;
            try (JarFile jar = new JarFile(jarFileName);){
                String automaticModuleName;
                Manifest manifest = jar.getManifest();
                if (manifest != null && (automaticModuleName = manifest.getMainAttributes().getValue(AUTOMATIC_MODULE_NAME)) != null) {
                    return automaticModuleName.toCharArray();
                }
            }
            catch (Throwable throwable2) {
                if (throwable == null) {
                    throwable = throwable2;
                } else if (throwable != throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        catch (IOException iOException) {}
        return AutomaticModuleNaming.determineAutomaticModuleNameFromFileName(jarFileName, true, true);
    }

    public static char[] determineAutomaticModuleName(String fileName, boolean isFile, Manifest manifest) {
        String automaticModuleName;
        if (manifest != null && (automaticModuleName = manifest.getMainAttributes().getValue(AUTOMATIC_MODULE_NAME)) != null) {
            return automaticModuleName.toCharArray();
        }
        return AutomaticModuleNaming.determineAutomaticModuleNameFromFileName(fileName, true, isFile);
    }

    public static char[] determineAutomaticModuleNameFromManifest(Manifest manifest) {
        String automaticModuleName;
        if (manifest != null && (automaticModuleName = manifest.getMainAttributes().getValue(AUTOMATIC_MODULE_NAME)) != null) {
            return automaticModuleName.toCharArray();
        }
        return null;
    }

    public static char[] determineAutomaticModuleNameFromFileName(String name, boolean skipDirectory, boolean removeExtension) {
        int index;
        int start = 0;
        int end = name.length();
        if (skipDirectory) {
            index = name.lastIndexOf(File.separatorChar);
            start = index + 1;
        }
        if (removeExtension && (name.endsWith(".jar") || name.endsWith(".JAR"))) {
            end -= 4;
        }
        index = start;
        while (index < end - 1) {
            block11: {
                if (name.charAt(index) == '-' && name.charAt(index + 1) >= '0' && name.charAt(index + 1) <= '9') {
                    int index2 = index + 2;
                    while (index2 < end) {
                        char c = name.charAt(index2);
                        if (c == '.') break;
                        if (c >= '0' && c <= '9') {
                            ++index2;
                            continue;
                        }
                        break block11;
                    }
                    end = index;
                    break;
                }
            }
            ++index;
        }
        StringBuilder sb = new StringBuilder(end - start);
        boolean needDot = false;
        int i = start;
        while (i < end) {
            char c = name.charAt(i);
            if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9') {
                if (needDot) {
                    sb.append('.');
                    needDot = false;
                }
                sb.append(c);
            } else if (sb.length() > 0) {
                needDot = true;
            }
            ++i;
        }
        return sb.toString().toCharArray();
    }
}

