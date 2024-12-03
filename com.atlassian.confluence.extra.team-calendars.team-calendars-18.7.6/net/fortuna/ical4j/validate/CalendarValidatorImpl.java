/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Color;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Image;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Name;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RefreshInterval;
import net.fortuna.ical4j.model.property.Source;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

public class CalendarValidatorImpl
implements Validator<Calendar> {
    protected final List<Class<? extends Property>> calendarProperties = new ArrayList<Class<? extends Property>>();
    private final List<ValidationRule> rules;

    public CalendarValidatorImpl(ValidationRule ... rules) {
        this.rules = Arrays.asList(rules);
        Collections.addAll(this.calendarProperties, CalScale.class, Method.class, ProdId.class, Version.class, Uid.class, LastModified.class, Url.class, RefreshInterval.class, Source.class, Color.class, Name.class, Description.class, Categories.class, Image.class);
    }

    @Override
    public void validate(Calendar target) throws ValidationException {
        for (ValidationRule rule : this.rules) {
            if (CompatibilityHints.isHintEnabled("ical4j.validation.relaxed") && rule.isRelaxedModeSupported()) continue;
            switch (rule.getType()) {
                case None: {
                    rule.getInstances().forEach(s -> PropertyValidator.assertNone(s, target.getProperties()));
                    break;
                }
                case One: {
                    rule.getInstances().forEach(s -> PropertyValidator.assertOne(s, target.getProperties()));
                    break;
                }
                case OneOrLess: {
                    rule.getInstances().forEach(s -> PropertyValidator.assertOneOrLess(s, target.getProperties()));
                    break;
                }
                case OneOrMore: {
                    rule.getInstances().forEach(s -> PropertyValidator.assertOneOrMore(s, target.getProperties()));
                }
            }
        }
        if (!CompatibilityHints.isHintEnabled("ical4j.validation.relaxed") && !Version.VERSION_2_0.equals(target.getProperty("VERSION"))) {
            throw new ValidationException("Unsupported Version: " + ((Content)target.getProperty("VERSION")).getValue());
        }
        if (target.getComponents().isEmpty()) {
            throw new ValidationException("Calendar must contain at least one component");
        }
        for (Property property : target.getProperties()) {
            boolean isCalendarProperty;
            boolean bl = isCalendarProperty = this.calendarProperties.stream().filter(calProp -> calProp.isInstance(property)) != null;
            if (property instanceof XProperty || isCalendarProperty) continue;
            throw new ValidationException("Invalid property: " + property.getName());
        }
        Method method = (Method)target.getProperty("METHOD");
        if (Method.PUBLISH.equals(method)) {
            new PublishValidator().validate(target);
        } else if (Method.REQUEST.equals(target.getProperty("METHOD"))) {
            new RequestValidator().validate(target);
        } else if (Method.REPLY.equals(target.getProperty("METHOD"))) {
            new ReplyValidator().validate(target);
        } else if (Method.ADD.equals(target.getProperty("METHOD"))) {
            new AddValidator().validate(target);
        } else if (Method.CANCEL.equals(target.getProperty("METHOD"))) {
            new CancelValidator().validate(target);
        } else if (Method.REFRESH.equals(target.getProperty("METHOD"))) {
            new RefreshValidator().validate(target);
        } else if (Method.COUNTER.equals(target.getProperty("METHOD"))) {
            new CounterValidator().validate(target);
        } else if (Method.DECLINE_COUNTER.equals(target.getProperty("METHOD"))) {
            new DeclineCounterValidator().validate(target);
        }
        if (method != null) {
            for (CalendarComponent component : target.getComponents()) {
                component.validate(method);
            }
        }
    }

    public static class DeclineCounterValidator
    implements Validator<Calendar> {
        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponent("VEVENT") != null) {
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
                ComponentValidator.assertNone("VTODO", target.getComponents());
                ComponentValidator.assertNone("VTIMEZONE", target.getComponents());
                ComponentValidator.assertNone("VALARM", target.getComponents());
            } else if (target.getComponent("VTODO") != null) {
                ComponentValidator.assertNone("VALARM", target.getComponents());
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
            }
        }
    }

    public static class CounterValidator
    implements Validator<Calendar> {
        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponent("VEVENT") != null) {
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
                ComponentValidator.assertNone("VTODO", target.getComponents());
            } else if (target.getComponent("VTODO") != null) {
                ComponentValidator.assertOneOrLess("VTIMEZONE", target.getComponents());
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
            }
        }
    }

    public static class RefreshValidator
    implements Validator<Calendar> {
        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponent("VEVENT") != null) {
                ComponentValidator.assertNone("VALARM", target.getComponents());
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
                ComponentValidator.assertNone("VTODO", target.getComponents());
            } else if (target.getComponent("VTODO") != null) {
                ComponentValidator.assertNone("VALARM", target.getComponents());
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
                ComponentValidator.assertNone("VTIMEZONE", target.getComponents());
            }
        }
    }

    public static class CancelValidator
    implements Validator<Calendar> {
        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponent("VEVENT") != null) {
                ComponentValidator.assertNone("VALARM", target.getComponents());
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
                ComponentValidator.assertNone("VTODO", target.getComponents());
            } else if (target.getComponent("VTODO") != null) {
                ComponentValidator.assertOneOrLess("VTIMEZONE", target.getComponents());
                ComponentValidator.assertNone("VALARM", target.getComponents());
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
            } else if (target.getComponent("VJOURNAL") != null) {
                ComponentValidator.assertNone("VALARM", target.getComponents());
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
            }
        }
    }

    public static class AddValidator
    implements Validator<Calendar> {
        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponent("VEVENT") != null) {
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
                ComponentValidator.assertNone("VTODO", target.getComponents());
            } else if (target.getComponent("VTODO") != null) {
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
            } else if (target.getComponent("VJOURNAL") != null) {
                ComponentValidator.assertOneOrLess("VTIMEZONE", target.getComponents());
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
            }
        }
    }

    public static class ReplyValidator
    implements Validator<Calendar> {
        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponent("VEVENT") != null) {
                ComponentValidator.assertOneOrLess("VTIMEZONE", target.getComponents());
                ComponentValidator.assertNone("VALARM", target.getComponents());
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
                ComponentValidator.assertNone("VTODO", target.getComponents());
            } else if (target.getComponent("VFREEBUSY") != null) {
                ComponentValidator.assertNone("VTODO", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
                ComponentValidator.assertNone("VTIMEZONE", target.getComponents());
                ComponentValidator.assertNone("VALARM", target.getComponents());
            } else if (target.getComponent("VTODO") != null) {
                ComponentValidator.assertOneOrLess("VTIMEZONE", target.getComponents());
                ComponentValidator.assertNone("VALARM", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
            }
        }
    }

    public static class RequestValidator
    implements Validator<Calendar> {
        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponent("VEVENT") != null) {
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
                ComponentValidator.assertNone("VTODO", target.getComponents());
            } else if (target.getComponent("VFREEBUSY") != null) {
                ComponentValidator.assertNone("VTODO", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
                ComponentValidator.assertNone("VTIMEZONE", target.getComponents());
                ComponentValidator.assertNone("VALARM", target.getComponents());
            } else if (target.getComponent("VTODO") != null) {
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
            }
        }
    }

    public static class PublishValidator
    implements Validator<Calendar> {
        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponent("VEVENT") != null) {
                ComponentValidator.assertNone("VFREEBUSY", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
                if (!CompatibilityHints.isHintEnabled("ical4j.validation.relaxed")) {
                    ComponentValidator.assertNone("VTODO", target.getComponents());
                }
            } else if (target.getComponent("VFREEBUSY") != null) {
                ComponentValidator.assertNone("VTODO", target.getComponents());
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
                ComponentValidator.assertNone("VTIMEZONE", target.getComponents());
                ComponentValidator.assertNone("VALARM", target.getComponents());
            } else if (target.getComponent("VTODO") != null) {
                ComponentValidator.assertNone("VJOURNAL", target.getComponents());
            }
        }
    }
}

