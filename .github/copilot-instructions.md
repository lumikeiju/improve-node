<!-- @format -->

# Copilot Instructions for improvenode

## About This Project & Your Role as an AI Assistant

This is a **hobby learning project** to develop a JOSM (Java OpenStreetMap Editor) plugin. The goal is to learn JOSM plugin development through hands-on coding. Your role is to **guide and assist** the developer, not to take over the work:

- **Design discussions**: Help think through architecture decisions, trade-offs, and patterns
- **Code drafting**: Show examples or starter code, explain approaches
- **Code review**: Spot potential issues, suggest improvements, explain best practices
- **Explanation**: Help understand JOSM APIs, plugin patterns, and how existing code works
- **Testing & debugging**: Guide troubleshooting steps

**Let the developer do the actual coding work.** This accelerates learning and builds confidence.

## Project Overview

This is a **JOSM plugin** named **improvenode** (styled "ImproveNode"). The goal is to adapt the **ImproveWay** reference plugin to apply its accuracy-enhancement logic to **selected nodes** instead of ways. This is a hobby learning project designed to teach JOSM plugin development by extending an existing, well-documented plugin pattern.

The **ImproveWay plugin** (in `local-storage/improve-way/`) is the reference implementation that demonstrates the architecture and patterns we're building upon.

**Key Facts:**

- Java-based plugin for JOSM (requires JOSM 19044+)
- Single entry point: `ImproveNodePlugin` extends JOSM's Plugin class
- Main mode: `ImproveNodeAction` - adapted from ImproveWay's `ImproveWayAccuracyAction`
- Adapts logic from the ImproveWay reference plugin to work with **node selection** instead of way selection
- Uses Gradle build system (`build.gradle`, `gradle.properties`)
- Licensed under GPL

**Reference Material:**

- `local-storage/josm-docs-plugin-development-guide.md` - Authoritative JOSM plugin development guide
- `local-storage/improve-way/` - **Reference implementation** (ImproveWay plugin) that this project is based on; demonstrates architecture, patterns, and best practices we're adapting

## Architecture

### Core Components (Reference: ImproveWay)

The improvenode plugin will adapt these components from the ImproveWay reference implementation:

**ImproveWayPlugin.java** (entry point pattern)

- Plugin entry point registered in `build.gradle` as `mainClass`
- Only responsibility: when a map frame initializes, register the main action as a map mode via `addMapMode()`
- Pattern: Minimal plugin wrapper that defers to action class
- **Our implementation**: `ImproveNodePlugin`

**ImproveWayAccuracyAction.java** (~1000+ lines)

- Main map mode class - handles all UI rendering, mouse/keyboard events, and node manipulation
- Implements 5 interfaces: `MapMode`, `MapViewPaintable`, `DataSelectionListener`, `ModifierExListener`, `KeyPressReleaseListener`, `ExpertModeChangeListener`
- State machine with two states:
    - `State.selecting` - user selects **a way** to improve (will adapt to **selecting nodes**)
    - `State.improving` - user moves/adds/deletes nodes on the selected way (will adapt to operate on **selected nodes**)
- Tracks transient state: `targetWay`, `candidateNode`, `candidateSegment`, `mousePos`, `dragging`
- Uses JOSM Command pattern for undo/redo: `AddCommand`, `ChangeCommand`, `DeleteCommand`, `MoveCommand`, `SequenceCommand`
- **Our implementation**: `ImproveNodeAccuracyAction`, adapted to work with node selection instead of way selection

**ImproveWayAccuracyHelper.java** (utility pattern)

- Static utility class with no constructor (utils pattern)
- Key methods: `findWay()`, `findCandidateNode()`, `findSegmentForNewNode()`, etc.
- Purpose: Isolate geometric and spatial lookup logic from main action class
- **Our implementation**: `ImproveNodeAccuracyHelper`, adapted for node-based operations

### Key Patterns & Workflows

**Drawing & Rendering** (`ImproveWayAccuracyAction.paint()`)

- Implements `MapViewPaintable.paint(Graphics2D, MapView, Bounds)` to render overlays on map
- Conditional rendering based on state and modifier keys (Ctrl/Alt/Meta):
    - Selecting state: highlight target way with custom stroke
    - Improving state: draw preview lines connecting candidate node to adjacent nodes
- Uses JOSM's `GuiHelper.getCustomizedStroke()` to load stroke patterns from preferences

**Event Handling** - Modifier-based mode switching

- `Ctrl` key held: Add node mode (drawing preview between segment endpoints)
- `Alt` key held: Delete node mode (drawing line through node endpoints)
- Neither pressed: Move node mode (standard selection)
- Meta/Super key (`mod4`): Used for helper visualization on long press
- Implemented via `ModifierExListener.modifiersChanged()` and `KeyPressReleaseListener`

**Helper Visualization** (when expert mode + key held 250ms+)

