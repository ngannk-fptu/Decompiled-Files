/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.document.DateTools
 *  org.apache.lucene.document.DateTools$Resolution
 */
package org.apache.lucene.queryparser.flexible.standard.processors;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfig;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.nodes.TermRangeQueryNode;

public class TermRangeQueryNodeProcessor
extends QueryNodeProcessorImpl {
    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        if (node instanceof TermRangeQueryNode) {
            FieldConfig fieldConfig;
            TimeZone timeZone;
            TermRangeQueryNode termRangeNode = (TermRangeQueryNode)node;
            FieldQueryNode upper = (FieldQueryNode)termRangeNode.getUpperBound();
            FieldQueryNode lower = (FieldQueryNode)termRangeNode.getLowerBound();
            DateTools.Resolution dateRes = null;
            boolean inclusive = false;
            Locale locale = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.LOCALE);
            if (locale == null) {
                locale = Locale.getDefault();
            }
            if ((timeZone = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.TIMEZONE)) == null) {
                timeZone = TimeZone.getDefault();
            }
            CharSequence field = termRangeNode.getField();
            String fieldStr = null;
            if (field != null) {
                fieldStr = field.toString();
            }
            if ((fieldConfig = this.getQueryConfigHandler().getFieldConfig(fieldStr)) != null) {
                dateRes = fieldConfig.get(StandardQueryConfigHandler.ConfigurationKeys.DATE_RESOLUTION);
            }
            if (termRangeNode.isUpperInclusive()) {
                inclusive = true;
            }
            String part1 = lower.getTextAsString();
            String part2 = upper.getTextAsString();
            try {
                DateFormat df = DateFormat.getDateInstance(3, locale);
                df.setLenient(true);
                if (part1.length() > 0) {
                    Date d1 = df.parse(part1);
                    part1 = DateTools.dateToString((Date)d1, (DateTools.Resolution)dateRes);
                    lower.setText(part1);
                }
                if (part2.length() > 0) {
                    Date d2 = df.parse(part2);
                    if (inclusive) {
                        Calendar cal = Calendar.getInstance(timeZone, locale);
                        cal.setTime(d2);
                        cal.set(11, 23);
                        cal.set(12, 59);
                        cal.set(13, 59);
                        cal.set(14, 999);
                        d2 = cal.getTime();
                    }
                    part2 = DateTools.dateToString((Date)d2, (DateTools.Resolution)dateRes);
                    upper.setText(part2);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return node;
    }

    @Override
    protected QueryNode preProcessNode(QueryNode node) throws QueryNodeException {
        return node;
    }

    @Override
    protected List<QueryNode> setChildrenOrder(List<QueryNode> children) throws QueryNodeException {
        return children;
    }
}

