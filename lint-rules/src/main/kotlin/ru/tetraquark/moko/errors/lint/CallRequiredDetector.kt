package ru.tetraquark.moko.errors.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.visitor.AbstractUastVisitor
import java.util.LinkedList

class CallRequiredDetector : Detector(), SourceCodeScanner {

    companion object Issues {
        private val IMPLEMENTATION = Implementation(
            CallRequiredDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        @JvmField
        val ISSUE = Issue.create(
            id = "MissingRequiredCall",
            briefDescription = "Missing method call",
            explanation = "You must to call 'execute' method of ErrorHandlerContext object",
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION
        )

        private const val HANDLE_CONTEXT_ANNOTATION_NAME =
            "dev.icerock.moko.errors.annotation.HandleContextFactory"
        private const val CALL_REQUIRED_ANNOTATION_NAME =
            "dev.icerock.moko.errors.annotation.CallRequired"
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>>? =
        listOf(UMethod::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? =
        object : UElementHandler() {

            override fun visitMethod(node: UMethod) {
                node.accept(HandlerContextVisitor(context))
            }
        }

    class HandlerContextVisitor(
        private val context: JavaContext
    ) : AbstractUastVisitor() {
        private var handleNodesQueue = LinkedList<UCallExpression>()

        override fun visitCallExpression(node: UCallExpression): Boolean {
            node.resolve()?.annotations?.forEach {
                when (it.qualifiedName) {
                    CALL_REQUIRED_ANNOTATION_NAME -> {
                        handleNodesQueue.poll()
                        return true
                    }
                    HANDLE_CONTEXT_ANNOTATION_NAME -> {
                        handleNodesQueue.push(node)
                        return true
                    }
                }
            }

            return super.visitCallExpression(node)
        }

        override fun afterVisitMethod(node: UMethod) {
            handleNodesQueue.forEach {
                context.report(
                    issue = ISSUE,
                    location = context.getNameLocation(it),
                    message = "Should call `execute` method of `ExceptionHandlerContext` objects.",
                    quickfixData = null
                )
            }
        }
    }
}
