# EventNest 🎪

A Java-based Event/Fest Management System built with core Object-Oriented Programming principles — designed to handle event registration, participation tracking, and result generation for college tech fests and department events.

> **Note:** "EventNest" is a placeholder name — swap it out below if you land on something else (FocesHub, FestFlow, etc.)

## 📖 About

EventNest models how a college fest or department event gets organized — from event creation to participant registration to certificate generation — as a clean set of Java classes. Originally built as an OOP coursework project, it's designed with real-world use in mind (e.g., piloting at a FOCES event).

## ✨ Features

- Event creation with categories (Technical / Cultural)
- Participant registration with clash detection (no double-booking same time slot)
- Waitlist handling once an event reaches capacity
- Score calculation that adapts per event type (quiz vs. coding contest vs. dance, etc.)
- Certificate generation for participants and winners
- Export participant lists / results to file

## 🏗️ Architecture & OOP Concepts

| Concept | Where it's used |
|---|---|
| **Inheritance** | `TechnicalEvent` and `CulturalEvent` extend a base `Event` class |
| **Polymorphism** | `calculateScore()` behaves differently per event type |
| **Encapsulation** | Registration limits and fee logic are private, exposed via controlled methods |
| **Composition** | `Event` *has-a* list of `Registration` objects |

### Core Classes

- `Event` — name, date, venue, category, max participants
- `Participant` — name, roll number, department, registered events
- `Coordinator` — manages an event (extends shared `User` base with `Participant`)
- `Registration` — links a `Participant` to an `Event`
- `Certificate` — generated per participant based on participation/winner status

## 🛠️ Tech Stack

- **Language:** Java
- **Framework (planned):** Javalin / Spark Java (lightweight, for a basic web form)
- **Database (planned):** SQLite

## 🚀 Roadmap

- [ ] Phase 1: Core Java OOP classes + console I/O
- [ ] Phase 2: Swap console I/O for a simple web form; add SQLite persistence
- [ ] Phase 3: Pilot at a real FOCES event
- [ ] Phase 4 (optional): Fold into a broader campus companion app as an "Events" module

## 📦 Getting Started

```bash
git clone https://github.com/<your-username>/eventnest.git
cd eventnest
javac Main.java
java Main
```

*(Update once the actual project structure/build tool is finalized — Maven/Gradle instructions go here.)*

## 📄 License

MIT — feel free to use and adapt.

## 🙋 Author

Built by Geowon — B.Tech CSE student, College of Engineering Chengannur (KTU).
