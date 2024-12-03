/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.rfc4646;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.i18n.rfc4646.Subtag;
import org.apache.abdera.i18n.rfc4646.SubtagSet;

public class Range
extends SubtagSet {
    private static final long serialVersionUID = -6397227794306856431L;
    private final boolean extended;
    private static final String range = "((?:[a-zA-Z]{1,8}|\\*))((?:[-_](?:[a-zA-Z0-9]{1,8}|\\*))*)";
    private static final String range_component = "[-_]((?:[a-zA-Z0-9]{1,8}|\\*))";
    private static final Pattern p_range = Pattern.compile("((?:[a-zA-Z]{1,8}|\\*))((?:[-_](?:[a-zA-Z0-9]{1,8}|\\*))*)");
    private static final Pattern p_range_component = Pattern.compile("[-_]((?:[a-zA-Z0-9]{1,8}|\\*))");
    private static final String language = "((?:[a-zA-Z]{2,3}(?:[-_](?:[a-zA-Z]{3}|\\*)){0,3})|[a-zA-Z]{4}|[a-zA-Z]{5,8}|\\*)";
    private static final String script = "((?:[-_](?:[a-zA-Z]{4}|\\*))?)";
    private static final String region = "((?:[-_](?:(?:[a-zA-Z]{2})|(?:[0-9]{3})|\\*))?)";
    private static final String variant = "((?:[-_](?:(?:[a-zA-Z0-9]{5,8})|(?:[0-9][a-zA-Z0-9]{3})|\\*))*)";
    private static final String extension = "((?:[-_](?:(?:[a-wy-zA-WY-Z0-9](?:[-_][a-zA-Z0-9]{2,8})+)|\\*))*)";
    private static final String privateuse = "[xX](?:[-_][a-zA-Z0-9]{2,8})+";
    private static final String _privateuse = "((?:[-_](?:[xX](?:[-_][a-zA-Z0-9]{2,8})+)+|\\*)?)";
    private static final String langtag = "^((?:[a-zA-Z]{2,3}(?:[-_](?:[a-zA-Z]{3}|\\*)){0,3})|[a-zA-Z]{4}|[a-zA-Z]{5,8}|\\*)((?:[-_](?:[a-zA-Z]{4}|\\*))?)((?:[-_](?:(?:[a-zA-Z]{2})|(?:[0-9]{3})|\\*))?)((?:[-_](?:(?:[a-zA-Z0-9]{5,8})|(?:[0-9][a-zA-Z0-9]{3})|\\*))*)((?:[-_](?:(?:[a-wy-zA-WY-Z0-9](?:[-_][a-zA-Z0-9]{2,8})+)|\\*))*)((?:[-_](?:[xX](?:[-_][a-zA-Z0-9]{2,8})+)+|\\*)?)$";
    private static final String grandfathered = "^(?:art[-_]lojban|cel[-_]gaulish|en[-_]GB[-_]oed|i[-_]ami|i[-_]bnn|i[-_]default|i[-_]enochian|i[-_]hak|i[-_]klingon|i[-_]lux|i[-_]mingo|i[-_]navajo|i[-_]pwn|i[-_]tao||i[-_]tay|i[-_]tsu|no[-_]bok|no[-_]nyn|sgn[-_]BE[-_]fr|sgn[-_]BE[-_]nl|sgn[-_]CH[-_]de|zh[-_]cmn|zh[-_]cmn[-_]Hans|zh[-_]cmn[-_]Hant|zh[-_]gan|zh[-_]guoyu|zh[-_]hakka|zh[-_]min|zh[-_]min[-_]nan|zh[-_]wuu|zh[-_]xiang|zh[-_]yue)$";
    private static final Pattern p_privateuse = Pattern.compile("^[xX](?:[-_][a-zA-Z0-9]{2,8})+$");
    private static final Pattern p_grandfathered = Pattern.compile("^(?:art[-_]lojban|cel[-_]gaulish|en[-_]GB[-_]oed|i[-_]ami|i[-_]bnn|i[-_]default|i[-_]enochian|i[-_]hak|i[-_]klingon|i[-_]lux|i[-_]mingo|i[-_]navajo|i[-_]pwn|i[-_]tao||i[-_]tay|i[-_]tsu|no[-_]bok|no[-_]nyn|sgn[-_]BE[-_]fr|sgn[-_]BE[-_]nl|sgn[-_]CH[-_]de|zh[-_]cmn|zh[-_]cmn[-_]Hans|zh[-_]cmn[-_]Hant|zh[-_]gan|zh[-_]guoyu|zh[-_]hakka|zh[-_]min|zh[-_]min[-_]nan|zh[-_]wuu|zh[-_]xiang|zh[-_]yue)$");
    private static final Pattern p_extended_range = Pattern.compile("^((?:[a-zA-Z]{2,3}(?:[-_](?:[a-zA-Z]{3}|\\*)){0,3})|[a-zA-Z]{4}|[a-zA-Z]{5,8}|\\*)((?:[-_](?:[a-zA-Z]{4}|\\*))?)((?:[-_](?:(?:[a-zA-Z]{2})|(?:[0-9]{3})|\\*))?)((?:[-_](?:(?:[a-zA-Z0-9]{5,8})|(?:[0-9][a-zA-Z0-9]{3})|\\*))*)((?:[-_](?:(?:[a-wy-zA-WY-Z0-9](?:[-_][a-zA-Z0-9]{2,8})+)|\\*))*)((?:[-_](?:[xX](?:[-_][a-zA-Z0-9]{2,8})+)+|\\*)?)$");

    public Range(String range, boolean extended) {
        super(Range.parse((String)range, (boolean)extended).primary);
        this.extended = extended;
    }

    public Range(String range) {
        this(Range.parse((String)range).primary);
    }

    public Range(Lang lang) {
        this(lang.toString());
    }

    public Range(Lang lang, boolean extended) {
        this(lang.toString(), extended);
    }

    Range(Subtag primary) {
        super(primary);
        this.extended = !this.checkBasic();
    }

    public Range append(Subtag subtag) {
        Subtag last = null;
        Iterator i$ = this.iterator();
        while (i$.hasNext()) {
            Subtag tag;
            last = tag = (Subtag)i$.next();
        }
        last.setNext(subtag);
        return this;
    }

    public Range appendWildcard() {
        return this.append(Subtag.newWildcard());
    }

    public Range clone() {
        return new Range(this.primary.clone());
    }

    public Range toBasicRange() {
        if (this.primary.getType() == Subtag.Type.WILDCARD) {
            return new Range("*");
        }
        LinkedList<Subtag> list = new LinkedList<Subtag>();
        for (Subtag tag : this) {
            if (tag.getType() == Subtag.Type.WILDCARD) continue;
            list.add(tag.clone());
        }
        Subtag primary = null;
        Subtag current = null;
        for (Subtag tag : list) {
            tag.setNext(null);
            tag.setPrevious(null);
            if (primary == null) {
                current = primary = tag;
                continue;
            }
            current.setNext(tag);
            current = tag;
        }
        return new Range(primary);
    }

    public boolean isBasic() {
        return !this.extended;
    }

    private boolean checkBasic() {
        for (Subtag current = this.primary.getNext(); current != null; current = current.getNext()) {
            if (current.getType() != Subtag.Type.WILDCARD) continue;
            return false;
        }
        return true;
    }

    public boolean matches(String lang) {
        return this.matches(new Lang(lang), this.extended);
    }

    public boolean matches(String lang, boolean extended) {
        return this.matches(new Lang(lang), extended);
    }

    public boolean matches(Lang lang) {
        return this.matches(lang, false);
    }

    public boolean matches(Lang lang, boolean extended) {
        Subtag ecurrent;
        Iterator i = this.iterator();
        Iterator e = lang.iterator();
        if (this.isBasic() && !extended) {
            if (this.primary.getType() == Subtag.Type.WILDCARD) {
                return true;
            }
            while (i.hasNext() && e.hasNext()) {
                Subtag en;
                Subtag in = (Subtag)i.next();
                if (in.equals(en = (Subtag)e.next())) continue;
                return false;
            }
            return true;
        }
        Subtag icurrent = (Subtag)i.next();
        if (!icurrent.equals(ecurrent = (Subtag)e.next())) {
            return false;
        }
        while (i.hasNext()) {
            icurrent = (Subtag)i.next();
            while (icurrent.getType() == Subtag.Type.WILDCARD && i.hasNext()) {
                icurrent = (Subtag)i.next();
            }
            if (icurrent.getType() == Subtag.Type.WILDCARD) {
                return true;
            }
            boolean matched = false;
            while (e.hasNext()) {
                ecurrent = (Subtag)e.next();
                if (extended && ecurrent.getType().ordinal() < icurrent.getType().ordinal()) continue;
                if (!ecurrent.equals(icurrent)) break;
                matched = true;
                break;
            }
            if (matched) continue;
            return false;
        }
        return true;
    }

    public Lang[] filter(Lang ... lang) {
        LinkedList<Lang> langs = new LinkedList<Lang>();
        for (Lang l : lang) {
            if (!this.matches(l)) continue;
            langs.add(l);
        }
        return langs.toArray(new Lang[langs.size()]);
    }

    public String[] filter(String ... lang) {
        LinkedList<String> langs = new LinkedList<String>();
        for (String l : lang) {
            if (!this.matches(l)) continue;
            langs.add(l);
        }
        return langs.toArray(new String[langs.size()]);
    }

    public static Lang[] filter(String range, Lang ... lang) {
        return new Range(range).filter(lang);
    }

    public static String[] filter(String range, String ... lang) {
        return new Range(range).filter(lang);
    }

    public static boolean matches(String range, Lang lang, boolean extended) {
        return new Range(range, extended).matches(lang);
    }

    public static boolean matches(String range, Lang lang) {
        return new Range(range).matches(lang);
    }

    public static boolean matches(String range, String lang, boolean extended) {
        return new Range(range, extended).matches(lang);
    }

    public static boolean matches(String range, String lang) {
        return new Range(range).matches(lang);
    }

    public static Range parse(String range) {
        return Range.parse(range, false);
    }

    public static Range parse(String range, boolean extended) {
        if (!extended) {
            Subtag primary = null;
            Subtag current = null;
            Matcher m = p_range.matcher(range);
            if (m.find()) {
                String first = m.group(1);
                String therest = m.group(2);
                current = primary = new Subtag(first.equals("*") ? Subtag.Type.WILDCARD : Subtag.Type.SIMPLE, first.toLowerCase(Locale.US));
                Matcher n = p_range_component.matcher(therest);
                while (n.find()) {
                    String name = n.group(1).toLowerCase(Locale.US);
                    current = new Subtag(name.equals("*") ? Subtag.Type.WILDCARD : Subtag.Type.SIMPLE, name, current);
                }
            }
            return new Range(primary);
        }
        Subtag primary = null;
        Matcher m = p_grandfathered.matcher(range);
        if (m.find()) {
            String[] tags = range.split("[-_]");
            Subtag current = null;
            for (String tag : tags) {
                current = current == null ? (primary = new Subtag(Subtag.Type.GRANDFATHERED, tag, null)) : new Subtag(Subtag.Type.GRANDFATHERED, tag, current);
            }
            return new Range(primary);
        }
        m = p_privateuse.matcher(range);
        if (m.find()) {
            String[] tags = range.split("[-_]");
            Subtag current = null;
            for (String tag : tags) {
                current = current == null ? (primary = new Subtag(tag.equals("*") ? Subtag.Type.WILDCARD : Subtag.Type.SINGLETON, tag, null)) : new Subtag(tag.equals("*") ? Subtag.Type.WILDCARD : Subtag.Type.PRIVATEUSE, tag, current);
            }
            return new Range(primary);
        }
        m = p_extended_range.matcher(range);
        if (m.find()) {
            String[] tags;
            String langtag = m.group(1);
            String script = m.group(2);
            String region = m.group(3);
            String variant = m.group(4);
            String extension = m.group(5);
            String privateuse = m.group(6);
            Subtag current = null;
            for (String tag : tags = langtag.split("[-_]")) {
                current = current == null ? (primary = new Subtag(tag.equals("*") ? Subtag.Type.WILDCARD : Subtag.Type.PRIMARY, tag)) : new Subtag(tag.equals("*") ? Subtag.Type.WILDCARD : Subtag.Type.EXTLANG, tag, current);
            }
            if (script != null && script.length() > 0) {
                current = new Subtag(script.substring(1).equals("*") ? Subtag.Type.WILDCARD : Subtag.Type.SCRIPT, script.substring(1), current);
            }
            if (region != null && region.length() > 0) {
                current = new Subtag(region.substring(1).equals("*") ? Subtag.Type.WILDCARD : Subtag.Type.REGION, region.substring(1), current);
            }
            if (variant != null && variant.length() > 0) {
                variant = variant.substring(1);
                for (String tag : tags = variant.split("-")) {
                    current = new Subtag(tag.equals("*") ? Subtag.Type.WILDCARD : Subtag.Type.VARIANT, tag, current);
                }
            }
            if (extension != null && extension.length() > 0) {
                tags = (extension = extension.substring(1)).split("-");
                current = new Subtag(tags[0].equals("*") ? Subtag.Type.WILDCARD : Subtag.Type.SINGLETON, tags[0], current);
                for (int i = 1; i < tags.length; ++i) {
                    String tag = tags[i];
                    current = new Subtag(tag.equals("*") ? Subtag.Type.WILDCARD : (tag.length() == 1 ? Subtag.Type.SINGLETON : Subtag.Type.EXTENSION), tag, current);
                }
            }
            if (privateuse != null && privateuse.length() > 0) {
                tags = (privateuse = privateuse.substring(1)).split("-");
                current = new Subtag(tags[0].equals("*") ? Subtag.Type.WILDCARD : Subtag.Type.SINGLETON, tags[0], current);
                for (int i = 1; i < tags.length; ++i) {
                    current = new Subtag(tags[i].equals("*") ? Subtag.Type.WILDCARD : Subtag.Type.PRIVATEUSE, tags[i], current);
                }
            }
            return new Range(primary);
        }
        throw new IllegalArgumentException("Invalid range");
    }
}

