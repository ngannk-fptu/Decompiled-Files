/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.calendar.diff;

import ietf.params.xml.ns.icalendar_2.ActionPropType;
import ietf.params.xml.ns.icalendar_2.AttachPropType;
import ietf.params.xml.ns.icalendar_2.CalAddressListParamType;
import ietf.params.xml.ns.icalendar_2.CalAddressParamType;
import ietf.params.xml.ns.icalendar_2.CalAddressPropertyType;
import ietf.params.xml.ns.icalendar_2.CalscalePropType;
import ietf.params.xml.ns.icalendar_2.CategoriesPropType;
import ietf.params.xml.ns.icalendar_2.CutypeParamType;
import ietf.params.xml.ns.icalendar_2.DateDatetimePropertyType;
import ietf.params.xml.ns.icalendar_2.DatetimePropertyType;
import ietf.params.xml.ns.icalendar_2.DurationPropType;
import ietf.params.xml.ns.icalendar_2.EncodingParamType;
import ietf.params.xml.ns.icalendar_2.FbtypeParamType;
import ietf.params.xml.ns.icalendar_2.FreebusyPropType;
import ietf.params.xml.ns.icalendar_2.GeoPropType;
import ietf.params.xml.ns.icalendar_2.IntegerPropertyType;
import ietf.params.xml.ns.icalendar_2.PartstatParamType;
import ietf.params.xml.ns.icalendar_2.PeriodType;
import ietf.params.xml.ns.icalendar_2.RangeParamType;
import ietf.params.xml.ns.icalendar_2.RecurPropertyType;
import ietf.params.xml.ns.icalendar_2.RecurType;
import ietf.params.xml.ns.icalendar_2.RelatedParamType;
import ietf.params.xml.ns.icalendar_2.ReltypeParamType;
import ietf.params.xml.ns.icalendar_2.RequestStatusPropType;
import ietf.params.xml.ns.icalendar_2.RoleParamType;
import ietf.params.xml.ns.icalendar_2.RsvpParamType;
import ietf.params.xml.ns.icalendar_2.ScheduleAgentParamType;
import ietf.params.xml.ns.icalendar_2.ScheduleForceSendParamType;
import ietf.params.xml.ns.icalendar_2.StatusPropType;
import ietf.params.xml.ns.icalendar_2.TextListPropertyType;
import ietf.params.xml.ns.icalendar_2.TextParameterType;
import ietf.params.xml.ns.icalendar_2.TextPropertyType;
import ietf.params.xml.ns.icalendar_2.TranspPropType;
import ietf.params.xml.ns.icalendar_2.TriggerPropType;
import ietf.params.xml.ns.icalendar_2.UriParameterType;
import ietf.params.xml.ns.icalendar_2.UriPropertyType;
import ietf.params.xml.ns.icalendar_2.UtcDatetimePropertyType;
import ietf.params.xml.ns.icalendar_2.UtcOffsetPropertyType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.calendar.diff.ValueComparator;
import org.bedework.util.xml.tagdefs.XcalTags;

public class ValueMatcher {
    private static ValueMatcherRegistry registry = new ValueMatcherRegistry();
    private Map<Class, ValueConverter> instanceConverters;

    public ValueComparator getComparator(Object val) {
        return this.getConverter(val).convert(val);
    }

    public Object getElementAndValue(Object val) {
        return this.getConverter(val).getElementAndValue(val);
    }

    public List getNormalized(Object val) {
        return this.getConverter(val).getNormalized(val);
    }

    public static void registerGlobalConverter(Class cl, ValueConverter vc) {
        registry.registerConverter(cl, vc);
    }

    public void registerInstanceConverter(Class cl, ValueConverter vc) {
        if (this.instanceConverters != null) {
            this.instanceConverters = new HashMap<Class, ValueConverter>();
        }
        this.instanceConverters.put(cl, vc);
    }

