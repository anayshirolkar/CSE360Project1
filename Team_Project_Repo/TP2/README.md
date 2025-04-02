# CSE 360 Team Project Phase 2 – Discussion Forum

This repository contains the complete solution for our Phase 2 Discussion Forum project for CSE 360. The project is developed by **Team Monday 4** and includes design documents, code, testing artifacts, and supporting materials.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Design Documents & Implementation Details](#design-documents--implementation-details)
- [User Stories](#user-stories)
- [Implementation Plan & Progress](#implementation-plan--progress)
- [Testing](#testing)
  - [Automated Tests](#automated-tests)
  - [Manual Tests](#manual-tests)
- [Repository Structure](#repository-structure)
- [Team Members & Contributions](#team-members--contributions)
- [Team Norms](#team-norms)
- [Getting Started](#getting-started)
- [Resources & Links](#resources--links)


---

## Project Overview

Our project is a fully functional discussion forum that enables users to:
- Register securely with email verification and password encryption.
- Create, edit, and delete discussion threads.
- Reply to posts with nested responses.
- Search and filter questions to quickly locate topics.
- Manage content with moderation tools and a user-friendly interface.

The system is designed with features such as confirmation pop-ups, structured input fields, scrollable layouts, and a simplified sidebar display to enhance the user experience.

---

## Features

- **Secure Registration & Authentication:**  
  Users can register, log in, reset passwords, and verify their email securely.

- **Thread Creation & Management:**  
  - Create new threads (questions)  
  - Edit or delete existing threads

- **Replies & Nested Threads:**  
  - Post multiple answers  
  - Reply to answers (nested replies) for interactive discussions

- **Confirmation Pop-Ups:**  
  - Confirm critical actions like deleting or editing posts

- **Search & Filter:**  
  - Dedicated search bar to locate questions  
  - Category-based filters for easier navigation

- **User Profiles & Activity Tracking:**  
  - Each user can view their posts, replies, and overall activity

- **Moderation Tools:**  
  - Moderators can remove inappropriate content and ban disruptive users

- **Responsive UI:**  
  - Scrollable layout with structured input fields and a clean sidebar for questions

---

## Design Documents & Implementation Details

- **UML Diagrams & Design Documents:**  
  The project’s design is based on detailed UML diagrams that map out the system’s structure and interactions.

- **Code Implementation:**  
  The code implements the design with:
  - Confirmation dialogs to prevent accidental edits/deletes
  - Separate text boxes for question and answer inputs
  - A dynamic and scrollable layout
  - A robust search functionality for finding specific questions

- **Integration & Standup Meetings:**  
  Our implementation plan was discussed and refined during multiple standup meetings. Detailed screencasts are provided in the repository for each meeting.

---

## User Stories

1. **Registering and Feeling Secure:**  
   - Users sign up, log in, and reset passwords securely with email verification and encryption.
   
2. **The Power to Start a Conversation:**  
   - Users can create threads for open-ended questions or detailed topics with options to edit or delete.
   
3. **Engaging Through Replies:**  
   - Users can post multiple answers and nested replies, ensuring a structured discussion.
   
4. **Showing Appreciation – Likes and Upvotes:**  
   - A like/upvote system encourages quality contributions and highlights insightful responses.
   
5. **Finding What Matters – Search and Filters:**  
   - A search bar and category filters allow users to quickly locate relevant topics.
   
6. **Your Forum Identity – Profiles and Activity Tracking:**  
   - Personal profiles display a user’s posts, replies, and overall activity.
   
7. **Keeping the Community Safe – Moderation and Admin Controls:**  
   - Moderators have the ability to remove inappropriate content and manage user behavior.

---

## Implementation Plan & Progress

- **Gantt Chart & Implementation Plan:**  
  Our project plan is detailed in our [Gantt Chart](https://docs.google.com/spreadsheets/d/1Eqem_eECaQ4lPMkFtNW5SqAtTPISNjh6-oijKPttYK4/edit?usp=sharing).

- **Standup Meetings:**  
  We held a series of meetings with the following highlights:
  
  - **Standup Meeting 1 (2/8/2025):**  
    - Planned project structure and tasks  
    - Uploaded initial code to GitHub
  
  - **Standup Meeting 2 (2/12/2025):**  
    - Shared progress on features  
    - Selected best features for implementation
  
  - **Standup Meeting 3 (2/15/2025):**  
    - Discussed work progress and issues  
    - Listed additional features for the forum
  
  - **Standup Meeting 4 (2/19/2025):**  
    - Finalized implementation approach and testing strategy
  
  - **Standup Meeting 5 (2/22/2025):**  
    - Discussed nested thread feature and documentation updates
  
  - **Standup Meeting 6 (2/26/2025):**  
    - Finalized screencast plan and updated test results

---

## Testing

### Automated Tests

Tests have been implemented to ensure the integrity of both the Questions and Answers classes:

- **Questions Tests:**  
  - Add Question  
  - Delete Question  
  - Edit Question  
  - Search Questions  
  [Questions Test Code](https://github.com/anayshirolkar/CSE360Project1/blob/Phase_2_Integration/HW2/QuestionsTest.java)

- **Answers Tests:**  
  - Add Answer  
  - Delete Answer  
  - Edit Answer  
  [Answers Test Code](https://github.com/anayshirolkar/CSE360Project1/blob/Phase_2_Integration/HW2/AnswersTest.java)

### Manual Tests

We developed detailed test cases for manual verification:

- **Test Case 1: Add Question (Student Role)**  
  - **Operation:** Create (Add Question)  
  - **Expected Outcome:** Question added and confirmation provided
  
- **Test Case 2: Add Answer (Student Role)**  
  - **Operation:** Create (Add Answer)  
  - **Expected Outcome:** Answer added and confirmation provided
  
- **Test Case 3: View All Questions (All Users)**  
  - **Operation:** Read (View Questions)  
  - **Expected Outcome:** Display list of questions with IDs and content (marked [Resolved] if applicable)
  
- **Test Case 4: View All Answers (All Users)**  
  - **Operation:** Read (View Answers)  
  - **Expected Outcome:** Display list of answers under each question
  
- **Test Case 5: View Searched Questions (All Users)**  
  - **Operation:** Read (Search Questions)  
  - **Expected Outcome:** List of questions containing the searched keyword
  
- **Test Case 6: Ask Follow-Up Questions (All Users)**  
  - **Operation:** Create (Nested Replies)  
  - **Expected Outcome:** Display threaded/nested replies preserving sequence

---

## Repository Structure


---

## Team Members & Contributions

- **Anay Shirolkar:**  
  - Created UML diagrams and sequence diagrams  
  - Integrated designs from multiple features

- **Chakshu Jain:**  
  - Implemented major features  
  - Debugged code and test cases  
  - Produced screencasts

- **Diya Dineshbhai Chaudhari:**  
  - Worked on feature implementation and debugging  
  - Recorded and uploaded standup meetings

- **Lubna Firdaus:**  
  - Wrote automated tests and performed manual testing  
  - Documented standup meeting notes

- **Prajakta Punjaji Kadukar:**  
  - Integrated new UI features and enhancements

- **Rishith Ashit Mody:**  
  - Developed user stories and guided feature implementation

---

## Team Norms

- **Communication:**  
  - Primary communication via Discord  
  - Responses expected within 24 hours; urgent issues handled via calls or quick meetings

- **Meetings:**  
  - Twice weekly meetings with written agendas and documented actions  
  - Members to notify the team if they cannot attend and catch up using meeting notes

- **Collaboration:**  
  - Active participation and review of each other’s work  
  - Code changes are managed through pull requests and reviewed before merging

- **Conflict Resolution:**  
  - Professional discussion and voting for resolving disagreements  
  - Constructive criticism focused on work output

- **Deadlines & Code Management:**  
  - Strict adherence to deadlines  
  - Version control via GitHub with detailed documentation and code comments

---

## Getting Started

### Prerequisites

- Java Development Kit (JDK)
- Git
- An IDE (e.g., Eclipse, IntelliJ IDEA) or your preferred text editor

### Installation

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/anayshirolkar/CSE360Project1.git
   cd CSE360Project1
