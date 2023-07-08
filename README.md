# RoleplAI
A lightweight tool adding some commands to modify entity behavior. Intended for use in roleplay servers. Works well with datapacks / command blocks. 

## Commands
- `/roleplai` - Main command (alias `/rai`)
- `/roleplai <source entity> goto <target position> [speed] [force]` - Make source entity pathfind to target position at given speed (default 1). If speed is set too high, the entity might run in circles. If force is set to true, the entity will continuously go to that position until given a new directive. Otherwise, entity might get distracted along the way.
- `/roleplai <source entity> attack <target entity> [force]` - Make source entity attack target entity. Will only work if source entity is capable of attacking entities - if not, source entity will just walk into target entity. If force is set to true, the entity will continuously be made to attack entity until given a new directive, or until target entity is killed. Otherwise, the entity might get distracted along the way.
- `/roleplai <source entity> follow <target entity> [range] [speed]` - Make source entity follow target entity at given range (default 0) and given speed (default 1). Once source entity is in range, it will stop and act according to default behavior until out of range.
- `/roleplai <source entity> actas <entity type>` - Gives source entity the behavior of the given entity type (via DisguiseLib). Usually works well, but strange interactions exist.
- `/roleplai <source entity> reset [behavior|goals]` - resets the behavior (actas) or goals (goto/attack/follow) of source entity. If not specified, resets both. 
