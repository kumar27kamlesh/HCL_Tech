package com.hcl.ewallet.user.enums;

import java.util.Arrays;

public enum CurrencyType {
        INR("INR", "Indian Rupee"),
        USD("USD", "US Dollar"),
        EUR("EUR", "Euro"),
        GBP("GBP", "British Pound");

        private final String code;
        private final String description;

        CurrencyType(String code, String description) {
                this.code = code;
                this.description = description;
        }

        public String getCode() {
                return code;
        }

        public String getDescription() {
                return description;
        }

        public static CurrencyType fromCode(String code) {
                return Arrays.stream(values())
                        .filter(c -> c.code.equalsIgnoreCase(code))
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalArgumentException("Unsupported currency: " + code));
        }
}
