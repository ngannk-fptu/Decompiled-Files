/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.platform.win32.Advapi32Util
 *  com.sun.jna.platform.win32.Win32Exception
 *  com.sun.jna.platform.win32.WinReg
 *  com.sun.jna.platform.win32.WinReg$HKEY
 */
package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;
import oshi.hardware.common.AbstractSoundCard;

@Immutable
final class WindowsSoundCard
extends AbstractSoundCard {
    private static final String REGISTRY_SOUNDCARDS = "SYSTEM\\CurrentControlSet\\Control\\Class\\{4d36e96c-e325-11ce-bfc1-08002be10318}\\";

    WindowsSoundCard(String kernelVersion, String name, String codec) {
        super(kernelVersion, name, codec);
    }

    public static List<SoundCard> getSoundCards() {
        String[] keys;
        ArrayList<SoundCard> soundCards = new ArrayList<SoundCard>();
        for (String key : keys = Advapi32Util.registryGetKeys((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)REGISTRY_SOUNDCARDS)) {
            String fullKey = REGISTRY_SOUNDCARDS + key;
            try {
                if (!Advapi32Util.registryValueExists((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)fullKey, (String)"Driver")) continue;
                soundCards.add(new WindowsSoundCard(Advapi32Util.registryGetStringValue((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)fullKey, (String)"Driver") + " " + Advapi32Util.registryGetStringValue((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)fullKey, (String)"DriverVersion"), Advapi32Util.registryGetStringValue((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)fullKey, (String)"ProviderName") + " " + Advapi32Util.registryGetStringValue((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)fullKey, (String)"DriverDesc"), Advapi32Util.registryGetStringValue((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)fullKey, (String)"DriverDesc")));
            }
            catch (Win32Exception e) {
                if (e.getErrorCode() == 5) continue;
                throw e;
            }
        }
        return soundCards;
    }
}

