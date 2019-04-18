/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes;

import com.google.common.collect.Comparators;
import com.google.common.collect.Sets;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class VoteButtonGroup extends ButtonGroup {

    private Map<Long, Object> votes = new HashMap<>();

    private BiConsumer<List<VoteResult>, Message> periodicConsumer;

    private Set<Long> allowedUsers = Sets.newConcurrentHashSet();

    private Timer timer = new Timer();

    private Timer voteTimer;

    VoteButtonGroup(long ownerId, long channelId, long guildId, BiConsumer<List<VoteResult>, Message> periodicRunnable, Timer voteTimer) {
        super(ownerId, channelId, guildId);
        this.periodicConsumer = periodicRunnable;
        if (periodicRunnable != null) {
            setUpVoteProcessConsumer();
        }
        this.voteTimer = voteTimer;
    }

    private void setUpVoteProcessConsumer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CascadeBot.INS.getShardManager().getGuildById(getGuildId()).getTextChannelById(getChannelId()).getMessageById(getMessageId()).queue(message -> {
                    periodicConsumer.accept(getOrderedVoteResults(), message);
                });
            }
        }, 5000, 5000);
    }

    public void addVote(User user, Object vote) {
        if (votes.containsKey(user.getIdLong())) {
            if (votes.get(user.getIdLong()).equals(vote)) {
                votes.remove(user.getIdLong());
                return;
            }
        }
        votes.put(user.getIdLong(), vote);
    }

    public Map<Long, Object> getVotes() {
        return votes;
    }

    public List<VoteResult> getOrderedVoteResults() {
        Map<Object, Integer> countMap = new HashMap<>();
        for (Object key : getVotes().values()) {
            if (countMap.containsKey(key)) {
                countMap.put(key, countMap.get(key) + 1);
            } else {
                countMap.put(key, 1);
            }
        }

        // Sorts all counts from largest to smallest value
        return countMap.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(entry -> new VoteResult(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
    }

    public Set<Long> getAllowedUsers() {
        return Set.copyOf(allowedUsers);
    }

    public boolean isUserAllowed(long userId) {
        return allowedUsers.contains(userId);
    }

    public boolean allowUser(long userId) {
        return allowedUsers.add(userId);
    }

    public boolean denyUser(long userId) {
        return allowedUsers.remove(userId);
    }

    public void stopVote() {
        voteTimer.cancel();
        timer.cancel();
    }

    void voteFinished() {
        timer.cancel();
    }

}