    private ValueConverter getConverter(Object o) {
        ValueConverter vc;
        Class<?> cl = o.getClass();
        if (this.instanceConverters != null && (vc = ValueMatcherRegistry.findConverter(cl, this.instanceConverters)) != null) {
            return vc;
        }
        return ValueMatcher.registry.getConverter(o);
    }

    static {
        ValueMatcher.registry.registerStandardConverter(ActionPropType.class, new ActionPropConverter());
        ValueMatcher.registry.registerStandardConverter(FreebusyPropType.class, new FreebusyPropConverter());
        ValueMatcher.registry.registerStandardConverter(RequestStatusPropType.class, new RequestStatusPropConverter());
        ValueMatcher.registry.registerStandardConverter(GeoPropType.class, new GeoPropConverter());
        ValueMatcher.registry.registerStandardConverter(StatusPropType.class, new StatusPropConverter());
        ValueMatcher.registry.registerStandardConverter(TranspPropType.class, new TranspPropConverter());
        ValueMatcher.registry.registerStandardConverter(CalscalePropType.class, new CalscalePropConverter());
        ValueMatcher.registry.registerStandardConverter(TriggerPropType.class, new TriggerPropConverter());
        ValueMatcher.registry.registerStandardConverter(DurationPropType.class, new DurationPropConverter());
        ValueMatcher.registry.registerStandardConverter(AttachPropType.class, new AttachPropConverter());
        ValueMatcher.registry.registerStandardConverter(DateDatetimePropertyType.class, new DateDatetimePropConverter());
        ValueMatcher.registry.registerStandardConverter(DatetimePropertyType.class, new DatetimePropConverter());
        ValueMatcher.registry.registerStandardConverter(UtcDatetimePropertyType.class, new UtcDatetimePropConverter());
        ValueMatcher.registry.registerStandardConverter(CalAddressPropertyType.class, new CalAddressPropConverter());
        ValueMatcher.registry.registerStandardConverter(UtcOffsetPropertyType.class, new UtcOffsetPropConverter());
        ValueMatcher.registry.registerStandardConverter(TextListPropertyType.class, new TextListPropConverter());
        ValueMatcher.registry.registerStandardConverter(TextPropertyType.class, new TextPropConverter());
        ValueMatcher.registry.registerStandardConverter(RecurPropertyType.class, new RecurPropConverter());
        ValueMatcher.registry.registerStandardConverter(IntegerPropertyType.class, new IntegerPropConverter());
        ValueMatcher.registry.registerStandardConverter(UriPropertyType.class, new UriPropConverter());
        ValueMatcher.registry.registerStandardConverter(CalAddressParamType.class, new CalAddressParamConverter());
        ValueMatcher.registry.registerStandardConverter(CalAddressListParamType.class, new CalAddressListParamConverter());
        ValueMatcher.registry.registerStandardConverter(TextParameterType.class, new TextParamConverter());
        ValueMatcher.registry.registerStandardConverter(UriParameterType.class, new UriParamConverter());
        ValueMatcher.registry.registerStandardConverter(CutypeParamType.class, new CutypeParamConverter());
        ValueMatcher.registry.registerStandardConverter(EncodingParamType.class, new EncodingParamConverter());
        ValueMatcher.registry.registerStandardConverter(FbtypeParamType.class, new FbtypeParamConverter());
        ValueMatcher.registry.registerStandardConverter(PartstatParamType.class, new PartstatParamConverter());
        ValueMatcher.registry.registerStandardConverter(RangeParamType.class, new RangeParamConverter());
        ValueMatcher.registry.registerStandardConverter(RelatedParamType.class, new RelatedParamConverter());
        ValueMatcher.registry.registerStandardConverter(ReltypeParamType.class, new ReltypeParamConverter());
        ValueMatcher.registry.registerStandardConverter(RoleParamType.class, new RoleParamConverter());
        ValueMatcher.registry.registerStandardConverter(RsvpParamType.class, new RsvpParamConverter());
        ValueMatcher.registry.registerStandardConverter(ScheduleAgentParamType.class, new ScheduleAgentParamConverter());
        ValueMatcher.registry.registerStandardConverter(ScheduleForceSendParamType.class, new ScheduleForceSendParamConverter());
    }

