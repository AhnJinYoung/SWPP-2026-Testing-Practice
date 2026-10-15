# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `internal fun dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Return the task-list D-day label for a task, giving completion precedence over its optional due date and counting the signed difference between active tasks' due dates and today in calendar days.

**Pre-conditions**
- `today` is the calendar date against which the label is calculated; `dueDate` is either a valid calendar date or `null`.

**Post-conditions**
- Success cases
  - A completed task returns exactly `"CLEAR"` when it has no due date. `[Unit]`
    - Tip: Given `dueDate = null`, `today = LocalDate.of(2026, 10, 14)`, and `isCompleted = true` / When `dueDateLabel` is called / Then it returns exactly `"CLEAR"`; write `DueDateUtilsTest.dueDateLabel_completedWithoutDueDate_returnsClear`.
  - A completed task returns exactly `"CLEAR"` when it has a due date, regardless of whether that date is past or future. `[Unit]`
    - Tip: Given calls with `dueDate = LocalDate.of(2026, 10, 10)` and `dueDate = LocalDate.of(2026, 10, 20)`, `today = LocalDate.of(2026, 10, 14)`, and `isCompleted = true` / When `dueDateLabel` is called for each due date / Then both calls return exactly `"CLEAR"`; write `DueDateUtilsTest.dueDateLabel_completedWithDueDate_returnsClear`.
  - An active task without a due date returns exactly the empty string `""`. `[Unit]`
    - Tip: Given `dueDate = null`, `today = LocalDate.of(2026, 10, 14)`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `""`; write `DueDateUtilsTest.dueDateLabel_activeWithoutDueDate_returnsEmpty`.
  - An active task due today returns exactly `"D-Day"`. `[Unit]`
    - Tip: Given `dueDate = LocalDate.of(2026, 10, 14)`, `today = LocalDate.of(2026, 10, 14)`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `"D-Day"`; write `DueDateUtilsTest.dueDateLabel_dueToday_returnsDDay`.
  - An active task due after today returns exactly `"D-N"`, where `N` is the positive number of calendar days from today to the due date. `[Unit]`
    - Tip: Given `dueDate = LocalDate.of(2026, 10, 19)`, `today = LocalDate.of(2026, 10, 14)`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `"D-5"`; write `DueDateUtilsTest.dueDateLabel_dueInFiveDays_returnsDMinusFive`.
  - An active task due before today returns exactly `"D+N"`, where `N` is the positive number of calendar days by which it is overdue. `[Unit]`
    - Tip: Given `dueDate = LocalDate.of(2026, 10, 9)`, `today = LocalDate.of(2026, 10, 14)`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `"D+5"`; write `DueDateUtilsTest.dueDateLabel_overdueByFiveDays_returnsDPlusFive`.
- Error cases
  - None. Every combination admitted by the signature has a defined result above. `[Unit]`
- Edge cases
  - At the boundary between overdue, due today, and due in the future, the dates immediately before, at, and immediately after today return `"D+1"`, `"D-Day"`, and `"D-1"`, respectively. `[Unit]`
    - Tip: Given `today = LocalDate.of(2026, 10, 14)`, `isCompleted = false`, and due dates `LocalDate.of(2026, 10, 13)`, `LocalDate.of(2026, 10, 14)`, and `LocalDate.of(2026, 10, 15)` / When `dueDateLabel` is called for each due date / Then the results are exactly `"D+1"`, `"D-Day"`, and `"D-1"`; write `DueDateUtilsTest.dueDateLabel_aroundTodayBoundary_returnsSignedOneDayLabels`.
  - Calendar-day counting remains continuous immediately before, at, and after a month boundary. `[Unit]`
    - Tip: Given `today = LocalDate.of(2026, 1, 31)`, `isCompleted = false`, and due dates `LocalDate.of(2026, 1, 30)`, `LocalDate.of(2026, 1, 31)`, and `LocalDate.of(2026, 2, 1)` / When `dueDateLabel` is called for each due date / Then the results are exactly `"D+1"`, `"D-Day"`, and `"D-1"`; write `DueDateUtilsTest.dueDateLabel_aroundMonthBoundary_countsCalendarDays`.
  - Calendar-day counting remains continuous immediately before, at, and after a year boundary. `[Unit]`
    - Tip: Given `today = LocalDate.of(2026, 12, 31)`, `isCompleted = false`, and due dates `LocalDate.of(2026, 12, 30)`, `LocalDate.of(2026, 12, 31)`, and `LocalDate.of(2027, 1, 1)` / When `dueDateLabel` is called for each due date / Then the results are exactly `"D+1"`, `"D-Day"`, and `"D-1"`; write `DueDateUtilsTest.dueDateLabel_aroundYearBoundary_countsCalendarDays`.
  - Calendar-day counting includes leap day immediately before, at, and after the leap-day boundary. `[Unit]`
    - Tip: Given `today = LocalDate.of(2024, 2, 29)`, `isCompleted = false`, and due dates `LocalDate.of(2024, 2, 28)`, `LocalDate.of(2024, 2, 29)`, and `LocalDate.of(2024, 3, 1)` / When `dueDateLabel` is called for each due date / Then the results are exactly `"D+1"`, `"D-Day"`, and `"D-1"`; write `DueDateUtilsTest.dueDateLabel_aroundLeapDayBoundary_countsCalendarDays`.
  - A span longer than the remaining days field of a calendar period still uses the total number of elapsed calendar days. `[Unit]`
    - Tip: Given `dueDate = LocalDate.of(2024, 3, 1)`, `today = LocalDate.of(2024, 1, 31)`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `"D-30"`; write `DueDateUtilsTest.dueDateLabel_acrossLeapMonth_returnsTotalCalendarDays`.
  - The same total-calendar-day count applies when the span is in the past, so a span longer than one month overdue is not shortened to its day-of-month remainder. `[Unit]`
    - Tip: Given `dueDate = LocalDate.of(2024, 1, 31)`, `today = LocalDate.of(2024, 3, 1)`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `"D+30"`; write `DueDateUtilsTest.dueDateLabel_overdueAcrossLeapMonth_returnsTotalCalendarDays`.
  - A span of exactly one calendar month is counted in days, not months. `[Unit]`
    - Tip: Given `dueDate = LocalDate.of(2026, 11, 14)`, `today = LocalDate.of(2026, 10, 14)`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns exactly `"D-31"`; write `DueDateUtilsTest.dueDateLabel_exactlyOneMonthAhead_returnsThirtyOneDays`.
