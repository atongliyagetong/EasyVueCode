package com.example.demo.model;

import java.util.List;
import java.util.Map;

/**
 * Created by Chen on 2019/3/1.
 */
public class ExcleColumn {
    private String fileName;
    private String fileCode;
    private boolean threeListTreeFlag;
    private boolean extendQuery=false;  //是否高级查询，默认否

    private String superTreeName;

    private String  superTreeCode;
    /**
     * 字段
     */
    private List<Field> fields;



    /**
     * 存储excle一行（单个功能）的select下拉框的值，
     * key       下拉框字段名称    例如：包装材质
     * value    下拉框中的值       例如：1:金属,2:塑料
     */

    private Map<String, String> selectKeyValueMap;

    public boolean isExtendQuery() {
        return extendQuery;
    }

    public void setExtendQuery(boolean extendQuery) {
        this.extendQuery = extendQuery;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileCode() {
        return fileCode;
    }

    public void setFileCode(String fileCode) {
        this.fileCode = fileCode;
    }

    public boolean isThreeListTreeFlag() {
        return threeListTreeFlag;
    }

    public void setThreeListTreeFlag(boolean threeListTreeFlag) {
        this.threeListTreeFlag = threeListTreeFlag;
    }

    public String getSuperTreeName() {
        return superTreeName;
    }

    public void setSuperTreeName(String superTreeName) {
        this.superTreeName = superTreeName;
    }

    public String getSuperTreeCode() {
        return superTreeCode;
    }

    public void setSuperTreeCode(String superTreeCode) {
        this.superTreeCode = superTreeCode;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }


    public Map<String, String> getSelectKeyValueMap() {
        return selectKeyValueMap;
    }

    public void setSelectKeyValueMap(Map<String, String> selectKeyValueMap) {
        this.selectKeyValueMap = selectKeyValueMap;
    }

    @Override
    public String toString() {
        return "ExcleColumn{" +
                "fileName='" + fileName + '\'' +
                ", fileCode='" + fileCode + '\'' +
                ", threeListTreeFlag=" + threeListTreeFlag +
                ", superTreeName='" + superTreeName + '\'' +
                ", superTreeCode='" + superTreeCode + '\'' +
                ", fields=" + fields +
                ", selectKeyValueMap=" + selectKeyValueMap +
                '}';
    }
}
