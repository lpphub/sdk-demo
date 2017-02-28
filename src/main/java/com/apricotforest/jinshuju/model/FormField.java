package com.apricotforest.jinshuju.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FormField implements Serializable {
    private String type;
    private String label;
    @JSONField(name = "api_code")
    private String apiCode;
    private String notes;
    @JSONField(name = "predefined_value")
    private String predefinedValue;
    @JSONField(name = "private")
    private Boolean isPrivate;
    private Map<String, String> validations;
    private List<Option> choices;

    @JSONField(name = "allow_other")
    private Boolean allowOther;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getApiCode() {
        return apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPredefinedValue() {
        return predefinedValue;
    }

    public void setPredefinedValue(String predefinedValue) {
        this.predefinedValue = predefinedValue;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Map<String, String> getValidations() {
        return validations;
    }

    public void setValidations(Map<String, String> validations) {
        this.validations = validations;
    }

    public List<Option> getChoices() {
        return choices;
    }

    public void setChoices(List<Option> choices) {
        this.choices = choices;
    }

    public Boolean getAllowOther() {
        return allowOther;
    }

    public void setAllowOther(Boolean allowOther) {
        this.allowOther = allowOther;
    }
}
