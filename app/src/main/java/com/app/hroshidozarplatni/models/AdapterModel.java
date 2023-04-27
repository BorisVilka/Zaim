package com.app.hroshidozarplatni.models;

public interface AdapterModel {
    enum Type {
        TYPE_HEADER,
        TYPE_FAV_ITEM,
        TYPE_DEF_ITEM,
        TYPE_FOOTER
    }

    Type getType();
}