- Arc visualization for turn angles
- Perpendicular distance lines
- Equal-angle circle helpers
- All configurable via JOSM preferences (stroke patterns, colors, dimensions in pixels)
- **Our adaptation**: Helper visuals will apply to selected nodes, with adapted geometry calculations

**Command Pattern** (Undo/Redo)

- All node mutations wrap in JOSM `Command` objects
- Move: `MoveCommand`
- Add: `AddCommand`
- Delete: `DeleteCommand`
- Multiple ops: `SequenceCommand` for atomic grouping

## Build & Development

**Authoritative Guide:** See `local-storage/josm-docs-plugin-development-guide.md` for official JOSM plugin development practices.

**Build System:** Gradle (`build.gradle`, `gradle.properties`)

- JOSM Gradle plugin: `id 'org.openstreetmap.josm' version '0.8.2'`
- Main class: `org.openstreetmap.josm.plugins.improvenode.ImproveNodePlugin`
- Output JAR: `build/dist/ImproveNode.jar`

> **Gradle version is pinned to 8.x.** The JOSM Gradle plugin 0.8.2 uses `Provider.forUseAtConfigurationTime()`, which was removed in Gradle 9. Do not upgrade the Gradle wrapper past 8.x until the JOSM Gradle plugin is updated. The Dependabot `gradle` ecosystem entry has been intentionally omitted for this reason.

**Key Gradle Properties (`gradle.properties`):**

```properties
version=0.1.0
```

**Plugin Lifecycle:** JOSM invokes `mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame)` when the map frame initializes. This is where plugins typically register UI components like map modes. See the guide for other lifecycle methods (preferences, download dialog, etc.).

**Preferences System** (`readPreferences()`)

- All visual styling loaded from JOSM preferences on mode initialization
- Stroke patterns: `improvenode.stroke.*` (e.g., "1 6" = 1px on, 6px off) - prefix adapted from ImproveWay's pattern
- Colors: Named color properties with defaults (e.g., `improve node helper line`)
- Dimensions: pixel-based sizes for arcs, circles, text offsets
- Access via `Config.getPref()` - see JOSM guide for property patterns like `IntegerProperty`

**Localization**

- Uses JOSM's i18n: `tr()`, `marktr()`, `trn()` from `org.openstreetmap.josm.tools.I18n`
- Reference: ImproveWay has 40+ language packs in `data/` folder (.lang files) - copy and adapt for ImproveNode

## Common Tasks

### Adding a New Helper Visual

1. Define color in `readPreferences()` as `NamedColorProperty`
2. Define stroke style if needed (e.g., dotted vs solid)
3. Add drawing logic in `paint()` method, conditioned on `helpersEnabled` flag
4. Adjust pixel dimension config vars (arc radius, etc.) in preferences setup

### Handling Modifier Key Logic

- Don't hardcode key constants - use JOSM's `Shortcut` registry
- Always check modifier state via listener pattern, not direct key polling
- Example: `ctrl` boolean flag updated via `ModifierExListener.modifiersChanged()`

### Moving from Selecting to Improving State

- **In ImproveWay**: Triggered in `selectionChanged()` when user clicks target way
- **In our adaptation**: Will trigger when user selects one or more nodes (via `DataSelectionListener.selectionChanged()`)
- Call `updateStateByCurrentSelection()` to transition state machine
- In improving state, mouse position updates trigger `getNewPointEN()` to compute preview

### Commands & Undo

- Always wrap mutations in Command objects retrieved from `UndoRedoHandler.getInstance().add()`
- Use `SequenceCommand` if multiple atomic operations needed
- Never directly modify way/node objects without command

## Learning Workflow for the Developer

**When you're ready to work on a feature:**

1. **Start with design**: Discuss the feature goal and sketch out approach with AI
2. **Read reference code**: Look at `local-storage/improve-way/` for working examples
3. **Check the JOSM guide**: Review `local-storage/josm-docs-plugin-development-guide.txt` for API details
4. **Write the code**: Implement the feature, ask questions as you go
5. **Code review**: Have AI review for JOSM patterns, edge cases, and improvements
6. **Iterate**: Refine based on feedback and test in JOSM

**Red flags to watch for:**

- Hardcoded coordinates or magic numbers → use JOSM preference system
- Direct object modification → wrap in Commands for undo/redo
- Catching all exceptions silently → use JOSM's Logging
- Ignoring coordinate systems → distinguish EastNorth vs screen Point

## External Dependencies

- JOSM API: `org.openstreetmap.josm.*` (MapMode, DataSet, Way, Node, Command classes)
- Java AWT/Swing: Graphics2D, cursors, fonts, event handling
- No external third-party libraries

## Debugging Tips

- State transitions: check `state` variable via debug breakpoint in `updateStateByCurrentSelection()`
- Coordinate systems: JOSM uses `EastNorth` (map projection) and screen `Point` - conversion via `MapView.getPoint(EastNorth)`
- Preference values: set in JOSM's preferences dialog or edit JOSM config XML directly
- Selection changes: `DataSelectionListener.selectionChanged()` is the hook for way/node selection events
