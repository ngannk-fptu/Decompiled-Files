/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaPersister
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.bandana;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaPersister;
import com.atlassian.confluence.setup.bandana.BandanaPersisterSupport;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaRecord;
import com.atlassian.confluence.setup.bandana.KeyedBandanaContext;
import com.atlassian.confluence.setup.bandana.persistence.dao.ConfluenceBandanaRecordDao;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceDaoBandanaPersister
implements BandanaPersister {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceDaoBandanaPersister.class);
    private ConfluenceBandanaRecordDao dao;
    private BandanaPersisterSupport support;

    public Object retrieve(BandanaContext context, String key) {
        ConfluenceBandanaRecord record = this.dao.getRecord(this.toContextKey(context), key);
        return record == null ? null : this.getObjectFromValue(record, context);
    }

    public Map retrieve(BandanaContext context) {
        Collection c = this.dao.findForContext(this.toContextKey(context));
        HashMap<String, Object> m = new HashMap<String, Object>();
        for (ConfluenceBandanaRecord r : c) {
            Object o = this.getObjectFromValue(r, context);
            if (o == null) continue;
            m.put(r.getKey(), o);
        }
        return m;
    }

    public Iterable<String> retrieveKeys(BandanaContext bandanaContext) {
        return this.dao.findKeysForContext(this.toContextKey(bandanaContext));
    }

    private String toContextKey(BandanaContext context) {
        if (!(context instanceof KeyedBandanaContext)) {
            throw new ClassCastException("expected KeyedBandanaContext but got " + context.getClass());
        }
        return ((KeyedBandanaContext)context).getContextKey();
    }

    public void store(BandanaContext context, String key, Object value) {
        StringWriter objectData = new StringWriter();
        try {
            this.support.getSerializer(context).serialize(value, objectData);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to serialize object data", e);
        }
        this.dao.saveOrUpdate(new ConfluenceBandanaRecord(this.toContextKey(context), key, objectData.toString()));
    }

    public void flushCaches() {
    }

    public void remove(BandanaContext context) {
        this.dao.removeAllInContext(this.toContextKey(context));
    }

    public void remove(BandanaContext context, String key) {
        this.dao.remove(new ConfluenceBandanaRecord(this.toContextKey(context), key, null));
    }

    private Object getObjectFromValue(ConfluenceBandanaRecord record, BandanaContext context) {
        try {
            return this.support.getSerializer(context).deserialize(new StringReader(record.getValue()));
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to deserialize object data", e);
        }
        catch (Exception e) {
            log.debug("Configuration could not be loaded because class could not be found (context: " + record.getContext() + ", key: " + record.getKey() + ").\n" + e.getClass().getName() + ": " + e.getMessage(), (Throwable)e);
            return null;
        }
    }

    public void setConfluenceBandanaRecordDao(ConfluenceBandanaRecordDao dao) {
        this.dao = dao;
    }

    public void setBandanaPersisterSupport(BandanaPersisterSupport support) {
        this.support = support;
    }
}

