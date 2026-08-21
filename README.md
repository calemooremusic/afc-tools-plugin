# AFC Tools

A RuneLite plugin combining the tools an Agility FC (Wilderness Agility Course
mass) runner needs into one plugin: gear/supply checklist, tile markers,
entity hiding, and a personal PK fight log.

Requirements are sourced from https://oldschool.runescape.wiki/w/Guide:Agility_FC

## Status

- [x] Plugin shell (compiles, loads, config registered)
- [x] Gear/supply checklist module (inventory + equipment aware overlay)
- [x] Personal PK fight log module (your own fights only - see PvP Restrictions below)
- [ ] Tile marker module
- [ ] Entity hider module

## PK Log module - how it works

- Listens for `HitsplatApplied` and `ActorDeath` events.
- **Outgoing damage** (you hit/kill someone): uses `Hitsplat.isMine()`, which
  RuneLite sets reliably - this direction is accurate.
- **Incoming damage** (someone hits/kills you): RuneLite doesn't expose
  "who caused this hitsplat" directly. We track `InteractingChanged` to see
  which player is currently targeting you, and attribute the next incoming
  hit to them. This is a best-effort heuristic, not guaranteed-correct -
  in chaotic multi-attacker fights (fall-ins, multicombat south of the
  course) it can occasionally mis-attribute a hit. Good enough for "who's
  on me" awareness; don't treat it as forensic proof.
- Everything is stored **only in memory on your own client** and shown in a
  local overlay. There's an optional "print to chat" toggle so entries are
  easy to read/copy, but nothing is ever transmitted automatically to
  clanmates or a server - that would cross into "PvP clan opponent
  identification," which is banned by RuneLite's plugin rules. If you want
  the FC to know, you relay it yourself (voice/Discord) same as today, just
  reading off a clean log instead of scrollback.

## ⚠️ Before you build: verify the item IDs

`src/main/java/com/afctools/checklist/ChecklistRequirements.java` has
placeholder `-1` item IDs with comments showing the intended
`net.runelite.api.gameval.ItemID` constant name. These were written from
memory and are **not verified**. Once the project builds in IntelliJ:

1. Open `ChecklistRequirements.java`
2. For each `Set.of(-1)`, use autocomplete on `ItemID.` to find the real
   constant and replace the placeholder
3. Some rows need multiple IDs (e.g. all 4 d'hide body colours) - add them
   all to the `Set.of(...)`

## Local setup

1. Install **JDK 11** (Adoptium Temurin recommended - must be 11, not newer)
2. Install **IntelliJ IDEA** (Community Edition is fine)
3. Open this folder in IntelliJ as a Gradle project, let it sync
4. Set Project SDK to JDK 11 (File > Project Structure > Project)
5. Run `./gradlew run` (or use IntelliJ's Gradle panel > Tasks > run) to
   launch a real RuneLite client with this plugin loaded in developer mode
6. Log in with a Jagex account in dev mode - see
   https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts

## Plugin Hub rules to keep in mind

This project follows RuneLite's plugin rules (see `AGENTS.md`). The two
modules most likely to bump into restrictions:

- **PK log**: must only track *your own* fights (your damage dealt/taken,
  win/loss) - no opponent identification, gear scouting, or "who's
  attackable" summaries. Those are explicitly banned.
- **Entity hider**: fine as long as it's hiding visual clutter (overhead
  text, hitsplats) rather than doing anything that changes click zones or
  hides things Jagex requires to stay visible.

## License

BSD 2-Clause (matches the RuneLite plugin template)
