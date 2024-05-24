package onTrackService;
import org.junit.jupiter.api.Test;

import onTrackService.OnTrackService.Feedback;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OnTrackIntegrationTest {

    @Test
    public void testTaskInbox() {
        // Layer 3: TaskService
        OnTrackService.TaskService taskService = new OnTrackService.TaskService();
        OnTrackService.Task task = taskService.createTask("Title", "Description", "Creator");

        // Layer 2: Task Inbox
        boolean isCollaboratorAdded = taskService.addCollaborator(task.getId(), "Collaborator");
        assertTrue(isCollaboratorAdded);

        // Layer 1: Home Screen
        assertEquals        ("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals("Creator", task.getCreator());
    }

    @Test
    public void testTaskList() {
        // Layer 3: TaskService
        OnTrackService.TaskService taskService = new OnTrackService.TaskService();
        OnTrackService.Task task1 = taskService.createTask("Title1", "Description1", "Creator1");
        OnTrackService.Task task2 = taskService.createTask("Title2", "Description2", "Creator2");

        // Layer 2: Task List
        assertNotNull(task1);
        assertNotNull(task2);

        // Layer 1: Home Screen
        assertEquals("Title1", task1.getTitle());
        assertEquals("Description1", task1.getDescription());
        assertEquals("Creator1", task1.getCreator());

        assertEquals("Title2", task2.getTitle());
        assertEquals("Description2", task2.getDescription());
        assertEquals("Creator2", task2.getCreator());
    }

    @Test
    public void testViewTaskAndSendMessage() {
        // Layer 3: TaskService
        OnTrackService.TaskService taskService = new OnTrackService.TaskService();
        OnTrackService.Task task = taskService.createTask("Title", "Description", "Creator");
        
        // Layer 2: View Task
        boolean isSubmitted = taskService.submitTask(task.getId(), "Student", "Submission");
        assertTrue(isSubmitted);

        // Layer 2: Show Messages
        boolean isMessageSent = taskService.sendMessage(task.getId(), "Sender", "Recipient", "Message");
        assertTrue(isMessageSent);
        
        // Verify messages
        List<String> messages = taskService.getTaskMessages(task.getId());
        assertEquals(1, messages.size());
        assertEquals("Sender (to Recipient): Message", messages.get(0));
    }

    @Test
    public void testScheduleSession() {
        // Layer 3: TutoringService
        OnTrackService.TutoringService tutoringService = new OnTrackService.TutoringService();
        Date date = new Date();
        OnTrackService.Session session = tutoringService.scheduleSession("Tutor", "Student", date, "10:00 AM");

        // Layer 1: Home Screen
        assertNotNull(session);
        assertEquals("Tutor", session.getTutor());
        assertEquals("Student", session.getStudent());
        assertEquals(date, session.getDate());
        assertEquals("10:00 AM", session.getTime());
    }
    
    @Test
    public void testFeedbackIntegration() {
        // Create a task and provide feedback
        OnTrackService.TaskService taskService = new OnTrackService.TaskService();
        OnTrackService.FeedbackService feedbackService = new OnTrackService.FeedbackService();
        
        OnTrackService.Task task = taskService.createTask("English Essay", "Write a 500-word essay", "Teacher A");
        OnTrackService.Feedback feedback = feedbackService.provideFeedback(task.getId(), "Tutor X", "Good effort on the essay.");

        // Ensure feedback is provided and accessible
        assertNotNull(feedback);
        assertEquals("Tutor X", feedback.getTutor());
        assertEquals("Good effort on the essay.", feedback.getComments());

        // Validate feedback retrieval
        Map<Integer, OnTrackService.Feedback> taskFeedback = task.getFeedbacks();
        assertTrue(taskFeedback.containsKey(feedback.getId()));
    }

    @Test
    public void testStudyGroup() {
        // Layer 3: StudyGroupService
        OnTrackService.StudyGroupService studyGroupService = new OnTrackService.StudyGroupService();
        OnTrackService.StudyGroup group = studyGroupService.createStudyGroup("Study Group", "Creator");

        // Layer 2: Join Study Group
        boolean isMemberJoined = studyGroupService.joinStudyGroup("Student", group.getId());
        assertTrue(isMemberJoined);

        // Layer 1: Home Screen
        assertEquals("Study Group", group.getGroupName());
        assertTrue(group.getMembers().contains("Creator"));
        assertTrue(group.getMembers().contains("Student"));
    }

    @Test
    public void testProgressReport() {
        // Layer 3: ProgressReportService
        OnTrackService.ProgressReportService reportService = new OnTrackService.ProgressReportService();
        OnTrackService.ProgressReport report = reportService.generateReport("Student");

        // Layer 1: Home Screen
        assertNotNull(report);
        assertEquals("Student", report.getStudent());
        assertEquals(90, report.getAverageScore());
        assertEquals(20, report.getTasksCompleted());
    }

    @Test
    public void testNotifications() {
        // Layer 3: NotificationService
        OnTrackService.NotificationService notificationService = new OnTrackService.NotificationService();
        
        // Notify student on task update
        notificationService.notifyStudentOnTaskUpdate("Student", 1, "Task updated");
        List<String> studentNotifications = notificationService.getNotifications("Student");
        assertEquals(1, studentNotifications.size());
        assertEquals("Task 1: Task updated", studentNotifications.get(0));

        // Notify tutor on task submission
        notificationService.notifyTutorOnTaskSubmission("Tutor", 1, "Student");
        List<String> tutorNotifications = notificationService.getNotifications("Tutor");
        assertEquals(1, tutorNotifications.size());
        assertEquals("Task 1 submitted by Student", tutorNotifications.get(0));
    }
}

