/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.schema;

import java.util.ArrayList;
import java.util.List;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.AbstractField;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.JobType;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="xmpBJ", namespace="http://ns.adobe.com/xap/1.0/bj/")
public class XMPBasicJobTicketSchema
extends XMPSchema {
    @PropertyType(type=Types.Job, card=Cardinality.Bag)
    public static final String JOB_REF = "JobRef";
    private ArrayProperty bagJobs;

    public XMPBasicJobTicketSchema(XMPMetadata metadata) {
        this(metadata, null);
    }

    public XMPBasicJobTicketSchema(XMPMetadata metadata, String ownPrefix) {
        super(metadata, ownPrefix);
    }

    public void addJob(String id, String name, String url) {
        this.addJob(id, name, url, null);
    }

    public void addJob(String id, String name, String url, String fieldPrefix) {
        JobType job = new JobType(this.getMetadata(), fieldPrefix);
        job.setId(id);
        job.setName(name);
        job.setUrl(url);
        this.addJob(job);
    }

    public void addJob(JobType job) {
        String prefix = this.getNamespacePrefix(job.getNamespace());
        if (prefix != null) {
            job.setPrefix(prefix);
        } else {
            this.addNamespace(job.getNamespace(), job.getPrefix());
        }
        if (this.bagJobs == null) {
            this.bagJobs = this.createArrayProperty(JOB_REF, Cardinality.Bag);
            this.addProperty(this.bagJobs);
        }
        this.bagJobs.getContainer().addProperty(job);
    }

    public List<JobType> getJobs() throws BadFieldValueException {
        List<AbstractField> tmp = this.getUnqualifiedArrayList(JOB_REF);
        if (tmp != null) {
            ArrayList<JobType> layers = new ArrayList<JobType>();
            for (AbstractField abstractField : tmp) {
                if (abstractField instanceof JobType) {
                    layers.add((JobType)abstractField);
                    continue;
                }
                throw new BadFieldValueException("Job expected and " + abstractField.getClass().getName() + " found.");
            }
            return layers;
        }
        return null;
    }
}

