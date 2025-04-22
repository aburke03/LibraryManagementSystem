# Library Management System - Group G

## Overview
A command‑line Java application for managing a library’s books, members, and finances with role‑based authentication and transaction tracking.

## How to Run
1. Clone the repo and open in IntelliJ.
2. Run `LibraryManagementSystem.java`'s `main()`method.
3. Proceed to select your desired option in the CLI below.
4. For testing, upon running each test file with coverage, you will see each class has 100% coverage
using specification, structural and property based testing when appropriate.

## Features
- **User Roles**
    - **Full‑time librarians** authenticate with a 6‑digit code (`123456`, `654321`, `000000`).
    - **Volunteer librarians** press Enter at the code prompt (limited permissions).
- **Book Management**
    - Add or remove books.
    - Checkout/return books.
    - If a full‑time librarian tries to checkout a non‑existent book, they can purchase it (random \$10–\$100 cost), add it to the catalog, then proceed with checkout.
- **Member Management**
    - Add members (all roles).
    - Revoke memberships (full‑time only).
- **Financial Management**
    - Operating cash balance (starts at \$39 000).
    - Accept incoming donations (full‑time only).
    - Withdraw salaries (full‑time only).
    - Automatic book‑purchase deductions on checkout.
- **Transaction & Audit Tracking**
    - Per‑librarian salary withdrawal history.
    - Per‑librarian book purchase history.

## CLI Menu
1. Add Book
2. Remove Book
3. Add Member
4. Remove Member ← full‑time only
5. Checkout Book ← purchase‑and‑add option for full‑time
6. Return Book
7. View All Books
8. View All Members
9. Add Donation ← full‑time only
10. Withdraw Salary ← full‑time only
11. Exit


## Design Decisions
- **Role Enforcement** 
  - Interface checks isFullTime before donations, salary, member‑revocation, or purchasing flows.

- **Separation of Concerns:**
  - LibraryAccounts handles all cash‑balance logic.
  - Purchasing encapsulates random cost generation.
  - Librarians tracks authentication and per‑librarian transactions.

- **Data Structures:**
  - Used Map<String, …> for fast lookups (books, members, librarian codes).
  - CLI logic centralized in a switch‑case for clarity and easy extension.
