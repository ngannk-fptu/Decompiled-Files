/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.en;

import org.apache.lucene.analysis.en.KStemData1;
import org.apache.lucene.analysis.en.KStemData2;
import org.apache.lucene.analysis.en.KStemData3;
import org.apache.lucene.analysis.en.KStemData4;
import org.apache.lucene.analysis.en.KStemData5;
import org.apache.lucene.analysis.en.KStemData6;
import org.apache.lucene.analysis.en.KStemData7;
import org.apache.lucene.analysis.en.KStemData8;
import org.apache.lucene.analysis.util.CharArrayMap;
import org.apache.lucene.analysis.util.OpenStringBuilder;
import org.apache.lucene.util.Version;

public class KStemmer {
    private static final int MaxWordLen = 50;
    private static final String[] exceptionWords = new String[]{"aide", "bathe", "caste", "cute", "dame", "dime", "doge", "done", "dune", "envelope", "gage", "grille", "grippe", "lobe", "mane", "mare", "nape", "node", "pane", "pate", "plane", "pope", "programme", "quite", "ripe", "rote", "rune", "sage", "severe", "shoppe", "sine", "slime", "snipe", "steppe", "suite", "swinge", "tare", "tine", "tope", "tripe", "twine"};
    private static final String[][] directConflations = new String[][]{{"aging", "age"}, {"going", "go"}, {"goes", "go"}, {"lying", "lie"}, {"using", "use"}, {"owing", "owe"}, {"suing", "sue"}, {"dying", "die"}, {"tying", "tie"}, {"vying", "vie"}, {"aged", "age"}, {"used", "use"}, {"vied", "vie"}, {"cued", "cue"}, {"died", "die"}, {"eyed", "eye"}, {"hued", "hue"}, {"iced", "ice"}, {"lied", "lie"}, {"owed", "owe"}, {"sued", "sue"}, {"toed", "toe"}, {"tied", "tie"}, {"does", "do"}, {"doing", "do"}, {"aeronautical", "aeronautics"}, {"mathematical", "mathematics"}, {"political", "politics"}, {"metaphysical", "metaphysics"}, {"cylindrical", "cylinder"}, {"nazism", "nazi"}, {"ambiguity", "ambiguous"}, {"barbarity", "barbarous"}, {"credulity", "credulous"}, {"generosity", "generous"}, {"spontaneity", "spontaneous"}, {"unanimity", "unanimous"}, {"voracity", "voracious"}, {"fled", "flee"}, {"miscarriage", "miscarry"}};
    private static final String[][] countryNationality = new String[][]{{"afghan", "afghanistan"}, {"african", "africa"}, {"albanian", "albania"}, {"algerian", "algeria"}, {"american", "america"}, {"andorran", "andorra"}, {"angolan", "angola"}, {"arabian", "arabia"}, {"argentine", "argentina"}, {"armenian", "armenia"}, {"asian", "asia"}, {"australian", "australia"}, {"austrian", "austria"}, {"azerbaijani", "azerbaijan"}, {"azeri", "azerbaijan"}, {"bangladeshi", "bangladesh"}, {"belgian", "belgium"}, {"bermudan", "bermuda"}, {"bolivian", "bolivia"}, {"bosnian", "bosnia"}, {"botswanan", "botswana"}, {"brazilian", "brazil"}, {"british", "britain"}, {"bulgarian", "bulgaria"}, {"burmese", "burma"}, {"californian", "california"}, {"cambodian", "cambodia"}, {"canadian", "canada"}, {"chadian", "chad"}, {"chilean", "chile"}, {"chinese", "china"}, {"colombian", "colombia"}, {"croat", "croatia"}, {"croatian", "croatia"}, {"cuban", "cuba"}, {"cypriot", "cyprus"}, {"czechoslovakian", "czechoslovakia"}, {"danish", "denmark"}, {"egyptian", "egypt"}, {"equadorian", "equador"}, {"eritrean", "eritrea"}, {"estonian", "estonia"}, {"ethiopian", "ethiopia"}, {"european", "europe"}, {"fijian", "fiji"}, {"filipino", "philippines"}, {"finnish", "finland"}, {"french", "france"}, {"gambian", "gambia"}, {"georgian", "georgia"}, {"german", "germany"}, {"ghanian", "ghana"}, {"greek", "greece"}, {"grenadan", "grenada"}, {"guamian", "guam"}, {"guatemalan", "guatemala"}, {"guinean", "guinea"}, {"guyanan", "guyana"}, {"haitian", "haiti"}, {"hawaiian", "hawaii"}, {"holland", "dutch"}, {"honduran", "honduras"}, {"hungarian", "hungary"}, {"icelandic", "iceland"}, {"indonesian", "indonesia"}, {"iranian", "iran"}, {"iraqi", "iraq"}, {"iraqui", "iraq"}, {"irish", "ireland"}, {"israeli", "israel"}, {"italian", "italy"}, {"jamaican", "jamaica"}, {"japanese", "japan"}, {"jordanian", "jordan"}, {"kampuchean", "cambodia"}, {"kenyan", "kenya"}, {"korean", "korea"}, {"kuwaiti", "kuwait"}, {"lankan", "lanka"}, {"laotian", "laos"}, {"latvian", "latvia"}, {"lebanese", "lebanon"}, {"liberian", "liberia"}, {"libyan", "libya"}, {"lithuanian", "lithuania"}, {"macedonian", "macedonia"}, {"madagascan", "madagascar"}, {"malaysian", "malaysia"}, {"maltese", "malta"}, {"mauritanian", "mauritania"}, {"mexican", "mexico"}, {"micronesian", "micronesia"}, {"moldovan", "moldova"}, {"monacan", "monaco"}, {"mongolian", "mongolia"}, {"montenegran", "montenegro"}, {"moroccan", "morocco"}, {"myanmar", "burma"}, {"namibian", "namibia"}, {"nepalese", "nepal"}, {"nicaraguan", "nicaragua"}, {"nigerian", "nigeria"}, {"norwegian", "norway"}, {"omani", "oman"}, {"pakistani", "pakistan"}, {"panamanian", "panama"}, {"papuan", "papua"}, {"paraguayan", "paraguay"}, {"peruvian", "peru"}, {"portuguese", "portugal"}, {"romanian", "romania"}, {"rumania", "romania"}, {"rumanian", "romania"}, {"russian", "russia"}, {"rwandan", "rwanda"}, {"samoan", "samoa"}, {"scottish", "scotland"}, {"serb", "serbia"}, {"serbian", "serbia"}, {"siam", "thailand"}, {"siamese", "thailand"}, {"slovakia", "slovak"}, {"slovakian", "slovak"}, {"slovenian", "slovenia"}, {"somali", "somalia"}, {"somalian", "somalia"}, {"spanish", "spain"}, {"swedish", "sweden"}, {"swiss", "switzerland"}, {"syrian", "syria"}, {"taiwanese", "taiwan"}, {"tanzanian", "tanzania"}, {"texan", "texas"}, {"thai", "thailand"}, {"tunisian", "tunisia"}, {"turkish", "turkey"}, {"ugandan", "uganda"}, {"ukrainian", "ukraine"}, {"uruguayan", "uruguay"}, {"uzbek", "uzbekistan"}, {"venezuelan", "venezuela"}, {"vietnamese", "viet"}, {"virginian", "virginia"}, {"yemeni", "yemen"}, {"yugoslav", "yugoslavia"}, {"yugoslavian", "yugoslavia"}, {"zambian", "zambia"}, {"zealander", "zealand"}, {"zimbabwean", "zimbabwe"}};
    private static final String[] supplementDict = new String[]{"aids", "applicator", "capacitor", "digitize", "electromagnet", "ellipsoid", "exosphere", "extensible", "ferromagnet", "graphics", "hydromagnet", "polygraph", "toroid", "superconduct", "backscatter", "connectionism"};
    private static final String[] properNouns = new String[]{"abrams", "achilles", "acropolis", "adams", "agnes", "aires", "alexander", "alexis", "alfred", "algiers", "alps", "amadeus", "ames", "amos", "andes", "angeles", "annapolis", "antilles", "aquarius", "archimedes", "arkansas", "asher", "ashly", "athens", "atkins", "atlantis", "avis", "bahamas", "bangor", "barbados", "barger", "bering", "brahms", "brandeis", "brussels", "bruxelles", "cairns", "camoros", "camus", "carlos", "celts", "chalker", "charles", "cheops", "ching", "christmas", "cocos", "collins", "columbus", "confucius", "conners", "connolly", "copernicus", "cramer", "cyclops", "cygnus", "cyprus", "dallas", "damascus", "daniels", "davies", "davis", "decker", "denning", "dennis", "descartes", "dickens", "doris", "douglas", "downs", "dreyfus", "dukakis", "dulles", "dumfries", "ecclesiastes", "edwards", "emily", "erasmus", "euphrates", "evans", "everglades", "fairbanks", "federales", "fisher", "fitzsimmons", "fleming", "forbes", "fowler", "france", "francis", "goering", "goodling", "goths", "grenadines", "guiness", "hades", "harding", "harris", "hastings", "hawkes", "hawking", "hayes", "heights", "hercules", "himalayas", "hippocrates", "hobbs", "holmes", "honduras", "hopkins", "hughes", "humphreys", "illinois", "indianapolis", "inverness", "iris", "iroquois", "irving", "isaacs", "italy", "james", "jarvis", "jeffreys", "jesus", "jones", "josephus", "judas", "julius", "kansas", "keynes", "kipling", "kiwanis", "lansing", "laos", "leeds", "levis", "leviticus", "lewis", "louis", "maccabees", "madras", "maimonides", "maldive", "massachusetts", "matthews", "mauritius", "memphis", "mercedes", "midas", "mingus", "minneapolis", "mohammed", "moines", "morris", "moses", "myers", "myknos", "nablus", "nanjing", "nantes", "naples", "neal", "netherlands", "nevis", "nostradamus", "oedipus", "olympus", "orleans", "orly", "papas", "paris", "parker", "pauling", "peking", "pershing", "peter", "peters", "philippines", "phineas", "pisces", "pryor", "pythagoras", "queens", "rabelais", "ramses", "reynolds", "rhesus", "rhodes", "richards", "robins", "rodgers", "rogers", "rubens", "sagittarius", "seychelles", "socrates", "texas", "thames", "thomas", "tiberias", "tunis", "venus", "vilnius", "wales", "warner", "wilkins", "williams", "wyoming", "xmas", "yonkers", "zeus", "frances", "aarhus", "adonis", "andrews", "angus", "antares", "aquinas", "arcturus", "ares", "artemis", "augustus", "ayers", "barnabas", "barnes", "becker", "bejing", "biggs", "billings", "boeing", "boris", "borroughs", "briggs", "buenos", "calais", "caracas", "cassius", "cerberus", "ceres", "cervantes", "chantilly", "chartres", "chester", "connally", "conner", "coors", "cummings", "curtis", "daedalus", "dionysus", "dobbs", "dolores", "edmonds"};
    private static final CharArrayMap<DictEntry> dict_ht = KStemmer.initializeDictHash();
    private final OpenStringBuilder word = new OpenStringBuilder();
    private int j;
    private int k;
    DictEntry matchedEntry = null;
    private static char[] ization = "ization".toCharArray();
    private static char[] ition = "ition".toCharArray();
    private static char[] ation = "ation".toCharArray();
    private static char[] ication = "ication".toCharArray();
    String result;

