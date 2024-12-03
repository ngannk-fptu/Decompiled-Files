/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.model.parameter.Abbrev;
import net.fortuna.ical4j.model.parameter.AltRep;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.DelegatedFrom;
import net.fortuna.ical4j.model.parameter.DelegatedTo;
import net.fortuna.ical4j.model.parameter.Dir;
import net.fortuna.ical4j.model.parameter.Display;
import net.fortuna.ical4j.model.parameter.Email;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.model.parameter.FbType;
import net.fortuna.ical4j.model.parameter.Feature;
import net.fortuna.ical4j.model.parameter.FmtType;
import net.fortuna.ical4j.model.parameter.Label;
import net.fortuna.ical4j.model.parameter.Language;
import net.fortuna.ical4j.model.parameter.Member;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Range;
import net.fortuna.ical4j.model.parameter.RelType;
import net.fortuna.ical4j.model.parameter.Related;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.parameter.ScheduleAgent;
import net.fortuna.ical4j.model.parameter.ScheduleStatus;
import net.fortuna.ical4j.model.parameter.SentBy;
import net.fortuna.ical4j.model.parameter.Type;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.parameter.Vvenue;

public class DefaultParameterFactorySupplier
implements Supplier<List<ParameterFactory<? extends Parameter>>> {
    @Override
    public List<ParameterFactory<? extends Parameter>> get() {
        List<ParameterFactory> rfc5545 = Arrays.asList(new Abbrev.Factory(), new AltRep.Factory(), new Cn.Factory(), new CuType.Factory(), new DelegatedFrom.Factory(), new DelegatedTo.Factory(), new Dir.Factory(), new Encoding.Factory(), new FmtType.Factory(), new FbType.Factory(), new Language.Factory(), new Member.Factory(), new PartStat.Factory(), new Range.Factory(), new Related.Factory(), new RelType.Factory(), new Role.Factory(), new Rsvp.Factory(), new ScheduleAgent.Factory(), new ScheduleStatus.Factory(), new SentBy.Factory(), new Type.Factory(), new TzId.Factory(), new Value.Factory(), new Vvenue.Factory());
        List<ParameterFactory> rfc7986 = Arrays.asList(new Display.Factory(), new Email.Factory(), new Feature.Factory(), new Label.Factory());
        ArrayList<ParameterFactory<? extends Parameter>> factories = new ArrayList<ParameterFactory<? extends Parameter>>(rfc5545);
        factories.addAll(rfc7986);
        return factories;
    }
}

