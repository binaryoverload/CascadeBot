package org.cascadebot.cascadebot.data.objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.data.language.Locale;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class CommandFilter {

    private FilterTarget target;
    private String targetId;
    private CommandFilter parent;
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

    public boolean evaluateCommandFilter(Locale locale, ICommandExecutable command, Member member, TextChannel channel) {
        boolean filtered = false;
        switch (target) {
            case COMMAND:
                filtered = command.command().equals(targetId) || command.command(locale).equals(targetId);
                break;
            case USER:
                filtered = member.getUser().getId().equals(targetId);
                break;
            case ROLE:
                filtered = member.getRoles().stream().anyMatch(role -> role.getId().equals(targetId));
                break;
            case CHANNEL:
                filtered = channel.getId().equals(targetId);
                break;
        }
        return filtered && subFilters.stream().anyMatch(filter -> filter.evaluateCommandFilter(locale, command, member, channel));
    }

    public List<CommandFilter> getSubFilters() {
        return List.copyOf(subFilters);
    }

    public enum FilterTarget {

        USER,
        ROLE,
        COMMAND(USER, ROLE),
        CHANNEL(COMMAND, USER, ROLE);

        @Getter
        private final EnumSet<FilterTarget> allowedSubFilters;

        FilterTarget(FilterTarget... allowedSubFilters) {
            this.allowedSubFilters = EnumSet.copyOf(Set.of(allowedSubFilters));
        }

    }

}
