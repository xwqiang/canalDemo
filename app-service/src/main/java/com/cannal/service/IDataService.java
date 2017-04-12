package com.cannal.service;
/**
 * Created by xuwuqiang on 2017/4/11.
 */
public interface IDataService {

    void onDelete(String schema,String table);
    void onInsert();
    void onUpdaet();
}
