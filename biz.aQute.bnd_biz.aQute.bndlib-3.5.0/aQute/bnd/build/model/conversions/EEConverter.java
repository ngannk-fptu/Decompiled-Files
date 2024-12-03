/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.EE;
import aQute.bnd.build.model.conversions.Converter;

public class EEConverter
implements Converter<EE, String> {
    @Override
    public EE convert(String input) throws IllegalArgumentException {
        if (input == null) {
            return null;
        }
        return EE.parse(input);
    }

    @Override
    public EE error(String msg) {
        return EE.UNKNOWN;
    }
}

