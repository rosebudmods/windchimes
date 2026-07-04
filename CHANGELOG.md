### ☀️ new
- direction aware redstone activation
- chimes give off comparator signal when ringing
- picking up string grants all chime recipe book unlocks
- swing animation in opposite direction to where player clicked from in the angle they are looking
- platform swings in tandem with rods
- add interaction cooldown per chime to prevent interaction spam

### ⚙️ changes
- add seeds and animation tracking to create more randomized but network-synced interaction animations
- slight model edits for rod & clapper heights
- randomized rod heights based on blockPos
- simplify some animation maths
- simplify difference approach for thunder/rain/clear day/night


### 🛠️ fixes
- lerping and tweening for better framerate on animations and transition between ambient and interaction
- only swing arm on off-cooldown interaction
- moved some sound & animation code to be server-authoritative