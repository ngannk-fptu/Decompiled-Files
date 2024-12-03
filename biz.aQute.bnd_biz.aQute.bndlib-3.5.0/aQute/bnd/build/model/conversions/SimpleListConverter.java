/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.conversions.Converter;
import aQute.bnd.build.model.conversions.NoopConverter;
import aQute.libg.qtokens.QuotedTokenizer;
import java.util.ArrayList;
import java.util.List;

public class SimpleListConverter<R>
implements Converter<List<R>, String> {
    private Converter<? extends R, ? super String> itemConverter;

    public static <R> Converter<List<R>, String> create(Converter<R, ? super String> itemConverter) {
        return new SimpleListConverter<R>(itemConverter);
    }

    public static Converter<List<String>, String> create() {
        return new SimpleListConverter<String>(new NoopConverter());
    }

    private SimpleListConverter(Converter<? extends R, ? super String> itemConverter) {
        this.itemConverter = itemConverter;
    }

    @Override
    public List<R> convert(String input) throws IllegalArgumentException {
        if (input == null) {
            return null;
        }
        ArrayList<R> result = new ArrayList<R>();
        if (input == null || "<<EMPTY>>".equalsIgnoreCase(input.trim())) {
            return result;
        }
        QuotedTokenizer qt = new QuotedTokenizer(input, ",");
        String token = qt.nextToken();
        while (token != null) {
            result.add(this.itemConverter.convert(token.trim()));
            token = qt.nextToken();
        }
        return result;
    }

    @Override
    public List<R> error(String msg) {
        ArrayList<R> l = new ArrayList<R>();
        l.add(this.itemConverter.error(msg));
        return l;
    }
}

