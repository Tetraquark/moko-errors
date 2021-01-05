package ru.tetraquark.moko.errors.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue

class CallRequiredRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(CallRequiredDetector.ISSUE)

    override val api: Int = com.android.tools.lint.detector.api.CURRENT_API
}
