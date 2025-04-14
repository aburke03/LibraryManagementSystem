# Library Management System

## Overview
A command-line-based library system for managing books and members, built with Java and Maven.

## How to Run
1. Clone the repo and open in IntelliJ.
2. Run `LibraryManagementSystem.java`'s `main()`method.
3. Proceed to select your desired option in the CLI below.

## CLI Usage
The librarian can:
- Add/Remove Books
- Add/Remove Members
- Checkout/Return Books
- View book/member details

## Design Decisions
- Used `Map` for fast lookups by ID
- CLI implemented as modular switch-case structure
- Clean separation of data vs. UI logic
