<p align="center">
  <img src="branding/screenshot.png" height="400">
</p>

<h1 align="center">Open Flood</h1>

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.gunshippenguin.openflood">
      <img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" height="40"/>
  </a>
  <a href="https://f-droid.org/repository/browse/?fdid=com.gunshippenguin.openflood">
      <img alt="Get Open Flood on F-Droid" src="https://f-droid.org/wiki/images/c/c4/F-Droid-button_available-on.png" height="40"/>
  </a>
</p>

A flood fill puzzle game for Android.

## Gameplay

Starting with the upper left hand corner of the board fill adjacent 
cells with that color by tapping the colored buttons. The aim of the game
is to try to fill the entire board with a single color.

You can generate a board from a seed by holding down the new game icon.

## Building / Installing

Build and install using Android Studio or from the command line by executing the
following commands.

To build and install on \*nix:

```
./gradlew installDebug
```

To build on Windows:

```
gradlew.bat installDebug
```

To build without installing, use the `assembleDebug` task instead. This will
output a debug apk in `app/build/outputs/apk/debug`

## License

[GPL 3.0+](https://github.com/GunshipPenguin/open_flood/blob/master/LICENSE.md)
© 

Fonts are Licensed under the [SIL Open Font License](https://github.com/ThePreviousOne/open_flood/blob/master/OFL.txt)

