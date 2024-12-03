/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.ttf;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class OpenTypeScript {
    private static final Log LOG = LogFactory.getLog(OpenTypeScript.class);
    public static final String INHERITED = "Inherited";
    public static final String UNKNOWN = "Unknown";
    public static final String TAG_DEFAULT = "DFLT";
    private static final Map<String, String[]> UNICODE_SCRIPT_TO_OPENTYPE_TAG_MAP;
    private static int[] unicodeRangeStarts;
    private static String[] unicodeRangeScripts;

    private OpenTypeScript() {
    }

    private static void parseScriptsFile(InputStream inputStream) throws IOException {
        String s;
        TreeMap<int[], String> unicodeRanges = new TreeMap<int[], String>(new Comparator<int[]>(){

            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] < o2[0] ? -1 : (o1[0] == o2[0] ? 0 : 1);
            }
        });
        LineNumberReader rd = new LineNumberReader(new InputStreamReader(inputStream));
        int[] lastRange = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
        String lastScript = null;
        while ((s = rd.readLine()) != null) {
            StringTokenizer st;
            int nFields;
            int comment = s.indexOf(35);
            if (comment != -1) {
                s = s.substring(0, comment);
            }
            if (s.length() < 2 || (nFields = (st = new StringTokenizer(s, ";")).countTokens()) < 2) continue;
            String characters = st.nextToken().trim();
            String script = st.nextToken().trim();
            int[] range = new int[2];
            int rangeDelim = characters.indexOf("..");
            if (rangeDelim == -1) {
                range[0] = range[1] = Integer.parseInt(characters, 16);
            } else {
                range[0] = Integer.parseInt(characters.substring(0, rangeDelim), 16);
                range[1] = Integer.parseInt(characters.substring(rangeDelim + 2), 16);
            }
            if (range[0] == lastRange[1] + 1 && script.equals(lastScript)) {
                lastRange[1] = range[1];
                continue;
            }
            unicodeRanges.put(range, script);
            lastRange = range;
            lastScript = script;
        }
        rd.close();
        unicodeRangeStarts = new int[unicodeRanges.size()];
        unicodeRangeScripts = new String[unicodeRanges.size()];
        int i = 0;
        for (Map.Entry e : unicodeRanges.entrySet()) {
            OpenTypeScript.unicodeRangeStarts[i] = ((int[])e.getKey())[0];
            OpenTypeScript.unicodeRangeScripts[i] = (String)e.getValue();
            ++i;
        }
    }

    private static String getUnicodeScript(int codePoint) {
        OpenTypeScript.ensureValidCodePoint(codePoint);
        int type = Character.getType(codePoint);
        if (type == 0) {
            return UNKNOWN;
        }
        int scriptIndex = Arrays.binarySearch(unicodeRangeStarts, codePoint);
        if (scriptIndex < 0) {
            scriptIndex = -scriptIndex - 2;
        }
        return unicodeRangeScripts[scriptIndex];
    }

    public static String[] getScriptTags(int codePoint) {
        OpenTypeScript.ensureValidCodePoint(codePoint);
        String unicode = OpenTypeScript.getUnicodeScript(codePoint);
        return UNICODE_SCRIPT_TO_OPENTYPE_TAG_MAP.get(unicode);
    }

    private static void ensureValidCodePoint(int codePoint) {
        if (codePoint < 0 || codePoint > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid codepoint: " + codePoint);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        Object[][] table = new Object[][]{{"Adlam", new String[]{"adlm"}}, {"Ahom", new String[]{"ahom"}}, {"Anatolian_Hieroglyphs", new String[]{"hluw"}}, {"Arabic", new String[]{"arab"}}, {"Armenian", new String[]{"armn"}}, {"Avestan", new String[]{"avst"}}, {"Balinese", new String[]{"bali"}}, {"Bamum", new String[]{"bamu"}}, {"Bassa_Vah", new String[]{"bass"}}, {"Batak", new String[]{"batk"}}, {"Bengali", new String[]{"bng2", "beng"}}, {"Bhaiksuki", new String[]{"bhks"}}, {"Bopomofo", new String[]{"bopo"}}, {"Brahmi", new String[]{"brah"}}, {"Braille", new String[]{"brai"}}, {"Buginese", new String[]{"bugi"}}, {"Buhid", new String[]{"buhd"}}, {"Canadian_Aboriginal", new String[]{"cans"}}, {"Carian", new String[]{"cari"}}, {"Caucasian_Albanian", new String[]{"aghb"}}, {"Chakma", new String[]{"cakm"}}, {"Cham", new String[]{"cham"}}, {"Cherokee", new String[]{"cher"}}, {"Common", new String[]{TAG_DEFAULT}}, {"Coptic", new String[]{"copt"}}, {"Cuneiform", new String[]{"xsux"}}, {"Cypriot", new String[]{"cprt"}}, {"Cyrillic", new String[]{"cyrl"}}, {"Deseret", new String[]{"dsrt"}}, {"Devanagari", new String[]{"dev2", "deva"}}, {"Duployan", new String[]{"dupl"}}, {"Egyptian_Hieroglyphs", new String[]{"egyp"}}, {"Elbasan", new String[]{"elba"}}, {"Ethiopic", new String[]{"ethi"}}, {"Georgian", new String[]{"geor"}}, {"Glagolitic", new String[]{"glag"}}, {"Gothic", new String[]{"goth"}}, {"Grantha", new String[]{"gran"}}, {"Greek", new String[]{"grek"}}, {"Gujarati", new String[]{"gjr2", "gujr"}}, {"Gurmukhi", new String[]{"gur2", "guru"}}, {"Han", new String[]{"hani"}}, {"Hangul", new String[]{"hang"}}, {"Hanunoo", new String[]{"hano"}}, {"Hatran", new String[]{"hatr"}}, {"Hebrew", new String[]{"hebr"}}, {"Hiragana", new String[]{"kana"}}, {"Imperial_Aramaic", new String[]{"armi"}}, {INHERITED, new String[]{INHERITED}}, {"Inscriptional_Pahlavi", new String[]{"phli"}}, {"Inscriptional_Parthian", new String[]{"prti"}}, {"Javanese", new String[]{"java"}}, {"Kaithi", new String[]{"kthi"}}, {"Kannada", new String[]{"knd2", "knda"}}, {"Katakana", new String[]{"kana"}}, {"Kayah_Li", new String[]{"kali"}}, {"Kharoshthi", new String[]{"khar"}}, {"Khmer", new String[]{"khmr"}}, {"Khojki", new String[]{"khoj"}}, {"Khudawadi", new String[]{"sind"}}, {"Lao", new String[]{"lao "}}, {"Latin", new String[]{"latn"}}, {"Lepcha", new String[]{"lepc"}}, {"Limbu", new String[]{"limb"}}, {"Linear_A", new String[]{"lina"}}, {"Linear_B", new String[]{"linb"}}, {"Lisu", new String[]{"lisu"}}, {"Lycian", new String[]{"lyci"}}, {"Lydian", new String[]{"lydi"}}, {"Mahajani", new String[]{"mahj"}}, {"Malayalam", new String[]{"mlm2", "mlym"}}, {"Mandaic", new String[]{"mand"}}, {"Manichaean", new String[]{"mani"}}, {"Marchen", new String[]{"marc"}}, {"Meetei_Mayek", new String[]{"mtei"}}, {"Mende_Kikakui", new String[]{"mend"}}, {"Meroitic_Cursive", new String[]{"merc"}}, {"Meroitic_Hieroglyphs", new String[]{"mero"}}, {"Miao", new String[]{"plrd"}}, {"Modi", new String[]{"modi"}}, {"Mongolian", new String[]{"mong"}}, {"Mro", new String[]{"mroo"}}, {"Multani", new String[]{"mult"}}, {"Myanmar", new String[]{"mym2", "mymr"}}, {"Nabataean", new String[]{"nbat"}}, {"Newa", new String[]{"newa"}}, {"New_Tai_Lue", new String[]{"talu"}}, {"Nko", new String[]{"nko "}}, {"Ogham", new String[]{"ogam"}}, {"Ol_Chiki", new String[]{"olck"}}, {"Old_Italic", new String[]{"ital"}}, {"Old_Hungarian", new String[]{"hung"}}, {"Old_North_Arabian", new String[]{"narb"}}, {"Old_Permic", new String[]{"perm"}}, {"Old_Persian", new String[]{"xpeo"}}, {"Old_South_Arabian", new String[]{"sarb"}}, {"Old_Turkic", new String[]{"orkh"}}, {"Oriya", new String[]{"ory2", "orya"}}, {"Osage", new String[]{"osge"}}, {"Osmanya", new String[]{"osma"}}, {"Pahawh_Hmong", new String[]{"hmng"}}, {"Palmyrene", new String[]{"palm"}}, {"Pau_Cin_Hau", new String[]{"pauc"}}, {"Phags_Pa", new String[]{"phag"}}, {"Phoenician", new String[]{"phnx"}}, {"Psalter_Pahlavi", new String[]{"phlp"}}, {"Rejang", new String[]{"rjng"}}, {"Runic", new String[]{"runr"}}, {"Samaritan", new String[]{"samr"}}, {"Saurashtra", new String[]{"saur"}}, {"Sharada", new String[]{"shrd"}}, {"Shavian", new String[]{"shaw"}}, {"Siddham", new String[]{"sidd"}}, {"SignWriting", new String[]{"sgnw"}}, {"Sinhala", new String[]{"sinh"}}, {"Sora_Sompeng", new String[]{"sora"}}, {"Sundanese", new String[]{"sund"}}, {"Syloti_Nagri", new String[]{"sylo"}}, {"Syriac", new String[]{"syrc"}}, {"Tagalog", new String[]{"tglg"}}, {"Tagbanwa", new String[]{"tagb"}}, {"Tai_Le", new String[]{"tale"}}, {"Tai_Tham", new String[]{"lana"}}, {"Tai_Viet", new String[]{"tavt"}}, {"Takri", new String[]{"takr"}}, {"Tamil", new String[]{"tml2", "taml"}}, {"Tangut", new String[]{"tang"}}, {"Telugu", new String[]{"tel2", "telu"}}, {"Thaana", new String[]{"thaa"}}, {"Thai", new String[]{"thai"}}, {"Tibetan", new String[]{"tibt"}}, {"Tifinagh", new String[]{"tfng"}}, {"Tirhuta", new String[]{"tirh"}}, {"Ugaritic", new String[]{"ugar"}}, {UNKNOWN, new String[]{TAG_DEFAULT}}, {"Vai", new String[]{"vai "}}, {"Warang_Citi", new String[]{"wara"}}, {"Yi", new String[]{"yi  "}}};
        UNICODE_SCRIPT_TO_OPENTYPE_TAG_MAP = new HashMap<String, String[]>(table.length);
        for (Object[] array : table) {
            UNICODE_SCRIPT_TO_OPENTYPE_TAG_MAP.put((String)array[0], (String[])array[1]);
        }
        String path = "/org/apache/fontbox/unicode/Scripts.txt";
        BufferedInputStream input = null;
        try {
            input = new BufferedInputStream(OpenTypeScript.class.getResourceAsStream(path));
            OpenTypeScript.parseScriptsFile(input);
        }
        catch (IOException e) {
            LOG.warn((Object)("Could not parse Scripts.txt, mirroring char map will be empty: " + e.getMessage()));
        }
        finally {
            if (input != null) {
                try {
                    ((InputStream)input).close();
                }
                catch (IOException ex) {
                    LOG.warn((Object)"Could not close Scripts.txt");
                }
            }
        }
    }
}

