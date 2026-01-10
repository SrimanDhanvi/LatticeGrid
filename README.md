# LatticeGrid (V0)

LatticeGrid is an early-stage prototype exploring
local-first, device-to-device cooperation without cloud dependency.

This repository contains **V0**, a proof-of-concept Android application
that validates hotspot-based data sharing using a device-as-server model.

---

## What V0 Does

- Uses a local Wi-Fi hotspot for device-to-device networking
- Allows a device to act as a local server
- Enables nearby devices to download files directly
- Runs transfers in the background using Kotlin coroutines
- Includes basic battery-aware behavior (non-hosting below threshold)

---

## What V0 Does NOT Do (Yet)

- No automatic host election
- No encryption
- No storage pooling
- No compute sharing
- No cloud services

These are intentionally deferred to later versions.

---

## Architecture (V0)

- Network: Local Wi-Fi hotspot
- Server: Device itself (not external)
- Clients: Nearby devices connected to hotspot
- Communication: TCP sockets
- Storage: App-internal storage

---

## Status

V0 is **working and validated**.
File transfer over hotspot has been successfully demonstrated.

This version focuses on feasibility, not optimization or scale.

---

## Roadmap (High-Level)

- V1: Intelligent, battery-aware role selection
- V2: Secure mesh and storage sharing
- V3: Resource federation (task offloading)
- V4: OS / platform integration

---

## Note

This is an experimental prototype.
Code structure may change significantly in future versions.