    private char finalChar() {
        return this.word.charAt(this.k);
    }

    private char penultChar() {
        return this.word.charAt(this.k - 1);
    }

    private boolean isVowel(int index) {
        return !this.isCons(index);
    }

    private boolean isCons(int index) {
        char ch = this.word.charAt(index);
        if (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u') {
            return false;
        }
        if (ch != 'y' || index == 0) {
            return true;
        }
        return !this.isCons(index - 1);
    }

    private static CharArrayMap<DictEntry> initializeDictHash() {
        int i;
        DictEntry entry;
        int i2;
        CharArrayMap<DictEntry> d = new CharArrayMap<DictEntry>(Version.LUCENE_31, 1000, false);
        d = new CharArrayMap(Version.LUCENE_31, 1000, false);
        for (i2 = 0; i2 < exceptionWords.length; ++i2) {
            if (d.containsKey(exceptionWords[i2])) {
                throw new RuntimeException("Warning: Entry [" + exceptionWords[i2] + "] already in dictionary 1");
            }
            entry = new DictEntry(exceptionWords[i2], true);
            d.put(exceptionWords[i2], entry);
        }
        for (i2 = 0; i2 < directConflations.length; ++i2) {
            if (d.containsKey(directConflations[i2][0])) {
                throw new RuntimeException("Warning: Entry [" + directConflations[i2][0] + "] already in dictionary 2");
            }
            entry = new DictEntry(directConflations[i2][1], false);
            d.put(directConflations[i2][0], entry);
        }
        for (i2 = 0; i2 < countryNationality.length; ++i2) {
            if (d.containsKey(countryNationality[i2][0])) {
                throw new RuntimeException("Warning: Entry [" + countryNationality[i2][0] + "] already in dictionary 3");
            }
            entry = new DictEntry(countryNationality[i2][1], false);
            d.put(countryNationality[i2][0], entry);
        }
        DictEntry defaultEntry = new DictEntry(null, false);
        String[] array = KStemData1.data;
        for (i = 0; i < array.length; ++i) {
            if (d.containsKey(array[i])) {
                throw new RuntimeException("Warning: Entry [" + array[i] + "] already in dictionary 4");
            }
            d.put(array[i], defaultEntry);
        }
        array = KStemData2.data;
        for (i = 0; i < array.length; ++i) {
            if (d.containsKey(array[i])) {
                throw new RuntimeException("Warning: Entry [" + array[i] + "] already in dictionary 4");
            }
            d.put(array[i], defaultEntry);
        }
        array = KStemData3.data;
        for (i = 0; i < array.length; ++i) {
            if (d.containsKey(array[i])) {
                throw new RuntimeException("Warning: Entry [" + array[i] + "] already in dictionary 4");
            }
            d.put(array[i], defaultEntry);
        }
        array = KStemData4.data;
        for (i = 0; i < array.length; ++i) {
            if (d.containsKey(array[i])) {
                throw new RuntimeException("Warning: Entry [" + array[i] + "] already in dictionary 4");
            }
            d.put(array[i], defaultEntry);
        }
        array = KStemData5.data;
        for (i = 0; i < array.length; ++i) {
            if (d.containsKey(array[i])) {
                throw new RuntimeException("Warning: Entry [" + array[i] + "] already in dictionary 4");
            }
            d.put(array[i], defaultEntry);
        }
        array = KStemData6.data;
        for (i = 0; i < array.length; ++i) {
            if (d.containsKey(array[i])) {
                throw new RuntimeException("Warning: Entry [" + array[i] + "] already in dictionary 4");
            }
            d.put(array[i], defaultEntry);
        }
        array = KStemData7.data;
        for (i = 0; i < array.length; ++i) {
            if (d.containsKey(array[i])) {
                throw new RuntimeException("Warning: Entry [" + array[i] + "] already in dictionary 4");
            }
            d.put(array[i], defaultEntry);
        }
        for (i = 0; i < KStemData8.data.length; ++i) {
            if (d.containsKey(KStemData8.data[i])) {
                throw new RuntimeException("Warning: Entry [" + KStemData8.data[i] + "] already in dictionary 4");
            }
            d.put(KStemData8.data[i], defaultEntry);
        }
        for (i = 0; i < supplementDict.length; ++i) {
            if (d.containsKey(supplementDict[i])) {
                throw new RuntimeException("Warning: Entry [" + supplementDict[i] + "] already in dictionary 5");
            }
            d.put(supplementDict[i], defaultEntry);
        }
        for (i = 0; i < properNouns.length; ++i) {
            if (d.containsKey(properNouns[i])) {
                throw new RuntimeException("Warning: Entry [" + properNouns[i] + "] already in dictionary 6");
            }
            d.put(properNouns[i], defaultEntry);
        }
        return d;
    }

