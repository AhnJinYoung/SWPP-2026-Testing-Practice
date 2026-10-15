package com.example.swpp.tododapp.tasks

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DueDateUtilsTest {

    @Test
    fun dueDateLabel_completedWithoutDueDate_returnsClear() {
        // GIVEN
        val dueDate = null
        val today = LocalDate.of(2026, 10, 14)
        val isCompleted = true

        // WHEN
        val result = dueDateLabel(dueDate, today, isCompleted)

        // THEN
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_completedWithDueDate_returnsClear() {
        // GIVEN
        val pastDueDate = LocalDate.of(2026, 10, 10)
        val futureDueDate = LocalDate.of(2026, 10, 20)
        val today = LocalDate.of(2026, 10, 14)
        val isCompleted = true

        // WHEN
        val pastResult = dueDateLabel(pastDueDate, today, isCompleted)
        val futureResult = dueDateLabel(futureDueDate, today, isCompleted)

        // THEN
        assertEquals("CLEAR", pastResult)
        assertEquals("CLEAR", futureResult)
    }

    @Test
    fun dueDateLabel_activeWithoutDueDate_returnsEmpty() {
        // GIVEN
        val dueDate = null
        val today = LocalDate.of(2026, 10, 14)
        val isCompleted = false

        // WHEN
        val result = dueDateLabel(dueDate, today, isCompleted)

        // THEN
        assertEquals("", result)
    }

    @Test
    fun dueDateLabel_dueToday_returnsDDay() {
        // GIVEN
        val dueDate = LocalDate.of(2026, 10, 14)
        val today = LocalDate.of(2026, 10, 14)
        val isCompleted = false

        // WHEN
        val result = dueDateLabel(dueDate, today, isCompleted)

        // THEN
        assertEquals("D-Day", result)
    }

    @Test
    fun dueDateLabel_dueInFiveDays_returnsDMinusFive() {
        // GIVEN
        val dueDate = LocalDate.of(2026, 10, 19)
        val today = LocalDate.of(2026, 10, 14)
        val isCompleted = false

        // WHEN
        val result = dueDateLabel(dueDate, today, isCompleted)

        // THEN
        assertEquals("D-5", result)
    }

    @Test
    fun dueDateLabel_overdueByFiveDays_returnsDPlusFive() {
        // GIVEN
        val dueDate = LocalDate.of(2026, 10, 9)
        val today = LocalDate.of(2026, 10, 14)
        val isCompleted = false

        // WHEN
        val result = dueDateLabel(dueDate, today, isCompleted)

        // THEN
        assertEquals("D+5", result)
    }

    @Test
    fun dueDateLabel_aroundTodayBoundary_returnsSignedOneDayLabels() {
        // GIVEN
        val today = LocalDate.of(2026, 10, 14)
        val dueDates = listOf(
            LocalDate.of(2026, 10, 13),
            LocalDate.of(2026, 10, 14),
            LocalDate.of(2026, 10, 15),
        )

        // WHEN
        val results = dueDates.map { dueDateLabel(it, today, isCompleted = false) }

        // THEN
        assertEquals(listOf("D+1", "D-Day", "D-1"), results)
    }

    @Test
    fun dueDateLabel_aroundMonthBoundary_countsCalendarDays() {
        // GIVEN
        val today = LocalDate.of(2026, 1, 31)
        val dueDates = listOf(
            LocalDate.of(2026, 1, 30),
            LocalDate.of(2026, 1, 31),
            LocalDate.of(2026, 2, 1),
        )

        // WHEN
        val results = dueDates.map { dueDateLabel(it, today, isCompleted = false) }

        // THEN
        assertEquals(listOf("D+1", "D-Day", "D-1"), results)
    }

    @Test
    fun dueDateLabel_aroundYearBoundary_countsCalendarDays() {
        // GIVEN
        val today = LocalDate.of(2026, 12, 31)
        val dueDates = listOf(
            LocalDate.of(2026, 12, 30),
            LocalDate.of(2026, 12, 31),
            LocalDate.of(2027, 1, 1),
        )

        // WHEN
        val results = dueDates.map { dueDateLabel(it, today, isCompleted = false) }

        // THEN
        assertEquals(listOf("D+1", "D-Day", "D-1"), results)
    }

    @Test
    fun dueDateLabel_aroundLeapDayBoundary_countsCalendarDays() {
        // GIVEN
        val today = LocalDate.of(2024, 2, 29)
        val dueDates = listOf(
            LocalDate.of(2024, 2, 28),
            LocalDate.of(2024, 2, 29),
            LocalDate.of(2024, 3, 1),
        )

        // WHEN
        val results = dueDates.map { dueDateLabel(it, today, isCompleted = false) }

        // THEN
        assertEquals(listOf("D+1", "D-Day", "D-1"), results)
    }

    @Test
    fun dueDateLabel_acrossLeapMonth_returnsTotalCalendarDays() {
        // GIVEN
        val dueDate = LocalDate.of(2024, 3, 1)
        val today = LocalDate.of(2024, 1, 31)
        val isCompleted = false

        // WHEN
        val result = dueDateLabel(dueDate, today, isCompleted)

        // THEN
        assertEquals("D-30", result)
    }

    @Test
    fun dueDateLabel_overdueAcrossLeapMonth_returnsTotalCalendarDays() {
        // GIVEN
        val dueDate = LocalDate.of(2024, 1, 31)
        val today = LocalDate.of(2024, 3, 1)
        val isCompleted = false

        // WHEN
        val result = dueDateLabel(dueDate, today, isCompleted)

        // THEN
        assertEquals("D+30", result)
    }

    @Test
    fun dueDateLabel_exactlyOneMonthAhead_returnsThirtyOneDays() {
        // GIVEN
        val dueDate = LocalDate.of(2026, 11, 14)
        val today = LocalDate.of(2026, 10, 14)
        val isCompleted = false

        // WHEN
        val result = dueDateLabel(dueDate, today, isCompleted)

        // THEN
        assertEquals("D-31", result)
    }
}
