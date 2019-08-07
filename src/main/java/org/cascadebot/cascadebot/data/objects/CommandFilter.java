package org.cascadebot.cascadebot.data.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@AllArgsConstructor
@Getter
public class CommandFilter {

    private final FilterTarget target;
    private final String targetId;
    private final FilterType type;
    private final CommandFilter parent;
    private final List<CommandFilter> subFilters = new CopyOnWriteArrayList<>();

    public boolean addSubFilter(CommandFilter filter) {
        if (!target.getAllowedSubFilters().contains(filter.getTarget())) {
            throw new IllegalStateException(
                    String.format(
                            "The filter with target type %s cannot be added to a filter with target type %s!",
                            filter.getTarget(),
                            target
                    )
            );
        }
        return subFilters.add(filter);
    }

    public void addSubFilter(int position, CommandFilter filter) {
        if (!target.getAllowedSubFilters().contains(filter.getTarget())) {
            throw new IllegalStateException(
                    String.format(
                            "The filter with target type %s cannot be added to a filter with target type %s!",
                            filter.getTarget(),
                            target
                    )
            );
        }
        subFilters.add(position, filter);
    }

    public boolean removeSubFilter(CommandFilter filter) {
        return subFilters.remove(filter);
    }

    public CommandFilter removeSubFilter(int position) {
        return subFilters.remove(position);
    }

    public List<CommandFilter> getSubFilters() {
        return List.copyOf(subFilters);
    }

    public enum FilterTarget {

        COMMAND,
        USER(COMMAND),
        ROLE(COMMAND),
        CHANNEL(ROLE, USER, COMMAND);

        @Getter
        private final EnumSet<FilterTarget> allowedSubFilters;

        FilterTarget(FilterTarget... allowedSubFilters) {
            this.allowedSubFilters = EnumSet.copyOf(Set.of(allowedSubFilters));
        }

    }

}
