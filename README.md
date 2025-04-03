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
- **Template:** [[Template Link](https://arizonastateu-my.sharepoint.com/:w:/g/personal/lfirdaus_sundevils_asu_edu/EbuToRTFtllJpkD7UWVzdWYB1HzZGu38KzH7ULKUcFxoUg)](#)
- **Screencasts:**  
- Screencast 1 (Demo, Code & JUnit Testing): [Link](Team_Project_Repo/TP3/Screencasts)  
- Screencast 2 (Architecture, Design & Code Alignment): [Link](Team_Project_Repo/TP3/Screencasts)  
- Screencast 3 (Standup Meetings): [Link](Team_Project_Repo/TP3/Screencasts)
- **Standup Meetings:** [Standup Meetings Link](#)

## Team Members

- Anay Shirolkar
- Prajakta Kadukar
- Rishith Mody
- Chakshu Jain
- Diya Chaudhary
- Lubna Firdaus

## Dependencies

- **Java Version:** Java 8 or higher  
- **Libraries:** JavaFX, JUnit (for testing)  
- **Tools:** Astah (for UML diagrams)

## Notes
- This repository is intended for the CSE360 TP3 submission and includes all source code, documentation, and evidence required for grading.
