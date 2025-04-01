package com.gradle.develocity;

final class ServiceMessage {

    private static final String SERVICE_MESSAGE_START = "##teamcity[";
    private static final String SERVICE_MESSAGE_END = "]";

    private final String name;
    private final String argument;

    private ServiceMessage(String name, String argument) {
        this.name = name;
        this.argument = argument;
    }

    static ServiceMessage of(String name, String argument) {
        return new ServiceMessage(name, argument);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SERVICE_MESSAGE_START);
        sb.append(name);
        sb.append(' ');
        sb.append('\'');
        sb.append(escape(argument));
        sb.append('\'');
        sb.append(SERVICE_MESSAGE_END);
        return sb.toString();
    }

    private String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            sb.append(escape(c));
        }
        return sb.toString();
    }

    private String escape(char c) {
        String escapeCharacter = "|";
        switch (c) {
            case '\n':
                return escapeCharacter + "n";
            case '\r':
                return escapeCharacter + "r";
            case '|':
                return escapeCharacter + "|";
            case '\'':
                return escapeCharacter + "\'";
            case '[':
                return escapeCharacter + "[";
            case ']':
                return escapeCharacter + "]";
            default:
                return c < 128 ? Character.toString(c) : escapeCharacter + String.format("0x%04x", (int) c);
        }
    }

}
