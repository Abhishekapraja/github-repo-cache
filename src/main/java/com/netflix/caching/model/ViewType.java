package com.netflix.caching.model;

/**
 * Enum to define all types of views supported
 */
public enum ViewType {
    STARS("stars"),
    OPEN_ISSUES("open_issues"),
    LAST_UPDATED("last_updated"),
    FORKS("forks");

    public final String label;

    ViewType(String label) {
        this.label = label;
    }

    public static ViewType valueOfLabel(String label) {
        for (ViewType e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Undefined view for given value : " + label);
    }
}
