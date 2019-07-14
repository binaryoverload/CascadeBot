/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.informational;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.Constants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.language.LanguageUtils;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;

import java.util.ArrayList;
import java.util.List;

import org.cascadebot.shared.SecurityLevel;

public class UserInfoCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        User userForInfo = sender.getUser();
        if (context.getArgs().length > 0) {
            userForInfo = DiscordUtils.getUser(context.getGuild(), context.getMessage(0), true);
        }
        if (userForInfo == null) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.invalid_user"));
            return;
        }
        Member member = context.getGuild().getMember(userForInfo);
        String status = "";
        String statusName = "";
        String footer = "Requested by " + sender.getUser().getAsTag();
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();

        if (member != null) {
            builder.addField(context.i18n("commands.userinfo.user_join_date"), FormatUtils.formatDateTime(member.getJoinDate()), true);
            statusName = LanguageUtils.getEnumI18n(context.getLocale(), "statuses", member.getOnlineStatus());
            if (member.getGame() != null && member.getGame().getType() == Game.GameType.STREAMING) {
                status = context.globalEmote("streaming");
                statusName = context.i18n("statuses.streaming");
            } else if (member.getOnlineStatus() == OnlineStatus.ONLINE) {
                status = context.globalEmote("online");
            } else if (member.getOnlineStatus() == OnlineStatus.OFFLINE) {
                status = context.globalEmote("offline");
            } else if (member.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB) {
                status = context.globalEmote("dnd");
            } else if (member.getOnlineStatus() == OnlineStatus.IDLE) {
                status = context.globalEmote("idle");
            }
        }

        List<Page> pageList = new ArrayList<>();

        builder.setTitle(userForInfo.getAsTag());
        builder.setThumbnail(userForInfo.getAvatarUrl());
        builder.addField(context.i18n("commands.userinfo.user_created"), FormatUtils.formatDateTime(userForInfo.getCreationTime()), true);
        builder.addField(context.i18n("commands.userinfo.user_id"), userForInfo.getId(), true);
        builder.addField(context.i18n("commands.userinfo.user_mutual_servers"), String.valueOf(userForInfo.getMutualGuilds().size()), true);
        builder.setFooter(footer, sender.getUser().getEffectiveAvatarUrl());
        builder.setColor(Constants.COLOR_INFO);
        pageList.add(new PageObjects.EmbedPage(builder, false));
        long userId = userForInfo.getIdLong();
        SecurityLevel userSecurityLevel = CascadeBot.INS.getPermissionsManager().getUserSecurityLevel(userId);
        if (userSecurityLevel != null) {
            builder.addField(context.i18n("commands.userinfo.user_official_role"), FormatUtils.formatEnum(userSecurityLevel), true);
        }

        if (member != null) {
            builder.addField("Status", status + statusName, true);
            Game game = member.getGame();

            if (game != null) {
                String gameStatus;
                String gameType = LanguageUtils.getEnumI18n(context.getLocale(), "game_types", game.getType());
                if (game.isRich()) {
                    RichPresence presence = game.asRichPresence();
                    gameStatus = String.format("%s **%s**", gameType, presence.getName());
                    if (presence.getDetails() != null) gameStatus += "\n*" + presence.getDetails() + "*";
                    if (presence.getState() != null) gameStatus += "\n*" + presence.getState() + "*";
                } else {
                    gameStatus = String.format("%s **%s**", gameType, game.getName());
                }
                builder.addField(context.i18n("commands.userinfo.activity"), gameStatus, true);
            }

            StringBuilder stringer = new StringBuilder();
            String author = "Roles for: " + sender.getUser().getAsTag();
            EmbedBuilder embed = new EmbedBuilder().setAuthor(author).setFooter(footer, sender.getUser().getEffectiveAvatarUrl()).setColor(Constants.COLOR_INFO);
            int i = 1;
            for (Role role : member.getRoles()) {
                if (i % 20 == 0) {
                    pageList.add(new PageObjects.EmbedPage(
                            new EmbedBuilder().setDescription(stringer.toString()).setColor(Constants.COLOR_INFO).setAuthor(author).setFooter(footer, sender.getUser().getEffectiveAvatarUrl()),
                            false));
                    stringer = new StringBuilder();
                }
                stringer.append("<@&" + role.getId() + ">" + " ID: " + role.getId() + "\n");
                i = i + 1;
            }
            pageList.add(new PageObjects.EmbedPage(embed.setDescription(stringer.toString()), false));

        }
        context.getUIMessaging().sendPagedMessage(pageList);
    }

    @Override
    public String command() {
        return "userinfo";
    }

    @Override
    public Module getModule() {
        return Module.INFORMATIONAL;
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("userinfo", true);
    }

}
