/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.ActivityObjectType
 *  com.atlassian.streams.api.ActivityVerb
 *  com.atlassian.streams.api.common.Preconditions
 *  com.google.common.base.Preconditions
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.api.ActivityObjectType;
import com.atlassian.streams.api.ActivityVerb;
import com.atlassian.streams.api.common.Preconditions;
import com.atlassian.streams.spi.StreamsFilterOption;

public interface StreamsFilterOptionProvider {
    public Iterable<StreamsFilterOption> getFilterOptions();

    public Iterable<ActivityOption> getActivities();

    public static final class ActivityOption {
        private final String displayName;
        private final ActivityObjectType type;
        private final ActivityVerb verb;

        public ActivityOption(String displayName, ActivityObjectType type, ActivityVerb verb) {
            this.displayName = Preconditions.checkNotBlank((String)displayName, (String)"displayName");
            this.type = (ActivityObjectType)com.google.common.base.Preconditions.checkNotNull((Object)type, (Object)"type");
            this.verb = (ActivityVerb)com.google.common.base.Preconditions.checkNotNull((Object)verb, (Object)"verb");
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public ActivityObjectType getType() {
            return this.type;
        }

        public ActivityVerb getVerb() {
            return this.verb;
        }

        public int hashCode() {
            int prime = 31;
            return 31 * (31 + this.type.hashCode()) + this.verb.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ActivityOption other = (ActivityOption)obj;
            if (this.type == null && other.type != null || this.verb == null && other.verb != null) {
                return false;
            }
            return this.type.equals(other.type) && this.verb.equals(other.verb);
        }

        public String toActivityOptionKey() {
            return this.getType().key() + ":" + this.getVerb().key();
        }
    }
}

