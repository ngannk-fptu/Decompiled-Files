/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package net.fortuna.ical4j.vcard;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;
import net.fortuna.ical4j.vcard.property.AccessibilityInfo;
import net.fortuna.ical4j.vcard.property.Accessible;
import net.fortuna.ical4j.vcard.property.Address;
import net.fortuna.ical4j.vcard.property.AutoSchedule;
import net.fortuna.ical4j.vcard.property.BDay;
import net.fortuna.ical4j.vcard.property.Birth;
import net.fortuna.ical4j.vcard.property.BookingInfo;
import net.fortuna.ical4j.vcard.property.BookingRestricted;
import net.fortuna.ical4j.vcard.property.BookingWindowEnd;
import net.fortuna.ical4j.vcard.property.BookingWindowStart;
import net.fortuna.ical4j.vcard.property.CalAdrUri;
import net.fortuna.ical4j.vcard.property.CalUri;
import net.fortuna.ical4j.vcard.property.Capacity;
import net.fortuna.ical4j.vcard.property.Categories;
import net.fortuna.ical4j.vcard.property.Clazz;
import net.fortuna.ical4j.vcard.property.ClientPidMap;
import net.fortuna.ical4j.vcard.property.CostInfo;
import net.fortuna.ical4j.vcard.property.DDay;
import net.fortuna.ical4j.vcard.property.Death;
import net.fortuna.ical4j.vcard.property.Email;
import net.fortuna.ical4j.vcard.property.FbUrl;
import net.fortuna.ical4j.vcard.property.Fn;
import net.fortuna.ical4j.vcard.property.Gender;
import net.fortuna.ical4j.vcard.property.Geo;
import net.fortuna.ical4j.vcard.property.Impp;
import net.fortuna.ical4j.vcard.property.Inventory;
import net.fortuna.ical4j.vcard.property.Key;
import net.fortuna.ical4j.vcard.property.Kind;
import net.fortuna.ical4j.vcard.property.Label;
import net.fortuna.ical4j.vcard.property.Lang;
import net.fortuna.ical4j.vcard.property.LocationType;
import net.fortuna.ical4j.vcard.property.Logo;
import net.fortuna.ical4j.vcard.property.MaxInstances;
import net.fortuna.ical4j.vcard.property.Member;
import net.fortuna.ical4j.vcard.property.Multibook;
import net.fortuna.ical4j.vcard.property.N;
import net.fortuna.ical4j.vcard.property.Name;
import net.fortuna.ical4j.vcard.property.Nickname;
import net.fortuna.ical4j.vcard.property.NoCost;
import net.fortuna.ical4j.vcard.property.Note;
import net.fortuna.ical4j.vcard.property.Org;
import net.fortuna.ical4j.vcard.property.Photo;
import net.fortuna.ical4j.vcard.property.ProdId;
import net.fortuna.ical4j.vcard.property.Related;
import net.fortuna.ical4j.vcard.property.Restricted;
import net.fortuna.ical4j.vcard.property.RestrictedAccessInfo;
import net.fortuna.ical4j.vcard.property.Revision;
import net.fortuna.ical4j.vcard.property.Role;
import net.fortuna.ical4j.vcard.property.SortString;
import net.fortuna.ical4j.vcard.property.Sound;
import net.fortuna.ical4j.vcard.property.Source;
import net.fortuna.ical4j.vcard.property.Telephone;
import net.fortuna.ical4j.vcard.property.Title;
import net.fortuna.ical4j.vcard.property.Tz;
import net.fortuna.ical4j.vcard.property.Uid;
import net.fortuna.ical4j.vcard.property.Url;
import net.fortuna.ical4j.vcard.property.Version;
import net.fortuna.ical4j.vcard.property.XML;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PropertyFactoryRegistry {
    private static final Log LOG = LogFactory.getLog(PropertyFactoryRegistry.class);
    private final Map<Property.Id, PropertyFactory<? extends Property>> defaultFactories = new HashMap<Property.Id, PropertyFactory<? extends Property>>();
    private final Map<String, PropertyFactory<? extends Property>> extendedFactories;

    public PropertyFactoryRegistry() {
        this.defaultFactories.put(Property.Id.VERSION, Version.FACTORY);
        this.defaultFactories.put(Property.Id.FN, Fn.FACTORY);
        this.defaultFactories.put(Property.Id.N, N.FACTORY);
        this.defaultFactories.put(Property.Id.BDAY, BDay.FACTORY);
        this.defaultFactories.put(Property.Id.GENDER, Gender.FACTORY);
        this.defaultFactories.put(Property.Id.ORG, Org.FACTORY);
        this.defaultFactories.put(Property.Id.ADR, Address.FACTORY);
        this.defaultFactories.put(Property.Id.TEL, Telephone.FACTORY);
        this.defaultFactories.put(Property.Id.EMAIL, Email.FACTORY);
        this.defaultFactories.put(Property.Id.GEO, Geo.FACTORY);
        this.defaultFactories.put(Property.Id.CLASS, Clazz.FACTORY);
        this.defaultFactories.put(Property.Id.KEY, Key.FACTORY);
        this.defaultFactories.put(Property.Id.BIRTH, Birth.FACTORY);
        this.defaultFactories.put(Property.Id.CALADRURI, CalAdrUri.FACTORY);
        this.defaultFactories.put(Property.Id.CALURI, CalUri.FACTORY);
        this.defaultFactories.put(Property.Id.CATEGORIES, Categories.FACTORY);
        this.defaultFactories.put(Property.Id.CLIENTPIDMAP, ClientPidMap.FACTORY);
        this.defaultFactories.put(Property.Id.DDAY, DDay.FACTORY);
        this.defaultFactories.put(Property.Id.DEATH, Death.FACTORY);
        this.defaultFactories.put(Property.Id.FBURL, FbUrl.FACTORY);
        this.defaultFactories.put(Property.Id.IMPP, Impp.FACTORY);
        this.defaultFactories.put(Property.Id.KIND, Kind.FACTORY);
        this.defaultFactories.put(Property.Id.LABEL, Label.FACTORY);
        this.defaultFactories.put(Property.Id.LANG, Lang.FACTORY);
        this.defaultFactories.put(Property.Id.LOGO, Logo.FACTORY);
        this.defaultFactories.put(Property.Id.MEMBER, Member.FACTORY);
        this.defaultFactories.put(Property.Id.NAME, Name.FACTORY);
        this.defaultFactories.put(Property.Id.NICKNAME, Nickname.FACTORY);
        this.defaultFactories.put(Property.Id.NOTE, Note.FACTORY);
        this.defaultFactories.put(Property.Id.PHOTO, Photo.FACTORY);
        this.defaultFactories.put(Property.Id.PRODID, ProdId.FACTORY);
        this.defaultFactories.put(Property.Id.RELATED, Related.FACTORY);
        this.defaultFactories.put(Property.Id.REV, Revision.FACTORY);
        this.defaultFactories.put(Property.Id.ROLE, Role.FACTORY);
        this.defaultFactories.put(Property.Id.SORT_STRING, SortString.FACTORY);
        this.defaultFactories.put(Property.Id.SOUND, Sound.FACTORY);
        this.defaultFactories.put(Property.Id.SOURCE, Source.FACTORY);
        this.defaultFactories.put(Property.Id.TITLE, Title.FACTORY);
        this.defaultFactories.put(Property.Id.TZ, Tz.FACTORY);
        this.defaultFactories.put(Property.Id.UID, Uid.FACTORY);
        this.defaultFactories.put(Property.Id.URL, Url.FACTORY);
        this.defaultFactories.put(Property.Id.XML, XML.FACTORY);
        this.defaultFactories.put(Property.Id.AUTOSCHEDULE, AutoSchedule.FACTORY);
        this.defaultFactories.put(Property.Id.BOOKINGINFO, BookingInfo.FACTORY);
        this.defaultFactories.put(Property.Id.BOOKINGRESTRICTED, BookingRestricted.FACTORY);
        this.defaultFactories.put(Property.Id.BOOKINGWINDOWEND, BookingWindowEnd.FACTORY);
        this.defaultFactories.put(Property.Id.BOOKINGWINDOWSTART, BookingWindowStart.FACTORY);
        this.defaultFactories.put(Property.Id.MAXINSTANCES, MaxInstances.FACTORY);
        this.defaultFactories.put(Property.Id.MULTIBOOK, Multibook.FACTORY);
        this.defaultFactories.put(Property.Id.ACCESSIBILITYINFO, AccessibilityInfo.FACTORY);
        this.defaultFactories.put(Property.Id.ACCESSIBLE, Accessible.FACTORY);
        this.defaultFactories.put(Property.Id.CAPACITY, Capacity.FACTORY);
        this.defaultFactories.put(Property.Id.COSTINFO, CostInfo.FACTORY);
        this.defaultFactories.put(Property.Id.INVENTORY, Inventory.FACTORY);
        this.defaultFactories.put(Property.Id.LOCATIONTYPE, LocationType.FACTORY);
        this.defaultFactories.put(Property.Id.NOCOST, NoCost.FACTORY);
        this.defaultFactories.put(Property.Id.RESTRICTED, Restricted.FACTORY);
        this.defaultFactories.put(Property.Id.RESTRICTEDACCESSINFO, RestrictedAccessInfo.FACTORY);
        this.extendedFactories = new ConcurrentHashMap<String, PropertyFactory<? extends Property>>();
    }

    public PropertyFactory<? extends Property> getFactory(String value) {
        Property.Id id = null;
        try {
            id = Property.Id.valueOf(value);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (id != null) {
            return this.defaultFactories.get((Object)id);
        }
        return this.extendedFactories.get(value);
    }

    public void register(String extendedName, PropertyFactory<Property> factory) {
        this.extendedFactories.put(extendedName, factory);
    }
}

