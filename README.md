# CSE360 Project TP3

## Overview

This project implements an enhanced discussion forum system supporting multiple user roles (students, reviewers, and instructors). The application includes the following features:

- **Review Management:**  
  - Reviewers can create, view, update, and delete reviews for questions and potential answers.
  - Reviews include a weightage value and are displayed in descending order (most useful on top).

- **Messaging:**  
  - Reviewers can view the list of reviews they have written.
  - Reviewers can see and reply to private messages from authors.
  - Students can engage in private exchanges with reviewers.

- **Student Features:**  
  - Students can read reviews of potential answers.
  - Students can request permission to become a reviewer.
  - Students can maintain a list of trusted reviewers and perform searches that only return reviews from trusted reviewers.

- **Instructor Features:**  
  - Instructors can review questions and answers from specific students to determine whether to grant reviewer privileges.

## Project Structure

- **HW2/**  
  Contains all source files:
  - **Domain Classes:**  
    `Question.java`, `Answer.java`, `Review.java`, `Message.java`, `ReviewerRequest.java`, `TrustedReviewers.java`, etc.
  - **UI Pages:**  
    `DiscussionPage.java`, `UserHomePage.java`, `UserLoginPage.java`, `ReviewManagementPage.java`, `MessagingPage.java`, `ReviewerRequestPage.java`, `InstructorReviewPage.java`, `TrustedReviewersPage.java`.
  - **Test Classes:**  
    `AnswersTest.java`, `QuestionsTest.java`.

- **databasePart1/**  
  Contains database helper classes.
## Links

- **GitHub Repository:** [GitHub Repo Link](#)
- **Template:** [Template Link](#)
- **Screencasts:**  
- Screencast 1 (Demo, Code & JUnit Testing): [Link](#)  
- Screencast 2 (Architecture, Design & Code Alignment): [Link](#)  
- Screencast 3 (Standup Meetings): [Link](#)
- **Gantt Chart:** [Gantt Chart Link](#)
- **Standup Meetings:** [Standup Meetings Link](#)

## Team Members

- Member 1
- Member 2
- Member 3
- Member 4
- Member 5
- Member 6

## Dependencies

- **Java Version:** Java 8 or higher  
- **Libraries:** JavaFX, JUnit (for testing)  
- **Tools:** Astah (for UML diagrams)

## Notes

- Ensure all repository links and external resources (screencasts, Gantt chart, standup meetings) are accessible.
- This repository is intended for the CSE360 TP3 submission and includes all source code, documentation, and evidence required for grading.
