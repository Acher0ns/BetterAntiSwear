# BetterAntiSwear ![Downloads](https://img.shields.io/github/downloads/Acher0ns/BetterAntiSwear/total)

[![Support via PayPal](https://cdn.rawgit.com/twolfson/paypal-github-button/1.0.0/dist/button.svg)](https://www.paypal.me/kamroncole/)

Minecraft Spigot Plugin to prevent players from swearing in in-game chat.

Improvement on [Advanced AntiSwear](https://www.spigotmc.org/resources/advanced-antiswear.16354/) Spigot plugin by [Brooky1010](https://www.spigotmc.org/resources/authors/brooky1010.28606/)

#### I want to make it clear, a majority of the work done here/source code is by [Brooky1010](https://www.spigotmc.org/resources/authors/brooky1010.28606/). All I did was make improvements at a friends request. I highly recommend checking out the original author.


# Features:
+ Custom blacklisted Word List
+ Custom whitelisted Word List
+ Ability to block custom phrases (or words with spaces)
+ Autohandles leet variations of blacklisted words
+ Custom Swear Message
+ Take money from players when they swear. (Requires Vault)
+ Custom Message + Prefix
+ Permissions
+ Config Reload Command
+ Kick players when they swear for an amount of times
+ Show all the blocked words in-game.
+ You can use the variable %player% in the kick & warn message to show the player name.
+ You can hurt players when they swear!
+ You can mute the whole chat at once!
+ Built in chat clear module!
+ Moderator warning
+ Custom Sound Module (Sound list here)
+ Custom Built-In Actionbar
+ Custom Hologram Popup (Requires HoloGramAPI)
+ Console Command Support
+ Check how many times a player has sworn


# Commands:
- /antiswear - Shows author information. (No Permission)
- /as help - Shows commands & help. (No Permission)
- /as reload - Reloads the config. (antiswear.reload)
- /as message - Sets the swear message (antiswear.manage)
- /as prefix - Sets the prefix (antiswear.manage)
- /as kickmessage - Sets the message that will be displayed if kicking is enabled in the config. (antiswear.manage)
- /as info - Shows all the recent info (antiswear.manage)
- /as kick - Kicks a player from the server instantly (antiswear.manage)
- /as add - Adds a word from in-game to the config. (antiswear.manage)
- /as remove - Removes a word from the config. (antiswear.manage)
- /as toggle - Mutes or unmutes the chat. (antiswear.toggle)
- /as cc - Clears the chat for everyone. (antiswear.cc)
- /as debug - Outputs useful information to console when reporting a bug. (antiswear.manage)
- /as check - Check for updates (antiswear.manage)
- /as count - Check how many times a player cursed.


# Permissions:
- antiswear.* - Gives access to all the permissions.
- antiswear.bypass - Be able to talk when the chat is muted.
- antiswear.manage - Manages the messages.
- antiswear.mod - Receive the message if players tried to swear.
- antiswear.reload - Reload the BetterAntiSwear config.
- antiswear.toggle - Be able toggle between global mute and unmute.


# Addon Plugins:
HologramAPI: [Click here](https://www.spigotmc.org/resources/hologramapi.21286/)

Vault: [Click here](https://www.spigotmc.org/resources/vault.34315/)


# FAQ:
Q: All the green text in my config is gone.

A: That is caused by adding words or modifying the config from in-game.


Q: I edited my sound effect and now the plugin doesn't work anymore.

A: You added an invalid sound effect. Get a full list here.


Q: The plugin isn't working.

A: Make sure you have the latest Spigot installed and you are running Java 8.


# Changelog:
### 4/16/2021 v1.2:
 - Can now add/remove phrases to config lists from command line
 - Fix detection formatting
 - Properly make config lists blank
 - Add required API version (1.16)
 - Lower java compiler version to increase compatability

### 4/15/2021 v1.1:
 - Re-add update check
 - Broadcast what in the blacklist was detected to mods
 - Send message letting user know config reloaded after word add/removed
 - Remove redundant code
 - no longer provides a default list of blacklisted/whitelisted words in the config


### 4/14/2021 v1.0:
 - Made swear filter more comprehensive, instead of searching a message word by word (and rebuilding the message letter by letter to filter the message), now block a message if it contains a blacklisted word.
 - Add a list of whitelisted words to the config to prevent false positives (detecting 'ass' in 'grass'). I believe this is the better approach to blocking swear words as creating a list one whitelisted words is easier than adding all combinations of ways to get arround the original message (going word by word does not block "yyFUCKyy", or combinations thereof, assuming they it not on the blacklist)
 - Add ability to block phrases or words with spaces in them. For Example "f u c k" could not and would not be blocked by the original plugin
 - Add ability to auto handle most leet variations of words
 - Add ability to auto handle most attempts to bypass the swear filter by using special characters as spaces
 - Add ability to add/remove words from the whitelist
 - Add ability to check how many times a player has sworn
 - Various code organization and performance improvements
 - Remove metric tracking, for obvious reasons
 - Remove update check feature (for now), for obvious reasons
