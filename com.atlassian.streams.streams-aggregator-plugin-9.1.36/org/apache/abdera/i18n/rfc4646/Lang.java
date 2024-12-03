/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.rfc4646;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.abdera.i18n.rfc4646.Range;
import org.apache.abdera.i18n.rfc4646.Subtag;
import org.apache.abdera.i18n.rfc4646.SubtagSet;

public final class Lang
extends SubtagSet {
    private static final long serialVersionUID = -7095560018906537331L;
    private final Locale locale = this.initLocale();
    private static final String language = "((?:[a-zA-Z]{2,3}(?:[-_][a-zA-Z]{3}){0,3})|[a-zA-Z]{4}|[a-zA-Z]{5,8})";
    private static final String script = "((?:[-_][a-zA-Z]{4})?)";
    private static final String region = "((?:[-_](?:(?:[a-zA-Z]{2})|(?:[0-9]{3})))?)";
    private static final String variant = "((?:[-_](?:(?:[a-zA-Z0-9]{5,8})|(?:[0-9][a-zA-Z0-9]{3})))*)";
    private static final String extension = "((?:[-_][a-wy-zA-WY-Z0-9](?:[-_][a-zA-Z0-9]{2,8})+)*)";
    private static final String privateuse = "[xX](?:[-_][a-zA-Z0-9]{2,8})+";
    private static final String _privateuse = "((?:[-_][xX](?:[-_][a-zA-Z0-9]{2,8})+)?)";
    private static final String grandfathered = "^(?:art[-_]lojban|cel[-_]gaulish|en[-_]GB[-_]oed|i[-_]ami|i[-_]bnn|i[-_]default|i[-_]enochian|i[-_]hak|i[-_]klingon|i[-_]lux|i[-_]mingo|i[-_]navajo|i[-_]pwn|i[-_]tao||i[-_]tay|i[-_]tsu|no[-_]bok|no[-_]nyn|sgn[-_]BE[-_]fr|sgn[-_]BE[-_]nl|sgn[-_]CH[-_]de|zh[-_]cmn|zh[-_]cmn[-_]Hans|zh[-_]cmn[-_]Hant|zh[-_]gan|zh[-_]guoyu|zh[-_]hakka|zh[-_]min|zh[-_]min[-_]nan|zh[-_]wuu|zh[-_]xiang|zh[-_]yue)$";
    private static final String langtag = "^((?:[a-zA-Z]{2,3}(?:[-_][a-zA-Z]{3}){0,3})|[a-zA-Z]{4}|[a-zA-Z]{5,8})((?:[-_][a-zA-Z]{4})?)((?:[-_](?:(?:[a-zA-Z]{2})|(?:[0-9]{3})))?)((?:[-_](?:(?:[a-zA-Z0-9]{5,8})|(?:[0-9][a-zA-Z0-9]{3})))*)((?:[-_][a-wy-zA-WY-Z0-9](?:[-_][a-zA-Z0-9]{2,8})+)*)((?:[-_][xX](?:[-_][a-zA-Z0-9]{2,8})+)?)$";
    private static final Pattern p_langtag = Pattern.compile("^((?:[a-zA-Z]{2,3}(?:[-_][a-zA-Z]{3}){0,3})|[a-zA-Z]{4}|[a-zA-Z]{5,8})((?:[-_][a-zA-Z]{4})?)((?:[-_](?:(?:[a-zA-Z]{2})|(?:[0-9]{3})))?)((?:[-_](?:(?:[a-zA-Z0-9]{5,8})|(?:[0-9][a-zA-Z0-9]{3})))*)((?:[-_][a-wy-zA-WY-Z0-9](?:[-_][a-zA-Z0-9]{2,8})+)*)((?:[-_][xX](?:[-_][a-zA-Z0-9]{2,8})+)?)$");
    private static final Pattern p_privateuse = Pattern.compile("^[xX](?:[-_][a-zA-Z0-9]{2,8})+$");
    private static final Pattern p_grandfathered = Pattern.compile("^(?:art[-_]lojban|cel[-_]gaulish|en[-_]GB[-_]oed|i[-_]ami|i[-_]bnn|i[-_]default|i[-_]enochian|i[-_]hak|i[-_]klingon|i[-_]lux|i[-_]mingo|i[-_]navajo|i[-_]pwn|i[-_]tao||i[-_]tay|i[-_]tsu|no[-_]bok|no[-_]nyn|sgn[-_]BE[-_]fr|sgn[-_]BE[-_]nl|sgn[-_]CH[-_]de|zh[-_]cmn|zh[-_]cmn[-_]Hans|zh[-_]cmn[-_]Hant|zh[-_]gan|zh[-_]guoyu|zh[-_]hakka|zh[-_]min|zh[-_]min[-_]nan|zh[-_]wuu|zh[-_]xiang|zh[-_]yue)$");

    public Lang() {
        this(Lang.init(Locale.getDefault()));
    }

    public Lang(Locale locale) {
        this(Lang.init(locale));
    }

    private static Subtag init(Locale locale) {
        try {
            return Lang.parse((String)locale.toString()).primary;
        }
        catch (Exception e) {
            Subtag c = null;
            Subtag primary = new Subtag(Subtag.Type.PRIMARY, locale.getLanguage());
            String country = locale.getCountry();
            String variant = locale.getVariant();
            if (country != null) {
                c = new Subtag(Subtag.Type.REGION, country, primary);
            }
            if (variant != null) {
                new Subtag(Subtag.Type.VARIANT, variant, c);
            }
            return primary;
        }
    }

    public Lang(String lang) {
        this(Lang.parse((String)lang).primary);
    }

    Lang(Subtag primary) {
        super(primary);
    }

    private Locale initLocale() {
        Subtag primary = this.getLanguage();
        Subtag region = this.getRegion();
        Subtag variant = this.getVariant();
        if (variant != null && region != null) {
            return new Locale(primary.toString(), region.toString(), variant.toString());
        }
        if (region != null) {
            return new Locale(primary.toString(), region.toString());
        }
        return new Locale(primary.toString());
    }

    public Subtag getLanguage() {
        return this.primary;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public Subtag getExtLang() {
        block4: for (Subtag subtag : this) {
            switch (subtag.getType()) {
                case PRIMARY: {
                    continue block4;
                }
                case EXTLANG: {
                    return subtag;
                }
            }
            return null;
        }
        return null;
    }

    public Subtag getScript() {
        block4: for (Subtag subtag : this) {
            switch (subtag.getType()) {
                case PRIMARY: 
                case EXTLANG: {
                    continue block4;
                }
                case SCRIPT: {
                    return subtag;
                }
            }
            return null;
        }
        return null;
    }

    public Subtag getRegion() {
        block4: for (Subtag subtag : this) {
            switch (subtag.getType()) {
                case PRIMARY: 
                case EXTLANG: 
                case SCRIPT: {
                    continue block4;
                }
                case REGION: {
                    return subtag;
                }
            }
            return null;
        }
        return null;
    }

    public Subtag getVariant() {
        block4: for (Subtag subtag : this) {
            switch (subtag.getType()) {
                case PRIMARY: 
                case EXTLANG: 
                case SCRIPT: 
                case REGION: {
                    continue block4;
                }
                case VARIANT: {
                    return subtag;
                }
            }
            return null;
        }
        return null;
    }

    public Subtag getExtension() {
        block4: for (Subtag subtag : this) {
            switch (subtag.getType()) {
                case PRIMARY: 
                case EXTLANG: 
                case SCRIPT: 
                case REGION: 
                case VARIANT: {
                    continue block4;
                }
                case EXTENSION: {
                    return subtag.getPrevious();
                }
            }
            return null;
        }
        return null;
    }

    public Subtag getPrivateUse() {
        block4: for (Subtag subtag : this) {
            switch (subtag.getType()) {
                case PRIMARY: 
                case EXTLANG: 
                case SCRIPT: 
                case REGION: 
                case VARIANT: 
                case EXTENSION: {
                    continue block4;
                }
                case PRIVATEUSE: {
                    return subtag.getPrevious();
                }
            }
            return null;
        }
        return null;
    }

    public Range asRange() {
        return new Range(this.toString());
    }

    public Lang clone() {
        return new Lang(this.primary.clone());
    }

    public Lang canonicalize() {
        Subtag primary = null;
        Subtag current = null;
        int p = -1;
        int t = -1;
        LinkedList<Subtag> tags = new LinkedList<Subtag>();
        for (Subtag tag : this) {
            tags.add(tag);
        }
        LinkedList<Subtag> ext = new LinkedList<Subtag>();
        for (Subtag tag : tags) {
            if (tag.getType() != Subtag.Type.SINGLETON || tag.getName().equalsIgnoreCase("x")) continue;
            ext.add(tag);
        }
        if (ext.size() > 0) {
            p = tags.indexOf(ext.get(0));
            t = tags.indexOf(ext.get(ext.size() - 1));
        }
        Collections.sort(ext, new Comparator<Subtag>(){

            @Override
            public int compare(Subtag o1, Subtag o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        LinkedList<Subtag> extchain = new LinkedList<Subtag>();
        for (Subtag tag : ext) {
            extchain.add(tag);
            for (current = tag.getNext(); current != null && current.getType() == Subtag.Type.EXTENSION; current = current.getNext()) {
                extchain.add(current);
            }
        }
        LinkedList result = new LinkedList();
        result.addAll(tags.subList(0, p));
        result.addAll(extchain);
        result.addAll(tags.subList(t + 2, tags.size()));
        current = null;
        for (Subtag tag : result) {
            tag = tag.canonicalize();
            if (primary == null) {
                current = primary = tag;
                continue;
            }
            current.setNext(tag);
            current = tag;
        }
        return new Lang(primary);
    }

    public boolean isDeprecated() {
        for (Subtag tag : this) {
            if (!tag.isDeprecated()) continue;
            return true;
        }
        return false;
    }

    public Lang getParent() {
        Lang lang = this.clone();
        Subtag last = null;
        Iterator i$ = lang.iterator();
        while (i$.hasNext()) {
            Subtag tag;
            last = tag = (Subtag)i$.next();
        }
        if (last.getPrevious() == null) {
            return null;
        }
        last.getPrevious().setNext(null);
        return lang;
    }

    public boolean isChildOf(Lang lang) {
        Range range = new Range(lang).appendWildcard();
        return range.matches(this);
    }

    public boolean isParentOf(Lang lang) {
        return lang.isChildOf(this);
    }

    public static Lang parse(String lang) {
        Subtag primary = null;
        Matcher m = p_grandfathered.matcher(lang);
        if (m.find()) {
            String[] tags = lang.split("[-_]");
            Subtag current = null;
            for (String tag : tags) {
                current = current == null ? (primary = new Subtag(Subtag.Type.GRANDFATHERED, tag, null)) : new Subtag(Subtag.Type.GRANDFATHERED, tag, current);
            }
            return new Lang(primary);
        }
        m = p_privateuse.matcher(lang);
        if (m.find()) {
            String[] tags = lang.split("[-_]");
            Subtag current = null;
            for (String tag : tags) {
                current = current == null ? (primary = new Subtag(Subtag.Type.SINGLETON, tag, null)) : new Subtag(Subtag.Type.PRIVATEUSE, tag, current);
            }
            return new Lang(primary);
        }
        m = p_langtag.matcher(lang);
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
                current = current == null ? (primary = new Subtag(Subtag.Type.PRIMARY, tag)) : new Subtag(Subtag.Type.EXTLANG, tag, current);
            }
            if (script != null && script.length() > 0) {
                current = new Subtag(Subtag.Type.SCRIPT, script.substring(1), current);
            }
            if (region != null && region.length() > 0) {
                current = new Subtag(Subtag.Type.REGION, region.substring(1), current);
            }
            if (variant != null && variant.length() > 0) {
                variant = variant.substring(1);
                for (String tag : tags = variant.split("-")) {
                    current = new Subtag(Subtag.Type.VARIANT, tag, current);
                }
            }
            if (extension != null && extension.length() > 0) {
                extension = extension.substring(1);
                tags = extension.split("-");
                current = new Subtag(Subtag.Type.SINGLETON, tags[0], current);
                for (int i = 1; i < tags.length; ++i) {
                    String tag = tags[i];
                    current = new Subtag(tag.length() == 1 ? Subtag.Type.SINGLETON : Subtag.Type.EXTENSION, tag, current);
                }
            }
            if (privateuse != null && privateuse.length() > 0) {
                privateuse = privateuse.substring(1);
                tags = privateuse.split("-");
                current = new Subtag(Subtag.Type.SINGLETON, tags[0], current);
                for (int i = 1; i < tags.length; ++i) {
                    current = new Subtag(Subtag.Type.PRIVATEUSE, tags[i], current);
                }
            }
            return new Lang(primary);
        }
        throw new IllegalArgumentException();
    }

    public static String fromLocale(Locale locale) {
        return new Lang(locale).toString();
    }
}

