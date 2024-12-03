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
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.ParameterFactory;
import net.fortuna.ical4j.vcard.parameter.Altid;
import net.fortuna.ical4j.vcard.parameter.Calscale;
import net.fortuna.ical4j.vcard.parameter.Encoding;
import net.fortuna.ical4j.vcard.parameter.Fmttype;
import net.fortuna.ical4j.vcard.parameter.Geo;
import net.fortuna.ical4j.vcard.parameter.Language;
import net.fortuna.ical4j.vcard.parameter.Pid;
import net.fortuna.ical4j.vcard.parameter.Pref;
import net.fortuna.ical4j.vcard.parameter.SortAs;
import net.fortuna.ical4j.vcard.parameter.Type;
import net.fortuna.ical4j.vcard.parameter.Tz;
import net.fortuna.ical4j.vcard.parameter.Value;
import net.fortuna.ical4j.vcard.parameter.Version;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ParameterFactoryRegistry {
    private static final Log LOG = LogFactory.getLog(ParameterFactoryRegistry.class);
    private final Map<Parameter.Id, ParameterFactory<? extends Parameter>> defaultFactories = new HashMap<Parameter.Id, ParameterFactory<? extends Parameter>>();
    private final Map<String, ParameterFactory<? extends Parameter>> extendedFactories;

    public ParameterFactoryRegistry() {
        this.defaultFactories.put(Parameter.Id.ALTID, Altid.FACTORY);
        this.defaultFactories.put(Parameter.Id.CALSCALE, Calscale.FACTORY);
        this.defaultFactories.put(Parameter.Id.ENCODING, Encoding.FACTORY);
        this.defaultFactories.put(Parameter.Id.FMTTYPE, Fmttype.FACTORY);
        this.defaultFactories.put(Parameter.Id.GEO, Geo.FACTORY);
        this.defaultFactories.put(Parameter.Id.LANGUAGE, Language.FACTORY);
        this.defaultFactories.put(Parameter.Id.PID, Pid.FACTORY);
        this.defaultFactories.put(Parameter.Id.PREF, Pref.FACTORY);
        this.defaultFactories.put(Parameter.Id.SORT_AS, SortAs.FACTORY);
        this.defaultFactories.put(Parameter.Id.TYPE, Type.FACTORY);
        this.defaultFactories.put(Parameter.Id.TZ, Tz.FACTORY);
        this.defaultFactories.put(Parameter.Id.VALUE, Value.FACTORY);
        this.defaultFactories.put(Parameter.Id.VERSION, Version.FACTORY);
        this.extendedFactories = new ConcurrentHashMap<String, ParameterFactory<? extends Parameter>>();
    }

    public ParameterFactory<? extends Parameter> getFactory(String value) {
        try {
            return this.defaultFactories.get((Object)Parameter.Id.valueOf(value));
        }
        catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Not a default parameter: [" + value + "]"));
            }
            return this.extendedFactories.get(value);
        }
    }

    public void register(String extendedName, ParameterFactory<Parameter> factory) {
        this.extendedFactories.put(extendedName, factory);
    }
}

