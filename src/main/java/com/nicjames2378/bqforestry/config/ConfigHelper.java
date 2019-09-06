package com.nicjames2378.bqforestry.config;

import java.util.ArrayList;

class ConfigHelper {
    public enum BeeTypes {
        larvae("larvae"),
        drone("drone"),
        princess("princess"),
        queen("queen");

        private final String text;

        BeeTypes(final String text) {
            this.text = text;
        }

        public String get() {
            return text;
        }
    }

    static String[] getBeeTypes() {
        ArrayList<String> list = new ArrayList<>();
        for (BeeTypes a : BeeTypes.values()) {
            list.add(a.get());
        }

        return list.toArray(new String[0]);
    }
}
