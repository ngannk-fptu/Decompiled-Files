/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.fortuna.ical4j.util.Strings;

public class TextList
implements Serializable,
Iterable<String> {
    private static final long serialVersionUID = -417427815871330636L;
    private List<String> texts;

    public TextList() {
        this.texts = new CopyOnWriteArrayList<String>();
    }

    public TextList(String aValue) {
        this.texts = new CopyOnWriteArrayList<String>();
        Pattern pattern = Pattern.compile("(?:\\\\.|[^\\\\,]++)+");
        Matcher matcher = pattern.matcher(aValue);
        while (matcher.find()) {
            this.texts.add(Strings.unescape(matcher.group().replace("\\\\", "\\")));
        }
    }

    public TextList(String[] textValues) {
        this.texts = Arrays.asList(textValues);
    }

    public final String toString() {
        return this.texts.stream().map(t -> Strings.escape(t)).collect(Collectors.joining(","));
    }

    public final boolean add(String text) {
        return this.texts.add(text);
    }

    public final boolean isEmpty() {
        return this.texts.isEmpty();
    }

    @Override
    public final Iterator<String> iterator() {
        return this.texts.iterator();
    }

    public final boolean remove(String text) {
        return this.texts.remove(text);
    }

    public final int size() {
        return this.texts.size();
    }
}

