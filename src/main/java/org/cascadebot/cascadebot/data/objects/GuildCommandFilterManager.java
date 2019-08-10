package org.cascadebot.cascadebot.data.objects;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuildCommandFilterManager {

    @Getter
    @Setter
    private FilterMode filterMode = FilterMode.OFF;
    private final List<CommandFilter> commandFilters = new CopyOnWriteArrayList<>();

    public enum FilterMode {
        WHITELIST, BLACKLIST, OFF
    }

    public List<CommandFilter> getCommandFilters() {
        return List.copyOf(commandFilters);
    }

    public void addFilter(CommandFilter filter) {
        commandFilters.add(filter);
    }

    public boolean removeFilter(CommandFilter filter) {
        return commandFilters.remove(filter);
    }

    public CommandFilter removeFilter(int index) {
        return commandFilters.remove(index);
    }

}