    private boolean isAlpha(char ch) {
        return ch >= 'a' && ch <= 'z';
    }

    private int stemLength() {
        return this.j + 1;
    }

    private boolean endsIn(char[] s) {
        if (s.length > this.k) {
            return false;
        }
        int r = this.word.length() - s.length;
        this.j = this.k;
        int r1 = r;
        int i = 0;
        while (i < s.length) {
            if (s[i] != this.word.charAt(r1)) {
                return false;
            }
            ++i;
            ++r1;
        }
        this.j = r - 1;
        return true;
    }

    private boolean endsIn(char a, char b) {
        if (2 > this.k) {
            return false;
        }
        if (this.word.charAt(this.k - 1) == a && this.word.charAt(this.k) == b) {
            this.j = this.k - 2;
            return true;
        }
        return false;
    }

    private boolean endsIn(char a, char b, char c) {
        if (3 > this.k) {
            return false;
        }
        if (this.word.charAt(this.k - 2) == a && this.word.charAt(this.k - 1) == b && this.word.charAt(this.k) == c) {
            this.j = this.k - 3;
            return true;
        }
        return false;
    }

    private boolean endsIn(char a, char b, char c, char d) {
        if (4 > this.k) {
            return false;
        }
        if (this.word.charAt(this.k - 3) == a && this.word.charAt(this.k - 2) == b && this.word.charAt(this.k - 1) == c && this.word.charAt(this.k) == d) {
            this.j = this.k - 4;
            return true;
        }
        return false;
    }

