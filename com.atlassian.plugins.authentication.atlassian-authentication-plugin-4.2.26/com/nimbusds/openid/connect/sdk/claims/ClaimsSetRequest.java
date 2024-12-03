/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.langtag.LangTag;
import com.nimbusds.langtag.LangTagException;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.claims.ClaimRequirement;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

@Immutable
public class ClaimsSetRequest
implements JSONAware {
    private final Collection<Entry> entries;

    public ClaimsSetRequest() {
        this(Collections.emptyList());
    }

    public ClaimsSetRequest(Collection<Entry> entries) {
        if (entries == null) {
            throw new IllegalArgumentException("The entries must not be null");
        }
        this.entries = Collections.unmodifiableCollection(entries);
    }

    public ClaimsSetRequest add(String claimName) {
        return this.add(new Entry(claimName));
    }

    public ClaimsSetRequest add(Entry entry) {
        LinkedList<Entry> updatedEntries = new LinkedList<Entry>(this.getEntries());
        updatedEntries.add(entry);
        return new ClaimsSetRequest(updatedEntries);
    }

    public Collection<Entry> getEntries() {
        return this.entries;
    }

    public Set<String> getClaimNames(boolean withLangTag) {
        HashSet<String> names = new HashSet<String>();
        for (Entry en : this.entries) {
            names.add(en.getClaimName(withLangTag));
        }
        return Collections.unmodifiableSet(names);
    }

    public Entry get(String claimName, LangTag langTag) {
        for (Entry en : this.getEntries()) {
            if (claimName.equals(en.getClaimName()) && langTag == null && en.getLangTag() == null) {
                return en;
            }
            if (!claimName.equals(en.getClaimName()) || langTag == null || !langTag.equals(en.getLangTag())) continue;
            return en;
        }
        return null;
    }

    public ClaimsSetRequest delete(String claimName, LangTag langTag) {
        LinkedList<Entry> updatedEntries = new LinkedList<Entry>();
        for (Entry en : this.getEntries()) {
            if (claimName.equals(en.getClaimName()) && langTag == null && en.getLangTag() == null || claimName.equals(en.getClaimName()) && langTag != null && langTag.equals(en.getLangTag())) continue;
            updatedEntries.add(en);
        }
        return new ClaimsSetRequest(updatedEntries);
    }

    public ClaimsSetRequest delete(String claimName) {
        LinkedList<Entry> updatedEntries = new LinkedList<Entry>();
        for (Entry en : this.getEntries()) {
            if (claimName.equals(en.getClaimName())) continue;
            updatedEntries.add(en);
        }
        return new ClaimsSetRequest(updatedEntries);
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        for (Entry entry : this.entries) {
            Map.Entry<String, JSONObject> jsonObjectEntry = entry.toJSONObjectEntry();
            o.put(jsonObjectEntry.getKey(), jsonObjectEntry.getValue());
        }
        return o;
    }

    @Override
    public String toJSONString() {
        return this.toJSONObject().toJSONString();
    }

    public String toString() {
        return this.toJSONString();
    }

    public static ClaimsSetRequest parse(JSONObject jsonObject) throws ParseException {
        ClaimsSetRequest claimsRequest = new ClaimsSetRequest();
        for (String key : jsonObject.keySet()) {
            if ("verified_claims".equals(key)) continue;
            JSONObject value = JSONObjectUtils.getJSONObject(jsonObject, key, null);
            claimsRequest = claimsRequest.add(Entry.parse(new AbstractMap.SimpleImmutableEntry<String, JSONObject>(key, value)));
        }
        return claimsRequest;
    }

    public static ClaimsSetRequest parse(String json) throws ParseException {
        return ClaimsSetRequest.parse(JSONObjectUtils.parse(json));
    }

    @Immutable
    public static class Entry {
        private final String claimName;
        private final ClaimRequirement requirement;
        private final LangTag langTag;
        private final Object value;
        private final List<?> values;
        private final String purpose;
        private final Map<String, Object> additionalInformation;

        public Entry(String claimName) {
            this(claimName, ClaimRequirement.VOLUNTARY, null, null, null, null, null);
        }

        private Entry(String claimName, ClaimRequirement requirement, LangTag langTag, Object value, List<?> values, String purpose, Map<String, Object> additionalInformation) {
            if (claimName == null) {
                throw new IllegalArgumentException("The claim name must not be null");
            }
            this.claimName = claimName;
            if (requirement == null) {
                throw new IllegalArgumentException("The claim requirement must not be null");
            }
            this.requirement = requirement;
            this.langTag = langTag;
            if (value != null && values == null) {
                this.value = value;
                this.values = null;
            } else if (value == null && values != null) {
                this.value = null;
                this.values = values;
            } else if (value == null && values == null) {
                this.value = null;
                this.values = null;
            } else {
                throw new IllegalArgumentException("Either value or values must be specified, but not both");
            }
            this.purpose = purpose;
            this.additionalInformation = additionalInformation;
        }

        public String getClaimName() {
            return this.getClaimName(false);
        }

        public String getClaimName(boolean withLangTag) {
            if (withLangTag && this.langTag != null) {
                return this.claimName + "#" + this.langTag.toString();
            }
            return this.claimName;
        }

        public Entry withClaimRequirement(ClaimRequirement requirement) {
            return new Entry(this.claimName, requirement, this.langTag, this.value, this.values, this.purpose, this.additionalInformation);
        }

        public ClaimRequirement getClaimRequirement() {
            return this.requirement;
        }

        public Entry withLangTag(LangTag langTag) {
            return new Entry(this.claimName, this.requirement, langTag, this.value, this.values, this.purpose, this.additionalInformation);
        }

        public LangTag getLangTag() {
            return this.langTag;
        }

        public Entry withValue(String value) {
            return new Entry(this.claimName, this.requirement, this.langTag, value, null, this.purpose, this.additionalInformation);
        }

        public Entry withValue(Number value) {
            return new Entry(this.claimName, this.requirement, this.langTag, value, null, this.purpose, this.additionalInformation);
        }

        public Entry withValue(JSONObject value) {
            return new Entry(this.claimName, this.requirement, this.langTag, value, null, this.purpose, this.additionalInformation);
        }

        public Entry withValue(Object value) {
            return new Entry(this.claimName, this.requirement, this.langTag, value, null, this.purpose, this.additionalInformation);
        }

        public String getValueAsString() {
            if (this.value instanceof String) {
                return (String)this.value;
            }
            return null;
        }

        @Deprecated
        public String getValue() {
            return this.getValueAsString();
        }

        public Number getValueAsNumber() {
            if (this.value instanceof Number) {
                return (Number)this.value;
            }
            return null;
        }

        public JSONObject getValueAsJSONObject() {
            if (this.value instanceof JSONObject) {
                return (JSONObject)this.value;
            }
            return null;
        }

        public Object getRawValue() {
            return this.value;
        }

        public Entry withValues(List<?> values) {
            return new Entry(this.claimName, this.requirement, this.langTag, null, values, this.purpose, this.additionalInformation);
        }

        public List<String> getValuesAsListOfStrings() {
            if (this.values == null) {
                return null;
            }
            if (this.values.isEmpty()) {
                return Collections.emptyList();
            }
            ArrayList<String> list = new ArrayList<String>(this.values.size());
            for (Object v : this.values) {
                if (v instanceof String) {
                    list.add((String)v);
                    continue;
                }
                return null;
            }
            return list;
        }

        @Deprecated
        public List<String> getValues() {
            return this.getValuesAsListOfStrings();
        }

        public List<JSONObject> getValuesAsListOfJSONObjects() {
            if (this.values == null) {
                return null;
            }
            if (this.values.isEmpty()) {
                return Collections.emptyList();
            }
            ArrayList<JSONObject> list = new ArrayList<JSONObject>(this.values.size());
            for (Object v : this.values) {
                if (v instanceof JSONObject) {
                    list.add((JSONObject)v);
                    continue;
                }
                return null;
            }
            return list;
        }

        public List<?> getValuesAsRawList() {
            return this.values;
        }

        public Entry withPurpose(String purpose) {
            return new Entry(this.claimName, this.requirement, this.langTag, this.value, this.values, purpose, this.additionalInformation);
        }

        public String getPurpose() {
            return this.purpose;
        }

        public Entry withAdditionalInformation(Map<String, Object> additionalInformation) {
            return new Entry(this.claimName, this.requirement, this.langTag, this.value, this.values, this.purpose, additionalInformation);
        }

        public Map<String, Object> getAdditionalInformation() {
            return this.additionalInformation;
        }

        public Map.Entry<String, JSONObject> toJSONObjectEntry() {
            JSONObject entrySpec = null;
            if (this.getRawValue() != null) {
                entrySpec = new JSONObject();
                entrySpec.put("value", this.getRawValue());
            }
            if (this.getValuesAsRawList() != null) {
                entrySpec = new JSONObject();
                entrySpec.put("values", this.getValuesAsRawList());
            }
            if (this.getClaimRequirement().equals((Object)ClaimRequirement.ESSENTIAL)) {
                if (entrySpec == null) {
                    entrySpec = new JSONObject();
                }
                entrySpec.put("essential", true);
            }
            if (this.getPurpose() != null) {
                if (entrySpec == null) {
                    entrySpec = new JSONObject();
                }
                entrySpec.put("purpose", this.getPurpose());
            }
            if (this.getAdditionalInformation() != null) {
                if (entrySpec == null) {
                    entrySpec = new JSONObject();
                }
                for (Map.Entry<String, Object> additionalInformationEntry : this.getAdditionalInformation().entrySet()) {
                    entrySpec.put(additionalInformationEntry.getKey(), additionalInformationEntry.getValue());
                }
            }
            return new AbstractMap.SimpleImmutableEntry<String, JSONObject>(this.getClaimName(true), entrySpec);
        }

        public static Entry parse(Map.Entry<String, JSONObject> jsonObjectEntry) throws ParseException {
            boolean isEssential;
            JSONObject spec;
            String claimName;
            String claimNameWithOptLangTag = jsonObjectEntry.getKey();
            LangTag langTag = null;
            if (claimNameWithOptLangTag.contains("#")) {
                String[] parts = claimNameWithOptLangTag.split("#", 2);
                claimName = parts[0];
                try {
                    langTag = LangTag.parse(parts[1]);
                }
                catch (LangTagException e) {
                    throw new ParseException(e.getMessage(), e);
                }
            } else {
                claimName = claimNameWithOptLangTag;
            }
            if ((spec = jsonObjectEntry.getValue()) == null) {
                return new Entry(claimName).withLangTag(langTag);
            }
            ClaimRequirement requirement = ClaimRequirement.VOLUNTARY;
            if (spec.containsKey("essential") && (isEssential = JSONObjectUtils.getBoolean(spec, "essential"))) {
                requirement = ClaimRequirement.ESSENTIAL;
            }
            String purpose = JSONObjectUtils.getString(spec, "purpose", null);
            if (spec.get("value") != null) {
                Object expectedValue = spec.get("value");
                Map<String, Object> additionalInformation = Entry.getAdditionalInformationFromClaim(spec);
                return new Entry(claimName, requirement, langTag, expectedValue, null, purpose, additionalInformation);
            }
            if (spec.get("values") != null) {
                List<Object> expectedValues = JSONObjectUtils.getList(spec, "values");
                Map<String, Object> additionalInformation = Entry.getAdditionalInformationFromClaim(spec);
                return new Entry(claimName, requirement, langTag, null, expectedValues, purpose, additionalInformation);
            }
            Map<String, Object> additionalInformation = Entry.getAdditionalInformationFromClaim(spec);
            return new Entry(claimName, requirement, langTag, null, null, purpose, additionalInformation);
        }

        private static Map<String, Object> getAdditionalInformationFromClaim(JSONObject spec) {
            HashSet<String> stdKeys = new HashSet<String>(Arrays.asList("essential", "value", "values", "purpose"));
            HashMap<String, Object> additionalClaimInformation = new HashMap<String, Object>();
            for (Map.Entry additionalClaimInformationEntry : spec.entrySet()) {
                if (stdKeys.contains(additionalClaimInformationEntry.getKey())) continue;
                additionalClaimInformation.put((String)additionalClaimInformationEntry.getKey(), additionalClaimInformationEntry.getValue());
            }
            return additionalClaimInformation.isEmpty() ? null : additionalClaimInformation;
        }
    }
}

