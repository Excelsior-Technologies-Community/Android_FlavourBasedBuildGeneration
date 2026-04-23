package com.ext.flavourbasedbuildtest

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : BaseMainActivity() {
    private val testLogs = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupStagingUI()
    }

    private fun setupStagingUI() {
        // Setup staging-specific UI elements
        val runTestsButton = findViewById<Button>(R.id.runTestsButton)
        val viewLogsButton = findViewById<Button>(R.id.viewLogsButton)
        val unitTestsText = findViewById<TextView>(R.id.unitTestsText)
        val integrationTestsText = findViewById<TextView>(R.id.integrationTestsText)
        val pendingTestsText = findViewById<TextView>(R.id.pendingTestsText)
        val performanceBar = findViewById<android.widget.ProgressBar>(R.id.performanceBar)

        // Button listeners
        runTestsButton.setOnClickListener {
            Log.d("StagingUI", "Running Tests")
            testLogs.clear()
            testLogs.add("=== Test Execution Started ===")
            testLogs.add("Timestamp: ${System.currentTimeMillis()}")
            Toast.makeText(this, "Running Test Suite...", Toast.LENGTH_SHORT).show()
            
            // Simulate test execution
            runTestsButton.isEnabled = false
            runTestsButton.text = "Running..."
            
            runTestsButton.postDelayed({
                testLogs.add("Unit Tests: 142 passed, 3 failed")
                testLogs.add("Integration Tests: 58 passed, 0 failed")
                testLogs.add("Performance Tests: 8 pending")
                testLogs.add("Total: 200 tests, 200 passed, 3 failed")
                testLogs.add("=== Test Execution Completed ===")
                
                unitTestsText.text = "145"
                integrationTestsText.text = "60"
                pendingTestsText.text = "8"
                performanceBar.progress = 92
                runTestsButton.isEnabled = true
                runTestsButton.text = "Run Tests"
                Toast.makeText(this, "Tests Completed: 205 Passed", Toast.LENGTH_SHORT).show()
                
                // Show test results dialog
                showTestResults()
            }, 2000)
        }

        viewLogsButton.setOnClickListener {
            Log.d("StagingUI", "Viewing Logs")
            showLogsDialog()
        }
    }

    private fun showTestResults() {
        val results = """
            Test Results Summary
            ====================
            Unit Tests: 145/145 Passed
            Integration Tests: 60/60 Passed
            Performance Tests: 8/8 Pending
            Total: 205/205 Passed
            
            Duration: 2.0s
            Status: SUCCESS
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Test Results")
            .setMessage(results)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showLogsDialog() {
        val logsText = if (testLogs.isEmpty()) {
            "No test logs available. Run tests first."
        } else {
            testLogs.joinToString("\n")
        }

        val scrollView = ScrollView(this)
        val textView = TextView(this).apply {
            text = logsText
            setPadding(40, 20, 40, 20)
            textSize = 12f
        }
        scrollView.addView(textView)

        AlertDialog.Builder(this)
            .setTitle("Test Logs")
            .setView(scrollView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("Clear Logs") { dialog, _ ->
                testLogs.clear()
                Toast.makeText(this, "Logs Cleared", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }
}
