package com.sqs.panel.treedemo;

public class Profession{
    String code;  //专业代码
    String name;//专业名称
    String type;//文件类型

    public Profession(String code, String name, String type) {
        this.code = code;
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    //重写方法
    public String toString() {
        return name;
    }
}
