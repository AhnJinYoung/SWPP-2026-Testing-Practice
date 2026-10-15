package com.example.swpp.tododapp.tasks

import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.swpp.tododapp.data.Task
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@SmallTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {

    @Test
    fun setDueDateLabel_activeWithoutDueDate_clearsAndHidesView() {
        // GIVEN
        val textView = textView("CLEAR", View.VISIBLE)
        val task = Task(
            title = "Buy milk",
            description = "Two cartons",
            isCompleted = false,
            id = "task-no-date",
            dueDate = null,
        )

        // WHEN
        setDueDateLabel(textView, task)

        // THEN
        assertEquals("", textView.text.toString())
        assertEquals(View.GONE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_activeFutureTask_showsDMinusLabel() {
        // GIVEN
        val textView = textView("stale", View.GONE)
        val task = Task(
            title = "Submit report",
            description = "Final copy",
            isCompleted = false,
            id = "task-future",
            dueDate = LocalDate.now().plusDays(5),
        )

        // WHEN
        setDueDateLabel(textView, task)

        // THEN
        assertEquals("D-5", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_activeTaskDueToday_showsDDay() {
        // GIVEN
        val textView = textView("stale", View.GONE)
        val task = Task(
            title = "Call client",
            description = "At noon",
            isCompleted = false,
            id = "task-today",
            dueDate = LocalDate.now(),
        )

        // WHEN
        setDueDateLabel(textView, task)

        // THEN
        assertEquals("D-Day", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_activeOverdueTask_showsDPlusLabel() {
        // GIVEN
        val textView = textView("stale", View.GONE)
        val task = Task(
            title = "Return book",
            description = "Library",
            isCompleted = false,
            id = "task-overdue",
            dueDate = LocalDate.now().minusDays(5),
        )

        // WHEN
        setDueDateLabel(textView, task)

        // THEN
        assertEquals("D+5", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_completedTaskWithDueDate_showsClear() {
        // GIVEN
        val textView = textView("D+5", View.GONE)
        val task = Task(
            title = "Return book",
            description = "Library",
            isCompleted = true,
            id = "task-complete-dated",
            dueDate = LocalDate.now().minusDays(5),
        )

        // WHEN
        setDueDateLabel(textView, task)

        // THEN
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_completedTaskWithoutDueDate_showsClear() {
        // GIVEN
        val textView = textView("", View.GONE)
        val task = Task(
            title = "Wash car",
            description = "Blue car",
            isCompleted = true,
            id = "task-complete-undated",
            dueDate = null,
        )

        // WHEN
        setDueDateLabel(textView, task)

        // THEN
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_nullTask_clearsAndHidesView() {
        // GIVEN
        val textView = textView("CLEAR", View.VISIBLE)
        val task = null

        // WHEN
        setDueDateLabel(textView, task)

        // THEN
        assertEquals("", textView.text.toString())
        assertEquals(View.GONE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_allAdmittedInputs_updatesWithoutError() {
        // GIVEN
        val textView = textView("old", View.VISIBLE)
        val task = null

        // WHEN
        setDueDateLabel(textView, task)

        // THEN
        assertEquals("", textView.text.toString())
        assertEquals(View.GONE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_aroundTodayBoundary_showsSignedOneDayLabels() {
        // GIVEN
        val textView = textView("stale", View.GONE)
        val tasks = listOf(
            Task(
                title = "Yesterday task",
                description = "Due yesterday",
                isCompleted = false,
                id = "task-yesterday",
                dueDate = LocalDate.now().minusDays(1),
            ),
            Task(
                title = "Today task",
                description = "Due today",
                isCompleted = false,
                id = "task-today-edge",
                dueDate = LocalDate.now(),
            ),
            Task(
                title = "Tomorrow task",
                description = "Due tomorrow",
                isCompleted = false,
                id = "task-tomorrow",
                dueDate = LocalDate.now().plusDays(1),
            ),
        )
        val expectedLabels = listOf("D+1", "D-Day", "D-1")

        // WHEN / THEN
        tasks.zip(expectedLabels).forEach { (task, expectedLabel) ->
            setDueDateLabel(textView, task)
            assertEquals(expectedLabel, textView.text.toString())
            assertEquals(View.VISIBLE, textView.visibility)
        }
    }

    private fun textView(text: String, visibility: Int): TextView =
        TextView(ApplicationProvider.getApplicationContext()).apply {
            this.text = text
            this.visibility = visibility
        }
}
