/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModel;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class DateModel
extends BeanModel
implements TemplateDateModel {
    static final ModelFactory FACTORY = new ModelFactory(){

        @Override
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new DateModel((Date)object, (BeansWrapper)wrapper);
        }
    };
    private final int type;

    public DateModel(Date date, BeansWrapper wrapper) {
        super(date, wrapper);
        this.type = date instanceof java.sql.Date ? 2 : (date instanceof Time ? 1 : (date instanceof Timestamp ? 3 : wrapper.getDefaultDateType()));
    }

    @Override
    public Date getAsDate() {
        return (Date)this.object;
    }

    @Override
    public int getDateType() {
        return this.type;
    }
}