    private DictEntry wordInDict() {
        if (this.matchedEntry != null) {
            return this.matchedEntry;
        }
        DictEntry e = dict_ht.get(this.word.getArray(), 0, this.word.length());
        if (e != null && !e.exception) {
            this.matchedEntry = e;
        }
        return e;
    }

    private void plural() {
        if (this.word.charAt(this.k) == 's') {
            if (this.endsIn('i', 'e', 's')) {
                this.word.setLength(this.j + 3);
                --this.k;
                if (this.lookup()) {
                    return;
                }
                ++this.k;
                this.word.unsafeWrite('s');
                this.setSuffix("y");
                this.lookup();
            } else {
                if (this.endsIn('e', 's')) {
                    boolean tryE;
                    this.word.setLength(this.j + 2);
                    --this.k;
                    boolean bl = tryE = this.j > 0 && (this.word.charAt(this.j) != 's' || this.word.charAt(this.j - 1) != 's');
                    if (tryE && this.lookup()) {
                        return;
                    }
                    this.word.setLength(this.j + 1);
                    --this.k;
                    if (this.lookup()) {
                        return;
                    }
                    this.word.unsafeWrite('e');
                    ++this.k;
                    if (!tryE) {
                        this.lookup();
                    }
                    return;
                }
                if (this.word.length() > 3 && this.penultChar() != 's' && !this.endsIn('o', 'u', 's')) {
                    this.word.setLength(this.k);
                    --this.k;
                    this.lookup();
                }
            }
        }
    }

