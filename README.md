--- 
### moove
---

**components and ui package**

In the moove/src, the package components and ui has everything the user interacts with on the screen. StatCard is a visual box that shows a statistic. The FormDialog and InventoryDialogs are pop-up windows like when you click to add a new block of cheese to the inventory, this appears with text boxes to type in. The ui holds these smaller components together.

**model package**

The package model has the "actors" of this management and has the information structure for these classes. These are the inventory items, the manager, and the clerk. A cheese, for example, like other products, has id, name, description, price, quantity, shelfLifeDays, etc.Other classes that are not necessarily actors but holds data are also found here. The CashWithdrawal class tracks money moving out. It had fields like amount and purpose.

**service package and the moove folder**

The service package contains classes that reaches out to the data folder to load, save, or update information. The moove folder in the project has "data" that contains the csv files/database which contains inventory, transactions, product flow, etc. A service class reads this and bring them to the app. The moove folder also has "resources" folder that contains the css(has css file that controls the colors, fonts, button sizes, themes, etc.), fonts(ttf files of text styles), and images(icons, logos) folder. 

**exception package**

The package exception contains Exception subclasses that uses Java's error handling mechanism. The AgriException, StorageException, and PermissionException alarms for specific failures like a database storage error, calculation rule failing, or a user trying to click a button they aren't allowed to use. The InsufficientFundsException is an alarm for trying to withdraw money or buy something when digital wallet is empty.The AuthenticationException is thrown for wrong password and ValidationException when letters are typed into a text box that only accepts numbers.
