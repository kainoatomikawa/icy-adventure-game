# Icy Adventure Game

Icy Adventure Game is a 2D, tile-based adventure game built in Java that focuses on **procedural world generation, state management, and constrained rendering**. The game challenges players to navigate a randomly generated map while managing health and visibility under environmental constraints.

The project emphasizes clean architecture, modular design, and deterministic world generation.

## Key Features
- **Procedural World Generation**  
  Generates a unique map per run using a seed, composed of non-overlapping rooms connected by hallways.

- **Deterministic Saves & Reloading**  
  Game state can be saved and reloaded using a stored seed and player state, ensuring identical world reconstruction across sessions.

- **Line-of-Sight Rendering**  
  Implements ray-based visibility so players only see tiles within their immediate line of sight, reducing the rendered world to a local view.

- **Environmental Simulation**  
  Each tile has a temperature value that dynamically affects player health, introducing risk-reward decision-making during navigation.

- **Goal-Oriented Gameplay Loop**  
  The objective is to reach the final generated room while avoiding death from environmental damage.

## Technical Overview
- World generation and gameplay logic are fully decoupled from rendering.
- The game uses a grid-based tile engine with layered abstractions for:
  - World generation
  - Player state
  - Rendering and HUD
  - Input handling
- Save/load functionality reconstructs the world from a seed rather than serializing the entire map, reducing storage complexity.

## Technologies
- Java
- Custom tile rendering engine
- Princeton `StdDraw` library

---

This project demonstrates applied object-oriented design, procedural generation, and real-time state management in an interactive system.