    private void setSuffix(String s) {
        this.setSuff(s, s.length());
    }

    private void setSuff(String s, int len) {
        this.word.setLength(this.j + 1);
        for (int l = 0; l < len; ++l) {
            this.word.unsafeWrite(s.charAt(l));
        }
        this.k = this.j + len;
    }

    private boolean lookup() {
        this.matchedEntry = dict_ht.get(this.word.getArray(), 0, this.word.size());
        return this.matchedEntry != null;
    }

    private void pastTense() {
        if (this.word.length() <= 4) {
            return;
        }
        if (this.endsIn('i', 'e', 'd')) {
            this.word.setLength(this.j + 3);
            --this.k;
            if (this.lookup()) {
                return;
            }
            ++this.k;
            this.word.unsafeWrite('d');
            this.setSuffix("y");
            this.lookup();
            return;
        }
        if (this.endsIn('e', 'd') && this.vowelInStem()) {
            this.word.setLength(this.j + 2);
            this.k = this.j + 1;
            DictEntry entry = this.wordInDict();
            if (entry != null && !entry.exception) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            if (this.doubleC(this.k)) {
                this.word.setLength(this.k);
                --this.k;
                if (this.lookup()) {
                    return;
                }
                this.word.unsafeWrite(this.word.charAt(this.k));
                ++this.k;
                this.lookup();
                return;
            }
            if (this.word.charAt(0) == 'u' && this.word.charAt(1) == 'n') {
                this.word.unsafeWrite('e');
                this.word.unsafeWrite('d');
                this.k += 2;
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            return;
        }
    }

    private boolean doubleC(int i) {
        if (i < 1) {
            return false;
        }
        if (this.word.charAt(i) != this.word.charAt(i - 1)) {
            return false;
        }
        return this.isCons(i);
    }

    private boolean vowelInStem() {
        for (int i = 0; i < this.stemLength(); ++i) {
            if (!this.isVowel(i)) continue;
            return true;
        }
        return false;
    }

    private void aspect() {
        if (this.word.length() <= 5) {
            return;
        }
        if (this.endsIn('i', 'n', 'g') && this.vowelInStem()) {
            this.word.setCharAt(this.j + 1, 'e');
            this.word.setLength(this.j + 2);
            this.k = this.j + 1;
            DictEntry entry = this.wordInDict();
            if (entry != null && !entry.exception) {
                return;
            }
            this.word.setLength(this.k);
            --this.k;
            if (this.lookup()) {
                return;
            }
            if (this.doubleC(this.k)) {
                --this.k;
                this.word.setLength(this.k + 1);
                if (this.lookup()) {
                    return;
                }
                this.word.unsafeWrite(this.word.charAt(this.k));
                ++this.k;
                this.lookup();
                return;
            }
            if (this.j > 0 && this.isCons(this.j) && this.isCons(this.j - 1)) {
                this.k = this.j;
                this.word.setLength(this.k + 1);
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            return;
        }
    }

    private void ityEndings() {
        int old_k = this.k;
        if (this.endsIn('i', 't', 'y')) {
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setCharAt(this.j + 1, 'i');
            this.word.append("ty");
            this.k = old_k;
            if (this.j > 0 && this.word.charAt(this.j - 1) == 'i' && this.word.charAt(this.j) == 'l') {
                this.word.setLength(this.j - 1);
                this.word.append("le");
                this.k = this.j;
                this.lookup();
                return;
            }
            if (this.j > 0 && this.word.charAt(this.j - 1) == 'i' && this.word.charAt(this.j) == 'v') {
                this.word.setLength(this.j + 1);
                this.word.unsafeWrite('e');
                this.k = this.j + 1;
                this.lookup();
                return;
            }
            if (this.j > 0 && this.word.charAt(this.j - 1) == 'a' && this.word.charAt(this.j) == 'l') {
                this.word.setLength(this.j + 1);
                this.k = this.j;
                this.lookup();
                return;
            }
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.k = this.j;
            return;
        }
    }

    private void nceEndings() {
        int old_k = this.k;
        if (this.endsIn('n', 'c', 'e')) {
            char word_char = this.word.charAt(this.j);
            if (word_char != 'e' && word_char != 'a') {
                return;
            }
            this.word.setLength(this.j);
            this.word.unsafeWrite('e');
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j);
            this.k = this.j - 1;
            if (this.lookup()) {
                return;
            }
            this.word.unsafeWrite(word_char);
            this.word.append("nce");
            this.k = old_k;
        }
    }

    private void nessEndings() {
        if (this.endsIn('n', 'e', 's', 's')) {
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.word.charAt(this.j) == 'i') {
                this.word.setCharAt(this.j, 'y');
            }
            this.lookup();
        }
    }