    private static class ScheduleForceSendParamConverter
    extends DefaultConverter<ScheduleForceSendParamType> {
        private ScheduleForceSendParamConverter() {
        }

        @Override
        public ValueComparator convert(ScheduleForceSendParamType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText());
            return vc;
        }

        @Override
        public ScheduleForceSendParamType getElementAndValue(ScheduleForceSendParamType val) {
            try {
                ScheduleForceSendParamType param = (ScheduleForceSendParamType)val.getClass().newInstance();
                param.setText(val.getText());
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class ScheduleAgentParamConverter
    extends DefaultConverter<ScheduleAgentParamType> {
        private ScheduleAgentParamConverter() {
        }

        @Override
        public ValueComparator convert(ScheduleAgentParamType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText());
            return vc;
        }

        @Override
        public ScheduleAgentParamType getElementAndValue(ScheduleAgentParamType val) {
            try {
                ScheduleAgentParamType param = (ScheduleAgentParamType)val.getClass().newInstance();
                param.setText(val.getText());
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class RsvpParamConverter
    extends DefaultConverter<RsvpParamType> {
        private RsvpParamConverter() {
        }

        @Override
        public ValueComparator convert(RsvpParamType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.booleanVal, String.valueOf(val.isBoolean()));
            return vc;
        }

        @Override
        public RsvpParamType getElementAndValue(RsvpParamType val) {
            try {
                RsvpParamType param = (RsvpParamType)val.getClass().newInstance();
                param.setBoolean(val.isBoolean());
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class RoleParamConverter
    extends DefaultConverter<RoleParamType> {
        private RoleParamConverter() {
        }

        @Override
        public ValueComparator convert(RoleParamType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText());
            return vc;
        }

        @Override
        public RoleParamType getElementAndValue(RoleParamType val) {
            try {
                RoleParamType param = (RoleParamType)val.getClass().newInstance();
                param.setText(val.getText());
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class ReltypeParamConverter
    extends DefaultConverter<ReltypeParamType> {
        private ReltypeParamConverter() {
        }

        @Override
        public ValueComparator convert(ReltypeParamType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText().toString());
            return vc;
        }

        @Override
        public ReltypeParamType getElementAndValue(ReltypeParamType val) {
            try {
                ReltypeParamType param = (ReltypeParamType)val.getClass().newInstance();
                param.setText(val.getText());
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class RelatedParamConverter
    extends DefaultConverter<RelatedParamType> {
        private RelatedParamConverter() {
        }

        @Override
        public ValueComparator convert(RelatedParamType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText());
            return vc;
        }

        @Override
        public RelatedParamType getElementAndValue(RelatedParamType val) {
            try {
                RelatedParamType param = (RelatedParamType)val.getClass().newInstance();
                param.setText(val.getText());
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class RangeParamConverter
    extends DefaultConverter<RangeParamType> {
        private RangeParamConverter() {
        }

        @Override
        public ValueComparator convert(RangeParamType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText().toString());
            return vc;
        }

        @Override
        public RangeParamType getElementAndValue(RangeParamType val) {
            try {
                RangeParamType param = (RangeParamType)val.getClass().newInstance();
                param.setText(val.getText());
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class PartstatParamConverter
    extends DefaultConverter<PartstatParamType> {
        private PartstatParamConverter() {
        }

        @Override
        public ValueComparator convert(PartstatParamType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText());
            return vc;
        }

        @Override
        public PartstatParamType getElementAndValue(PartstatParamType val) {
            try {
                PartstatParamType param = (PartstatParamType)val.getClass().newInstance();
                param.setText(val.getText());
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class FbtypeParamConverter
    extends DefaultConverter<FbtypeParamType> {
        private FbtypeParamConverter() {
        }

        @Override
        public ValueComparator convert(FbtypeParamType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText());
            return vc;
        }

        @Override
        public FbtypeParamType getElementAndValue(FbtypeParamType val) {
            try {
                FbtypeParamType param = (FbtypeParamType)val.getClass().newInstance();
                param.setText(val.getText());
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class EncodingParamConverter
    extends DefaultConverter<EncodingParamType> {
        private EncodingParamConverter() {
        }

        @Override
        public ValueComparator convert(EncodingParamType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText());
            return vc;
        }

        @Override
        public EncodingParamType getElementAndValue(EncodingParamType val) {
            try {
                EncodingParamType param = (EncodingParamType)val.getClass().newInstance();
                param.setText(val.getText());
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class CutypeParamConverter
    extends DefaultConverter<CutypeParamType> {
        private CutypeParamConverter() {
        }

        @Override
        public ValueComparator convert(CutypeParamType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText());
            return vc;
        }

        @Override
        public CutypeParamType getElementAndValue(CutypeParamType val) {
            try {
                CutypeParamType param = (CutypeParamType)val.getClass().newInstance();
                param.setText(val.getText());
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class UriParamConverter
    extends DefaultConverter<UriParameterType> {
        private UriParamConverter() {
        }

        @Override
        public ValueComparator convert(UriParameterType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.uriVal, val.getUri());
            return vc;
        }

        @Override
        public UriParameterType getElementAndValue(UriParameterType val) {
            try {
                UriParameterType param = (UriParameterType)val.getClass().newInstance();
                param.setUri(val.getUri());
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class TextParamConverter
    extends DefaultConverter<TextParameterType> {
        private TextParamConverter() {
        }

        @Override
        public ValueComparator convert(TextParameterType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText());
            return vc;
        }

        @Override
        public TextParameterType getElementAndValue(TextParameterType val) {
            try {
                TextParameterType param = (TextParameterType)val.getClass().newInstance();
                param.setText(val.getText());
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class CalAddressListParamConverter
    extends DefaultConverter<CalAddressListParamType> {
        private CalAddressListParamConverter() {
        }

        @Override
        public ValueComparator convert(CalAddressListParamType val) {
            List<String> ss = val.getCalAddress();
            ValueComparator vc = new ValueComparator();
            for (String s : ss) {
                vc.addValue(XcalTags.calAddressVal, s);
            }
            return vc;
        }

        @Override
        public CalAddressListParamType getElementAndValue(CalAddressListParamType val) {
            try {
                CalAddressListParamType param = (CalAddressListParamType)val.getClass().newInstance();
                List<String> ss = val.getCalAddress();
                for (String s : ss) {
                    param.getCalAddress().add(s);
                }
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class CalAddressParamConverter
    extends DefaultConverter<CalAddressParamType> {
        private CalAddressParamConverter() {
        }

        @Override
        public ValueComparator convert(CalAddressParamType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.calAddressVal, val.getCalAddress());
            return vc;
        }

        @Override
        public CalAddressParamType getElementAndValue(CalAddressParamType val) {
            try {
                CalAddressParamType param = (CalAddressParamType)val.getClass().newInstance();
                param.setCalAddress(val.getCalAddress());
                return param;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class UriPropConverter
    extends DefaultConverter<UriPropertyType> {
        private UriPropConverter() {
        }

        @Override
        public ValueComparator convert(UriPropertyType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.uriVal, val.getUri());
            return vc;
        }

        @Override
        public UriPropertyType getElementAndValue(UriPropertyType val) {
            try {
                UriPropertyType prop = (UriPropertyType)val.getClass().newInstance();
                prop.setUri(val.getUri());
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class IntegerPropConverter
    extends DefaultConverter<IntegerPropertyType> {
        private IntegerPropConverter() {
        }

        @Override
        public ValueComparator convert(IntegerPropertyType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.integerVal, String.valueOf(val.getInteger()));
            return vc;
        }

        @Override
        public IntegerPropertyType getElementAndValue(IntegerPropertyType val) {
            try {
                IntegerPropertyType prop = (IntegerPropertyType)val.getClass().newInstance();
                prop.setInteger(val.getInteger());
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class RecurPropConverter
    extends DefaultConverter<RecurPropertyType> {
        private RecurPropConverter() {
        }

        @Override
        public ValueComparator convert(RecurPropertyType val) {
            RecurType r = val.getRecur();
            ValueComparator vc = new ValueComparator();
            this.append(vc, XcalTags.freq, r.getFreq().toString());
            this.append(vc, XcalTags.count, r.getCount());
            this.append(vc, XcalTags.until, r.getUntil());
            this.append(vc, XcalTags.interval, r.getInterval());
            this.append(vc, XcalTags.bysecond, r.getBysecond());
            this.append(vc, XcalTags.byminute, r.getByminute());
            this.append(vc, XcalTags.byhour, r.getByhour());
            this.append(vc, XcalTags.byday, r.getByday());
            this.append(vc, XcalTags.byyearday, r.getByyearday());
            this.append(vc, XcalTags.bymonthday, r.getBymonthday());
            this.append(vc, XcalTags.byweekno, r.getByweekno());
            this.append(vc, XcalTags.bymonth, r.getBymonth());
            this.append(vc, XcalTags.bysetpos, r.getBysetpos());
            if (r.getWkst() != null) {
                this.append(vc, XcalTags.wkst, r.getWkst().toString());
            }
            return vc;
        }

        @Override
        public RecurPropertyType getElementAndValue(RecurPropertyType val) {
            try {
                RecurPropertyType prop = (RecurPropertyType)val.getClass().newInstance();
                prop.setRecur(val.getRecur());
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        private void append(ValueComparator vc, QName nm, List val) {
            if (val == null) {
                return;
            }
            for (Object o : val) {
                this.append(vc, nm, o);
            }
        }

        private void append(ValueComparator vc, QName nm, Object val) {
            if (val == null) {
                return;
            }
            vc.addValue(nm, String.valueOf(val));
        }
    }

    private static class TextPropConverter
    extends DefaultConverter<TextPropertyType> {
        private TextPropConverter() {
        }

        @Override
        public ValueComparator convert(TextPropertyType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText());
            return vc;
        }

        @Override
        public TextPropertyType getElementAndValue(TextPropertyType val) {
            try {
                TextPropertyType prop = (TextPropertyType)val.getClass().newInstance();
                prop.setText(val.getText());
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class TextListPropConverter
    extends DefaultConverter<TextListPropertyType> {
        private TextListPropConverter() {
        }

        @Override
        public ValueComparator convert(TextListPropertyType val) {
            List<String> ss = val.getText();
            ValueComparator vc = new ValueComparator();
            for (String s : ss) {
                vc.addValue(XcalTags.textVal, s);
            }
            return vc;
        }

        @Override
        public TextListPropertyType getElementAndValue(TextListPropertyType val) {
            try {
                TextListPropertyType prop = (TextListPropertyType)val.getClass().newInstance();
                List<String> ss = val.getText();
                for (String s : ss) {
                    prop.getText().add(s);
                }
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        @Override
        public List<TextListPropertyType> getNormalized(TextListPropertyType val) {
            if (!(val instanceof CategoriesPropType)) {
                return super.getNormalized(val);
            }
            try {
                ArrayList<TextListPropertyType> res = new ArrayList<TextListPropertyType>();
                for (String s : val.getText()) {
                    TextListPropertyType prop = (TextListPropertyType)val.getClass().newInstance();
                    prop.getText().add(s);
                    res.add(prop);
                    prop.setParameters(val.getParameters());
                }
                return res;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class UtcOffsetPropConverter
    extends DefaultConverter<UtcOffsetPropertyType> {
        private UtcOffsetPropConverter() {
        }

        @Override
        public ValueComparator convert(UtcOffsetPropertyType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.utcOffsetVal, val.getUtcOffset());
            return vc;
        }

        @Override
        public UtcOffsetPropertyType getElementAndValue(UtcOffsetPropertyType val) {
            try {
                UtcOffsetPropertyType prop = (UtcOffsetPropertyType)val.getClass().newInstance();
                prop.setUtcOffset(val.getUtcOffset());
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class CalAddressPropConverter
    extends DefaultConverter<CalAddressPropertyType> {
        private CalAddressPropConverter() {
        }

        @Override
        public ValueComparator convert(CalAddressPropertyType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.calAddressVal, val.getCalAddress());
            return vc;
        }

        @Override
        public CalAddressPropertyType getElementAndValue(CalAddressPropertyType val) {
            try {
                CalAddressPropertyType prop = (CalAddressPropertyType)val.getClass().newInstance();
                prop.setCalAddress(val.getCalAddress());
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class UtcDatetimePropConverter
    extends DefaultConverter<UtcDatetimePropertyType> {
        private UtcDatetimePropConverter() {
        }

        @Override
        public ValueComparator convert(UtcDatetimePropertyType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.utcDateTimeVal, XcalUtil.getIcalFormatDateTime(val.getUtcDateTime().toString()));
            return vc;
        }

        @Override
        public UtcDatetimePropertyType getElementAndValue(UtcDatetimePropertyType val) {
            try {
                UtcDatetimePropertyType prop = (UtcDatetimePropertyType)val.getClass().newInstance();
                prop.setUtcDateTime(val.getUtcDateTime());
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class DatetimePropConverter
    extends DefaultConverter<DatetimePropertyType> {
        private DatetimePropConverter() {
        }

        @Override
        public ValueComparator convert(DatetimePropertyType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.dateTimeVal, XcalUtil.getIcalFormatDateTime(val.getDateTime().toString()));
            return vc;
        }

        @Override
        public DatetimePropertyType getElementAndValue(DatetimePropertyType val) {
            try {
                DatetimePropertyType prop = (DatetimePropertyType)val.getClass().newInstance();
                prop.setDateTime(val.getDateTime());
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class DateDatetimePropConverter
    extends DefaultConverter<DateDatetimePropertyType> {
        private DateDatetimePropConverter() {
        }

        @Override
        public ValueComparator convert(DateDatetimePropertyType val) {
            XcalUtil.DtTzid dtTzid = XcalUtil.getDtTzid(val);
            ValueComparator vc = new ValueComparator();
            if (dtTzid.dateOnly) {
                vc.addValue(XcalTags.dateVal, dtTzid.dt);
            } else {
                vc.addValue(XcalTags.dateTimeVal, dtTzid.dt);
            }
            return vc;
        }

        @Override
        public DateDatetimePropertyType getElementAndValue(DateDatetimePropertyType val) {
            try {
                DateDatetimePropertyType prop = (DateDatetimePropertyType)val.getClass().newInstance();
                DateDatetimePropertyType dt = val;
                if (dt.getDate() != null) {
                    prop.setDate(dt.getDate());
                } else {
                    prop.setDateTime(dt.getDateTime());
                }
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class AttachPropConverter
    extends DefaultConverter<AttachPropType> {
        private AttachPropConverter() {
        }

        @Override
        public ValueComparator convert(AttachPropType val) {
            AttachPropType ap = val;
            ValueComparator vc = new ValueComparator();
            if (ap.getBinary() != null) {
                vc.addValue(XcalTags.binaryVal, ap.getBinary());
            } else {
                vc.addValue(XcalTags.uriVal, ap.getUri());
            }
            return vc;
        }

        @Override
        public AttachPropType getElementAndValue(AttachPropType val) {
            try {
                AttachPropType prop = (AttachPropType)val.getClass().newInstance();
                AttachPropType ap = val;
                if (ap.getBinary() != null) {
                    prop.setBinary(ap.getBinary());
                } else {
                    prop.setUri(ap.getUri());
                }
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class DurationPropConverter
    extends DefaultConverter<DurationPropType> {
        private DurationPropConverter() {
        }

        @Override
        public ValueComparator convert(DurationPropType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.durationVal, val.getDuration().toString());
            return vc;
        }

        @Override
        public DurationPropType getElementAndValue(DurationPropType val) {
            try {
                DurationPropType prop = (DurationPropType)val.getClass().newInstance();
                prop.setDuration(val.getDuration());
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class TriggerPropConverter
    extends DefaultConverter<TriggerPropType> {
        private TriggerPropConverter() {
        }

        @Override
        public ValueComparator convert(TriggerPropType val) {
            TriggerPropType tp = val;
            ValueComparator vc = new ValueComparator();
            if (tp.getDuration() != null) {
                vc.addValue(XcalTags.durationVal, tp.getDuration().toString());
            } else {
                vc.addValue(XcalTags.dateTimeVal, tp.getDateTime().toString());
            }
            return vc;
        }

        @Override
        public TriggerPropType getElementAndValue(TriggerPropType val) {
            try {
                TriggerPropType prop = (TriggerPropType)val.getClass().newInstance();
                TriggerPropType tp = val;
                if (tp.getDuration() != null) {
                    prop.setDuration(tp.getDuration());
                } else {
                    prop.setDateTime(tp.getDateTime());
                }
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class CalscalePropConverter
    extends DefaultConverter<CalscalePropType> {
        private CalscalePropConverter() {
        }

        @Override
        public ValueComparator convert(CalscalePropType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText().toString());
            return vc;
        }

        @Override
        public CalscalePropType getElementAndValue(CalscalePropType val) {
            try {
                CalscalePropType prop = (CalscalePropType)val.getClass().newInstance();
                prop.setText(val.getText());
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class TranspPropConverter
    extends DefaultConverter<TranspPropType> {
        private TranspPropConverter() {
        }

        @Override
        public ValueComparator convert(TranspPropType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText().toString());
            return vc;
        }

        @Override
        public TranspPropType getElementAndValue(TranspPropType val) {
            try {
                TranspPropType prop = (TranspPropType)val.getClass().newInstance();
                prop.setText(val.getText());
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class StatusPropConverter
    extends DefaultConverter<StatusPropType> {
        private StatusPropConverter() {
        }

        @Override
        public ValueComparator convert(StatusPropType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText().toString());
            return vc;
        }

        @Override
        public StatusPropType getElementAndValue(StatusPropType val) {
            try {
                StatusPropType prop = (StatusPropType)val.getClass().newInstance();
                prop.setText(val.getText());
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class GeoPropConverter
    extends DefaultConverter<GeoPropType> {
        private GeoPropConverter() {
        }

        @Override
        public ValueComparator convert(GeoPropType val) {
            GeoPropType gp = val;
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.latitudeVal, String.valueOf(gp.getLatitude()));
            vc.addValue(XcalTags.longitudeVal, String.valueOf(gp.getLongitude()));
            return vc;
        }

        @Override
        public GeoPropType getElementAndValue(GeoPropType val) {
            try {
                GeoPropType prop = (GeoPropType)val.getClass().newInstance();
                GeoPropType gp = val;
                prop.setLatitude(gp.getLatitude());
                prop.setLongitude(gp.getLongitude());
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class RequestStatusPropConverter
    extends DefaultConverter<RequestStatusPropType> {
        private RequestStatusPropConverter() {
        }

        @Override
        public ValueComparator convert(RequestStatusPropType val) {
            RequestStatusPropType rs = val;
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.codeVal, rs.getCode());
            if (rs.getDescription() != null) {
                vc.addValue(XcalTags.descriptionVal, rs.getDescription());
            }
            if (rs.getExtdata() != null) {
                vc.addValue(XcalTags.extdataVal, rs.getExtdata());
            }
            return vc;
        }

        @Override
        public RequestStatusPropType getElementAndValue(RequestStatusPropType val) {
            try {
                RequestStatusPropType prop = (RequestStatusPropType)val.getClass().newInstance();
                RequestStatusPropType rs = val;
                prop.setCode(rs.getCode());
                if (rs.getDescription() != null) {
                    prop.setDescription(rs.getDescription());
                }
                if (rs.getExtdata() != null) {
                    prop.setExtdata(rs.getExtdata());
                }
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class FreebusyPropConverter
    implements ValueConverter<FreebusyPropType> {
        private FreebusyPropConverter() {
        }

        @Override
        public ValueComparator convert(FreebusyPropType val) {
            List<PeriodType> ps = val.getPeriod();
            ValueComparator vc = new ValueComparator();
            for (PeriodType p : ps) {
                StringBuilder sb = new StringBuilder(p.getStart().toXMLFormat());
                sb.append("\t");
                if (p.getDuration() != null) {
                    sb.append(p.getDuration());
                } else {
                    sb.append(p.getEnd().toXMLFormat());
                }
                vc.addValue(XcalTags.periodVal, sb.toString());
            }
            return vc;
        }

        @Override
        public FreebusyPropType getElementAndValue(FreebusyPropType val) {
            try {
                FreebusyPropType prop = (FreebusyPropType)val.getClass().newInstance();
                List<PeriodType> ps = val.getPeriod();
                for (PeriodType p : ps) {
                    prop.getPeriod().add(p);
                }
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        @Override
        public List<FreebusyPropType> getNormalized(FreebusyPropType val) {
            try {
                ArrayList<FreebusyPropType> res = new ArrayList<FreebusyPropType>();
                List<PeriodType> ps = val.getPeriod();
                for (PeriodType p : ps) {
                    FreebusyPropType prop = (FreebusyPropType)val.getClass().newInstance();
                    prop.getPeriod().add(p);
                    res.add(prop);
                    prop.setParameters(val.getParameters());
                }
                return res;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static class ActionPropConverter
    extends DefaultConverter<ActionPropType> {
        private ActionPropConverter() {
        }

        @Override
        public ValueComparator convert(ActionPropType val) {
            ValueComparator vc = new ValueComparator();
            vc.addValue(XcalTags.textVal, val.getText().toString());
            return vc;
        }

        @Override
        public ActionPropType getElementAndValue(ActionPropType val) {
            try {
                ActionPropType prop = (ActionPropType)val.getClass().newInstance();
                prop.setText(val.getText());
                return prop;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static abstract class DefaultConverter<T>
    implements ValueConverter<T> {
        private DefaultConverter() {
        }

        @Override
        public List<T> getNormalized(T val) {
            ArrayList<T> res = new ArrayList<T>();
            res.add(val);
            return res;
        }
    }

    private static class ValueMatcherRegistry {
        private static Map<Class, ValueConverter> standardConverters = new HashMap<Class, ValueConverter>();
        private Map<Class, ValueConverter> nonStandardConverters;

        private ValueMatcherRegistry() {
        }

        public void registerConverter(Class cl, ValueConverter vc) {
            if (this.nonStandardConverters != null) {
                this.nonStandardConverters = new HashMap<Class, ValueConverter>();
            }
            this.nonStandardConverters.put(cl, vc);
        }

        private void registerStandardConverter(Class cl, ValueConverter vc) {
            standardConverters.put(cl, vc);
        }

        private ValueConverter getConverter(Object o) {
            ValueConverter vc;
            Class<?> cl = o.getClass();
            if (this.nonStandardConverters != null && (vc = ValueMatcherRegistry.findConverter(cl, this.nonStandardConverters)) != null) {
                return vc;
            }
            vc = ValueMatcherRegistry.findConverter(cl, standardConverters);
            if (vc == null) {
                throw new RuntimeException("ValueMatcher: No converter for class " + cl);
            }
            return vc;
        }

        static ValueConverter findConverter(Class cl, Map<Class, ValueConverter> converters) {
            for (Class lcl = cl; lcl != null; lcl = lcl.getSuperclass()) {
                ValueConverter vc = converters.get(lcl);
                if (vc == null) continue;
                return vc;
            }
            return null;
        }
    }

    public static interface ValueConverter<T> {
        public ValueComparator convert(T var1);

        public T getElementAndValue(T var1);

        public List<T> getNormalized(T var1);
    }
}

