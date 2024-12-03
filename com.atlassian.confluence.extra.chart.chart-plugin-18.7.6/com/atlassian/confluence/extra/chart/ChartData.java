/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.core.util.DateUtils
 *  org.apache.commons.lang.StringUtils
 *  org.cyberneko.html.parsers.DOMParser
 *  org.dom4j.Attribute
 *  org.dom4j.CDATA
 *  org.dom4j.Document
 *  org.dom4j.Element
 *  org.dom4j.Node
 *  org.dom4j.Text
 *  org.dom4j.io.DOMReader
 *  org.joda.time.LocalDate
 *  org.joda.time.ReadablePartial
 *  org.joda.time.Years
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.chart;

import com.atlassian.confluence.extra.chart.ChartUtil;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.core.util.DateUtils;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import org.apache.commons.lang.StringUtils;
import org.cyberneko.html.parsers.DOMParser;
import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.io.DOMReader;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.joda.time.LocalDate;
import org.joda.time.ReadablePartial;
import org.joda.time.Years;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ChartData {
    private static final Logger log = LoggerFactory.getLogger(ChartData.class);
    private String rendered;
    private Set<Locale> locales = new LinkedHashSet<Locale>();
    private Set<NumberFormat> numberFormats = new LinkedHashSet<NumberFormat>();
    private List<DateFormat> dateFormats = new ArrayList<DateFormat>();
    private Document doc = null;
    private Dataset dataset = null;
    private Class timePeriodClass = null;
    private String[] tableList = null;
    private String[] columnList = null;
    private List headerList;
    private int[] columnMap = null;
    private boolean isVerticalDataOrientation = false;
    private boolean forgive = true;
    private boolean isDate = true;
    private Calendar deltaCalendar = null;
    private int deltaCalendarField = 0;
    private int deltaCalendarFieldMultiplier = 1;
    private Date anchorDate = null;
    private static final String TABLE = "TABLE";
    private static final String THEAD = "THEAD";
    private static final String TBODY = "TBODY";
    private static final String TFOOT = "TFOOT";
    private static final String TR = "TR";
    private static final char NBSP = '\u00a0';
    private static final int INVALID = -1;
    private static final int MAX_RANGE = 200;
    public static final Date MAX_DATE = DateUtils.getDateDay((int)9999, (int)12, (int)31);

    public ChartData(String rendered, String tables, String columns, boolean forgive) {
        this.rendered = rendered;
        this.forgive = forgive;
        if (tables != null && !tables.trim().equals("")) {
            this.tableList = tables.split(",");
        }
        if (columns != null && !columns.trim().equals("")) {
            this.columnList = columns.split(",");
        }
    }

    public void addLocale(Locale locale) {
        if (!this.locales.contains(locale)) {
            this.locales.add(locale);
            this.addNumberFormat(NumberFormat.getPercentInstance(locale));
            this.addNumberFormat(NumberFormat.getCurrencyInstance(locale));
            this.addNumberFormat(NumberFormat.getInstance(locale));
            if (this.isDate) {
                this.addDateFormat(DateFormat.getDateInstance(3, locale));
                this.addDateFormat(DateFormat.getDateInstance(2, locale));
                this.addDateFormat(DateFormat.getDateInstance(1, locale));
            } else {
                this.addDateFormat(DateFormat.getTimeInstance(3, locale));
                this.addDateFormat(DateFormat.getTimeInstance(2, locale));
                this.addDateFormat(DateFormat.getDateInstance(3, locale));
                this.addDateFormat(DateFormat.getDateInstance(2, locale));
            }
            this.addDateFormat(DateFormat.getDateTimeInstance(3, 3, locale));
        }
    }

    public void addNumberFormat(NumberFormat numberFormat) {
        this.numberFormats.add(numberFormat);
    }

    public void addDateFormat(DateFormat dateFormat) {
        dateFormat.setLenient(this.forgive);
        if (!this.dateFormats.contains(dateFormat)) {
            this.dateFormats.add(dateFormat);
        }
    }

    public DateFormat getDateFormat(int index) {
        return this.dateFormats.get(index);
    }

    public void setTimePeriod(String timePeriod) throws MacroExecutionException {
        try {
            this.timePeriodClass = Class.forName("org.jfree.data.time." + StringUtils.capitalize((String)timePeriod));
        }
        catch (ClassNotFoundException exception) {
            throw new MacroExecutionException("Invalid time period specified: " + timePeriod);
        }
        boolean bl = this.isDate = !timePeriod.equalsIgnoreCase("hour") && !timePeriod.equalsIgnoreCase("minute") && !timePeriod.equalsIgnoreCase("second") && !timePeriod.equalsIgnoreCase("millisecond");
        if (timePeriod.equalsIgnoreCase("hour")) {
            this.deltaCalendarField = 10;
        } else if (timePeriod.equalsIgnoreCase("minute")) {
            this.deltaCalendarField = 12;
        } else if (timePeriod.equalsIgnoreCase("second")) {
            this.deltaCalendarField = 13;
        } else if (timePeriod.equalsIgnoreCase("millisecond")) {
            this.deltaCalendarField = 14;
        } else if (timePeriod.equalsIgnoreCase("DAY")) {
            this.deltaCalendarField = 6;
        } else if (timePeriod.equalsIgnoreCase("week")) {
            this.deltaCalendarField = 3;
        } else if (timePeriod.equalsIgnoreCase("month")) {
            this.deltaCalendarField = 2;
        } else if (timePeriod.equalsIgnoreCase("quarter")) {
            this.deltaCalendarField = 2;
            this.deltaCalendarFieldMultiplier = 3;
        } else if (timePeriod.equalsIgnoreCase("year")) {
            this.deltaCalendarField = 1;
        }
    }

    public void setVerticalDataOrientation(boolean value) {
        this.isVerticalDataOrientation = value;
    }

    public void setDateDeltaBase(String value) throws ParseException {
        if (this.deltaCalendar == null) {
            this.deltaCalendar = Calendar.getInstance();
        }
        this.deltaCalendar.setTime(this.toDate(value));
    }

    public Dataset processData(Dataset dataset) throws ParseException, MacroExecutionException {
        long startTime = System.currentTimeMillis();
        this.dataset = dataset;
        try {
            this.doc = this.parseBody(this.rendered);
        }
        catch (IOException | SAXException exception) {
            throw new MacroExecutionException((Throwable)exception);
        }
        int tableCount = 0;
        int tableNumber = 0;
        Element element = this.doc.getRootElement();
        this.lookForTables(element, tableNumber, tableCount);
        if (log.isDebugEnabled()) {
            log.debug("time: " + (System.currentTimeMillis() - startTime) + " ms");
        }
        return this.dataset;
    }

    private Document parseBody(String rendered) throws IOException, SAXException {
        DOMParser domParser = new DOMParser();
        domParser.parse(new InputSource(new StringReader(rendered)));
        return new DOMReader().read(domParser.getDocument());
    }

    private void lookForTables(Element e, int tableNumber, int tableCount) throws ParseException {
        for (Element element : e.elements()) {
            if (TABLE.equalsIgnoreCase(element.getName())) {
                if (this.isTableInList(element, ++tableCount, this.tableList)) {
                    this.processTableContent(element);
                    ++tableNumber;
                    continue;
                }
                this.lookForTables(element, tableNumber, tableCount);
                continue;
            }
            this.lookForTables(element, tableNumber, tableCount);
        }
    }

    private boolean isTableInList(Element element, int tableCount, String[] tableList) {
        int tableListLength = tableList == null ? 0 : tableList.length;
        boolean found = tableListLength == 0;
        Attribute attribute = element.attribute("id");
        String id = attribute == null ? null : attribute.getValue();
        String tableCountString = Integer.toString(tableCount);
        for (int i = 0; !found && i < tableListLength; ++i) {
            found = tableList[i].equalsIgnoreCase(id) || tableList[i].equals(tableCountString);
        }
        return found;
    }

    private void processTableContent(Element element) throws ParseException {
        this.headerList = null;
        this.processTableElements(element);
    }

    private void processTableElements(Element element) throws ParseException {
        for (Object o : element.elements()) {
            Element e = (Element)o;
            if (THEAD.equalsIgnoreCase(e.getName()) || TBODY.equalsIgnoreCase(e.getName()) || TFOOT.equalsIgnoreCase(e.getName())) {
                this.processTableElements(e);
                continue;
            }
            if (!TR.equalsIgnoreCase(e.getName())) continue;
            if (this.headerList == null) {
                this.headerList = e.elements();
                this.setupColumnMap();
                continue;
            }
            if (this.isVerticalDataOrientation) {
                this.processVerticalDataRow(e);
                continue;
            }
            this.processHorizontalDataRow(e);
        }
    }

    private void processHorizontalDataRow(Element row) throws ParseException {
        block4: {
            String category;
            ColumnIterator columnIterator;
            block7: {
                block6: {
                    block5: {
                        columnIterator = new ColumnIterator(row, this.headerList, this.columnMap);
                        if (!columnIterator.hasNext()) break block4;
                        category = this.getFullText((Element)columnIterator.next());
                        if (!(this.dataset instanceof DefaultPieDataset)) break block5;
                        DefaultPieDataset pieDataset = (DefaultPieDataset)this.dataset;
                        while (columnIterator.hasNext()) {
                            String value = this.getFullText((Element)columnIterator.next());
                            String key = this.getFullText((Element)columnIterator.header());
                            pieDataset.setValue((Comparable)((Object)key), this.toNumber(value));
                        }
                        break block4;
                    }
                    if (!(this.dataset instanceof DefaultCategoryDataset)) break block6;
                    DefaultCategoryDataset catDataset = (DefaultCategoryDataset)this.dataset;
                    while (columnIterator.hasNext()) {
                        String value = this.getFullText((Element)columnIterator.next());
                        if (columnIterator.isCurrentColumnNull()) continue;
                        String key = this.getFullText((Element)columnIterator.header());
                        catDataset.addValue(this.toNumber(value), (Comparable)((Object)category), (Comparable)((Object)key));
                    }
                    break block4;
                }
                if (!(this.dataset instanceof XYSeriesCollection)) break block7;
                XYSeries xySeries = new XYSeries((Comparable)((Object)category));
                ((XYSeriesCollection)this.dataset).addSeries(xySeries);
                while (columnIterator.hasNext()) {
                    String value = this.getFullText((Element)columnIterator.next());
                    if (columnIterator.isCurrentColumnNull()) continue;
                    String key = this.getFullText((Element)columnIterator.header());
                    xySeries.add(this.toNumber(key), this.toNumber(value));
                }
                break block4;
            }
            if (!(this.dataset instanceof TimeSeriesCollection)) break block4;
            TimeSeries timeSeries = new TimeSeries((Comparable)((Object)category), this.timePeriodClass);
            ((TimeSeriesCollection)this.dataset).addSeries(timeSeries);
            while (columnIterator.hasNext()) {
                String value = this.getFullText((Element)columnIterator.next());
                if (columnIterator.isCurrentColumnNull()) continue;
                String key = this.getFullText((Element)columnIterator.header());
                timeSeries.add(RegularTimePeriod.createInstance(this.timePeriodClass, this.toDate(key), TimeZone.getDefault()), this.toNumber(value));
            }
        }
    }

    private void processVerticalDataRow(Element row) throws ParseException {
        ColumnIterator columnIterator = new ColumnIterator(row, this.headerList, this.columnMap);
        if (columnIterator.hasNext()) {
            if (this.dataset instanceof DefaultPieDataset) {
                DefaultPieDataset pieDataset = (DefaultPieDataset)this.dataset;
                String key = this.getFullText((Element)columnIterator.next());
                if (columnIterator.hasNext()) {
                    String value = this.getFullText((Element)columnIterator.next());
                    pieDataset.setValue((Comparable)((Object)key), this.toNumber(value));
                }
            } else if (this.dataset instanceof DefaultCategoryDataset) {
                DefaultCategoryDataset catDataset = (DefaultCategoryDataset)this.dataset;
                String category = this.getFullText((Element)columnIterator.next());
                while (columnIterator.hasNext()) {
                    String value = this.getFullText((Element)columnIterator.next());
                    String key = this.getFullText((Element)columnIterator.header());
                    catDataset.addValue(this.toNumber(value), (Comparable)((Object)key), (Comparable)((Object)category));
                }
            } else if (this.dataset instanceof XYSeriesCollection) {
                String key = this.getFullText((Element)columnIterator.next());
                while (columnIterator.hasNext()) {
                    String value = this.getFullText((Element)columnIterator.next());
                    String seriesKey = this.getFullText((Element)columnIterator.header());
                    XYSeriesCollection collection = (XYSeriesCollection)this.dataset;
                    XYSeries series = null;
                    for (int i = collection.getSeriesCount() - 1; i >= 0; --i) {
                        if (!collection.getSeriesKey(i).equals(seriesKey)) continue;
                        series = collection.getSeries(i);
                    }
                    if (series == null) {
                        series = new XYSeries((Comparable)((Object)seriesKey));
                        collection.addSeries(series);
                    }
                    series.add(this.toNumber(key), this.toNumber(value));
                }
            } else if (this.dataset instanceof TimeSeriesCollection) {
                String key = this.getFullText((Element)columnIterator.next());
                TimeSeriesCollection collection = (TimeSeriesCollection)this.dataset;
                while (columnIterator.hasNext()) {
                    String value = this.getFullText((Element)columnIterator.next());
                    String seriesKey = this.getFullText((Element)columnIterator.header());
                    TimeSeries series = collection.getSeries((Comparable)((Object)seriesKey));
                    if (series == null) {
                        series = new TimeSeries((Comparable)((Object)seriesKey), this.timePeriodClass);
                        collection.addSeries(series);
                    }
                    series.add(RegularTimePeriod.createInstance(this.timePeriodClass, this.toDate(key), TimeZone.getDefault()), this.toNumber(value));
                }
            } else if (this.dataset instanceof TaskSeriesCollection) {
                Task task;
                String category = null;
                if (columnIterator.hasNext()) {
                    category = this.getFullText((Element)columnIterator.next());
                }
                if (category.equals("")) {
                    category = this.getFullText((Element)columnIterator.header());
                }
                if (category.equals("")) {
                    category = this.getFullText((Element)this.headerList.get(0));
                }
                TaskSeriesCollection collection = (TaskSeriesCollection)this.dataset;
                TaskSeries taskSeries = null;
                if (ChartUtil.isVersion103Capable()) {
                    taskSeries = collection.getSeries((Comparable)((Object)category));
                }
                if (taskSeries == null) {
                    taskSeries = new TaskSeries(category);
                    collection.add(taskSeries);
                }
                String group = this.getFullText((Element)columnIterator.next());
                Task mainTask = null;
                if (!group.equals("")) {
                    mainTask = taskSeries.get(group);
                }
                if ((task = this.createTask(columnIterator)) != null) {
                    if (mainTask == null) {
                        taskSeries.add(task);
                    } else {
                        mainTask.addSubtask(task);
                    }
                }
            }
        }
    }

    private Task createTask(ColumnIterator iterator) throws ParseException {
        Task task = null;
        while (iterator.hasNext()) {
            String start;
            String name = this.getFullText((Element)iterator.next());
            String string = start = iterator.hasNext() ? this.getFullText((Element)iterator.next()) : "";
            if (!iterator.hasNext()) continue;
            String end = this.getFullText((Element)iterator.next());
            if (!start.trim().equals("") && !end.trim().equals("")) {
                Date startDate = this.toDate(start);
                Date endDate = this.toDate(end);
                this.setDateDeltaBase(start);
                if (!this.isValidDateForTask(startDate, startDate)) {
                    throw new ParseException("Invalid input date at table row '" + name + "'", 0);
                }
                if (!this.isValidDateForTask(startDate, endDate)) {
                    throw new ParseException("Invalid input date at table row '" + name + "'", 0);
                }
                task = new Task(name, startDate, endDate);
            }
            if (!iterator.hasNext()) continue;
            String percent = this.getFullText((Element)iterator.next());
            if (task == null || percent.equals("")) continue;
            task.setPercentComplete(this.toNumber(percent).doubleValue());
        }
        return task;
    }

    private boolean isValidDateForTask(Date startDate, Date verifiedDate) {
        this.anchorDate = this.anchorDate == null ? startDate : (this.anchorDate.before(startDate) ? this.anchorDate : startDate);
        long distance = Years.yearsBetween((ReadablePartial)new LocalDate((Object)this.anchorDate), (ReadablePartial)new LocalDate((Object)verifiedDate)).getYears();
        return distance <= 200L && !verifiedDate.after(MAX_DATE);
    }

    private String getFullText(Element element) {
        if (element == null) {
            return "";
        }
        StringBuilder buff = new StringBuilder();
        Iterator i = element.nodeIterator();
        while (i.hasNext()) {
            Node node = (Node)i.next();
            if (node instanceof Text || node instanceof CDATA) {
                buff.append(node.getText());
                continue;
            }
            if (!(node instanceof Element)) continue;
            buff.append(this.getFullText((Element)node));
        }
        return buff.toString().replace('\u00a0', ' ').trim();
    }

    private Number toNumber(String value) throws ParseException {
        if (value != null) {
            for (NumberFormat numberFormat : this.numberFormats) {
                try {
                    return numberFormat.parse(value);
                }
                catch (ParseException parseException) {
                }
            }
        }
        if (this.forgive) {
            if (value != null && (value = value.replaceAll("[^0-9\\.\\+,-]", "")).length() > 0) {
                try {
                    return new Double(value);
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
            return 0.0;
        }
        throw new ParseException("'" + value + "' could not be converted to number.", 0);
    }

    public Date toDate(String value) throws ParseException {
        if (value != null) {
            for (DateFormat dateFormat : this.dateFormats) {
                try {
                    return dateFormat.parse(value);
                }
                catch (ParseException parseException) {
                }
            }
        }
        if (this.deltaCalendar != null) {
            int delta = this.toNumber(value).intValue();
            this.deltaCalendar.add(this.deltaCalendarField, delta * this.deltaCalendarFieldMultiplier);
            Date date = this.deltaCalendar.getTime();
            this.deltaCalendar.add(this.deltaCalendarField, -delta * this.deltaCalendarFieldMultiplier);
            return date;
        }
        throw new ParseException("'" + value + "' could not be converted to date.", 0);
    }

    private void setupColumnMap() {
        if (this.columnList == null || this.columnList.length == 0) {
            this.columnMap = null;
            return;
        }
        this.columnMap = new int[this.columnList.length];
        for (int i = 0; i < this.columnMap.length; ++i) {
            String column = this.columnList[i];
            try {
                this.columnMap[i] = Integer.parseInt(column) - 1;
                continue;
            }
            catch (NumberFormatException exception) {
                this.columnMap[i] = this.convertColumnNameToIndex(column.trim());
            }
        }
    }

    private int convertColumnNameToIndex(String columnName) {
        for (int i = 0; i < this.headerList.size(); ++i) {
            Attribute attribute = ((Element)this.headerList.get(i)).attribute("title");
            String title = attribute == null ? null : attribute.getValue().trim();
            String value = this.getFullText((Element)this.headerList.get(i));
            if (!columnName.equalsIgnoreCase(value) && !columnName.equalsIgnoreCase(title)) continue;
            return i;
        }
        return -1;
    }

    class ColumnIterator
    implements Iterator {
        private List list;
        private List headerList;
        private int position = -1;
        private int[] columnMap;
        private int length;
        private Set<Integer> nullColumnIndexes;

        public ColumnIterator(Element row, List headerList, int[] columnMap) {
            this.list = row.elements();
            this.headerList = headerList;
            this.columnMap = columnMap;
            this.length = columnMap == null ? this.list.size() : columnMap.length;
            this.initNullColumnIndexes();
        }

        private void initNullColumnIndexes() {
            this.nullColumnIndexes = new HashSet<Integer>();
            if (!ChartData.this.isVerticalDataOrientation) {
                String elementText;
                String elementText2;
                int i;
                int j = this.list.size();
                for (i = 1; i < j && StringUtils.isBlank((String)(elementText2 = ChartData.this.getFullText((Element)this.list.get(i)))); ++i) {
                    this.nullColumnIndexes.add(i);
                }
                for (i = this.list.size() - 1; i >= 0 && StringUtils.isBlank((String)(elementText = ChartData.this.getFullText((Element)this.list.get(i)))); --i) {
                    this.nullColumnIndexes.add(i);
                }
            }
        }

        private int getColumnIndex() {
            if (this.columnMap != null && this.position < this.columnMap.length) {
                return this.columnMap[this.position];
            }
            return this.position;
        }

        public boolean isCurrentColumnNull() {
            return this.nullColumnIndexes.contains(this.getColumnIndex());
        }

        public Object next() {
            if (this.hasNext()) {
                ++this.position;
                int index = this.getColumnIndex();
                if (index >= 0 && index < this.list.size()) {
                    return this.list.get(index);
                }
            }
            return null;
        }

        public Object header() {
            int index;
            if (this.headerList != null && (index = this.getColumnIndex()) >= 0 && index < this.headerList.size()) {
                return this.headerList.get(index);
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            return this.position < this.length - 1;
        }

        @Override
        public void remove() {
        }
    }
}