    private void ismEndings() {
        if (this.endsIn('i', 's', 'm')) {
            this.word.setLength(this.j + 1);
            this.k = this.j;
            this.lookup();
        }
    }

    private void mentEndings() {
        int old_k = this.k;
        if (this.endsIn('m', 'e', 'n', 't')) {
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.append("ment");
            this.k = old_k;
        }
    }

    private void izeEndings() {
        int old_k = this.k;
        if (this.endsIn('i', 'z', 'e')) {
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.unsafeWrite('i');
            if (this.doubleC(this.j)) {
                this.word.setLength(this.j);
                this.k = this.j - 1;
                if (this.lookup()) {
                    return;
                }
                this.word.unsafeWrite(this.word.charAt(this.j - 1));
            }
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.append("ize");
            this.k = old_k;
        }
    }

    private void ncyEndings() {
        if (this.endsIn('n', 'c', 'y')) {
            if (this.word.charAt(this.j) != 'e' && this.word.charAt(this.j) != 'a') {
                return;
            }
            this.word.setCharAt(this.j + 2, 't');
            this.word.setLength(this.j + 3);
            this.k = this.j + 2;
            if (this.lookup()) {
                return;
            }
            this.word.setCharAt(this.j + 2, 'c');
            this.word.unsafeWrite('e');
            this.k = this.j + 3;
            this.lookup();
        }
    }

