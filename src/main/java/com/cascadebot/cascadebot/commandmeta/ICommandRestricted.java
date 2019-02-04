/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import com.cascadebot.cascadebot.permissions.Permission;
import com.cascadebot.shared.SecurityLevel;

public interface ICommandRestricted extends ICommandMain {

    default SecurityLevel getCommandLevel() {
        return SecurityLevel.STAFF;
    }

    @Override
    default Permission getPermission() {
        return null; // Since these cannot be run by normal guilds, this cannot have a permission
    }

}
