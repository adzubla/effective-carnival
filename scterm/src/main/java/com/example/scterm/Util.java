package com.example.scterm;

import com.solab.iso8583.IsoMessage;

public class Util {

    public static String describe(IsoMessage m) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("TYPE: %04x\n", m.getType()));
        for (int i = 2; i < 128; i++) {
            if (m.hasField(i)) {
                sb.append(String.format("F %3d(%s): '%s' -> %s\n", i, m.getField(i).getType(), m.getField(i).toString(),
                        m.getObjectValue(i)));
            }
        }
        return sb.toString();
    }

}