    private void bleEndings() {
        int old_k = this.k;
        if (this.endsIn('b', 'l', 'e')) {
            if (this.word.charAt(this.j) != 'a' && this.word.charAt(this.j) != 'i') {
                return;
            }
            char word_char = this.word.charAt(this.j);
            this.word.setLength(this.j);
            this.k = this.j - 1;
            if (this.lookup()) {
                return;
            }
            if (this.doubleC(this.k)) {
                this.word.setLength(this.k);
                --this.k;
                if (this.lookup()) {
                    return;
                }
                ++this.k;
                this.word.unsafeWrite(this.word.charAt(this.k - 1));
            }
            this.word.setLength(this.j);
            this.word.unsafeWrite('e');
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j);
            this.word.append("ate");
            this.k = this.j + 2;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j);
            this.word.unsafeWrite(word_char);
            this.word.append("ble");
            this.k = old_k;
        }
    }

    private void icEndings() {
        if (this.endsIn('i', 'c')) {
            this.word.setLength(this.j + 3);
            this.word.append("al");
            this.k = this.j + 4;
            if (this.lookup()) {
                return;
            }
            this.word.setCharAt(this.j + 1, 'y');
            this.word.setLength(this.j + 2);
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setCharAt(this.j + 1, 'e');
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.append("ic");
            this.k = this.j + 2;
        }
    }

    private void ionEndings() {
        int old_k = this.k;
        if (!this.endsIn('i', 'o', 'n')) {
            return;
        }
        if (this.endsIn(ization)) {
            this.word.setLength(this.j + 3);
            this.word.unsafeWrite('e');
            this.k = this.j + 3;
            this.lookup();
            return;
        }
        if (this.endsIn(ition)) {
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.append("ition");
            this.k = old_k;
        } else if (this.endsIn(ation)) {
            this.word.setLength(this.j + 3);
            this.word.unsafeWrite('e');
            this.k = this.j + 3;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.append("ation");
            this.k = old_k;
        }
        if (this.endsIn(ication)) {
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite('y');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.append("ication");
            this.k = old_k;
        }
        this.j = this.k - 3;
        this.word.setLength(this.j + 1);
        this.word.unsafeWrite('e');
        this.k = this.j + 1;
        if (this.lookup()) {
            return;
        }
        this.word.setLength(this.j + 1);
        this.k = this.j;
        if (this.lookup()) {
            return;
        }
        this.word.setLength(this.j + 1);
        this.word.append("ion");
        this.k = old_k;
    }

    private void erAndOrEndings() {
        int old_k = this.k;
        if (this.word.charAt(this.k) != 'r') {
            return;
        }
        if (this.endsIn('i', 'z', 'e', 'r')) {
            this.word.setLength(this.j + 4);
            this.k = this.j + 3;
            this.lookup();
            return;
        }
        if (this.endsIn('e', 'r') || this.endsIn('o', 'r')) {
            char word_char = this.word.charAt(this.j + 1);
            if (this.doubleC(this.j)) {
                this.word.setLength(this.j);
                this.k = this.j - 1;
                if (this.lookup()) {
                    return;
                }
                this.word.unsafeWrite(this.word.charAt(this.j - 1));
            }
            if (this.word.charAt(this.j) == 'i') {
                this.word.setCharAt(this.j, 'y');
                this.word.setLength(this.j + 1);
                this.k = this.j;
                if (this.lookup()) {
                    return;
                }
                this.word.setCharAt(this.j, 'i');
                this.word.unsafeWrite('e');
            }
            if (this.word.charAt(this.j) == 'e') {
                this.word.setLength(this.j);
                this.k = this.j - 1;
                if (this.lookup()) {
                    return;
                }
                this.word.unsafeWrite('e');
            }
            this.word.setLength(this.j + 2);
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite(word_char);
            this.word.unsafeWrite('r');
            this.k = old_k;
        }
    }

    private void lyEndings() {
        int old_k = this.k;
        if (this.endsIn('l', 'y')) {
            this.word.setCharAt(this.j + 2, 'e');
            if (this.lookup()) {
                return;
            }
            this.word.setCharAt(this.j + 2, 'y');
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            if (this.j > 0 && this.word.charAt(this.j - 1) == 'a' && this.word.charAt(this.j) == 'l') {
                return;
            }
            this.word.append("ly");
            this.k = old_k;
            if (this.j > 0 && this.word.charAt(this.j - 1) == 'a' && this.word.charAt(this.j) == 'b') {
                this.word.setCharAt(this.j + 2, 'e');
                this.k = this.j + 2;
                return;
            }
            if (this.word.charAt(this.j) == 'i') {
                this.word.setLength(this.j);
                this.word.unsafeWrite('y');
                this.k = this.j;
                if (this.lookup()) {
                    return;
                }
                this.word.setLength(this.j);
                this.word.append("ily");
                this.k = old_k;
            }
            this.word.setLength(this.j + 1);
            this.k = this.j;
        }
    }

    private void alEndings() {
        int old_k = this.k;
        if (this.word.length() < 4) {
            return;
        }
        if (this.endsIn('a', 'l')) {
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            if (this.doubleC(this.j)) {
                this.word.setLength(this.j);
                this.k = this.j - 1;
                if (this.lookup()) {
                    return;
                }
                this.word.unsafeWrite(this.word.charAt(this.j - 1));
            }
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.append("um");
            this.k = this.j + 2;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.append("al");
            this.k = old_k;
            if (this.j > 0 && this.word.charAt(this.j - 1) == 'i' && this.word.charAt(this.j) == 'c') {
                this.word.setLength(this.j - 1);
                this.k = this.j - 2;
                if (this.lookup()) {
                    return;
                }
                this.word.setLength(this.j - 1);
                this.word.unsafeWrite('y');
                this.k = this.j - 1;
                if (this.lookup()) {
                    return;
                }
                this.word.setLength(this.j - 1);
                this.word.append("ic");
                this.k = this.j;
                this.lookup();
                return;
            }
            if (this.word.charAt(this.j) == 'i') {
                this.word.setLength(this.j);
                this.k = this.j - 1;
                if (this.lookup()) {
                    return;
                }
                this.word.append("ial");
                this.k = old_k;
                this.lookup();
            }
        }
    }

    private void iveEndings() {
        int old_k = this.k;
        if (this.endsIn('i', 'v', 'e')) {
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.append("ive");
            if (this.j > 0 && this.word.charAt(this.j - 1) == 'a' && this.word.charAt(this.j) == 't') {
                this.word.setCharAt(this.j - 1, 'e');
                this.word.setLength(this.j);
                this.k = this.j - 1;
                if (this.lookup()) {
                    return;
                }
                this.word.setLength(this.j - 1);
                if (this.lookup()) {
                    return;
                }
                this.word.append("ative");
                this.k = old_k;
            }
            this.word.setCharAt(this.j + 2, 'o');
            this.word.setCharAt(this.j + 3, 'n');
            if (this.lookup()) {
                return;
            }
            this.word.setCharAt(this.j + 2, 'v');
            this.word.setCharAt(this.j + 3, 'e');
            this.k = old_k;
        }
    }

    KStemmer() {
    }

    String stem(String term) {
        boolean changed = this.stem(term.toCharArray(), term.length());
        if (!changed) {
            return term;
        }
        return this.asString();
    }

    String asString() {
        String s = this.getString();
        if (s != null) {
            return s;
        }
        return this.word.toString();
    }

    CharSequence asCharSequence() {
        return this.result != null ? this.result : this.word;
    }

    String getString() {
        return this.result;
    }

    char[] getChars() {
        return this.word.getArray();
    }

    int getLength() {
        return this.word.length();
    }

    private boolean matched() {
        return this.matchedEntry != null;
    }

    boolean stem(char[] term, int len) {
        this.result = null;
        this.k = len - 1;
        if (this.k <= 1 || this.k >= 49) {
            return false;
        }
        DictEntry entry = dict_ht.get(term, 0, len);
        if (entry != null) {
            if (entry.root != null) {
                this.result = entry.root;
                return true;
            }
            return false;
        }
        this.word.reset();
        this.word.reserve(len + 10);
        for (int i = 0; i < len; ++i) {
            char ch = term[i];
            if (!this.isAlpha(ch)) {
                return false;
            }
            this.word.unsafeWrite(ch);
        }
        this.matchedEntry = null;
        this.plural();
        if (!this.matched()) {
            this.pastTense();
            if (!this.matched()) {
                this.aspect();
                if (!this.matched()) {
                    this.ityEndings();
                    if (!this.matched()) {
                        this.nessEndings();
                        if (!this.matched()) {
                            this.ionEndings();
                            if (!this.matched()) {
                                this.erAndOrEndings();
                                if (!this.matched()) {
                                    this.lyEndings();
                                    if (!this.matched()) {
                                        this.alEndings();
                                        if (!this.matched()) {
                                            entry = this.wordInDict();
                                            this.iveEndings();
                                            if (!this.matched()) {
                                                this.izeEndings();
                                                if (!this.matched()) {
                                                    this.mentEndings();
                                                    if (!this.matched()) {
                                                        this.bleEndings();
                                                        if (!this.matched()) {
                                                            this.ismEndings();
                                                            if (!this.matched()) {
                                                                this.icEndings();
                                                                if (!this.matched()) {
                                                                    this.ncyEndings();
                                                                    if (!this.matched()) {
                                                                        this.nceEndings();
                                                                        this.matched();
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        entry = this.matchedEntry;
        if (entry != null) {
            this.result = entry.root;
        }
        return true;
    }

    static class DictEntry {
        boolean exception;
        String root;

        DictEntry(String root, boolean isException) {
            this.root = root;
            this.exception = isException;
        }
    }
}

