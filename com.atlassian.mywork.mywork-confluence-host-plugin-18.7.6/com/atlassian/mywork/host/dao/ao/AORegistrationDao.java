/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.mywork.model.Registration
 *  com.atlassian.mywork.model.Registration$RegistrationId
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Lists
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.dao.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.mywork.host.dao.RegistrationDao;
import com.atlassian.mywork.host.dao.ao.AORegistration;
import com.atlassian.mywork.host.dao.ao.AbstractAODao;
import com.atlassian.mywork.model.Registration;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class AORegistrationDao
extends AbstractAODao<AORegistration, String>
implements RegistrationDao {
    private final ObjectMapper mapper = new ObjectMapper();

    public AORegistrationDao(ActiveObjects ao) {
        super(AORegistration.class, ao);
    }

    private AORegistration getOrCreate(Registration.RegistrationId id) throws Exception {
        AORegistration dto = (AORegistration)this.ao.get(AORegistration.class, (Object)id.toString());
        if (dto == null) {
            dto = (AORegistration)this.ao.create(AORegistration.class, new DBParam[]{new DBParam("ID", (Object)id.toString())});
            this.update(dto, new Registration(id));
        }
        return dto;
    }

    @Override
    public Registration get(Registration.RegistrationId id) {
        try {
            return (Registration)this.mapper.readValue(this.getOrCreate(id).getData(), Registration.class);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Registration> getAll() {
        AORegistration[] aoRegistrations = (AORegistration[])this.ao.find(AORegistration.class);
        ArrayList registrations = Lists.newArrayListWithExpectedSize((int)aoRegistrations.length);
        for (AORegistration aoRegistration : aoRegistrations) {
            try {
                registrations.add((Registration)this.mapper.readValue(aoRegistration.getData(), Registration.class));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return registrations;
    }

    @Override
    public Date getMostRecentUpdate() {
        Date date = new Date(0L);
        for (AORegistration reg : (AORegistration[])this.ao.find(AORegistration.class, Query.select().order("UPDATED DESC").limit(1))) {
            Date updated = reg.getUpdated();
            date = date.compareTo((Date)MoreObjects.firstNonNull((Object)updated, (Object)date)) < 0 ? updated : date;
        }
        return date;
    }

    @Override
    public void set(Registration registration) {
        try {
            this.update(this.getOrCreate(registration.getId()), registration);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void update(AORegistration dto, Registration registration) throws IOException {
        dto.setData(this.mapper.writeValueAsString((Object)registration));
        dto.setUpdated(new Date());
        dto.save();
    }
}

