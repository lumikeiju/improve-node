<!-- @format -->

# ImproveNode

JOSM plugin for efficiently improving standalone nodes.

## Usage

Enable **Improve Node** mode, then click the corrected imagery position for a highlighted standalone node. The plugin moves only nodes that are not members of any parent way, so it is suited for imported point datasets such as light poles, trees, or utility assets.

- Move near a standalone node to highlight it, then click where it should be moved.
- Select one or more standalone nodes to limit the mode to that selection.
- Hold `Shift` to keep the current highlighted node locked while aiming.
- Hold `Ctrl` and click to add a new standalone node.
- Hold `Alt` and click to delete the highlighted standalone node if it is untagged.

## Build

Build with the Gradle wrapper:

```powershell
.\gradlew.bat clean build
```

The development plugin update site is generated at:

```text
build/localDist/list
```

Add that file URL as a JOSM plugin update site in expert mode to load `ImproveNode-dev` into a local JOSM instance.

## Credits

[ImproveWayAccuracy](https://josm.openstreetmap.de/wiki/Help/Action/ImproveWayAccuracy) by [Alexander Kachkaev](https://github.com/kachkaev).

[ImproveWay](https://github.com/JOSM/improve-way) by [András Kolesár](https://github.com/kolesar-andras).

[ImproveNode](https://github.com/Lumikeiju/improve-node) by [Amy Bordenave](https://github.com/Lumikeiju).
