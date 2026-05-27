# LatticeGrid

**Capability-Aware Device-to-Device Resource Sharing System**

LatticeGrid is a local distributed computation framework designed to intelligently offload computational workloads between nearby devices based on performance capability and network conditions.

The system dynamically decides whether a task should execute locally or on a connected device by evaluating:

- Local compute capability
- Remote compute capability
- Network overhead / latency

LatticeGrid aims to reduce execution time and improve performance using adaptive task orchestration across heterogeneous devices.

---

# Overview

Modern households and small teams often own multiple smart devices with unused computational resources.

LatticeGrid explores how nearby devices can cooperatively execute workloads more efficiently through intelligent task offloading while remaining fully local and privacy-preserving.

The current version demonstrates:

- Device profiling
- Capability exchange
- Predictive execution selection
- Local network task offloading

---

# Features

## Adaptive Task Offloading
Automatically predicts whether local execution or remote execution will be faster.

## Device Profiling
Each device benchmarks itself and stores an average compute profile locally.

## Local-Only Architecture
No cloud infrastructure required.

## Lightweight Communication
Uses NanoHTTPD for local device communication.

## Predictive Scheduling
Makes execution decisions before task execution.

## Capability-Aware Execution
Selects execution location based on measured performance rather than manual selection.

---

# Current Architecture (V1)

## Components

### DeviceProfiler
Responsible for:

- Running benchmark tests
- Computing average execution time
- Storing device performance profile

---

### LocalServer
Lightweight HTTP server running on each device.

Endpoints:

| Endpoint | Description |
|---|---|
| `/profile` | Returns stored average compute capability |
| `/task` | Executes computation workload |

---

### Smart Decision Engine
Determines whether to:

- Execute locally
- Offload to remote device

Decision factors:

- Local benchmark average
- Remote benchmark average
- Network round-trip overhead

---

### NetworkUtils
Handles:

- Gateway IP retrieval
- Local network utilities
- Connectivity support

---

# Execution Flow

## Example Scenario

Phone-A connects to Phone-B hotspot.

LatticeGrid then:

1. Retrieves Phone-A local compute profile
2. Fetches Phone-B compute profile
3. Measures network overhead
4. Predicts total offload execution cost
5. Selects optimal execution location
6. Executes task accordingly

---

# Experimental Results

| Execution Mode | Time |
|---|---|
| Local Execution | 9921 ms |
| Remote Offloaded Execution | 2057 ms |

### Performance Gain

- ~79% faster
- ~4.8x speedup

The system can also intelligently determine when local execution is more efficient and avoid unnecessary offloading.

---

# Why LatticeGrid Is Intelligent

LatticeGrid is not a simple remote execution system.

It:

- Builds persistent device performance profiles
- Exchanges lightweight capability metadata
- Predicts execution cost before task execution
- Minimizes total execution time dynamically
- Avoids unnecessary benchmarking during runtime

This creates a capability-aware adaptive distributed computation system.

---

# Current Scope (V1)

- Two-device system
- Matrix multiplication benchmark workload
- Average-based capability modeling
- Network latency estimation
- Predictive execution selection
- Local hotspot-based communication

---

# Future Expansion

Planned future work includes:

- Multi-device mesh support
- CPU/RAM-aware capability modeling
- Battery-aware scheduling
- Dynamic load balancing
- Distributed task partitioning
- Performance curve modelling
- Video processing workloads
- Compression workloads
- AI processing workloads

---

# Long-Term Vision

LatticeGrid aims to evolve into a local distributed compute layer for personal devices.

The goal is to allow nearby devices to cooperatively execute heavy workloads while remaining:

- Local-first
- Privacy-preserving
- Cloud-independent
- Capability-aware

Future versions will explore creator-focused workloads such as video processing acceleration.

---

# Tech Stack

- Kotlin
- Android
- NanoHTTPD
- Coroutines
- SharedPreferences

---

# Project Status

Current Status: **Prototype / Research Validation (V1)**

LatticeGrid V1 validates the feasibility of adaptive local task offloading between heterogeneous mobile devices.

---

# Author

**Sriman Dhanvi Adepu**

---

# License

Currently unlicensed.