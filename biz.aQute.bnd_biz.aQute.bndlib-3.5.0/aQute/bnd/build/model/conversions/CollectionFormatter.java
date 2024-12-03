/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.conversions.Converter;
import aQute.bnd.build.model.conversions.DefaultFormatter;
import java.util.Collection;

public class CollectionFormatter<T>
implements Converter<String, Collection<? extends T>> {
    private final String separator;
    private final Converter<String, ? super T> itemFormatter;
    private final String emptyOutput;
    private final boolean leadingSpace;
    private final String initial;
    private final String suffix;

    public CollectionFormatter(String separator) {
        this(separator, (String)null);
    }

    public CollectionFormatter(String separator, String emptyOutput) {
        this(separator, new DefaultFormatter(), emptyOutput);
    }

    public CollectionFormatter(String separator, Converter<String, ? super T> itemFormatter) {
        this(separator, itemFormatter, null);
    }

    public CollectionFormatter(String separator, Converter<String, ? super T> itemFormatter, String emptyOutput) {
        this(separator, itemFormatter, emptyOutput, false, "\\\n\t", "");
    }

    public CollectionFormatter(String separator, Converter<String, ? super T> itemFormatter, String emptyOutput, String prefix, String suffix) {
        this(separator, itemFormatter, emptyOutput, false, prefix, suffix);
    }

    public CollectionFormatter(String separator, Converter<String, ? super T> itemFormatter, String emptyOutput, boolean leadingSpace, String prefix, String suffix) {
        this.separator = separator;
        this.itemFormatter = itemFormatter;
        this.emptyOutput = emptyOutput;
        this.leadingSpace = leadingSpace;
        this.initial = prefix;
        this.suffix = suffix;
    }

    @Override
    public String convert(Collection<? extends T> input) throws IllegalArgumentException {
        String result = null;
        if (input != null) {
            if (input.isEmpty()) {
                result = this.emptyOutput;
            } else {
                StringBuilder buffer = new StringBuilder();
                if (this.leadingSpace) {
                    buffer.append(' ');
                }
                if (input.size() == 1) {
                    T item = input.iterator().next();
                    buffer.append(this.itemFormatter.convert(item));
                } else {
                    String del = this.initial == null ? "" : this.initial;
                    for (T item : input) {
                        buffer.append(del);
                        buffer.append(this.itemFormatter.convert(item));
                        del = this.separator;
                    }
                }
                if (this.suffix != null) {
                    buffer.append(this.suffix);
                }
                result = buffer.toString();
            }
        }
        return result;
    }

    @Override
    public String error(String msg) {
        return msg;
    }
}

