/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.PropertyUtils
 *  org.apache.commons.collections.FastHashMap
 */
package org.apache.commons.validator;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Msg;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;
import org.apache.commons.validator.Var;
import org.apache.commons.validator.util.ValidatorUtils;

public class Field
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -8502647722530192185L;
    private static final String DEFAULT_ARG = "org.apache.commons.validator.Field.DEFAULT";
    public static final String TOKEN_INDEXED = "[]";
    protected static final String TOKEN_START = "${";
    protected static final String TOKEN_END = "}";
    protected static final String TOKEN_VAR = "var:";
    protected String property = null;
    protected String indexedProperty = null;
    protected String indexedListProperty = null;
    protected String key = null;
    protected String depends = null;
    protected int page = 0;
    protected boolean clientValidation = true;
    protected int fieldOrder = 0;
    private final List<String> dependencyList = Collections.synchronizedList(new ArrayList());
    @Deprecated
    protected FastHashMap hVars = new FastHashMap();
    @Deprecated
    protected FastHashMap hMsgs = new FastHashMap();
    protected Map<String, Arg>[] args = new Map[0];

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getFieldOrder() {
        return this.fieldOrder;
    }

    public void setFieldOrder(int fieldOrder) {
        this.fieldOrder = fieldOrder;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getIndexedProperty() {
        return this.indexedProperty;
    }

    public void setIndexedProperty(String indexedProperty) {
        this.indexedProperty = indexedProperty;
    }

    public String getIndexedListProperty() {
        return this.indexedListProperty;
    }

    public void setIndexedListProperty(String indexedListProperty) {
        this.indexedListProperty = indexedListProperty;
    }

    public String getDepends() {
        return this.depends;
    }

    public void setDepends(String depends) {
        this.depends = depends;
        this.dependencyList.clear();
        StringTokenizer st = new StringTokenizer(depends, ",");
        while (st.hasMoreTokens()) {
            String depend = st.nextToken().trim();
            if (depend == null || depend.length() <= 0) continue;
            this.dependencyList.add(depend);
        }
    }

    public void addMsg(Msg msg) {
        this.getMsgMap().put(msg.getName(), msg);
    }

    public String getMsg(String key) {
        Msg msg = this.getMessage(key);
        return msg == null ? null : msg.getKey();
    }

    public Msg getMessage(String key) {
        return this.getMsgMap().get(key);
    }

    public Map<String, Msg> getMessages() {
        return Collections.unmodifiableMap(this.getMsgMap());
    }

    public boolean isClientValidation() {
        return this.clientValidation;
    }

    public void setClientValidation(boolean clientValidation) {
        this.clientValidation = clientValidation;
    }

    public void addArg(Arg arg) {
        if (arg == null || arg.getKey() == null || arg.getKey().length() == 0) {
            return;
        }
        this.determineArgPosition(arg);
        this.ensureArgsCapacity(arg);
        Map<String, Arg> argMap = this.args[arg.getPosition()];
        if (argMap == null) {
            this.args[arg.getPosition()] = argMap = new HashMap<String, Arg>();
        }
        if (arg.getName() == null) {
            argMap.put(DEFAULT_ARG, arg);
        } else {
            argMap.put(arg.getName(), arg);
        }
    }

    private void determineArgPosition(Arg arg) {
        int position = arg.getPosition();
        if (position >= 0) {
            return;
        }
        if (this.args == null || this.args.length == 0) {
            arg.setPosition(0);
            return;
        }
        String key = arg.getName() == null ? DEFAULT_ARG : arg.getName();
        int lastPosition = -1;
        int lastDefault = -1;
        for (int i = 0; i < this.args.length; ++i) {
            if (this.args[i] != null && this.args[i].containsKey(key)) {
                lastPosition = i;
            }
            if (this.args[i] == null || !this.args[i].containsKey(DEFAULT_ARG)) continue;
            lastDefault = i;
        }
        if (lastPosition < 0) {
            lastPosition = lastDefault;
        }
        arg.setPosition(++lastPosition);
    }

    private void ensureArgsCapacity(Arg arg) {
        if (arg.getPosition() >= this.args.length) {
            Map[] newArgs = new Map[arg.getPosition() + 1];
            System.arraycopy(this.args, 0, newArgs, 0, this.args.length);
            this.args = newArgs;
        }
    }

    public Arg getArg(int position) {
        return this.getArg(DEFAULT_ARG, position);
    }

    public Arg getArg(String key, int position) {
        if (position >= this.args.length || this.args[position] == null) {
            return null;
        }
        Arg arg = this.args[position].get(key);
        if (arg == null && key.equals(DEFAULT_ARG)) {
            return null;
        }
        return arg == null ? this.getArg(position) : arg;
    }

    public Arg[] getArgs(String key) {
        Arg[] args = new Arg[this.args.length];
        for (int i = 0; i < this.args.length; ++i) {
            args[i] = this.getArg(key, i);
        }
        return args;
    }

    public void addVar(Var v) {
        this.getVarMap().put(v.getName(), v);
    }

    public void addVar(String name, String value, String jsType) {
        this.addVar(new Var(name, value, jsType));
    }

    public Var getVar(String mainKey) {
        return this.getVarMap().get(mainKey);
    }

    public String getVarValue(String mainKey) {
        String value = null;
        Var o = this.getVarMap().get(mainKey);
        if (o != null && o instanceof Var) {
            Var v = o;
            value = v.getValue();
        }
        return value;
    }

    public Map<String, Var> getVars() {
        return Collections.unmodifiableMap(this.getVarMap());
    }

    public String getKey() {
        if (this.key == null) {
            this.generateKey();
        }
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isIndexed() {
        return this.indexedListProperty != null && this.indexedListProperty.length() > 0;
    }

    public void generateKey() {
        this.key = this.isIndexed() ? this.indexedListProperty + TOKEN_INDEXED + "." + this.property : this.property;
    }

    void process(Map<String, String> globalConstants, Map<String, String> constants) {
        String replaceValue;
        String key2;
        String key;
        this.hMsgs.setFast(false);
        this.hVars.setFast(true);
        this.generateKey();
        for (Map.Entry<String, String> entry : constants.entrySet()) {
            key = entry.getKey();
            key2 = TOKEN_START + key + TOKEN_END;
            replaceValue = entry.getValue();
            this.property = ValidatorUtils.replace(this.property, key2, replaceValue);
            this.processVars(key2, replaceValue);
            this.processMessageComponents(key2, replaceValue);
        }
        for (Map.Entry<String, String> entry : globalConstants.entrySet()) {
            key = entry.getKey();
            key2 = TOKEN_START + key + TOKEN_END;
            replaceValue = entry.getValue();
            this.property = ValidatorUtils.replace(this.property, key2, replaceValue);
            this.processVars(key2, replaceValue);
            this.processMessageComponents(key2, replaceValue);
        }
        for (String key3 : this.getVarMap().keySet()) {
            String key22 = "${var:" + key3 + TOKEN_END;
            Var var = this.getVar(key3);
            replaceValue = var.getValue();
            this.processMessageComponents(key22, replaceValue);
        }
        this.hMsgs.setFast(true);
    }

    private void processVars(String key, String replaceValue) {
        for (String varKey : this.getVarMap().keySet()) {
            Var var = this.getVar(varKey);
            var.setValue(ValidatorUtils.replace(var.getValue(), key, replaceValue));
        }
    }

    private void processMessageComponents(String key, String replaceValue) {
        String varKey = "${var:";
        if (key != null && !key.startsWith(varKey)) {
            for (Msg msg : this.getMsgMap().values()) {
                msg.setKey(ValidatorUtils.replace(msg.getKey(), key, replaceValue));
            }
        }
        this.processArg(key, replaceValue);
    }

    private void processArg(String key, String replaceValue) {
        for (int i = 0; i < this.args.length; ++i) {
            Map<String, Arg> argMap = this.args[i];
            if (argMap == null) continue;
            for (Arg arg : argMap.values()) {
                if (arg == null) continue;
                arg.setKey(ValidatorUtils.replace(arg.getKey(), key, replaceValue));
            }
        }
    }

    public boolean isDependency(String validatorName) {
        return this.dependencyList.contains(validatorName);
    }

    public List<String> getDependencyList() {
        return Collections.unmodifiableList(this.dependencyList);
    }

    public Object clone() {
        Field field = null;
        try {
            field = (Field)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
        }
        Map[] tempMap = new Map[this.args.length];
        field.args = tempMap;
        for (int i = 0; i < this.args.length; ++i) {
            if (this.args[i] == null) continue;
            HashMap<String, Arg> argMap = new HashMap<String, Arg>(this.args[i]);
            for (Map.Entry entry : argMap.entrySet()) {
                String validatorName = (String)entry.getKey();
                Arg arg = (Arg)entry.getValue();
                argMap.put(validatorName, (Arg)arg.clone());
            }
            field.args[i] = argMap;
        }
        field.hVars = ValidatorUtils.copyFastHashMap(this.hVars);
        field.hMsgs = ValidatorUtils.copyFastHashMap(this.hMsgs);
        return field;
    }

    public String toString() {
        StringBuilder results = new StringBuilder();
        results.append("\t\tkey = " + this.key + "\n");
        results.append("\t\tproperty = " + this.property + "\n");
        results.append("\t\tindexedProperty = " + this.indexedProperty + "\n");
        results.append("\t\tindexedListProperty = " + this.indexedListProperty + "\n");
        results.append("\t\tdepends = " + this.depends + "\n");
        results.append("\t\tpage = " + this.page + "\n");
        results.append("\t\tfieldOrder = " + this.fieldOrder + "\n");
        if (this.hVars != null) {
            results.append("\t\tVars:\n");
            for (String key : this.getVarMap().keySet()) {
                results.append("\t\t\t");
                results.append((Object)key);
                results.append("=");
                results.append(this.getVarMap().get(key));
                results.append("\n");
            }
        }
        return results.toString();
    }

    Object[] getIndexedProperty(Object bean) throws ValidatorException {
        Object indexedProperty = null;
        try {
            indexedProperty = PropertyUtils.getProperty((Object)bean, (String)this.getIndexedListProperty());
        }
        catch (IllegalAccessException e) {
            throw new ValidatorException(e.getMessage());
        }
        catch (InvocationTargetException e) {
            throw new ValidatorException(e.getMessage());
        }
        catch (NoSuchMethodException e) {
            throw new ValidatorException(e.getMessage());
        }
        if (indexedProperty instanceof Collection) {
            return ((Collection)indexedProperty).toArray();
        }
        if (indexedProperty.getClass().isArray()) {
            return (Object[])indexedProperty;
        }
        throw new ValidatorException(this.getKey() + " is not indexed");
    }

    private int getIndexedPropertySize(Object bean) throws ValidatorException {
        Object indexedProperty = null;
        try {
            indexedProperty = PropertyUtils.getProperty((Object)bean, (String)this.getIndexedListProperty());
        }
        catch (IllegalAccessException e) {
            throw new ValidatorException(e.getMessage());
        }
        catch (InvocationTargetException e) {
            throw new ValidatorException(e.getMessage());
        }
        catch (NoSuchMethodException e) {
            throw new ValidatorException(e.getMessage());
        }
        if (indexedProperty == null) {
            return 0;
        }
        if (indexedProperty instanceof Collection) {
            return ((Collection)indexedProperty).size();
        }
        if (indexedProperty.getClass().isArray()) {
            return ((Object[])indexedProperty).length;
        }
        throw new ValidatorException(this.getKey() + " is not indexed");
    }

    private boolean validateForRule(ValidatorAction va, ValidatorResults results, Map<String, ValidatorAction> actions, Map<String, Object> params, int pos) throws ValidatorException {
        ValidatorResult result = results.getValidatorResult(this.getKey());
        if (result != null && result.containsAction(va.getName())) {
            return result.isValid(va.getName());
        }
        if (!this.runDependentValidators(va, results, actions, params, pos)) {
            return false;
        }
        return va.executeValidationMethod(this, params, results, pos);
    }

    private boolean runDependentValidators(ValidatorAction va, ValidatorResults results, Map<String, ValidatorAction> actions, Map<String, Object> params, int pos) throws ValidatorException {
        List<String> dependentValidators = va.getDependencyList();
        if (dependentValidators.isEmpty()) {
            return true;
        }
        for (String depend : dependentValidators) {
            ValidatorAction action = actions.get(depend);
            if (action == null) {
                this.handleMissingAction(depend);
            }
            if (this.validateForRule(action, results, actions, params, pos)) continue;
            return false;
        }
        return true;
    }

    public ValidatorResults validate(Map<String, Object> params, Map<String, ValidatorAction> actions) throws ValidatorException {
        if (this.getDepends() == null) {
            return new ValidatorResults();
        }
        ValidatorResults allResults = new ValidatorResults();
        Object bean = params.get("java.lang.Object");
        int numberOfFieldsToValidate = this.isIndexed() ? this.getIndexedPropertySize(bean) : 1;
        for (int fieldNumber = 0; fieldNumber < numberOfFieldsToValidate; ++fieldNumber) {
            Iterator<String> dependencies = this.dependencyList.iterator();
            ValidatorResults results = new ValidatorResults();
            while (dependencies.hasNext()) {
                boolean good;
                String depend = dependencies.next();
                ValidatorAction action = actions.get(depend);
                if (action == null) {
                    this.handleMissingAction(depend);
                }
                if (good = this.validateForRule(action, results, actions, params, fieldNumber)) continue;
                allResults.merge(results);
                return allResults;
            }
            allResults.merge(results);
        }
        return allResults;
    }

    private void handleMissingAction(String name) throws ValidatorException {
        throw new ValidatorException("No ValidatorAction named " + name + " found for field " + this.getProperty());
    }

    protected Map<String, Msg> getMsgMap() {
        return this.hMsgs;
    }

    protected Map<String, Var> getVarMap() {
        return this.hVars;
    }
}

