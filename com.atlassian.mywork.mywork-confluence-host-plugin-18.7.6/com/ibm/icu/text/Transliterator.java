/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.text.AnyTransliterator;
import com.ibm.icu.text.BreakTransliterator;
import com.ibm.icu.text.CaseFoldTransliterator;
import com.ibm.icu.text.CompoundTransliterator;
import com.ibm.icu.text.EscapeTransliterator;
import com.ibm.icu.text.LowercaseTransliterator;
import com.ibm.icu.text.NameUnicodeTransliterator;
import com.ibm.icu.text.NormalizationTransliterator;
import com.ibm.icu.text.NullTransliterator;
import com.ibm.icu.text.RemoveTransliterator;
import com.ibm.icu.text.Replaceable;
import com.ibm.icu.text.ReplaceableString;
import com.ibm.icu.text.RuleBasedTransliterator;
import com.ibm.icu.text.StringTransform;
import com.ibm.icu.text.TitlecaseTransliterator;
import com.ibm.icu.text.TransliteratorIDParser;
import com.ibm.icu.text.TransliteratorParser;
import com.ibm.icu.text.TransliteratorRegistry;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnescapeTransliterator;
import com.ibm.icu.text.UnicodeFilter;
import com.ibm.icu.text.UnicodeNameTransliterator;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.text.UppercaseTransliterator;
import com.ibm.icu.util.CaseInsensitiveString;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;

