package com.cascadebot.cascadebot.commands;

public enum CommandType {

    CORE,
    DEVELOPER(false);

    private boolean availableModule;

    CommandType() {
        this.availableModule = true;
    }

    CommandType(boolean availableModule) {
        this.availableModule = availableModule;
    }

    public boolean isAvailableModule() {
        return availableModule;
    }
}