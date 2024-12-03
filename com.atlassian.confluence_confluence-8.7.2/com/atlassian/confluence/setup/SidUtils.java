/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.core.util.StringUtils
 */
package com.atlassian.confluence.setup;

import com.atlassian.annotations.Internal;
import com.atlassian.core.util.StringUtils;
import java.security.SecureRandom;
import java.util.zip.CRC32;

@Internal
class SidUtils {
    private static final String CHARACTER_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String[] BAD_WORDS = new String[]{"FUCK", "SHIT", "COCK", "DICK", "CUNT", "TWAT", "BITCH", "BASTARD", "JIZ", "JISM", "FART", "CRAP", "ASS", "PORN", "PISS", "PUSSY", "BALLS", "TITS", "BOOBS", "COOCH", "CUM", "CHOAD", "DILDO", "DOUCHE", "CLIT", "MUFF", "NOB", "PECKER", "PRICK", "POONTANG", "QUEEF", "SNATCH", "TWOT", "DYKE", "COON", "NIG", "FAG", "WANKER", "GOOK", "FUDGEPACKER", "QUEER", "RAGHEAD", "SKANK", "SPIC", "GOD", "DAMN", "FICK", "SCHEISSE", "SCHWANZ", "FOTZE", "HURE", "SCHWUCHTEL", "SCHWUL", "TITTEN", "ARSCH", "IDIOT", "SAU", "ASSHAT", "TURDBURGLAR", "DIRTYSANCHEZ", "FELCH", "BLASEN", "WICKSER", "FEUCHT", "MOESE", "MILCHTUETEN", "FISTING", "HOOKERS"};
    private static final int KEY_LENGTH = 18;
    private static final char CURRENT_VERSION_INITAL_CHAR = 'B';
    private static final String PREVIOUS_VERSIONS_INITIAL_CHARS = "A";
    private static final char SEPARATOR_CHAR = '-';
    private final SecureRandom random;

    SidUtils() {
        Object seedStr = String.valueOf(System.currentTimeMillis());
        seedStr = (String)seedStr + ":" + System.identityHashCode(seedStr);
        seedStr = (String)seedStr + ":" + System.getProperties().toString();
        byte[] seed = ((String)seedStr).getBytes();
        this.random = new SecureRandom(seed);
    }

    String generateSID() {
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
            res.append(this.getCharacterForCRC(res.toString().getBytes()));
        } while (!this.isKeyClean(res.toString()));
        return res.toString();
    }

    boolean isValidSID(String sid) {
        boolean valid = this.validateStringSyntax(sid);
        if (valid) {
            String keyStr = sid.substring(0, 18);
            char crcChar = this.getCharacterForCRC(keyStr.getBytes());
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
        for (int i = 0; i < BAD_WORDS.length; ++i) {
            String badWord = BAD_WORDS[i];
            if (charKey.indexOf(badWord) == -1) continue;
            return false;
        }
        return true;
    }

    private String stripDashesAndNumbers(String key) {
        String res = StringUtils.replaceAll((String)key, (String)"-", (String)"");
        res = StringUtils.replaceAll((String)res, (String)"1", (String)"I");
        res = StringUtils.replaceAll((String)res, (String)"2", (String)"Z");
        res = StringUtils.replaceAll((String)res, (String)"3", (String)"E");
        res = StringUtils.replaceAll((String)res, (String)"4", (String)PREVIOUS_VERSIONS_INITIAL_CHARS);
        res = StringUtils.replaceAll((String)res, (String)"5", (String)"S");
        res = StringUtils.replaceAll((String)res, (String)"6", (String)"G");
        res = StringUtils.replaceAll((String)res, (String)"7", (String)"T");
        res = StringUtils.replaceAll((String)res, (String)"8", (String)"B");
        res = StringUtils.replaceAll((String)res, (String)"9", (String)"P");
        res = StringUtils.replaceAll((String)res, (String)"0", (String)"O");
        return res;
    }
}

