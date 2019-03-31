# NotABadPlayer-Android

Created: 2019 March

Android's default music player sucks so much that I remade it with some extra features.

Platform: Android SDK 14+ (Android 4.0 IceCream Sandwich)

Usage: Media player

Technologies: Android SDK 21, Android's MediaStore, Guava (com.google.guava:guava:26.0-android)

Architectural design:

* MVP (Model=data, View=interface, Presenter=data/interface bridge, state controller)

* App consists of multiple activities and fragments, the main activity is seperate from the launch activity (launch only asks for read/write permissions), activities are forced to rely on singletons in order to their job. Its complete nonsense, single activity apps are better, there should be no need to rely on singletons to make your application work properly

* Activities communicate trough serialization objects (Java Base64 serialization style)

* Every view subclasses a BaseView interface (with one exception, CreatePlaylistActivity, because it has only one possible action)

* Every BaseView has a BasePresenter, a presenter which holds the model, makes the decision making

* The views are always responsible for UI navigation

* Upon UI interaction, views alert their presenters, which perform some logic based on the input

* BaseView and BasePresenter have a lot of duplicate methods - view usually has "performAction" type of methods,
  presenter has "onEvent" type of methods, it looks messy, but surprisingly it gets the job done, UI is seperated from the logic
  
* So many empty interface methods of BaseView and BasePresenter... extremely slow and boring to write

Design:

* CPU and energy efficient, memory ineffecient since the audio information is retrieved once and reused when trying to use the audio player.

* Virtually no exceptions are thrown, the try-catch blocks usually just print errors/warnings to log

* Storage is super simple, save and retrieve, no cache is used

* Media storage is cached for CPU/energy efficiency

* Always single process app, if you open it from another Android app it opens a new window instead of adding an activity to the stack of the caller app

* Supports one orientation only: portrait, onCreate is almost never called twice

# Features

Bind all kinds of user actions like making the next/previous buttons jump backwards and forwards.

3 app themes, different sorting options,optional volume bar on the player screen.

CPU & energy effecient, but memory inefficient (takes about 45MB RAM average).

Includes standart player features like creating playlists, searching for tracks, controlling the audio player even when not on the player screen (a quick player is available, attached to the bottom of the screen).

Includes slighty more fancy features like jumping back to the previously played song, regardless to which album or list it belonged to.

Portrait mode only.

# Screens

Albums screen (quick screen at the bottom)

![alt text](https://github.com/felixisto/NotABadPlayer-Android/blob/master/About/1.jpg)

Player screen

![alt text](https://github.com/felixisto/NotABadPlayer-Android/blob/master/About/2.jpg)

Playlist screen

![alt text](https://github.com/felixisto/NotABadPlayer-Android/blob/master/About/3.jpg)

Search screen

![alt text](https://github.com/felixisto/NotABadPlayer-Android/blob/master/About/4.jpg)

Settings screen - keybind options

![alt text](https://github.com/felixisto/NotABadPlayer-Android/blob/master/About/8.jpg)

Dark app theme

![alt text](https://github.com/felixisto/NotABadPlayer-Android/blob/master/About/7.jpg)
