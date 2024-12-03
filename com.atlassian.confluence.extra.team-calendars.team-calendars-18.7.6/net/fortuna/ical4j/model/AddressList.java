/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;

public class AddressList
implements Serializable,
Iterable<URI> {
    private static final long serialVersionUID = 81383256078213569L;
    private List<URI> addresses = new CopyOnWriteArrayList<URI>();

    public AddressList() {
    }

    public AddressList(String aValue) throws URISyntaxException {
        StringTokenizer t = new StringTokenizer(aValue, ",");
        while (t.hasMoreTokens()) {
            try {
                this.addresses.add(new URI(Uris.encode(Strings.unquote(t.nextToken()))));
            }
            catch (URISyntaxException use) {
                if (CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed")) continue;
                throw use;
            }
        }
    }

    public final String toString() {
        return this.addresses.stream().map(a -> Strings.quote(Uris.decode(Strings.valueOf(a)))).collect(Collectors.joining(","));
    }

    public final boolean add(URI address) {
        return this.addresses.add(address);
    }

    public final boolean isEmpty() {
        return this.addresses.isEmpty();
    }

    @Override
    public final Iterator<URI> iterator() {
        return this.addresses.iterator();
    }

    public final boolean remove(URI address) {
        return this.addresses.remove(address);
    }

    public final int size() {
        return this.addresses.size();
    }
}