public abstract class Transliterator
implements StringTransform {
    public static final int FORWARD = 0;
    public static final int REVERSE = 1;
    private String ID;
    private UnicodeSet filter;
    private int maximumContextLength = 0;
    private static TransliteratorRegistry registry = new TransliteratorRegistry();
    private static Map<CaseInsensitiveString, String> displayNameCache = Collections.synchronizedMap(new HashMap());
    private static final String RB_DISPLAY_NAME_PREFIX = "%Translit%%";
    private static final String RB_SCRIPT_DISPLAY_NAME_PREFIX = "%Translit%";
    private static final String RB_DISPLAY_NAME_PATTERN = "TransliteratorNamePattern";
    static final char ID_DELIM = ';';
    static final char ID_SEP = '-';
    static final char VARIANT_SEP = '/';
    static final boolean DEBUG = false;
    private static final String ROOT = "root";
    private static final String RB_RULE_BASED_IDS = "RuleBasedTransliteratorIDs";

    protected Transliterator(String ID, UnicodeFilter filter) {
        if (ID == null) {
            throw new NullPointerException();
        }
        this.ID = ID;
        this.setFilter(filter);
    }

    public final int transliterate(Replaceable text, int start, int limit) {
        if (start < 0 || limit < start || text.length() < limit) {
            return -1;
        }
        Position pos = new Position(start, limit, start);
        this.filteredTransliterate(text, pos, false, true);
        return pos.limit;
    }

    public final void transliterate(Replaceable text) {
        this.transliterate(text, 0, text.length());
    }

    public final String transliterate(String text) {
        ReplaceableString result = new ReplaceableString(text);
        this.transliterate(result);
        return result.toString();
    }

    public final void transliterate(Replaceable text, Position index, String insertion) {
        index.validate(text.length());
        if (insertion != null) {
            text.replace(index.limit, index.limit, insertion);
            index.limit += insertion.length();
            index.contextLimit += insertion.length();
        }
        if (index.limit > 0 && UTF16.isLeadSurrogate(text.charAt(index.limit - 1))) {
            return;
        }
        this.filteredTransliterate(text, index, true, true);
    }

    public final void transliterate(Replaceable text, Position index, int insertion) {
        this.transliterate(text, index, UTF16.valueOf(insertion));
    }

    public final void transliterate(Replaceable text, Position index) {
        this.transliterate(text, index, null);
    }

    public final void finishTransliteration(Replaceable text, Position index) {
        index.validate(text.length());
        this.filteredTransliterate(text, index, false, true);
    }

    protected abstract void handleTransliterate(Replaceable var1, Position var2, boolean var3);

    private void filteredTransliterate(Replaceable text, Position index, boolean incremental, boolean rollback) {
        boolean isIncrementalRun;
        if (this.filter == null && !rollback) {
            this.handleTransliterate(text, index, incremental);
            return;
        }
        int globalLimit = index.limit;
        Object log = null;
        do {
            int delta;
            if (this.filter != null) {
                int c;
                while (index.start < globalLimit && !this.filter.contains(c = text.char32At(index.start))) {
                    index.start += UTF16.getCharCount(c);
                }
                index.limit = index.start;
                while (index.limit < globalLimit && this.filter.contains(c = text.char32At(index.limit))) {
                    index.limit += UTF16.getCharCount(c);
                }
            }
            if (index.start == index.limit) break;
            boolean bl = isIncrementalRun = index.limit < globalLimit ? false : incremental;
            if (rollback && isIncrementalRun) {
                int charLength;
                int runStart = index.start;
                int runLimit = index.limit;
                int runLength = runLimit - runStart;
                int rollbackOrigin = text.length();
                text.copy(runStart, runLimit, rollbackOrigin);
                int passStart = runStart;
                int rollbackStart = rollbackOrigin;
                int passLimit = index.start;
                int uncommittedLength = 0;
                int totalDelta = 0;
                while ((passLimit += (charLength = UTF16.getCharCount(text.char32At(passLimit)))) <= runLimit) {
                    uncommittedLength += charLength;
                    index.limit = passLimit;
                    this.handleTransliterate(text, index, true);
                    delta = index.limit - passLimit;
                    if (index.start != index.limit) {
                        int rs = rollbackStart + delta - (index.limit - passStart);
                        text.replace(passStart, index.limit, "");
                        text.copy(rs, rs + uncommittedLength, passStart);
                        index.start = passStart;
                        index.limit = passLimit;
                        index.contextLimit -= delta;
                        continue;
                    }
                    passStart = passLimit = index.start;
                    rollbackStart += delta + uncommittedLength;
                    uncommittedLength = 0;
                    runLimit += delta;
                    totalDelta += delta;
                }
                globalLimit += totalDelta;
                text.replace(rollbackOrigin += totalDelta, rollbackOrigin + runLength, "");
                index.start = passStart;
                continue;
            }
            int limit = index.limit;
            this.handleTransliterate(text, index, isIncrementalRun);
            delta = index.limit - limit;
            if (!isIncrementalRun && index.start != index.limit) {
                throw new RuntimeException("ERROR: Incomplete non-incremental transliteration by " + this.getID());
            }
            globalLimit += delta;
        } while (this.filter != null && !isIncrementalRun);
        index.limit = globalLimit;
    }

    public void filteredTransliterate(Replaceable text, Position index, boolean incremental) {
        this.filteredTransliterate(text, index, incremental, false);
    }

    public final int getMaximumContextLength() {
        return this.maximumContextLength;
    }

    protected void setMaximumContextLength(int a) {
        if (a < 0) {
            throw new IllegalArgumentException("Invalid context length " + a);
        }
        this.maximumContextLength = a;
    }

    public final String getID() {
        return this.ID;
    }

    protected final void setID(String id) {
        this.ID = id;
    }

    public static final String getDisplayName(String ID) {
        return Transliterator.getDisplayName(ID, ULocale.getDefault(ULocale.Category.DISPLAY));
    }

    public static String getDisplayName(String id, Locale inLocale) {
        return Transliterator.getDisplayName(id, ULocale.forLocale(inLocale));
    }

    public static String getDisplayName(String id, ULocale inLocale) {
        String n;
        ICUResourceBundle bundle = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt73b/translit", inLocale);
        String[] stv = TransliteratorIDParser.IDtoSTV(id);
        if (stv == null) {
            return "";
        }
        String ID = stv[0] + '-' + stv[1];
        if (stv[2] != null && stv[2].length() > 0) {
            ID = ID + '/' + stv[2];
        }
        if ((n = displayNameCache.get(new CaseInsensitiveString(ID))) != null) {
            return n;
        }
        try {
            return bundle.getString(RB_DISPLAY_NAME_PREFIX + ID);
        }
        catch (MissingResourceException missingResourceException) {
            try {
                MessageFormat format = new MessageFormat(bundle.getString(RB_DISPLAY_NAME_PATTERN));
                Object[] args = new Object[]{2, stv[0], stv[1]};
                for (int j = 1; j <= 2; ++j) {
                    try {
                        args[j] = bundle.getString(RB_SCRIPT_DISPLAY_NAME_PREFIX + (String)args[j]);
                        continue;
                    }
                    catch (MissingResourceException missingResourceException2) {
                        // empty catch block
                    }
                }
                return stv[2].length() > 0 ? format.format(args) + '/' + stv[2] : format.format(args);
            }
            catch (MissingResourceException missingResourceException3) {
                throw new RuntimeException();
            }
        }
    }

    public final UnicodeFilter getFilter() {
        return this.filter;
    }

    public void setFilter(UnicodeFilter filter) {
        if (filter == null) {
            this.filter = null;
        } else {
            try {
                this.filter = new UnicodeSet((UnicodeSet)filter).freeze();
            }
            catch (Exception e) {
                this.filter = new UnicodeSet();
                filter.addMatchSetTo(this.filter);
                this.filter.freeze();
            }
        }
    }

    public static final Transliterator getInstance(String ID) {
        return Transliterator.getInstance(ID, 0);
    }

    public static Transliterator getInstance(String ID, int dir) {
        StringBuffer canonID = new StringBuffer();
        ArrayList<TransliteratorIDParser.SingleID> list = new ArrayList<TransliteratorIDParser.SingleID>();
        UnicodeSet[] globalFilter = new UnicodeSet[1];
        if (!TransliteratorIDParser.parseCompoundID(ID, dir, canonID, list, globalFilter)) {
            throw new IllegalArgumentException("Invalid ID " + ID);
        }
        List<Transliterator> translits = TransliteratorIDParser.instantiateList(list);
        Transliterator t = null;
        t = list.size() > 1 || canonID.indexOf(";") >= 0 ? new CompoundTransliterator(translits) : translits.get(0);
        t.setID(canonID.toString());
        if (globalFilter[0] != null) {
            t.setFilter(globalFilter[0]);
        }
        return t;
    }

    static Transliterator getBasicInstance(String id, String canonID) {
        StringBuffer s = new StringBuffer();
        Transliterator t = registry.get(id, s);
        if (s.length() != 0) {
            t = Transliterator.getInstance(s.toString(), 0);
        }
        if (t != null && canonID != null) {
            t.setID(canonID);
        }
        return t;
    }

    public static final Transliterator createFromRules(String ID, String rules, int dir) {
        Transliterator t = null;
        TransliteratorParser parser = new TransliteratorParser();
        parser.parse(rules, dir);
        if (parser.idBlockVector.size() == 0 && parser.dataVector.size() == 0) {
            t = new NullTransliterator();
        } else if (parser.idBlockVector.size() == 0 && parser.dataVector.size() == 1) {
            t = new RuleBasedTransliterator(ID, parser.dataVector.get(0), parser.compoundFilter);
        } else if (parser.idBlockVector.size() == 1 && parser.dataVector.size() == 0) {
            t = parser.compoundFilter != null ? Transliterator.getInstance(parser.compoundFilter.toPattern(false) + ";" + parser.idBlockVector.get(0)) : Transliterator.getInstance(parser.idBlockVector.get(0));
            if (t != null) {
                t.setID(ID);
            }
        } else {
            ArrayList<Transliterator> transliterators = new ArrayList<Transliterator>();
            int passNumber = 1;
            int limit = Math.max(parser.idBlockVector.size(), parser.dataVector.size());
            for (int i = 0; i < limit; ++i) {
                Transliterator temp;
                String idBlock;
                if (i < parser.idBlockVector.size() && (idBlock = parser.idBlockVector.get(i)).length() > 0 && !((temp = Transliterator.getInstance(idBlock)) instanceof NullTransliterator)) {
                    transliterators.add(Transliterator.getInstance(idBlock));
                }
                if (i >= parser.dataVector.size()) continue;
                RuleBasedTransliterator.Data data = parser.dataVector.get(i);
                transliterators.add(new RuleBasedTransliterator("%Pass" + passNumber++, data, null));
            }
            t = new CompoundTransliterator(transliterators, passNumber - 1);
            t.setID(ID);
            if (parser.compoundFilter != null) {
                t.setFilter(parser.compoundFilter);
            }
        }
        return t;
    }

    public String toRules(boolean escapeUnprintable) {
        return this.baseToRules(escapeUnprintable);
    }

    protected final String baseToRules(boolean escapeUnprintable) {
        if (escapeUnprintable) {
            int c;
            StringBuffer rulesSource = new StringBuffer();
            String id = this.getID();
            for (int i = 0; i < id.length(); i += UTF16.getCharCount(c)) {
                c = UTF16.charAt(id, i);
                if (Utility.escapeUnprintable(rulesSource, c)) continue;
                UTF16.append(rulesSource, c);
            }
            rulesSource.insert(0, "::");
            rulesSource.append(';');
            return rulesSource.toString();
        }
        return "::" + this.getID() + ';';
    }

    public Transliterator[] getElements() {
        Transliterator[] result;
        if (this instanceof CompoundTransliterator) {
            CompoundTransliterator cpd = (CompoundTransliterator)this;
            result = new Transliterator[cpd.getCount()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = cpd.getTransliterator(i);
            }
        } else {
            result = new Transliterator[]{this};
        }
        return result;
    }

    public final UnicodeSet getSourceSet() {
        UnicodeSet result = new UnicodeSet();
        this.addSourceTargetSet(this.getFilterAsUnicodeSet(UnicodeSet.ALL_CODE_POINTS), result, new UnicodeSet());
        return result;
    }

    protected UnicodeSet handleGetSourceSet() {
        return new UnicodeSet();
    }

    public UnicodeSet getTargetSet() {
        UnicodeSet result = new UnicodeSet();
        this.addSourceTargetSet(this.getFilterAsUnicodeSet(UnicodeSet.ALL_CODE_POINTS), new UnicodeSet(), result);
        return result;
    }

    @Deprecated
    public void addSourceTargetSet(UnicodeSet inputFilter, UnicodeSet sourceSet, UnicodeSet targetSet) {
        UnicodeSet myFilter = this.getFilterAsUnicodeSet(inputFilter);
        UnicodeSet temp = new UnicodeSet(this.handleGetSourceSet()).retainAll(myFilter);
        sourceSet.addAll(temp);
        for (String s : temp) {
            String t;
            if (s.equals(t = this.transliterate(s))) continue;
            targetSet.addAll(t);
        }
    }

    @Deprecated
    public UnicodeSet getFilterAsUnicodeSet(UnicodeSet externalFilter) {
        UnicodeSet temp;
        if (this.filter == null) {
            return externalFilter;
        }
        UnicodeSet filterSet = new UnicodeSet(externalFilter);
        try {
            temp = this.filter;
        }
        catch (ClassCastException e) {
            temp = new UnicodeSet();
            this.filter.addMatchSetTo(temp);
        }
        return filterSet.retainAll(temp).freeze();
    }

    public final Transliterator getInverse() {
        return Transliterator.getInstance(this.ID, 1);
    }

    public static void registerClass(String ID, Class<? extends Transliterator> transClass, String displayName) {
        registry.put(ID, transClass, true);
        if (displayName != null) {
            displayNameCache.put(new CaseInsensitiveString(ID), displayName);
        }
    }

    public static void registerFactory(String ID, Factory factory) {
        registry.put(ID, factory, true);
    }

    public static void registerInstance(Transliterator trans) {
        registry.put(trans.getID(), trans, true);
    }

    static void registerInstance(Transliterator trans, boolean visible) {
        registry.put(trans.getID(), trans, visible);
    }

    public static void registerAlias(String aliasID, String realID) {
        registry.put(aliasID, realID, true);
    }

    static void registerSpecialInverse(String target, String inverseTarget, boolean bidirectional) {
        TransliteratorIDParser.registerSpecialInverse(target, inverseTarget, bidirectional);
    }

    public static void unregister(String ID) {
        displayNameCache.remove(new CaseInsensitiveString(ID));
        registry.remove(ID);
    }

    public static final Enumeration<String> getAvailableIDs() {
        return registry.getAvailableIDs();
    }

    public static final Enumeration<String> getAvailableSources() {
        return registry.getAvailableSources();
    }

    public static final Enumeration<String> getAvailableTargets(String source) {
        return registry.getAvailableTargets(source);
    }

    public static final Enumeration<String> getAvailableVariants(String source, String target) {
        return registry.getAvailableVariants(source, target);
    }

    @Deprecated
    public static void registerAny() {
        AnyTransliterator.register();
    }

    @Override
    public String transform(String source) {
        return this.transliterate(source);
    }

    static {
        UResourceBundle bundle = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt73b/translit", ROOT);
        UResourceBundle transIDs = bundle.get(RB_RULE_BASED_IDS);
        int maxRows = transIDs.getSize();
        for (int row = 0; row < maxRows; ++row) {
            String resString;
            UResourceBundle colBund = transIDs.get(row);
            String ID = colBund.getKey();
            if (ID.indexOf("-t-") >= 0) continue;
            UResourceBundle res = colBund.get(0);
            String type = res.getKey();
            if (type.equals("file") || type.equals("internal")) {
                int dir;
                resString = res.getString("resource");
                String direction = res.getString("direction");
                switch (direction.charAt(0)) {
                    case 'F': {
                        dir = 0;
                        break;
                    }
                    case 'R': {
                        dir = 1;
                        break;
                    }
                    default: {
                        throw new RuntimeException("Can't parse direction: " + direction);
                    }
                }
                registry.put(ID, resString, dir, !type.equals("internal"));
                continue;
            }
            if (type.equals("alias")) {
                resString = res.getString();
                registry.put(ID, resString, true);
                continue;
            }
            throw new RuntimeException("Unknown type: " + type);
        }
        Transliterator.registerSpecialInverse("Null", "Null", false);
        Transliterator.registerClass("Any-Null", NullTransliterator.class, null);
        RemoveTransliterator.register();
        EscapeTransliterator.register();
        UnescapeTransliterator.register();
        LowercaseTransliterator.register();
        UppercaseTransliterator.register();
        TitlecaseTransliterator.register();
        CaseFoldTransliterator.register();
        UnicodeNameTransliterator.register();
        NameUnicodeTransliterator.register();
        NormalizationTransliterator.register();
        BreakTransliterator.register();
        AnyTransliterator.register();
    }

    public static interface Factory {
        public Transliterator getInstance(String var1);
    }

    public static class Position {
        public int contextStart;
        public int contextLimit;
        public int start;
        public int limit;

        public Position() {
            this(0, 0, 0, 0);
        }

        public Position(int contextStart, int contextLimit, int start) {
            this(contextStart, contextLimit, start, contextLimit);
        }

        public Position(int contextStart, int contextLimit, int start, int limit) {
            this.contextStart = contextStart;
            this.contextLimit = contextLimit;
            this.start = start;
            this.limit = limit;
        }

        public Position(Position pos) {
            this.set(pos);
        }

        public void set(Position pos) {
            this.contextStart = pos.contextStart;
            this.contextLimit = pos.contextLimit;
            this.start = pos.start;
            this.limit = pos.limit;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Position) {
                Position pos = (Position)obj;
                return this.contextStart == pos.contextStart && this.contextLimit == pos.contextLimit && this.start == pos.start && this.limit == pos.limit;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.contextStart, this.contextLimit, this.start, this.limit);
        }

        public String toString() {
            return "[cs=" + this.contextStart + ", s=" + this.start + ", l=" + this.limit + ", cl=" + this.contextLimit + "]";
        }

        public final void validate(int length) {
            if (this.contextStart < 0 || this.start < this.contextStart || this.limit < this.start || this.contextLimit < this.limit || length < this.contextLimit) {
                throw new IllegalArgumentException("Invalid Position {cs=" + this.contextStart + ", s=" + this.start + ", l=" + this.limit + ", cl=" + this.contextLimit + "}, len=" + length);
            }
        }
    }
}

