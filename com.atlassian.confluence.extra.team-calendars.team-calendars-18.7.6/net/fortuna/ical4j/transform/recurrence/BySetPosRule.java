/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.recurrence;

import java.util.Collections;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.util.Dates;

public class BySetPosRule
implements Transformer<DateList> {
    private final NumberList setPosList;

    public BySetPosRule(NumberList setPosList) {
        this.setPosList = setPosList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (this.setPosList.isEmpty()) {
            return dates;
        }
        Collections.sort(dates);
        DateList setPosDates = Dates.getDateListInstance(dates);
        int size = dates.size();
        for (Integer setPos : this.setPosList) {
            int pos = setPos;
            if (pos > 0 && pos <= size) {
                setPosDates.add(dates.get(pos - 1));
                continue;
            }
            if (pos >= 0 || pos < -size) continue;
            setPosDates.add(dates.get(size + pos));
        }
        return setPosDates;
    }
}

