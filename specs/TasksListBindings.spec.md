# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `fun setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Render the supplied task's D-day status in its list-row `TextView` using the current calendar date. Show the view only when a label exists, and clear and hide it when no task label exists so recycled rows cannot display stale status.

**Pre-conditions**
- `textView` is a valid list-row `TextView`.
- The function reads the system clock itself, so a test cannot pin "today". Every date-dependent expectation below is written relative to `LocalDate.now()` at call time, and tests must build due dates with `LocalDate.now().plusDays(n)` / `minusDays(n)` instead of a fixed calendar date.

**Post-conditions**
- Success cases
  - An active task with no due date clears any label left by a previously bound task: the text is exactly `""` and visibility is exactly `View.GONE`. `[Integration]`
    - Tip: Given a `TextView` left showing `"CLEAR"` with `View.VISIBLE` by a previously bound completed task (the row-recycling and check/uncheck path named in the PR intent), and `Task(title = "Buy milk", description = "Two cartons", isCompleted = false, id = "task-no-date", dueDate = null)` / When `setDueDateLabel` is called / Then the text is exactly `""` and visibility is exactly `View.GONE`; write `TasksListBindingsTest.setDueDateLabel_activeWithoutDueDate_clearsAndHidesView`.
  - An active task with a future due date shows exactly `"D-N"` and sets visibility to exactly `View.VISIBLE`. `[Integration]`
    - Tip: Given a `TextView` initially containing `"stale"` with `View.GONE`, and `Task(title = "Submit report", description = "Final copy", isCompleted = false, id = "task-future", dueDate = LocalDate.now().plusDays(5))` / When `setDueDateLabel` is called / Then the text is exactly `"D-5"` and visibility is exactly `View.VISIBLE`; write `TasksListBindingsTest.setDueDateLabel_activeFutureTask_showsDMinusLabel`.
  - An active task due today shows exactly `"D-Day"` and sets visibility to exactly `View.VISIBLE`. `[Integration]`
    - Tip: Given a `TextView` initially containing `"stale"` with `View.GONE`, and `Task(title = "Call client", description = "At noon", isCompleted = false, id = "task-today", dueDate = LocalDate.now())` / When `setDueDateLabel` is called / Then the text is exactly `"D-Day"` and visibility is exactly `View.VISIBLE`; write `TasksListBindingsTest.setDueDateLabel_activeTaskDueToday_showsDDay`.
  - An active overdue task shows exactly `"D+N"` and sets visibility to exactly `View.VISIBLE`. `[Integration]`
    - Tip: Given a `TextView` initially containing `"stale"` with `View.GONE`, and `Task(title = "Return book", description = "Library", isCompleted = false, id = "task-overdue", dueDate = LocalDate.now().minusDays(5))` / When `setDueDateLabel` is called / Then the text is exactly `"D+5"` and visibility is exactly `View.VISIBLE`; write `TasksListBindingsTest.setDueDateLabel_activeOverdueTask_showsDPlusLabel`.
  - A completed task with a due date shows exactly `"CLEAR"` and sets visibility to exactly `View.VISIBLE`. `[Integration]`
    - Tip: Given a `TextView` initially containing `"D+5"` with `View.GONE`, and `Task(title = "Return book", description = "Library", isCompleted = true, id = "task-complete-dated", dueDate = LocalDate.now().minusDays(5))` / When `setDueDateLabel` is called / Then the text is exactly `"CLEAR"` and visibility is exactly `View.VISIBLE`; write `TasksListBindingsTest.setDueDateLabel_completedTaskWithDueDate_showsClear`.
  - A completed task without a due date shows exactly `"CLEAR"` and sets visibility to exactly `View.VISIBLE`. `[Integration]`
    - Tip: Given a `TextView` initially containing `""` with `View.GONE`, and `Task(title = "Wash car", description = "Blue car", isCompleted = true, id = "task-complete-undated", dueDate = null)` / When `setDueDateLabel` is called / Then the text is exactly `"CLEAR"` and visibility is exactly `View.VISIBLE`; write `TasksListBindingsTest.setDueDateLabel_completedTaskWithoutDueDate_showsClear`.
  - A `null` task leaves no label: the text is exactly `""` and visibility is exactly `View.GONE`. `[Integration]`
    - Tip: Given a `TextView` initially containing `"CLEAR"` with `View.VISIBLE`, and `task = null` / When `setDueDateLabel` is called / Then the text is exactly `""` and visibility is exactly `View.GONE`; write `TasksListBindingsTest.setDueDateLabel_nullTask_clearsAndHidesView`.
- Error cases
  - None; both a concrete `Task` and `null` are defined inputs, while `textView` is non-null by signature. `[Integration]`
    - Tip: Given a valid `TextView` initially containing `"old"` with `View.VISIBLE`, and `task = null` / When `setDueDateLabel` is called / Then no exception is thrown, the text is exactly `""`, and visibility is exactly `View.GONE`; write `TasksListBindingsTest.setDueDateLabel_allAdmittedInputs_updatesWithoutError`.
- Edge cases
  - At the boundary between overdue, due today, and due in the future, the dates immediately before, at, and immediately after the current date show `"D+1"`, `"D-Day"`, and `"D-1"`, each with `View.VISIBLE`. `[Integration]`
    - Tip: Given a `TextView` initially containing `"stale"` with `View.GONE`, and active tasks with ids `"task-yesterday"`, `"task-today-edge"`, and `"task-tomorrow"` due on `LocalDate.now().minusDays(1)`, `LocalDate.now()`, and `LocalDate.now().plusDays(1)` respectively / When `setDueDateLabel` is called for each task / Then the text values are exactly `"D+1"`, `"D-Day"`, and `"D-1"`, and visibility is exactly `View.VISIBLE` after every call; write `TasksListBindingsTest.setDueDateLabel_aroundTodayBoundary_showsSignedOneDayLabels`.
