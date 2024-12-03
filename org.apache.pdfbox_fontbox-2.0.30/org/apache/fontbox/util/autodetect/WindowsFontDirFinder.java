/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.util.autodetect;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.fontbox.util.Charsets;
import org.apache.fontbox.util.autodetect.FontDirFinder;

public class WindowsFontDirFinder
implements FontDirFinder {
    private String getWinDir(String osName) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = osName.startsWith("Windows 9") ? runtime.exec("command.com /c echo %windir%") : runtime.exec("cmd.exe /c echo %windir%");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charsets.ISO_8859_1));
        String winDir = bufferedReader.readLine();
        bufferedReader.close();
        return winDir;
    }

    @Override
    public List<File> find() {
        File osFontsDir;
        ArrayList<File> fontDirList = new ArrayList<File>();
        String windir = null;
        try {
            windir = System.getProperty("env.windir");
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        String osName = System.getProperty("os.name");
        if (windir == null) {
            try {
                windir = this.getWinDir(osName);
            }
            catch (IOException iOException) {
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
        }
        if (windir != null && windir.length() > 2) {
            File psFontsDir;
            if (windir.endsWith("/")) {
                windir = windir.substring(0, windir.length() - 1);
            }
            if ((osFontsDir = new File(windir + File.separator + "FONTS")).exists() && osFontsDir.canRead()) {
                fontDirList.add(osFontsDir);
            }
            if ((psFontsDir = new File(windir.substring(0, 2) + File.separator + "PSFONTS")).exists() && psFontsDir.canRead()) {
                fontDirList.add(psFontsDir);
            }
        } else {
            char driveLetter;
            String windowsDirName = osName.endsWith("NT") ? "WINNT" : "WINDOWS";
            for (driveLetter = 'C'; driveLetter <= 'E'; driveLetter = (char)(driveLetter + '\u0001')) {
                osFontsDir = new File(driveLetter + ":" + File.separator + windowsDirName + File.separator + "FONTS");
                try {
                    if (!osFontsDir.exists() || !osFontsDir.canRead()) continue;
                    fontDirList.add(osFontsDir);
                    break;
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
            }
            for (driveLetter = 'C'; driveLetter <= 'E'; driveLetter = (char)(driveLetter + '\u0001')) {
                File psFontsDir = new File(driveLetter + ":" + File.separator + "PSFONTS");
                try {
                    if (!psFontsDir.exists() || !psFontsDir.canRead()) continue;
                    fontDirList.add(psFontsDir);
                    break;
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
            }
        }
        try {
            File localFontDir;
            String localAppData = System.getenv("LOCALAPPDATA");
            if (localAppData != null && !localAppData.isEmpty() && (localFontDir = new File(localAppData + File.separator + "Microsoft" + File.separator + "Windows" + File.separator + "Fonts")).exists() && localFontDir.canRead()) {
                fontDirList.add(localFontDir);
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        return fontDirList;
    }
}

