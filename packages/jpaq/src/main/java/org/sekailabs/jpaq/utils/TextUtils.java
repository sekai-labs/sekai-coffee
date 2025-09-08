package org.sekailabs.jpaq.utils;

public class TextUtils {
    private TextUtils (){}
    public static String toCamelCase(String text, boolean isCapitalizeFirst) {
        String[] words = text.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (String s : words) {
            String word = s;
            if (isCapitalizeFirst && !word.isEmpty()) {
                word = Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            } else if (!word.isEmpty()) {
                word = word.toLowerCase();
            }
            builder.append(word);
        }
        return builder.toString();
    }

    public static String kebabToCamel(String kebabCaseString) {
        if (kebabCaseString == null || kebabCaseString.isEmpty()) {
            return kebabCaseString;
        }

        String[] parts = kebabCaseString.split("-");
        StringBuilder camelCaseString = new StringBuilder(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            camelCaseString.append(parts[i].substring(0, 1).toUpperCase());
            camelCaseString.append(parts[i].substring(1));
        }

        return camelCaseString.toString();
    }

    public static String camelToKebab(String camelCaseString) {
        if (camelCaseString == null || camelCaseString.isEmpty()) {
            return camelCaseString;
        }

        StringBuilder kebabCaseString = new StringBuilder();
        for (int i = 0; i < camelCaseString.length(); i++) {
            char c = camelCaseString.charAt(i);
            if (Character.isUpperCase(c)) {
                kebabCaseString.append('-');
                kebabCaseString.append(Character.toLowerCase(c));
            } else {
                kebabCaseString.append(c);
            }
        }

        return kebabCaseString.toString();
    }
}
