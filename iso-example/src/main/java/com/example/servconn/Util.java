package com.example.servconn;

import com.solab.iso8583.IsoMessage;

public class Util {

    public static void print(String title, IsoMessage m) {
        System.out.println(title);
        System.out.printf("TYPE: %04x\n", m.getType());
        for (int i = 2; i < 128; i++) {
            if (m.hasField(i)) {
                System.out.printf("F %3d(%s): %s -> '%s'\n", i, m.getField(i)
                        .getType(), m.getObjectValue(i), m.getField(i)
                        .toString());
            }
        }
    }

}
