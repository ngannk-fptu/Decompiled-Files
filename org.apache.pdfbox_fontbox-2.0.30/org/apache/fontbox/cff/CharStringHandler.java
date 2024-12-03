/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cff;

import java.util.ArrayList;
import java.util.List;
import org.apache.fontbox.cff.CharStringCommand;

public abstract class CharStringHandler {
    public List<Number> handleSequence(List<Object> sequence) {
        ArrayList<Number> numbers = new ArrayList<Number>();
        for (Object obj : sequence) {
            if (obj instanceof CharStringCommand) {
                List<Number> results = this.handleCommand(numbers, (CharStringCommand)obj);
                numbers.clear();
                if (results == null) continue;
                numbers.addAll(results);
                continue;
            }
            numbers.add((Number)obj);
        }
        return numbers;
    }

    public abstract List<Number> handleCommand(List<Number> var1, CharStringCommand var2);
}

