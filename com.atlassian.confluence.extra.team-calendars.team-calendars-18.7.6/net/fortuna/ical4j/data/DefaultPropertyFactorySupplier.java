/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.property.Acknowledged;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.BusyType;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Color;
import net.fortuna.ical4j.model.property.Comment;
import net.fortuna.ical4j.model.property.Completed;
import net.fortuna.ical4j.model.property.Conference;
import net.fortuna.ical4j.model.property.Contact;
import net.fortuna.ical4j.model.property.Country;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Due;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.ExRule;
import net.fortuna.ical4j.model.property.ExtendedAddress;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.Geo;
import net.fortuna.ical4j.model.property.Image;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Locality;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.LocationType;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Name;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.PercentComplete;
import net.fortuna.ical4j.model.property.Postalcode;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.RefreshInterval;
import net.fortuna.ical4j.model.property.Region;
import net.fortuna.ical4j.model.property.RelatedTo;
import net.fortuna.ical4j.model.property.Repeat;
import net.fortuna.ical4j.model.property.RequestStatus;
import net.fortuna.ical4j.model.property.Resources;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Source;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.StreetAddress;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Tel;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.TzName;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;
import net.fortuna.ical4j.model.property.TzUrl;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.Version;

public class DefaultPropertyFactorySupplier
implements Supplier<List<PropertyFactory<? extends Property>>> {
    @Override
    public List<PropertyFactory<? extends Property>> get() {
        List<PropertyFactory> rfc5545 = Arrays.asList(new Acknowledged.Factory(), new Action.Factory(), new Attach.Factory(), new Attendee.Factory(), new BusyType.Factory(), new CalScale.Factory(), new Categories.Factory(), new Clazz.Factory(), new Comment.Factory(), new Completed.Factory(), new Contact.Factory(), new Country.Factory(), new Created.Factory(), new Description.Factory(), new DtEnd.Factory(), new DtStamp.Factory(), new DtStart.Factory(), new Due.Factory(), new Duration.Factory(), new ExDate.Factory(), new ExRule.Factory(), new ExtendedAddress.Factory(), new FreeBusy.Factory(), new Geo.Factory(), new LastModified.Factory(), new Locality.Factory(), new Location.Factory(), new LocationType.Factory(), new Method.Factory(), new Name.Factory(), new Organizer.Factory(), new PercentComplete.Factory(), new Postalcode.Factory(), new Priority.Factory(), new ProdId.Factory(), new RDate.Factory(), new RecurrenceId.Factory(), new Region.Factory(), new RelatedTo.Factory(), new Repeat.Factory(), new RequestStatus.Factory(), new Resources.Factory(), new RRule.Factory(), new Sequence.Factory(), new Status.Factory(), new StreetAddress.Factory(), new Summary.Factory(), new Tel.Factory(), new Transp.Factory(), new Trigger.Factory(), new TzId.Factory(), new TzName.Factory(), new TzOffsetFrom.Factory(), new TzOffsetTo.Factory(), new TzUrl.Factory(), new Uid.Factory(), new Url.Factory(), new Version.Factory());
        List<PropertyFactory> rfc7986 = Arrays.asList(new Color.Factory(), new Conference.Factory(), new Image.Factory(), new RefreshInterval.Factory(), new Source.Factory());
        ArrayList<PropertyFactory<? extends Property>> factories = new ArrayList<PropertyFactory<? extends Property>>(rfc5545);
        factories.addAll(rfc7986);
        return factories;
    }
}

