/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.license;

import com.atlassian.license.SIDManager;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.zip.CRC32;

@Deprecated
public class DefaultSIDManager
implements SIDManager {
    private static final String CHARACTER_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String[] BAD_WORDS = new String[]{"FUCK", "SHIT", "COCK", "DICK", "CUNT", "TWAT", "BITCH", "BASTARD", "JIZ", "JISM", "FART", "CRAP", "ASS", "PORN", "PISS", "PUSSY", "BALLS", "TITS", "BOOBS", "COOCH", "CUM", "CHOAD", "DILDO", "DOUCHE", "CLIT", "MUFF", "NOB", "PECKER", "PRICK", "POONTANG", "QUEEF", "SNATCH", "TWOT", "DYKE", "COON", "NIG", "FAG", "WANKER", "GOOK", "FUDGEPACKER", "QUEER", "RAGHEAD", "SKANK", "SPIC", "GOD", "DAMN", "FICK", "SCHEISSE", "SCHWANZ", "FOTZE", "HURE", "SCHWUCHTEL", "SCHWUL", "TITTEN", "ARSCH", "IDIOT", "SAU", "ASSHAT", "TURDBURGLAR", "DIRTYSANCHEZ", "FELCH", "BLASEN", "WICKSER", "FEUCHT", "MOESE", "MILCHTUETEN", "FISTING", "HOOKERS"};
    private static final int KEY_LENGTH = 18;
    private static final char CURRENT_VERSION_INITAL_CHAR = 'B';
    private static final String PREVIOUS_VERSIONS_INITIAL_CHARS = "A";
    private static final char SEPARATOR_CHAR = '-';
    private final SecureRandom random;

    public DefaultSIDManager() {
        this(null);
    }

    DefaultSIDManager(byte[] seed) {
        if (seed == null) {
            String seedStr = String.valueOf(System.currentTimeMillis());
            seedStr = seedStr + ":" + System.identityHashCode(seedStr);
            seedStr = seedStr + ":" + System.getProperties().toString();
            seed = seedStr.getBytes(StandardCharsets.UTF_8);
        }
        this.random = new SecureRandom(seed);
    }

    @Override
    public String generateSID() {
        StringBuffer res;
        do {
            res = new StringBuffer();
            res.append('B');
            int charCount = 1;
            for (int i = 1; i < 18; ++i) {
                if (charCount == 4) {
                    res.append('-');
                    charCount = 0;
                    continue;
                }
                int index = (int)(this.random.nextDouble() * (double)CHARACTER_POOL.length());
                res.append(CHARACTER_POOL.charAt(index));
                ++charCount;
            }
            res.append(this.getCharacterForCRC(res.toString().getBytes(StandardCharsets.UTF_8)));
        } while (!this.isKeyClean(res.toString()));
        return res.toString();
    }

    @Override
    public boolean isValidSID(String sid) {
        boolean valid = this.validateStringSyntax(sid);
        if (valid) {
            String keyStr = sid.substring(0, 18);
            char crcChar = this.getCharacterForCRC(keyStr.getBytes(StandardCharsets.UTF_8));
            char checkChar = sid.charAt(18);
            valid = checkChar == crcChar;
        }
        return valid;
    }

    private boolean validateStringSyntax(String sid) {
        boolean valid = true;
        if (sid == null) {
            valid = false;
        } else if (sid.length() != 19) {
            valid = false;
        } else if (sid.charAt(0) != 'B' && PREVIOUS_VERSIONS_INITIAL_CHARS.indexOf(sid.charAt(0)) == -1) {
            valid = false;
        } else if (sid.charAt(4) != '-' || sid.charAt(9) != '-' || sid.charAt(14) != '-') {
            valid = false;
        }
        return valid;
    }

    private char getCharacterForCRC(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        long crcValue = crc32.getValue();
        int index = (int)(crcValue % (long)CHARACTER_POOL.length());
        return CHARACTER_POOL.charAt(index);
    }

    private boolean isKeyClean(String key) {
        String charKey = this.stripDashesAndNumbers(key);
        for (String badWord : BAD_WORDS) {
            if (!charKey.contains(badWord)) continue;
            return false;
        }
        return true;
    }

    private String stripDashesAndNumbers(String key) {
        return key.replace("-", "").replace("1", "I").replace("2", "Z").replace("3", "E").replace("4", PREVIOUS_VERSIONS_INITIAL_CHARS).replace("5", "S").replace("6", "G").replace("7", "T").replace("8", "B").replace("9", "P").replace("0", "O");
    }
}

