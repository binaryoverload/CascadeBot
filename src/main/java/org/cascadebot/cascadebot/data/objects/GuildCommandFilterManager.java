package org.cascadebot.cascadebot.data.objects;

import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class GuildCommandFilterManager {

    private FilterMode filterMode = FilterMode.OFF;
    private final List<CommandFilter> commandFilters = new CopyOnWriteArrayList<>();

    public enum FilterMode {
        WHITELIST, BLACKLIST, OFF
    }

}
