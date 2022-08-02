package com.code.jdk;


import java.util.ArrayList;
import java.util.List;

/**
 * @author haijun.kuang
 */
public class HeapOOMTest {

    static  class  OOMObject{}

    public static void main(String[] args) {
        List<OOMObject> list = new ArrayList<>();

        while (true){
            list.add(new OOMObject());
        }
    }

}
