# CSE360 Project TP4

## Overview

This project continues the development of an enhanced discussion forum system, building on TP3, with the following new features and improvements:

- **Advanced Review Management**  
  - Reviewers can rate reviews and flag inappropriate content.  
  - Reviews can include attachments or reference links.  
  - Improved sorting: by weightage, rating, or recency.

- **Enhanced Messaging**  
  - Threaded private messaging between students and reviewers.  
  - Notifications for new messages and replies.

- **Student Features**  
  - Students can bookmark useful reviews for later reference.  
  - Students can view reviewer profiles and history before trusting them.

- **Reviewer Features**  
  - Reviewers can manage their own profile, including expertise tags.  
  - Dashboard showing review statistics and feedback.

- **Instructor Features**  
  - Instructors can assign reviewer privileges directly without a request.  
  - Analytics dashboard showing student/reviewer performance.

## Project Structure

- **HW4/**  
  Contains all source files:  
  - **Domain Classes:**  
    `Question.java`, `Answer.java`, `Review.java`, `Message.java`, `ReviewerRequest.java`, `TrustedReviewers.java`, etc.
  - **UI Pages:**  
    `DiscussionPage.java`, `UserHomePage.java`, `UserLoginPage.java`, `ReviewManagementPage.java`, `MessagingPage.java`, `ReviewerRequestPage.java`, `InstructorReviewPage.java`, `TrustedReviewersPage.java`, `ReviewerProfilePage.java`, `NotificationsPage.java`.
  - **Test Classes:**  
    `AnswersTest.java`, `QuestionsTest.java`, `ReviewHistoryTest.java`, `TrustedReviewerTest.java`.

- **databasePart1/**  
  Contains updated database helper classes and schema files.

## Summary of Needed Implementations

- **Private Feedback System for Reviewers**  
  Allow students and instructors to send confidential feedback to reviewers.

- **Review Update Functionality with History**  
  Enable reviewers to edit existing reviews and maintain a visible change history.

- **Notification System for Updated Reviews**  
  Notify students when a review they bookmarked or trusted has been updated.

- **Reviewer Profile Page**  
  Dedicated reviewer pages showing bio, expertise tags, past reviews, and ratings.

- **Database Schema Updates**  
  Extend schema to support feedback, review history, notifications, and profile data.

- **UI Integration for All New Features**  
  Build and integrate UI elements for feedback submission, review history, notifications, and profile browsing.

## Links

- **GitHub Repository:** [GitHub Repo Link](#)
- **Template:** [Template Link]( https://arizonastateu-my.sharepoint.com/:w:/g/personal/lfirdaus_sundevils_asu_edu/EaTftlOXkcRAhruzCENhYpsBBV6iw0uTazz6SilAMDTnPw?e=EZcRg3)
- **Screencasts:**  
  - Screencast 1 (Demo, Code & JUnit Testing): [Link](Team_Project_Repo/TP4/Screencasts)  
  - Screencast 2 (Architecture, Design & Code Alignment): [Link](Team_Project_Repo/TP4/Screencasts)  
  - Screencast 3 (Standup Meetings): [Link](Team_Project_Repo/TP4/Screencasts)
- **Standup Meetings:** [Standup Meetings Link](Team_Project_Repo/TP4/StandupMeets)

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

This repository is intended for the CSE360 TP4 submission and includes all updated source code, documentation, and evidence required for grading.
