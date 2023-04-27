package com.app.hroshidozarplatni.models;

public class TitleModel implements AdapterModel {
    String value;

    public TitleModel(String value) {
        this.value = value;
    }

    @Override
    public AdapterModel.Type getType() {
        return AdapterModel.Type.TYPE_HEADER;
    }

    public String getValue() {
        return value;
    }
}
