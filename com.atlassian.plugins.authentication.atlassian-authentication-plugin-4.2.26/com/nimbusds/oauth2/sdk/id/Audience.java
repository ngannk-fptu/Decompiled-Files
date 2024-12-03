/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.id;

import com.nimbusds.oauth2.sdk.id.Identifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.jcip.annotations.Immutable;

@Immutable
public final class Audience
extends Identifier {
    public Audience(String value) {
        super(value);
    }

    public Audience(URI value) {
        super(value.toString());
    }

    public Audience(Identifier value) {
        super(value.getValue());
    }

    public List<Audience> toSingleAudienceList() {
        ArrayList<Audience> audienceList = new ArrayList<Audience>(1);
        audienceList.add(this);
        return audienceList;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Audience && this.toString().equals(object.toString());
    }

    public static List<String> toStringList(Audience audience) {
        if (audience == null) {
            return null;
        }
        return Collections.singletonList(audience.getValue());
    }

    public static List<String> toStringList(List<Audience> audienceList) {
        if (audienceList == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<String>(audienceList.size());
        for (Audience aud : audienceList) {
            list.add(aud.getValue());
        }
        return list;
    }

    public static List<Audience> create(List<String> strings) {
        if (strings == null) {
            return null;
        }
        ArrayList<Audience> audienceList = new ArrayList<Audience>(strings.size());
        for (String s : strings) {
            audienceList.add(new Audience(s));
        }
        return audienceList;
    }

    public static List<Audience> create(String ... strings) {
        if (strings == null) {
            return null;
        }
        return Audience.create(Arrays.asList(strings));
    }

    public static boolean matchesAny(Collection<Audience> c1, Collection<Audience> c2) {
        if (c1 == null || c2 == null) {
            return false;
        }
        for (Audience aud : c1) {
            if (!c2.contains(aud)) continue;
            return true;
        }
        return false;
    }
}

