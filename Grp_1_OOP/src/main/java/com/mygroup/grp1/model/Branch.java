package com.mygroup.grp1.model;

/**
 * Supported First Bank Uganda branches and their account-number prefixes.
 */
public enum Branch {
    KAMPALA("Kampala", "KLA"),
    GULU("Gulu", "GUL"),
    MBARARA("Mbarara", "MBR"),
    JINJA("Jinja", "JIN"),
    MBALE("Mbale", "MBL");

    private final String displayName;
    private final String code;

    Branch(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
