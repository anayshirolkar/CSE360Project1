Project Overview
Our project is a fully functional discussion forum that allows users to register securely, create discussion threads, reply to posts, and engage with the community through features such as likes/upvotes and search functionality. The system is built with user safety and clarity in mind, offering confirmation pop-ups, editable content, and structured input fields.

Features
Secure User Registration: Includes email verification, password encryption, and password reset options.
Thread Creation & Management: Users can post questions, edit, update, or delete threads.
Replies & Nested Threads: Supports multiple answers and nested replies to encourage interactive discussions.
Confirmation Pop-ups: Ensures that critical actions (like deleting or editing) require user confirmation.
Search Functionality: Dedicated search bar and filters to quickly locate relevant discussions.
User Profiles: Each user has a profile that tracks their posts, replies, and interactions.
Moderation Tools: Moderators have access to tools for removing inappropriate content and managing user behavior.
Responsive UI: A scrollable layout with separate text boxes and a simplified sidebar for question titles.
User Stories
Registering and Feeling Secure:
Users can sign up, log in, and reset passwords with assurance through email verification and encryption.
The Power to Start a Conversation:
Users can initiate threads with questions or detailed topics and have control over editing or deleting their posts.
Engaging Through Replies:
Users can reply to questions and answers, with structured formatting ensuring clarity.
Showing Appreciation – Likes and Upvotes:
A built-in mechanism to like and upvote posts, promoting quality discussions.
Finding What Matters – Search and Filters:
A search bar and category-based filters to help users quickly locate specific discussions.
Your Forum Identity – Profiles and Activity Tracking:
Personal profiles allow users to view their contributions and track past discussions.
Keeping the Community Safe – Moderation and Admin Controls:
Moderators can remove inappropriate content and ban users to maintain a positive community.
Implementation Details
Design Documents & UML Diagrams:
Our design documents include detailed UML diagrams that map out the structure and interactions within the system.

Code Integration:
The code implements the design through features such as confirmation pop-ups, structured input fields, and a dynamic, scrollable layout.

Standup Meetings & Progress:
We conducted multiple standup meetings (from early February 2025 to late February 2025) to discuss progress, feature updates, and testing plans. Detailed screencasts of these meetings are available in the repository.

Testing
Automated Tests
Questions Class:

Add Question
Delete Question
Edit Question
Search Questions
Answers Class:

Add Answer
Delete Answer
Edit Answer
Test cases are available in the repository:

Questions Test
Answers Test
Manual Tests
We developed several manual test cases to ensure full coverage:

Test Case 1: Add Question (Student Role)
Test Case 2: Add Answer (Student Role)
Test Case 3: View All Questions (All Users)
Test Case 4: View All Answers (All Users)
Test Case 5: Search Questions (All Users)
Test Case 6: Ask Follow-Up Questions (Nested Replies)
Detailed steps and expected outcomes for each test case are documented in the project report.

Repository Structure
bash
Copy
CSE360Project1/
├── HW2/
│   ├── src/               # Source code for the discussion forum
│   ├── tests/             # Automated tests for Questions and Answers
│   └── docs/              # Design documents and UML diagrams
├── screencast/            # Screencasts for testing and standup meetings
└── README.md              # This file
Team Members & Contributions
Anay Shirolkar: UML diagrams, sequence diagrams, integration modifications.
Chakshu Jain: Implementation of features, debugging, and screencast creation.
Diya Dineshbhai Chaudhari: Feature implementation, debugging, and standup meeting recordings.
Lubna Firdaus: Automated tests and manual testing of CRUD operations.
Prajakta Punjaji Kadukar: UI enhancements and integration of new features.
Rishith Ashit Mody: Drafting user stories and guiding feature implementations.
Team Norms
Communication:
Primary communication via Discord with a 24-hour response expectation.

Meetings:
Twice weekly meetings with written agendas and documented action items.

Collaboration:
Active participation in code reviews, pull requests, and documentation efforts.

Conflict Resolution:
Professional discussions and votes to resolve disagreements.

Deadlines & Code Management:
Strict adherence to deadlines and version control best practices using GitHub.

Getting Started
Prerequisites
Java Development Kit (JDK)
Git
An IDE (e.g., Eclipse, IntelliJ IDEA) or any preferred text editor
Installation
Clone the Repository:

bash
Copy
git clone https://github.com/anayshirolkar/CSE360Project1.git
cd CSE360Project1
Checkout the Phase 2 Integration Branch:

bash
Copy
git checkout Phase_2_Integration
Compile and Run the Application:
Follow your IDE’s instructions or use command-line tools to build and run the application.

Resources & Links
GitHub Repository:
Main Repository

HW2 Code & Integration:
Phase 2 Integration Branch

Gantt Chart & Implementation Plan:
Gantt Chart

Screencasts:
Screencasts Directory



Gantt Chart Link - https://docs.google.com/spreadsheets/d/1Eqem_eECaQ4lPMkFtNW5SqAtTPISNjh6-oijKPttYK4/edit?gid=0#gid=0



