![TonicFoods Banner](Images/Banner.jpeg?raw=true)
The is the GitHub repo for TonicFoods, the worlds most lightweight custom food plugin for Minecraft.

# API
There ain't much to the API. There are 2 functions:
- giveFood(Player, Food (as string), Amount) --> Gives a player food
- getFoods() --> returns a list of all valid food names

There are also 2 events:
- TonicFoodEatEvent --> Fired when a player eats the food and right before the effects take place (can cancel)
- TonicFoodGiveEvent --> Fired whenever a player obtains food from TonicFoods (can cancel + get string of the food)

The API isn't perfect, but it's honest work.

# Spigot
TonicFoods supports 1.16 and up, and has more examples + docs on Spigot.
Please check out the [Spigot Page](https://www.spigotmc.org/resources/tonicfoods.72274/) for full documentation.

# License
This project is licensed under GNU GPL-3.0 (General Public License)
- You are not to sell this plugin, regardless of your modifications.
- You are able welcomed to distribute this resource or any modification thereof on any platform for free.
- If you do make modifications and redistribute you must disclose the modifications made and link back to the original resource in some way.
- All redistributions or modifications are to automatically be open-source and follow this license and it's core principles.
- Any software made for public use must be open-source and should be made with good intentions.

# Contact Me
If you have any questions or inquiries, you can reach me at https://zachduda.com/contact


# Metrics
[![TonicFoods bStats Graph](https://bstats.org/signatures/bukkit/TonicFoods.svg)](https://bstats.org/plugin/bukkit/TonicFoods/6192)
